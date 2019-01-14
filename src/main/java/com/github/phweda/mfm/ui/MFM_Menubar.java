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

import com.github.phweda.mfm.MAMEInfo;
import com.github.phweda.mfm.MFMListBuilder;
import com.github.phweda.mfm.MFMPlayLists;
import com.github.phweda.mfm.MFMSettings;
import com.github.phweda.utils.SwingUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.SortedMap;
import java.util.SortedSet;

import static com.github.phweda.mfm.MFM_Constants.*;
import static com.github.phweda.mfm.ui.MFMAction.HELP;
import static com.github.phweda.mfm.ui.MFM_Components.getResources;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/30/2016
 * Time: 10:55 AM
 */
@SuppressWarnings("WeakerAccess")
class MFM_MenuBar {
    public static final String LOOK_FEEL = "Look & Feel";
    public static final String FONT_SIZE = "FontSize";
    public static final String UI = "UI";
    public static final String LANGUAGE_LISTS = "Language Lists";
    public static final String MFM_LISTS = "MFM Lists";
    public static final String SHOW_LIST = "Show List";
    public static final String MY_LISTS = "My Lists";
    public static final String DAT = "DAT";
    public static final String VIDEO = "Video";
    public static final String LIST = "List";
    public static final String SHOW_XML = "Show XML";
    public static final String EXIT = "Exit";
    public static final String SETTINGS = MFMAction.SETTINGS;
    public static final String MFM = "MFM";
    public static final String LOGS = "Logs";
    private static final MFM_MenuBar ourInstance = new MFM_MenuBar();
    public static final int LEFT = 20;
    // For recreating this menu when user's lists changes
    private JMenu myListMenu = null;
    private final Action mfmActionShowList = new MFMAction(SHOW_LIST, null);
    private final Action mfmActionListBuilder = new MFMAction(MFMAction.LIST_BUILDER, null);

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
        // jMenuBar.add(createCommandmenu()); // Legacy capability NOTE retain for possible future reinstatement
        jMenuBar.add(createVideomenu());
        jMenuBar.add(createDATmenu());
        jMenuBar.add(createResourcesmenu());
        jMenuBar.add(createListmenu());
        jMenuBar.add(createUImenu());
        jMenuBar.add(createHelpmenu());

        jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        return jMenuBar;
    }

    private static JMenu createLogsmenu() {
        JMenu logsMenu = new JMenu(LOGS);
        logsMenu.setMnemonic(KeyEvent.VK_L);

        Action mfmLog = new MFMAction(MFMAction.LOG, null);
        Action mameOutput = new MFMAction(MFMAction.MAME_OUTPUT, null);
        Action mfmErrorLog = new MFMAction(MFMAction.ERROR_LOG, null);
        Action mfmGCLog = null;
        if ((com.github.phweda.mfm.MFM.getGcLog() != null) && com.github.phweda.mfm.MFM.getGcLog().exists()) {
            mfmGCLog = new MFMAction(MFMAction.GC_LOG, null);
        }
        Action mfmZipLogs = new MFMAction(MFMAction.ZIP_LOGS, null);
        Action mfmPostToPastie = new MFMAction(MFMAction.POST_ERRORS_TO_PASTIE, null);
        Action mameControlsDUMP = new MFMAction(MFMAction.DUMP_WAYS_CONTROLS, null);

        Action mfmCleanLogs = new MFMAction(MFMAction.CLEAN_LOGS, null);

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

        if (com.github.phweda.mfm.MFM.isDebug() || com.github.phweda.mfm.MFM.isSystemDebug()) {
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

        logsMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return logsMenu;
    }

    @SuppressWarnings("squid:Unused")
    private static JMenu createCommandmenu() {
        JMenu commandMenu = new JMenu(COMMANDS);
        commandMenu.setMnemonic(KeyEvent.VK_C);

        Action mameCommandbuilder = new MFMAction(MFMAction.MAME_COMMAND_BUILDER,
                MFM_Components.getResources().getImageIcon(MFMUI_Resources.MAME_LOGO_SMALL));

        commandMenu.add(mameCommandbuilder);
        commandMenu.add(new JSeparator());
        commandMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return commandMenu;
    }

    @SuppressWarnings("squid:Unused")
    private static JMenuItem createMyCommandsmenu() {
        return null;
    }

    private static JMenu createMFMmenu() {
        JMenu mfmMenu = new JMenu(MFM);

        Action openFile = new MFMAction(MFMAction.OPEN_FILE, null);

        Action settings = new MFMAction(SETTINGS,
                getResources().getImageIcon(MFMUI_Resources.S_PNG));

        Action openDataSet = new MFMAction(MFMAction.LOAD_DATA_SET, null);
        Action parseMAME = new MFMAction(MFMAction.PARSE_MAME_RUNNABLE, null);
        Action parseMAMEAll = new MFMAction(MFMAction.PARSE_MAME_ALL, null);

        Action exit = new MFMAction(EXIT,
                getResources().getImageIcon(MFMUI_Resources.EX_PNG));

        // NOTE This makes the JMenu widen in certain L&Fs
        JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem(SHOW_XML);
        jcbmi.setAction(new MFMAction(MFMAction.SHOW_MAME_XML, null));
        jcbmi.setSelected(MFMSettings.getInstance().isShowXML());
        jcbmi.addItemListener(event -> {
            MFMUI_Setup.getInstance().refreshLeftPane();
            MFMSettings.getInstance().setShowXML(((AbstractButton) event.getItemSelectable()).isSelected());
        });

        jcbmi.setHorizontalTextPosition(SwingConstants.CENTER);
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

        mfmMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return mfmMenu;
    }

    private JMenu createListmenu() {
        JMenu listMenu = new JMenu(LIST);
        listMenu.setMnemonic(KeyEvent.VK_L);

        Action importList = new MFMAction(MFMAction.IMPORT_LIST,
                getResources().getImageIcon(MFMUI_Resources.I_PNG));
        Action removeList = new MFMAction(MFMAction.REMOVE_LIST, null);
        Action listEditor = new MFMAction(MFMAction.LIST_EDITOR, null);
        Action listtoFile = new MFMAction(MFMAction.SAVE_LIST_TO_FILE, null);
        Action listDatatoFile = new MFMAction(MFMAction.SAVE_LIST_DATA, null);

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
        listMenu.setMargin(new Insets(0, LEFT, 0, 0));

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

    private static JMenu createVideomenu() {
        JMenu videoMenu = new JMenu(VIDEO);
        videoMenu.setMnemonic(KeyEvent.VK_V);

        Action vDub = new MFMAction(MFMAction.VDUB,
                getResources().getImageIcon(MFMUI_Resources.VDUB));

        Action ffmpeg = new MFMAction(MFMAction.FFMPEG, null);
        Action gifImagesList = new MFMAction(MFMAction.EXTRACT_GIF_IMAGES, null);
        Action aviImagesList = new MFMAction(MFMAction.EXTRACT_AVI_IMAGES, null);
        aviImagesList.setEnabled(false);

        videoMenu.add(vDub);
        videoMenu.add(ffmpeg);
        videoMenu.add(new JSeparator());
        videoMenu.add(gifImagesList);
        videoMenu.add(aviImagesList);

        videoMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return videoMenu;
    }

    private static JMenu createDATmenu() {
        JMenu dataMenu = new JMenu(DAT);
        dataMenu.setMnemonic(KeyEvent.VK_D);

        Action listtoDAT = new MFMAction(MFMAction.CREATE_DAT_FROM_LIST, null);
        Action dattolist = new MFMAction(MFMAction.CREATE_LIST_FROM_DAT, null);
        Action validateDAT = new MFMAction(MFMAction.VALIDATE_DAT, null);
        Action validateXML = new MFMAction(MFMAction.VALIDATE_XML, null);
        Action filterDATbyList = new MFMAction(MFMAction.FILTER_DAT_BY_LIST, null);
        Action filterDATbyExtList = new MFMAction(MFMAction.FILTER_DAT_BY_EXTERNAL_LIST, null);

        dataMenu.add(listtoDAT);
        dataMenu.add(dattolist);
        dataMenu.add(new JSeparator());
        dataMenu.add(validateDAT);
        dataMenu.add(validateXML);
        dataMenu.add(new JSeparator());
        dataMenu.add(filterDATbyList);
        dataMenu.add(filterDATbyExtList);
        dataMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return dataMenu;
    }

    public final void updateListMenu() {
        createMyListmenu();
    }

    private JMenu createMyListmenu() {

        if (myListMenu == null) {
            myListMenu = new JMenu(MY_LISTS);
        }

        myListMenu.removeAll();

        JMenuItem item;
        SortedMap<String, SortedSet<String>> pls = MFMPlayLists.getInstance().getMyPlayListsTree();
        for (String name : pls.keySet()) {
            item = myListMenu.add(mfmActionShowList);
            item.setActionCommand(SHOW_LIST);
            item.setText(name);
        }

        myListMenu.repaint();
        myListMenu.validate();
        return myListMenu;
    }

    private JMenu createMFMLists() {
        JMenu mfmList = new JMenu(MFM_LISTS);
        JMenuItem item;
        SortedMap<String, SortedSet<String>> builtinPL = MFMPlayLists.getInstance().getMFMplaylistsTree();

        JMenu softwareLists = new JMenu(SOFTWARE_LISTS);
        MenuScroller.setScrollerFor(softwareLists, LEFT, 50, 3, 3);
        for (String name : MAMEInfo.getSoftwareLists().getSoftwarelistsMap().keySet()) {
            item = softwareLists.add(mfmActionShowList);
            item.setActionCommand(MFMAction.SHOW_LIST);
            item.setText(name);
        }
        mfmList.add(softwareLists);

        JMenu languageList = new JMenu(LANGUAGE_LISTS);
        for (String name : MFMListBuilder.getLanguagesListsMap().keySet()) {
            item = languageList.add(mfmActionShowList);
            item.setActionCommand(MFMAction.SHOW_LIST);
            item.setText(name);
        }
        mfmList.add(languageList);
        mfmList.add(new JSeparator());

        for (String name : builtinPL.keySet()) {
            item = mfmList.add(mfmActionShowList);
            item.setActionCommand(MFMAction.SHOW_LIST);
            item.setText(name);
        }
        return mfmList;
    }

    private static JMenu createUImenu() {
        JMenu uiMenu = new JMenu(UI);
        uiMenu.setMnemonic(KeyEvent.VK_U);

        JMenu fontMenu = new JMenu(FONT_SIZE);
        fontMenu.setMnemonic(KeyEvent.VK_F);

        Action veryLarge = new MFMAction(VERYLARGE, getResources().getImageIcon(MFMUI_Resources.ARROW_PNG));
        Action large = new MFMAction(LARGE, getResources().getImageIcon(MFMUI_Resources.ARROW_PNG));
        Action normal = new MFMAction(NORMAL, getResources().getImageIcon(MFMUI_Resources.MINUS_PNG));

        JMenuItem item;
        fontMenu.add(veryLarge);
        item = fontMenu.add(large);
        item.setMnemonic(KeyEvent.VK_D);
        item = fontMenu.add(normal);
        item.setMnemonic(KeyEvent.VK_N);

        uiMenu.add(fontMenu);

        JMenu landFMenu = new JMenu(LOOK_FEEL);
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

        uiMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return uiMenu;
    }

    private static JMenu createResourcesmenu() {
        JMenu resourcesMenu = new JMenu("Resources");
        Action scan = new MFMAction(MFMAction.SCAN_RESOURCES, null);
        Action copyResources = new MFMAction(MFMAction.COPY_RESOURCES, null);
        Action saveResources = new MFMAction(MFMAction.SAVE_RESOURCES_TO_FILE, null);

        resourcesMenu.add(scan);
        resourcesMenu.add(copyResources);
        resourcesMenu.add(saveResources);
        resourcesMenu.setMargin(new Insets(0, LEFT, 0, 0));
        return resourcesMenu;
    }

    private static JMenu createHelpmenu() {
        JMenu helpMenu = new JMenu(HELP);
        Action ma = new MFMAction(HELP, null);
        JMenuItem item;
        item = helpMenu.add(ma);
        item.setMnemonic(KeyEvent.VK_H);
        item.setText(MFM_USER_GUIDE);
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText(HOT_KEYS);
        item.setActionCommand(HELP);
        helpMenu.add(new JSeparator());
        item = helpMenu.add(ma);
        item.setText(ABOUT);
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText(MFM_COPYRIGHT);
        item.setActionCommand(HELP);
        item = helpMenu.add(ma);
        item.setText(GNU_GPL);
        item.setActionCommand(HELP);

        helpMenu.setMargin(new Insets(0, LEFT, 0, 0));

        return helpMenu;
    }
}
