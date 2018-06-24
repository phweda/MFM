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

import Phweda.MFM.*;
import Phweda.utils.SwingUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.TreeMap;
import java.util.TreeSet;

import static Phweda.MFM.UI.MFMAction.HelpAction;
import static Phweda.MFM.UI.MFMAction.ShowListAction;
import static Phweda.MFM.UI.MFM_Components.getResources;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/30/2016
 * Time: 10:55 AM
 */
class MFM_MenuBar {
    private static MFM_MenuBar ourInstance = new MFM_MenuBar();
    // For recreating this menu when user's lists changes
    private JMenu myListMenu = null;
    private MFMAction mfmActionShowList = new MFMAction("Show List", null);
    private MFMAction mfmActionListBuilder = new MFMAction(MFMAction.ListBuilderAction, null);

    private MFM_MenuBar() {
    }

    public static MFM_MenuBar getInstance() {
        return ourInstance;
    }

    static JMenuBar getJMenubar() {
        return ourInstance.createMenuBar();
    }

    private JMenuBar createMenuBar() {
        JMenuBar MenuBar = new JMenuBar();
        MenuBar.add(createMFMmenu());
        MenuBar.add(createLogsmenu());
        // MenuBar.add(createCommandmenu()); // Legacy capability nOTE retain for possible future reinstatement
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

            logsMenu.add(MAMEControlsDUMP);
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

        commandMenu.add(MAMECommandBuilder);
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
        MFMAction parseMAMEAll = new MFMAction(MFMAction.ParseMAME_AllAction, null);

        MFMAction exit = new MFMAction("Exit",
                getResources().getImageIcon(MFMUI_Resources.EX_PNG));

        // This makes the JMenu widen fixme
        JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem("Show XML");
        jcbmi.setAction(new MFMAction(MFMAction.MAME_XMLAction, null));
        jcbmi.setSelected(MFMSettings.getInstance().isShowXML());
        jcbmi.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                MFMUI_Setup.getInstance().refreshLeftPane();
                MFMSettings.getInstance().setShowXML(((AbstractButton)e.getItemSelectable()).isSelected());
            }
        });

        jcbmi.setHorizontalTextPosition(JButton.CENTER);
        jcbmi.setIconTextGap(8);
        jcbmi.setMargin(new Insets(0,0,0,0));

        JMenuItem item;
        MFMMenu.add(openFile);
        item = MFMMenu.add(settings);
        item.setMnemonic(KeyEvent.VK_S);
        MFMMenu.add(new JSeparator());
        MFMMenu.add(openDataSet);
        MFMMenu.add(jcbmi);
        MFMMenu.add(new JSeparator());
        MFMMenu.add(parseMAME);
        MFMMenu.add(parseMAMEAll);
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

        listMenu.add(createListbuildermenu());
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

        videoMenu.add(VDub);
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
        MFMAction ValidateDAT = new MFMAction(MFMAction.ValidateDATAction, null);
        MFMAction FilterDATbyList = new MFMAction(MFMAction.FilterDATbyListAction, null);
        MFMAction FilterDATbyExternalList = new MFMAction(MFMAction.FilterDATbyExternalListAction, null);

        dataMenu.add(ListtoDAT);
        dataMenu.add(DATtoList);
        dataMenu.add(new JSeparator());
        dataMenu.add(ValidateDAT);
        dataMenu.add(new JSeparator());
        dataMenu.add(FilterDATbyList);
        dataMenu.add(FilterDATbyExternalList);
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

        JMenu softwareLists = new JMenu("Software Lists");
        MenuScroller.setScrollerFor(softwareLists, 20, 50, 3, 3);
        for (String name : MAMEInfo.getSoftwareLists().getSoftwarelistsMap().keySet()) {
            item = softwareLists.add(mfmActionShowList);
            item.setActionCommand(ShowListAction);
            item.setText(name);
        }
        mfmList.add(softwareLists);

        JMenu languageList = new JMenu("Language Lists");
        for (String name : MFMListBuilder.getLanguagesListsMap().keySet()) {
            item = languageList.add(mfmActionShowList);
            item.setActionCommand(ShowListAction);
            item.setText(name);
        }
        mfmList.add(languageList);
        mfmList.add(new JSeparator());

        for (String name : builtinPL.keySet()) {
            item = mfmList.add(mfmActionShowList);
            item.setActionCommand(ShowListAction);
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
        FontMenu.add(VeryLarge);
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
        JMenu helpMenu = new JMenu(HelpAction);
        MFMAction ma = new MFMAction(HelpAction, null);
        JMenuItem item;
        item = helpMenu.add(ma);
        item.setMnemonic('h');

        item.setText("MFM User Guide");
        item.setActionCommand(HelpAction);
        item = helpMenu.add(ma);
        item.setText("About");
        item.setActionCommand(HelpAction);
        item = helpMenu.add(ma);
        item.setText("MFM Copyright");
        item.setActionCommand(HelpAction);
        item = helpMenu.add(ma);
        item.setText("GNU GPL");
        item.setActionCommand(HelpAction);

        helpMenu.setMargin(new Insets(0, 20, 0, 0));

        return helpMenu;
    }
}
