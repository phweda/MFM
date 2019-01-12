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

package com.github.phweda.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class ClockPanel extends JPanel implements Runnable {
    private transient Thread thread;

    public ClockPanel() {
        setPreferredSize(new Dimension(60, 30));
        //   setFont(new Font("Arial", Font.BOLD, 16));
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(super.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(super.getForeground());
        g.drawString(timeNow(), 20, 25);
    }

    private String timeNow() {
        Calendar now = Calendar.getInstance();
        int hrs = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        return zero(hrs) + ":" + zero(min) + ":" + zero(sec);
    }

    private String zero(int num) {
        return (num < 10) ? ("0" + num) : ("" + num);
    }

    public void start() {
        if (thread == null) thread = new Thread(this);
        thread.start();
    }

    public void run() {
        while (thread == Thread.currentThread()) {
            repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Clock thread InterruptedException");
                Thread.currentThread().interrupt();
            }

        }
    }
}
