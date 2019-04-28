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

package com.github.phweda.mfm.ui;

import com.github.phweda.mfm.*;
import com.github.phweda.mfm.mame.Machine;
import com.github.phweda.mfm.mame.Mame;
import com.github.phweda.utils.FileUtils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.github.phweda.mfm.MFMSettings.PLAYABLE;
import static com.github.phweda.mfm.MFM_Constants.HYPHEN;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 5/20/2017
 * Time: 1:25 PM
 */
class MAME_Stats {
    private final Mame mame;
    private final LinkedHashMap<String, AtomicInteger> stats = new LinkedHashMap<>(100);
    private final DecimalFormat percentFormater = new DecimalFormat("##.##%");

    private static final String TOTAL = "Total";
    private static final String DISPLAY_RASTER = "<html><center>Display<br>Raster</html>";
    private static final String DISPLAY_VECTOR = "<html><center>Display<br>Vector</html>";
    private static final String DISPLAY_LCD = "<html><center>Display<br>LCD</html>";
    private static final String ORIENTATION_COCKTAIL = "<html><center>Orientation<br>Cocktail</html>";
    private static final String ORIENTATION_HORIZONTAL = "<html><center>Orientation<br>Horizontal</html>";
    private static final String ORIENTATION_VERTICAL = "<html><center>Orientation<br>Vertical</html>";

    private static final String STATUS_GOOD = "<html><center>Status<br>Good</html>";
    private static final String STATUS_IMPERFECT = "<html><center>Status<br>Imperfect</html>";
    private static final String STATUS_PRELIMINARY = "<html><center>Status<br>Preliminary</html>";

    @SuppressWarnings("WeakerAccess")
    public static final String RUNNABLE = "Runnable";
    static final Map<String, String> STATS_KEY_MAP = Arrays.stream(new Object[][]{
            {TOTAL, "Total Machines"},
            {MFM_Constants.ALL, "Machines possibly usable: Total minus BIOS & Devices"},
            {MFMListBuilder.ARCADE, "Arcade games"},
            {"BIOS", "ROM BIOS (non-volatile firmware)"},
            {"CHD", "Machines with at least 1 CHD"},
            {"CHDs", "Total number of CHDs"},
            {Machine.DEVICE, "System hardware e.g. Controller Floppy Z80"},
            {DISPLAY_RASTER, DISPLAY_RASTER},
            {DISPLAY_VECTOR, DISPLAY_VECTOR},
            {DISPLAY_LCD, DISPLAY_LCD},
            {Machine.MECHANICAL, "Mechanical Machine"},
            {ORIENTATION_COCKTAIL, "Cocktail orientation"},
            {ORIENTATION_HORIZONTAL, "Horizontal orientation"},
            {ORIENTATION_VERTICAL, "Vertical orientation"},
            {RUNNABLE, "Marked runnable in XML"},
            {Machine.SAMPLE, "Machine has audio sample(s)"},
            {STATUS_GOOD, "Machines marked good"},
            {STATUS_IMPERFECT, "Machines marked imperfect"},
            {STATUS_PRELIMINARY, "Machines marked preliminary"},
            {MFMListBuilder.SYSTEMS, "Systems(MESS) - PCs, handhelds etc."},
            {PLAYABLE, "Machines marked Status good or imperfect"},
    }).collect(Collectors.toMap(kv -> (String) kv[0], kv -> (String) kv[1],
            (x, y) -> {
                throw new IllegalStateException("Duplicate key " + x);
            },
            LinkedHashMap<String, String>::new)); // In this case IDEA inspection is wrong

    private static final int SIXTY = 60;
    private static final int SEVENTY_FIVE = 75;
    private static final int ONE_NINETY = 190;
    private static final int FIFTEEN_HUNDRED = 1500;
    private static final int SIXTEEN_HUNDRED = 1600;

    MAME_Stats() {
        mame = MFM_Data.getInstance().getMame();
        STATS_KEY_MAP.forEach((key, value) -> stats.put(key, new AtomicInteger()));
        // fixme we have two definitions for All!! MFM_Constants.ALL and MFMListBuilder.ALL!!
        processStats(MFM_Data.getInstance().getDataVersion().contains(MFMListBuilder.ALL));
    }

    final void saveStats() {
        displayStats();
        saveStatstoFile();
    }

    private void saveStatstoFile() {
        try (PrintWriter pw = new PrintWriter(new File(MFM.getMfmListsDir() +
                MFM_Data.getInstance().getDataVersion() + "_stats.csv"), StandardCharsets.UTF_8.name())) {
            pw.println(getHeaderLine());
            pw.println(getCountsLine());
            pw.println(getTotalPercentsLine());
            pw.println(getAllPercentsLine());
            pw.flush();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String getHeaderLine() {
        StringBuilder stringBuilder = new StringBuilder(275);
        STATS_KEY_MAP.keySet().forEach(key -> {
            key = key.replaceAll(MFM_Wiki.XML_TAG_REGEX, " "); // Remove html tags
            stringBuilder.append(key);
            stringBuilder.append(FileUtils.COMMA_STRING);
        });

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(FileUtils.COMMA_STRING));
    }

    final String getDescriptionLine() {
        StringBuilder stringBuilder = new StringBuilder(520);
        STATS_KEY_MAP.keySet().forEach(key -> {
            stringBuilder.append(STATS_KEY_MAP.get(key));
            stringBuilder.append(FileUtils.COMMA_STRING);
        });
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(FileUtils.COMMA_STRING));
    }

    private String getCountsLine() {
        StringBuilder stringBuilder = new StringBuilder(100);
        stats.keySet().forEach(key -> {
            stringBuilder.append(stats.get(key));
            stringBuilder.append(FileUtils.COMMA_STRING);
        });
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(FileUtils.COMMA_STRING));
    }

    private String getTotalPercentsLine() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stats.keySet().forEach(key -> {
            stringBuilder.append(percentFormater.format(
                    stats.get(key).floatValue() / stats.get(TOTAL).floatValue()));
            stringBuilder.append(FileUtils.COMMA_STRING);
        });
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(FileUtils.COMMA_STRING));
    }

    private String getAllPercentsLine() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stats.keySet().forEach(key -> {
            stringBuilder.append(percentFormater.format(
                    stats.get(key).floatValue() / stats.get("All").floatValue()));
            stringBuilder.append(FileUtils.COMMA_STRING);
        });
        return stringBuilder.substring(0, stringBuilder.lastIndexOf(FileUtils.COMMA_STRING));
    }

    final Object[][] getStatsArray() {
        Object[][] statsArray = new Object[3][stats.size()];

        AtomicInteger total = stats.get(TOTAL);
        AtomicInteger all = stats.get(MFM_Constants.ALL);
        final AtomicInteger ai = new AtomicInteger(0);
        stats.forEach((key, value) -> {
            statsArray[0][ai.get()] = MFMController.decimalFormater.format(value);
            statsArray[1][ai.get()] = percentFormater.format(value.floatValue() / total.floatValue());
            statsArray[2][ai.getAndIncrement()] = percentFormater.format(value.floatValue() / all.floatValue());
        });
        statsArray[1][0] = TOTAL + '%';
        statsArray[2][0] = "All% ➵";
        statsArray[2][3] = HYPHEN;
        statsArray[1][5] = HYPHEN;
        statsArray[2][5] = HYPHEN;
        statsArray[2][6] = HYPHEN;

        return statsArray;
    }

    final Object[] getStatsHeaders() {
        return stats.keySet().toArray();
    }

    private void displayStats() {
        JTable statsTable = new JTable(getStatsArray(), getStatsHeaders()) {
            //Implement table header tool tips.
            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    @Override
                    public String getToolTipText(MouseEvent event) {
                        Point p = event.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        return STATS_KEY_MAP.get(columnModel.getColumn(index).getHeaderValue().toString());
                    }
                };
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
                }
                return comp;
            }
        };

        int[] widths = {SIXTY, SIXTY, SIXTY, SIXTY, SIXTY, SIXTY, SIXTY, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE,
                SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE,
                SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE, SEVENTY_FIVE}; // 21 = 1530

        for (int i = 0; i < statsTable.getColumnModel().getColumnCount(); i++) {
            statsTable.getColumnModel().getColumn(i).setMinWidth(widths[i]);
        }
        statsTable.setRowHeight(30);
        statsTable.getTableHeader().setPreferredSize(new Dimension(FIFTEEN_HUNDRED, 40));
        statsTable.setFillsViewportHeight(true);
        statsTable.setShowGrid(true);

        SwingUtilities.invokeLater(() -> {
                    JDialog dialog = new JDialog((Frame) null, "MAME STATS: " +
                            MFMSettings.getInstance().getDataVersion());
                    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    dialog.setIconImage(MFMUI_Setup.getMFMIcon().getImage());
                    dialog.setLocation(50, 250);
            dialog.setMinimumSize(new Dimension(SIXTEEN_HUNDRED, ONE_NINETY));
            dialog.setMaximumSize(new Dimension(SIXTEEN_HUNDRED, ONE_NINETY));
            dialog.setPreferredSize(new Dimension(SIXTEEN_HUNDRED, ONE_NINETY));
                    dialog.setLayout(new BorderLayout());
                    JScrollPane scrollPane = new JScrollPane(statsTable);
            scrollPane.setMinimumSize(new Dimension(FIFTEEN_HUNDRED, ONE_NINETY));
            scrollPane.setMaximumSize(new Dimension(FIFTEEN_HUNDRED, ONE_NINETY));
                    dialog.add(scrollPane, BorderLayout.CENTER);
                    dialog.pack();
                    dialog.setVisible(true);
                }
        );
    }

    private void processStats(boolean all) {
        for (Machine machine : mame.getMachine()) {
            if (all) {
                if (machine.getIsbios().equals(Machine.YES)) {
                    stats.get("BIOS").getAndIncrement();
                } else if (machine.getIsdevice().equals(Machine.YES)) {
                    stats.get(Machine.DEVICE).getAndIncrement();
                } else {
                    stats.get(MFM_Constants.ALL).getAndIncrement();
                }
            } else if (machine.getIsbios().equals(Machine.YES) || machine.getIsdevice().equals(Machine.YES)) {
                continue;
            } else {
                stats.get(MFM_Constants.ALL).getAndIncrement();
            }

            stats.get(TOTAL).getAndIncrement();

            if ((machine.getDisk() != null) && !machine.getDisk().isEmpty()) {
                stats.get("CHD").getAndIncrement();
                stats.get("CHDs").getAndAdd(machine.getDisk().size());
            }

            if ((machine.getDisplay() != null) && !machine.getDisplay().isEmpty()) {
                switch (machine.getDisplay().get(0).getType()) {
                    case MFM_Constants.RASTER:
                        stats.get(DISPLAY_RASTER).getAndIncrement();
                        break;
                    case MFM_Constants.VECTOR:
                        stats.get(DISPLAY_VECTOR).getAndIncrement();
                        break;
                    case "lcd":
                        stats.get(DISPLAY_LCD).getAndIncrement();
                        break;
                    default:
                        break;
                }
            }

            if (MFMPlayLists.getInstance().getPlayList(MFMListBuilder.ARCADE).contains(machine.getName())) {
                stats.get(MFMListBuilder.ARCADE).getAndIncrement();
            }

            if (MFMPlayLists.getInstance().getPlayList(MFMListBuilder.SYSTEMS).contains(machine.getName())) {
                stats.get(MFMListBuilder.SYSTEMS).getAndIncrement();
            }

            if (machine.getIsmechanical().equals(Machine.YES)) {
                stats.get(Machine.MECHANICAL).getAndIncrement();
            }

            if (machine.getIsVertical() != null) {
                if (!machine.getIsVertical().isEmpty() &&
                        machine.getIsVertical().equalsIgnoreCase(Machine.HORIZONTAL)) {
                    stats.get(ORIENTATION_HORIZONTAL).getAndIncrement();
                } else if (!machine.getIsVertical().isEmpty() &&
                        machine.getIsVertical().equalsIgnoreCase(Machine.VERTICAL)) {
                    stats.get(ORIENTATION_VERTICAL).getAndIncrement();
                }
            }

            if ((machine.getDriver() != null) && (machine.getDriver().getCocktail() != null) &&
                    !machine.getDriver().getCocktail().isEmpty()) {
                stats.get(ORIENTATION_COCKTAIL).getAndIncrement();
            }

            if ((machine.getRunnable() != null) && machine.getRunnable().equals(Machine.YES) &&
                    !machine.getIsbios().equals(Machine.YES)) {
                stats.get(RUNNABLE).getAndIncrement();
            }

            if ((machine.getSample() != null) && !machine.getSample().isEmpty()) {
                stats.get(Machine.SAMPLE).getAndIncrement();
            }

            if ((machine.getDriver() != null) && !machine.getIsbios().equals(Machine.YES)) {
                switch (machine.getDriver().getStatus()) {
                    case Machine.PRELIMINARY:
                        stats.get(STATUS_PRELIMINARY).getAndIncrement();
                        break;
                    case Machine.GOOD:
                        stats.get(STATUS_GOOD).getAndIncrement();
                        stats.get(PLAYABLE).getAndIncrement();
                        break;
                    case Machine.IMPERFECT:
                        stats.get(STATUS_IMPERFECT).getAndIncrement();
                        stats.get(PLAYABLE).getAndIncrement();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
