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

import Phweda.MFM.MFM;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

import static Phweda.MFM.UI.MFMUI_Resources.MFM_Icon_PNG;


/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/29/11
 * Time: 6:59 PM
 */
public class MFMUI {
    private static final Color MFMcolor = new Color(163, 47, 22);
    private static final Color MFMSettingsBGcolor = new Color(250, 240, 230);
    private static final Color MFMLightGreen = new Color(102, 255, 102);
    private static final Color MFMLightRed = new Color(255, 48, 48);
    private static MFMUI mfmui = null;
    private MFMUI_Setup setup;
    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final Point screenCenterPoint = new Point(screenSize.width / 2, screenSize.height / 2);
    private static JFrame settingsFrame;
    private static boolean progressRunning = false;
    private static JDialog busyDialog = new JDialog(settingsFrame, MFM.MFM_TITLE);
    private static Thread busyThread = null;

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

    public static void showBusy(boolean start, boolean task) { // task false is empty startup. task true is loading data

        progressRunning = start;
        if (start) {
            busyThread = new Thread() {
                @Override
                public void run() {
                    busyDialog.setLocation(screenCenterPoint.x - 150, screenCenterPoint.y - 50);
                    JXBusyLabel busyLabel = createComplexBusyLabel();
                    if (task) {
                        busyLabel.setText("<HTML>DATA<br>LOADING</HTML>");
                        busyLabel.setToolTipText("MFM Data Loading");
                    } else {
                        busyLabel.setText("<HTML>Parsing<br>MAME data</HTML>");
                        busyLabel.setToolTipText("Parsing MAME Data");
                    }

                    busyDialog.add(busyLabel);
                    busyLabel.setBusy(true);
                    busyDialog.pack();
                    busyDialog.setVisible(true);
                }
            };
            busyThread.start();
        } else {
            busyDialog.setVisible(false);
            busyDialog.dispose();
            busyThread.interrupt();
            busyThread = null;
        }
    }

    private static JXBusyLabel createComplexBusyLabel() {
        JXBusyLabel label = new JXBusyLabel(new Dimension(325, 150));
        // default is 100
        label.setDelay(100);
        /*
        BusyPainter painter = new BusyPainter(
                new Rectangle2D.Float(0.0f, 0.0f, 8.0f, 8.0f),
                new Rectangle2D.Float(20.5f, 20.5f, 75.0f, 75.0f));
*/
        MFMBusyPainter painter = new MFMBusyPainter(
                new Ellipse2D.Double(0.0d, 0.0d, 15.0d, 15.0d),
                new Ellipse2D.Double(10.0d, 10.0d, 125.0d, 125.0d));

        painter.setTrailLength(64);
        painter.setPoints(192);
        painter.setFrame(-1);
        painter.setBaseColor(MFMUI.getMFMcolor());
        painter.setHighlightColor(Color.orange);

        label.setPreferredSize(new Dimension(325, 150));
        label.setMinimumSize(new Dimension(325, 150));
        label.setIcon(new EmptyIcon(150, 150));
        label.setBusyPainter(painter);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 24));
        return label;
    }

    public static boolean isProgressRunning() {
        return progressRunning;
    }

    public static JFrame getSettings() {
        MFM_SettingsPanel.showSettingsPanel(getSettingsFrame());
        return settingsFrame;
    }

    private static JFrame getSettingsFrame() {
        if (settingsFrame != null) {
            settingsFrame.dispose();
        }
        settingsFrame = new JFrame("MFM Settings");
        settingsFrame.setIconImage(MFMUI_Resources.getInstance().getImageIcon(MFM_Icon_PNG).getImage());

        return settingsFrame;
    }



    public static Color getMFMcolor() {
        return MFMcolor;
    }

    static Color getMFMSettingsBGcolor() {
        return MFMSettingsBGcolor;
    }

    static Color getMFMLightGreen() {
        return MFMLightGreen;
    }

    static Color getMFMLightRed() {
        return MFMLightRed;
    }

}