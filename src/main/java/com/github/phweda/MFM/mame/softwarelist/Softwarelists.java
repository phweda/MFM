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

package com.github.phweda.MFM.mame.softwarelist;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 5/5/2018
 * Time: 5:23 PM
 * <p>
 * MFM unique pseudo XML to allow saving all MAME Softwarelist XML in a single file
 */

import javax.xml.bind.annotation.*;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * MFM unique pseudo XML Object to allow saving all MAME Softwarelist XML files(objects) in a single file<br/>
 * Softwarelists class serves as the XML root for all Softwarelist entries
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "softwarelists"
})
@XmlRootElement(name = "softwarelists")
public class Softwarelists {
    protected Set<Softwarelist> softwarelists;
    @XmlAttribute(name = "version")
    protected String version;
    @XmlTransient
    private TreeMap<String, Softwarelist> softwarelistsMap;

    public Set<Softwarelist> getSoftwarelists() {
        if (softwarelists == null) {
            softwarelists = new TreeSet<>();
        }
        return this.softwarelists;
    }

    public void setSoftwarelists(Set<Softwarelist> softwarelists) {
        this.softwarelists = softwarelists;
    }


    public TreeMap<String, Softwarelist> getSoftwarelistsMap() {
        if (softwarelistsMap == null) {
            softwarelistsMap = generateSoftwarelistsMap();
        }
        return this.softwarelistsMap;
    }

    /**
     * Gets the value of the build property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the build property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

    private TreeMap<String, Softwarelist> generateSoftwarelistsMap() {
        TreeMap<String, Softwarelist> map = new TreeMap<>();
        this.getSoftwarelists().forEach(softwarelist -> map.put(softwarelist.getName(), softwarelist));
        return map;
    }

}
