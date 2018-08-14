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
 * Date: 1/15/2018
 * Time: 10:57 AM
 */
public class ParseRootOnlyINI extends ParseTextFile {
    ParseRootOnlyINI(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    @Override
    protected void processLine(String line) {

        while (scanner.hasNext()) {

            /* TODO figure out the Regex : match left square bracket followed by zero
             * or more alpha and or numeric characters followed by right square bracket
             * That would greatly reduce the following
             */
            //scanner.next(Pattern.compile("[\[\d*?\w\*?]]"))
            if (line.contains("[") && !line.contains("FOLDER_SETTINGS")) {
                // Resolve the 'only has Root Folder' issue
                if (line.contains("ROOT_FOLDER")) {
                    do {
                        line = scanner.nextLine();
                    } while (line.matches("\\s*") || line.startsWith(";"));
                }


                while (scanner.hasNext()) {
                    if (line.length() > 0) {
                        map.put(line, "");
                    }
                    line = scanner.nextLine();
                }
            } else {
                line = scanner.nextLine();
            }
        }
    }
}
