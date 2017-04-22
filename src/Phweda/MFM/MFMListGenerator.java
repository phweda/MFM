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

import Phweda.MFM.Utils.ParseFolderINIs;
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
    // TODO eliminate this since VIDs project is dead ?
    // fixme reduce this to only the Arcade exclusions since we now have Systems Categories
    private static final List<String> vidsExcludeCategoriesList =
            Arrays.asList(
                    "3D Printer", "Arcade BIOS", "Astrological Computer", "Audio Sequencer",
                    "Bridge Machine", "Business Computer / Terminal", "Calculator / Pocket Computer", "Cash Counter",
                    "Casino", "Casino / Cards", "Casino / Lottery", "Casino / Cards * Mature *",
                    "Casino * Mature *", "Casino / Multiplay", "Casino / Racing", "Casino / Reels",
                    "Casino / Reels * Mature *", "Casino / Roulette", "Chess Machine", "Clock",
                    "Development Computer", "Devices", "Document Processors", "Dot-Matrix Display", "Drum Machine",
                    "EPROM Programmer", "Electromechanical / Coin pusher",
                    "Educational Game", "Electronic Game", "Electromechanical / Misc.", "Electromechanical / Pinball",
                    "Electromechanical / Reels", "Electromechanical / Utilities", "Game Console",
                    "Handheld Child Computers", "", "Handheld Console", "Home Computer",
                    "In Circuit Emulator", "Kit Computer", "Laptop / Notebook / Portable",
                    "Matrix Printer", "Microcomputer", "Multi-cart Board", "Network Processor",
                    "Not Classified", "Pocket Device / Pad / PDA", "Print Club", "Punched Card Computer",
                    "Quiz / Music English", "Quiz / Chinese", "Quiz / English", "Quiz / English * Mature *",
                    "Quiz / French", "Quiz / German", "Quiz / Italian", "Quiz / Japanese",
                    "Quiz / Japanese * Mature *", "Quiz / Music Japanese", "Quiz / Korean", "Quiz / Spanish",
                    "Robot Control", "Satellite Receiver", "Single Board Computer", "Speech Synthesiser",
                    "Synthesiser", "System / BIOS", "System / Device", "Telephone / ComputerPhone",
                    "Tabletop / Cards", "Tabletop / Go", "Tabletop / Hanafuda",
                    "Tabletop / Hanafuda * Mature *", "Tabletop / Mahjong", "Tabletop / Mahjong * Mature *",
                    "Tabletop / Misc.", "Tabletop / Misc. * Mature *", "Tabletop / Multiplay", "Tabletop / Othello",
                    "Tabletop / Othello * Mature *", "Tabletop / Renju", "Test ROM",
                    "Training Board", "Utilities / Test", "Utilities / Update",
                    "VTR Control", "Word-processing Machine", "Workstation / Server"
            );
    private static MFMListGenerator ourInstance = new MFMListGenerator();
    private static TreeMap<String, TreeSet<String>> languagesListMap;

    private MFMListGenerator() {
    }

    public static MFMListGenerator getInstance() {
        return ourInstance;
    }

    private static TreeMap<String, TreeSet<String>> loadLanguagesINI() {
        try {
            if (MFM.isDebug()) {
                MFM.logger.addToList("In loadLanguagesINI");
            }
            TreeMap<String, TreeSet<String>> languagesMap = new TreeMap<String, TreeSet<String>>();
            new ParseFolderINIs(MFMSettings.getInstance().getLanguageINI(), languagesMap).processFile();
            // if not all then remove non-playable machines
            if (!MFM.isProcessAll()) {
                filterLanguagesMap(languagesMap);
            }
            MFM_Data.getInstance().setStaticData(MFM_Constants.LANGUAGESLISTS, languagesMap);
            return languagesMap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void filterLanguagesMap(TreeMap<String, TreeSet<String>> languagesMap) {
        // remove any non-playable machines
        languagesMap.forEach((key, set) ->
                set.removeIf(machineName -> !allList.contains(machineName))
        );
        // Remove any empty lists
        languagesMap.entrySet().removeIf(set -> set.getValue().isEmpty());
    }

/*
    private static boolean needsVID(Machine machine) {
        String category = machine.getCategory();
        return !vidsExcludeCategoriesList.contains(category);
    }
*/

    HashMap<String, TreeSet<String>> generateMFMLists() {
        HashMap<String, TreeSet<String>> lists;
        lists = (HashMap<String, TreeSet<String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.LISTS);

        if (lists == null) {
            lists = new HashMap<String, TreeSet<String>>();
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

                // Create System & Arcade lists
                String category = machine.getCategory();
                category = category.trim();

                if (arcadeCategories.contains(category)) {
                    arcadeList.add(machineName);
                } else if (systemCategories.contains(category)) {
                    systemList.add(machineName);
                } else {
                    if (MFM.isDebug()) {
                        MFM.logger.addToList(machineName + " category : " + category +
                                " has no entry in Arcade or System categories lists.");
                    }
                }

                if (machine.getDisk().size() > 0) {
                    CHDList.add(machineName);
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
                    if (machine.getDriver().getCocktail() != null && !machine.getDriver().getCocktail().isEmpty()) {
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
                if (cloneof == null || cloneof.isEmpty()) {
                    noClonesList.add(machineName);
                } else if (cloneof.length() > 1) {
                    clonesList.add(machineName);
                } else {
                    System.out.println("We shouldn't get here : ListBuilder.generateMFMLists() cloneof");
                    MFM.logger.addToList("We shouldn't get here : ListBuilder.generateMFMLists() cloneof");
                }


                if (machine.getDisplay().size() > 0 || !machine.getScreentype().isEmpty()) {
                    String screenType = null;
                    if (!machine.getScreentype().isEmpty()) {
                        screenType = machine.getScreentype();
                    } else if (machine.getDisplay().size() > 0) {
                        screenType = machine.getDisplay().get(0).getType();
                    }
                    if (screenType == null) {
                        screenType = "";
                    }
                    switch (screenType) {

                        case "raster":
                            rasterDisplayList.add(machineName);
                            break;

                        case "vector":
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

            runnableList = MAMEInfo.getRunnableMachines();
            // Which Categories have at least one Machine
            HashMap<String, ArrayList<String>> categoryGames = MAMEInfo.getCategoryMachines();
            for (Map.Entry<String, ArrayList<String>> categoryEntry : categoryGames.entrySet()) {
                if (!categoryEntry.getValue().isEmpty()) {
                    categoriesWithMachineList.add(categoryEntry.getKey());
                }
            }

            lists.put(ALL, allList);
            lists.put(BIOS, biosList);
            lists.put(DEVICES, devicesList);
            lists.put(VERTICAL, verticalsList);
            lists.put(HORIZONTAL, horizontalList);
            lists.put(CLONE, clonesList);
            lists.put(NO_CLONE, noClonesList);
            lists.put(COCKTAIL, cocktailList);
            lists.put(SIMULTANEOUS, simultaneousList);
            lists.put(RUNNABLE, runnableList);
            lists.put(ARCADE, arcadeList);
            lists.put(SYSTEMS, systemList);
            lists.put(RASTER, rasterDisplayList);
            lists.put(VECTOR, vectorDisplayList);
            lists.put(LCD, lcdDisplayList);
            lists.put(CHD, CHDList);
            lists.put(NO_IMPERFECT, noImpefectList);
            lists.put(CATEGORIES, categoriesWithMachineList);

            MFM_Data.getInstance().setStaticData(MFM_Constants.LISTS, lists);
        }
        // must be here have to ensure ALL list already exists
        languagesListsMap = getLanguageLists();
        return lists;
    }

    TreeMap<String, TreeSet<String>> getLanguageLists() {
        if (languagesListMap == null) {
            languagesListMap =
                    (TreeMap<String, TreeSet<String>>) MFM_Data.getInstance().getStaticData(MFM_Constants.LANGUAGESLISTS);
        }
        if (languagesListMap == null) {
            languagesListMap = loadLanguagesINI();
            if (languagesListMap == null && MFM.isSystemDebug()) {
                System.out.println("In MFMListGenerator failed to generate Languages lists");
            }
        }
        return languagesListMap;
    }
}
