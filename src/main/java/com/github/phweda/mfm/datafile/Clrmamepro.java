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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.10.24 at 07:22:36 PM EDT 
//


package com.github.phweda.mfm.datafile;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="header" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="forcemerging" default="split">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="split"/>
 *             &lt;enumeration value="full"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="forcenodump" default="obsolete">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="obsolete"/>
 *             &lt;enumeration value="required"/>
 *             &lt;enumeration value="ignore"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="forcepacking" default="zip">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="zip"/>
 *             &lt;enumeration value="unzip"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "clrmamepro")
@SuppressWarnings({"SameParameterValue", "unused", "WeakerAccess"})
public class Clrmamepro {

    @XmlAttribute(name = "header")
    protected String header;
    @XmlAttribute(name = "forcemerging")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String forcemerging;
    @XmlAttribute(name = "forcenodump")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String forcenodump;
    @XmlAttribute(name = "forcepacking")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String forcepacking;

    /**
     * Gets the value of the header property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the forcemerging property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForcemerging() {
        if (forcemerging == null) {
            return "split";
        } else {
            return forcemerging;
        }
    }

    /**
     * Sets the value of the forcemerging property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForcemerging(String value) {
        this.forcemerging = value;
    }

    /**
     * Gets the value of the forcenodump property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForcenodump() {
        if (forcenodump == null) {
            return "obsolete";
        } else {
            return forcenodump;
        }
    }

    /**
     * Sets the value of the forcenodump property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForcenodump(String value) {
        this.forcenodump = value;
    }

    /**
     * Gets the value of the forcepacking property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForcepacking() {
        if (forcepacking == null) {
            return "zip";
        } else {
            return forcepacking;
        }
    }

    /**
     * Sets the value of the forcepacking property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForcepacking(String value) {
        this.forcepacking = value;
    }

}
