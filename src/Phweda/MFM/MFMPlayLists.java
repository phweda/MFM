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

package Phweda.MFM;

import Phweda.MFM.mame.softwarelist.Software;
import Phweda.MFM.mame.softwarelist.Softwarelists;
import Phweda.utils.PersistUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Phweda.MFM.MFMListBuilder.*;

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/30/11
 * Time: 1:40 PM
 */

/**
 * A list of Playlists
 */
public class MFMPlayLists implements Serializable {
    private static MFMPlayLists playlists;
    private TreeMap<String, SortedSet<String>> mfmPlayListsTree = null;
    private transient SortedMap<String, SortedSet<String>> languagesLists = null;
    private transient Softwarelists softwareLists = null;
    private TreeMap<String, SortedSet<String>> myPlayListsTree = null;
    private ArrayList<String> allListsNames;
    private ArrayList<String> myListsNames;

    private TreeSet<String> allMachineNames = null;
    private TreeSet<String> biosMachineNames = null;
    private TreeSet<String> deviceMachineNames = null;
    private transient SortedSet<String> runnableMachineNames = null;
    private TreeSet<String> noCloneMachineNames = null;
    // Remove as VIDs project is dead
    // private TreeSet<String> VIDsMachineNames = null;
    private transient SortedSet<String> clonesMachineNames = null;
    private transient SortedSet<String> verticalsMachineNames = null;
    private transient SortedSet<String> horizontalsMachineNames = null;
    private transient SortedSet<String> cocktailMachineNames = null;
    private transient SortedSet<String> simultaneousMachineNames = null;
    private transient SortedSet<String> systemMachineNames = null;
    private transient SortedSet<String> arcadeMachineNames = null;
    private transient SortedSet<String> chdMachineNames = null;
    private transient SortedSet<String> noImperfectMachineNames = null;

    private transient SortedSet<String> rasterDisplayMachineNames = null;
    private transient SortedSet<String> vectorDisplayMachineNames = null;
    private transient SortedSet<String> lcdDisplayMachineNames = null;

    // We originally named an All list which does not include BIOS or DEVICES so everything is that combined list
    public static final String EVERYTHING = "Everything";
    private static final String MFM_PLAYLISTS_XML = "MFM_Playlists.xml";

    private MFMPlayLists() {
        loadPlayLists();
        populateNames();
    }

    static void refreshLists() {
        playlists = new MFMPlayLists();
    }

    private void populateNames() {
        allListsNames = new ArrayList<>(mfmPlayListsTree.keySet());
        allListsNames.addAll(myPlayListsTree.keySet());
        allListsNames.addAll(languagesLists.keySet());
        myListsNames = new ArrayList<>(myPlayListsTree.keySet());
    }

    public static MFMPlayLists getInstance() {
        if (playlists == null) {
            playlists = new MFMPlayLists();
        }
        return playlists;
    }

    public SortedMap<String, SortedSet<String>> getMFMplaylistsTree() {
        TreeMap<String, SortedSet<String>> tempMap = new TreeMap<>();
        for (Map.Entry<String, SortedSet<String>> entry : mfmPlayListsTree.entrySet()) {
            if (!mfmPlayListsTree.get(entry.getKey()).isEmpty()) {
                tempMap.put(entry.getKey(), entry.getValue());
            }
        }
        return tempMap;
    }

    public SortedSet<String> getListBuilderPlaylistsKeys() {
        TreeSet<String> tempSet = new TreeSet<>(mfmPlayListsTree.keySet());
        // Remove non-buildable lists ALL == New see MFM_Menubar
        tempSet.remove(ALL);
        tempSet.remove(BIOS);
        tempSet.remove(CHD);
        tempSet.remove(CLONE);
        tempSet.remove(DEVICES);
        tempSet.addAll(languagesListsMap.keySet());
        return tempSet;
    }

    public SortedSet<String> getListEditorKeys() {
        TreeSet<String> tempSet = new TreeSet<>(mfmPlayListsTree.keySet());
        tempSet.remove(ALL);
        tempSet.remove(BIOS);
        tempSet.remove(DEVICES);
        tempSet.addAll(languagesListsMap.keySet());
        if (myPlayListsTree.size() > 0) {
            tempSet.addAll(myPlayListsTree.keySet());
        }
        return tempSet;
    }

    public String[] getLanguagesPlayListsKeys() {
        ArrayList<String> list = new ArrayList<>();
        list.add(ALL_LANGUAGES);
        list.addAll(languagesLists.keySet());
        return list.toArray(new String[0]);
    }

    public SortedMap<String, SortedSet<String>> getLanguagesListMap() {
        return languagesListsMap;
    }

    public SortedMap<String, SortedSet<String>> getMyPlayListsTree() {
        return myPlayListsTree;
    }

    public SortedMap<String, SortedSet<String>> getALLPlayListsTree() {
        TreeMap<String, SortedSet<String>> allTree = new TreeMap<>();
        allTree.putAll(mfmPlayListsTree);
        allTree.putAll(myPlayListsTree);
        allTree.putAll(languagesLists);
        return allTree;
    }

    public void createPlayList(String name, String[] machines) {
        TreeSet<String> treeSet = new TreeSet<>(Arrays.asList(machines));
        myPlayListsTree.put(name, treeSet);
        allListsNames.add(name);
        myListsNames.add(name);
        persistPlayLists();
    }

    void createPlayList(String name, TreeSet<String> machines) {
        TreeSet<String> treeSet = new TreeSet<>(machines);
        myPlayListsTree.put(name, treeSet);
        allListsNames.add(name);
        myListsNames.add(name);
        persistPlayLists();
    }

    public Object[] playListNames() {
        return getAllPlayListNames().toArray();
    }

    private Set<String> getAllPlayListNames() {
        return new TreeSet<>(allListsNames);
    }

    public Set<String> getMFMListNames() {
        return mfmPlayListsTree.keySet();
    }

    public String getNextListName(String currentName, boolean next) {
        int index = allListsNames.indexOf(currentName);
        if (next) {
            if (++index == allListsNames.size()) {
                index = 0;
            }
        } else {
            if (--index == -1) {
                index = allListsNames.size() - 1;
            }
        }
        return allListsNames.get(index);
    }

    public String getNextMyListName(String currentName, boolean next) {
        int index = myListsNames.indexOf(currentName);
        if (next) {
            if (++index == myListsNames.size()) {
                index = 0;
            }
        } else {
            if (--index == -1) {
                index = myListsNames.size() - 1;
            }
        }
        return myListsNames.get(index);
    }

    public Object[] myPlayListNames() {
        return myListsNames.toArray();
    }

    public String[] getListBuilderNames() {
        ArrayList<String> builderNames = new ArrayList<>();
        if (MFMSettings.getInstance().getDataVersion().contains(MFMListBuilder.ALL)) {
            builderNames.add(MFMListBuilder.RUNNABLE);
        }
        builderNames.add(MFMListBuilder.ALL);
        builderNames.addAll(myListsNames);
        return builderNames.toArray(new String[0]);
    }

    public SortedSet<String> getPlayList(String listName) {
        // TODO determine if we want Softwarelists included in this output or is it separate?
        // To support JSON and future DB. Get everything
        if (listName.equalsIgnoreCase(EVERYTHING)) {
            TreeSet<String> everything = new TreeSet<>(mfmPlayListsTree.get(MFMListBuilder.ALL));
            everything.addAll(mfmPlayListsTree.get(MFMListBuilder.BIOS));
            everything.addAll(mfmPlayListsTree.get(MFMListBuilder.DEVICES));
            return everything;
        }
        if (mfmPlayListsTree.containsKey(listName)) {
            return mfmPlayListsTree.get(listName);
        } else if (languagesLists.containsKey(listName)) {
            return languagesLists.get(listName);
        } else if (myPlayListsTree.containsKey(listName)) {
            return myPlayListsTree.get(listName);
        } else if (softwareLists.getSoftwarelistsMap().containsKey(listName)) {
            return getSoftwareListEntries(listName);
        }
        return new TreeSet<>();
    }

    private TreeSet<String> getSoftwareListEntries(String softwareListName) {
        // Softwarelists are list of programs for a given system
        TreeSet<String> programs = new TreeSet<>();
        List<Software> softwares = softwareLists.getSoftwarelistsMap().get(softwareListName).getSoftware();
        for (Software software : softwares) {
            programs.add(software.getName());
        }
        return programs;
    }

    public void removePlayList(String name) {
        myPlayListsTree.remove(name);
        allListsNames.remove(name);
        myListsNames.remove(name);
        persistPlayLists();
    }

    public void addMachineToPlayList(String listName, String machineName) {
        myPlayListsTree.get(listName).add(machineName);
        persistPlayLists();
    }

    public void removeMachineFromPlayList(String listName, String machineName) {
        myPlayListsTree.get(listName).remove(machineName);
        persistPlayLists();
    }

    private void loadPlayLists() {
        Path path = Paths.get(MFM.getMfmSettingsDir() + MFM_PLAYLISTS_XML);
        if (path.toFile().exists()) {
            try {
                myPlayListsTree = (TreeMap<String, SortedSet<String>>) PersistUtils.loadAnObjectXML(
                        MFM.getMfmSettingsDir() + MFM_PLAYLISTS_XML);

            } catch (Exception e) {
                e.printStackTrace(MFM.getLogger().writer());
            }
        }
        if (myPlayListsTree == null) {
            myPlayListsTree = new TreeMap<>();
        }
        // We retrieve the built in Machine lists each time
        // They are persisted by MAMEInfo in MFM_Data
        boolean all = MFMSettings.getInstance().getDataVersion().contains(MFMListBuilder.ALL);
        if (allMachineNames == null) {
            if (MFMListBuilder.getAllList() == null) {
                MFM.getLogger().addToList("MFMListBuilder is null in MFMPlayLists");
                MFM.getLogger().addToList(Arrays.toString(Thread.currentThread().getStackTrace()));
            }
            allMachineNames = MFMListBuilder.getAllList();
            biosMachineNames = MFMListBuilder.getBiosList();
            deviceMachineNames = MFMListBuilder.getDevicesList();
            // Need to determine by Data Set NOT application flag
            if (all) {
                runnableMachineNames = MFMListBuilder.getRunnableList();
            }
            clonesMachineNames = MFMListBuilder.getClonesList();
            noCloneMachineNames = MFMListBuilder.getNoClonesList();
            //    VIDsMachineNames = MFMListBuilder.getVIDsList();
            verticalsMachineNames = MFMListBuilder.getVerticalsList();
            horizontalsMachineNames = MFMListBuilder.getHorizontalsList();
            cocktailMachineNames = MFMListBuilder.getCocktailsList();
            simultaneousMachineNames = MFMListBuilder.getSimultaneousList();
            arcadeMachineNames = MFMListBuilder.getArcadeList();
            systemMachineNames = MFMListBuilder.getSystemList();
            rasterDisplayMachineNames = MFMListBuilder.getRasterDisplayList();
            vectorDisplayMachineNames = MFMListBuilder.getVectorDisplayList();
            lcdDisplayMachineNames = MFMListBuilder.getLcdDisplayList();
            chdMachineNames = MFMListBuilder.getChdList();
            noImperfectMachineNames = MFMListBuilder.getNoImperfectList();
        }
        mfmPlayListsTree = new TreeMap<>();
        mfmPlayListsTree.put(MFMListBuilder.ALL, allMachineNames);
        // NOTE as of 0.85 we have multiple built in lists that DO NOT exist
        if (biosMachineNames != null && !biosMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.BIOS, biosMachineNames);
        }
        if (deviceMachineNames != null && !deviceMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.DEVICES, deviceMachineNames);
        }

        if (all) {
            mfmPlayListsTree.put(MFMListBuilder.RUNNABLE, runnableMachineNames);
        }
        mfmPlayListsTree.put(MFMListBuilder.CLONE, clonesMachineNames);
        mfmPlayListsTree.put(MFMListBuilder.NO_CLONE, noCloneMachineNames);
        //    mfmPlayListsTree.put(MFMListBuilder.PD_VIDS, VIDsMachineNames);
        if (verticalsMachineNames != null && !verticalsMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.VERTICAL, verticalsMachineNames);
        }
        if (horizontalsMachineNames != null && !horizontalsMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.HORIZONTAL, horizontalsMachineNames);
        }
        if (cocktailMachineNames != null && !cocktailMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.COCKTAIL, cocktailMachineNames);
        }
        if (simultaneousMachineNames != null && !simultaneousMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.SIMULTANEOUS, simultaneousMachineNames);
        }
        mfmPlayListsTree.put(MFMListBuilder.ARCADE, arcadeMachineNames);
        if (systemMachineNames != null && !systemMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.SYSTEMS, systemMachineNames);
        }
        if (rasterDisplayMachineNames != null && !rasterDisplayMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.RASTER, rasterDisplayMachineNames);
        }
        if (vectorDisplayMachineNames != null && !vectorDisplayMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.VECTOR, vectorDisplayMachineNames);
        }
        if (lcdDisplayMachineNames != null && !lcdDisplayMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.LCD, lcdDisplayMachineNames);
        }
        if (chdMachineNames != null && !chdMachineNames.isEmpty()) {
            mfmPlayListsTree.put(MFMListBuilder.CHD, chdMachineNames);
        }
        mfmPlayListsTree.put(MFMListBuilder.NO_IMPERFECT, noImperfectMachineNames);

        languagesLists = MFMListBuilder.getLanguagesListsMap();
        softwareLists = MAMEInfo.getSoftwareLists();
    }

    public void persistPlayLists() {
        PersistUtils.saveAnObjectXML(myPlayListsTree, MFM.getMfmSettingsDir() + MFM_PLAYLISTS_XML);
    }

}
