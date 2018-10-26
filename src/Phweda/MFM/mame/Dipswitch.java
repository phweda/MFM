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
// Generated on: 2017.09.10 at 12:51:51 PM EDT
//


package Phweda.MFM.mame;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}condition" minOccurs="0"/>
 *         &lt;element ref="{}diplocation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}dipvalue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tag" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="mask" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "condition",
        "diplocation",
        "dipvalue",
        "entry",
        "unknownElements"
})
@XmlRootElement(name = "dipswitch")
public class Dipswitch {

    protected Condition condition;
    protected List<Diplocation> diplocation;
    protected List<Dipvalue> dipvalue;
    // Psuedo XML to handle entry in very old MAME versions
    protected List<String> entry;


    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "tag", required = true)
    protected String tag;
    @XmlAttribute(name = "mask", required = true)
    protected String mask;

    // Catchall for any unknown Elements. As MAME DTD changes this will allow for
    // continuation of MFM without a code change. BUT IS NOT RECOMMENDED
    // Addition of Elements and Attributes should be handled with code updates.
    @XmlAnyElement(lax = true)
    private List<Object> unknownElements;

    /**
     * Gets the value of the condition property.
     *
     * @return possible object is
     * {@link Condition }
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     *
     * @param value allowed object is
     *              {@link Condition }
     */
    public void setCondition(Condition value) {
        this.condition = value;
    }

    /**
     * Gets the value of the diplocation property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diplocation property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiplocation().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Diplocation }
     */
    public List<Diplocation> getDiplocation() {
        if (diplocation == null) {
            diplocation = new ArrayList<Diplocation>();
        }
        return this.diplocation;
    }

    /**
     * Gets the value of the dipvalue property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dipvalue property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDipvalue().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dipvalue }
     */
    public List<Dipvalue> getDipvalue() {
        if (dipvalue == null) {
            dipvalue = new ArrayList<Dipvalue>();
        }
        return this.dipvalue;
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
     * Gets the value of the tag property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the value of the tag property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTag(String value) {
        this.tag = value;
    }

    /**
     * Gets the value of the mask property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMask() {
        return mask;
    }

    /**
     * Sets the value of the mask property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMask(String value) {
        this.mask = value;
    }

    /**
     * Psuedo XML to handle entry in very old MAME versions
     * <p>
     * Gets the value of the entry property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiplocation().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getEntry() {
        if (entry == null) {
            entry = new ArrayList<String>();
        }
        return this.entry;
    }
}
