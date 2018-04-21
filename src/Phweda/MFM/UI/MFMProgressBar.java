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

package Phweda.MFM.UI;

import javax.swing.*;
import java.awt.*;

import static java.lang.Thread.sleep;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/17/12
 * Time: 9:16 PM
 */
public class MFMProgressBar extends JProgressBar implements Runnable {
    MFMProgressBar() {
        super(0, 100);
        //    this.setString(title);
        this.setBackground(Color.black);
        this.setForeground(Color.green);
        this.setPreferredSize(new Dimension(600, 30));
        this.setIndeterminate(true);
    }

    void increment(int newValue) {
        this.setIndeterminate(false);
        this.setValue(newValue);
        repaint();
    }

    @Override
    public void run() {

        while (true) {
            increment(this.getValue() + 5);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
