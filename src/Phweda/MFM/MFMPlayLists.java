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

package Phweda.MFM;

import Phweda.utils.PersistUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Phweda.MFM.MFMListBuilder.*;

/**
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
    private TreeMap<String, TreeSet<String>> MFMPlayListsTree = null;
    private TreeMap<String, TreeSet<String>> languagesLists = null;
    private TreeMap<String, TreeSet<String>> myPlayListsTree = null;
    private ArrayList<String> allListsNames;
    private ArrayList<String> myListsNames;

    private TreeSet<String> allMachineNames = null;
    private TreeSet<String> biosMachineNames = null;
    private TreeSet<String> deviceMachineNames = null;
    private TreeSet<String> runnableMachineNames = null;
    private TreeSet<String> noCloneMachineNames = null;
    // Remove as VIDs project is dead
    // private TreeSet<String> VIDsMachineNames = null;
    private TreeSet<String> clonesMachineNames = null;
    private TreeSet<String> verticalsMachineNames = null;
    private TreeSet<String> horizontalsMachineNames = null;
    private TreeSet<String> cocktailMachineNames = null;
    private TreeSet<String> simultaneousMachineNames = null;
    private TreeSet<String> systemMachineNames = null;
    private TreeSet<String> arcadeMachineNames = null;
    private TreeSet<String> CHDMachineNames = null;
    private TreeSet<String> noImperfectMachineNames = null;

    private TreeSet<String> rasterDisplayMachineNames = null;
    private TreeSet<String> vectorDisplayMachineNames = null;
    private TreeSet<String> lcdDisplayMachineNames = null;

    // Note very hackish - quick way to populate the set inline
    private TreeSet<String> doNotPopulateList = new TreeSet<String>() {{
        add(ALL);
        add(SYSTEMS);
    }};

    private MFMPlayLists() {
        loadPlayLists();
        populateNames();
    }

    public void refreshLists() {
        MFMListBuilder.initLists();
        allMachineNames = null;
        loadPlayLists();
        populateNames();
    }

    private void populateNames() {
        allListsNames = new ArrayList<>(MFMPlayListsTree.keySet());
        allListsNames.addAll(myPlayListsTree.keySet());
        allListsNames.addAll(languagesLists.keySet());
        myListsNames = new ArrayList<String>(myPlayListsTree.keySet());
    }

    public static MFMPlayLists getInstance() {
        if (playlists == null) {
            playlists = new MFMPlayLists();
        }
        return playlists;
    }

    public TreeMap<String, TreeSet> getMFMplaylistsTree() {
        TreeMap<String, TreeSet> tempMap = new TreeMap<String, TreeSet>();
        for (String key : MFMPlayListsTree.keySet()) {
            if (MFMPlayListsTree.get(key).size() > 0) {
                tempMap.put(key, MFMPlayListsTree.get(key));
            }
        }
        return tempMap;
    }

    public TreeSet<String> getListBuilderPlaylistsKeys() {
        TreeSet<String> tempSet = new TreeSet<String>(MFMPlayListsTree.keySet());
        // Remove non-buildable lists ALL == New see MFM_Menubar
        tempSet.remove(ALL);
        tempSet.remove(BIOS);
        tempSet.remove(CHD);
        tempSet.remove(CLONE);
        tempSet.remove(DEVICES);
        tempSet.remove(PD_VIDS);

        tempSet.addAll(languagesListsMap.keySet());
        return tempSet;
    }

    public String[] getLanguagesPLsKeys() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(ALL_LANGUAGES);
        list.addAll(languagesLists.keySet());
        return list.toArray(new String[list.size()]);
    }

    public TreeMap<String, TreeSet<String>> getLanguagesListMap() {
        return languagesListsMap;
    }

    public TreeMap<String, TreeSet<String>> getMyPlayListsTree() {
        return myPlayListsTree;
    }

    public TreeMap<String, TreeSet<String>> getALLPlayListsTree() {
        TreeMap<String, TreeSet<String>> allTree = new TreeMap<String, TreeSet<String>>();
        allTree.putAll(MFMPlayListsTree);
        allTree.putAll(myPlayListsTree);
        allTree.putAll(languagesLists);
        return allTree;
    }


    public TreeSet<String> createPlayList(String name, String[] games) {
        TreeSet<String> treeSet = new TreeSet(Arrays.asList(games));
        myPlayListsTree.put(name, treeSet);
        persistPlayLists();
        allListsNames.add(name);
        persistPlayLists();
        return treeSet;
    }

    TreeSet<String> createPlayList(String name, TreeSet<String> games) {
        TreeSet<String> treeSet = new TreeSet(games);
        myPlayListsTree.put(name, treeSet);
        persistPlayLists();
        allListsNames.add(name);
        persistPlayLists();
        return treeSet;
    }

    public Object[] PlayListNames() {
        return getAllPlayListNames().toArray();
    }

    private Set<String> getAllPlayListNames() {
        return new TreeSet<>(allListsNames);
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

    public TreeSet<String> getPlayList(String name) {
        if (MFMPlayListsTree.containsKey(name)) {
            return MFMPlayListsTree.get(name);
        } else if (languagesLists.containsKey(name)) {
            return languagesLists.get(name);
        } else {
            return myPlayListsTree.get(name);
        }
    }

    public void removePlayList(String name) {
        myPlayListsTree.remove(name);
        allListsNames.remove(name);
        persistPlayLists();
    }

    public void addGameToPlayList(String listName, String gameName) {
        myPlayListsTree.get(listName).add(gameName);
        persistPlayLists();
    }

    public void removeGameFromPlayList(String listName, String gameName) {
        myPlayListsTree.get(listName).remove(gameName);
        persistPlayLists();
    }

    private void loadPlayLists() {

        Path path = Paths.get(MFM.MFM_SETTINGS_DIR + "MFM_Playlists.xml");
        if (path.toFile().exists()) {
            try {
                myPlayListsTree = (TreeMap<String, TreeSet<String>>) PersistUtils.loadAnObjectXML(
                        MFM.MFM_SETTINGS_DIR + "MFM_Playlists.xml");

            } catch (Exception e) {
                e.printStackTrace(MFM.logger.Writer());
            }
        }
        if (myPlayListsTree == null) {
            myPlayListsTree = new TreeMap<String, TreeSet<String>>();
        }
        // We retrieve the built in game lists each time
        // They are persisted by MAMEInfo in MFM_Data
        boolean ALL = MFMSettings.getInstance().getDataVersion().contains("ALL");
        if (allMachineNames == null) {
            // fixme How did we get here without MFMListBuilder existing? NOTE as of May 2017 no repro
            if (MFMListBuilder.getAllList() == null) {
                MFM.logger.addToList("MFMListBuilder is null in MFMPlayLists");
                MFM.logger.addToList(Arrays.toString(Thread.currentThread().getStackTrace()));
            }
            allMachineNames = MFMListBuilder.getAllList();
            biosMachineNames = MFMListBuilder.getBiosList();
            deviceMachineNames = MFMListBuilder.getDevicesList();
            // Need to determine by Data Set NOT application flag
            if (ALL) {
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
            CHDMachineNames = MFMListBuilder.getCHDList();
            noImperfectMachineNames = MFMListBuilder.getNoImperfectList();
        }
        MFMPlayListsTree = new TreeMap<String, TreeSet<String>>();
        MFMPlayListsTree.put(MFMListBuilder.ALL, allMachineNames);
        // NOTE as of 0.85 we have multiple built in lists that DO NOT exist
        if (biosMachineNames != null && !biosMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.BIOS, biosMachineNames);
        }
        if (deviceMachineNames != null && !deviceMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.DEVICES, deviceMachineNames);
        }

        if (ALL) {
            MFMPlayListsTree.put(MFMListBuilder.RUNNABLE, runnableMachineNames);
        }
        MFMPlayListsTree.put(MFMListBuilder.CLONE, clonesMachineNames);
        MFMPlayListsTree.put(MFMListBuilder.NO_CLONE, noCloneMachineNames);
        //    MFMPlayListsTree.put(MFMListBuilder.PD_VIDS, VIDsMachineNames);
        if (verticalsMachineNames != null && !verticalsMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.VERTICAL, verticalsMachineNames);
        }
        if (horizontalsMachineNames != null && !horizontalsMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.HORIZONTAL, horizontalsMachineNames);
        }
        if (cocktailMachineNames != null && !cocktailMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.COCKTAIL, cocktailMachineNames);
        }
        if (simultaneousMachineNames != null && !simultaneousMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.SIMULTANEOUS, simultaneousMachineNames);
        }
        MFMPlayListsTree.put(MFMListBuilder.ARCADE, arcadeMachineNames);
        MFMPlayListsTree.put(MFMListBuilder.SYSTEMS, systemMachineNames);
        if (rasterDisplayMachineNames != null && !rasterDisplayMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.RASTER, rasterDisplayMachineNames);
        }
        if (vectorDisplayMachineNames != null && !vectorDisplayMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.VECTOR, vectorDisplayMachineNames);
        }
        if (lcdDisplayMachineNames != null && !lcdDisplayMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.LCD, lcdDisplayMachineNames);
        }
        if (CHDMachineNames != null && !CHDMachineNames.isEmpty()) {
            MFMPlayListsTree.put(MFMListBuilder.CHD, CHDMachineNames);
        }
        MFMPlayListsTree.put(MFMListBuilder.NO_IMPERFECT, noImperfectMachineNames);

        languagesLists = MFMListBuilder.getLanguagesListsMap();
    }

    public void persistPlayLists() {
        PersistUtils.saveAnObjectXML(myPlayListsTree, MFM.MFM_SETTINGS_DIR + "MFM_Playlists.xml");
    }

}
