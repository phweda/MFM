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

package Phweda.MFM.UI;

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFM;
import Phweda.MFM.MFMPlayLists;
import Phweda.MFM.MFM_Data;
import Phweda.MFM.mame.Machine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.SortedSet;


class MFM_Wiki {

    static final String XML_TAG_REGEX = "<.*?>";

    private static final String TABLE_DEFINITION =
            "{| class=\"wikitable\" style=\"margin-left: 5px; margin-right: auto; border-style: solid; border-width: 3px\"\n";
    private static final String ROW_SEPARATER = "|- ";
    private static final String ROW = "| ";
    private static final String ROW_RIGHT = "| style=\"text-align:right;\" | ";

    private static final String MACHINE_TABLE_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 270px;\" | Game(System) Name\n" +
                    "! style=\"width: 110px;\" | Machine Name\n" +
                    "! style=\"width: 120px;\" | Manufacter\n" +
                    "! style=\"width: 45px;\" | Year\n" +
                    "! style=\"width: 240px;\" | Catgegory\n" +
                    "! style=\"width: 75px;\" | MAME Version\n" +
                    "! style=\"width: 75px;\" | Status\n" +
                    "|+MAME Data &rarr;  Created by [[Mame File Manager]]\n" + ROW_SEPARATER;

    private static final String STATS_TABLE_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 120px;\" | List\n" +
                    "! style=\"width: 100px;text-align:right;\" | Machines\n" +
                    "! style=\"width: 100px;text-align:right;\" | % of Total\n" +
                    "! style=\"width: 100px;text-align:right;\" | % of All\n" +
                    "|+MAME List Statistics ";

    private static final String STATS_LEGEND_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 50px;\" | List\n" +
                    "! style=\"width: 100px;\" | Description\n" +
                    "|+MAME List Statistics LEGEND\n";

    private static final String TABLE_FOOTER = "|}";

    private MFM_Wiki() { // To cover implicit constructor squid:S1118
    }

    /**
     * Creates a wikimedia table of list data
     * Saves to text file
     *
     * @param list of machines to dump data
     */
    static void listtoWikiTable(String list) {

        try (PrintWriter printWriter = new PrintWriter(MFM.MFM_LISTS_DIR + list + "_" +
                MFM_Data.getInstance().getPublishableDataVersion() + ".wiki")) {
            SortedSet<String> machineSet = MFMPlayLists.getInstance().getPlayList(list);

            printWriter.println(MACHINE_TABLE_HEADER);

            for (String machineName : machineSet) {
                Machine machine = MAMEInfo.getMachine(machineName);
                if (machine == null) {
                    continue;
                }
                printWriter.println(ROW + machine.getDescription());
                printWriter.println(ROW + machine.getName());
                printWriter.println(ROW + machine.getManufacturer());
                printWriter.println(ROW + machine.getYear());
                printWriter.println(ROW + machine.getCategory());
                printWriter.println(ROW + machine.getMAMEVersionAdded());
                printWriter.println(ROW + machine.getValueOf("Status"));
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
    }

    static void statstoWikiTable(Object[][] stats, Object[] headers) {
        try (PrintWriter printWriter = new PrintWriter(MFM.MFM_LISTS_DIR + "Stats_" +
                MFM_Data.getInstance().getPublishableDataVersion() + ".wiki")) {

            printWriter.println(STATS_TABLE_HEADER + MFM_Data.getInstance().getPublishableDataVersion() + "<br/>"
                    + " Created by [[Mame File Manager]]\n" + ROW_SEPARATER);
            printWriter.println(ROW + "TOTAL");
            printWriter.println(ROW_RIGHT + stats[0][0].toString());
            printWriter.println(ROW_RIGHT + "100%");
            printWriter.println(ROW + "");
            printWriter.println(ROW_SEPARATER);
            // skip first row it is row headers for displayed version
            for (int i = 1; i < headers.length; i++) {
                printWriter.println(ROW + headers[i].toString().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW_RIGHT + stats[0][i].toString());
                printWriter.println(ROW_RIGHT + stats[1][i].toString());
                printWriter.println(ROW_RIGHT + stats[2][i].toString());
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
        statsWikiLegend();
    }

    private static void statsWikiLegend() {
        try (PrintWriter printWriter = new PrintWriter(MFM.MFM_LISTS_DIR + "STATS_LEGEND.wiki")) {
            printWriter.println(STATS_LEGEND_HEADER + ROW_SEPARATER);
            for (Map.Entry<String, String> entry : MAME_Stats.STATS_KEY_MAP.entrySet()) {
                printWriter.println(ROW + entry.getKey().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW + entry.getValue().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
