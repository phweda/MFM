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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/27/11
 * Time: 5:31 PM
 */
public class ParseCommandList extends ParseTextFile {
    private static Map<String, String> commands;
    private static String category;

    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     * @param mapIn    A Map of Maps with the command set Category as key
     */
    public ParseCommandList(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    @Override
    protected void processLine(String line) {
        /* Eliminate any lines not a Command Category '#' or Command '-' */
        if (line.length() < 3 || (line.charAt(0) != '-' && line.charAt(0) != '#' && line.charAt(0) != '*')) {
            return;
        }

        if (line.charAt(0) == '#' || line.charAt(0) == '*') {
            StringBuilder sb = new StringBuilder(line);
            sb.delete(0, 1); //Remove # and space
            category = sb.toString().trim();
            if (map.containsKey(category)) {
                commands = (Map<String, String>) map.get(category);
            } else {
                commands = new HashMap<String, String>(20);
                map.put(category, commands);
            }

        } else if (line.charAt(0) == '-') {
            /* Split at First whitespace */
            // TODO fix this REGEX returns empty first string - String[] strs = line.split("^(\\S*)");
            String[] strs = new String[2];
            if (!line.contains("/")) { // early MAME versions had short command switches
                int index = line.indexOf(' ');
                strs[0] = line.substring(0, index);
                strs[1] = line.substring(index + 1);
                commands.put(strs[0], strs[1].trim());
            } else {
                strs[0] = line.substring(0, 31);
                strs[1] = line.substring(32);
                commands.put(strs[0].trim(), strs[1].trim());
            }
        } else {
            /* We should never get here but ... */
            log("Invalid line. Unable to process.");
        }

    }
}