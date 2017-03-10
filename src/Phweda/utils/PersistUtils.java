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

package Phweda.utils;

import Phweda.MFM.MFM;
import Phweda.MFM.datafile.Datafile;
import Phweda.MFM.mame.Mame;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

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
            // DEBUG
            System.out.println("PersistUtils.loadAnObjectPATH is  : " + path);
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

    public static Object retrieveJAXB(String path, Class _class) {
        if (MFM.isSystemDebug()) {
            System.out.println("In retrieveJAXB");
        }
        Object obj = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            obj = jaxbUnmarshaller.unmarshal(new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void saveJAXB(Object obj, String path, Class _class) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output optimized not readable
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(obj, new File(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
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
