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

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/16/2016
 * Time: 11:47 AM
 */

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MFM;
import Phweda.MFM.MFM_Data;
import Phweda.MFM.datafile.Datafile;
import Phweda.MFM.datafile.Disk;
import Phweda.MFM.datafile.Game;
import Phweda.MFM.datafile.Header;
import Phweda.MFM.mame.Machine;
import Phweda.MFM.mame.Rom;

import javax.xml.parsers.ParserConfigurationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Creates a standard DAT file for a list
 */
public class MFM_DATmaker {

    private static Map<String, Machine> map;
    private static final String NODUMP = "nodump";

    public static Datafile generateDAT(String listName, Set list) throws ParserConfigurationException {
        return createDAT(listName, list);
    }

    private static Datafile createDAT(String listName, Set<String> list) throws ParserConfigurationException {
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
    private static Phweda.MFM.datafile.Rom transformRom(Rom romIn) {
        Phweda.MFM.datafile.Rom rom = new Phweda.MFM.datafile.Rom();
        rom.setName(romIn.getName());
        rom.setSize(romIn.getSize());
        rom.setCrc(romIn.getCrc());
        rom.setMd5(romIn.getMd5());
        rom.setSha1(romIn.getSha1());

        return rom;
    }

    private static Phweda.MFM.datafile.Disk transformDisk(Phweda.MFM.mame.Disk diskIn) {
        Phweda.MFM.datafile.Disk disk = new Phweda.MFM.datafile.Disk();
        disk.setName(diskIn.getName());
        disk.setSha1(diskIn.getSha1());
        disk.setMd5(diskIn.getMd5());
        return disk;
    }

    /*
        <header>
            <name>PD Video Project</name>
            <description>PD Video Project 09-6-2015</description>
            <category>Standard DatFile</category>
		    <version>09-6-2015</version>
		    <author>phweda</author>
		    <clrmamepro forcepacking="unzip"/>
	    </header>
    */

    private static Header createHeader(String listName) {
        Header header = new Header();
        header.setName(listName);
        header.setDescription(listName);
        header.setComment("MFM generated list - " + listName);
        header.setCategory("Standard DatFile");
        header.setDate(new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
        header.setVersion(MFM_Data.getInstance().getDataVersion());
        header.setAuthor(MFM.APPLICATION_NAME);

        return header;
    }
}
