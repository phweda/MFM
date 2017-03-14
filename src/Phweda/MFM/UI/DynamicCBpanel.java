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
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/19/2015
 * Time: 11:55 AM
 */

/**
 * JPanel for dynamic lists of checkboxes
 */
public class DynamicCBpanel extends JPanel {

    private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();

    DynamicCBpanel(ArrayList<String> keys, int columns) {
        super();
        this.setLayout(new GridLayout(keys.size() / columns + 1, columns));
        addCheckboxes(keys);
    }

    void setToolTips(Map<String, String> map) {
        checkBoxes.parallelStream()
                .forEach(cb ->
                {
                    cb.setToolTipText(map.get(cb.getText()));
                    System.out.println(map.get(cb.getText()));
                });
    }

    ArrayList<JCheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    private void addCheckboxes(Collection<String> keys) {
        for (String key : keys) {
            JCheckBox checkBox = new JCheckBox(key);
            this.add(checkBox);
            checkBoxes.add(checkBox);
        }
    }

    TreeSet<String> getChecked() {
        TreeSet<String> ts = new TreeSet<String>();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                ts.add(checkBox.getText());
            }
        }
        return ts;
    }

    JCheckBox getJCheckBoxbyText(String text) {
        for (JCheckBox box : getCheckBoxes()) {
            if (box.getText().equalsIgnoreCase(text)) {
                return box;
            }
        }
        return null;
    }
}
