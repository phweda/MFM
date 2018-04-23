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

import Phweda.utils.XMLUtils;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/23/2017
 * Time: 10:21 AM
 */

/**
 * Backs up raw DTD file then formats for human readability *
 *
 */
public class DTD_Formatter {

    static File inputFile;
    Path inputBackupFile;

    private static String currentElement;
    private static HashMap<String, Integer> elements = new HashMap<String, Integer>();

    public DTD_Formatter(File fileIn) {
        inputFile = fileIn;
        boolean valid = XMLUtils.validate(inputFile);
        if(!valid){
            JOptionPane.showMessageDialog(null, "DTD_Formatter received a non-valid XML document\n" +
                    "File: " + inputFile.getAbsolutePath(), "XML Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        inputBackupFile = Paths.get(inputFile.getAbsolutePath() + "_Backup");
        try {
            Path path = Files.copy(fileIn.toPath(), inputBackupFile, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        format();
    }

    private void format() {

        try {
            // Use scanner to slurp entire file contents
            String content = new Scanner(inputFile).useDelimiter("\\Z").next();
            String path = inputFile.getAbsolutePath();
            inputFile.delete();
            Scanner scanner = new Scanner(content);
            scanner.useDelimiter("\\R");
            try (PrintWriter pw = new PrintWriter(new File(path))) {
                while (scanner.hasNext()) {
                    String line = scanner.next();
                    // System.out.println(scanner.next());
                    if (line.startsWith("<!ELEMENT")) {
                        String currentElement = processElement(line);
                        for (int i = 0; i < ((Integer) elements.get(currentElement)); i++) {
                            pw.print("\t");
                        }
                        pw.println(line);
                    } else if (line.startsWith("<!ATTLIST")) {
                        // output +1 tab past current element
                        for (int i = 0; i < ((Integer) elements.get(getElementName((line)))) + 1; i++) {
                            pw.print("\t");
                        }
                        pw.println(line);
                    }
                    pw.flush();
                }
            } catch (FileNotFoundException exc) {
                exc.printStackTrace();
            }
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
    }

    private String processElement(String line) {
        currentElement = getElementName(line);
        if (elements.isEmpty()) {
            elements.put(currentElement, new Integer(0));
        }

        for (String element : getSubElements(line)) {
            elements.put(element, elements.get(currentElement) + 1);
        }
        return currentElement;
    }

    private ArrayList<String> getSubElements(String line) {
        ArrayList<String> elements = new ArrayList<String>();
        // Return if none. See http://xmlwriter.net/xml_guide/element_declaration.shtml#DeclareMixed
        if (!line.contains("(") || (line.contains("#") && !line.contains("|"))) {
            return elements;
        }
        String input = line.substring(line.indexOf('(') + 1, line.indexOf(")") - 1);
        // Common case
        if (input.contains(",")) {
            Scanner scanner = new Scanner(input);
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                elements.add(stripSpecialCharacters(scanner.next()));
            }
        } else if (!line.contains("|")) {
            elements.add(stripSpecialCharacters(input));
        } else {
            // Punt for now do not think MAME has mixed content XML
        }
        return elements;
    }

    private String getElementName(String line) {
        String result = line.substring(line.indexOf(' ') + 1);
        result = result.substring(0, result.indexOf(' '));
        return result;
    }

    private String stripSpecialCharacters(String input) {
        Pattern regex = Pattern.compile("[?*+|,$]");
        Matcher matcher = regex.matcher(input);
        if (matcher.find()) {
            // strip last char
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

}
