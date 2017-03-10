/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

import Phweda.MFM.MFM;

import javax.swing.*;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/29/11
 * Time: 6:59 PM
 */
public class MFMUI {
    private static MFMUI mfmui = null;
    private MFMUI_Setup setup;

    private static final Color MFMcolor = new Color(163, 47, 22);
    private static final Color MFMSettingsBGcolor = new Color(250, 240, 230);
    private static final Color MFMLightGreen = new Color(102, 255, 102);
    private static final Color MFMLightRed = new Color(255, 48, 48);

    private MFMUI() {

        // GUI initializer
        setup = MFMUI_Setup.getInstance();
    }

    // NOTE UNCOMMENT if you run directly from this class
    //   private static MAMEInfo MI = new MAMEInfo();
    //   private static MAMESettings MS = MAMESettings.getInstance();

    public static void main(String[] args) {

        // set a new dismiss delay milliseconds in millis
        ToolTipManager.sharedInstance().setDismissDelay(20000);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                mfmui = new MFMUI();
                MFMUI_Setup.getController().init();
            }
        });


    }

    public static Color getMFMcolor() {
        return MFMcolor;
    }

    public static Color getMFMSettingsBGcolor() {
        return MFMSettingsBGcolor;
    }

    public static Color getMFMLightGreen() {
        return MFMLightGreen;
    }

    public static Color getMFMLightRed() {
        return MFMLightRed;
    }

}