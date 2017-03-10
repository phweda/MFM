/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

import Phweda.MFM.MFM;
import Phweda.utils.ParseTextFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/6/2015
 * Time: 9:23 PM
 */
public class ParsenPlayerINI extends ParseTextFile {
    public ParsenPlayerINI(String fileName, Map nplayers) {
        super(fileName, nplayers);
    }

    @Override
    protected void processLine(String line) {
        if (!line.contains("=") || line.isEmpty()) {
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
            map.put(key, value);
        }
        //no need to call lineScanner.close(), since the source is a String
    }
}

