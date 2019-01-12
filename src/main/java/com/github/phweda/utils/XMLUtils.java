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

package com.github.phweda.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: phweda
 * Date: 11/18/11
 * Time: 4:27 PM
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public class XMLUtils {

    private static DocumentBuilder db;

    /* Static initializer for class fields  */
    static {
        /* get the XML Document factory */
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        /* Use factory to get an instance of document builder */
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private XMLUtils() {
    }

    public static Document parseXmlFile(File file) {
        //create the document object
        Document dom = null;
        try {
            /* Get DOM representation of the XML file */
            dom = db.parse(file);
            db.reset();
        } catch (SAXException | IOException exc) {
            exc.printStackTrace();
        }
        return dom;
    }


    public static Document parseXmlFile(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        //create the document object
        Document dom = null;
        try {
            //Get DOM representation of the XML file
            dom = db.parse(inputStream);
            db.reset();
        } catch (SAXException | IOException exc) {
            exc.printStackTrace();
        }
        return dom;
    }

    public static boolean validate(File inputFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true); //default value is false
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.parse(inputFile);
        } catch (SAXException | ParserConfigurationException | IOException exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }
}
