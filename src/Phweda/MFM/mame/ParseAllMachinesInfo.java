/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2017.  Author phweda : phweda1@yahoo.com
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

import Phweda.MFM.*;
import Phweda.MFM.Utils.ParseFolderINIs;
import Phweda.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static Phweda.MFM.mame.Machine.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/29/11
 * Time: 9:33 PM
 */
public class ParseAllMachinesInfo {
    //   private static Map<String, Machine> allMachinesInfo = new TreeMap<String, Machine>();
    private static Mame mame = new Mame();
    private static TreeSet<String> runnable = new TreeSet<String>();
    private static HashMap<String, ArrayList<String>> CategoryMachineListMap = new HashMap<String, ArrayList<String>>();
    private static HashMap<String, Map> FolderINIfiles = null;

    private static String build;
    private static ArrayList<String> categoriesList;
    private static Controllers controllers = Controllers.getInstance();

    private static ArrayList<String> machineList;
    private static int machineCount = 0;

    private static MFMSettings mfmSettings = MFMSettings.getInstance();
    /**
     * NOTE : Unless MFM arg -all flag is present we only load Playable games as determined by :
     * <driver status="good" or
     * <driver status="imperfect"
     * see MAME source info.c - info_xml_creator::output_driver()
     **/

    public static Mame loadAllMachinesInfo(boolean all) {
        System.out.println("Parsing with ALL flag: " + all);
        // Note as of 0.85 we handle all Mame -listxml versions the same
        mame = loadAllMachinesInfoJAXB();
        // Set data version here. 0.85 change to handle older Mame versions
        if (mame.getBuild() != null && !mame.getBuild().isEmpty()) {
            mfmSettings.setDataVersion(mame.getBuild());
        } else {
            mfmSettings.setDataVersion(MAMEexe.getMAMEexeVersion());
            mame.setBuild(mfmSettings.getDataVersion());
        }

        if (!all) {
            removeNotRunnable();
        }
        for (Machine machine : mame.getMachineMap().values()) {
            addNonMAMEinfo(machine, machine.getName());
            findControls(machine);
        }

        // TODO where to put this?
        findCategories();
        return mame;
    }

    private static void removeNotRunnable() {
        // NOTE This does leave us Devices and BIOS since they have no driver
        mame.getMachine()
                .removeIf(machine -> {
                    return machine.getDriver() != null &&
                            machine.getDriver().getStatus().equals(Machine.PRELIMINARY);
                });
    }

    /**
     * Original version. Replaced by JAXB marshalling
     * Retained for versions prior to MAME 173 which is the last change in MAME DTD
     *
     * @return Map of all Machines
     * @deprecated 0.85 release handles all Mame versions with -listxml (from 0.70)
     */
    private static void loadAllMachinesInfoDOM(Set<String> prefixes) {
        machineList = new ArrayList<String>();
        Document dom = null;
        for (String str : prefixes) {
            getMachineInfo(dom, str);
            dom = null;
            // TODO figure out which version it is
            if (MAMEInfo.getVersionDouble() < 0.139d) {
                break;
            }
        }

        System.out.print("\nTotal Machines : " + machineCount + "\n\n");
        MFM.logger.addToList("\nTotal Machines : " + machineCount + "\n\n", true);

    }

    private static Mame loadAllMachinesInfoJAXB() {
        return loadAllMAME();
    }

    private static Mame loadAllMAME() {
        Mame mame = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Mame.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Process process = MAMEexe.run("-listxml");
            InputStream inputStream = process.getInputStream();
            mame = (Mame) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException | MAMEexe.MAME_Exception e) {
            e.printStackTrace();
        }
        return mame;
    }

    public static TreeSet<String> getRunnable() {
        return runnable;
    }

    private static TreeSet<String> getGamesPrefixes() throws IOException {
        return new MAME_Game_Prefixes().getPrefixes();
    }

    private static void getMachineInfo(Document dom, String chars) {
        // TODO we should extract this to a method see other calls for -listxml MAME_Compatible
        ArrayList<String> fullListargs = new ArrayList<String>();
        fullListargs.add("-listxml");
        fullListargs.add(chars + "*");
        Process process = null;
        try {
            process = MAMEexe.run(fullListargs);
        } catch (MAMEexe.MAME_Exception e) {
            e.printStackTrace();
        }
        try {
            dom = XMLUtils.parseXmlFile(process.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Element root = dom.getDocumentElement();

        NodeList machineNodes = root.getElementsByTagName(Machine.MACHINE);
        // Legacy xml dtd value is "game"
        if (machineNodes.getLength() < 2) {
            machineNodes = root.getElementsByTagName(GAME);
        }

        /* */
        // Get a total count of Machine nodes in XML - 2/2/2016 trying to rectify count with coccola
        // NOTE assumption machines are only returned once is incorrect - think it is device_refs 10/21/2016
        // machineCount += machineNodes.getLength();

        for (int i = 0; i < machineNodes.getLength(); i++) {
            Element machineNode = (Element) machineNodes.item(i);
            // Ensure there are children
            if (machineNode.hasChildNodes()) {
                String bios = machineNode.getAttribute(Machine.ISBIOS);
                /* if playable */
                Element driver = (Element) machineNode.getElementsByTagName(DRIVER).item(0);
                if (driver != null) {
                    String status = driver.getAttribute(STATUS);
                    if (!machineList.contains(machineNode.getAttribute(Machine.NAME))) {
                        machineList.add(machineNode.getAttribute(Machine.NAME));
                        machineCount++;
                        if (MFM.isProcessAll()) {
                            loadMachineInfo(machineNode, status, bios);
                        }  //   NOTE 10/13/2015 to include all see loadMachineInfo
                        else if (status.equalsIgnoreCase(GOOD) || status.equalsIgnoreCase(IMPERFECT)) {
                            // the real work
                            // Already have Status so might as well pass it in
                            loadMachineInfo(machineNode, status, bios);
                        }
                    }

                }
            }
        }
    }

    /*
     * Huge rewrite to JAXB objects Oct 2016
     *
     */
    private static void loadMachineInfo(Element machineElement, String status, String isBios) {
        Machine machine = new Machine();

        // local to reduce calls within method
        String machineName = machineElement.getAttribute(Machine.NAME);
        machine.setName(machineName);

        Element driverEL = (Element) machineElement.getElementsByTagName(Machine.DRIVER).item(0);
        Driver driver = new Driver();
        driver.setStatus(status);
        if (driverEL != null) {
            if (!driverEL.getAttribute(Machine.COCKTAIL).isEmpty()) {
                driver.setCocktail(driverEL.getAttribute(Machine.COCKTAIL));
            }
        }
        machine.setDriver(driver);
        machine.setIsbios(isBios);

        // Leave in to show progress in command window
        if (MFM.isDebug()) {
            System.out.println(machineName);
            MFM.logger.addToList(machineName);
        }

        /* We know there is one and only one description*/
        machine.setDescription(machineElement.getElementsByTagName(Machine.DESCRIPTION).item(0).getTextContent());

        /* We may not get these */
        Element yearEL = (Element) machineElement.getElementsByTagName(Machine.YEAR).item(0);
        if (yearEL != null) {
            machine.setYear(yearEL.getTextContent());
        }

        Element manufacturerEL = (Element) machineElement.getElementsByTagName(Machine.MANUFACTURER).item(0);
        if (manufacturerEL != null) {
            machine.setManufacturer(manufacturerEL.getTextContent());
        }

        NodeList disks = machineElement.getElementsByTagName(Machine.DISK);
        if (disks != null && disks.item(0) != null) {
            for (int k = 0; k < disks.getLength(); k++) {
                Disk disk = new Disk();
                disk.setName(((Element) disks.item(k)).getAttribute(Machine.NAME));
                machine.getDisk().add(disk);
                // System.out.println(machine.getName() + " disk : " + ((Element)disks.item(k)).getAttribute(Machine.NAME));
            }
        }

        NodeList softwarelists = machineElement.getElementsByTagName(Machine.SOFTWARELIST);
        if (softwarelists != null && softwarelists.item(0) != null) {
            for (int k = 0; k < softwarelists.getLength(); k++) {
                Softwarelist sList = new Softwarelist();
                sList.setName(((Element) softwarelists.item(k)).getAttribute(Machine.NAME));
                machine.getSoftwarelist().add(sList);
                if (MFM.isSystemDebug()) {
                    System.out.println(machine.getName() + " softwarelist : " + sList.getName());
                }
            }
        }

        // Early MAME versions it was video
        Element displayEL = (Element) machineElement.getElementsByTagName(Machine.DISPLAY).item(0);
        if (displayEL != null) {
            Display display = new Display();
            display.setType(displayEL.getAttribute(Machine.TYPE));
            display.setRotate(displayEL.getAttribute(Machine.ROTATE));
            display.setHeight(displayEL.getAttribute(Machine.HEIGHT));
            display.setWidth(displayEL.getAttribute(Machine.WIDTH));
            machine.getDisplay().add(display);
        } else {
            Element videoEL = (Element) machineElement.getElementsByTagName(Machine.VIDEO).item(0);
            Display display = new Display();
            if (videoEL != null) {
                // vertical raster
                machine.setScreentype(videoEL.getAttribute(Machine.SCREEN));
                machine.setIsVertical(videoEL.getAttribute(Machine.ORIENTATION));
                display.setHeight(videoEL.getAttribute(Machine.HEIGHT));
                display.setWidth(videoEL.getAttribute(Machine.WIDTH));
            }
            machine.getDisplay().add(display);
        }

        String cloneof = machineElement.getAttribute(Machine.CLONEOF);
        if (cloneof.length() > 0) {
            machine.setCloneof(cloneof);
        }

        String romof = machineElement.getAttribute(Machine.ROMOF);
        if (romof.length() > 0) {
            machine.setRomof(romof);
        }

        String sourceFile = machineElement.getAttribute(Machine.SOURCEFILE);
        machine.setSourcefile(sourceFile);

        NodeList roms = machineElement.getElementsByTagName(Machine.ROM);
        // TODO - isn't this extraneous check?
        if (roms.getLength() > 0) {
            for (int i = 0; i < roms.getLength(); i++) {
                Element romEL = ((Element) roms.item(i));
                Rom rom = new Rom();
                rom.setName(romEL.getAttribute(Machine.NAME));
                rom.setSize(romEL.getAttribute(Machine.SIZE));
                rom.setSha1(romEL.getAttribute(Machine.SHA1));
                rom.setCrc(romEL.getAttribute(Machine.CRC));
                machine.getRom().add(rom);
            }
        }

        // Need to get device_refs may be 0 to N of them
        NodeList deviceRefs = machineElement.getElementsByTagName(Machine.DEVICE_REF);
        if (deviceRefs.getLength() > 0) {
            for (int i = 0; i < deviceRefs.getLength(); i++) {
                DeviceRef deviceRef = new DeviceRef();
                deviceRef.setName(((Element) deviceRefs.item(i)).getAttribute(Machine.NAME));
                machine.getDeviceRef().add(deviceRef);
            }
        }

        Element inputEL = (Element) machineElement.getElementsByTagName(Machine.INPUT).item(0);
        if (inputEL != null) {
            Input input = new Input();
//      NOTE XML changed ~173
            boolean boolButtons = false;
            String buttons = inputEL.getAttribute(Machine.BUTTONS);
            if (!buttons.isEmpty()) {
                input.setButtons(buttons);
                boolButtons = true;
            }

            String players = inputEL.getAttribute(Machine.PLAYERS);
            if (!players.isEmpty()) {
                input.setPlayers(players);
            }

            NodeList nodeList = inputEL.getElementsByTagName(Machine.CONTROL);
            String type;
            int buttonsInt = 0;
            ArrayList<String> controlArgs = new ArrayList<String>(5);
            for (int i = 0; i < nodeList.getLength(); i++) {
                int tempInt = 0;
                Element currentNode = (Element) nodeList.item(i);
                // Extraneous but to be safe
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    type = currentNode.getAttribute(Controllers.TYPE);
                    String tempButtonStr;
                    if (currentNode.getAttribute(Controllers.WAYS).isEmpty()) {
                        controllers.addMachine(type, machine.getName());
                        tempButtonStr = currentNode.getAttribute(Controllers.BUTTONS);
                        if (!tempButtonStr.isEmpty()) {
                            tempInt = Integer.parseInt(tempButtonStr);
                        }
                    } else {
                        controlArgs.add(type);
                        tempButtonStr = currentNode.getAttribute(Controllers.BUTTONS);
                        if (!tempButtonStr.isEmpty()) {
                            tempInt = Integer.parseInt(tempButtonStr);
                        }
                        controlArgs.add(currentNode.getAttribute(Controllers.WAYS));
                        controlArgs.add(currentNode.getAttribute(Controllers.WAYS2));
                        controlArgs.add(currentNode.getAttribute(Controllers.WAYS3));
                        controllers.addMachine(controlArgs, machine.getName());
                        controlArgs.clear();
                    }
                    Control control = new Control();
                    control.setType(type);
                    input.getControl().add(control);
                }
                buttonsInt = Math.max(tempInt, buttonsInt);
            }
            if (!boolButtons) {
                machine.setButtons(buttonsInt);
            }
            machine.setInput(input);
        }

//************** NON MAME INFORMATION SOURCES *************************************************************
        addNonMAMEinfo(machine, machineName);
//**********************************************************************************************

        mame.getMachine().add(machine);
    }

    private static void addNonMAMEinfo(Machine machine, String machineName) {
        machine.setHistory(LoadNonMAMEresources.getHistoryDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getMAMEInfoDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getMESSInfoDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getSYSInfoDAT().get(machineName));
        machine.setMAMEVersionAdded(LoadNonMAMEresources.getVersion().get(machineName));

        Map<String, String> MachinetoCategoryMap = LoadNonMAMEresources.getMachinetoCategoryMap();
        if (MachinetoCategoryMap.containsKey(machineName)) {
            machine.setCategory(MachinetoCategoryMap.get(machineName));
            if (!CategoryMachineListMap.containsKey(MachinetoCategoryMap.get(machineName))) {
                CategoryMachineListMap.put(MachinetoCategoryMap.get(machineName), new ArrayList<String>());
            }
            CategoryMachineListMap.get(MachinetoCategoryMap.get(machineName)).add(machineName);
        }

        if (LoadNonMAMEresources.getNplayers().containsKey(machineName)) {
            machine.setNplayerEntry(LoadNonMAMEresources.getNplayers().get(machineName));
        }

        System.out.println(machineName);
        // Is it runnable?  skip if it is a Device
        // With 0.85 MFM release we now go back to very old Mame versions where Driver was not present(BIOSes) -
        // just assume those are runnable
        if ((!machine.getIsdevice().equals(Machine.YES) && (machine.getDriver() == null ||
                machine.getDriver().getStatus().equals(GOOD) || machine.getDriver().getStatus().equals(IMPERFECT))
                // Not bios or no bios value ""
                && (machine.getIsbios().equals(NO) || machine.getIsbios().isEmpty()))) {
            runnable.add(machineName);
        }
    }

    private static void findCategories() {
        categoriesList = new ArrayList<String>();
        // Machines are the key with Category the value in MachinetoCategoryMap
        Map<String, String> MachinetoCategoryMap = LoadNonMAMEresources.getMachinetoCategoryMap();
        for (String game : MachinetoCategoryMap.keySet()) {
            if (!categoriesList.contains(MachinetoCategoryMap.get(game))) {
                String key = MachinetoCategoryMap.get(game);
                categoriesList.add(key);
                CategoryMachineListMap.put(key, new ArrayList<String>());
            }
        }
        // NOTE leave in to show running in shell window
        System.out.println(categoriesList);
        if (MFM.isDebug()) {
            MFM.logger.addToList(categoriesList);
        }
    }

    private static void findControls(Machine machine) {
        // Has input and control(s)
        if (machine.getInput() != null &&
                machine.getInput().getControl() != null && !machine.getInput().getControl().isEmpty()) {
            for (Control control : machine.getInput().getControl()) {
                String type = control.getType();
                if (control.getWays() == null || control.getWays().isEmpty()) {
                    controllers.addMachine(type, machine.getName());
                } else {
                    ArrayList<String> controlArgs = new ArrayList<String>(5);
                    controlArgs.add(type);
                    controlArgs.add(control.getWays());
                    if (control.getWays2() != null) {
                        controlArgs.add(control.getWays2());
                    }
                    if (control.getWays3() != null) {
                        controlArgs.add(control.getWays3());
                    }
                    controllers.addMachine(controlArgs, machine.getName());
                    controlArgs.clear();
                }
            }
        }
    }

    /**
     * Load any INI files in <MAME root>/folders
     * 7/31/2015 we now are going to include with MFM
     * Catver.ini
     * category_full.ini
     */
    private static void loadFoldersINIs(HashSet<File> files) {
        try {
            FolderINIfiles = new HashMap<String, Map>();

            for (File file : files) {
                if (MFM.isDebug()) {
                    MFM.logger.addToList(file.getName());
                }
                if (!file.getName().contains(MFM_Constants.CATVER_INI_FILENAME) &&
                        !file.getName().contains(MFM_Constants.CATVER_FULL_INI_FILENAME) &&
                        !file.getName().contains(MFM_Constants.NPLAYERS_INI_FILENAME)) {
                        /* LinkedHashMap returns the order elements are added
                         * TreeMap gives us the Natural order
                         * At least two common INI files are not ordered - Catlist.ini & Genre.ini
                         */
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    new ParseFolderINIs(file.getAbsolutePath(), map).processFile();
                    String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    // Convert to TreeMap for order see above
                    TreeMap<String, String> orderedMap = new TreeMap<String, String>(map);
                    FolderINIfiles.put(name, orderedMap);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // TODO check can we ever get here without it being initialized??
    public static HashMap<String, Map> INIfiles(HashSet<File> files) {
        if (FolderINIfiles == null) {
            loadFoldersINIs(files);
        }
        return FolderINIfiles;
    }

    public static ArrayList<String> getCategoriesList() {
        return categoriesList;
    }

    public static HashMap<String, ArrayList<String>> getCategoryGamesList() {
        return CategoryMachineListMap;
    }

    public static Controllers getControllers() {
        return controllers;
    }

}
