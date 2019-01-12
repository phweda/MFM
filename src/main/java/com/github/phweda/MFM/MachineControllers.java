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

package com.github.phweda.MFM;

import com.github.phweda.MFM.mame.Control;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/16/2015
 * Time: 7:34 PM
 */
public class MachineControllers implements Serializable {

    public static final String TYPE = "type";
    public static final String WAYS = "ways";
    public static final String WAYS2 = "ways2";
    public static final String WAYS3 = "ways3";
    public static final String BUTTONS = "buttons";
    public static final String DIAL = "dial";
    public static final String DOUBLEJOY = "doublejoy";
    public static final String GAMBLING = "gambling";
    public static final String HANAFUDA = "hanafuda";
    public static final String JOY = "joy";
    public static final String KEYBOARD = "keyboard";
    public static final String KEYPAD = "keypad";
    public static final String LIGHTGUN = "lightgun";
    public static final String MAHJONG = "mahjong";
    public static final String MOUSE = "mouse";
    public static final String ONLY_BUTTONS = "only_buttons"; // This is the new value in MAME 173
    public static final String PADDLE = "paddle";
    public static final String PEDAL = "pedal";
    public static final String POSITIONAL = "positional";
    public static final String STICK = "stick";
    public static final String TRACKBALL = "trackball";
    public static final String TRIPLEJOY = "triplejoy";
    private static MachineControllers ourInstance;
    private static TreeMap<Integer, TreeSet<String>> controlMachinesList = new TreeMap<>();
    private static TreeMap<Integer, com.github.phweda.MFM.mame.Control> controls = new TreeMap<>();

    private static TreeMap<String, String> controllerMAMEtoLabel = new TreeMap<>();
    private static TreeMap<String, String> controllerLabeltoMAME = new TreeMap<>();
    // Hard coded need to find a dynamic way to handle new joystick types
    private static String[] joysticks = new String[]{"All", "1", "2", "vertical2", "4", "8", "3 (half4)", "5 (half8)"};
    private static String[] doubleJoysticks = new String[]{"All", "2", "vertical2", "4/2", "4", "8", "8/2"};

    private MachineControllers() {
        populateLabels(MFM_Data.getInstance().getControllerLabelsFile());
    }

    /**
     * Reads the MFM_Controller.ini file to create the Controls to common label name mapping
     *
     * @param file MFM_Controller.ini
     */
    private static void populateLabels(File file) {

        if (!file.exists()) {
            return;
        }
        try (Stream<String> lines = Files.lines(file.toPath())) {
            String delimiter = "=";
            lines.filter(line -> line.contains(delimiter))
                    .forEach(line -> {
                        String key = line.split(delimiter)[0].trim();
                        String value = line.split(delimiter)[1].trim();
                        controllerMAMEtoLabel.putIfAbsent(key, value);
                        controllerLabeltoMAME.putIfAbsent(value, key);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MachineControllers getInstance() {
        if (ourInstance == null) {
            ourInstance = new MachineControllers();
        }
        return ourInstance;
    }

    /**
     * MachineControllers name mapping MAME XML value are keys
     *
     * @return Map of common names or null if file not found
     */
    public static SortedMap<String, String> getControllerMAMEtoLabel() {
        return controllerMAMEtoLabel;
    }

    /**
     * MachineControllers name set
     *
     * @return Set of common names or null if file not found
     */
    public static SortedSet<String> getMAMEControllerNames(SortedSet<String> commonLabels) {
        TreeSet<String> labels = new TreeSet<>();
        for (String label : commonLabels) {
            labels.add(controllerLabeltoMAME.get(label));
        }
        return labels;
    }

    public static String getMAMEControllerLabelName(String mameName) {
        return controllerMAMEtoLabel.get(mameName);
    }

    /**
     * MachineControllers reverse name mapping Common names are keys
     *
     * @return Map of common names or null if file not found
     */
    public static SortedMap<String, String> getControllerLabeltoMAME() {
        return controllerLabeltoMAME;
    }

    TreeMap<Integer, TreeSet<String>> getControlMachinesList() {
        return controlMachinesList;
    }

    static void setControlMachinesList(TreeMap<Integer, TreeSet<String>> controlMachinesList) {
        MachineControllers.controlMachinesList = controlMachinesList;
    }

    public static String[] getJoysticks() {
        return joysticks;
    }

    public static String[] getDoubleJoysticks() {
        return doubleJoysticks;
    }

    public static boolean controlHasMachine(String control, String machine) {
        return controlMachinesList.get(ourInstance.getSignature(control)).contains(machine);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean checkSignature(int signature) {
        return controlMachinesList.keySet().contains(signature);
    }

/*  For Development only
    public static void dumpAllWaysControls() {
        File controlGamesListFile = new File(MFM.MFM_LISTS_DIR + "controlsGames.csv");
        File controlsFile = new File(MFM.MFM_LISTS_DIR + "controls.csv");
        PrintWriter pwcontrolGamesList = null;
        PrintWriter pwControls = null;

        try {
            pwcontrolGamesList = new PrintWriter(controlGamesListFile);
            pwControls = new PrintWriter(controlsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pwControls.println("Signature,Type,WAYS,WAYS2,WAYS3,COUNT");
        for (int signature : controls.keySet()) {
            com.github.phweda.MFM.mame.Control control = controls.get(signature);
            pwControls.println(Integer.toString(signature) + ',' + control.getType() + ',' +
                    control.getWays() + ',' + control.getWays2() + ',' + control.getWays3() + ','
                    + controlMachinesList.get(signature).size());
        }
        pwControls.close();

        pwcontrolGamesList.println("Signature,FileName,NAME,Manufacturer,Type,WAYS,WAYS2,WAYS3");
        for (int signature : controlMachinesList.keySet()) {
            com.github.phweda.MFM.mame.Control control = controls.get(signature);
            if (signature == -1771213723 || signature == -1134657068 || signature == -995842198
                    || signature == -865288 || signature == 3083120 || signature == 104086693
                    || signature == 106542458 || signature == 109764752 || signature == 503739367
                    || signature == 730225098 || signature == 829995282 || signature == 991968362
                    || signature == 1381039892
                    ) {
                continue;
            }
            Iterator<String> iter = controlMachinesList.get(signature).iterator();
            String signatureString = Integer.toString(signature);
            Map<String, Machine> machines = MAMEInfo.getMame().getMachineMap();
            while (iter.hasNext()) {
                String machine = iter.next();
                pwcontrolGamesList.println(signatureString + ',' + machine + ",\"" +
                        machines.get(machine).getDescription() + "\",\"" + machines.get(machine).getManufacturer() +
                        "\"," + control.getType() + ',' + control.getWays() + ',' + control.getWays2() + ',' +
                        control.getWays3());
            }
        }
        pwcontrolGamesList.close();
    }
*/

    public final void addMachine(List<String> args, String game) {
        add(getSignature(args), game);
    }

    public final void addMachine(String type, String game) {
        add(getSignature(type), game);
    }

    final int getSignature(List<String> args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (!arg.equals("null")) {
                sb.append(arg);
            }
        }
        int hash = sb.toString().hashCode();


        if (!controls.containsKey(hash)) {
            Control control = new com.github.phweda.MFM.mame.Control();
            control.setType(args.get(0));
            if (args.size() > 1) {
                control.setWays(args.get(1));
            }

            if (args.size() > 2) {
                control.setWays2(args.get(2));
            }

            if (args.size() > 3) {
                control.setWays3(args.get(3));
            }
            controls.put(hash, control);
        }
        return hash;
    }

    public final void add(int signature, String game) {

        if (controlMachinesList.containsKey(signature)) {
            controlMachinesList.get(signature).add(game);
        } else {
            TreeSet<String> treeSet = new TreeSet<>();
            treeSet.add(game);
            controlMachinesList.put(signature, treeSet);
        }
    }

    // type ways ways2 ways3
    private int getSignature(String type) {
        int hash = type.hashCode();
        if (!controls.containsKey(hash)) {
            // System.out.println('`' + hash + "\tfor " + type);
            if (MFM.isDebug()) {
                MFM.getLogger().addToList('`' + hash + "\tfor " + type);
            }
            Control control = new com.github.phweda.MFM.mame.Control();
            control.setType(type);
            controls.put(hash, control);
        }
        return hash;
    }

    final boolean signatureHasMachine(int signature, String machineName) {
        if (!checkSignature(signature)) {
            // except "doublejoy" -806141341
            if (signature != -806141341) {
                MFM.getLogger().addToList("Machine signature not found for: " + machineName + " - " + signature, true);
            }
            return false;
        }
        return controlMachinesList.get(signature).contains(machineName);
    }

    final boolean signaturesHasMachine(TreeSet<Integer> signatures, String machineName) {
        for (int signature : signatures) {
            if (!checkSignature(signature)) {
                // except "doublejoy" -806141341
                if (signature != -806141341) {
                    MFM.getLogger().addToList("Machine signature not found for: " + machineName + " - " + signature, true);
                }
                continue;
            }
            if (controlMachinesList.get(signature).contains(machineName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
