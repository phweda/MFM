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

package com.github.phweda.mfm.ui;

import com.github.phweda.mfm.MAMEexe;
import com.github.phweda.mfm.MFM;
import com.github.phweda.mfm.MFMSettings;
import com.github.phweda.mfm.MFM_Constants;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

import static com.github.phweda.mfm.ui.MFMUI_Resources.MFM_ICON_PNG;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/7/2015
 * Time: 2:27 PM
 */
public final class MFMUI_Setup {
    private static final ImageIcon mfmIcon = MFMUI_Resources.getInstance().getImageIcon(MFM_ICON_PNG);
    private static final int LOCATION_220 = 220;
    private static final int LOCATION_260 = 260;
    private static final int LOCATION_300 = 300;
    private static final int MFM_BORDER_COLOR = -3407821;
    private static final double RESIZE_WEIGHT = 0.65;
    private MFM_Components mfmComponents;
    private static final MFMUI_Setup ourInstance = new MFMUI_Setup();
    private static JFrame frame;
    private final MFMController controller = new MFMController();
    private JSplitPane mfmMainPane;
    private JComponent leftPane;

    private MFMUI_Setup() {
        MAMEexe.setBaseArgs(MFMSettings.getInstance().fullMAMEexePath());
    }

    public static MFMUI_Setup getInstance() {
        return ourInstance;
    }

    private static void getBaseFrame() {
        frame = new JFrame();
        frame.setTitle(MFM.getMfmTitle());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    MFMController getController() {
        return controller;
    }

    void init() {
        controller.init();
    }

    public static void loadDataSet() {
        MFMController.loadDataSet(false, false);
    }

    MFM_Components getMfmComponents() {
        return mfmComponents;
    }

    static ImageIcon getMFMIcon() {
        return mfmIcon;
    }

    JPanel getFillPanel() {
        return mfmComponents.getFillPanel();
    }

    public void updateMenuBar(String newListName) {
        mfmComponents.updateListMenu();
        frame.pack();
        if (!newListName.isEmpty()) {
            MFMController.changeList(newListName);
        }
    }

    JFrame getFrame() {
        if (frame == null) {
            getBaseFrame();
        }

        if (MFM.isSystemDebug()) {
            System.out.println("MFMUI_Setup getFrame");
        }

        mfmComponents = MFM_Components.getInstance();
        frame.setJMenuBar(mfmComponents.getMenuBar());
        setupMainView();
        frame.getContentPane().add(MFM_Components.createStatusBar(frame.getWidth()), BorderLayout.SOUTH);
        frame.setIconImage(mfmIcon.getImage());
        frame.setResizable(true);

        String fontSize = MFMSettings.getInstance().MFMFontSize();
        switch (fontSize) {
            case MFM_Constants.NORMAL:
                mfmMainPane.setDividerLocation(LOCATION_220);
                // mfmMainPane.setDividerLocation(0.12d);
                break;

            case MFM_Constants.LARGE:
                mfmMainPane.setDividerLocation(LOCATION_260);
                // mfmMainPane.setDividerLocation(0.12d);
                break;

            case MFM_Constants.VERYLARGE:
                mfmMainPane.setDividerLocation(LOCATION_300);
                // mfmMainPane.setDividerLocation(0.12d);
                break;

            default:
                mfmMainPane.setDividerLocation(LOCATION_220);
                break;
        }

        frame.pack();
        return frame;
    }

    private void setupMainView() {
        mfmMainPane = new JSplitPane();
        mfmMainPane.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null,
                        TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
                        new Color(MFM_BORDER_COLOR)));
        if (MFM.isListOnly()) {
            mfmMainPane.setRightComponent(new JScrollPane(mfmComponents.getMachineListTable()));
        } else {
            fullView();
        }

        setLeftPane();
        frame.getContentPane().add(mfmMainPane, BorderLayout.CENTER);
    }

    private void fullView() {
        JSplitPane mfmGamePane = new JSplitPane();
        mfmGamePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createRaisedBevelBorder(), null,
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null,
            new Color(MFM_BORDER_COLOR)));
        mfmGamePane.setOneTouchExpandable(false);

        //final JScrollPane scrollPane2 = new JScrollPane();
        //scrollPane2.setViewportView(mfmComponents.getMachineListTable());
        //mfmGamePane.setLeftComponent(scrollPane2);
      mfmGamePane.setLeftComponent(new MachineListForm().getPnlMachineListView());
        mfmGamePane.setRightComponent(mfmComponents.extrasTabbedPane());
        mfmGamePane.setResizeWeight(RESIZE_WEIGHT);

        mfmMainPane.setRightComponent(mfmGamePane);
    }

    private void setLeftPane() {
        if (mfmComponents.getMFMFolderTreeScrollPane() != null) {
            leftPane = mfmComponents.getMFMFolderTreeScrollPane();
        } else if (MFMSettings.getInstance().isShowXML()) {
            leftPane = new JScrollPane(MAMEtoJTree.getInstance(false).getMAMEjTree());
        } else {
            mfmMainPane.setDividerLocation(0); // Minimize Pane instead of revalidating whole UI
        }
        mfmMainPane.setLeftComponent(leftPane);
    }

    void refreshLeftPane() {
        if (leftPane != null) {
            leftPane.removeAll();
        }
        setLeftPane();
        mfmMainPane.validate();
    }

}
