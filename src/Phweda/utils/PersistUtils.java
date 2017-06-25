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

import Phweda.MFM.datafile.Datafile;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/4/11
 * Time: 3:47 PM
 */
public class PersistUtils {

    public static void saveAnObject(Object obj, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object loadAnObject(String path) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DO NOT close outputstream so other files may be added to zip from the caller
     *
     * @param obj
     * @param zipOutputStream
     * @param fileName
     */
    public static void saveAnObjecttoZip(Object obj, ZipOutputStream zipOutputStream, String fileName) {
        try {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            ObjectOutputStream oos = new ObjectOutputStream(zipOutputStream);
            oos.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object loadAnObjectFromZip(String zipPath, String fileName) throws IOException {
        try {
            ZipEntry zipEntry = new ZipEntry(fileName);
            ZipFile zipFile = new ZipFile(zipPath);

            ObjectInputStream ois = new ObjectInputStream(zipFile.getInputStream(zipEntry));
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveAnObjectXML(Object obj, String path) {
        try {
            // Serialize object into XML
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
                    new FileOutputStream(path)));
            encoder.writeObject(obj);
            encoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Object loadAnObjectXML(String path) throws FileNotFoundException {
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(path)));
            Object obj = decoder.readObject();
            decoder.close();
            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Non-validating
     *
     * @param path   path to input file
     * @param _class class of xml root object
     * @return root object
     */
    public static Object retrieveJAXB(String path, Class _class) {
        Object obj = null;
        /*
            Block parser from reaching out externally see:
            https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#SAXTransformerFactory
        */
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(),
                    new InputSource(new FileReader(new File(path))));
            JAXBContext jc = JAXBContext.newInstance(_class);
            Unmarshaller um = jc.createUnmarshaller();
            obj = um.unmarshal(xmlSource);
        } catch (JAXBException | FileNotFoundException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void saveJAXB(Object obj, String path, Class _class) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(obj, new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * DO NOT close outputstream so other files may be added to zip from the caller
     *
     * @param obj
     * @param zipOutputStream
     * @param fileName
     * @param _class
     */
    public static void saveJAXBtoZip(Object obj, ZipOutputStream zipOutputStream, String fileName, Class _class,
                                     boolean formatted) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);

            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);

            jaxbMarshaller.marshal(obj, zipOutputStream);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Object retrieveJAXBfromZip(String zipPath, String fileName, Class _class) {
        Object obj = null;
        try {
            ZipEntry zipEntry = new ZipEntry(fileName);
            ZipFile zipFile = new ZipFile(zipPath);

            JAXBContext jaxbContext = JAXBContext.newInstance(_class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            obj = jaxbUnmarshaller.unmarshal(zipFile.getInputStream(zipEntry));
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void saveXMLDoctoFile(Document doc, DocumentType documentType, String path)
            throws TransformerException {
        // Save DOM XML doc to File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        doc.setXmlVersion("1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, documentType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, documentType.getSystemId());

        transformer.transform(new DOMSource(doc), new StreamResult(new File(path)));
    }

    // TODO do not like the mixed methodologies here // FIXME: 11/25/2016
    public static void saveDATtoFile(Datafile datafile, String path)
            throws ParserConfigurationException, JAXBException, TransformerException, FileNotFoundException {

        JAXBContext jc = JAXBContext.newInstance(Datafile.class);

        // Create the Document
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        DocumentType docType = getDocumentType(document);

        // Marshal the Object to a Document formatted so human readable
        Marshaller marshaller = jc.createMarshaller();
/*
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, docType.getSystemId());
*/

        marshaller.marshal(datafile, document);
        document.setXmlStandalone(true);
        // NOTE could output with marshaller but cannot set DTD document type
/*
        OutputStream os = new FileOutputStream(path);
        marshaller.marshal(datafile, os);
*/

        // Output the Document
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //    transformerFactory.setAttribute("indent-number", "4");
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //   transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "8");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }

    private static DocumentType getDocumentType(Document doc) {
        DOMImplementation domImpl = doc.getImplementation();
        // <!DOCTYPE datafile PUBLIC "-//Logiqx//DTD ROM Management Datafile//EN" "http://www.logiqx.com/Dats/datafile.dtd">
        DocumentType doctype = domImpl.createDocumentType("datafile",
                "-//Logiqx//DTD ROM Management Datafile//EN",
                "http://www.logiqx.com/Dats/datafile.dtd");
        return doctype;
    }

}
