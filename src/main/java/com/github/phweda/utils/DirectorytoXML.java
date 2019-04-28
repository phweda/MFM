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

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 3/18/12
 * Time: 4:20 PM
 */
@SuppressWarnings("WeakerAccess")
public final class DirectorytoXML extends SimpleFileVisitor<Path> {
    private Path directory;
    private Document directoryDocument;
    private File outputFile;
    private Element currentDir;
    private Comment comment;
    private Map<String, String> suffixes = new TreeMap<>();
    private final EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
    private final DecimalFormat df = new DecimalFormat("###,##0.00");

    /**
     * Creates XML representation of a folder and all descendant files
     * Caution the caller must clear suffixes if a previous call was made to saveDirectoryFilteredtoXML
     *
     * @param directoryIn Root directory to walk
     * @return boolean success
     */
    public boolean saveDirectorytoXML(String directoryIn) {
        directory = Paths.get(directoryIn);
        outputFile = new File(directory.toAbsolutePath() + FileUtils.DIRECTORY_SEPARATOR +
                directory.getFileName() + ".xml");
        directoryDocument = getDocument();

        if (walkDirectory()) {
            printXMLtoFile();
            return true;
        } else {
            return false;
        }
    }

    public boolean saveDirectoryFilteredtoXML(String directoryIn, Map<String, String> findSuffixes) {
        suffixes = findSuffixes;
        return saveDirectorytoXML(directoryIn);
    }

    private boolean walkDirectory() {
        try {
            Path path = Files.walkFileTree(directory, opts, 20, this); //start the walk
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        // Since we are creating Elements from folder name need to strip whitespace and special characters
        // regex == NOT a digit or alpha
        //    String dirName = dir.getFileName().toString().replaceAll("[^\\da-zA-Z]", "");
        String dirName = dir.getFileName().toString().replaceAll("[\\s]", "_");
        if (dirName.matches("^[^a-zA-Z]{1}.*")) {
            dirName = "_" + dirName;
        }
        System.out.println(dirName);
        if (directoryDocument.getDocumentElement() == null) {
            Element root = directoryDocument.createElement(dirName);
            directoryDocument.appendChild(root);
            root.getParentNode().insertBefore(comment, root);
            currentDir = root;
        } else {
            Element directoryElement = directoryDocument.createElement(dirName);
            //TODO change this to use Path.startswith()
            // Only if it is a child not a sibling
            if (dir.toString().contains(currentDir.getAttribute("path"))) {
                currentDir.appendChild(directoryElement);
                currentDir = directoryElement;
            } else {
                do {
                    currentDir = (Element) currentDir.getParentNode();
                } while (!dir.toString().contains(currentDir.getAttribute("path")));
                currentDir.appendChild(directoryElement);
                currentDir = directoryElement;
            }
        }
        currentDir.setAttribute("path", dir.toString());

        //System.out.println("<" + directory.getFileName() + ">");
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (acceptFile(file)) {
            Element element = directoryDocument.createElement("file");
            element.appendChild(directoryDocument.createTextNode(file.getFileName().toString()));
            element.setAttribute("sha1", Hasher.getSHA1(file.toFile()));
            element.setAttribute("size", String.valueOf(file.toFile().length())); //
            currentDir.appendChild(element);
        }
        //System.out.println( file.getFileName());
        return super.visitFile(file, attrs);
    }

    //Output the XML to File
    private void printXMLtoFile() {
        try (PrintWriter out = new PrintWriter(outputFile, Charset.defaultCharset().toString())) {
            StringWriter sw = new StringWriter();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            //create string from xml tree
            Result result = new StreamResult(sw);
            Source source = new DOMSource(directoryDocument);
            transformer.transform(source, result);

            String xml = sw.toString();
            out.print(xml);
            out.flush();
        } catch (FileNotFoundException | UnsupportedEncodingException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private Document getDocument() {
        Document doc = null;
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            comment = doc.createComment("Size is in bytes. Folder names have: whitespace replaced with" +
                    " an underscore, and prepended underscore if they do not start with an Alpha character." +
                    FileUtils.NEWLINE + "This is to meet the XML Element naming standard.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;
    }


    private boolean acceptFile(Path path) {
        String extension = getExtension(path);
        return (extension != null) && (suffixes.isEmpty() || (suffixes.get(extension) != null));
    }

    private String getExtension(Path path) {
        if (path != null) {
            String filename = path.getFileName().toString();
            int i = filename.lastIndexOf('.');
            if ((i > 0) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }
}
