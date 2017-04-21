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

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/2014
 * Time: 2:47 PM
 */

import Phweda.MFM.UI.MFMUI;
import Phweda.MFM.UI.MFMUI_Setup;
import Phweda.MFM.mame.Control;
import Phweda.MFM.mame.Machine;
import Phweda.utils.PersistUtils;
import Phweda.utils.QuadState;
import Phweda.utils.TriState;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adding more built in lists i.e. noclones and PD VIDs
 * This provides the service of building those lists
 * Intent to later add wildcard builder capabilities i.e. (b*, noclone)
 * would build a list of all playable noclone games starting with b
 */
public final class MFMListBuilder {
    public static final String ALL = "ALL";
    public static final String RUNNABLE = "RUNNABLE";
    public static final String NO_IMPERFECT = "NOIMPERFECT";
    public static final String PD_VIDS = "PD VIDs";
    public static final String CLONE = "CLONE";
    public static final String DEVICES = "DEVICES";
    public static final String NO_CLONE = "NOCLONE";
    public static final String VERTICAL = "VERTICAL";
    public static final String HORIZONTAL = "HORIZONTAL";
    public static final String SIMULTANEOUS = "SIMULTANEOUS";
    public static final String COCKTAIL = "COCKTAIL";
    public static final String ARCADE = "ARCADE";
    public static final String SYSTEMS = "SYSTEMS(MESS)";
    public static final String LANGUAGES = "languages";
    public static final String RASTER = "RASTER";
    public static final String VECTOR = "VECTOR";
    public static final String LCD = "LCD";
    public static final String UNKNOWN = "UNKNOWN";

    // TODO fixme bad design!!!
    static final String ALL_LANGUAGES = "All Languages";
    static final String BIOS = "BIOS";
    static final String IMPERFECT = "IMPERFECT";
    static final String CHD = "CHD";
    static final String ALL_DISPLAY_TYPES = "All Displays";
    static final String CATEGORIES = "CATEGORIES";
    static final String CATEGORY_LISTS_HASHMAP = "Category Lists HashMap";
    static final String ARCADE_CATEGORY_ROOTS = "Arcade Roots";
    static final String SYSTEM_CATEGORY_ROOTS = "System Roots";
    static final String MATURE_CATEGORY_ROOTS = "Mature Roots";
    static final String ALL_CATEGORY_ROOTS = "All Roots";
    static final String ALL_MATURE_CATEGORIES = "All Mature Categories";
    static final String ARCADE_CATEGORIES = "Arcade Categories";
    static final String SYSTEM_CATEGORIES = "System Categories";
    static final String ARCADE_NOMATURE_CATEGORIES = "Arcade No Mature Categories";
    static final String SYSTEM_NOMATURE_CATEGORIES = "System No Mature Categories";
    private static final MFMPlayLists playLists;
    static Map<String, Machine> allMachines;
    static TreeSet<String> runnableList;
    static ArrayList<String> arcadeCategories;
    static ArrayList<String> systemCategories;
    static TreeSet<String> allList = new TreeSet<String>();
    static TreeSet<String> biosList = new TreeSet<String>();
    static TreeSet<String> devicesList = new TreeSet<String>();
    static TreeSet<String> noClonesList = new TreeSet<String>();
    static TreeSet<String> noImpefectList = new TreeSet<String>();
    static TreeSet<String> VIDsList = new TreeSet<String>();
    static TreeSet<String> clonesList = new TreeSet<String>();
    static TreeSet<String> verticalsList = new TreeSet<String>();
    static TreeSet<String> horizontalList = new TreeSet<String>();
    static TreeSet<String> cocktailList = new TreeSet<String>();
    static TreeSet<String> simultaneousList = new TreeSet<String>();
    static TreeSet<String> arcadeList = new TreeSet<String>();
    static TreeSet<String> systemList = new TreeSet<String>();
    static TreeSet<String> CHDList = new TreeSet<String>();
    static TreeSet<String> rasterDisplayList = new TreeSet<String>();
    static TreeSet<String> vectorDisplayList = new TreeSet<String>();
    static TreeSet<String> lcdDisplayList = new TreeSet<String>();
    static TreeSet<String> categoriesWithMachineList = new TreeSet<String>();
    static TreeMap<String, TreeSet<String>> languagesListsMap;
    private static TreeSet<String> allCategoriesList;
    private static TreeMap<String, ArrayList<String>> categoryHierarchy;
    private static ArrayList<String> allCategoryRoots;
    private static ArrayList<String> arcadeCategoryRoots;
    private static ArrayList<String> systemCategoryRoots;
    private static ArrayList<String> matureCategoryRoots;
    private static ArrayList<String> noMatureCategoryRoots;
    private static ArrayList<String> arcadeNoMatureCategoryRoots;
    private static ArrayList<String> systemNoMatureCategoryRoots;
    private static ArrayList<String> allMatureCategories;
    private static ArrayList<String> noMatureCategories;
    private static ArrayList<String> arcadeNoMatureCategories;
    private static ArrayList<String> systemNoMatureCategories;
    private static HashMap<String, TreeSet<String>> lists;

    static {
        try {
            allMachines = MAMEInfo.getMame().getMachineMap();
            runnableList = new TreeSet<String>(MAMEInfo.getRunnableMachines());
            allCategoriesList = MAMEInfo.getAllCategories();
            categoryHierarchy = MAMEInfo.getCategoryHierarchy();
        } catch (Exception e) {
            e.printStackTrace();
            MFM.logger.out("FATAL error is MFMListBuilder. Check your Data Set");
            // TODO ?? Consolodate these exit calls
            System.exit(5);
        }

        getCategoryLists();
        // Populate built in lists
        lists = MFMListGenerator.getInstance().generateMFMLists();
        languagesListsMap = MFMListGenerator.getInstance().getLanguageLists();

        playLists = MFMPlayLists.getInstance();
        if (MFM.isDebug()) {
            if (MAMEInfo.getCategoryHierarchy() != null) {
                MFM.logger.addToList("Main categories has : " + MAMEInfo.getCategoryHierarchy().size() + " entries");
            }
        }
        if (MFM.isSystemDebug()) {
            System.out.println("End STATIC MFMListBuilder");
        }
    }

    static TreeSet<String> getAllList() {
        return lists.get(ALL);
    }

    static TreeSet<String> getBiosList() {
        return lists.get(BIOS);
    }

    static TreeSet<String> getDevicesList() {
        return lists.get(DEVICES);
    }

    static TreeSet<String> getRunnableList() {
        return lists.get(RUNNABLE);
    }

    static TreeSet<String> getNoImperfectList() {
        return lists.get(NO_IMPERFECT);
    }

    static TreeSet<String> getNoClonesList() {
        return lists.get(NO_CLONE);
    }

    static TreeSet<String> getVIDsList() {
        return lists.get(PD_VIDS);
    }

    static TreeSet<String> getClonesList() {
        return lists.get(CLONE);
    }

    public static TreeSet<String> getVerticalsList() {
        return lists.get(VERTICAL);
    }

    public static TreeSet<String> getHorizontalsList() {
        return lists.get(HORIZONTAL);
    }

    public static TreeSet<String> getCocktailsList() {
        return lists.get(COCKTAIL);
    }

    public static TreeSet<String> getSimultaneousList() {
        return lists.get(SIMULTANEOUS);
    }

    public static TreeSet<String> getSystemList() {
        return lists.get(SYSTEMS);
    }

    public static TreeSet<String> getArcadeList() {
        return lists.get(ARCADE);
    }

    public static TreeSet<String> getRasterDisplayList() {
        return lists.get(RASTER);
    }

    public static TreeSet<String> getVectorDisplayList() {
        return lists.get(VECTOR);
    }

    public static TreeSet<String> getCHDList() {
        return lists.get(CHD);
    }

    public static TreeSet<String> getLcdDisplayList() {
        return lists.get(LCD);
    }

    public static TreeSet<String> getCategoriesWithMachineList() {
        return lists.get(CATEGORIES);
    }

    public static ArrayList<String> getControllersList() {
        return Controllers.getControllersList();
    }

    public static TreeMap<String, TreeSet<String>> getLanguagesListsMap() {
        return languagesListsMap;
    }

    public static ArrayList<String> getAllNoMatureCategoryRoots() {
        return noMatureCategoryRoots;
    }

    public static ArrayList<String> getCategoriesList(
            boolean allCategories, boolean arcadeOnly, boolean systemOnly, boolean noMature) {
        // First Roots only
        if (!allCategories) {
            if (arcadeOnly) {
                if (noMature) {
                    return arcadeNoMatureCategoryRoots;
                } else {
                    return arcadeCategoryRoots;
                }
            } else if (systemOnly) {
                if (noMature) {
                    return systemNoMatureCategoryRoots;
                } else {
                    return systemCategoryRoots;
                }
            }
            // It is ALL Roots
            else {
                if (noMature) {
                    return noMatureCategoryRoots;
                } else {
                    return allCategoryRoots;
                }
            }
        }
        // It is categories
        else {
            if (arcadeOnly) {
                if (noMature) {
                    return arcadeNoMatureCategories;
                } else {
                    return arcadeCategories;
                }
            } else if (systemOnly) {
                if (noMature) {
                    return systemNoMatureCategories;
                } else {
                    return systemCategories;
                }
            } else {
                if (noMature) {
                    return noMatureCategories;
                } else {
                    return new ArrayList<String>(allCategoriesList);
                }
            }
        }
    }

    // TODO move this to MAMEInfo ???
    private static void getCategoryLists() {
        HashMap<String, ArrayList<String>> categoryListsMap =
                (HashMap<String, ArrayList<String>>) MFM_Data.getInstance().getStaticData(CATEGORY_LISTS_HASHMAP);
        if (categoryListsMap == null) {
            MFM.logger.addToList("MFMListBuilder found no cached categories", true);
            try {
                categoryListsMap = (HashMap<String, ArrayList<String>>)
                        PersistUtils.loadAnObjectXML(MFM.MFM_CATEGORY_DIR + MFM.MFM_CATEGORY_DATA_FILE);
                MFM_Data.getInstance().setStaticData(CATEGORY_LISTS_HASHMAP, categoryListsMap);
                // TODO fixme also in MAMEInfo need a better way/place
                // Write out static data set
                //MFM_Data.getInstance().persistStaticData();
            } catch (FileNotFoundException e) {
                MFM.logger.addToList("MFMListBuilder FAILED to load categoryRootsMap from file", true);
                e.printStackTrace();
                return;
            }
        }
        allCategoryRoots = categoryListsMap.get(ALL_CATEGORY_ROOTS);
        arcadeCategoryRoots = categoryListsMap.get(ARCADE_CATEGORY_ROOTS);
        matureCategoryRoots = categoryListsMap.get(MATURE_CATEGORY_ROOTS);
        systemCategoryRoots = categoryListsMap.get(SYSTEM_CATEGORY_ROOTS);
        allMatureCategories = categoryListsMap.get(ALL_MATURE_CATEGORIES);
        systemCategories = categoryListsMap.get(SYSTEM_CATEGORIES);
        systemNoMatureCategories = categoryListsMap.get(SYSTEM_NOMATURE_CATEGORIES);
        arcadeCategories = categoryListsMap.get(ARCADE_CATEGORIES);
        arcadeNoMatureCategories = categoryListsMap.get(ARCADE_NOMATURE_CATEGORIES);

        noMatureCategoryRoots = new ArrayList<String>(allCategoryRoots);
        noMatureCategoryRoots.removeAll(matureCategoryRoots);

        arcadeNoMatureCategoryRoots = new ArrayList<String>(arcadeCategoryRoots);
        arcadeNoMatureCategoryRoots.removeAll(matureCategoryRoots);

        systemNoMatureCategoryRoots = new ArrayList<String>(systemCategoryRoots);
        systemNoMatureCategoryRoots.removeAll(matureCategoryRoots);

        noMatureCategories = new ArrayList<String>(allCategoriesList);
        noMatureCategories.removeAll(allMatureCategories);

        if (MFM.isSystemDebug()) {
            MFM.logger.addToList("Finished in MFMListBuilder.getCategoryLists()");
        }

    }

    public static String importList(Container container) {
        JFileChooser fileChooser = new JFileChooser(MFM.MFM_LISTS_DIR);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(null, JFileChooser.APPROVE_SELECTION);

        File file = fileChooser.getSelectedFile();

        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = stripSuffix(file.getName());
        String[] machines = lines.toArray(new String[lines.size()]);
        createPlayList(fileName, machines, container);

        return fileName;
    }

    public static void createPlayList(String name, String[] list, Container container) {
        boolean ok = MFMListBuilder.checkListName(name, container);
        if (!ok) {
            return;
        }
        MFMPlayLists.getInstance().createPlayList(name, checkList(list));
        MFMUI_Setup.updateMenuBar(name);
    }

    static TreeSet<String> checkList(String[] list) {
        TreeSet<String> newList = new TreeSet<String>();
        for (String machine : list) {
            if (lists.get(ALL).contains(machine)) {
                newList.add(machine);
            }
        }
        return newList;
    }

    private static boolean checkListName(String name, Component comp) {

        if (playLists.getALLPlayListsTree().containsKey(name)) {
            if (playLists.getMyPlayListsTree().containsKey(name)) {
                int result = JOptionPane.showConfirmDialog(comp,
                        "That list name already exists. Overwrite it?", "", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(comp, "Please rename that is a reserved list name.");
                return false;
            }
        }
        return true;
    }

    private static String stripSuffix(String fileName) {
        // Strip suffix
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    public static void diffLists(Frame frame) {
        JFileChooser fileChooser = new JFileChooser(MFM.MFM_LISTS_DIR);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showDialog(null, JFileChooser.APPROVE_SELECTION);

        File[] files = fileChooser.getSelectedFiles();

        if (files.length < 2) {
            JOptionPane.showMessageDialog(null, "You must select two files.");
            return;
        }

        if (files.length > 2) {
            JOptionPane.showMessageDialog(null, "Diff will be the first two files returned");
        }


        String name1 = stripSuffix(files[0].getName());
        String name2 = stripSuffix(files[1].getName());

        List<String> lines1 = null;
        List<String> lines2 = null;
        try {
            lines1 = Files.readAllLines(files[0].toPath(), Charset.defaultCharset());
            lines2 = Files.readAllLines(files[1].toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        TreeSet<String> list1 = new TreeSet<String>(lines1);
        TreeSet<String> list2 = new TreeSet<String>(lines2);
        TreeSet<String> commonList = new TreeSet<String>(list1);
        commonList.retainAll(list2);
        list1.removeAll(commonList);
        list2.removeAll(commonList);

        StringBuilder output = new StringBuilder();
        output.append("Machines unique to each list are:\n\n");
        output.append(name1);
        output.append(" : \n");

        for (String aList1 : list1) {
            output.append(aList1);
            output.append("\n");
        }
        output.deleteCharAt(output.length() - 2);
        output.append("\n\n");

        output.append(name2);
        output.append(" : \n");
        for (String aList2 : list2) {
            output.append(aList2);
            output.append("\n");
        }
        // Remove the final newline NOTE very Anal!! ;)
        // bug ID#34 FIXED
        output.deleteCharAt(output.length() - 1);

        JTextArea txt = new JTextArea(output.toString());
        txt.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(txt);
        JDialog dialog = new JDialog(frame, "Lists Difference");
        dialog.getContentPane().add(scrollPane);
        dialog.setMinimumSize(new Dimension(300, 200));
        dialog.setPreferredSize(new Dimension(400, (int) MFMUI.screenSize.getHeight() / 2));
        dialog.setMaximumSize(new Dimension(400, (int) MFMUI.screenSize.getHeight() / 2));

        dialog.setLocation(MFMUI.screenSize.width / 2 - 150, MFMUI.screenSize.height / 2 - 100);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static Object[] getRunnableArray() {
        return getRunnableList().toArray();
    }

    public static void dumpListData(String list) {
        File newFile = new File(MFM.MFM_LISTS_DIR + list +
                        MFM_Data.getInstance().getDataVersion() + "_data.csv");
        TreeSet<String> machines = MFMPlayLists.getInstance().getPlayList(list);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(newFile);
            pw.println(Machine.CSV_HEADER);
            for (String machine : machines) {
                pw.println(MAMEInfo.getMachine(machine).toString());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final class Builder {

        public static final QuadState orientation = new QuadState(VERTICAL, HORIZONTAL, COCKTAIL, QuadState.ALL);
        public static final QuadState displayType = new QuadState(RASTER, VECTOR, LCD, ALL_DISPLAY_TYPES);
        public static final TriState MAME = new TriState(ARCADE, SYSTEMS, TriState.BOTH);
        private static final Builder builder = new Builder();
        private static final String ALL = "All";
        private static final int million = 1000000;
        private static final String DIAL = "dial";
        private static final String DOUBLEJOY = "doublejoy";
        private static final String GAMBLING = "gambling";
        private static final String HANAFUDA = "hanafuda";
        private static final String JOY = "joy";
        private static final String KEYBOARD = "keyboard";
        private static final String KEYPAD = "keypad";
        private static final String LIGHTGUN = "lightgun";
        private static final String MAHJONG = "mahjong";
        private static final String MOUSE = "mouse";
        private static final String PADDLE = "paddle";
        private static final String PEDAL = "pedal";
        // positional ????
        private static final String STICK = "stick";
        private static final String TRACKBALL = "trackball";
        private static final String TRIPLEJOY = "triplejoy";
        private static final TreeSet<String> pedalControls = new TreeSet<String>(Arrays.asList(new String[]{
                DIAL, DOUBLEJOY, GAMBLING, HANAFUDA, JOY, KEYBOARD, KEYPAD, LIGHTGUN, MAHJONG, MOUSE,
                PADDLE, STICK, TRACKBALL, TRIPLEJOY
        }));
        private static final TreeSet<String> paddleControls = new TreeSet<String>(Arrays.asList(new String[]{
                DIAL
        }));
        private static final TreeSet<String> dialControls = new TreeSet<String>(Arrays.asList(new String[]{
                PADDLE
        }));
        private static final TreeSet<String> hanafudaControls = new TreeSet<String>(Arrays.asList(new String[]{
                KEYBOARD
        }));
        private static final TreeSet<String> keypadControls = new TreeSet<String>(Arrays.asList(new String[]{
                KEYBOARD
        }));
        private static final TreeSet<String> lightgunControls = new TreeSet<String>(Arrays.asList(new String[]{
                MOUSE
        }));
        private static final TreeSet<String> mouseControls = new TreeSet<String>(Arrays.asList(new String[]{
                TRACKBALL
        }));
        private static final TreeSet<String> trackballControls = new TreeSet<String>(Arrays.asList(new String[]{
                MOUSE
        }));
        private static final int JOY1 = 3268317;
        private static final int JOY2 = 3268318;
        private static final int JOY3_half4 = -1238305841;
        private static final int JOY5_half8 = -558247347;
        private static final int JOY4 = 3268320;
        private static final int JOYVERTICAL2 = -482411992;
        private static final int JOY8 = 3268324;
        private static final int DOUBLEJOY2_2 = -1607713821;
        private static final int DOUBLEJOY4_2 = -1607713759;
        private static final int DOUBLEJOY4_4 = -1607713757;
        private static final int DOUBLEJOY8_2 = -1607713635;
        private static final int DOUBLEJOY8_8 = -1607713629;
        private static final int DOUBLEJOY5_half8 = 1993708739;
        private static final int DOUBLEJOYV2_V2 = -410698973;
        //  3268318
        private static final TreeSet<Integer> joy2 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317
        }));
        //  -1238305841
        private static final TreeSet<Integer> joy3_half4 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317
        }));
        //  -558247347
        private static final TreeSet<Integer> joy5_half8 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317
        }));
        //  3268320
        private static final TreeSet<Integer> joy4 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317, 3268318, -1238305841, -558247347, -482411992
        }));
        //  3268324
        private static final TreeSet<Integer> joy8 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317, 3268318, 3268320, -1238305841, -558247347, -482411992
        }));
        //  -482411992
        private static final TreeSet<Integer> joyvertical2 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                3268317
        }));
        //  -1607713629
        private static final TreeSet<Integer> doublejoy8_8 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                -1607713821, -1607713759, -410698973, 1993708739, -1607713757
        }));
        //  -1607713759
        private static final TreeSet<Integer> doublejoy4_2 = new TreeSet<Integer>(Arrays.asList(new Integer[]{

        }));
        //  -1607713757
        private static final TreeSet<Integer> doublejoy4_4 = new TreeSet<Integer>(Arrays.asList(new Integer[]{
                -1607713821, -1607713759, -410698973, 1993708739
        }));
        //  -410698973
        private static final TreeSet<Integer> doublejoyvertical2_vertical2 = new TreeSet<Integer>(Arrays.asList(new Integer[]{

        }));
        //  1993708739
        private static final TreeSet<Integer> doublejoy5_half8 = new TreeSet<Integer>(Arrays.asList(new Integer[]{

        }));
        public static int buttons = 0;
        public static boolean orLessButtons = true;
        public static int players = 1;
        public static boolean orLessPlayers = true;
        public static boolean noClones = false;
        public static boolean simultaneousOnly = false;
        static boolean noMature = true;
        static boolean noImperfect = false;
        static boolean waysSelected = false;
        static boolean exactMatch = false;
        static String language = null;
        private static String ways = ALL;
        private static String ways2 = ALL;
        String machineWays = null;
        String machineWays2 = null;
        String machineWays2A = null;
        String machineWays2B = null;
        private int joySignature;
        private int doublejoySignature;
        //private static final String DIAL = "dial";


        //============================================================================================
        //                              Controls Compatibility
        private TreeSet<String> categories;
        private TreeSet<String> controls;

        private Builder() {
        }

        public static Builder getInstance() {
            return builder;
        }

        // TODO convert ListBuilderUI to use these
        public static int getPlayers() {
            return players;
        }

        public static void setPlayers(int players) {
            Builder.players = players;
        }

        public static int getButtons() {
            return buttons;
        }

        public static void setButtons(String buttons) {
            // specials case where 9 means 9 or more
            if (buttons.equals("9+")) {
                // Arbitrary ridiculously high value
                Builder.buttons = million;
                return;
            }
            Builder.buttons = Integer.parseInt(buttons);
        }

        //============================================================================================
        //                              Control Signature Compatibility

        /*
                    -1607713821	doublejoy	2	2
                    -1607713759	doublejoy	4	2
                    -1607713757	doublejoy	4	4
                    -1607713635	doublejoy	8	2
                    -1607713629	doublejoy	8	8
                    -1238305841	joy	3 (half4)
                    -558247347	joy	5 (half8)
                    -482411992	joy	vertical2
                    -410698973	doublejoy	vertical2	vertical2
                    3268317	joy	1
                    3268318	joy	2
                    3268320	joy	4
                    3268324	joy	8
                    1993708739	doublejoy	5 (half8)	5 (half8)
         */

        public static boolean isNoMature() {
            return noMature;
        }

        public static void setNoMature(boolean noMature) {
            Builder.noMature = noMature;
        }

        public static boolean isOrLessButtons() {
            return orLessButtons;
        }

        public static void setOrLessButtons(boolean orLessButtons) {
            Builder.orLessButtons = orLessButtons;
        }

        public static boolean isOrLessPlayers() {
            return orLessPlayers;
        }

        public static void setOrLessPlayers(boolean orLessPlayers) {
            Builder.orLessPlayers = orLessPlayers;
        }

        public static boolean isNoClones() {
            return noClones;
        }

        public static void setNoClones(boolean noClones) {
            Builder.noClones = noClones;
        }

        public static boolean isNoImperfect() {
            return noImperfect;
        }

        public static void setNoImperfect(boolean noImperfect) {
            Builder.noImperfect = noImperfect;
        }

        public static boolean isSimultaneousOnly() {
            return simultaneousOnly;
        }

        public static void setSimultaneousOnly(boolean simultaneousOnly) {
            Builder.simultaneousOnly = simultaneousOnly;
        }

        public static void setWays(String ways) {
            Builder.ways = ways;
            if (!waysSelected && !ways.equals(ALL)) {
                waysSelected = true;
            } else if (waysSelected && ways.equals(ALL) && ways2.equals(ALL)) {
                waysSelected = false;
            }
        }

        public static void setWays2(String ways2) {
            Builder.ways2 = ways2;
            if (!waysSelected && !ways2.equals(ALL)) {
                waysSelected = true;
            } else if (waysSelected && ways.equals(ALL) && ways2.equals(ALL)) {
                waysSelected = false;
            }
        }

        public static void setExactMatch(boolean exactMatchIn) {
            exactMatch = exactMatchIn;
        }

        public static void setLanguage(String languageIn) {
            language = languageIn;
        }

        public final TreeSet<String> generateList() {
            // fixme probably should just use single list
            TreeSet<String> baseList;
            TreeSet<String> finalList = new TreeSet<String>();
            calculateWays(); // generates the Control signature from selected values
            /**
             * Runnable only!!
             * Start with base lists: Both or Arcade or Systems
             *  Then filter by orientation: (ALL), Vertical, Horizontal, Cocktail
             *  Then filter by  displayType: (ALL), Raster, Vector, LCD
             *
             */
            baseList = new TreeSet<String>(runnableList);
            if (MAME.getState().equals(ARCADE)) {
                baseList.retainAll(getArcadeList());
            } else if (MAME.getState().equals(SYSTEMS)) {
                // Must be Systems
                baseList.retainAll(getSystemList());
            }

            // Filter by orientation
            switch (orientation.getState()) {

                case QuadState.ALL:
                    // nothing drop through
                    break;

                case VERTICAL:
                    baseList.retainAll(getVerticalsList());
                    break;

                case HORIZONTAL:
                    baseList.retainAll(getHorizontalsList());
                    break;

                case COCKTAIL:
                    baseList.retainAll(getCocktailsList());
                    break;
            }

            // Filter by displayType
            switch (displayType.getState()) {

                case QuadState.ALL:
                    // nothing drop through
                    break;

                case RASTER:
                    baseList.retainAll(getRasterDisplayList());
                    break;

                case VECTOR:
                    baseList.retainAll(getVectorDisplayList());
                    break;

                case LCD:
                    baseList.retainAll(getLcdDisplayList());
                    break;
            }

            if (simultaneousOnly) {
                baseList.retainAll(getSimultaneousList());
            }

            if (noClones) {
                baseList.retainAll(getNoClonesList());
            }

            if (noImperfect) {
                baseList.retainAll(getNoImperfectList());
            }

            if (!language.equals(ALL_LANGUAGES)) {
                baseList.retainAll(languagesListsMap.get(language));
            }

            // performance short circuit
            if (baseList.isEmpty()) {
                return baseList;
            }

            // If any Categories are selected or No Mature checked (All categories minus Mature)
            if (!categories.isEmpty() || noMature) {
                if (categories.isEmpty()) {
                    categories.addAll(allCategoriesList);
                } else {
                    // Have to account for roots
                    expandRoots();
                }

                if (noMature) {
                    categories.removeAll(allMatureCategories);
                }

                TreeSet<String> tempList = new TreeSet<String>();
                for (String machineName : baseList) {
                    Machine machine = allMachines.get(machineName);
                    if (categories.contains(machine.getCategory())) {
                        tempList.add(machineName);
                    }
                }
                baseList.retainAll(tempList);
            }

            //    TreeSet<String> selectedControls = new TreeSet<String>(this.controls);
            TreeSet<String> expandedControls = expandControls(this.controls);
            if (MFM.isSystemDebug()) {
                System.out.println("Expanded Controls are : " + expandedControls);
            }

            for (String machineName : baseList) {
                Machine machine = allMachines.get(machineName);
                if (checkControls(machine, expandedControls)
                        && checkButtonsPlayers(machine.getButtons(),
                        Integer.parseInt(machine.getInput().getPlayers()))) {
                    finalList.add(machineName);
                }
            }

            controls = new TreeSet<String>();
            categories = null;
            return finalList;
        }

        private boolean checkControls(Machine machine, TreeSet<String> expandedControls) {
            // If No Controls or ways are selected ignore them
            if (controls.isEmpty() && !waysSelected) {
                return true;
            }
            List<String> machineControls = machine.getInput().getControl().stream().map(
                    Control::getType).collect(Collectors.toList());
            machineControls.retainAll(expandedControls);
            // TODO test does this resolve buttons??
            if (machineControls.isEmpty()) {
                return false;
            }

            if (waysSelected) {
                if (ways.equalsIgnoreCase(ALL) && ways2.equalsIgnoreCase(ALL)) {
                    return true;
                }

                if (exactMatch) {
                    if (controls.contains(Controllers.JOY)) {
                        if (Controllers.getInstance().signatureHasMachine(joySignature, machine.getName())) {
                            return true;
                        }
                    }

                    if (controls.contains(Controllers.DOUBLEJOY)) {
                        if (Controllers.getInstance().signatureHasMachine(doublejoySignature, machine.getName())) {
                            return true;
                        }
                    }
                } else {
                    if (controls.contains(Controllers.JOY)) {
                        if (Controllers.getInstance().signaturesHasMachine(
                                expandJoys(joySignature), machine.getName())) {
                            return true;
                        }
                    }

                    if (controls.contains(Controllers.DOUBLEJOY)) {
                        if (Controllers.getInstance().signaturesHasMachine(
                                expandJoys(doublejoySignature), machine.getName())) {
                            return true;
                        }
                    }
                }

                return false;
            }
            return true;
        }

        // TODO change for Gauntlet logic Done needs testing
        // If simultaneous is selected ignore Players settings
        private boolean checkButtonsPlayers(int machineButtons, int machinePlayers) {

            /** NOTE agreement with Obiwantje for Games where the number of Players for Simultaneous
             *  is different than the number of Players. So we just ignore number of Players
             */
            if (simultaneousOnly) {
                return machineButtons == buttons || (orLessButtons && machineButtons <= buttons);
            }

            if (machineButtons == buttons && machinePlayers == players) {
                return true;
            } else if (orLessButtons && orLessPlayers && machineButtons <= buttons && machinePlayers <= players) {
                return true;
            } else if (orLessButtons && machineButtons <= buttons && machinePlayers == players) {
                return true;
            } else if (orLessPlayers && machinePlayers <= players &&
                    (machineButtons == buttons || (buttons == million && machineButtons >= 9))) {
                return true;
            }
            return false;
        }

        private void calculateWays() {
            machineWays = ways.equalsIgnoreCase(ALL) ? "" : ways;
            joySignature = Controllers.getInstance().getSignature(new ArrayList<String>(
                    Arrays.asList(new String[]{Controllers.JOY, machineWays})));

            machineWays2 = ways2.equalsIgnoreCase(ALL) ? "" : ways2;
            machineWays2A = null;
            machineWays2B = null;
            if (machineWays2.contains("/")) {
                machineWays2A = machineWays2.substring(0, machineWays2.indexOf("/"));
                machineWays2B = machineWays2.substring(machineWays2.indexOf("/") + 1, machineWays2.length());
            } else {
                machineWays2A = machineWays2B = machineWays2;
            }

            doublejoySignature = Controllers.getInstance().getSignature(new ArrayList<String>(
                    Arrays.asList(new String[]{Controllers.DOUBLEJOY, machineWays2A, machineWays2B})));
        }

        public void setCategories(TreeSet<String> categories) {
            this.categories = categories;
        }

        public void setControls(TreeSet<String> controls) {
            this.controls = controls;
        }

        // Take selected Categories and expand roots
        private void expandRoots() {
            TreeSet<String> set = new TreeSet<>();
            for (String category : categories) {
                if (allCategoryRoots.contains(category)) {
                    List children = categoryHierarchy.get(category);
                    if (children != null) {
                        set.addAll(children);
                    } else {
                        MFM.logger.addToList("Category: " + category + " is a root but returned no children");
                    }
                }
            }
            categories.addAll(set);
        }

        private TreeSet<Integer> expandJoys(int joySignature) {
            TreeSet<Integer> expandedJoys = new TreeSet<Integer>();
            expandedJoys.add(joySignature);
            switch (joySignature) {

                case JOY8:
                    expandedJoys.addAll(joy8);
                    break;

                case JOY2:
                    expandedJoys.addAll(joy2);
                    break;

                case JOY3_half4:
                    expandedJoys.addAll(joy3_half4);
                    break;

                case JOY4:
                    expandedJoys.addAll(joy4);
                    break;

                case JOY5_half8:
                    expandedJoys.addAll(joy5_half8);
                    break;

                case JOYVERTICAL2:
                    expandedJoys.addAll(joyvertical2);
                    break;

                case DOUBLEJOY8_8:
                    expandedJoys.addAll(doublejoy8_8);
                    break;

                case DOUBLEJOY4_2:
                    expandedJoys.addAll(doublejoy4_2);
                    break;

                case DOUBLEJOY4_4:
                    expandedJoys.addAll(doublejoy4_4);
                    break;

                case DOUBLEJOYV2_V2:
                    expandedJoys.addAll(doublejoyvertical2_vertical2);
                    break;

                case DOUBLEJOY5_half8:
                    expandedJoys.addAll(doublejoy5_half8);
                    break;
            }
            return expandedJoys;
        }

        private TreeSet<String> expandControls(TreeSet<String> controls) {
            TreeSet<String> expandedControls = new TreeSet<String>(controls);
            for (String control : controls) {

                switch (control) {

                    case DIAL:
                        expandedControls.addAll(dialControls);
                        break;

                    case HANAFUDA:
                        expandedControls.addAll(hanafudaControls);
                        break;

                    case KEYPAD:
                        expandedControls.addAll(keypadControls);
                        break;

                    case LIGHTGUN:
                        expandedControls.addAll(lightgunControls);
                        break;

                    case MOUSE:
                        expandedControls.addAll(mouseControls);
                        break;

                    case PADDLE:
                        expandedControls.addAll(paddleControls);
                        break;

                    case PEDAL:
                        expandedControls.addAll(pedalControls);
                        // Special case since we have added all return
                        return expandedControls;

                    case TRACKBALL:
                        expandedControls.addAll(trackballControls);
                        break;
                }

            }
            return expandedControls;
        }
    }
}
