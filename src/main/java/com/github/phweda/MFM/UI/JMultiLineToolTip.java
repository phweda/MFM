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

package com.github.phweda.MFM.UI;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/12/2016
 * Time: 3:37 PM
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;
import java.awt.*;

/**
 * Tooltip class that can wrap the tooltip text into multiple lines.
 * <p>
 * Based on the JMultiLineToolTip class of Zafir Anjum, see the following URL:
 * http://www.codeguru.com/java/articles/122.shtml
 * <p>
 * Obtained by phweda from
 * https://github.com/ntamas/cl1/blob/master/src/java/uk/ac/rhul/cs/cl1/ui/JMultiLineToolTip.java
 */
public class JMultiLineToolTip extends JToolTip {
    /// The preferred fixed width of the tooltip
    private int fixedWidth = 0;
    /// The text of the tooltip
    String text;
    /// The component to which this tooltip is associated
    JComponent jComponent;

    JMultiLineToolTip() {
        updateUI();
    }

    /**
     * Returns the preferred fixed width of the tooltip.
     *
     * @return the preferred fixed width of the tooltip or zero if
     * there is no preferred width.
     */
    int getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Sets the preferred fixed width of the tooltip.
     *
     * @param width the new preferred fixed width or zero if you
     *              don't want to specify a fixed width
     */
    void setFixedWidth(int width) {
        this.fixedWidth = width;
    }

    @Override
    public void updateUI() {
        setUI(MultiLineToolTipUI.createUI(this));
    }
}

/**
 * UI class corresponding to JMultiLineToolTip.
 * <p>
 * Unfortunately I cannot make this class internal to JMultiLineToolTip
 * because of its static members. Meh.
 *
 * @author tamas
 */
class MultiLineToolTipUI extends BasicToolTipUI {
    // Shared instance of this UI class
    private static MultiLineToolTipUI instance = new MultiLineToolTipUI();
    // The JTextArea used to render the multi-line tooltip
    private JTextArea textArea;
    private CellRendererPane rendererPane;
    // The border used by the tooltip
    private Border tooltipBorder = new CompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
    );

    // Returns the shared instance of this UI class
    @SuppressWarnings("squid:S1172")
    public static ComponentUI createUI(JComponent c) {
        return instance;
    }

    // Installs this UI to the given component
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    // Uninstalls this UI from the given component
    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        c.remove(rendererPane);
        rendererPane = null;
    }

    /// Paints the tooltip on the given component using the given graphics context
    @Override
    public void paint(Graphics g, JComponent c) {
        Dimension size = c.getSize();
        // textArea.setBackground(c.getBackground());
        textArea.setBackground(Color.YELLOW);
        rendererPane.paintComponent(g, textArea, c, 0, 0,
                size.width, size.height, true);
    }

    /**
     * Calculates and returns the preferred size of the tooltip
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        String tipText = ((JToolTip) c).getTipText();
        JMultiLineToolTip comp = (JMultiLineToolTip) c;
        Dimension dim;

        if (tipText == null)
            return new Dimension(0, 0);

        textArea = new JTextArea(tipText);
        textArea.setBorder(tooltipBorder);
        rendererPane.removeAll();
        rendererPane.add(textArea);
        textArea.setWrapStyleWord(true);
        int width = comp.getFixedWidth();

        if (width > 0) {
            textArea.setLineWrap(true);
            dim = textArea.getPreferredSize();
            dim.width = width;
            dim.height++;
            textArea.setSize(dim);
        } else {
            textArea.setLineWrap(false);
        }

        dim = textArea.getPreferredSize();
        dim.height++;
        dim.width++;
        return dim;
    }

    /**
     * Calculates and returns the minimum size of the tooltip.
     * <p>
     * The minimum size is equal to the preferred size.
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * Calculates and returns the maximum size of the tooltip.
     * <p>
     * The maximum size is equal to the preferred size.
     */
    @Override
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }
}
