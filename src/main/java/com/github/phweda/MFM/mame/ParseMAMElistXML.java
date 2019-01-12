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

package com.github.phweda.MFM.mame;

import com.github.phweda.MFM.*;
import com.github.phweda.MFM.Utils.ParseFolderINIs;
import com.github.phweda.MFM.mame.softwarelist.Softwarelists;
import com.github.phweda.utils.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import static com.github.phweda.MFM.mame.Machine.PRELIMINARY;
import static com.github.phweda.MFM.mame.ParseMAMEexternalData.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/29/11
 * Time: 9:33 PM
 */
public final class ParseMAMElistXML {
    //   private static Map<String, Machine> allMachinesInfo = new TreeMap<String, Machine>();
    private static Mame mame = new Mame();
    private static Softwarelists softwarelists;
    private static final Set<String> runnable = new TreeSet<>();
    private static Map<String, Map<String, String>> folderINIfiles = null;
    private static final MFMSettings mfmSettings = MFMSettings.getInstance();

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
        MFM.getLogger().addToList(message + FileUtils.NEWLINE + "MAME exe is: " +
                MFMSettings.getInstance().fullMAMEexePath());

        try {
            mame = loadAllMachinesInfoJAXB();
            softwarelists = generateSoftwareLists();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (mame == null) {
                return null;
            }
        }

        // Set data version here. 0.85 change to handle older Mame versions
        if ((mame.getBuild() != null) && !mame.getBuild().isEmpty()) {
            mfmSettings.generateDataVersion(mame.getBuild());
        } else {
            mfmSettings.generateDataVersion(MAMEexe.getMAMEexeVersion());
            mame.setBuild(mfmSettings.getDataVersion());
        }

        if (!all) {
            removeNotRunnable();
        }
        for (Machine machine : mame.getMachineMap().values()) {
            addNonMAMEinfo(machine);
            if (isRunnable(machine)) {
                runnable.add(machine.getName());
            }
            findControls(machine);
        }
        findCategories();
        return mame;
    }

    private static void removeNotRunnable() {
        // NOTE This does leave us Devices and BIOS since they have no driver
        mame.getMachine()
                .removeIf(machine -> (machine.getDriver() != null) &&
                        machine.getDriver().getStatus().equals(PRELIMINARY));
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

            Process process = MAMEexe.run(MAMEexe.LISTXML);
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

    public static Set<String> getRunnable() {
        return runnable;
    }

    /**
     * Load any INI files in <MAME root>/folders
     * 7/31/2015 we now are going to include with MFM
     * Catver.ini
     */
    private static void loadFoldersINIs(Iterable<? extends File> files) {
        try {
            folderINIfiles = new HashMap<>(12);

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
                    LinkedHashMap<String, String> map = new LinkedHashMap<>(24);
                    new ParseFolderINIs(file.getAbsolutePath(), map).processFile();
                    String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
                    // Convert to TreeMap for order see above
                    Map<String, String> orderedMap = new TreeMap<>(map);
                    folderINIfiles.put(name, orderedMap);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, String>> iniFiles(Iterable<? extends File> files) {
        if (folderINIfiles == null) {
            loadFoldersINIs(files);
        }
        return Collections.unmodifiableMap(folderINIfiles);
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

}
