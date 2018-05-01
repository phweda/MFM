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

package Phweda.MFM.Utils;

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFM;
import Phweda.MFM.MFMListBuilder;
import Phweda.MFM.MFM_Constants;
import Phweda.MFM.mame.Machine;
import Phweda.utils.PersistUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/12/2015
 * Time: 4:09 PM
 */

/**
 * Uses catver.ini and arcade.ini to update MFM Category file
 * This is NOT a complete automated solution. Still requires human tweaks and reimport
 * Need to see if I can get a definitive statement on future catver.ini changes. If yes may be able
 * to assure no roots tweaking (May 2018)
 */
public class AnalyzeCategories {

    private HashMap<String, ArrayList<String>> categoryGames;
    private ArrayList<String> allCategories;

    private static ArrayList<String> rootCategories;
    private static ArrayList<String> arcadeRoots;
    private static ArrayList<String> systemRoots;
    private static ArrayList<String> matureRoots;

    private static Set<String> arcadeCategories;
    private static ArrayList<String> systemCategories;
    private static ArrayList<String> arcadeNoMatureCategories;
    private static ArrayList<String> systemNoMatureCategories;

    private static HashMap<String, String> Version = new HashMap<String, String>();
    private static HashMap<String, String> MachinetoCategoryMap = new HashMap<String, String>();
    private static HashMap<String, Set<String>> CategorytoMachinesMap = new HashMap<String, Set<String>>();

    private static Map<String, String> arcadeCategoriesMap = new HashMap<String, String>();

    private static final String SEPARATOR = "~";

    private static String savePath = MFM.MFM_CATEGORY_DIR;

    private Map<String, HashMap<String, String>> CatverINImap = new HashMap<String, HashMap<String, String>>();
    private TreeMap<String, ArrayList<String>> categoryHierarchy = new TreeMap<String, ArrayList<String>>();

    /* Hashmap of Arraylists for CategoryListsMap.xml
        All Roots
        Arcade Categories
        System Roots
        System Categories
        Arcade Roots
        Arcade No Mature Categories
        All Mature Categories
        Mature Roots
        System No Mature Categories
    */
    private static HashMap<String, ArrayList<String>> categoryListsMap = new HashMap<String, ArrayList<String>>();

    public AnalyzeCategories() {
        parseInputFiles();
        //   getCategories();
        createCatHierarchy();
        arcadeSystemLists();
        filterMature();
        persistCategoriestoFile(savePath);
    }

    private void parseInputFiles() {
        try {

            CatverINImap.put("category", MachinetoCategoryMap);
            CatverINImap.put("version", Version);
            new ParseCatverINI(MFM.MFM_FOLDERS_DIR + MFM_Constants.CATVER_INI_FILENAME, CatverINImap).processFile();
            new ParseRootOnlyINI(MFM.MFM_FOLDERS_DIR + MFM_Constants.ARCADE_INI_FILENAME, arcadeCategoriesMap).processFile();

            TreeSet<String> sortedSet = new TreeSet<String>(MachinetoCategoryMap.values());
            allCategories = new ArrayList<String>(sortedSet);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void analyzeFile() {
        allCategories = new ArrayList<String>();
        for (String key : categoryGames.keySet()) {
            ArrayList<String> list = categoryGames.get(key);
            if (!list.isEmpty()) {
                allCategories.add(key);
                if (MFM.isSystemDebug()) {
                    System.out.println(key);
                }
            }
        }
    }

    private void createCatHierarchy() {
        for (String entry : allCategories) {
            if (entry.contains("/")) {
                String key = entry.substring(0, entry.indexOf('/') - 1).trim();
                int index = entry.indexOf("/") + 1;
                String subKey = entry.substring(index);

                if (categoryHierarchy.containsKey(key)) {
                    // Shouldn't need but guess there could be duplicates
                    if (!categoryHierarchy.get(key).contains(subKey)) {
                        categoryHierarchy.get(key).add(subKey);
                    }
                } else {
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(subKey);
                    categoryHierarchy.put(key, list);
                }
            } else { // with newest catver.ini ... circa 191 we shouldn't need this
                // If we haven't previously added it
                if (!categoryHierarchy.containsKey(entry)) {
                    categoryHierarchy.put(entry, new ArrayList<String>());
                }
            }
        }

        int count = 0;
        int countSub = 0;
        rootCategories = new ArrayList<String>();
        matureRoots = new ArrayList<String>();
        for (String key : categoryHierarchy.keySet()) {
            System.out.println(++count + ". " + key);
            rootCategories.add(key);
            if (Pattern.compile(Pattern.quote("mature"), Pattern.CASE_INSENSITIVE).matcher(key).find()) {
                matureRoots.add(key);
            }
            for (String subKey : categoryHierarchy.get(key)) {
                if (MFM.isSystemDebug()) {
                    System.out.println("\t" + ++countSub + ". " + subKey);
                }
            }
            if (MFM.isSystemDebug()) {
                System.out.println("");
            }
        }
        categoryListsMap.put(MFMListBuilder.ALL_CATEGORY_ROOTS, rootCategories);
        categoryListsMap.put(MFMListBuilder.MATURE_CATEGORY_ROOTS, matureRoots);
    }

    private void arcadeSystemLists() {
        Set<String> arcadeRootSet = new TreeSet<String>();
        arcadeCategories = new TreeSet<String>();
        arcadeCategoriesMap.forEach((key, value) -> {
            Machine machine = MAMEInfo.getMachine(key);
            // Some roots do not have a direct entry e.g. Sports
            if (machine != null) {
                String category = machine.getCategory();
                if (category.isEmpty()) {
                    return; // Goes to the next iteration of foreach
                }
                arcadeCategories.add(category);

                if (category.contains("/")) {
                    category = category.substring(0, category.indexOf('/')).trim();
                }
                // we always add here as some categories are also roots e.g. Ball NOTE not with newest 191+
                arcadeRootSet.add(category);
            }
        });

        arcadeRoots = new ArrayList<String>(arcadeRootSet);

        systemCategories = new ArrayList<String>(allCategories);
        systemCategories.removeAll(arcadeCategories);

        // For human readability for sanity checking
        Collections.sort(systemCategories);

        // Generate System Roots -- TODO figure out correct single lamda
        TreeSet<String> sysRoots = new TreeSet<String>();
        systemCategories.forEach(category ->
        {
            if (category.indexOf('/') > 0) {
                sysRoots.add(category.substring(0, category.indexOf('/')).trim());
            }
        });
        systemRoots = new ArrayList<String>(sysRoots);

        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORIES, new ArrayList<String>(arcadeCategories));
        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORY_ROOTS, arcadeRoots);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORIES, systemCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORY_ROOTS, systemRoots);
    }

    @SuppressWarnings("unchecked")
    public AnalyzeCategories(String filePath) {
        try {
            Object obj = PersistUtils.loadAnObjectXML(filePath);
            if (obj.getClass().isAssignableFrom(HashMap.class)) {
                categoryGames = (HashMap<String, ArrayList<String>>) obj;
            } else {
                throw new ClassCastException("Input file must be XML of HashMap<String, ArrayList<String>> Object");
            }
            analyzeFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void filterMature() {
        arcadeNoMatureCategories = new ArrayList<String>();
        systemNoMatureCategories = new ArrayList<String>();
        TreeSet<String> matureCategories = new TreeSet<String>();

        arcadeCategories.forEach(category -> {
            if (!Pattern.compile(Pattern.quote("mature"), Pattern.CASE_INSENSITIVE).matcher(category).find()) {
                arcadeNoMatureCategories.add(category);
            } else {
                matureCategories.add(category);
            }
        });

        systemCategories.forEach(category -> {
            if (!Pattern.compile(Pattern.quote("mature"), Pattern.CASE_INSENSITIVE).matcher(category).find()) {
                systemNoMatureCategories.add(category);
            } else {
                matureCategories.add(category);
            }
        });

        categoryListsMap.put(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES, arcadeNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES, systemNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.ALL_MATURE_CATEGORIES, new ArrayList<String>(matureCategories));
    }

    private static void persistCategoriestoFile(String path) {
        PersistUtils.saveAnObjectXML(categoryListsMap, path + "categoryListsMapNEW.xml");
        outputToCSV();
    }

    private static void outputToCSV() {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File(MFM.MFM_CATEGORY_DIR + "categories.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final String header = "All Roots" + SEPARATOR + "Arcade Roots" + SEPARATOR + "System Roots" + SEPARATOR +
                "Mature Roots" + SEPARATOR + "" + "Arcade Categories" + SEPARATOR + "System Categories" + SEPARATOR +
                "All Mature Categories" + SEPARATOR + "" + "Arcade No Mature Categories" + SEPARATOR +
                "System No Mature Categories";

        pw.println(header);

        int[] sizes = {
                categoryListsMap.get(MFMListBuilder.ALL_CATEGORY_ROOTS).size(),
                categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORY_ROOTS).size(),
                categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORY_ROOTS).size(),
                categoryListsMap.get(MFMListBuilder.MATURE_CATEGORY_ROOTS).size(),
                categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORIES).size(),
                categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORIES).size(),
                categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES) != null ?
                        categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES).size() : 0,
                categoryListsMap.get(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES).size(),
                categoryListsMap.get(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES).size(),
        };

        int max = Arrays.stream(sizes).max().getAsInt();
        for (int i = 0; i < max; i++) {
            StringBuilder line = new StringBuilder();
            if (categoryListsMap.get(MFMListBuilder.ALL_CATEGORY_ROOTS).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.ALL_CATEGORY_ROOTS).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORY_ROOTS).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORY_ROOTS).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORY_ROOTS).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORY_ROOTS).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.MATURE_CATEGORY_ROOTS).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.MATURE_CATEGORY_ROOTS).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORIES).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORIES).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORIES).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORIES).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES) != null &&
                    categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES).size() > i) {
                line.append(categoryListsMap.get(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES).get(i));
            }
            line.append(SEPARATOR);
            if (categoryListsMap.get(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES).size() > i)
                line.append(categoryListsMap.get(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES).get(i));
            pw.println(line.toString());
        }
        pw.close();
    }

    public static void enterNewCategories(File file, String name) {
// All Roots	Arcade Roots	System Roots	Mature Roots	Arcade Categories	System Categories
// All Mature Categories	Arcade No Mature Categories	System No Mature Categories
        rootCategories = new ArrayList<String>();
        arcadeRoots = new ArrayList<String>();
        systemRoots = new ArrayList<String>();
        matureRoots = new ArrayList<String>();

        ArrayList<String> arcadeCategories = new ArrayList<String>();
        systemCategories = new ArrayList<String>();
        ArrayList<String> allMatureCategories = new ArrayList<String>();
        arcadeNoMatureCategories = new ArrayList<String>();
        systemNoMatureCategories = new ArrayList<String>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // NOTE Why doesn't this work!!
            //   String line = br.readLine(); // Throw out header
            String line = null;
            while ((line = br.readLine()) != null) {
                // Throw out header
                if (line.contains("All Roots")) {
                    continue;
                }
                String[] entries = line.split(SEPARATOR);
                if (MFM.isSystemDebug()) {
                    System.out.println("" + entries[0] + " , name=" + entries[1] + "");
                }

                rootCategories.add(entries[0]);
                arcadeRoots.add(entries[1]);
                systemRoots.add(entries[2]);
                matureRoots.add(entries[3]);
                arcadeCategories.add(entries[4]);
                // Some spreadsheets do not output all columns
                // We start to check here since Arcade Categories will always be the longest list
                if (entries.length > 5) systemCategories.add(entries[5]);
                if (entries.length > 6) allMatureCategories.add(entries[6]);
                if (entries.length > 7) arcadeNoMatureCategories.add(entries[7]);
                if (entries.length > 8) systemNoMatureCategories.add(entries[8]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove empty strings showing two different methods to accomplish it
        rootCategories.removeIf(item -> item == null || "".equals(item));
        arcadeRoots.removeIf(item -> item == null || "".equals(item));
        systemRoots.removeIf(item -> item == null || "".equals(item));
        matureRoots.removeAll(Collections.singleton(""));
        arcadeCategories.removeAll(Collections.singleton(""));
        systemCategories.removeAll(Collections.singleton(""));
        allMatureCategories.removeAll(Collections.singleton(""));
        arcadeNoMatureCategories.removeAll(Collections.singleton(""));
        systemNoMatureCategories.removeAll(Collections.singleton(""));

        // add all
        categoryListsMap = new HashMap<String, ArrayList<String>>(); // Ensure it is empty

        categoryListsMap.put(MFMListBuilder.ALL_CATEGORY_ROOTS, rootCategories);
        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORY_ROOTS, arcadeRoots);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORY_ROOTS, systemRoots);
        categoryListsMap.put(MFMListBuilder.MATURE_CATEGORY_ROOTS, matureRoots);

        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORIES, arcadeCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORIES, systemCategories);
        categoryListsMap.put(MFMListBuilder.ALL_MATURE_CATEGORIES, allMatureCategories);
        categoryListsMap.put(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES, arcadeNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES, systemNoMatureCategories);

        persistCategoriestoFile(MFM.MFM_CATEGORY_DIR + name);
    }
}
