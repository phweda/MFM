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
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 6/20/2017
 * Time: 3:10 PM
 */
public class AddRemoveDividerUI extends BasicSplitPaneUI {

    private JButton addButtonV;
    private JButton removeButtonV;
    private JButton addButtonH;
    private JButton removeButtonH;

    static  final String ADD = "Add";
    static  final String REMOVE = "Remove";

    private final Font defaultFont = new Font("Arial", Font.PLAIN, 14);
    private final Dimension size = new Dimension(25,25);

    {
        addButtonV = new JButton("<html>\u2192<br>\u2192</html>");
        removeButtonV = new JButton("<html>\u2190<br>\u2190</html>");
        addButtonH = new JButton("\u2193\u2193");
        removeButtonH = new JButton("\u2191\u2191");
        addButtonV.setFont(defaultFont);
        removeButtonV.setFont(defaultFont);
        addButtonH.setFont(defaultFont);
        removeButtonH.setFont(defaultFont);

        addButtonV.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButtonV.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButtonH.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButtonH.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addButtonV.setToolTipText(ADD);
        removeButtonV.setToolTipText(REMOVE);
        addButtonH.setToolTipText(ADD);
        removeButtonH.setToolTipText(REMOVE);

        addButtonV.setActionCommand(ADD);
        removeButtonV.setActionCommand(REMOVE);
        addButtonH.setActionCommand(ADD);
        removeButtonH.setActionCommand(REMOVE);

        addButtonV.setMargin(new Insets(0, -5, 0, -5));
        removeButtonV.setMargin(new Insets(0, -5, 0, -5));
    }

    AddRemoveDividerUI(int orientation) {
        this.setOrientation(orientation);
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
            public int getDividerSize() {
                return ((int) size.getWidth());
            }
        };

        if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            divider.setLayout(new BoxLayout(divider, BoxLayout.Y_AXIS));
            divider.add(Box.createVerticalGlue());
            divider.add(addButtonV);
            divider.add(Box.createVerticalStrut(50));
            divider.add(removeButtonV);
            divider.add(Box.createVerticalGlue());

        } else {
            divider.setLayout(new BoxLayout(divider, BoxLayout.X_AXIS));
            divider.add(Box.createHorizontalGlue());
            divider.add(addButtonH);
            divider.add(Box.createHorizontalStrut(50));
            divider.add(removeButtonH);
            divider.add(Box.createHorizontalGlue());
        }
        return divider;
    }

    void addActionListener(ActionListener listener){
        addButtonH.addActionListener(listener);
        addButtonV.addActionListener(listener);
        removeButtonH.addActionListener(listener);
        removeButtonV.addActionListener(listener);
    }
}
