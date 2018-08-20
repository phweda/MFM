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

import org.jdesktop.swingx.painter.BusyPainter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/23/2016
 * Time: 8:15 PM
 */
public class MFMBusyPainter extends BusyPainter {
    private final long startMillis = System.currentTimeMillis();

    MFMBusyPainter(Shape point, Shape trajectory) {
        super(point, trajectory);
    }

    @Override
    protected void doPaint(Graphics2D g, Object t, int width, int height) {
        super.doPaint(g, t, width, height);
        g.drawString(getTime(), (width / 7) * 2, (int) (height / 1.82));  //
    }

    private String getTime() {
        return (new SimpleDateFormat("mm:ss")).format(new Date(System.currentTimeMillis() - startMillis));
    }
}
