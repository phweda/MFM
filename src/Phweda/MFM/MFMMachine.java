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

package Phweda.MFM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/25/11
 * Time: 2:27 PM
 */

/**
 * Legacy class replaced by Machine class
 * <p>
 * NOTE this class follows the Java Beans Spec to enable saving object as XML
 * NOTE that Serialization does not require this
 */
public class MFMMachine implements Serializable {

    // All lower case string means from MAME XML and we match its values
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY = "display";
    public static final String DRIVER = "driver";
    public static final String DEVICE_REF = "device_ref";
    public static final String ISBIOS = "isbios";

    // disks synonymous with CHD
    // disk == has CHD
    public static final String DISK = "disk";
    public static final String CHD = "CHD";
    public static final String SOFTWARELIST = "softwarelist";

    // Not an attribute of the Machine
//    public static final String CATEGORY = "category";
    public static final String MAMEVERSIONADDED = "MAMEVersionAdded";
    public static final String YEAR = "year";
    public static final String MANUFACTURER = "manufacturer";
    public static final String CLONEOF = "cloneof";
    public static final String ROMOF = "romof";
    public static final String STATUS = "Status"; // From MAME XML with logic
    public static final String HISTORY = "History"; // From history.dat
    public static final String INFO = "Info"; // From MAMEInfo.dat or MESS.dat
    public static final String INPUT = "input";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String ROTATE = "rotate";
    public static final String DEVICES = "devices";
    public static final String CONTROL = "control";
    public static final String COCKTAIL = "cocktail";
    public static final String BUTTONS = "buttons";
    public static final String PLAYERS = "players";
    public static final String SOURCEFILE = "sourcefile";
    public static final String NINETY = "90";
    public static final String TWOSEVENTY = "270";
    public static final String TYPE = "type";

    // For Column header names all caps
    public static final String MACHINE_NAME = "Machine Name";
    public static final String MACHINE_FULL_NAME = "Game(System) Name";
    public static final String CATEGORY_CAPS = "Category";
    public static final String MAMEVERSIONADDED_CAPS = "MAMEVersionAdded";
    public static final String YEAR_CAPS = "Year";
    public static final String MANUFACTURER_CAPS = "Manufacturer";
    public static final String CLONEOF_CAPS = "Cloneof";

    private String name;
    private String description;
    private String category = ""; // From Catver_full.ini or Catver.ini NOTE not guaranteed to have one
    private String MAMEVersionAdded; // From Catver_full.ini or Catver.ini
    private String year;
    private String manufacturer;
    private String cloneof = "";
    private String romof;
    private String status;
    private String sourcefile;
    private String history;  // From History.dat
    private StringBuilder info = new StringBuilder();  // From MAMEInfo.dat or plus MESSInfo.dat or plus SYSInfo.dat
    private String nplayerEntry; // From nplayers.ini
    private ArrayList<String> devices;
    private ArrayList<String> disks;
    private ArrayList<String> softwarelists = new ArrayList<String>();

    private TreeSet<String> controls = new TreeSet<String>();
    private String displayType = "";

    // Majority of Video Games are Horizontal so false is default
    private boolean vertical = false; // <display rotate="270" or  rotate="90" equals vertical
    private boolean hasCHD = false; // has a <disk> element? Needs confirmation 1/24/16
    private boolean isCocktail = false;
    private boolean supportsSimultaneous = false; // From nplayers.ini
    private boolean isbios = false;

    private int players = 0; // From MAME
    private int buttons = 0; // From MAME

    private int height = 0;
    private int width = 0;


    /*
     */

    /**
     * Used by MachineListTableModel
     *
     * @param paramName table column header
     * @return value for table column
     *//*

    public String getValueOf(String paramName) {
        String value = "";

        //NOTE Bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7071246
        // can not put () around case Strings

        // For display column mapping see @MachineListTableModel
        switch (paramName) {
            case MACHINE_NAME:
                return getName();
            case MACHINE_FULL_NAME:
                return getDescription();
            case CATEGORY_CAPS:
                return getCategory();
            case MAMEVERSIONADDED:
                return getMAMEVersionAdded();
            case YEAR_CAPS:
                return getYear();
            case MANUFACTURER_CAPS:
                return getManufacturer();
            case CLONEOF_CAPS:
                return getCloneof();
            case ROMOF:
                return getROMof();
            case STATUS:
                return getStatus();
            case CHD:
                return hasCHD() ? "Yes" : "";

            // These are never shown in MachineList
            case HISTORY:
                return getHistory();
            case INFO:
                return getInfo();
        }
        return value;
    }
*/
    public MFMMachine() {
    }

    public MFMMachine(String name) {
        this.name = name;
    }

/*
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasCHD() {
        return hasCHD;
    }
*/

    public void setHasCHD(boolean hasCHD) {
        this.hasCHD = hasCHD;
        disks = new ArrayList<String>();
    }

    public boolean isbios() {
        return isbios;
    }

    public void setIsbios(boolean isbios) {
        this.isbios = isbios;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMAMEVersionAdded() {
        return MAMEVersionAdded;
    }

    public void setMAMEVersionAdded(String MAMEVersionAdded) {
        this.MAMEVersionAdded = MAMEVersionAdded;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCloneof() {
        return cloneof;
    }

    public void setCloneof(String cloneof) {
        this.cloneof = cloneof;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourcefile() {
        return sourcefile;
    }

    public void setSourcefile(String sourcefile) {
        this.sourcefile = sourcefile;
    }

    public String getInfo() {
        return info.toString();
    }

    public void setInfo(String info) {
        if (info != null && !info.isEmpty()) {
            this.info.append(info);
        }
    }

    public String getNplayerEntry() {
        return nplayerEntry;
    }

    public void setNplayerEntry(String nplayerEntry) {
        // System.out.println(this.getName() + " - nPlayer is: " + nplayerEntry);
        this.nplayerEntry = nplayerEntry;
        if (nplayerEntry.contains("sim")) {
            this.supportsSimultaneous = true;
        }
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isCocktail() {
        return isCocktail;
    }

    public void setIsCocktail(boolean isCocktail) {
        this.isCocktail = isCocktail;
    }

    public boolean isSupportsSimultaneous() {
        return supportsSimultaneous;
    }

    public void setSupportsSimultaneous(boolean supportsSimultaneous) {
        this.supportsSimultaneous = supportsSimultaneous;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getButtons() {
        return buttons;
    }

    public void setButtons(int buttons) {
        this.buttons = buttons;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        if (history != null) {
            this.history = history.length() > 2 ? history.trim() : history;
        }
    }

    public String getTruncatedHistory() {
        if (history.contains("- TECHNICAL")) {
            return history.substring(0, history.indexOf("- TECHNICAL")).trim();
        }
        return history;
    }

    public String getROMof() {
        return this.romof;
    }

    public String setROMof(String romof) {
        return this.romof = romof;
    }

    public ArrayList<String> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<String> devices) {
        this.devices = devices;
    }

    public void addControl(String type) {
        controls.add(type);
    }

    public TreeSet<String> getControls() {
        return controls;
    }

    public void setControls(TreeSet<String> controls) {
        this.controls = controls;
    }

    public void addDisk(String diskName) {
        disks.add(diskName);
    }

    // disks synonymous with CHD
    public ArrayList<String> getDisks() {
        return disks;
    }

    public void addSoftwarelist(String softwarelistName) {
        softwarelists.add(softwarelistName);
    }

    public ArrayList<String> getSoftwarelists() {
        return softwarelists;
    }

    private String disksToString() {
        int size = getDisks().size();
        int i = 0;
        StringBuilder sb = new StringBuilder(getDisks().get(i++));
        while (i < size) {
            sb.append(" ~ " + getDisks().get(i++));
        }
        return sb.toString();
    }

    private String softwarelistsToString() {
        int size = getSoftwarelists().size();
        int i = 0;
        StringBuilder sb = new StringBuilder(getSoftwarelists().get(i++));
        while (i < size) {
            sb.append(" ~ " + getSoftwarelists().get(i++));
        }
        return sb.toString();
    }

}
