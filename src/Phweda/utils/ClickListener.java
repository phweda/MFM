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

package Phweda.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class ClickListener extends MouseAdapter implements ActionListener {
    private final static int clickInterval = 500;
    private MouseEvent lastEvent;
    private Timer timer;

    public ClickListener() {
        this(clickInterval);
    }

    private ClickListener(int delay) {
        Integer desktopMultiClickInterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty(
                "awt.multiClickInterval");
        if (desktopMultiClickInterval != null) {
            delay = desktopMultiClickInterval;
        }
        timer = new Timer(delay, this);
    }

    // For testing and validation
    public static void main(String[] args) {
        JFrame frame = new JFrame("Double Click Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addMouseListener(new ClickListener() {

            @Override
            public void singleClick(MouseEvent e) {
                System.out.println("single : " + e.toString());
            }

            @Override
            public void doubleClick(MouseEvent e) {
                System.out.println("double : " + e.toString());
            }
        });
        frame.setPreferredSize(new Dimension(200, 200));
        frame.pack();
        frame.setVisible(true);
    }

    /*
     * NOTE always call super when you override this method
     *      to ensure double click behavior
     *
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        lastEvent = e;
        if (timer.isRunning() && !e.isConsumed() && e.getClickCount() > 1) {
            doubleClick(lastEvent);
            timer.stop();
        } else {
            timer.restart();
        }
    }

    /*
     * NOTE always call super when you override this method
     *      to ensure double click behavior
     *
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.stop();
        singleClick(lastEvent);
    }

    public abstract void singleClick(MouseEvent e);

    public abstract void doubleClick(MouseEvent e);

}