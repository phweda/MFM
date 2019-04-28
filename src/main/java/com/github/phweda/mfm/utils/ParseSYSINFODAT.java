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

package com.github.phweda.mfm.utils;

import com.github.phweda.utils.ParseTextFile;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/16/2015
 * Time: 8:43 PM
 */
public class ParseSYSINFODAT extends ParseTextFile {

    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     */
    public ParseSYSINFODAT(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    @Override
    protected void processLine(String line) {
        StringBuilder infoText = new StringBuilder();

        /* Find next line starting with $info */
        while (!line.startsWith("$info") && scanner.hasNext()) {
            line = scanner.nextLine();
        }
        // $info line may have multiple comma separated systems and may end with a comma

        if (line.startsWith("$info") && line.length() > 6) {
            line = line.substring(6);
            if (line.endsWith(",")) {
                line = line.substring(0, line.length() - 2);
            }
        }

        String[] systems = line.split("[,]");

        /* Find next line starting with $bio & GOTO next line */
        do {
            /* Are we at EOF? */
            if (!scanner.hasNext()) {
                return;
            }
            line = scanner.nextLine();
        } while (!line.startsWith("$bio") && scanner.hasNext());
        line = scanner.nextLine(); // skip $bio

        /* Until line starting with $end */
        do {
            infoText.append(line).append('\n');
            line = scanner.nextLine();
        } while (!line.startsWith("$end"));

        for (String system : systems) {
            map.put(system, infoText.toString());
        }

    }
}
