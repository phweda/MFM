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

import Phweda.MFM.MAMEexe;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/20/2017
 * Time: 11:23 AM
 */

/**
 * Extracts MAME DTD from the -listxml output Doctype node and writes it, unformatted, to file.
 * See @Phweda.MFM.Utils.DTD_Formatter
 */
public class MAMEDocType {
    private File outputdir;
    // Used to store Doctype hashes
    private StringBuilder outputData = new StringBuilder();

    public MAMEDocType(List<Path> files, String outputDirIn) {
        outputdir = new File(outputDirIn);
        if (!outputdir.exists()) {
            try {
                Files.createDirectory(outputdir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        process(files);
    }

    private void process(List<Path> files) {
        for (Path file : files) {
            saveDTD(file);
        }
        System.out.println(outputData.toString());
        Path path = Paths.get(outputdir + File.separator + "MameDTDdata.csv");
        try {
            Files.write(path, outputData.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDTD(Path file) {
        String version = getMameVersion(file);
        File xmlFile = null;
        ArrayList<String> args = mameArgs(file);
        try {
            xmlFile = new File(file.getParent().toString() + File.separator + "mame" + version + ".xml");
            // Overwrite existing file
            if (xmlFile.exists()) {
                Files.delete(xmlFile.toPath());
                boolean create = xmlFile.createNewFile();
                if (!create) {
                    System.out.println("XML file delete/create failed!!!");
                    return;
                }
            }

            Process process = MAMEexe.run(args, xmlFile, false);
            int processResult = process.waitFor();
            if (processResult != 0) {
                System.out.println("Mame exe failed");
                return;
            }

        } catch (IOException e) { //    | InterruptedException | IOException
            e.printStackTrace();
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        if (xmlFile.exists()) {
            try {
                Document doc = getMameDoc(xmlFile);
                if (doc == null) {
                    System.out.println("Failed to get MAME Document.");
                    return;
                }
                DocumentType doctype = doc.getDoctype();
                //    System.out.println(version + "," + doctype.getInternalSubset().hashCode());
                outputData.append(version);
                outputData.append(",");
                outputData.append(doctype.getInternalSubset().hashCode());
                outputData.append("\n");

                printDTDtoFile(doctype.getInternalSubset(), Paths.get(outputdir.getPath(),
                        "mame" + version + ".dtd"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printDTDtoFile(String data, Path path) {

        Source xmlInput = new StreamSource(new StringReader("<!DOCTYPE mame [" + data + "]>\n<mame/>"));
        StreamResult xmlOutput = new StreamResult(new StringWriter());

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "testing.dtd");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        try {
            Files.write(path, data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //    System.out.println(xmlOutput.getWriter().toString());
    }


    // Note older Mame versions of -listxml did NOT support filtered output
    private ArrayList<String> mameArgs(Path file) {
        ArrayList<String> args = new ArrayList<>();
        MAMEexe.setBaseArgs(file.toString());
        args.add(MAMEexe.LISTXML);
        args.add("0*");
        return args;
    }

    private static Document getMameDoc(File file) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return doc;
    }


    // TODO find an easier way!
    private String getMameVersion(Path mameExe) {
        String version = null;
        File temp = null;
        MAMEexe.setBaseArgs(mameExe.toString());
        ArrayList<String> args = new ArrayList<>();
        args.add("-h");
        try {
            Path output = Files.createTempFile("output", ".txt");
            temp = output.toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Process process = MAMEexe.run(args, temp, false);
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        if (temp == null) {
            return "";
        }
        try (Scanner scanner = new Scanner(temp).useDelimiter("\\Z")) {
            String content = scanner.next();
            int index = content.indexOf('0');
            version = content.substring(index + 2, index + 5);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return version != null ? version.trim() : "";
    }

}
