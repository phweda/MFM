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

package Phweda.MFM.UI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 6/23/2017
 * Time: 12:02 PM
 */
public class ListEditorModel<E> extends DefaultListModel<E> {

    /**
     * Add all elements from input List
     *
     * @param elements
     */
    void addAll(List<E> elements) {
        elements.forEach(element -> {
            if (!this.contains(element)) {
                this.addElement(element);
            }
        });
        this.fireContentsChanged(this, 0, this.getSize());
    }

    /**
     * Remove all elements from input List
     *
     * @param elements
     */
    void removeAll(List<E> elements) {
        elements.forEach(this::removeElement);
        this.fireContentsChanged(this, 0, this.getSize());
    }

    /**
     * Replace existing list with new list
     *
     * @param newList
     */
    public void refreshList(List<E> newList) {
        this.clear();
        newList.forEach(this::addElement);
        this.fireContentsChanged(this, 0, this.getSize());
    }

}
