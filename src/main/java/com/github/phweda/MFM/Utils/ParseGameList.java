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

package com.github.phweda.MFM.Utils;

import com.github.phweda.utils.ParseTextFile;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/11
 * Time: 9:49 PM
 */
public class ParseGameList extends ParseTextFile {
    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     */
    public ParseGameList(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    @Override
    protected void processLine(String line) {
        //use a second Scanner to parse the content of each line
        try (Scanner scanner = new Scanner(line)) {
            scanner.useDelimiter("\"");
            if (scanner.hasNext()) {
                String key = scanner.next();
                String value = "";
                if (scanner.hasNext()) {
                    value = scanner.next();
                }
                map.put(key.trim(), value.trim());
            } else {
                log("Empty or invalid line. Unable to process.");
            }
        }
    }
}
