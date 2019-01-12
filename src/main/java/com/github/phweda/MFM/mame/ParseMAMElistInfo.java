/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2011 - 2018.  Author phweda : phweda1@yahoo.com
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Phweda.MFM.mame;

import Phweda.MFM.MFMSettings;
import Phweda.MFM.MachineControllers;
import Phweda.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

import static Phweda.MFM.mame.ParseMAMEexternalData.*;

public final class ParseMAMElistInfo {
    public static final String GAME = "game";
    public static final String RESOURCE = "resource";
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String QUOTED_REGEX = "[^\"\\s]+|\"(\\\\.|[^\\\\\"])*\""; // Internet grabbed needs testing
    private static final String QUOTED_REGEX2 = "\"([^\"]*)\"|(\\S+)"; // match string within quotes
    private static final Pattern quotedPattern = Pattern.compile(QUOTED_REGEX2);

    private static final MFMSettings mfmSettings = MFMSettings.getInstance();
    private static final Set<String> runnable = new TreeSet<>();

    public static Mame loadAllMAME() {
        File file = getInputFile();
        String version = getInputVersion();
        String listInfo = getListInfo(file);
        if (listInfo.isEmpty()) {
            return null;
        }
        return parseToMAME(listInfo, version);
    }

    public static Set<String> getRunnable() {
        return runnable;
    }

    public static List<String> getCategoriesList() {
        return ParseMAMEexternalData.getCategoriesList();
    }

    public static Map<String, ArrayList<String>> getCategoryMachineListMap() {
        return ParseMAMEexternalData.getCategoryMachineListMap();
    }

    public static MachineControllers getMachineControllers() {
        return ParseMAMEexternalData.getMachineControllers();
    }

    private static File getInputFile() {
        JFileChooser chooser = new JFileChooser("");
        chooser.showDialog(null, "Select");
        return chooser.getSelectedFile();
    }

    private static String getInputVersion() {
        return JOptionPane.showInputDialog("Enter Version string");
    }

    private static Mame parseToMAME(String listInfo, String version) {
        Mame mame = new Mame();
        try (Scanner scanner = new Scanner(listInfo)) {
            scanner.useDelimiter(FileUtils.NEWLINE);
            createMame(mame, scanner);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        findCategories();
        mame.setBuild(version);
        mfmSettings.setDataVersion(version);
        return mame;
    }

    private static void createMame(Mame mame, Scanner scanner) {
        String line = scanner.nextLine();
        while (scanner.hasNext()) {
            if (line.startsWith(GAME) || line.startsWith(RESOURCE)) {
                mame.getMachine().add(createMachine(scanner));
            }
            line = scanner.nextLine();
        }
    }

    private static Machine createMachine(Scanner scanner) {
        Machine machine = new Machine();

        do {
            String line = scanner.nextLine().trim();
            if (line.startsWith(")")) { // End of game condition
                break;
            } else if (line.startsWith(Machine.NAME)) {
                machine.setName(line.split(WHITESPACE_REGEX)[1]);
            } else if (line.startsWith(Machine.DESCRIPTION)) {
                machine.setDescription(getQuotedString(line));
            } else if (line.startsWith(Machine.YEAR)) {
                machine.setYear(line.split(WHITESPACE_REGEX)[1]);
            } else if (line.startsWith(Machine.MANUFACTURER)) {
                machine.setManufacturer(getQuotedString(line));
            } else if (line.startsWith(Machine.CLONEOF)) {
                machine.setCloneof(line.split(WHITESPACE_REGEX)[1]);
            } else if (line.startsWith(Machine.ROMOF)) {
                machine.setRomof(line.split(WHITESPACE_REGEX)[1]);
            } else if (line.startsWith(Machine.ROM)) {
                machine.getRom().add(createRom(reduceLine(line)));
            } else if (line.startsWith("chip")) {
                machine.getChip().add(createChip(reduceLine(line)));
            } else if (line.startsWith(Machine.VIDEO)) {
                addDisplay(reduceLine(line), machine);
            } else if (line.startsWith(Machine.SOUND)) {
                createSound(reduceLine(line), machine);
            } else if (line.startsWith(Machine.INPUT)) {
                machine.setInput(createInput(reduceLine(line)));
            } else if (line.startsWith(Machine.DIPSWITCH)) {
                machine.getDipswitch().add(createDipswitch(reduceLine(line)));
            } else if (line.startsWith(Machine.DRIVER)) {
                machine.setDriver(createDriver(reduceLine(line)));
            }
        } while (scanner.hasNext());


        if (isRunnable(machine)) {
            runnable.add(machine.getName());
        }

        // In earlier (pre 0.70) MAME cocktail and BIOS designations were embedded in the description field
        if (machine.getDescription().contains(Machine.COCKTAIL)) {
            machine.getDriver().setCocktail("good");
        }

        if (machine.getDescription().toLowerCase().contains("bios")) {
            machine.setIsbios("yes");
        }

        findControls(machine);
        addNonMAMEinfo(machine);
        return machine;
    }

    //video ( screen raster orientation vertical x 224 y 288 colors 16 freq 60 )
    private static void addDisplay(String line, Machine machine) {

        Display display = new Display();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {

                    case Machine.SCREEN:
                        display.setScreen(scanner.next());
                        break;

                    case Machine.ORIENTATION:
                        display.setOrientation(scanner.next());
                        break;

                    case "x":
                        display.setWidth(scanner.next());
                        break;

                    case "y":
                        display.setHeight(scanner.next());
                        break;

                    case "colors":
                        display.setColors(scanner.next());
                        break;

                    case "freq":
                        display.setRefresh(scanner.next());
                        break;

                    default:
                        break;
                }
            }
        }
        machine.getDisplay().add(display);
    }

    // sound ( channels 1 )
    private static void createSound(String line, Machine machine) {
        try (Scanner scanner = new Scanner(line)) {
            if (scanner.next().equals("channels")) {
                Sound sound = new Sound();
                sound.setChannels(scanner.next());
                machine.setSound(sound);
            }
        }
    }

    // input ( players 2 control joy4way coins 3 )
    private static Input createInput(String line) {
        Input input = new Input();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case Machine.PLAYERS:
                        input.setPlayers(scanner.next());
                        break;

                    case Machine.CONTROL:
                        Control control = new Control();
                        control.setType(scanner.next());
                        input.getControl().add(control);
                        break;

                    case "coins":
                        input.setCoins(scanner.next());
                        break;

                    default:
                        break;
                }
            }
        }
        return input;
    }

    /**
     * dipswitch ( name "Service Mode" )
     * dipswitch ( name "Cabinet" entry "Upright" entry "Cocktail" default "Upright" )
     *
     * @param line dipswitch entry
     * @return
     */
    private static Dipswitch createDipswitch(String line) {
        Dipswitch dipswitch = new Dipswitch();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.findInLine(QUOTED_REGEX)) {
                    case Machine.NAME:
                        dipswitch.setName(getQuotedString(scanner.findInLine(QUOTED_REGEX)));
                        break;

                    case "entry":
                        dipswitch.getEntry().add(getQuotedString(scanner.findInLine(QUOTED_REGEX)));
                        break;

                    default:
                        break;
                }
            }
        }
        return dipswitch;
    }

    /**
     * driver ( status good color good sound good colordeep 8 )
     *
     * @param line
     * @return
     */
    private static Driver createDriver(String line) {
        Driver driver = new Driver();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case Machine.STATUS:
                        driver.setStatus(scanner.next());
                        break;

                    case "color":
                        driver.setColor(scanner.next());
                        break;

                    case "colordeep":
                        driver.setColordeep(scanner.next());
                        break;

                    case "hiscore":
                        driver.setHiscore(scanner.next());
                        break;

                    case Machine.SOUND:
                        driver.setSound(scanner.next());
                        break;

                    case "credits":
                        while (scanner.hasNext()) {
                            scanner.next(); // scan to the end
                        }
                        break;

                    default:
                        if (scanner.hasNext()) {
                            scanner.next();
                        }
                        break;
                }
            }
        }
        return driver;
    }

    /**
     * chip ( type cpu name Z80 clock 3072000 )
     * chip ( type audio name Namco )
     *
     * @param line Chip entry
     * @return Chip
     */
    private static Chip createChip(String line) {
        Chip chip = new Chip();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case Machine.TYPE:
                        chip.setType(scanner.next());
                        break;

                    case Machine.NAME:
                        chip.setName(scanner.next());
                        break;

                    case "clock":
                        chip.setClock(scanner.next());
                        break;

                    default:
                        break;
                }
            }
        }
        return chip;
    }

    /**
     * rom ( name namcopac.6e size 4096 crc fee263b3 )
     *
     * @param line rom entry
     * @return Rom for this line
     */
    private static Rom createRom(String line) {
        Rom rom = new Rom();
        try (Scanner scanner = new Scanner(line)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case Machine.NAME:
                        rom.setName(scanner.next());
                        break;

                    case Machine.SIZE:
                        rom.setSize(scanner.next());
                        break;

                    case "crc":
                        rom.setCrc(scanner.next());
                        break;

                    case "region":
                        rom.setRegion(scanner.next());
                        break;

                    case "offs":
                        rom.setOffset(scanner.next());
                        break;

                    case "flags":
                        rom.setFlags(scanner.next());
                        break;

                    default:
                        break;
                }
            }
        }
        return rom;
    }

    /**
     * Slurp in listinfo file
     *
     * @param listInfoFile listInfo output from very old MAME versions
     * @return file text as a String
     */
    private static String getListInfo(File listInfoFile) {
        try {
            return new String(Files.readAllBytes(listInfoFile.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getQuotedString(String quotedString) {
        return quotedString.contains("\"") ?
                quotedString.substring(quotedString.indexOf('\"') + 1, quotedString.lastIndexOf('\"')) :
                quotedString;
/*
        try (Scanner scanner = new Scanner(line)) {
            return scanner.findInLine(QUOTED_REGEX2);
        }
*/
/*
        Matcher matcher = quotedPattern.matcher(line);
        return (matcher.group(1) == null) ? "" : matcher.group(1);
*/
    }


    /**
     * Reduce input to string within the parentheses
     *
     * @param line input string
     * @return string
     */
    private static String reduceLine(String line) {
        return line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
    }
}
