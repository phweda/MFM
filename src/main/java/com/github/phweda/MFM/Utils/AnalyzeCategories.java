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

package com.github.phweda.MFM.Utils;

import com.github.phweda.MFM.MAMEInfo;
import com.github.phweda.MFM.MFM;
import com.github.phweda.MFM.MFMListBuilder;
import com.github.phweda.MFM.MFM_Constants;
import com.github.phweda.MFM.mame.Machine;
import com.github.phweda.utils.FileUtils;
import com.github.phweda.utils.PersistUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    private static final int MAX_MACHINES = 50000; // Should be good for years -> 0.201 has 39,414 Machines total
    private static final int SIZE_64 = 64;
    private static final int SIZE_128 = 128;
    private static final int SIZE_256 = 256;

    private static Map<String, ArrayList<String>> categoryGames;
    private static ArrayList<String> allCategories;

    private static ArrayList<String> rootCategories;
    private static ArrayList<String> arcadeRoots;
    private static ArrayList<String> systemRoots;
    private static ArrayList<String> matureRoots;

    private static Set<String> arcadeCategories;
    private static ArrayList<String> systemCategories;
    private static ArrayList<String> arcadeNoMatureCategories;
    private static ArrayList<String> systemNoMatureCategories;

    private static final HashMap<String, String> version = new HashMap<>(MAX_MACHINES);
    private static final HashMap<String, String> machinetoCategoryMap = new HashMap<>(MAX_MACHINES);
    private static final Map<String, String> arcadeCategoriesMap = new HashMap<>(MAX_MACHINES);

    private static final String SEPARATOR = "~";
    private static final String MATURE = "mature";
    private static final String SAVE_PATH = MFM.getMfmCategoryDir();

    private static final Map<String, HashMap<String, String>> catverINImap = new HashMap<>(4);
    private static final SortedMap<String, ArrayList<String>> categoryHierarchy = new TreeMap<>();

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
    private static Map<String, ArrayList<String>> categoryListsMap = new HashMap<>(SIZE_256);

    private AnalyzeCategories() {
        throw new IllegalStateException("Utility clsss");
    }

    public static boolean analyzeCategories() {
        try {
            parseInputFiles();
            //   getCategories();
            createCatHierarchy();
            arcadeSystemLists();
            filterMature();
            persistCategoriestoFile(SAVE_PATH);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean analyzeCategories(String filePath) {
        try {
            Object obj = PersistUtils.loadAnObjectXML(filePath);
            if (obj.getClass().isAssignableFrom(HashMap.class)) {
                categoryGames = (Map<String, ArrayList<String>>) obj;
            } else {
                throw new ClassCastException("Input file must be XML of HashMap<String, ArrayList<String>> Object");
            }
            analyzeFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void parseInputFiles() {
        try {

            catverINImap.put("category", machinetoCategoryMap);
            catverINImap.put("version", version);
            new ParseCatverINI(MFM.getMfmFoldersDir() + MFM_Constants.CATVER_INI_FILENAME, catverINImap).processFile();
            new ParseRootOnlyINI(MFM.getMfmFoldersDir() + MFM_Constants.ARCADE_INI_FILENAME, arcadeCategoriesMap).processFile();

            TreeSet<String> sortedSet = new TreeSet<>(machinetoCategoryMap.values());
            allCategories = new ArrayList<>(sortedSet);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void analyzeFile() {
        allCategories = new ArrayList<>(SIZE_256);
        for (Map.Entry<String, ArrayList<String>> entry : categoryGames.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                allCategories.add(entry.getKey());
                if (MFM.isSystemDebug()) {
                    System.out.println(entry.getKey());
                }
            }
        }
    }

    private static void createCatHierarchy() {
        for (String entry : allCategories) {
            if (entry.contains("/")) {
                String key = entry.substring(0, entry.indexOf('/') - 1).trim();
                int index = entry.indexOf('/') + 1;
                String subKey = entry.substring(index);

                if (categoryHierarchy.containsKey(key)) {
                    // Shouldn't need but guess there could be duplicates
                    if (!categoryHierarchy.get(key).contains(subKey)) {
                        categoryHierarchy.get(key).add(subKey);
                    }
                } else {
                    ArrayList<String> list = new ArrayList<>(SIZE_64);
                    list.add(subKey);
                    categoryHierarchy.put(key, list);
                }
            } else { // with newest catver.ini ... circa 191 we shouldn't need this
                // If we haven't previously added it
                if (!categoryHierarchy.containsKey(entry)) {
                    categoryHierarchy.put(entry, new ArrayList<>(SIZE_64));
                }
            }
        }

        int count = 0;
        int countSub = 0;
        rootCategories = new ArrayList<>(SIZE_128);
        matureRoots = new ArrayList<>(SIZE_64);
        for (Map.Entry<String, ArrayList<String>> category : categoryHierarchy.entrySet()) {
            System.out.println(++count + ". " + category.getKey());
            rootCategories.add(category.getKey());
            if (Pattern.compile(Pattern.quote(MATURE), Pattern.CASE_INSENSITIVE).matcher(category.getKey()).find()) {
                matureRoots.add(category.getKey());
            }
            for (String subKey : category.getValue()) {
                if (MFM.isSystemDebug()) {
                    System.out.println(FileUtils.TAB + ++countSub + ". " + subKey);
                }
            }
            if (MFM.isSystemDebug()) {
                System.out.println();
            }
        }
        categoryListsMap.put(MFMListBuilder.ALL_ROOTS, rootCategories);
        categoryListsMap.put(MFMListBuilder.MATURE_ROOTS, matureRoots);
    }

    private static void arcadeSystemLists() {
        Set<String> arcadeRootSet = new TreeSet<>();
        arcadeCategories = new TreeSet<>();
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

        arcadeRoots = new ArrayList<>(arcadeRootSet);

        systemCategories = new ArrayList<>(allCategories);
        systemCategories.removeAll(arcadeCategories);

        // For human readability for sanity checking
        Collections.sort(systemCategories);

        // Generate System Roots -- TODO figure out correct single lamda
        TreeSet<String> sysRoots = new TreeSet<>();
        systemCategories.forEach(category ->
        {
            if (category.indexOf('/') > -1) {
                sysRoots.add(category.substring(0, category.indexOf('/')).trim());
            }
        });
        systemRoots = new ArrayList<>(sysRoots);

        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORIES, new ArrayList<>(arcadeCategories));
        categoryListsMap.put(MFMListBuilder.ARCADE_ROOTS, arcadeRoots);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORIES, systemCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_ROOTS, systemRoots);
    }

    private static void filterMature() {
        arcadeNoMatureCategories = new ArrayList<>(SIZE_128);
        systemNoMatureCategories = new ArrayList<>(SIZE_128);
        TreeSet<String> matureCategories = new TreeSet<>();

        arcadeCategories.forEach(category -> {
            if (Pattern.compile(Pattern.quote(MATURE), Pattern.CASE_INSENSITIVE).matcher(category).find()) {
                matureCategories.add(category);
            } else {
                arcadeNoMatureCategories.add(category);
            }
        });

        systemCategories.forEach(category -> {
            if (Pattern.compile(Pattern.quote(MATURE), Pattern.CASE_INSENSITIVE).matcher(category).find()) {
                matureCategories.add(category);
            } else {
                systemNoMatureCategories.add(category);
            }
        });

        categoryListsMap.put(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES, arcadeNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES, systemNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.ALL_MATURE_CATEGORIES, new ArrayList<>(matureCategories));
    }

    private static void persistCategoriestoFile(String path) {
        PersistUtils.saveAnObjectXML(categoryListsMap, path + "categoryListsMapNEW.xml");
        outputToCSV();
    }

    private static void outputToCSV() {
        try (PrintWriter pw = new PrintWriter(new File(MFM.getMfmCategoryDir() + "categories.csv"),
                StandardCharsets.UTF_8.toString())) {

            final String header = MFMListBuilder.ALL_ROOTS + SEPARATOR + MFMListBuilder.ARCADE_ROOTS + SEPARATOR +
                    MFMListBuilder.SYSTEM_ROOTS + SEPARATOR + MFMListBuilder.MATURE_ROOTS + SEPARATOR +
                    MFMListBuilder.ARCADE_CATEGORIES + SEPARATOR + MFMListBuilder.SYSTEM_CATEGORIES + SEPARATOR +
                    MFMListBuilder.ALL_MATURE_CATEGORIES + SEPARATOR + MFMListBuilder.ARCADE_NOMATURE_CATEGORIES +
                    SEPARATOR + MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES;

            pw.println(header);

            int[] sizes = {
                    categoryListsMap.get(MFMListBuilder.ALL_ROOTS).size(),
                    categoryListsMap.get(MFMListBuilder.ARCADE_ROOTS).size(),
                    categoryListsMap.get(MFMListBuilder.SYSTEM_ROOTS).size(),
                    categoryListsMap.get(MFMListBuilder.MATURE_ROOTS).size(),
                    categoryListsMap.get(MFMListBuilder.ARCADE_CATEGORIES).size(),
                    categoryListsMap.get(MFMListBuilder.SYSTEM_CATEGORIES).size(),
                    (categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES) != null) ?
                            categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES).size() : 0,
                    categoryListsMap.get(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES).size(),
                    categoryListsMap.get(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES).size(),
            };

            int max = Arrays.stream(sizes).max().getAsInt();
            for (int i = 0; i < max; i++) {
                StringBuilder line = new StringBuilder(1024);
                if (categoryListsMap.get(MFMListBuilder.ALL_ROOTS).size() > i) {
                    line.append(categoryListsMap.get(MFMListBuilder.ALL_ROOTS).get(i));
                }
                line.append(SEPARATOR);
                if (categoryListsMap.get(MFMListBuilder.ARCADE_ROOTS).size() > i) {
                    line.append(categoryListsMap.get(MFMListBuilder.ARCADE_ROOTS).get(i));
                }
                line.append(SEPARATOR);
                if (categoryListsMap.get(MFMListBuilder.SYSTEM_ROOTS).size() > i) {
                    line.append(categoryListsMap.get(MFMListBuilder.SYSTEM_ROOTS).get(i));
                }
                line.append(SEPARATOR);
                if (categoryListsMap.get(MFMListBuilder.MATURE_ROOTS).size() > i) {
                    line.append(categoryListsMap.get(MFMListBuilder.MATURE_ROOTS).get(i));
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
                if ((categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES) != null) &&
                        (categoryListsMap.get(MFMListBuilder.ALL_MATURE_CATEGORIES).size() > i)) {
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
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ContinueStatement")
    public static void enterNewCategories(File file, String name) {
// All Roots	Arcade Roots	System Roots	Mature Roots	Arcade Categories	System Categories
// All Mature Categories	Arcade No Mature Categories	System No Mature Categories
        rootCategories = new ArrayList<>(SIZE_64);
        arcadeRoots = new ArrayList<>(SIZE_64);
        systemRoots = new ArrayList<>(SIZE_64);
        matureRoots = new ArrayList<>(SIZE_64);

        ArrayList<String> arcadeCategories = new ArrayList<>(SIZE_128);
        systemCategories = new ArrayList<>(SIZE_128);
        ArrayList<String> allMatureCategories = new ArrayList<>(SIZE_64);
        arcadeNoMatureCategories = new ArrayList<>(SIZE_128);
        systemNoMatureCategories = new ArrayList<>(SIZE_128);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // NOTE Why doesn't this work!!
            //   String line = br.readLine(); // Throw out header
            String line;
            while ((line = br.readLine()) != null) {
                // Throw out header
                if (line.contains(MFMListBuilder.ALL_ROOTS)) {
                    continue;
                }
                String[] entries = line.split(SEPARATOR);
                if (MFM.isSystemDebug()) {
                    System.out.println(FileUtils.EMPTY_STRING + entries[0] + " , name=" + entries[1] + FileUtils.EMPTY_STRING);
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
        rootCategories.removeIf(item -> (item == null) || item.isEmpty());
        arcadeRoots.removeIf(item -> (item == null) || item.isEmpty());
        systemRoots.removeIf(item -> (item == null) || item.isEmpty());
        matureRoots.removeAll(Collections.singleton(""));
        arcadeCategories.removeAll(Collections.singleton(""));
        systemCategories.removeAll(Collections.singleton(""));
        allMatureCategories.removeAll(Collections.singleton(""));
        arcadeNoMatureCategories.removeAll(Collections.singleton(FileUtils.EMPTY_STRING));
        systemNoMatureCategories.removeAll(Collections.singleton(FileUtils.EMPTY_STRING));

        // add all
        categoryListsMap = new HashMap<>(12); // Ensure it is empty

        categoryListsMap.put(MFMListBuilder.ALL_ROOTS, rootCategories);
        categoryListsMap.put(MFMListBuilder.ARCADE_ROOTS, arcadeRoots);
        categoryListsMap.put(MFMListBuilder.SYSTEM_ROOTS, systemRoots);
        categoryListsMap.put(MFMListBuilder.MATURE_ROOTS, matureRoots);

        categoryListsMap.put(MFMListBuilder.ARCADE_CATEGORIES, arcadeCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_CATEGORIES, systemCategories);
        categoryListsMap.put(MFMListBuilder.ALL_MATURE_CATEGORIES, allMatureCategories);
        categoryListsMap.put(MFMListBuilder.ARCADE_NOMATURE_CATEGORIES, arcadeNoMatureCategories);
        categoryListsMap.put(MFMListBuilder.SYSTEM_NOMATURE_CATEGORIES, systemNoMatureCategories);

        persistCategoriestoFile(MFM.getMfmCategoryDir() + name);
    }
}
