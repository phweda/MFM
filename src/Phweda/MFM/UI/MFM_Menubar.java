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
import java.awt.event.KeyEvent;
import java.util.SortedSet;
import java.util.TreeMap;

import static Phweda.MFM.UI.MFMAction.HELP;
import static Phweda.MFM.UI.MFMAction.SHOW_LIST;
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
    private MFMAction mfmActionListBuilder = new MFMAction(MFMAction.LIST_BUILDER, null);

    private MFM_MenuBar() {
    }

    public static MFM_MenuBar getInstance() {
        return ourInstance;
    }

    static JMenuBar getJMenubar() {
        return ourInstance.createMenuBar();
    }

    private JMenuBar createMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(createMFMmenu());
        jMenuBar.add(createLogsmenu());
        // jMenuBar.add(createCommandmenu()); // Legacy capability nOTE retain for possible future reinstatement
        jMenuBar.add(createVideomenu());
        jMenuBar.add(createDATmenu());
        jMenuBar.add(createResourcesmenu());
        jMenuBar.add(createListmenu());
        jMenuBar.add(createUImenu());
        jMenuBar.add(createHelpmenu());

        jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        return jMenuBar;
    }

    private JMenu createLogsmenu() {
        JMenu logsMenu = new JMenu("Logs");
        logsMenu.setMnemonic(KeyEvent.VK_L);

        MFMAction mfmLog = new MFMAction(MFMAction.LOG, null);
        MFMAction mameOutput = new MFMAction(MFMAction.MAME_OUTPUT, null);
        MFMAction mfmErrorLog = new MFMAction(MFMAction.ERROR_LOG, null);
        MFMAction mfmGCLog = null;
        if (MFM.GCLog != null && MFM.GCLog.exists()) {
            mfmGCLog = new MFMAction(MFMAction.GC_LOG, null);
        }
        MFMAction mfmZipLogs = new MFMAction(MFMAction.ZIP_LOGS, null);
        MFMAction mfmPostToPastie = new MFMAction(MFMAction.POST_ERRORS_TO_PASTIE, null);
        MFMAction mameControlsDUMP = new MFMAction(MFMAction.DUMP_WAYS_CONTROLS, null);

        MFMAction mfmCleanLogs = new MFMAction(MFMAction.CLEAN_LOGS, null);

        JMenuItem item;
        item = logsMenu.add(mfmLog);
        item.setMnemonic(KeyEvent.VK_S);
        item = logsMenu.add(mameOutput);
        item.setMnemonic(KeyEvent.VK_M);
        logsMenu.add(new JSeparator());
        item = logsMenu.add(mfmErrorLog);
        item.setMnemonic(KeyEvent.VK_E);

        if (mfmGCLog != null) {
            item = logsMenu.add(mfmGCLog);
            item.setMnemonic(KeyEvent.VK_G);
        }

        if (MFM.isDebug() || MFM.isSystemDebug()) {
            logsMenu.add(new JSeparator());
            item = logsMenu.add(mfmZipLogs);
            item.setMnemonic(KeyEvent.VK_Z);

            item = logsMenu.add(mfmPostToPastie);
            item.setMnemonic(KeyEvent.VK_P);

            logsMenu.add(mameControlsDUMP);
        }

        logsMenu.add(new JSeparator());
        item = logsMenu.add(mfmCleanLogs);
        item.setMnemonic(KeyEvent.VK_MINUS);

        logsMenu.setMargin(new Insets(0, 20, 0, 0));

        return logsMenu;
    }

    @SuppressWarnings("squid:Unused")
    private JMenu createCommandmenu() {
        JMenu commandMenu = new JMenu("Commands");
        commandMenu.setMnemonic(KeyEvent.VK_C);

        MFMAction mameCommandbuilder = new MFMAction(MFMAction.MAME_COMMAND_BUILDER,
                MFM_Components.getResources().getImageIcon(MFMUI_Resources.MAME_LOGO_SMALL));

        commandMenu.add(mameCommandbuilder);
        commandMenu.add(new JSeparator());
        commandMenu.setMargin(new Insets(0, 20, 0, 0));

        return commandMenu;
    }

    @SuppressWarnings("squid:Unused")
    private JMenuItem createMyCommandsmenu() {
        return null;
    }

    private JMenu createMFMmenu() {
        JMenu mfmMenu = new JMenu("MFM");

        MFMAction openFile = new MFMAction(MFMAction.OPEN_FILE, null);

        MFMAction settings = new MFMAction("Settings",
                getResources().getImageIcon(MFMUI_Resources.S_PNG));

        MFMAction openDataSet = new MFMAction(MFMAction.LOAD_DATA_SET, null);
        MFMAction parseMAME = new MFMAction(MFMAction.PARSE_MAME_RUNNABLE, null);
        MFMAction parseMAMEAll = new MFMAction(MFMAction.PARSE_MAME_ALL, null);

        MFMAction exit = new MFMAction("Exit",
                getResources().getImageIcon(MFMUI_Resources.EX_PNG));

        // This makes the JMenu widen fixme
        JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem("Show XML");
        jcbmi.setAction(new MFMAction(MFMAction.SHOW_MAME_XML, null));
        jcbmi.setSelected(MFMSettings.getInstance().isShowXML());
        jcbmi.addItemListener(event -> {
            MFMUI_Setup.getInstance().refreshLeftPane();
            MFMSettings.getInstance().setShowXML(((AbstractButton) event.getItemSelectable()).isSelected());
        });

        jcbmi.setHorizontalTextPosition(JButton.CENTER);
        jcbmi.setIconTextGap(8);
        jcbmi.setMargin(new Insets(0, 0, 0, 0));

        JMenuItem item;
        mfmMenu.add(openFile);
        item = mfmMenu.add(settings);
        item.setMnemonic(KeyEvent.VK_S);
        mfmMenu.add(new JSeparator());
        mfmMenu.add(openDataSet);
        mfmMenu.add(jcbmi);
        mfmMenu.add(new JSeparator());
        mfmMenu.add(parseMAME);
        mfmMenu.add(parseMAMEAll);
        mfmMenu.add(new JSeparator());
        item = mfmMenu.add(exit);
        item.setMnemonic(KeyEvent.VK_E);

        mfmMenu.setMargin(new Insets(0, 20, 0, 0));

        return mfmMenu;
    }

    private JMenu createListmenu() {
        JMenu listMenu = new JMenu("List");
        listMenu.setMnemonic(KeyEvent.VK_L);

        MFMAction importList = new MFMAction(MFMAction.IMPORT_LIST,
                getResources().getImageIcon(MFMUI_Resources.I_PNG));
        MFMAction removeList = new MFMAction("Remove List", null);
        MFMAction listEditor = new MFMAction("List Editor", null);
        MFMAction listtoFile = new MFMAction(MFMAction.SAVE_LIST_TO_FILE, null);
        MFMAction listDatatoFile = new MFMAction(MFMAction.SAVE_LIST_DATA, null);

        listMenu.add(createListbuildermenu());
        listMenu.add(listEditor);
        listMenu.add(new JSeparator());
        listMenu.add(createMyListmenu());
        listMenu.add(createMFMLists());
        listMenu.add(new JSeparator());
        listMenu.add(importList);
        listMenu.add(removeList);
        listMenu.add(new JSeparator());
        listMenu.add(listtoFile);
        listMenu.add(listDatatoFile);
        listMenu.setMargin(new Insets(0, 20, 0, 0));

        return listMenu;
    }

    private JMenu createListbuildermenu() {
        JMenu listBuilder = new JMenu(MFMAction.LIST_BUILDER);

        JMenuItem item;
        item = listBuilder.add(mfmActionListBuilder);
        item.setActionCommand(MFMAction.LIST_BUILDER);
        item.setText(ListBuilderUI.PREVIOUS);

        item = listBuilder.add(mfmActionListBuilder);
        item.setActionCommand(MFMAction.LIST_BUILDER);
        item.setText(ListBuilderUI.NEW);
        listBuilder.add(new JSeparator());

        for (String name : MFMPlayLists.getInstance().getListBuilderPlaylistsKeys()) {
            item = listBuilder.add(mfmActionListBuilder);
            item.setActionCommand(MFMAction.LIST_BUILDER);
            item.setText(name);
        }
        return listBuilder;
    }

    private JMenu createVideomenu() {
        JMenu videoMenu = new JMenu("Video");
        videoMenu.setMnemonic(KeyEvent.VK_V);

        MFMAction vDub = new MFMAction(MFMAction.VDUB,
                getResources().getImageIcon(MFMUI_Resources.VDUB));

        MFMAction ffmpeg = new MFMAction(MFMAction.FFMPEG, null);
        MFMAction gifImagesList = new MFMAction(MFMAction.EXTRACT_GIF_IMAGES, null);
        MFMAction aviImagesList = new MFMAction(MFMAction.EXTRACT_AVI_IMAGES, null);
        aviImagesList.setEnabled(false);

        videoMenu.add(vDub);
        videoMenu.add(ffmpeg);
        videoMenu.add(new JSeparator());
        videoMenu.add(gifImagesList);
        videoMenu.add(aviImagesList);

        videoMenu.setMargin(new Insets(0, 20, 0, 0));

        return videoMenu;
    }

    private JMenu createDATmenu() {
        JMenu dataMenu = new JMenu("DAT");
        dataMenu.setMnemonic(KeyEvent.VK_D);

        MFMAction listtoDAT = new MFMAction(MFMAction.CREATE_DAT_FROM_LIST, null);
        MFMAction dattolist = new MFMAction(MFMAction.CREATE_LIST_FROM_DAT, null);
        MFMAction validateDAT = new MFMAction(MFMAction.VALIDATE_DAT, null);
        MFMAction filterDATbyList = new MFMAction(MFMAction.FILTER_DAT_BY_LIST, null);
        MFMAction filterDATbyExternalList = new MFMAction(MFMAction.FILTER_DAT_BY_EXTERNAL_LIST, null);

        dataMenu.add(listtoDAT);
        dataMenu.add(dattolist);
        dataMenu.add(new JSeparator());
        dataMenu.add(validateDAT);
        dataMenu.add(new JSeparator());
        dataMenu.add(filterDATbyList);
        dataMenu.add(filterDATbyExternalList);
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
        TreeMap<String, SortedSet<String>> pls = MFMPlayLists.getInstance().getMyPlayListsTree();
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
        TreeMap<String, SortedSet<String>> builtinPL = MFMPlayLists.getInstance().getMFMplaylistsTree();

        JMenu softwareLists = new JMenu("Software Lists");
        MenuScroller.setScrollerFor(softwareLists, 20, 50, 3, 3);
        for (String name : MAMEInfo.getSoftwareLists().getSoftwarelistsMap().keySet()) {
            item = softwareLists.add(mfmActionShowList);
            item.setActionCommand(SHOW_LIST);
            item.setText(name);
        }
        mfmList.add(softwareLists);

        JMenu languageList = new JMenu("Language Lists");
        for (String name : MFMListBuilder.getLanguagesListsMap().keySet()) {
            item = languageList.add(mfmActionShowList);
            item.setActionCommand(SHOW_LIST);
            item.setText(name);
        }
        mfmList.add(languageList);
        mfmList.add(new JSeparator());

        for (String name : builtinPL.keySet()) {
            item = mfmList.add(mfmActionShowList);
            item.setActionCommand(SHOW_LIST);
            item.setText(name);
        }
        return mfmList;
    }

    private JMenu createUImenu() {
        JMenu uiMenu = new JMenu("UI");
        uiMenu.setMnemonic(KeyEvent.VK_U);

        JMenu fontMenu = new JMenu("FontSize");
        fontMenu.setMnemonic(KeyEvent.VK_F);

        MFMAction veryLarge = new MFMAction(MFM_Constants.VERYLARGE,
                getResources().getImageIcon(MFMUI_Resources.ARROW_PNG));
        MFMAction large = new MFMAction(MFM_Constants.LARGE, getResources().getImageIcon
                (MFMUI_Resources.ARROW_PNG));
        MFMAction normal = new MFMAction(MFM_Constants.NORMAL,
                getResources().getImageIcon(MFMUI_Resources.MINUS_PNG));

        JMenuItem item;
        fontMenu.add(veryLarge);
        item = fontMenu.add(large);
        item.setMnemonic(KeyEvent.VK_D);
        item = fontMenu.add(normal);
        item.setMnemonic(KeyEvent.VK_N);

        uiMenu.add(fontMenu);

        JMenu landFMenu = new JMenu("Look & Feel");
        fontMenu.setMnemonic(KeyEvent.VK_LEFT);

        boolean first = true;// Note it is a guess that the first is always the System Default
        for (Object name : SwingUtils.lookandFeelNames()) {
            if (first) {
                first = false;
                landFMenu.add(new MFMAction(name.toString(),
                        getResources().getImageIcon(MFMUI_Resources.CHECKMARK_PNG)));
            } else {
                landFMenu.add(new MFMAction(name.toString(), null));
            }
        }
        uiMenu.add(landFMenu);

        uiMenu.setMargin(new Insets(0, 20, 0, 0));

        return uiMenu;
    }

    private JMenu createResourcesmenu() {
        JMenu resourcesMenu = new JMenu("Resources");
        MFMAction scan = new MFMAction(MFMAction.SCAN_RESOURCES, null);
        MFMAction copyResources = new MFMAction(MFMAction.COPY_RESOURCES, null);
        MFMAction saveResources = new MFMAction(MFMAction.SAVE_RESOURCES_TO_FILE, null);

        resourcesMenu.add(scan);
        resourcesMenu.add(copyResources);
        resourcesMenu.add(saveResources);
        resourcesMenu.setMargin(new Insets(0, 20, 0, 0));
        return resourcesMenu;
    }

    private JMenu createHelpmenu() {
        JMenu helpMenu = new JMenu(HELP);
        MFMAction ma = new MFMAction(HELP, null);
        JMenuItem item;
        item = helpMenu.add(ma);
        item.setMnemonic('h');

        item.setText("MFM User Guide");
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText("About");
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText("MFM Copyright");
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText("GNU GPL");
        item.setActionCommand(HELP);

        helpMenu.setMargin(new Insets(0, 20, 0, 0));

        return helpMenu;
    }
}
