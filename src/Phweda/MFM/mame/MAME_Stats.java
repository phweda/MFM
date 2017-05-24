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

package Phweda.MFM.mame;

import Phweda.MFM.MFM;
import Phweda.MFM.MFM_Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 5/20/2017
 * Time: 1:25 PM
 */
public class MAME_Stats {
    final Mame mame;
    final static LinkedHashMap<String, AtomicInteger> stats = new LinkedHashMap<String, AtomicInteger>(100);

    static final LinkedHashMap<String, String> STATS_KEY_MAP = Arrays.stream(new Object[][]{
            {"Total", "Total Machines"},
            {"All", "Machines possibly usable: Total - BIOS - Devices"},
            {"BIOS", "ROM BIOS (non-volatile firmware)"},
            {"CHD", "Machines with at least 1 CHD"},
            {"CHDs", "Total number of CHDs"},
            {"Device", "System hardware e.g. Controller Floppy Z80"},
            {"Display Raster", ""},
            {"Display Vector", ""},
            {"Display LCD", ""},
            {"Mechanical", "Mechanical Machine"},
            {"Orientation Cocktail", "Cocktail orientation"},
            {"Orientation Horizontal", "Horizontal orientation"},
            {"Orientation Vertical", "Vertical orientation"},
            {"Runnable", "Marked runnable in XML"},
            {"Sample", "Machine has audio sample(s)"},
            {"Status Good", "Machines marked good"},
            {"Status Imperfect", "Machines marked imperfect"},
            {"Status Preliminary", "Machines marked preliminary"},
            {"Usable", "Status good or imperfect"},
    }).collect(Collectors.toMap(kv -> (String) kv[0], kv -> (String) kv[1],
            (x, y) -> {
                throw new IllegalStateException(String.format("Duplicate key ", x));
            },
            LinkedHashMap<String, String>::new));

    // initialize
    {
        mame = MFM_Data.getInstance().getMame();
        STATS_KEY_MAP.forEach((key, value) -> {
            stats.put(key, new AtomicInteger());
        });
    }

    public void saveStats() {
        processStats();
        //    displayStats();
        saveStatstoFile();
    }

    private void saveStatstoFile() {
        try (PrintWriter pw = new PrintWriter(new File(MFM.MFM_LISTS_DIR +
                MFM_Data.getInstance().getDataVersion() + "_stats.csv"))) {
            pw.println(getHeaderLine());
        //    pw.println(getDescriptionLine());
            pw.println(getCountsLine());
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getHeaderLine() {
        StringBuilder stringBuilder = new StringBuilder();
        STATS_KEY_MAP.keySet().forEach(key -> {
            stringBuilder.append(key);
            stringBuilder.append(",");
        });

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

    private String getDescriptionLine() {
        StringBuilder stringBuilder = new StringBuilder();
        STATS_KEY_MAP.keySet().forEach(key -> {
            stringBuilder.append(STATS_KEY_MAP.get(key));
            stringBuilder.append(",");
        });

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

    private String getCountsLine() {
        StringBuilder stringBuilder = new StringBuilder();
        stats.keySet().forEach(key -> {
            stringBuilder.append(stats.get(key));
            stringBuilder.append(",");
        });

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
    }

    private void displayStats() {

    }

    private void processStats() {
        for (Machine machine : mame.getMachine()) {
            stats.get("Total").getAndIncrement();

            if (machine.getIsbios().equals(Machine.YES)) {
                stats.get("BIOS").getAndIncrement();
            } else if (machine.getIsdevice().equals(Machine.YES)) {
                stats.get("Device").getAndIncrement();
            } else {
                stats.get("All").getAndIncrement();
            }

            if (machine.getDisk() != null && !machine.getDisk().isEmpty()) {
                stats.get("CHD").getAndIncrement();
                stats.get("CHDs").getAndAdd(machine.getDisk().size());
            }

            if (machine.getDisplay() != null && !machine.getDisplay().isEmpty()) {
                switch (machine.getDisplay().get(0).getType()) {
                    case "raster":
                        stats.get("Display Raster").getAndIncrement();
                        break;
                    case "vector":
                        stats.get("Display Vector").getAndIncrement();
                        break;
                    case "lcd":
                        stats.get("Display LCD").getAndIncrement();
                        break;
                }
            }

            if (machine.getIsmechanical().equals(Machine.YES)) {
                stats.get("Mechanical").getAndIncrement();
            }

            if (machine.getIsVertical() != null) {
                if (!machine.getIsVertical().isEmpty() &&
                        machine.getIsVertical().equalsIgnoreCase(Machine.HORIZONTAL)) {
                    stats.get("Orientation Horizontal").getAndIncrement();
                } else if (!machine.getIsVertical().isEmpty() &&
                        machine.getIsVertical().equalsIgnoreCase(Machine.VERTICAL)) {
                    stats.get("Orientation Vertical").getAndIncrement();
                }
            }

            if (machine.getDriver() != null && machine.getDriver().getCocktail() != null &&
                    !machine.getDriver().getCocktail().isEmpty()) {
                stats.get("Orientation Cocktail").getAndIncrement();
            }

            if (machine.getRunnable() != null && machine.getRunnable().equals(Machine.YES)) {
                stats.get("Runnable").getAndIncrement();
            }

            if (machine.getSample() != null && machine.getSample().size() > 0) {
                stats.get("Sample").getAndIncrement();
            }

            if (machine.getDriver() != null) {
                switch (machine.getDriver().getStatus()) {
                    case "preliminary":
                        stats.get("Status Preliminary").getAndIncrement();
                        break;
                    case "good":
                        stats.get("Status Good").getAndIncrement();
                        stats.get("Usable").getAndIncrement();
                        break;
                    case "imperfect":
                        stats.get("Status Imperfect").getAndIncrement();
                        stats.get("Usable").getAndIncrement();
                        break;
                }
            }
        }
    }
}
