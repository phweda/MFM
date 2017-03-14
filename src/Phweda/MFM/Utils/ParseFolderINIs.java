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

import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/21/11
 * Time: 12:12 AM
 */
public class ParseFolderINIs extends ParseTextFile {
/*
    When I took the static out we finished but got no UI without any exceptions.
*/
//    private static boolean root = false;

    public ParseFolderINIs(String fileName, Map mapIn) {
        super(fileName, mapIn);
    }

    /*
    * We just do all the work here
    *
    * TODO figure out how to handle files like Favorites.ini that do not have any [] after ROOT_FOLDER
    */
    @Override
    protected void processLine(String line) {

        // Now the meat of it
        String MAMEfolder = "";
        String previousLine = null;
        TreeSet<String> machines = null;
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

                    //  Get folder game set
                    // Need the IF for empty [] folder name
                    if (line.contains("[") && line.length() > 2) {
                        MAMEfolder = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
                    }

                    if (line.matches("\\w+")) {
                        previousLine = line;
                    }
                } else {
                    MAMEfolder = line.substring(line.indexOf('[') + 1, line.indexOf(']'));
                }

                if (previousLine == null) {
                    line = scanner.nextLine();
                } else {
                    line = previousLine;
                    previousLine = null;
                }

                machines = new TreeSet<String>();
                while (scanner.hasNext() && !line.contains("[")) {
                    if (line.length() > 0 && !line.contains("[")) {
                        machines.add(line);
                    }
                    line = scanner.nextLine();
                }
            } else {
                line = scanner.nextLine();
                continue;
            }
            // Do we need to trim the vector ??
            map.put(MAMEfolder, machines);
        }
    }
    //no need to call lineScanner.close(), since the source is a String
}

