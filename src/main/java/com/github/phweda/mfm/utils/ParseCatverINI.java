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

import com.github.phweda.mfm.MFM;
import com.github.phweda.utils.ParseTextFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/3/11
 * Time: 2:06 PM
 */
public class ParseCatverINI extends ParseTextFile {
    /* Sort of redundant to have both */
    private boolean category = false;
    private boolean MAMEVersionAdded = false;

    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     */
    public ParseCatverINI(String fileName, Map mapIn) {
        super(fileName, mapIn);
        MFM.getLogger().addToList("Catver file name: " + fileName, true);
    }

    @Override
    protected void processLine(String line) {
        if (line.startsWith("[") || line.length() < 3) {
            if (line.startsWith("[Category]")) {
                category = true;
                // Not needed but just to be robust if they ever change the file format order
                MAMEVersionAdded = false;
            }
            if (line.startsWith("[VerAdded]")) {
                MAMEVersionAdded = true;
                category = false;
            }
            return;
        }
        //use a second Scanner to parse the content of each line
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter("=");
        if (lineScanner.hasNext()) {
            String key = lineScanner.next();
            String value = "";
            if (lineScanner.hasNext()) {
                value = lineScanner.next();
            }
            if (category) {
                ((HashMap) map.get("category")).put(key.trim(), value.trim());
            } else if (MAMEVersionAdded) {
                ((HashMap) map.get("version")).put(key.trim(), value.trim());
            } else {
                // WE SHOULD NEVER GET HERE BUT ...
                MFM.getLogger().addToList("ParseCatverINI We failed to find version or category");
            }
        }
        lineScanner.close();
    }
}
