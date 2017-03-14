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

package Phweda.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/24/11
 * Time: 3:16 PM
 */
public class ParseTextFile {

    // fixme Fix these do not work as expected!!!
    protected static final String ALPHANUM_REGEX = "^[a-zA-Z0-9]";
    protected static final String NUM_REGEX = "^[0-9]";
    protected static final String ALPHA_REGEX = "^[a-zA-Z]";
    protected Scanner scanner;
    protected Map map;
    private File file = null;


    /**
     * Constructor.
     *
     * @param fileName full name of an existing, readable file.
     */
    public ParseTextFile(String fileName, Map mapIn) {
        file = new File(fileName);
        map = mapIn;
    }

    protected static void log(Object object) {
        System.out.println(String.valueOf(object));
    }

    /**   */
    public Map processFile() throws FileNotFoundException {
        //FileReader is used, not File, since File is not Closeable
        scanner = new Scanner(new FileReader(file));
        try {
            //Scanner to get each line
            while (scanner.hasNextLine()) {
                processLine(scanner.nextLine());
            }
        } finally {
            //Close the underlying stream
            scanner.close();
        }
        return map;
    }

    /*
     Overridable method for processing lines in different ways.

    */
    protected void processLine(String line) {
/* TODO this is horrible waste of resources! */
        //use a second Scanner to parse the content of each line
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("=");
        if (scanner.hasNext()) {
            String key = scanner.next();
            String value = "";
            if (scanner.hasNext()) {
                value = scanner.next();
            }
            log("Key is : " + quote(key.trim()) + ", and Value is : " + quote(value.trim()));
        } else {
            log("Empty or invalid line. Unable to process.");
        }
        //no need to call scanner.close(), since the source is a String
    }

    private String quote(String text) {
        String QUOTE = "'";
        return QUOTE + text + QUOTE;
    }

}

