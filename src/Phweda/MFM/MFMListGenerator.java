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

import Phweda.MFM.Utils.ParseFolderINIs;
import Phweda.MFM.datafile.Datafile;
import Phweda.MFM.mame.Machine;

import java.io.FileNotFoundException;
import java.util.*;

import static Phweda.MFM.MFMListBuilder.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 4/8/2016
 * Time: 9:35 PM
 */
class MFMListGenerator {

    private static final String WE_SHOULDN_T_GET_HERE_LIST_BUILDER_GENERATE_MFMLISTS_CLONEOF = "We shouldn't get here : ListBuilder.generateMFMLists() cloneof";

    private MFMListGenerator() {
    }

    private static TreeMap<String, SortedSet<String>> loadLanguagesINI() {
        try {
            if (MFM.isDebug()) {
                MFM.getLogger().addToList("In loadLanguagesINI");
            }
            TreeMap<String, SortedSet<String>> languagesMap = new TreeMap<>();
            new ParseFolderINIs(MFMSettings.getInstance().getLanguageINI(), languagesMap).processFile();
            filterLanguagesMap(languagesMap);
            MFM_Data.getInstance().setStaticData(MFM_Constants.LANGUAGESLISTS, languagesMap);
            return languagesMap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void filterLanguagesMap(Map<String, ? extends SortedSet<String>> languagesMap) {
        // remove any non-existent machines - some entries in the languages.ini do not exist in many versions
        languagesMap.forEach((key, set) ->
                set.removeIf(machineName -> !allList.contains(machineName))
        );
        // Remove any empty lists
        languagesMap.entrySet().removeIf(set -> set.getValue().isEmpty());
    }

    @SuppressWarnings("ContinueStatement")
    static HashMap<String, TreeSet<String>> generateMFMLists(boolean parsing) {
        HashMap<String, TreeSet<String>> lists = null;
        if (!parsing) {
            lists = (HashMap<String, TreeSet<String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.LISTS);
        }

        if (lists == null) {
            lists = new HashMap<>(24);
            for (Map.Entry<String, Machine> entry : allMachines.entrySet()) {
                Machine machine = entry.getValue();
                String machineName = machine.getName();
                if (machine.getIsbios().equals(Machine.YES)) {
                    biosList.add(machineName);
                    continue; // Eliminating BIOS from all lists except new biosList 3/27/16
                } else if (machine.getIsdevice().equals(Machine.YES)) {
                    devicesList.add(machineName);
                    continue; // Eliminating Devices from all lists with JAXB and all being added
                } else {
                    allList.add(machineName);
                }

                if (machine.getIsmechanical().equals(Machine.YES)) {
                    mechanicalList.add(machineName);
                }

                // Create System & Arcade lists
                String category = machine.getCategory();
                category = category.trim();

                if (arcadeCategories.contains(category)) {
                    arcadeList.add(machineName);
                } else if (systemCategories.contains(category)) {
                    systemList.add(machineName);
                } else {
                    if (MFM.isDebug()) {
                        MFM.getLogger().addToList(machineName + " category : " + category +
                                " has no entry in Arcade or System categories lists.");
                    }
                }

                if (machine.getDisk().isEmpty()) {
                    chdList.add(machineName);
                }

                // Skip other lists if it is NOT runnable
                if (!runnableList.contains(entry.getKey())) {
                    continue;
                }

                // For 0.85 release null check needed since older Mame versions' BIOS Machines and Devices
                // do not have a driver
                if (machine.getDriver() != null) {
                    if (!machine.getDriver().getStatus().equalsIgnoreCase(IMPERFECT)) {
                        noImpefectList.add(machineName);
                    }

                    // If the cocktail attr exists
                    if ((machine.getDriver().getCocktail() != null) && !machine.getDriver().getCocktail().isEmpty()) {
                        cocktailList.add(machineName);
                    }
                }

                if (!machine.getIsVertical().isEmpty() && machine.getIsVertical().equalsIgnoreCase(Machine.VERTICAL)) {
                    verticalsList.add(machineName);
                } else if (!machine.getIsVertical().isEmpty() &&
                        machine.getIsVertical().equalsIgnoreCase(Machine.HORIZONTAL)) {
                    horizontalList.add(machineName);
                }

                if (machine.isSupportsSimultaneous()) {
                    simultaneousList.add(machineName);
                }

                String cloneof = machine.getCloneof();
                if ((cloneof == null) || cloneof.isEmpty()) {
                    noClonesList.add(machineName);
                } else if (cloneof.length() > 1) { // No machine with single char so 2 or more we assume is correct
                    clonesList.add(machineName);
                } else {
                    System.out.println(WE_SHOULDN_T_GET_HERE_LIST_BUILDER_GENERATE_MFMLISTS_CLONEOF);
                    MFM.getLogger().addToList(WE_SHOULDN_T_GET_HERE_LIST_BUILDER_GENERATE_MFMLISTS_CLONEOF);
                }

                if (!machine.getDisplay().isEmpty() || !machine.getScreentype().isEmpty()) {
                    String screenType = null;
                    if (!machine.getScreentype().isEmpty()) {
                        screenType = machine.getScreentype();
                    } else if (!machine.getDisplay().isEmpty()) {
                        screenType = machine.getDisplay().get(0).getType();
                    }
                    if (screenType == null) {
                        screenType = "";
                    }
                    switch (screenType) {

                        case MFM_Constants.RASTER:
                            rasterDisplayList.add(machineName);
                            break;

                        case MFM_Constants.VECTOR:
                            vectorDisplayList.add(machineName);
                            break;

                        case "lcd":
                            lcdDisplayList.add(machineName);
                            break;

                        default:
                            if (MFM.isSystemDebug()) {
                                System.out.println(machineName + " has no screenType : " + screenType);
                            }
                    }
                }
            }

            // Which Categories have at least one Machine
            Map<String, ArrayList<String>> categoryGames = MAMEInfo.getCategoryMachinesMap();
            for (Map.Entry<String, ArrayList<String>> categoryEntry : categoryGames.entrySet()) {
                if (!categoryEntry.getValue().isEmpty()) {
                    categoriesWithMachineList.add(categoryEntry.getKey());
                }
            }

            lists.put(ALL, allList);
            // Eliminate empty non Language lists here for older MAME versions
            if (!biosList.isEmpty()) {
                lists.put(BIOS, biosList);
            }
            if (!devicesList.isEmpty()) {
                lists.put(DEVICES, devicesList);
            }
            if (!verticalsList.isEmpty()) {
                lists.put(VERTICAL, verticalsList);
            }
            if (!horizontalList.isEmpty()) {
                lists.put(HORIZONTAL, horizontalList);
            }
            if (!clonesList.isEmpty()) {
                lists.put(CLONE, clonesList);
            }
            if (!noClonesList.isEmpty()) {
                lists.put(NO_CLONE, noClonesList);
            }
            if (!cocktailList.isEmpty()) {
                lists.put(COCKTAIL, cocktailList);
            }
            if (!simultaneousList.isEmpty()) {
                lists.put(SIMULTANEOUS, simultaneousList);
            }
            if (!runnableList.isEmpty()) {
                lists.put(RUNNABLE, runnableList);
            }
            if (!arcadeList.isEmpty()) {
                lists.put(ARCADE, arcadeList);
            }
            if (!systemList.isEmpty()) {
                lists.put(SYSTEMS, systemList);
            }
            if (!rasterDisplayList.isEmpty()) {
                lists.put(RASTER, rasterDisplayList);
            }
            if (!vectorDisplayList.isEmpty()) {
                lists.put(VECTOR, vectorDisplayList);
            }
            if (!lcdDisplayList.isEmpty()) {
                lists.put(LCD, lcdDisplayList);
            }
            if (!chdList.isEmpty()) {
                lists.put(CHD, chdList);
            }
            if (!noImpefectList.isEmpty()) {
                lists.put(NO_IMPERFECT, noImpefectList);
            }
            if (!categoriesWithMachineList.isEmpty()) {
                lists.put(CATEGORIES, categoriesWithMachineList);
            }
            if (!mechanicalList.isEmpty()) {
                lists.put(MECHANICAL, mechanicalList);
            }
            MFM_Data.getInstance().setStaticData(MFM_Constants.LISTS, lists);
        }
        return lists;
    }

    @SuppressWarnings("unchecked")
    static TreeMap<String, SortedSet<String>> getLanguageLists(boolean parsing) {
        TreeMap<String, SortedSet<String>> languagesListMap;
        if (!parsing) {
            languagesListMap =
                    (TreeMap<String, SortedSet<String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.LANGUAGESLISTS);
        } else {
            languagesListMap = loadLanguagesINI();
            // Dependency ALL list must already exists
            MFM_Data.getInstance().setStaticData(MFM_Constants.LANGUAGESLISTS, languagesListMap);
            if ((languagesListMap == null) && MFM.isSystemDebug()) {
                System.out.println("In MFMListGenerator failed to generate Languages lists");
            }
        }
        return languagesListMap;
    }

    static TreeSet<String> generateListfromDAT(Datafile datafile) {
        TreeSet<String> list = new TreeSet<>();
        datafile.getGame().forEach(game -> list.add(game.getName()));
        return list;
    }

}
