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

package com.github.phweda.MFM.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/16/2016
 * Time: 11:47 AM
 */

import com.github.phweda.MFM.*;
import com.github.phweda.MFM.datafile.Datafile;
import com.github.phweda.MFM.datafile.Disk;
import com.github.phweda.MFM.datafile.Game;
import com.github.phweda.MFM.datafile.Header;
import com.github.phweda.MFM.mame.Machine;
import com.github.phweda.MFM.mame.Rom;
import com.github.phweda.utils.PersistUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * Creates a standard DAT file for a list
 */
@SuppressWarnings({"squid:S00117", "squid:S3008"})
public class MFM_DATmaker {

    private static Map<String, Machine> map;
    private static final String NODUMP = "nodump";
    private static final String LISTS_ZIP_FILE_SUFFIX = "_Lists_DATs.zip";
    private static File DATschemaFile = new File(MFM.getMfmSettingsDir() + "datafile2018.xsd");
    public static final String GOOD = "Good";

    public static Datafile generateDAT(String listName, Set<String> list) {
        return createDAT(listName, list);
    }

    /**
     * @param datafile input DAT
     * @param list     Machines to retain in modified DAT
     * @return modified DAT objects
     */
    public static Datafile filterDATbyList(Datafile datafile, Set<String> list) {
        Datafile filteredDatafile = new Datafile();
        filteredDatafile.setHeader(datafile.getHeader());
        datafile.getGame().forEach(game ->
        {
            if (list.contains(game.getName())) {
                filteredDatafile.getGame().add(game);
            }
        });
        return filteredDatafile;
    }


    public String validateDAT(File xmlFile) {
        try {
            if (DATschemaFile.exists()) {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(DATschemaFile);
                Validator validator = schema.newValidator();
                validator.validate(new StreamSource(xmlFile));
            } else {
                MFM.getLogger().addToList("Failed to find datafile!!! ", true);
                return "Failed to find datafile.xsd shoulf be in <MFM root>/Settings";
            }
        } catch (IOException | SAXException e) {
            if (MFM.isSystemDebug()) {
                System.out.println("Exception: " + e.getMessage());
            }
            e.printStackTrace();
            return e.getMessage();
        }
        return GOOD;
    }

    /**
     * NAMES:
     * 186_ALL_DAT.zip
     * 186_RUNNABLE_DAT.zip
     * Creates and zips up builtin lists. To support MFM Data Set torrent
     **/
    public static void saveBuiltinListsDATs() {
        List<Datafile> DATfiles = new ArrayList<>();
        MFMPlayLists.getInstance().getMFMListNames()
                .forEach(listName ->
                        DATfiles.add(createDAT(listName, MFMPlayLists.getInstance().getPlayList(listName))));

        saveDATS(DATfiles);
    }

    /**
     * NAMES:
     * 186_ALL_DAT.zip
     * 186_RUNNABLE_DAT.zip
     * Creates and zips up builtin lists. To support MFM Data Set torrent
     *
     * @param DATfiles DAT files to save to ZIP
     */
    private static void saveDATS(List<Datafile> DATfiles) {
        String zipName = getDATzipName();
        String versionSuffix = "(" + getDataVersionNumber() + ").dat";

        Thread persistDATs = new Thread() {
            @Override
            public void run() {
                if (MFM.isSystemDebug()) {
                    System.out.println("## Saving DATS");
                }
                synchronized (this) {
                    String logMessage = "Savings MFM DATS: " + DATfiles.size() + "\n\n";
                    System.out.println(logMessage);
                    MFM.getLogger().addToList(logMessage, true);

                    try (ZipOutputStream zipOutputStream =
                                 new ZipOutputStream(new FileOutputStream(zipName))) {
                        DATfiles.forEach(DAT -> PersistUtils.saveJAXBtoZip(DAT, zipOutputStream,
                                DAT.getHeader().getName() + versionSuffix, Datafile.class, true));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        persistDATs.start();
    }

    private static Datafile createDAT(String listName, Set<String> list) {
        Datafile df = new Datafile();
        df.setHeader(createHeader(listName));
        addMachines(df, list);
        return df;
    }

    /**
     * Game is DAT Element for a Machine
     *
     * @param datafile - new DAT file
     * @param list     - list of machines
     */
    private static void addMachines(Datafile datafile, Set<String> list) {
        map = MAMEInfo.getMame().getMachineMap();
        for (String machineName : list) {
            Machine machine = map.get(machineName);
            datafile.getGame().add(addMachine(machine, new Game()));
        }
    }

    private static Game addMachine(Machine machine, Game game) {
        if (machine == null) {
            return game;
        }
        if (game.getName() == null) {
            game.setName(machine.getName());
            game.setDescription(machine.getDescription());
            game.setYear(machine.getYear());
            game.setManufacturer(machine.getManufacturer());
        }
        for (Rom rom : machine.getRom()) {
            // Eliminate nodump
            if (!rom.getStatus().equals(NODUMP)) {
                game.getRom().add(transformRom(rom));
            }
        }
        for (Phweda.MFM.mame.Disk disk : machine.getDisk()) {
            Disk newDisk = transformDisk(disk);
            // Eliminate nodump
            if (disk.getStatus().equals(NODUMP)) {
                continue;
            }
            // Check for dupes - Parent & Child may have same Disk/CHD
            if (!game.getDisk().contains(newDisk)) {
                game.getDisk().add(newDisk);
            }
        }
        for (Phweda.MFM.mame.DeviceRef deviceRef : machine.getDeviceRef()) {
            if (map.containsKey(deviceRef.getName())) {
                addMachine(map.get(deviceRef.getName()), game);
            }
        }
        if (machine.getCloneof() != null) {
            addMachine(map.get(machine.getCloneof()), game);
        }
        return game;
    }

    // sequence is name, size, crc, md5, sha 1
    private static com.github.phweda.MFM.datafile.Rom transformRom(Rom romIn) {
        com.github.phweda.MFM.datafile.Rom rom = new com.github.phweda.MFM.datafile.Rom();
        rom.setName(romIn.getName());
        rom.setSize(romIn.getSize());
        rom.setCrc(romIn.getCrc());
        rom.setMd5(romIn.getMd5());
        rom.setSha1(romIn.getSha1());

        return rom;
    }

    private static com.github.phweda.MFM.datafile.Disk transformDisk(Phweda.MFM.mame.Disk diskIn) {
        com.github.phweda.MFM.datafile.Disk disk = new com.github.phweda.MFM.datafile.Disk();
        disk.setName(diskIn.getName());
        disk.setSha1(diskIn.getSha1());
        disk.setMd5(diskIn.getMd5());
        return disk;
    }

    private static Header createHeader(String listName) {
        Header header = new Header();
        header.setName(listName);
        header.setDescription(listName);
        header.setComment("MFM generated list - " + listName);
        header.setCategory("Standard DatFile");
        header.setDate(new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
        header.setVersion(MFM_Data.getInstance().getDataVersion());
        header.setAuthor(MFM.MAME_FILE_MANAGER);
        return header;
    }

    // Should really just change the file name format - oh well that's more trouble
    // 186_Lists_DATs.zip
    // 186_ALL_Lists_DATs.zip
    private static String getDATzipName() {
        StringBuilder sb = new StringBuilder(MFM.getMfmDataDir());
        String dataVersion = MFMSettings.getInstance().getDataVersion();
        // OK just for fun. really should be if/else with no String concat
        sb.append(dataVersion.contains(MFMSettings.ALL_UNDERSCORE) ?
                dataVersion.substring(4) + "_" + "ALL" : dataVersion);

        sb.append(LISTS_ZIP_FILE_SUFFIX);
        return sb.toString();
    }

    /**
     * Specifically for MFM version internal string
     *
     * @return Number string with 0. prefix removed
     */
    private static String getDataVersionNumber() {
        String version = MFMSettings.getInstance().getDataVersion();
        if (version.contains(".")) {
            return version.substring(version.lastIndexOf('.') + 1);
        }
        return version;
    }
}
