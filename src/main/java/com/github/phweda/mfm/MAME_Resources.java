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

package com.github.phweda.mfm;

import com.github.phweda.mfm.mame.DeviceRef;
import com.github.phweda.mfm.mame.Disk;
import com.github.phweda.mfm.mame.Machine;
import com.github.phweda.mfm.mame.Softwarelist;
import com.github.phweda.mfm.mame.softwarelist.Software;
import com.github.phweda.utils.FileUtils;
import com.github.phweda.utils.PersistUtils;
import com.github.phweda.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/2/2016
 * Time: 6:31 PM
 */

/**
 * MAME_Resources detects and stores the ROMs CHDs and Extras files from the User's entered sets
 */
@SuppressWarnings({"squid:S1845", "unchecked"})
public class MAME_Resources {

    public static final String EXTRAS = "extras";
    public static final String ZIPEXTRAS = "zipextras";
    private static final String RESOURCECACHE = "RESOURCE_CACHE";
    private static final String EXTRASRESOURCECACHE = "EXTRAS_RESOURCE_CACHE";
    private static final String ZIPEXTRASRESOURCECACHE = "ZIP_EXTRAS_RESOURCE_CACHE";
    private static final Map<String, Machine> machines = MAMEInfo.getMame().getMachineMap();
    private static final TreeMap<String, com.github.phweda.mfm.mame.softwarelist.Softwarelist> softwarelistsMap =
            MAMEInfo.getSoftwareLists().getSoftwarelistsMap();
    private static final FileUtils.MFMcacheResourceFiles cacheResourceFiles = new FileUtils.MFMcacheResourceFiles();
    private static MAME_Resources ourInstance = new MAME_Resources();
    private static SortedMap<String, Object> persistCaches;
    private static SortedMap<String, SortedMap<String, File>> romsChdsCache;
    private static SortedMap<String, SortedMap<String, File>> extrasResourceCache;
    private static SortedMap<String, SortedMap<String, String>> zipExtrasResourceCache;
    private static StringBuilder listResourceLog;

    private static MFMSettings mfmSettings = MFMSettings.getInstance();

    private MAME_Resources() {
        loadCaches();
    }

    public static MAME_Resources getInstance() {
        return ourInstance;
    }

    private static TreeMap<String, File> scanRoot(String key, String root) {
        TreeMap<String, File> map = new TreeMap<>();
        switch (key) {
            case MFM_Constants.ROMS_FULL_SET_DIRECTORY:
            case MFM_Constants.CHDS_FULL_SET_DIRECTORY:
            case MFM_Constants.MAME_VIDS_DIRECTORY:
                cacheResourceFiles.cacheAllFiles(Paths.get(root), map, false);
                break;
            case MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY:
                cacheResourceFiles.cacheAllFiles(Paths.get(root), map, true);
                break;
            case MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY:
                cacheResourceFiles.cacheSoftwarelistCHDsFiles(Paths.get(root), map);
                break;
            default:
                break;
        }
        return map;
    }

    private static void logScanResults() {
        StringBuilder sb = new StringBuilder("RESOURCE SCAN results:\n");
        for (Map.Entry<String, SortedMap<String, File>> entry : romsChdsCache.entrySet()) {
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue().size());
            sb.append("\n");
        }
        int extrasSize = extrasResourceCache.keySet().stream().mapToInt(key -> extrasResourceCache.get(key).size()).sum();
        sb.append(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
        sb.append(" : ");
        sb.append(extrasSize);
        sb.append("\n");

        MFM.getLogger().addToList(sb.toString(), true);
    }

    private static TreeMap<String, Object> getResourcesMap() {
        TreeMap<String, Object> resources = new TreeMap<>();
        resources.put(MFM_Constants.ROMS, new TreeSet<File>());
        resources.put(MFM_Constants.CHDS, new TreeSet<File>());
        TreeMap<String, TreeSet<File>> extras = new TreeMap<>();
        for (String key : mfmSettings.getFullSetExtrasDirectories().keySet()) {
            extras.put(key, new TreeSet<File>());
        }

        if (mfmSettings.VIDsFullSetDir() != null && mfmSettings.VIDsFullSetDir().length() > 2) {
            extras.put(MFM_Constants.VIDEOS, new TreeSet<File>());
        }
        resources.put(EXTRAS, extras);

        TreeMap<String, TreeSet<String>> zipExtras = new TreeMap<>();
        for (String key : mfmSettings.getExtrasZipFilesMap().keySet()) {
            zipExtras.put(key, new TreeSet<>());
        }
        resources.put(ZIPEXTRAS, zipExtras);
        return resources;
    }

    private static int calcMapofMapsTotalSize(Map map, Class<?> classType) {
        int files = 0;
        if (map.values().toArray()[0] instanceof File) {
            files += map.size();
        } else if (map.values().toArray()[0] instanceof Map) {
            while (map.keySet().iterator().hasNext()) {
                calcMapofMapsTotalSize((Map) map.keySet().iterator().next(), classType);
            }
        }
        return files;
    }

    private static void getMachineResources(String itemName, TreeMap<String, Object> resources) {
        Machine machine = machines.get(itemName);
        Software software;
        if (machine == null && itemName.contains(MFM_Constants.SOFTWARE_LIST_SEPARATER)) {

            String[] split = itemName.split(MFM_Constants.SOFTWARE_LIST_SEPARATER);
            String listName = split[0];
            String nameMatch = split[1];
            software = softwarelistsMap.get(listName).getSoftware()
                    .stream()
                    .filter(software1 -> software1.getName().equalsIgnoreCase(nameMatch))
                    .collect(Collectors.reducing((a, b) -> null))
                    .get();

            getSoftwareResources(listName, software, resources);
            return;
        }

        try {
            // Should be 1 and only 1 ROM zip now with 182 PD sets
            File romFile = getMachineROMFile(itemName);
            if (romFile != null) {
                ((TreeSet<File>) resources.get(MFM_Constants.ROMS)).add(romFile);
            } else {
                listResourceLog.append("FAILED to find ROM for Machine ");
                listResourceLog.append(itemName);
                listResourceLog.append(FileUtils.NEWLINE);
            }

            // Has CHD
            if (!machine.getDisk().isEmpty()) {
                ((TreeSet<File>) resources.get(MFM_Constants.CHDS)).addAll(getMachineCHDFiles(machine));
            }

            if (!machine.getSoftwarelist().isEmpty()) {
                getMachineSoftwareListFiles(machine, resources);
            }

            // NOTE assumption is that all Devices are ROMS
            if (!machine.getDeviceRef().isEmpty()) {
                ((TreeSet<File>) resources.get(MFM_Constants.ROMS)).addAll(getDeviceFiles(machine));
            }

            getExtrasFiles(machine, resources);
        } catch (Exception e) {
            e.printStackTrace();
            MFM.getLogger().addToList("MAME_Resources.getMachineResources EXCEPTION processing Machine " + itemName);
        }
    }

    private static void getSoftwareResources(String listName, Software software, TreeMap<String, Object> resources) {
        // TODO
    }


    private static File getMachineROMFile(String machineName) {
        SortedMap<String, File> romFiles = romsChdsCache.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
        if (romFiles.containsKey(machineName)) {
            return romFiles.get(machineName);
        } else {
            // This spits out with numerous devices which are NOT expected to be found
            // COMPARE machineName to ALL list??????
            listResourceLog.append("FAILED to find ROM for Machine ");
            listResourceLog.append(machineName);
            listResourceLog.append(FileUtils.NEWLINE);
        }
        return null;
    }

    private static ArrayList<File> getMachineCHDFiles(Machine machine) {
        SortedMap<String, File> chdFiles = romsChdsCache.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
        ArrayList<File> chds = new ArrayList<>();
        for (Disk disk : machine.getDisk()) {
            if (chdFiles.containsKey(disk.getName())) {
                chds.add(chdFiles.get(disk.getName()));
            } else {
                listResourceLog.append("FAILED to find CHD ");
                listResourceLog.append(disk);
                listResourceLog.append(" for Machine ");
                listResourceLog.append(machine.getName());
                listResourceLog.append(FileUtils.NEWLINE);
            }
        }
        return chds;
    }

    private static void getMachineSoftwareListFiles(Machine machine, Map resources) {
        SortedMap<String, File> listCHDFiles = romsChdsCache.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
        SortedMap<String, File> listROMFiles = romsChdsCache.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);

        for (Softwarelist softwarelist : machine.getSoftwarelist()) {
            if (listCHDFiles.containsKey(softwarelist.getName())) {
                ((TreeSet<File>) resources.get(MFM_Constants.CHDS)).add(listCHDFiles.get(softwarelist.getName()));
            } else if (listROMFiles.containsKey(softwarelist.getName())) {
                ((TreeSet<File>) resources.get(MFM_Constants.ROMS)).add(listROMFiles.get(softwarelist.getName()));
            } else {
                listResourceLog.append("FAILED to find Softwarelist ");
                listResourceLog.append(softwarelist);
                listResourceLog.append(" for Machine ");
                listResourceLog.append(machine.getName());
                listResourceLog.append(FileUtils.NEWLINE);
            }
        }
    }

    private static void checkRomof(SortedSet<String> list) {
        TreeSet<String> addList = new TreeSet<>();
        for (String machineName : list) {
            addRomof(machineName, addList);
        }
        list.addAll(addList);
    }

    private static void addRomof(String machineName, TreeSet<String> list) {
        Machine machine = machines.get(machineName);
        if (machine != null && machine.getRomof() != null && machine.getRomof().length() > 0) {
            String parent = machine.getRomof();
            list.add(parent);
            addRomof(parent, list);
        }
    }

    private static TreeSet<File> getDeviceFiles(Machine machine) {
        TreeSet<File> files = new TreeSet<>();
        for (DeviceRef deviceRef : machine.getDeviceRef()) {
            File rom = getMachineROMFile(deviceRef.getName());
            if (rom != null) {
                files.add(rom);
            }
        }
        return files;
    }

    private static void getExtrasFiles(Machine machine, Map resources) {
        String machineName = machine.getName();
        for (String key : extrasResourceCache.keySet()) {
            if (extrasResourceCache.get(key).containsKey(machineName)) {
                try {
                    // inner cast to get Extras TreeMap of outer cast of Arraylist of files
                    ((TreeSet<File>) ((TreeMap<String, Object>) resources.get(EXTRAS)).get(key)).add(
                            extrasResourceCache.get(key).get(machineName));
                } catch (NullPointerException e) {
                    String message = "Machine getExtrasFiles threw NullPointerException for : " + machineName +
                            " - key was " + key;
                    System.out.print(message);
                    MFM.getLogger().out(message);
                }
            }
            // If no Artwork search ancestors
            else if (key.equals(MFM_Constants.ARTWORK) && machine.getCloneof() != null &&
                    !machine.getCloneof().isEmpty()) {
                Machine tempMachine = machines.get(machine.getCloneof());
                while (tempMachine != null) {
                    if (extrasResourceCache.get(key).containsKey(tempMachine.getName())) {
                        // inner cast to get Extras TreeMap of outer cast of Arraylist of files
                        ((TreeSet<File>) ((TreeMap<String, Object>) resources.get(EXTRAS)).get(key)).add(
                                extrasResourceCache.get(key).get(tempMachine.getName()));
                        tempMachine = null;
                    } else {
                        tempMachine = machines.get(tempMachine.getCloneof());
                    }
                }
            }
        }
        if (zipExtrasResourceCache.size() > 0) {
            zipExtrasResourceCache.forEach((key2, value) -> {
                        if (value.containsKey(machineName)) {
                            ((Set<String>) ((TreeMap<String, Object>) resources.get(ZIPEXTRAS)).get(key2))
                                    .add(value.get(machineName));
                        }
                    }
            );
        }
    }

    private static void loadRequiredResources(String machineName) {
        // Get the getMachine object and check for romof and device_ref
        // Find and move those
        MFM.getLogger().out("Checking for required parent or device ROMs : " + machineName);
        Machine machine = MAMEInfo.getMachine(machineName);
        // NOTE we could get a null Machine??
        List devices = null;
        if (machine != null) {
            devices = machine.getDevice();
        } else {
            // TODO log this!!
            return;
        }
        if (machine.getRomof().length() > 0) {
            if (devices != null) {
                devices.add(machine.getRomof());
            } else {
                devices = new ArrayList<String>(1);
                devices.add(machine.getRomof());
            }
        }

        if (devices != null && !devices.isEmpty()) {
            for (Object name : devices) {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public final boolean hasCache() {
        return romsChdsCache != null && !romsChdsCache.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static void loadCaches() {
        try {

            if (new File(MFM.getMfmSettingsDir() + MFM.MAME_RESOURCES_CACHE).exists()) {
                persistCaches = (TreeMap<String, Object>)
                        PersistUtils.loadAnObject(MFM.getMfmSettingsDir() + MFM.MAME_RESOURCES_CACHE);

                romsChdsCache = (SortedMap<String, SortedMap<String, File>>) persistCaches.get(RESOURCECACHE);
                extrasResourceCache = (SortedMap<String, SortedMap<String, File>>) persistCaches.get(EXTRASRESOURCECACHE);
                zipExtrasResourceCache =
                        (SortedMap<String, SortedMap<String, String>>) persistCaches.get(ZIPEXTRASRESOURCECACHE);
            } else {
                MFM.getLogger().addToList("NO RESOURCE CACHE found");
                persistCaches = new TreeMap<>();
                romsChdsCache = new TreeMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCache() {
        PersistUtils.saveAnObject(persistCaches, MFM.getMfmSettingsDir() + MFM.MAME_RESOURCES_CACHE);
    }

    public static void scan() {
        // romsChdsCache = new TreeMap<String, TreeMap<String, File>>();
        TreeMap<String, String> roots = mfmSettings.getResourceRoots();
        for (String root : roots.keySet()) {
            try {
                if (root.equals(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY)) {
                    extrasResourceCache = cacheResourceFiles.cacheExtrasFiles(Paths.get(roots.get(root)));
                    zipExtrasResourceCache = ZipUtils.getZipEntryNames(mfmSettings.getExtrasZipFilesMap());
                    persistCaches.put(EXTRASRESOURCECACHE, extrasResourceCache);
                    persistCaches.put(ZIPEXTRASRESOURCECACHE, zipExtrasResourceCache);
                } else {
                    romsChdsCache.put(root, scanRoot(root, roots.get(root)));
                    persistCaches.put(RESOURCECACHE, romsChdsCache);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // NOTE do not need?? could have some nullpointers? Linux??
                // return;
            }
        }
        saveCache();
        logScanResults();
    }

    /**
     * TreeMap is:
     * <p>
     * "roms" -> ArrayList of rom files
     * "chds" -> ArrayList of CHD files
     * <p>
     * "extras" -> TreeMap<String, ArrayList<File>> keys from mfmSettings.getFullSetExtrasDirectories()
     * "artwork" ...
     * "flyers"
     * "icons"  ...
     * "snap"
     * ......
     * "zipextras" -> TreeMap<String, TreeSet<String>> keys from mfmSettings.getExtrasZipFilesMap()
     *
     * @param listName name of the list
     * @param list     set of Machine names
     * @return TreeMap of list's resources
     */
    public static SortedMap<String, Object> generateListResources(String listName, SortedSet<String> list) {
        listResourceLog = new StringBuilder();
        listResourceLog.append("Generating resource list for : ");
        listResourceLog.append(listName);

        try { // NOTE removed because SPLIT needs these too! 12/21/2016
            // If Merged or Split Set add Ancestors to the list
            if (!mfmSettings.isnonMerged()) {
                // adds ancestors to this list
                checkRomof(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeMap<String, Object> resources = getResourcesMap();
        try {
            for (String machine : list) {
                getMachineResources(machine, resources);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resources;
    }

}
