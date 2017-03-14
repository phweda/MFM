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

import Phweda.MFM.MFM;

import java.text.DecimalFormat;
import java.util.Date;

/*
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: Aug 19, 2003
 * Time: 8:36:38 PM
 *
 */

public class MemoryMonitor implements Runnable {

    private static int millis;

    public MemoryMonitor(int millis) {
        MemoryMonitor.millis = millis;
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

            //    now = new GregorianCalendar().getTime();
            //    time = sdateFormat.format(now);

            long freeMemory = Runtime.getRuntime().freeMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long usedMemory = totalMemory - freeMemory;

            double div = 1024;

            double freeMemoryMB = (freeMemory / div) / div;
            double totalMemoryMB = (totalMemory / div) / div;
            double usedMemoryMB = (usedMemory / div) / div;

            DecimalFormat format = new DecimalFormat("###,###,###,###,###.##");

            MFM.logger.out("\n---------------------------------------------------------------------------\r\n" +
                    "freeMemory  is : " + format.format(freeMemory) + " bytes & " + format.format(freeMemoryMB) + " MB\r\n" +
                    "totalMemory is : " + format.format(totalMemory) + " bytes & " + format.format(totalMemoryMB) + " MB\r\n" +
                    "usedMemory  is : " + format.format(usedMemory) + " bytes & " + format.format
                    (usedMemoryMB) + " MB\r\n" +
                    "***************************************************************************\n");

            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
