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

package Phweda.MFM.Utils;

import Phweda.utils.ParseTextFile;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/3/2014
 * Time: 7:43 PM
 */
public class ParseExtrasInfoDATs extends ParseTextFile {
    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file
     */
    public ParseExtrasInfoDATs(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    @Override
    protected void processLine(String line) {

        StringBuilder infoText = new StringBuilder();

        /* Find next line starting with $info */
        while (!line.startsWith("$info") && scanner.hasNext()) {
            line = scanner.nextLine();
        }
        // $info line has one game
        String game = line.substring(6, line.length());

        /* Find next line starting with $mame & GOTO next line */
        do {
            line = scanner.nextLine();
        } while (!line.startsWith("$mame") && scanner.hasNext());

        /* Are we at EOF? */
        if (!scanner.hasNext()) {
            return;
        }
        line = scanner.nextLine(); // skip $mame

        /* Until line starting with $end */
        do {
            // TODO do we need to add an endline here?
            // YES But it appears to give us two!! ??
            infoText.append(line).append('\n');
            line = scanner.nextLine();
        } while (!line.startsWith("$end"));

        map.put(game, infoText.toString());
    }

}
