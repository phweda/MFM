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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.19 at 10:16:00 AM EDT 
//


package Phweda.MFM.mame;

import Phweda.MFM.MFM;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://MFM.Phweda/MAME}description"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}year" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}manufacturer" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}biosset" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}rom" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}disk" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}device_ref" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}sample" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}chip" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}display" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}sound" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}input" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}dipswitch" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}configuration" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}port" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}adjuster" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}driver" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}device" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}slot" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}softwarelist" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://MFM.Phweda/MAME}ramoption" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sourcefile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isbios" default="no">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="yes"/>
 *             &lt;enumeration value="no"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="isdevice" default="no">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="yes"/>
 *             &lt;enumeration value="no"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="ismechanical" default="no">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="yes"/>
 *             &lt;enumeration value="no"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="runnable" default="yes">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="yes"/>
 *             &lt;enumeration value="no"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="cloneof" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="romof" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sampleof" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "history",
        "info",
        "nplayerEntry",
        "category",
        "supportsSimultaneous",
        "MAMEVersionAdded",
        "orientation",
        "screen",
        "description",
        "year",
        "manufacturer",
        "biosset",
        "rom",
        "disk",
        "deviceRef",
        "sample",
        "chip",
        "display",
        "sound",
        "input",
        "dipswitch",
        "configuration",
        "port",
        "adjuster",
        "driver",
        "device",
        "slot",
        "softwarelist",
        "ramoption"
})
@XmlRootElement(name = "machine")
public class Machine implements Serializable {

    private static final long serialVersionUID = 1771200835349120764L;

    //========================= MFM ===================================

    // For Column header names all caps
    public static final String MACHINE_NAME = "Machine Name";
    public static final String MACHINE_FULL_NAME = "Game(System) Name";
    public static final String CATEGORY_CAPS = "Category";
    public static final String YEAR_CAPS = "Year";
    public static final String MANUFACTURER_CAPS = "Manufacturer";
    public static final String STATUS_CAPS = "Status";
    public static final String CLONEOF_CAPS = "Cloneof";
    public static final String MAMEVERSIONADDED_CAPS = "MAMEVersionAdded";
    private static final int TIP_LENGTH = 2000;

    @XmlAttribute(name = "history")
    private String history;  // From History.dat
    @XmlAttribute(name = "info")
    private String info = "";  // From MAMEInfo.dat or plus MESSInfo.dat or plus SYSInfo.dat
    @XmlAttribute(name = "nplayerEntry")
    private String nplayerEntry; // From nplayers.ini
    @XmlAttribute(name = "category")
    private String category; // From Catver_full.ini or Catver.ini NOTE not guaranteed to have one
    @XmlAttribute(name = "supportsSimultaneous")
    private boolean supportsSimultaneous = false; // From nplayers.ini
    @XmlAttribute(name = "MAMEVersionAdded")
    private String MAMEVersionAdded; // From Catver_full.ini or Catver.ini
    @XmlAttribute(name = "orientation")
    private String orientation; // MAME older versions. vertical|horizontal
    @XmlAttribute(name = "screen")
    private String screen; // MAME older versions. raster|vector

    // @XmlAttribute and @XmlElement Strings for manual parsing in ParseAllMachineInfo
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY = "display";
    public static final String VIDEO = "video";
    public static final String DRIVER = "driver";
    public static final String DEVICE_REF = "device_ref";
    public static final String ISBIOS = "isbios";
    public static final String DISK = "disk";
    public static final String SOFTWARELIST = "softwarelist";
    public static final String YEAR = "year";
    public static final String MANUFACTURER = "manufacturer";
    public static final String CLONEOF = "cloneof";
    public static final String ROMOF = "romof";
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
    public static final String TYPE = "type";
    public static final String CHD = "CHD";
    public static final String STATUS = "status";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String VERTICAL = "Vertical";
    public static final String HORIZONTAL = "Horizontal";
    public static final String ORIENTATION = "orientation";
    public static final String SCREEN = "screen";
    public static final String ROM = "rom";
    public static final String SIZE = "size";
    public static final String SHA1 = "sha1";
    public static final String CRC = "crc";

    static final String MACHINE = "machine";
    // Legacy xml dtd value is "game"
    static final String GAME = "game";
    static final String GOOD = "good";
    static final String IMPERFECT = "imperfect";
    static final String PRELIMINARY = "preliminary";
    @XmlTransient
    private int buttons = -1;

    //======================END MFM ===================================

    @XmlElement(required = true)
    protected String description;
    protected String year;
    protected String manufacturer;
    protected List<Biosset> biosset;
    protected List<Rom> rom;
    protected List<Disk> disk;
    @XmlElement(name = "device_ref")
    protected List<DeviceRef> deviceRef;
    protected List<Sample> sample;
    protected List<Chip> chip;
    protected List<Display> display;
    protected Sound sound;
    protected Input input;
    protected List<Dipswitch> dipswitch;
    protected List<Configuration> configuration;
    protected List<Port> port;
    protected List<Adjuster> adjuster;
    protected Driver driver;
    protected List<Device> device;
    protected List<Slot> slot;
    protected List<Softwarelist> softwarelist;
    protected List<Ramoption> ramoption;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "sourcefile")
    protected String sourcefile;
    @XmlAttribute(name = "isbios")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String isbios;
    @XmlAttribute(name = "isdevice")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String isdevice;
    @XmlAttribute(name = "ismechanical")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String ismechanical;
    @XmlAttribute(name = "runnable")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String runnable;
    @XmlAttribute(name = "cloneof")
    protected String cloneof;
    @XmlAttribute(name = "romof")
    protected String romof;
    @XmlAttribute(name = "sampleof")
    protected String sampleof;

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the year property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the value of the year property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setYear(String value) {
        this.year = value;
    }

    /**
     * Gets the value of the manufacturer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the biosset property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the biosset property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBiosset().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Biosset }
     */
    public List<Biosset> getBiosset() {
        if (biosset == null) {
            biosset = new ArrayList<Biosset>();
        }
        return this.biosset;
    }

    /**
     * Gets the value of the rom property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rom property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRom().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rom }
     */
    public List<Rom> getRom() {
        if (rom == null) {
            rom = new ArrayList<Rom>();
        }
        return this.rom;
    }

    /**
     * Gets the value of the disk property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the disk property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisk().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Disk }
     */
    public List<Disk> getDisk() {
        if (disk == null) {
            disk = new ArrayList<Disk>();
        }
        return this.disk;
    }

    /**
     * Gets the value of the deviceRef property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deviceRef property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeviceRef().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeviceRef }
     */
    public List<DeviceRef> getDeviceRef() {
        if (deviceRef == null) {
            deviceRef = new ArrayList<DeviceRef>();
        }
        return this.deviceRef;
    }

    /**
     * Gets the value of the sample property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sample property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSample().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sample }
     */
    public List<Sample> getSample() {
        if (sample == null) {
            sample = new ArrayList<Sample>();
        }
        return this.sample;
    }

    /**
     * Gets the value of the chip property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chip property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChip().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Chip }
     */
    public List<Chip> getChip() {
        if (chip == null) {
            chip = new ArrayList<Chip>();
        }
        return this.chip;
    }

    /**
     * Gets the value of the display property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the display property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisplay().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Display }
     */
    public List<Display> getDisplay() {
        if (display == null) {
            display = new ArrayList<Display>();
        }
        return this.display;
    }

    /**
     * Gets the value of the sound property.
     *
     * @return possible object is
     * {@link Sound }
     */
    public Sound getSound() {
        return sound;
    }

    /**
     * Sets the value of the sound property.
     *
     * @param value allowed object is
     *              {@link Sound }
     */
    public void setSound(Sound value) {
        this.sound = value;
    }

    /**
     * Gets the value of the input property.
     *
     * @return possible object is
     * {@link Input }
     */
    public Input getInput() {
        return input;
    }

    /**
     * Sets the value of the input property.
     *
     * @param value allowed object is
     *              {@link Input }
     */
    public void setInput(Input value) {
        this.input = value;
    }

    /**
     * Gets the value of the dipswitch property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dipswitch property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDipswitch().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dipswitch }
     */
    public List<Dipswitch> getDipswitch() {
        if (dipswitch == null) {
            dipswitch = new ArrayList<Dipswitch>();
        }
        return this.dipswitch;
    }

    /**
     * Gets the value of the configuration property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configuration property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfiguration().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Configuration }
     */
    public List<Configuration> getConfiguration() {
        if (configuration == null) {
            configuration = new ArrayList<Configuration>();
        }
        return this.configuration;
    }

    /**
     * Gets the value of the port property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the port property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPort().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Port }
     */
    public List<Port> getPort() {
        if (port == null) {
            port = new ArrayList<Port>();
        }
        return this.port;
    }

    /**
     * Gets the value of the adjuster property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adjuster property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdjuster().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Adjuster }
     */
    public List<Adjuster> getAdjuster() {
        if (adjuster == null) {
            adjuster = new ArrayList<Adjuster>();
        }
        return this.adjuster;
    }

    /**
     * Gets the value of the driver property.
     *
     * @return possible object is
     * {@link Driver }
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * Sets the value of the driver property.
     *
     * @param value allowed object is
     *              {@link Driver }
     */
    public void setDriver(Driver value) {
        this.driver = value;
    }

    /**
     * Gets the value of the device property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the device property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDevice().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Device }
     */
    public List<Device> getDevice() {
        if (device == null) {
            device = new ArrayList<Device>();
        }
        return this.device;
    }

    /**
     * Gets the value of the slot property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the slot property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSlot().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Slot }
     */
    public List<Slot> getSlot() {
        if (slot == null) {
            slot = new ArrayList<Slot>();
        }
        return this.slot;
    }

    /**
     * Gets the value of the softwarelist property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the softwarelist property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSoftwarelist().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Softwarelist }
     */
    public List<Softwarelist> getSoftwarelist() {
        if (softwarelist == null) {
            softwarelist = new ArrayList<Softwarelist>();
        }
        return this.softwarelist;
    }

    /**
     * Gets the value of the ramoption property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ramoption property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRamoption().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ramoption }
     */
    public List<Ramoption> getRamoption() {
        if (ramoption == null) {
            ramoption = new ArrayList<Ramoption>();
        }
        return this.ramoption;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the sourcefile property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSourcefile() {
        return sourcefile;
    }

    /**
     * Sets the value of the sourcefile property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSourcefile(String value) {
        this.sourcefile = value;
    }

    /**
     * Gets the value of the isbios property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIsbios() {
        if (isbios == null) {
            return "no";
        } else {
            return isbios;
        }
    }

    /**
     * Sets the value of the isbios property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIsbios(String value) {
        this.isbios = value;
    }

    /**
     * Gets the value of the isdevice property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIsdevice() {
        if (isdevice == null) {
            return "no";
        } else {
            return isdevice;
        }
    }

    /**
     * Sets the value of the isdevice property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIsdevice(String value) {
        this.isdevice = value;
    }

    /**
     * Gets the value of the ismechanical property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getIsmechanical() {
        if (ismechanical == null) {
            return "no";
        } else {
            return ismechanical;
        }
    }

    /**
     * Sets the value of the ismechanical property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setIsmechanical(String value) {
        this.ismechanical = value;
    }

    /**
     * Gets the value of the runnable property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRunnable() {
        if (runnable == null) {
            return "yes";
        } else {
            return runnable;
        }
    }

    /**
     * Sets the value of the runnable property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRunnable(String value) {
        this.runnable = value;
    }

    /**
     * Gets the value of the cloneof property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCloneof() {
        return cloneof;
    }

    /**
     * Sets the value of the cloneof property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCloneof(String value) {
        this.cloneof = value;
    }

    /**
     * Gets the value of the romof property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRomof() {
        return romof;
    }

    /**
     * Sets the value of the romof property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRomof(String value) {
        this.romof = value;
    }

    /**
     * Gets the value of the sampleof property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSampleof() {
        return sampleof;
    }

    /**
     * Sets the value of the sampleof property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSampleof(String value) {
        this.sampleof = value;
    }

    //========================= MFM methods ===================================

    public String getCategory() {
        if (category == null) {
            return "";
        }
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        if (history != null) {
            this.history = history.length() > 2 ? history.trim() : history;
        }
    }

    public void setScreentype(String screentype) {
        screen = screentype;
    }

    public String getScreentype() {
        if (screen == null) {
            return "";
        }
        return screen;
    }

    public void setIsVertical(String orientationIn) {
        orientation = orientationIn;
    }

    public String getIsVertical() {
        if (!this.getDisplay().isEmpty()) {
            String rotate = this.getDisplay().get(0).getRotate();
            return rotate.equals("90") || rotate.equals("270") ? VERTICAL : HORIZONTAL;
        }
        return "";
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getHeight() {
        return this.getDisplay().get(0).getHeight();
    }

    public String getWidth() {
        return this.getDisplay().get(0).getWidth();
    }

    public String getTruncatedHistory() {
        int nthEndline = nthIndexOf(history, "\n", 12);
        if (nthEndline > 0 && history.length() < TIP_LENGTH) {
            return history.substring(0, nthEndline);
        } else if (history.length() >= TIP_LENGTH) {
            return history.substring(0, history.indexOf("\n", TIP_LENGTH));
        }
/*
        if(history.length() > 3000){
            if(MFM.isSystemDebug()){
                System.out.println("Truncated history total length is : " + history.length());
            }
            String temp = history.substring(0, 3000);
            return temp.substring(0, temp.lastIndexOf('\n'));
        }
*/
        return history;
    }

    /**
     * fixme find a good place for this
     * Return the <i>nth</i> index of the given token occurring in the given string.
     *
     * @param string String to search.
     * @param token  Token to match.
     * @param index  <i>Nth</i> index.
     * @return Index of <i>nth</i> item or -1.
     */
    private static int nthIndexOf(final String string, final String token,
                                  final int index) {
        int j = 0;

        for (int i = 0; i < index; i++) {
            j = string.indexOf(token, j + 1);
            if (j == -1) break;
        }

        return j;
    }

    public String getInfo() {
        return info.toString();
    }

    public void setInfo(String infoIn) {
        if (infoIn != null && !infoIn.isEmpty()) {
            info = info.concat(infoIn);
        }
    }

    public String getMAMEVersionAdded() {
        return MAMEVersionAdded;
    }

    public void setMAMEVersionAdded(String MAMEVersionAdded) {
        this.MAMEVersionAdded = MAMEVersionAdded;
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

    public boolean isSupportsSimultaneous() {
        return supportsSimultaneous;
    }

    public int getButtons() {
        if (buttons == -1) {
            for (Control control : this.getInput().getControl()) {
                String tempStr = control.getButtons();
                if (tempStr != null && !tempStr.isEmpty()) {
                    int tempInt = Integer.parseInt(tempStr);
                    buttons = tempInt > buttons ? tempInt : buttons;
                }
            }
        }
        if (MFM.isSystemDebug()) {
            System.out.println(this.getName() + " buttons are : " + buttons);
        }
        return buttons;
    }

    public void setButtons(int buttonsIn) {
        buttons = buttonsIn;
    }

    /**
     * Used by MachineListTableModel
     *
     * @param paramName table column header
     * @return value for table column
     */
    public String getValueOf(String paramName) {
        String value = "";

        //NOTE Bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7071246
        // can not put () around case Strings
        // DONE Java 8 has fixed

        // For display column mapping see @MachineListTableModel
        switch (paramName) {
            case (MACHINE_NAME):
                return getName();
            case MACHINE_FULL_NAME:
                return getDescription();
            case CATEGORY_CAPS:
                return getCategory();
            case MAMEVERSIONADDED_CAPS:
                return getMAMEVersionAdded();
            case YEAR_CAPS:
                return getYear();
            case MANUFACTURER_CAPS:
                return getManufacturer();
            case CLONEOF_CAPS:
                return getCloneof();
            case ROMOF:
                return getRomof();
            case STATUS_CAPS:
                return getDriver().getStatus();
            case CHD:
                return getDisk().size() > 0 ? "Yes" : "";

        }
        return value;
    }

    @Override
    public String toString() {
        // return super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append( doubleQuoteString(getDescription()));
        sb.append(COMMA);
        sb.append(getName());
        sb.append(COMMA);
        sb.append( doubleQuoteString(getManufacturer()));
        sb.append(COMMA);
        sb.append(getYear());
        sb.append(COMMA);
        sb.append(getCategory());
        sb.append(COMMA);
        sb.append(getDriver().getStatus());
        sb.append(COMMA);
        sb.append(getCloneof());
        sb.append(COMMA);
        sb.append(getMAMEVersionAdded());
        sb.append(COMMA);

        // NESTED TERNARY
        // String temp = isCocktail() ? COCKTAIL : isVertical() ? "vertical" : "horizontal";
        sb.append(getDriver().getCocktail() != null && !getDriver().getCocktail().isEmpty()
                ? COCKTAIL : getIsVertical().equals(VERTICAL) ? "vertical" : "horizontal");

        sb.append(COMMA);
        sb.append((getDisplay() != null && !getDisplay().isEmpty()) ? getDisplay().get(0).getType() : "");
        sb.append(COMMA);
        sb.append(getButtons());
        sb.append(COMMA);
        sb.append(getInput().getPlayers());
        sb.append(COMMA);
        sb.append(isSupportsSimultaneous() ? "yes" : "");
        sb.append(COMMA);

        sb.append(getDisk().size() > 0 ? doubleQuoteString(diskstoString()) : "");
        sb.append(COMMA);

        sb.append(getSoftwarelist().size() > 0 ? doubleQuoteString(SLstoString()) : "");
        sb.append(COMMA);

        sb.append(getSourcefile());
        sb.append(COMMA);
        sb.append(doubleQuoteString(controlstoString()));
        sb.append(COMMA);
        sb.append(getIsbios());
        sb.append(COMMA);
        sb.append(getIsdevice());

        return sb.toString();
    }

    private String SLstoString() {
        final StringBuilder sb = new StringBuilder();
        getSoftwarelist().forEach(list -> sb.append(list.getName() + " | "));
        return sb.length() > 4 ? sb.substring(0, sb.length() - 2) : "";
    }

    private String controlstoString() {
        final StringBuilder sb = new StringBuilder();
        getInput().getControl().forEach(control -> sb.append(control.getType() + " | "));
        return sb.length() > 4 ? sb.substring(0, sb.length() - 2) : "";
    }

    private String diskstoString() {
        final StringBuilder sb = new StringBuilder();
        getDisk().forEach(disk -> sb.append(disk.getName() + " | "));
        return sb.length() > 4 ? sb.substring(0, sb.length() - 2) : "";
    }

    // TODO find a better place for this
    public static String doubleQuoteString(String input){
        StringBuilder stringBuilder = new StringBuilder("\"");
        if(input.contains("\"")){
            input = input.replace("\"", "''");
        }
        stringBuilder.append(input);
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    private static final String COMMA = ",";
    public static final String CSV_HEADER = MACHINE_FULL_NAME + COMMA + MACHINE_NAME + COMMA
            + MANUFACTURER_CAPS + COMMA + YEAR_CAPS + COMMA + CATEGORY_CAPS + COMMA + STATUS + COMMA
            + CLONEOF_CAPS + COMMA + MAMEVERSIONADDED_CAPS + COMMA + "Orientation" + COMMA + "Display Type" + COMMA
            + BUTTONS + COMMA + PLAYERS + COMMA + "Simultaneous" + COMMA + "CHD" + COMMA
            + "SoftwareList" + COMMA + "Source file" + COMMA + "Controls" + COMMA + "BIOS" + COMMA + "DEVICE";

    //======================END MFM methods ===================================
}
