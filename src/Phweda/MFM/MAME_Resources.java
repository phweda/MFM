/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

package Phweda.MFM;

import Phweda.MFM.mame.*;
import Phweda.utils.FileUtils;
import Phweda.utils.PersistUtils;
import Phweda.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/2/2016
 * Time: 6:31 PM
 */
public class MAME_Resources {

    private static final String RESOURCECACHE = "RESOURCE_CACHE";
    private static final String EXTRASRESOURCECACHE = "EXTRAS_RESOURCE_CACHE";
    private static final String ZIPEXTRASRESOURCECACHE = "ZIP_EXTRAS_RESOURCE_CACHE";
    public static final String EXTRAS = "extras";
    public static final String ZIPEXTRAS = "zipextras";

    private static MAME_Resources ourInstance = new MAME_Resources();

    private static TreeMap<String, Object> persistCaches;
    private static TreeMap<String, TreeMap<String, File>> roms_chdsCache;
    private static TreeMap<String, TreeMap<String, File>> extrasResourceCache;
    private static TreeMap<String, TreeMap<String, String>> zipExtrasResourceCache;

    private static final Map<String, Machine> machines = MAMEInfo.getMame().getMachineMap();

    private static StringBuilder listResourceLog;

    private static final FileUtils.MFMcacheResourceFiles cacheResourceFiles = new FileUtils.MFMcacheResourceFiles();

    public static MAME_Resources getInstance() {
        return ourInstance;
    }

    public final boolean hasCache() {
        return roms_chdsCache != null && !roms_chdsCache.isEmpty();
    }

    private MAME_Resources() {
        loadCaches();
    }

    @SuppressWarnings("unchecked")
    private void loadCaches() {
        try {

            if (new File(MFM.MFM_SETTINGS_DIR + MFM.MAME_RESOURCES_CACHE).exists()) {
                persistCaches = (TreeMap<String, Object>)
                        PersistUtils.loadAnObject(MFM.MFM_SETTINGS_DIR + MFM.MAME_RESOURCES_CACHE);

                roms_chdsCache = (TreeMap<String, TreeMap<String, File>>) persistCaches.get(RESOURCECACHE);
                extrasResourceCache = (TreeMap<String, TreeMap<String, File>>) persistCaches.get(EXTRASRESOURCECACHE);
                zipExtrasResourceCache =
                        (TreeMap<String, TreeMap<String, String>>) persistCaches.get(ZIPEXTRASRESOURCECACHE);
            } else {
                MFM.logger.addToList("NO RESOURCE CACHE found");
                persistCaches = new TreeMap<String, Object>();
                roms_chdsCache = new TreeMap<String, TreeMap<String, File>>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCache() {
        PersistUtils.saveAnObject(persistCaches, MFM.MFM_SETTINGS_DIR + MFM.MAME_RESOURCES_CACHE);
    }

    public void scan() {
        // roms_chdsCache = new TreeMap<String, TreeMap<String, File>>();
        TreeMap<String, String> roots = MFMSettings.getResourceRoots();
        for (String root : roots.keySet()) {
            try {
                if (root.equals(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY)) {
                    extrasResourceCache = cacheResourceFiles.cacheExtrasFiles(Paths.get(roots.get(root)));
                    zipExtrasResourceCache = ZipUtils.getZipEntryNames(MFMSettings.getExtrasZipFilesMap());
                    persistCaches.put(EXTRASRESOURCECACHE, extrasResourceCache);
                    persistCaches.put(ZIPEXTRASRESOURCECACHE, zipExtrasResourceCache);
                } else {
                    roms_chdsCache.put(root, scanRoot(root, roots.get(root)));
                    persistCaches.put(RESOURCECACHE, roms_chdsCache);
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

    private static TreeMap<String, File> scanRoot(String key, String root) {
        TreeMap<String, File> map = new TreeMap<String, File>();
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
        }
        return map;
    }

    private static void logScanResults() {
        StringBuilder sb = new StringBuilder("RESOURCE SCAN results:\n");
        for (String key : roms_chdsCache.keySet()) {
            sb.append(key);
            sb.append(" : ");
            sb.append(roms_chdsCache.get(key).size());
            sb.append("\n");
        }
        int extrasSize = 0;
        for (String key : extrasResourceCache.keySet()) {
            extrasSize += extrasResourceCache.get(key).size();
        }
        sb.append(MFM_Constants.EXTRAS_FULL_SET_DIRECTORY);
        sb.append(" : ");
        sb.append(extrasSize);
        sb.append("\n");

        MFM.logger.addToList(sb.toString(), true);
    }

    /**
     * TreeMap is:
     * <p>
     * "roms" -> ArrayList of rom files
     * "chds" -> ArrayList of CHD files
     * <p>
     * "extras" -> TreeMap<String, ArrayList<File>> keys from MFMSettings.getFullSetExtrasDirectories()
     * "artwork" ...
     * "flyers"
     * "icons"  ...
     * "snap"
     * ......
     * "zipextras" -> TreeMap<String, TreeSet<String>> keys from MFMSettings.getExtrasZipFilesMap()
     *
     * @param listName name of the list
     * @param list     set of Machine names
     * @return TreeMap of list's resources
     */
    public TreeMap<String, Object> generateListResources(String listName, TreeSet<String> list) {
        listResourceLog = new StringBuilder();
        listResourceLog.append("Generating resource list for : ");
        listResourceLog.append(listName);

        try { // NOTE removed because SPLIT needs these too! 12/21/2016
            // If Merged or Split Set add Ancestors to the list
            if (!MFMSettings.isnonMerged()) {
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

    private static TreeMap<String, Object> getResourcesMap() {
        TreeMap<String, Object> resources = new TreeMap<>();
        resources.put(MFM_Constants.ROMS, new TreeSet<File>());
        resources.put(MFM_Constants.CHDS, new TreeSet<File>());
        TreeMap<String, TreeSet<File>> extras = new TreeMap<>();
        // TODO Shouldn't I check/update Extras full set directories here? They could have changed
        // TODO or perhaps on resource scan?
        for (String key : MFMSettings.getFullSetExtrasDirectories().keySet()) {
            extras.put(key, new TreeSet<File>());
        }

        if (MFMSettings.VIDsFullSetDir() != null && MFMSettings.VIDsFullSetDir().length() > 2) {
            extras.put(MFM_Constants.VIDEOS, new TreeSet<File>());
        }
        resources.put(EXTRAS, extras);

        TreeMap<String, TreeSet<String>> zipExtras = new TreeMap<String, TreeSet<String>>();
        for (String key : MFMSettings.getExtrasZipFilesMap().keySet()) {
            zipExtras.put(key, new TreeSet<String>());
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

    private static void getMachineResources(String machineName, TreeMap<String, Object> resources) {

        Machine machine = machines.get(machineName);
        if (machine == null) {
            // Should not get here!!
            MFM.logger.addToList("MAME_Resources.getMachineResources FAILED to find machine: " + machineName, true);
            return;
        }

        try {
            // Should be 1 and only 1 ROM .7z file // or zip now with 182 PD sets
            File romFile = getMachineROMFile(machineName);
            if (romFile != null) {
                ((TreeSet<File>) resources.get(MFM_Constants.ROMS)).add(romFile);
            } else {
                listResourceLog.append("FAILED to find ROM for Machine ");
                listResourceLog.append(machineName);
                listResourceLog.append(FileUtils.NEWLINE);
            }

            // Has CHD
            if (machine.getDisk().size() > 0) {
                ((TreeSet<File>) resources.get(MFM_Constants.CHDS)).addAll(getMachineCHDFiles(machine));
            }

            if (!machine.getSoftwarelist().isEmpty()) {
                getMachineSoftwareListFiles(machine, resources);
            }

            // NOTE assumption is that all Devices are ROMS
            if (machine.getDeviceRef().size() > 0) {
                ((TreeSet<File>) resources.get(MFM_Constants.ROMS)).addAll(getDeviceFiles(machine));
            }

            getExtrasFiles(machine, resources);
        } catch (Exception e) {
            e.printStackTrace();
            MFM.logger.addToList("MAME_Resources.getMachineResources EXCEPTION processing Machine " + machineName);
        }
    }

    private static File getMachineROMFile(String machineName) {
        TreeMap<String, File> romFiles = roms_chdsCache.get(MFM_Constants.ROMS_FULL_SET_DIRECTORY);
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
        TreeMap<String, File> CHDFiles = roms_chdsCache.get(MFM_Constants.CHDS_FULL_SET_DIRECTORY);
        ArrayList<File> chds = new ArrayList<File>();
        for (Disk disk : machine.getDisk()) {
            if (CHDFiles.containsKey(disk.getName())) {
                chds.add(CHDFiles.get(disk.getName()));
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
        TreeMap<String, File> listCHDFiles = roms_chdsCache.get(MFM_Constants.SOFTWARELIST_CHDS_FULL_SET_DIRECTORY);
        TreeMap<String, File> listROMFiles = roms_chdsCache.get(MFM_Constants.SOFTWARELIST_ROMS_FULL_SET_DIRECTORY);

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

    private static void checkRomof(TreeSet<String> list) {
        TreeSet<String> addList = new TreeSet<String>();
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
        TreeSet<File> files = new TreeSet<File>();
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

                    /*
                    005\
                    depthch\
                    invaders\
                    qbert\
                    zaxxon\

                    java.lang.NullPointerException
	at Phweda.MFM.MAME_Resources.getExtrasFiles(MAME_Resources.java:363)
	at Phweda.MFM.MAME_Resources.getMachineResources(MAME_Resources.java:272)
	at Phweda.MFM.MAME_Resources.generateListResources(MAME_Resources.java:194)
                    */

                    // inner cast to get Extras TreeMap of outer cast of Arraylist of files
                    ((TreeSet<File>) ((TreeMap<String, Object>) resources.get(EXTRAS)).get(key)).add(
                            extrasResourceCache.get(key).get(machineName));
                } catch (Exception e) {
                    if(e instanceof NullPointerException){
                        String message = "Machine getExtrasFiles threw NullPointerException for : " + machineName +
                                " - key was " + key;
                        System.out.print(message);
                        MFM.logger.out(message);
                    } else
                    {
                        e.printStackTrace();
                    }
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
        // So now for the zipped resources if they exist TODO
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
        MFM.logger.out("Checking for required parent or device ROMs : " + machineName);
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

}
