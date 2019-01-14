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

import com.github.phweda.mfm.MAMEInfo;
import com.github.phweda.mfm.MFM;
import com.github.phweda.mfm.MFMPlayLists;
import com.github.phweda.mfm.MFM_Data;
import com.github.phweda.mfm.mame.Machine;
import com.github.phweda.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedSet;


final class MFM_Wiki {

    static final String XML_TAG_REGEX = "<.*?>";
    private static final String WIKIPEDIA_ORG_WIKI_URL = "https://en.wikipedia.org/wiki/";
    private static final String TABLE_DEFINITION =
            "{| class=\"wikitable\" style=\"margin-left: 5px; margin-right: auto; " +
                    "border-style: solid; border-width: 3px\"" + FileUtils.NEWLINE;
    private static final String ROW_SEPARATER = "|- ";
    private static final String ROW = "| ";
    private static final String ROW_RIGHT = "| style=\"text-align:right;\" | ";
    private static final String SPACE = " ";
    private static final String SINGLE_QUOTE = "'";

    private static final String MACHINE_TABLE_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 270px;\" | Game(System) Name" + FileUtils.NEWLINE +
                    "! style=\"width: 110px;\" | Machine Name" + FileUtils.NEWLINE +
                    "! style=\"width: 120px;\" | Manufacter" + FileUtils.NEWLINE +
                    "! style=\"width: 45px;\" | Year" + FileUtils.NEWLINE +
                    "! style=\"width: 240px;\" | Catgegory" + FileUtils.NEWLINE +
                    "! style=\"width: 75px;\" | MAME Version" + FileUtils.NEWLINE +
                    "! style=\"width: 75px;\" | Status" + FileUtils.NEWLINE +
                    "|+MAME Data &rarr;  Created by [[Mame File Manager]]" + FileUtils.NEWLINE + ROW_SEPARATER;

    private static final String STATS_TABLE_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 120px;\" | List" + FileUtils.NEWLINE +
                    "! style=\"width: 100px;text-align:right;\" | Machines" + FileUtils.NEWLINE +
                    "! style=\"width: 100px;text-align:right;\" | % of Total" + FileUtils.NEWLINE +
                    "! style=\"width: 100px;text-align:right;\" | % of All" + FileUtils.NEWLINE +
                    "|+MAME List Statistics ";

    private static final String STATS_LEGEND_HEADER =
            TABLE_DEFINITION +
                    "! style=\"width: 50px;\" | List" + FileUtils.NEWLINE +
                    "! style=\"width: 100px;\" | Description" + FileUtils.NEWLINE +
                    "|+MAME List Statistics LEGEND" + FileUtils.NEWLINE;

    private static final String TABLE_FOOTER = "|}";

    private MFM_Wiki() { // To cover implicit constructor squid:S1118
    }

    /**
     * Creates a wikimedia table of list data
     * Saves to text file
     *
     * @param list of machines to dump data
     */
    @SuppressWarnings("ContinueStatement")
    static void listtoWikiTable(String list) {

        try (PrintWriter printWriter = new PrintWriter(MFM.getMfmListsDir() + list + '_' +
                MFM_Data.getInstance().getPublishableDataVersion() + ".wiki", StandardCharsets.UTF_8.name())) {
            SortedSet<String> machineSet = MFMPlayLists.getInstance().getPlayList(list);

            printWriter.println(MACHINE_TABLE_HEADER);

            for (String machineName : machineSet) {
                Machine machine = MAMEInfo.getMachine(machineName);
                if (machine == null) {
                    continue;
                }
                printWriter.println(ROW + getDescription(machine.getDescription()));
                printWriter.println(ROW + machine.getName());
                printWriter.println(ROW + machine.getManufacturer());
                printWriter.println(ROW + machine.getYear());
                printWriter.println(ROW + machine.getCategory());
                printWriter.println(ROW + machine.getMAMEVersionAdded());
                printWriter.println(ROW + machine.getValueOf(Machine.STATUS_CAPS));
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (FileNotFoundException | UnsupportedEncodingException exc) {
            exc.printStackTrace();
        }
    }

    static void statstoWikiTable(Object[][] stats, Object[] headers) {
        try (PrintWriter printWriter = new PrintWriter(MFM.getMfmListsDir() + "Stats_" +
                MFM_Data.getInstance().getPublishableDataVersion() + ".wiki", StandardCharsets.UTF_8.name())) {

            printWriter.println(STATS_TABLE_HEADER + MFM_Data.getInstance().getPublishableDataVersion() + "<br/>"
                    + " Created by [[Mame File Manager]]" + FileUtils.NEWLINE + ROW_SEPARATER);
            printWriter.println(ROW + "TOTAL");
            printWriter.println(ROW_RIGHT + stats[0][0]);
            printWriter.println(ROW_RIGHT + "100%");
            printWriter.println(ROW + "");
            printWriter.println(ROW_SEPARATER);
            // skip first row it is row headers for displayed version
            int length = headers.length;
            for (int i = 1; i < length; i++) {
                printWriter.println(ROW + headers[i].toString().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW_RIGHT + stats[0][i]);
                printWriter.println(ROW_RIGHT + stats[1][i]);
                printWriter.println(ROW_RIGHT + stats[2][i]);
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (FileNotFoundException | UnsupportedEncodingException exc) {
            exc.printStackTrace();
        }
        statsWikiLegend();
    }

    private static void statsWikiLegend() {
        try (PrintWriter printWriter = new PrintWriter(MFM.getMfmListsDir() + "STATS_LEGEND.wiki",
                StandardCharsets.UTF_8.name())) {
            printWriter.println(STATS_LEGEND_HEADER + ROW_SEPARATER);
            for (Map.Entry<String, String> entry : MAME_Stats.STATS_KEY_MAP.entrySet()) {
                printWriter.println(ROW + entry.getKey().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW + entry.getValue().replaceAll(XML_TAG_REGEX, " "));
                printWriter.println(ROW_SEPARATER);
            }
            printWriter.println(TABLE_FOOTER);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a lookup to check for a wikipedia.com entry
     *
     * @param description Machine's long name
     * @return Description and if wikipedia entry added format and URL of that entry
     */
    private static String getDescription(String description) {
        // Strip from an open parenthesis ( and trim
        String machineName = description.contains("(") ?
                description.substring(0, description.indexOf('(')).trim() : description.trim();

        // Strip from an / and trim
        if (machineName.contains(String.valueOf(FileUtils.SLASH))) {
            machineName = machineName.substring(0, description.indexOf(FileUtils.SLASH)).trim();
        }

        // Strip from an ' and trim unless it starts with the quote
        if (!machineName.startsWith(SINGLE_QUOTE) && machineName.contains(SINGLE_QUOTE)) {
            machineName = machineName.substring(0, description.indexOf(SINGLE_QUOTE)).trim();
        }

        if (machineName.contains(SPACE)) {
            machineName = machineName.replaceAll(SPACE, "_");
        }

        // If the URL exists return bracketed with the URL
        return urlExists(machineName) ?
                ('[' + WIKIPEDIA_ORG_WIKI_URL + machineName + SPACE + description + ']') : description;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private static boolean urlExists(String machineName) {
        try {
            URL url = new URL(WIKIPEDIA_ORG_WIKI_URL + machineName);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            return (huc.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException e) { // we ignore the exception
            return false;
        }
    }
}
