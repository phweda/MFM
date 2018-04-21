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

package Phweda.utils;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: Aug 19, 2003
 * Time: 8:36:38 PM
 *
 */

/**
 * Outputs the current RAM usage to the Outputstream
 * Usage:
 * MemoryMonitor mm = new MemoryMonitor(10000, System.out);
 * Thread mmThread = new Thread(mm);
 * mmThread.start();
 */
public class MemoryMonitor implements Runnable {

    private static int millis;
    private static PrintWriter printWriter;

    public MemoryMonitor(int millis, OutputStream outputStream) {
        MemoryMonitor.millis = millis;
        printWriter = new PrintWriter(outputStream);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        Thread thread = Thread.currentThread();

        //    SimpleDateFormat sdateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.S");
        String time = null;
        Date now = null;

        //noinspection InfiniteLoopStatement
        while (true) {

            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long usedMemory = totalMemory - freeMemory;

            double div = 1024;

            double freeMemoryMB = (freeMemory / div) / div;
            double totalMemoryMB = (totalMemory / div) / div;
            double usedMemoryMB = (usedMemory / div) / div;

            DecimalFormat format = new DecimalFormat("###,###,###,###,###.##");

            printWriter.append("\n---------------------------------------------------------------------------\r\n");
            printWriter.append("freeMemory  is : ");
            printWriter.append(format.format(freeMemory));
            printWriter.append(" bytes & ");
            printWriter.append(format.format(freeMemoryMB) + " MB\r\n");
            printWriter.append("totalMemory is : ");
            printWriter.append(format.format(totalMemory));
            printWriter.append(" bytes & ");
            printWriter.append(format.format(totalMemoryMB));
            printWriter.append(" MB\r\n");
            printWriter.append("usedMemory  is : ");
            printWriter.append(format.format(usedMemory));
            printWriter.append(" bytes & ");
            printWriter.append(format.format(usedMemoryMB) + " MB\r\n");
            printWriter.append("***************************************************************************\n");

            printWriter.flush();
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
