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

import Phweda.utils.ParseTextFile;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/2/11
 * Time: 10:08 PM
 */
public class ParseHistoryDAT extends ParseTextFile {
    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file
     */
    public ParseHistoryDAT(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    /* TODO test performance comparing usage of Strings vs Scanner */
    @Override
    protected void processLine(String line) {

        StringBuilder historyText = new StringBuilder();

        /* Find next line starting with $info */
        while (!line.startsWith("$info") && scanner.hasNext()) {
            line = scanner.nextLine();
        }
        /* Are we at EOF? */
        if (!scanner.hasNext()) {
            return;
        }
        // $info line has one or more game names
        line = line.substring(6, line.length() - 1);
        String[] games = line.trim().split("[,]");
        /* Find next line starting with $bio & GOTO next line */
        do {
            line = scanner.nextLine();
        } while (!line.startsWith("$bio"));
        line = scanner.nextLine(); // skip $bio

        /* Until line starting with $end */
        do {
            // TODO do we need to add an endline here?
            // YES But it appears to give us two!! ??
            historyText.append(line).append('\n');
            line = scanner.nextLine();
        } while (!line.startsWith("$end"));

        for (String game : games) {
            map.put(game, historyText.toString());
        }

    }
}
