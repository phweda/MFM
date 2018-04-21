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

import Phweda.MFM.MFM;
import Phweda.MFM.MFMListBuilder;
import Phweda.MFM.MFMPlayLists;
import Phweda.MFM.MFM_Constants;
import Phweda.utils.SwingUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.TreeMap;
import java.util.TreeSet;

import static Phweda.MFM.UI.MFM_Components.getResources;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/30/2016
 * Time: 10:55 AM
 */
class MFM_Menubar {
    private static MFM_Menubar ourInstance = new MFM_Menubar();
    // For recreating this menu when user's lists changes
    private JMenu myListMenu = null;
    private MFMAction mfmActionShowList = new MFMAction("Show List", null);
    private MFMAction mfmActionListBuilder = new MFMAction(MFMAction.ListBuilderAction, null);

    private MFM_Menubar() {
    }

    public static MFM_Menubar getInstance() {
        return ourInstance;
    }

    static JMenuBar getJMenubar() {
        return ourInstance.createMenuBar();
    }

    private JMenuBar createMenuBar() {
        JMenuBar MenuBar = new JMenuBar();
        MenuBar.add(createMFMmenu());
        MenuBar.add(createLogsmenu());
        // MenuBar.add(createCommandmenu()); // Legacy capability
        MenuBar.add(createVideomenu());
        MenuBar.add(createDATmenu());
        MenuBar.add(createResourcesmenu());
        MenuBar.add(createListmenu());
        MenuBar.add(createUImenu());
        MenuBar.add(createHelpmenu());

        MenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        return MenuBar;
    }

    private JMenu createLogsmenu() {
        JMenu logsMenu = new JMenu("Logs");
        logsMenu.setMnemonic(KeyEvent.VK_L);

        MFMAction MFMLog = new MFMAction(MFMAction.LogAction, null);
        MFMAction MAME_OUTPUT = new MFMAction(MFMAction.MAME_OUTPUTAction, null);
        MFMAction MFMErrorLog = new MFMAction(MFMAction.ErrorLogAction, null);
        MFMAction MFMGCLog = null;
        if (MFM.GCLog != null && MFM.GCLog.exists()) {
            MFMGCLog = new MFMAction(MFMAction.GCLogAction, null);
        }
        MFMAction MFMZipLogs = new MFMAction(MFMAction.ZipLogsAction, null);
        MFMAction MFMPostToPastie = new MFMAction(MFMAction.PostToPastieAction, null);
        MFMAction MAMEControlsDUMP = new MFMAction(MFMAction.MAMEControlsDUMPAction, null);

        MFMAction MFMCleanLogs = new MFMAction(MFMAction.CleanLogsAction, null);

        JMenuItem item;
        item = logsMenu.add(MFMLog);
        item.setMnemonic(KeyEvent.VK_S);
        item = logsMenu.add(MAME_OUTPUT);
        item.setMnemonic(KeyEvent.VK_M);
        logsMenu.add(new JSeparator());
        item = logsMenu.add(MFMErrorLog);
        item.setMnemonic(KeyEvent.VK_E);

        if (MFMGCLog != null) {
            item = logsMenu.add(MFMGCLog);
            item.setMnemonic(KeyEvent.VK_G);
        }

        if (MFM.isDebug() || MFM.isSystemDebug()) {
            logsMenu.add(new JSeparator());
            item = logsMenu.add(MFMZipLogs);
            item.setMnemonic(KeyEvent.VK_Z);

            item = logsMenu.add(MFMPostToPastie);
            item.setMnemonic(KeyEvent.VK_P);

            item = logsMenu.add(MAMEControlsDUMP);
        }

        logsMenu.add(new JSeparator());
        item = logsMenu.add(MFMCleanLogs);
        item.setMnemonic(KeyEvent.VK_MINUS);

        logsMenu.setMargin(new Insets(0, 20, 0, 0));

        return logsMenu;
    }

    private JMenu createCommandmenu() {
        JMenu commandMenu = new JMenu("Commands");
        commandMenu.setMnemonic(KeyEvent.VK_C);

        MFMAction MAMECommandBuilder = new MFMAction(MFMAction.MAMECommandBuilderAction,
                MFM_Components.getResources().getImageIcon(MFMUI_Resources.MAME_LOGO_SMALL));

        JMenuItem item;
        item = commandMenu.add(MAMECommandBuilder);
        commandMenu.add(new JSeparator());
        commandMenu.setMargin(new Insets(0, 20, 0, 0));

        return commandMenu;
    }

    private JMenuItem createMyCommandsmenu() {
        return null;
    }

    private JMenu createMFMmenu() {
        JMenu MFMMenu = new JMenu("MFM");

        MFMAction openFile = new MFMAction(MFMAction.OpenFileAction, null);

        MFMAction settings = new MFMAction("Settings",
                getResources().getImageIcon(MFMUI_Resources.S_PNG));

        MFMAction openDataSet = new MFMAction(MFMAction.LoadDataSetAction, null);
        MFMAction parseMAME = new MFMAction(MFMAction.ParseMAMEAction, null);

        MFMAction exit = new MFMAction("Exit",
                getResources().getImageIcon(MFMUI_Resources.EX_PNG));

        JMenuItem item;
        item = MFMMenu.add(openFile);
        item = MFMMenu.add(settings);
        item.setMnemonic(KeyEvent.VK_S);
        MFMMenu.add(new JSeparator());
        item = MFMMenu.add(openDataSet);
        item = MFMMenu.add(parseMAME);
        MFMMenu.add(new JSeparator());
        item = MFMMenu.add(exit);
        item.setMnemonic(KeyEvent.VK_E);

        MFMMenu.setMargin(new Insets(0, 20, 0, 0));

        return MFMMenu;
    }

    private JMenu createListmenu() {
        JMenu listMenu = new JMenu("List");
        listMenu.setMnemonic(KeyEvent.VK_L);

        MFMAction ImportList = new MFMAction(MFMAction.ImportListCommand,
                getResources().getImageIcon(MFMUI_Resources.I_PNG));
        MFMAction RemoveList = new MFMAction("Remove List", null);
        MFMAction ListEditor = new MFMAction("List Editor", null);
        MFMAction ListtoFile = new MFMAction(MFMAction.SaveListtoFileAction, null);
        MFMAction ListDatatoFile = new MFMAction(MFMAction.SaveListDataAction, null);

        JMenuItem item;
        item = listMenu.add(createListbuildermenu());
        listMenu.add(ListEditor);
        listMenu.add(new JSeparator());
        listMenu.add(createMyListmenu());
        listMenu.add(createMFMLists());
        listMenu.add(new JSeparator());
        listMenu.add(ImportList);
        listMenu.add(RemoveList);
        listMenu.add(new JSeparator());
        listMenu.add(ListtoFile);
        listMenu.add(ListDatatoFile);
        listMenu.setMargin(new Insets(0, 20, 0, 0));

        return listMenu;
    }

    private JMenu createListbuildermenu() {
        JMenu listBuilder = new JMenu(MFMAction.ListBuilderAction);

        JMenuItem item;
        item = listBuilder.add(mfmActionListBuilder);
        item.setActionCommand(MFMAction.ListBuilderAction);
        item.setText(ListBuilderUI.Previous);

        item = listBuilder.add(mfmActionListBuilder);
        item.setActionCommand(MFMAction.ListBuilderAction);
        item.setText(ListBuilderUI.New);
        listBuilder.add(new JSeparator());

        for (String name : MFMPlayLists.getInstance().getListBuilderPlaylistsKeys()) {
            item = listBuilder.add(mfmActionListBuilder);
            item.setActionCommand(MFMAction.ListBuilderAction);
            item.setText(name);
        }
        return listBuilder;
    }

    private JMenu createVideomenu() {
        JMenu videoMenu = new JMenu("Video");
        videoMenu.setMnemonic(KeyEvent.VK_V);

        MFMAction VDub = new MFMAction(MFMAction.VDUBAction,
                getResources().getImageIcon(MFMUI_Resources.VDUB));

        MFMAction FFmpeg = new MFMAction(MFMAction.FFmpegAction, null);
        MFMAction GIFImagesList = new MFMAction(MFMAction.GIFImagesAction, null);
        MFMAction AVIImagesList = new MFMAction(MFMAction.AVIImagesAction, null);
        AVIImagesList.setEnabled(false);

        JMenuItem item;
        item = videoMenu.add(VDub);
        videoMenu.add(FFmpeg);
        videoMenu.add(new JSeparator());
        videoMenu.add(GIFImagesList);
        videoMenu.add(AVIImagesList);

        videoMenu.setMargin(new Insets(0, 20, 0, 0));

        return videoMenu;
    }

    private JMenu createDATmenu() {
        JMenu dataMenu = new JMenu("DAT");
        dataMenu.setMnemonic(KeyEvent.VK_D);

        MFMAction ListtoDAT = new MFMAction(MFMAction.ListtoDATAction, null);
        MFMAction DATtoList = new MFMAction(MFMAction.DATtoListAction, null);
        MFMAction FilterDATbyList = new MFMAction(MFMAction.FilterDATbyListAction, null);
        MFMAction FilterDATbyExternalList = new MFMAction(MFMAction.FilterDATbyExternalListAction, null);

        JMenuItem item;
        item = dataMenu.add(ListtoDAT);
        item = dataMenu.add(DATtoList);
        dataMenu.add(new JSeparator());
        item = dataMenu.add(FilterDATbyList);
        item = dataMenu.add(FilterDATbyExternalList);
        dataMenu.setMargin(new Insets(0, 20, 0, 0));

        return dataMenu;
    }

    void updateListMenu() {
        createMyListmenu();
    }

    private JMenu createMyListmenu() {

        if (myListMenu == null) {
            myListMenu = new JMenu("My Lists");
        }

        myListMenu.removeAll();

        JMenuItem item;
        TreeMap<String, TreeSet<String>> pls = MFMPlayLists.getInstance().getMyPlayListsTree();
        for (String name : pls.keySet()) {
            item = myListMenu.add(mfmActionShowList);
            item.setActionCommand("Show List");
            item.setText(name);
        }

        myListMenu.repaint();
        myListMenu.validate();
        return myListMenu;
    }

    private JMenu createMFMLists() {
        JMenu mfmList = new JMenu("MFM Lists");
        JMenuItem item;
        TreeMap<String, TreeSet> builtinPL = MFMPlayLists.getInstance().getMFMplaylistsTree();

        JMenu languageList = new JMenu("Language Lists");
        for (String name : MFMListBuilder.getLanguagesListsMap().keySet()) {
            item = languageList.add(mfmActionShowList);
            item.setActionCommand("Show List");
            item.setText(name);
        }
        mfmList.add(languageList);
        mfmList.add(new JSeparator());

        for (String name : builtinPL.keySet()) {
            item = mfmList.add(mfmActionShowList);
            item.setActionCommand("Show List");
            item.setText(name);
        }
        return mfmList;
    }

    private JMenu createUImenu() {
        JMenu UIMenu = new JMenu("UI");
        UIMenu.setMnemonic(KeyEvent.VK_U);

        JMenu FontMenu = new JMenu("FontSize");
        FontMenu.setMnemonic(KeyEvent.VK_F);

        MFMAction VeryLarge = new MFMAction(MFM_Constants.VERYLARGE,
                getResources().getImageIcon(MFMUI_Resources.Arrow_PNG));
        MFMAction Large = new MFMAction(MFM_Constants.LARGE, getResources().getImageIcon
                (MFMUI_Resources.Arrow_PNG));
        MFMAction Normal = new MFMAction(MFM_Constants.NORMAL,
                getResources().getImageIcon(MFMUI_Resources.Minus_PNG));

        JMenuItem item;
        item = FontMenu.add(VeryLarge);
//        item.setMnemonic(KeyEvent.VK_I);
        item = FontMenu.add(Large);
        item.setMnemonic(KeyEvent.VK_D);
        item = FontMenu.add(Normal);
        item.setMnemonic(KeyEvent.VK_N);

        UIMenu.add(FontMenu);

        JMenu LandFMenu = new JMenu("Look & Feel");
        FontMenu.setMnemonic(KeyEvent.VK_LEFT);

        boolean first = true;// Note it is a guess that the first is always the System Default
        for (Object name : SwingUtils.LandFNames()) {
            if (first) {
                first = false;
                LandFMenu.add(new MFMAction(name.toString(),
                        getResources().getImageIcon(MFMUI_Resources.CHECKMARK_PNG)));
            } else {
                LandFMenu.add(new MFMAction(name.toString(), null));
            }
        }
        UIMenu.add(LandFMenu);

        UIMenu.setMargin(new Insets(0, 20, 0, 0));

        return UIMenu;
    }

    private JMenu createResourcesmenu() {
        JMenu resourcesMenu = new JMenu("Resources");
        MFMAction scan = new MFMAction(MFMAction.ScanResourcesAction, null);
        MFMAction copyResources = new MFMAction(MFMAction.CopyResourcesAction, null);
        MFMAction saveResources = new MFMAction(MFMAction.SaveResourcesAction, null);

        resourcesMenu.add(scan);
        resourcesMenu.add(copyResources);
        resourcesMenu.add(saveResources);
        resourcesMenu.setMargin(new Insets(0, 20, 0, 0));
        return resourcesMenu;
    }

    private JMenu createHelpmenu() {
        JMenu helpMenu = new JMenu("Help");
        MFMAction ma = new MFMAction("Help", null);
        JMenuItem item;
        item = helpMenu.add(ma);
        item.setMnemonic('h');

        item.setText("MFM User Guide");
        item.setActionCommand("Help");
        item = helpMenu.add(ma);
        item.setText("About");
        item.setActionCommand("Help");
        item = helpMenu.add(ma);
        item.setText("MFM Copyright");
        item.setActionCommand("Help");
        item = helpMenu.add(ma);
        item.setText("GNU GPL");
        item.setActionCommand("Help");

        helpMenu.setMargin(new Insets(0, 20, 0, 0));

        return helpMenu;
    }
}
