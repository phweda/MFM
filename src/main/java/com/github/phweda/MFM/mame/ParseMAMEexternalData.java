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

package com.github.phweda.MFM.mame;

import com.github.phweda.MFM.MFM;
import com.github.phweda.MFM.MachineControllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.phweda.MFM.mame.Machine.*;

/**
 * Add data from MAME external resources
 */
final class ParseMAMEexternalData {

    private static final Map<String, ArrayList<String>> categoryMachineListMap = new HashMap<>(20000);
    private static ArrayList<String> categoriesList;
    private static final MachineControllers machineControllers = MachineControllers.getInstance();

    private ParseMAMEexternalData() {
    }

    static Map<String, ArrayList<String>> getCategoryMachineListMap() {
        return categoryMachineListMap;
    }

    static List<String> getCategoriesList() {
        return categoriesList;
    }

    static MachineControllers getMachineControllers() {
        return machineControllers;
    }

    static void addNonMAMEinfo(Machine machine) {
        String machineName = machine.getName();
        machine.setHistory(LoadNonMAMEresources.getHistoryDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getMAMEInfoDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getMESSInfoDAT().get(machineName));
        machine.setInfo(LoadNonMAMEresources.getSYSInfoDAT().get(machineName));
        machine.setMAMEVersionAdded(LoadNonMAMEresources.getVersion().get(machineName));

        Map<String, String> machinetoCategoryMap = LoadNonMAMEresources.getMachinetoCategoryMap();
        if (machinetoCategoryMap.containsKey(machineName)) {
            machine.setCategory(machinetoCategoryMap.get(machineName));
            if (!categoryMachineListMap.containsKey(machinetoCategoryMap.get(machineName))) {
                categoryMachineListMap.put(machinetoCategoryMap.get(machineName), new ArrayList<>(128));
            }
            categoryMachineListMap.get(machinetoCategoryMap.get(machineName)).add(machineName);
        }

        if (LoadNonMAMEresources.getNplayers().containsKey(machineName)) {
            machine.setNplayerEntry(LoadNonMAMEresources.getNplayers().get(machineName));
        }
        System.out.println(machineName);
    }

    static boolean isRunnable(Machine machine) {
        // Is it runnable?  skip if it is a Device
        // With 0.85 MFM release we now go back to very old Mame versions where Driver was not present(BIOSes) -
        // just assume those are runnable
        return (((machine != null) && !machine.getIsdevice().equals(YES) &&
                ((machine.getDriver() == null) || machine.getDriver().getStatus().equals(GOOD) ||
                        machine.getDriver().getStatus().equals(IMPERFECT))
                // Not bios or no bios value ""
                && (machine.getIsbios().equals(NO) || machine.getIsbios().isEmpty())));
    }

    static void findCategories() {
        categoriesList = new ArrayList<>(320);
        // Machines are the key with Category the value in MachinetoCategoryMap
        Map<String, String> machinetoCategoryMap = LoadNonMAMEresources.getMachinetoCategoryMap();
        for (Map.Entry<String, String> stringStringEntry : machinetoCategoryMap.entrySet()) {
            if (!categoriesList.contains(stringStringEntry.getValue())) {
                String key = stringStringEntry.getValue();
                categoriesList.add(key);
                categoryMachineListMap.put(key, new ArrayList<>(128));
            }
        }
        // NOTE leave in to show running in shell window
        System.out.println(categoriesList);
        if (MFM.isDebug()) {
            MFM.getLogger().addToList(categoriesList.toString());
        }
    }

    static void findControls(Machine machine) {
        // Has input and control(s)
        if ((machine.getInput() != null) &&
                (machine.getInput().getControl() != null) && !machine.getInput().getControl().isEmpty()) {
            for (Control control : machine.getInput().getControl()) {
                String type = control.getType();
                if ((control.getWays() == null) || control.getWays().isEmpty()) {
                    machineControllers.addMachine(type, machine.getName());
                } else {
                    List<String> controlArgs = new ArrayList<>(5);
                    controlArgs.add(type);
                    controlArgs.add(control.getWays());
                    if (control.getWays2() != null) {
                        controlArgs.add(control.getWays2());
                    }
                    if (control.getWays3() != null) {
                        controlArgs.add(control.getWays3());
                    }
                    machineControllers.addMachine(controlArgs, machine.getName());
                    controlArgs.clear();
                }
            }
        }
    }
}
