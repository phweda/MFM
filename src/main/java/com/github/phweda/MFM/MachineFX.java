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

package com.github.phweda.MFM;/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/11/2015
 * Time: 9:24 PM
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Future class for/if move to JavaFX UI
 */
@SuppressWarnings("WeakerAccess")
public class MachineFX implements Serializable {

    private StringProperty name;
    private StringProperty category;
    private StringProperty description;
    private StringProperty year;
    private StringProperty manufacturer;

    public MachineFX(StringProperty name) {
        this.name = name;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String value) {
        nameProperty().set(value);
    }

    public StringProperty nameProperty() {
        if (name == null) name = new SimpleStringProperty(this, "name");
        return name;
    }

    public String getCategory() {
        return categoryProperty().get();
    }

    public void setCategory(String value) {
        categoryProperty().set(value);
    }

    public StringProperty categoryProperty() {
        if (category == null) category = new SimpleStringProperty(this, "category");
        return category;
    }

    public String getDescription() {
        return descriptionProperty().get();
    }

    public void setDescription(String value) {
        descriptionProperty().set(value);
    }

    public StringProperty descriptionProperty() {
        if (description == null) description = new SimpleStringProperty(this, "description");
        return description;
    }

    public String getYear() {
        return yearProperty().get();
    }

    public void setYear(String value) {
        yearProperty().set(value);
    }

    public StringProperty yearProperty() {
        if (year == null) year = new SimpleStringProperty(this, "year");
        return year;
    }

    public String getManufacturer() {
        return manufacturerProperty().get();
    }

    public void setManufacturer(String value) {
        manufacturerProperty().set(value);
    }

    public StringProperty manufacturerProperty() {
        if (manufacturer == null) manufacturer = new SimpleStringProperty(this, "manufacturer");
        return manufacturer;
    }







/*
    private StringProperty ;

    public void set(String value) {
        Property().set(value);
    }

    public String get() {
        return Property().get();
    }

    public StringProperty Property() {
        if ( == null)  = new SimpleStringProperty(this, "");
        return ;
    }

*/


}
