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

import Phweda.MFM.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static Phweda.MFM.UI.MFMUI_Resources.MFM_Icon_PNG;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/7/2015
 * Time: 2:27 PM
 */
public class MFMUI_Setup {
    private static final ImageIcon mfmIcon = MFMUI_Resources.getInstance().getImageIcon(MFM_Icon_PNG);
    static MFM_Components comps;
    private static MFMUI_Setup ourInstance = new MFMUI_Setup();
    private static JFrame frame;
    private static MFMController controller = new MFMController();
    private static JSplitPane MFMmainPane;
    private static JSplitPane MFMGamePane;
    private static MFMInformationPanel progress = new MFMInformationPanel();
    // Here for race condition where lists sizes were not present when ListBuilder is opened
    private MFMListBuilder listBuilder = null;

    private MFMUI_Setup() {
        //    System.out.println("MFMUI_Setup()" + listBuilder.toString());
    }

    public static MFMUI_Setup getInstance() {
        return ourInstance;
    }

    private static JFrame getBaseFrame() {
        frame = new JFrame();
        frame.setTitle(MFM.MFM_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        return frame;
    }

    static MFMController getController() {
        return controller;
    }

    static MFM_Components getComps() {
        return comps;
    }

    static ImageIcon getMFMIcon() {
        return mfmIcon;
    }

    static JPanel getFillPanel() {
        return MFM_Components.getFillPanel();
    }

    public static void updateMenuBar(String newListName) {
        comps.updateListMenu();
        frame.pack();
        if (!newListName.isEmpty()) {
            controller.changeList(newListName);
        }
    }

    public JFrame getFrame() {
        if (frame == null) {
            getBaseFrame();
        }

        if (MFM.isSystemDebug()) {
            System.out.println("MFMUI_Setup getFrame");
        }

        listBuilder = new MFMListBuilder();

        // FIXME: 10/17/2016 where should this be
        if (MFM_Data.getInstance().isStaticChanged()) {
            MFM_Data.getInstance().persistStaticData();
        }

        comps = new MFM_Components(controller);
        frame.setJMenuBar(comps.getMenuBar());
        setupMainView();
        frame.getContentPane().add(comps.createStatusBar(frame.getWidth()), BorderLayout.SOUTH);
        frame.setIconImage(mfmIcon.getImage());
        frame.setResizable(true);

        String fontSize = MFMSettings.MFMFontSize();
        switch (fontSize) {
            case MFM_Constants.NORMAL:
                MFMmainPane.setDividerLocation(220);
                // MFMmainPane.setDividerLocation(0.12d);
                break;

            case MFM_Constants.LARGE:
                MFMmainPane.setDividerLocation(260);
                // MFMmainPane.setDividerLocation(0.12d);
                break;

            case MFM_Constants.VERYLARGE:
                MFMmainPane.setDividerLocation(300);
                // MFMmainPane.setDividerLocation(0.12d);
                break;

            default:
                MFMmainPane.setDividerLocation(220);
                break;
        }

        frame.pack();
        return frame;
    }

    private void setupMainView() {
        MFMmainPane = new JSplitPane();
        MFMmainPane.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null,
                        TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
                        new Color(-3407821)));
        if (MFM.isListOnly()) {
            MFMmainPane.setRightComponent(new JScrollPane(comps.getMachineListTable()));
        } else {
            fullView();
        }

        if (MFM_Components.MFMFolderTreePane() != null) {
            MFMmainPane.setLeftComponent(MFM_Components.MFMFolderTreePane());
        } else {
            MFMmainPane.setLeftComponent(new JScrollPane(MAMEtoJTree.getInstance().getMAMEjTree()));
        }
        frame.getContentPane().add(MFMmainPane, BorderLayout.CENTER);
    }

    private void fullView() {
        MFMGamePane = new JSplitPane();
        MFMGamePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
                new Color(-3407821)));
        MFMGamePane.setOneTouchExpandable(false);

        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setViewportView(comps.getMachineListTable());
        MFMGamePane.setLeftComponent(scrollPane2);
        MFMGamePane.setRightComponent(comps.ExtrasTabbedPane());
        MFMGamePane.setResizeWeight(.65);

        MFMmainPane.setRightComponent(MFMGamePane);
    }

    static class MFMWindow extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            // fixme No longer needed?? 4/7/2017
            //    MFMSettings.getInstance().persistMySettings();
            super.windowClosing(e);
            MFM.logger.addToList("MFM Closing on frame closing command", true);
            MFM.exit();
        }
    }
}
