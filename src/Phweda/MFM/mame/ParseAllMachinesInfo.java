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

import Phweda.MFM.*;
import Phweda.MFM.Utils.ParseFolderINIs;
import Phweda.MFM.mame.softwarelist.Softwarelists;
import Phweda.utils.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
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
    private static Softwarelists softwarelists;
    private static TreeSet<String> runnable = new TreeSet<>();
    private static HashMap<String, ArrayList<String>> CategoryMachineListMap = new HashMap<>();
    private static HashMap<String, Map<String, String>> FolderINIfiles = null;

    private static ArrayList<String> categoriesList;
    private static MachineControllers machineControllers = MachineControllers.getInstance();
    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    /**
     * NOTE : If not parsing All we only load Playable games as determined by :
     * <driver status="good" or
     * <driver status="imperfect"
     * see MAME source info.cpp - info_xml_creator::output_driver
     * https://github.com/mamedev/mame/blob/master/src/frontend/mame/info.cpp
     **/

    public static Mame loadAllMachinesInfo(boolean all) {
        String message = "Parsing with ALL flag: " + all;
        System.out.println(message);
        MFM.getLogger().addToList(message + "\nMAME exe is: " + MFMSettings.getInstance().fullMAMEexePath());

        try {
            // Note as of 0.85 we handle all Mame -listxml versions the same
            mame = loadAllMachinesInfoJAXB();
            softwarelists = generateSoftwareLists();
        } catch (Exception e) {
            e.printStackTrace();
            if (mame == null) {
                return null;
            }
        }

        // Set data version here. 0.85 change to handle older Mame versions
        if (mame.getBuild() != null && !mame.getBuild().isEmpty()) {
            mfmSettings.generateDataVersion(mame.getBuild());
        } else {
            mfmSettings.generateDataVersion(MAMEexe.getMAMEexeVersion());
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
                .removeIf(machine -> machine.getDriver() != null &&
                        machine.getDriver().getStatus().equals(Machine.PRELIMINARY));
    }

    private static Mame loadAllMachinesInfoJAXB() {
        return loadAllMAME();
    }

    private static Mame loadAllMAME() {
        Mame mame = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Mame.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            if (MFM.isSystemDebug()) {
                jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            }

            Process process = MAMEexe.run("-listxml");
            InputStream inputStream = process.getInputStream();
            mame = (Mame) jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return mame;
    }

    private static Softwarelists generateSoftwareLists() {
        Softwarelists softwareLists = new Softwarelists();
        ParseSoftwareLists.generateSoftwareLists(softwareLists, MFMSettings.getInstance().MAMEexeDir() +
                FileUtils.DIRECTORY_SEPARATOR + "hash");
        MFM_Data.getInstance().setSoftwarelists(softwareLists);

        return softwareLists;
    }

    public static Softwarelists getSoftwarelists() {
        return softwarelists;
    }

    public static TreeSet<String> getRunnable() {
        return runnable;
    }

    /*************** NON MAME INFORMATION SOURCES *************************************************************/

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
                CategoryMachineListMap.put(MachinetoCategoryMap.get(machineName), new ArrayList<>());
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
        categoriesList = new ArrayList<>();
        // Machines are the key with Category the value in MachinetoCategoryMap
        Map<String, String> MachinetoCategoryMap = LoadNonMAMEresources.getMachinetoCategoryMap();
        for (String game : MachinetoCategoryMap.keySet()) {
            if (!categoriesList.contains(MachinetoCategoryMap.get(game))) {
                String key = MachinetoCategoryMap.get(game);
                categoriesList.add(key);
                CategoryMachineListMap.put(key, new ArrayList<>());
            }
        }
        // NOTE leave in to show running in shell window
        System.out.println(categoriesList);
        if (MFM.isDebug()) {
            MFM.getLogger().addToList(categoriesList.toString());
        }
    }

    private static void findControls(Machine machine) {
        // Has input and control(s)
        if (machine.getInput() != null &&
                machine.getInput().getControl() != null && !machine.getInput().getControl().isEmpty()) {
            for (Control control : machine.getInput().getControl()) {
                String type = control.getType();
                if (control.getWays() == null || control.getWays().isEmpty()) {
                    machineControllers.addMachine(type, machine.getName());
                } else {
                    ArrayList<String> controlArgs = new ArrayList<>(5);
                    controlArgs.add(type);
                    controlArgs.add(control.getWays());
                    if (control.getWays2() != null) {
                        controlArgs.add(control.getWays2());
                    }
                    if (control.getWays3() != null) {
                        controlArgs.add(control.getWays3());
                    }
                    machineControllers.addMachine(controlArgs, machine.getName());
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
            FolderINIfiles = new HashMap<>();

            for (File file : files) {
                if (MFM.isDebug()) {
                    MFM.getLogger().addToList(file.getName());
                }
                if (!file.getName().contains(MFM_Constants.CATVER_INI_FILENAME) &&
                        !file.getName().contains(MFM_Constants.CATVER_FULL_INI_FILENAME) &&
                        !file.getName().contains(MFM_Constants.NPLAYERS_INI_FILENAME)) {
                    /* LinkedHashMap returns the order elements are added
                     * TreeMap gives us the Natural order
                     * At least two common INI files are not ordered - Catlist.ini & Genre.ini
                     */
                    LinkedHashMap<String, String> map = new LinkedHashMap<>();
                    new ParseFolderINIs(file.getAbsolutePath(), map).processFile();
                    String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    // Convert to TreeMap for order see above
                    TreeMap<String, String> orderedMap = new TreeMap<>(map);
                    FolderINIfiles.put(name, orderedMap);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // TODO check can we ever get here without it being initialized??
    public static HashMap<String, Map<String, String>> INIfiles(HashSet<File> files) {
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

    public static MachineControllers getMachineControllers() {
        return machineControllers;
    }

}
