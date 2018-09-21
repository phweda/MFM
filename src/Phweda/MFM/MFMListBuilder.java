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

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/2014
 * Time: 2:47 PM
 */

import Phweda.MFM.UI.MFMUI_Setup;
import Phweda.MFM.datafile.Datafile;
import Phweda.MFM.mame.Control;
import Phweda.MFM.mame.Machine;
import Phweda.utils.FileUtils;
import Phweda.utils.QuadState;
import Phweda.utils.TriState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Adding more built in lists i.e. noclones and PD VIDs
 * This provides the service of building those lists
 * Intent to later add wildcard builder capabilities i.e. (b*, noclone)
 * would build a list of all playable noclone games starting with b
 */
@SuppressWarnings({"WeakerAccess", "PackageVisibleField"})
public final class MFMListBuilder {
    public static final String ALL = "ALL";
    public static final String RUNNABLE = "RUNNABLE";
    public static final String NO_IMPERFECT = "NOIMPERFECT";
    public static final String MECHANICAL = "MECHANICAL";
    public static final String BIOS = "BIOS";
    static final String IMPERFECT = "IMPERFECT";
    static final String CHD = "CHD";
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
    public static final String ALL_CONTROLS = "All Controls";
    public static final String ANY_CONTROLS = "Any Controls";
    public static final String EXACT_CONTROLS = "Exact Controls";

    static final String ALL_LANGUAGES = "All Languages";
    static final String ALL_SOFTWARE_LISTS = "All Software Lists";
    static final String ALL_YEARS = "All Years";
    static final String ALL_DISPLAY_TYPES = "All Displays";
    static final String CATEGORIES = "CATEGORIES";
    static final String CATEGORY_LISTS_HASHMAP = "Category Lists HashMap";
    public static final String ARCADE_ROOTS = "Arcade Roots";
    public static final String SYSTEM_ROOTS = "System Roots";
    public static final String MATURE_ROOTS = "Mature Roots";
    public static final String ALL_ROOTS = "All Roots";
    public static final String ALL_MATURE_CATEGORIES = "All Mature Categories";
    public static final String ARCADE_CATEGORIES = "Arcade Categories";
    public static final String SYSTEM_CATEGORIES = "System Categories";
    public static final String ARCADE_NOMATURE_CATEGORIES = "Arcade No Mature Categories";
    public static final String SYSTEM_NOMATURE_CATEGORIES = "System No Mature Categories";
    private static MFMPlayLists playLists;
    static Map<String, Machine> allMachines;
    static TreeSet<String> runnableList;
    static ArrayList<String> arcadeCategories;
    static ArrayList<String> systemCategories;
    //======= For use by MFMListGenerator when Parsing MAME ===================
    static TreeSet<String> allList = new TreeSet<>();
    static TreeSet<String> biosList = new TreeSet<>();
    static TreeSet<String> devicesList = new TreeSet<>();
    static TreeSet<String> noClonesList = new TreeSet<>();
    static TreeSet<String> noImpefectList = new TreeSet<>();
    static TreeSet<String> mechanicalList = new TreeSet<>();
    static TreeSet<String> clonesList = new TreeSet<>();
    static TreeSet<String> verticalsList = new TreeSet<>();
    static TreeSet<String> horizontalList = new TreeSet<>();
    static TreeSet<String> cocktailList = new TreeSet<>();
    static TreeSet<String> simultaneousList = new TreeSet<>();
    static TreeSet<String> arcadeList = new TreeSet<>();
    static TreeSet<String> systemList = new TreeSet<>();
    static TreeSet<String> chdList = new TreeSet<>();
    static TreeSet<String> rasterDisplayList = new TreeSet<>();
    static TreeSet<String> vectorDisplayList = new TreeSet<>();
    static TreeSet<String> lcdDisplayList = new TreeSet<>();
    //=====================================================================
    static TreeSet<String> categoriesWithMachineList = new TreeSet<>();
    static TreeMap<String, SortedSet<String>> languagesListsMap;
    private static TreeSet<String> allCategoriesList;
    private static TreeMap<String, ArrayList<String>> categoryHierarchy;
    private static ArrayList<String> allCategoryRoots;
    private static ArrayList<String> arcadeCategoryRoots;
    private static ArrayList<String> systemCategoryRoots;
    private static List<String> noMatureCategoryRoots;
    private static List<String> arcadeNoMatureCategoryRoots;
    private static List<String> systemNoMatureCategoryRoots;
    private static List<String> allMatureCategories;
    private static List<String> noMatureCategories;
    private static List<String> arcadeNoMatureCategories;
    private static List<String> systemNoMatureCategories;
    private static HashMap<String, TreeSet<String>> lists;

    private MFMListBuilder() { // To cover implicit public constructor
    }

    static void initLists(boolean parsing) {
        try {
            allMachines = MAMEInfo.getMame().getMachineMap();
            allCategoriesList = MAMEInfo.getAllCategories();
            categoryHierarchy = MAMEInfo.getCategoryHierarchyMap();
            runnableList = new TreeSet<>(MAMEInfo.getRunnableMachines());
            getCategoryLists();

            // Populate built in lists
            lists = MFMListGenerator.generateMFMLists(parsing);
            languagesListsMap = MFMListGenerator.getLanguageLists(parsing);
            if (MFM.isSystemDebug()) {
                System.out.println("Mechanical list:" + FileUtils.NEWLINE + mechanicalList);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            MFM.getLogger().out("FATAL error in MFMListBuilder. Check your Data Set");
            MFM.exit(5);
        }

        if (playLists != null) {
            MFMPlayLists.refreshLists();
        }
        playLists = MFMPlayLists.getInstance();
        if (MFM.isDebug() && (MAMEInfo.getCategoryHierarchyMap() != null)) {
            MFM.getLogger().addToList("Main categories has : " + MAMEInfo.getCategoryHierarchyMap().size() + " entries");
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

    public static SortedSet<String> getRunnableList() {
        return lists.get(RUNNABLE);
    }

    static TreeSet<String> getNoImperfectList() {
        return lists.get(NO_IMPERFECT);
    }

    static TreeSet<String> getMechanicalList() {
        return lists.get(MECHANICAL);
    }

    static TreeSet<String> getNoClonesList() {
        return lists.get(NO_CLONE);
    }

    static TreeSet<String> getClonesList() {
        return lists.get(CLONE);
    }

    public static SortedSet<String> getVerticalsList() {
        return lists.get(VERTICAL);
    }

    public static SortedSet<String> getHorizontalsList() {
        return lists.get(HORIZONTAL);
    }

    public static SortedSet<String> getCocktailsList() {
        return lists.get(COCKTAIL);
    }

    public static SortedSet<String> getSimultaneousList() {
        return lists.get(SIMULTANEOUS);
    }

    public static SortedSet<String> getSystemList() {
        return lists.get(SYSTEMS);
    }

    public static SortedSet<String> getArcadeList() {
        return lists.get(ARCADE);
    }

    public static SortedSet<String> getRasterDisplayList() {
        return lists.get(RASTER);
    }

    public static SortedSet<String> getVectorDisplayList() {
        return lists.get(VECTOR);
    }

    public static SortedSet<String> getChdList() {
        return lists.get(CHD);
    }

    public static SortedSet<String> getLcdDisplayList() {
        return lists.get(LCD);
    }

    public static SortedSet<String> getCategoriesWithMachineList() {
        return lists.get(CATEGORIES);
    }

    public static SortedMap<String, SortedSet<String>> getLanguagesListsMap() {
        return languagesListsMap;
    }

    public static List<String> getAllNoMatureCategoryRoots() {
        return noMatureCategoryRoots;
    }

    public static List<String> getCategoriesList(boolean allCategories, boolean arcadeOnly,
                                                 boolean systemOnly, boolean noMature) {
        // First Roots only
        if (!allCategories) {
            if (arcadeOnly) {
                return noMature ? arcadeNoMatureCategoryRoots : arcadeCategoryRoots;
            } else if (systemOnly) {
                return noMature ? systemNoMatureCategoryRoots : systemCategoryRoots;
            }
            // It is ALL Roots
            else {
                return noMature ? noMatureCategoryRoots : allCategoryRoots;
            }
        }
        // It is categories
        else {
            if (arcadeOnly) {
                return noMature ? arcadeNoMatureCategories : arcadeCategories;
            } else if (systemOnly) {
                return noMature ? systemNoMatureCategories : systemCategories;
            } else {
                return noMature ? noMatureCategories : new ArrayList<>(allCategoriesList);
            }
        }
    }

    private static void getCategoryLists() {
        // NOTE moved input from file to MAMEInfo 4/18

        Map<String, ArrayList<String>> map = MAMEInfo.getCategoryListsMap();
        allCategoryRoots = map.get(ALL_ROOTS);
        arcadeCategoryRoots = map.get(ARCADE_ROOTS);
        List<String> matureCategoryRoots = map.get(MATURE_ROOTS);
        systemCategoryRoots = map.get(SYSTEM_ROOTS);
        allMatureCategories = map.get(ALL_MATURE_CATEGORIES);
        systemCategories = map.get(SYSTEM_CATEGORIES);
        systemNoMatureCategories = map.get(SYSTEM_NOMATURE_CATEGORIES);
        arcadeCategories = map.get(ARCADE_CATEGORIES);
        arcadeNoMatureCategories = map.get(ARCADE_NOMATURE_CATEGORIES);

        noMatureCategoryRoots = new ArrayList<>(allCategoryRoots);
        noMatureCategoryRoots.removeAll(matureCategoryRoots);

        arcadeNoMatureCategoryRoots = new ArrayList<>(arcadeCategoryRoots);
        arcadeNoMatureCategoryRoots.removeAll(matureCategoryRoots);

        systemNoMatureCategoryRoots = new ArrayList<>(systemCategoryRoots);
        systemNoMatureCategoryRoots.removeAll(matureCategoryRoots);

        // NOTE believe this is a bootstrap parse only condition
        if (allCategoriesList == null) {
            allCategoriesList = new TreeSet<>(arcadeCategories);
            allCategoriesList.addAll(systemCategories);
        }
        noMatureCategories = new ArrayList<>(allCategoriesList);
        noMatureCategories.removeAll(allMatureCategories);

        if (MFM.isSystemDebug()) {
            MFM.getLogger().addToList("Finished in MFMListBuilder.getCategoryLists()");
        }

    }

    public static void createPlayList(String name, String[] list) {
        MFMPlayLists.getInstance().createPlayList(name, checkList(list));
        MFMUI_Setup.getInstance().updateMenuBar(name);
    }

    private static TreeSet<String> checkList(String[] list) {
        TreeSet<String> newList = new TreeSet<>();
        for (String machine : list) {
            if (lists.get(ALL).contains(machine)) {
                newList.add(machine);
            }
        }
        return newList;
    }

    public static String[] getRunnableArray() {
        return getRunnableList().toArray(new String[0]);
    }

    public static void createListfromDAT(String listName, Datafile datafile) {
        TreeSet<String> list = MFMListGenerator.generateListfromDAT(datafile);
        MFMPlayLists.getInstance().createPlayList(listName, list);
    }

    @SuppressWarnings({"squid:S1845", "squid:S1068", "squid:S135", "OverlyComplexClass", "MismatchedQueryAndUpdateOfCollection", "AssignmentOrReturnOfFieldWithMutableType", "DuplicateStringLiteralInspection"})
    public static final class Builder {

        public static final QuadState orientation = new QuadState(VERTICAL, HORIZONTAL, COCKTAIL, QuadState.ALL);
        public static final QuadState displayType = new QuadState(RASTER, VECTOR, LCD, ALL_DISPLAY_TYPES);
        public static final TriState MAME = new TriState(ARCADE, SYSTEMS, TriState.BOTH);
        public static final TriState ControlsFilterType = new TriState(ANY_CONTROLS, ALL_CONTROLS, EXACT_CONTROLS);

        private static final Builder builder = new Builder();
        private static final int MILLION = 1000000;
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
        // positional ???? Never needed. see below for hash
        private static final String STICK = "stick";
        private static final String TRACKBALL = "trackball";
        private static final String TRIPLEJOY = "triplejoy";
        private static final Collection<String> pedalControls = new TreeSet<>(Arrays.asList(
                DIAL, DOUBLEJOY, GAMBLING, HANAFUDA, JOY, KEYBOARD, KEYPAD, LIGHTGUN, MAHJONG, MOUSE,
                PADDLE, STICK, TRACKBALL, TRIPLEJOY
        ));
        private static final Collection<String> paddleControls = new TreeSet<>(Collections.singletonList(DIAL));
        private static final Collection<String> dialControls = new TreeSet<>(Collections.singletonList(PADDLE));
        private static final Collection<String> hanafudaControls = new TreeSet<>(Collections.singletonList(KEYBOARD));
        private static final Collection<String> keypadControls = new TreeSet<>(Collections.singletonList(KEYBOARD));
        private static final Collection<String> lightgunControls = new TreeSet<>(Collections.singletonList(MOUSE));
        private static final Collection<String> mouseControls = new TreeSet<>(Collections.singletonList(TRACKBALL));
        private static final Collection<String> trackballControls = new TreeSet<>(Collections.singletonList(MOUSE));
        private static final int JOY1 = 3268317;
        private static final int JOY2 = 3268318;
        private static final int JOY_3_HALF_4 = -1238305841;
        private static final int JOY_5_HALF_8 = -558247347;
        private static final int JOY4 = 3268320;
        private static final int JOYVERTICAL2 = -482411992;
        private static final int JOY8 = 3268324;
        private static final int DOUBLEJOY2_2 = -1607713821;
        private static final int DOUBLEJOY4_2 = -1607713759;
        private static final int DOUBLEJOY4_4 = -1607713757;
        private static final int DOUBLEJOY8_2 = -1607713635;
        private static final int DOUBLEJOY8_8 = -1607713629;
        private static final int DOUBLEJOY_5_HALF_8 = 1993708739;
        private static final int DOUBLEJOYV2_V2 = -410698973;
        //  3268318
        private static final Collection<Integer> joy2 = new TreeSet<>(Collections.singletonList(3268317));
        //  -1238305841
        private static final Collection<Integer> joy3_half4 = new TreeSet<>(Collections.singletonList(3268317));
        //  -558247347
        private static final Collection<Integer> joy5_half8 = new TreeSet<>(Collections.singletonList(3268317));
        //  3268320
        private static final Collection<Integer> joy4 = new TreeSet<>(Arrays.asList(
                3268317, 3268318, -1238305841, -558247347, -482411992
        ));
        //  3268324
        private static final Collection<Integer> joy8 = new TreeSet<>(Arrays.asList(
                3268317, 3268318, 3268320, -1238305841, -558247347, -482411992
        ));
        //  -482411992
        private static final Collection<Integer> joyvertical2 = new TreeSet<>(Collections.singletonList(3268317));
        //  -1607713629
        private static final Collection<Integer> doublejoy8_8 = new TreeSet<>(Arrays.asList(
                -1607713821, -1607713759, -410698973, 1993708739, -1607713757
        ));
        //  -1607713759
        private static final Collection<Integer> doublejoy4_2 = new TreeSet<>();
        //  -1607713757
        private static final Collection<Integer> doublejoy4_4 = new TreeSet<>(Arrays.asList(
                -1607713821, -1607713759, -410698973, 1993708739
        ));
        //  -410698973
        private static final Collection<Integer> doublejoyvertical2_vertical2 = new TreeSet<>();
        //  1993708739
        private static final Collection<Integer> doublejoy5_half8 = new TreeSet<>();

        static int buttons = 0;
        static boolean orLessButtons = true;
        static int players = 1;
        static boolean orLessPlayers = true;
        static boolean noClones;
        static boolean simultaneousOnly;
        static boolean noMature = true;
        static boolean noImperfect;
        static boolean noMechanical;
        static boolean waysSelected;
        static boolean exactMatch;
        static String language = ALL_LANGUAGES;
        static String year;
        static String baseListName = ALL;
        private static String ways = ALL;
        private static String ways2 = ALL;
        String machineWays;
        String machineWays2;
        String machineWays2A;
        String machineWays2B;
        private int joySignature;
        private int doublejoySignature;

        //============================================================================================
        //                              Controls Compatibility
        private SortedSet<String> categories;
        private SortedSet<String> controls;

        private Builder() {
        }

        public static Builder getInstance() {
            return builder;
        }

        public int getPlayers() {
            return players;
        }

        public static void setPlayers(int playersIn) {
            players = playersIn;
        }

        public static int getButtons() {
            return buttons;
        }

        public static void setButtons(String buttonsIn) {
            // specials case where 9 means 9 or more
            if ("9+".equals(buttonsIn)) {
                // Arbitrary ridiculously high value
                Builder.buttons = MILLION;
                return;
            }
            buttons = Integer.parseInt(buttonsIn);
        }

        public static void setButtons(int buttonsIn) {
            buttons = buttonsIn;
        }

        public static void setOrLessButtons(boolean orLessButtonsIn) {
            orLessButtons = orLessButtonsIn;
        }

        public static void setOrLessPlayers(boolean orLessPlayersIn) {
            orLessPlayers = orLessPlayersIn;
        }

        public static void setNoClones(boolean noClonesIn) {
            noClones = noClonesIn;
        }

        public static void setSimultaneousOnly(boolean simultaneousOnlyIn) {
            simultaneousOnly = simultaneousOnlyIn;
        }

        public static void setNoImperfect(boolean noImperfectIn) {
            noImperfect = noImperfectIn;
        }

        public static void setWaysSelected(boolean waysSelectedIn) {
            waysSelected = waysSelectedIn;
        }

        //============================================================================================
        //                              Control Signature Compatibility

        /*
        Signature	Type	WAYS	WAYS2	WAYS3
        -1771213723	gambling
        -1607713821	doublejoy	2	2
        -1607713759	doublejoy	4	2
        -1607713757	doublejoy	4	4
        -1607713635	doublejoy	8	2
        -1607713629	doublejoy	8	8
        -1238305841	joy	3 (half4)
        -1134657068	keypad
        -995842198	paddle
        -558247347	joy	5 (half8)
        -482411992	joy	vertical2
        -410698973	doublejoy	vertical2	vertical2
        -865288	hanafuda
        3083120	dial
        3268317	joy	1
        3268318	joy	2
        3268320	joy	4
        3268324	joy	8
        104086693	mouse
        106542458	pedal
        109764752	stick
        503739367	keyboard
        730225098	trackball
        829995282	mahjong
        991968362	lightgun
        1198557890	triplejoy	8	8	8
        1381039892	positional
        1993708739	doublejoy	5 (half8)	5 (half8)
         */

        public static void setNoMature(boolean noMatureIn) {
            noMature = noMatureIn;
        }

        public static void setWays(String waysIn) {
            Builder.ways = waysIn;
            if (!waysSelected && !waysIn.equals(ALL)) {
                waysSelected = true;
            } else if (waysSelected && waysIn.equals(ALL) && ways2.equals(ALL)) {
                waysSelected = false;
            }
        }

        public static void setWays2(String ways2In) {
            ways2 = ways2In;
            if (!waysSelected && !ways2In.equals(ALL)) {
                waysSelected = true;
            } else if (waysSelected && ways.equals(ALL) && ways2In.equals(ALL)) {
                waysSelected = false;
            }
        }

        public static void setExactMatch(boolean exactMatchIn) {
            exactMatch = exactMatchIn;
        }

        public static void setLanguage(String languageIn) {
            language = languageIn;
        }

        public static void setYear(String yearIn) {
            year = yearIn;
        }

        public static void setNoMechanical(boolean noMechanicalIn) {
            noMechanical = noMechanicalIn;
        }

        public static void setBaseListName(String baseListNameIn) {
            if (MFM.isSystemDebug()) {
                System.out.println();
            }
            baseListName = baseListNameIn;
            if (MFM.isSystemDebug()) {
                System.out.println("Base List is: " + baseListName);
            }
        }

        /**
         * This is the core functionality.
         *
         * @return filtered list
         */
        @SuppressWarnings("ContinueStatement")
        public SortedSet<String> generateList() {
            TreeSet<String> baseList;
            SortedSet<String> finalList = new TreeSet<>();
            calculateWays(); // generates the Control signature from selected values
            /*
             * With 0.9 release we allow the user to select the Base List.
             * Start with base lists: Both or Arcade or Systems
             *  Then filter by orientation: (ALL), Vertical, Horizontal, Cocktail
             *  Then filter by  displayType: (ALL), Raster, Vector, LCD
             *
             */
            baseList = new TreeSet<>(MFMPlayLists.getInstance().getPlayList(baseListName));
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
                default:
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
                default:
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

                Collection<String> tempList = new TreeSet<>();
                for (String machineName : baseList) {
                    Machine machine = allMachines.get(machineName);
                    if (categories.contains(machine.getCategory())) {
                        tempList.add(machineName);
                    }
                }
                baseList.retainAll(tempList);
            }

            Collection<String> expandedControls = expandControls(this.controls);
            if (MFM.isSystemDebug()) {
                System.out.println("Expanded Controls are : " + expandedControls);
            }

            // Apply Year and Controls filter
            boolean checkYear = ((year != null) && !year.equals(ALL_YEARS));

            for (String machineName : baseList) {
                Machine machine = allMachines.get(machineName);
                // In older MAME these are not guaranteed and it IS now possible to get a null Machine
                if ((machine == null) || (machine.getDriver() == null) || (machine.getInput() == null)) {
                    continue;
                }

                // If year is selected but does not match
                if (checkYear && ((machine.getYear() == null) || !machine.getYear().equals(year))) {
                    continue;
                }


                List<String> machineControls = machine.getInput().getControl().stream().map(
                        Control::getType).collect(Collectors.toList());

                // Is there an All match of Controls
                if (ControlsFilterType.getState().equals(EXACT_CONTROLS) &&
                        !machineControls.containsAll(this.controls)) {
                    continue;
                }

                // Is there an Exact match of Controls
                if (ControlsFilterType.getState().equals(EXACT_CONTROLS) &&
                        (!machineControls.containsAll(this.controls) ||
                                (machineControls.size() != this.controls.size()))) {
                    continue;
                }

                if (checkControls(machine, machineControls, expandedControls)
                        && checkButtonsPlayers(machine.getButtons(),
                        Integer.parseInt(machine.getInput().getPlayers()))) {
                    finalList.add(machineName);
                }
            }

            controls = new TreeSet<>();
            categories = null;
            return finalList;
        }

        private boolean checkControls(Machine machine, Collection<String> machineControls,
                                      Collection<String> expandedControls) {
            // If No Controls or ways are selected ignore them
            if (controls.isEmpty() && !waysSelected) {
                return true;
            }

            machineControls.retainAll(expandedControls);

            if (machineControls.isEmpty()) {
                return false;
            }

            if (waysSelected) {
                if (ways.equalsIgnoreCase(ALL) && ways2.equalsIgnoreCase(ALL)) {
                    return true;
                }

                if (exactMatch) {
                    if (controls.contains(MachineControllers.JOY) &&
                            MachineControllers.getInstance().signatureHasMachine(joySignature, machine.getName())) {
                        return true;
                    }

                    if (controls.contains(MachineControllers.DOUBLEJOY)) {
                        return MachineControllers.getInstance().signatureHasMachine(doublejoySignature, machine.getName());
                    }
                } else {
                    if (controls.contains(MachineControllers.JOY) &&
                            MachineControllers.getInstance().signaturesHasMachine(
                                    expandJoys(joySignature), machine.getName())) {
                        return true;
                    }

                    if (controls.contains(MachineControllers.DOUBLEJOY)) {
                        MachineControllers.getInstance().signaturesHasMachine(
                                expandJoys(doublejoySignature), machine.getName());
                    }
                }
                return false;
            }
            return true;
        }

        @SuppressWarnings("OverlyComplexBooleanExpression")
        private static boolean checkButtonsPlayers(int machineButtons, int machinePlayers) {

            /* NOTE agreement with Obiwantje for Games where the number of Players for Simultaneous
             *  is different than the number of Players. So we just ignore number of Players
             */
            if (simultaneousOnly) {
                return (machineButtons == buttons) || (orLessButtons && (machineButtons <= buttons));
            }

            if ((machineButtons == buttons) && (machinePlayers == players)) {
                return true;
            } else if (orLessButtons && orLessPlayers && (machineButtons <= buttons) && (machinePlayers <= players)) {
                return true;
            } else if (orLessButtons && (machineButtons <= buttons) && (machinePlayers == players)) {
                return true;
            } else {
                return orLessPlayers && (machinePlayers <= players) &&
                        ((machineButtons == buttons) || ((buttons == MILLION) && (machineButtons >= 9)));
            }
        }

        private void calculateWays() {
            machineWays = ways.equalsIgnoreCase(ALL) ? "" : ways;
            joySignature = MachineControllers.getInstance().getSignature(new ArrayList<>(
                    Arrays.asList(MachineControllers.JOY, machineWays)));

            machineWays2 = ways2.equalsIgnoreCase(ALL) ? "" : ways2;
            machineWays2A = null;
            machineWays2B = null;
            if (machineWays2.contains("/")) {
                machineWays2A = machineWays2.substring(0, machineWays2.indexOf('/'));
                machineWays2B = machineWays2.substring(machineWays2.indexOf('/') + 1);
            } else {
                machineWays2A = machineWays2B = machineWays2;
            }

            doublejoySignature = MachineControllers.getInstance().getSignature(new ArrayList<>(
                    Arrays.asList(MachineControllers.DOUBLEJOY, machineWays2A, machineWays2B)));
        }

        public void setCategories(SortedSet<String> categoriesIn) {
            this.categories = categoriesIn;
        }

        public void setControls(SortedSet<String> controlsIn) {
            this.controls = controlsIn;
        }

        // Take selected Categories and expand roots
        private void expandRoots() {
            Collection<String> set = new TreeSet<>();
            for (String category : categories) {

                if (allCategoryRoots.contains(category)) {
                    List<String> children = categoryHierarchy.get(category);
                    if (children != null) {
                        set.addAll(children);
                    } else {
                        MFM.getLogger().addToList("Category: " + category + " is a root but returned no children");
                    }
                }
            }
            categories.addAll(set);
        }

        private static TreeSet<Integer> expandJoys(int joySignature) {
            TreeSet<Integer> expandedJoys = new TreeSet<>();
            expandedJoys.add(joySignature);
            switch (joySignature) {

                case JOY8:
                    expandedJoys.addAll(joy8);
                    break;

                case JOY2:
                    expandedJoys.addAll(joy2);
                    break;

                case JOY_3_HALF_4:
                    expandedJoys.addAll(joy3_half4);
                    break;

                case JOY4:
                    expandedJoys.addAll(joy4);
                    break;

                case JOY_5_HALF_8:
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

                case DOUBLEJOY_5_HALF_8:
                    expandedJoys.addAll(doublejoy5_half8);
                    break;
                default:
                    break;
            }
            return expandedJoys;
        }

        private static Collection<String> expandControls(SortedSet<String> controls) {
            Collection<String> expandedControls = new TreeSet<>(controls);
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

                    default:
                        break;
                }
            }
            return expandedControls;
        }
    }
}
