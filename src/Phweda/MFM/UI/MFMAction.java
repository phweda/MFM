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
import Phweda.MFM.Utils.MFM_Clean_Logs;
import Phweda.utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/19/11
 * Time: 3:28 PM
 */
public class MFMAction extends AbstractAction {
    public static final String UPDATE_VERSION = "Update Version";
    static final String ERROR_LOG = "Error Log";
    static final String LOG = "Log";
    static final String GC_LOG = "GC Log";
    static final String CLEAN_LOGS = "Clean Logs";
    static final String RECORD_MACHINE = "Record Machine";
    static final String PLAYBACK_TO_AVI = "Playback to AVI";
    static final String PLAY_RECORD_TO_AVI = "Play & Record to AVI";
    static final String PLAYBACK_MACHINE = "Playback Machine";
    static final String MAME_OUTPUT = "MAME_OUTPUT";
    static final String VDUB = "VDub";
    static final String HANDBRAKE = "Handbrake";
    static final String FFMPEG = "FFmpeg";
    static final String EXTRACT_GIF_IMAGES = "Extract GIF Images";
    static final String EXTRACT_AVI_IMAGES = "Extract AVI Images";
    static final String ADD_TO_LIST = "Add to List";
    static final String SHOW_LIST = "Show List";
    static final String REMOVE_FROM_LIST = "Remove from List";
    static final String HELP = "Help";
    static final String GOTO_CLONEOF = "GOTO cloneof";
    static final String EDIT_VIDEO = "Edit Video";
    static final String MAME_COMMAND_BUILDER = "MAME Command Builder";
    static final String CONVERT_FILES = "Convert Files";
    static final String CROP_AVI = "Crop AVI";
    static final String LIST_BUILDER = "List Builder";
    static final String PLAY_VIDEO = "Play Video";
    static final String SHOW_CONTROLS_DEVICES = "Show Controls & Devices";
    static final String COPY_RESOURCES = "Copy Resources";
    static final String SAVE_RESOURCES_TO_FILE = "Save Resources to File";
    static final String IMPORT_LIST = "Import List";
    static final String ZIP_LOGS = "Zip Logs";
    static final String POST_ERRORS_TO_PASTIE = "Post Errors to Pastie";
    static final String DUMP_WAYS_CONTROLS = "DUMP WAYS Controls";
    static final String SAVE_LIST_DATA = "Save List Data";
    static final String SCAN_RESOURCES = "Scan Resources";
    static final String SAVE_LIST_TO_FILE = "Save List to File";
    static final String LIST_EDITOR = "List Editor";
    static final String CREATE_DAT_FROM_LIST = "Create DAT from List";
    static final String CREATE_LIST_FROM_DAT = "Create List from DAT";
    static final String VALIDATE_DAT = "Validate DAT";
    static final String FILTER_DAT_BY_LIST = "Filter DAT by List";
    static final String FILTER_DAT_BY_EXTERNAL_LIST = "Filter DAT by External List";
    static final String SHOW_MAME_XML = "Show MAME XML";
    static final String SHOW_MACHINE_XML = "Show Machine XML";
    static final String SHOW_SOFTWARE_XML = "Show Software XML";

    static final String SHOW_INFO = "Show Info";
    static final String SHOW_MANUAL = "Show Manual";
    static final String SHOW_HISTORY = "Show History";
    static final String RUN_MACHINE = "Run Machine";
    static final String OPEN_IMAGE = "Open Image";
    static final String OPEN_FILE = "Open File";
    public static final String LOAD_DATA_SET = "Load Data Set";
    static final String PARSE_MAME_ALL = "Parse MAME All";
    static final String PARSE_MAME_RUNNABLE = "Parse MAME Runnable";
    @SuppressWarnings("WeakerAccess")
    static final String MACHINE_VIDEO_INFO = "Machine Video Info";
    @SuppressWarnings("WeakerAccess")
    static final String EXIT = "Exit";

    private static MFMController controller = MFMUI_Setup.getInstance().getController();

    public MFMAction(String text, Icon icon) {
        super(text, icon);
    }

    @SuppressWarnings("squid:S1479")
    public void actionPerformed(ActionEvent e) {
        try {

            if (MFM.isDebug()) {
                MFM.getLogger().addToList("MFMAction action command is: " + e.getActionCommand());
            }

            switch (e.getActionCommand()) {

                case "File Operations":
                    //MFMFileOps.fileOps(((JMenuItem) e.getSource()).getText());
                    MFM.getLogger().out("CALL TO FILE OPS FOUND!!");
                    break;

                case HELP:
                    controller.showHelp(((JMenuItem) e.getSource()).getText());
                    break;

                case SHOW_LIST:
                    // Get the Play List name from JMenuItem text
                    controller.changeList(((JMenuItem) e.getSource()).getText());
                    break;

                case RUN_MACHINE:
                    if (MFMController.getMachineListTable().hasFocus()) {
                        controller.runGame("");
                    }
                    break;

                case RECORD_MACHINE:
                    controller.runGame(MAMECommands.RECORD);
                    break;

                case MACHINE_VIDEO_INFO:
                    controller.showGameVideoInfo();
                    break;

                case PLAYBACK_MACHINE:
                    controller.runGame(MAMECommands.PLAYBACK);
                    break;

                case PLAYBACK_TO_AVI:
                    controller.runGame(MAMECommands.PLAYBACK_TO_AVI);
                    break;

                case PLAY_RECORD_TO_AVI:
                    controller.runGame(MAMECommands.AVIWRITE);
                    break;

                case VDUB:
                    controller.vDUB();
                    break;

                case EDIT_VIDEO:
                    controller.vDUB();
                    break;

                case CROP_AVI:
                    controller.cropAVI();
                    break;

                case FFMPEG:
                    controller.ffmpegSettings();
                    break;

                case CONVERT_FILES:
                    controller.runFFmpeg();
                    break;

                case EXTRACT_GIF_IMAGES:
                    if (e.getSource() instanceof JMenuItem) {
                        controller.videoAction(EXTRACT_GIF_IMAGES);
                    }
                    break;

                case EXTRACT_AVI_IMAGES:
                    controller.videoAction(EXTRACT_AVI_IMAGES);
                    break;

                case LIST_BUILDER:
                    controller.showListBuilder(((JMenuItem) e.getSource()).getText());
                    break;

                case MFM_Constants.VERYLARGE:
                    // Order matters
                    SwingUtils.resizeFonts(MFM_Constants.VERYLARGEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT + MFM_Constants.VERYLARGEINT);
                    controller.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.VERYLARGE);
                    break;
                case MFM_Constants.LARGE:
                    SwingUtils.resizeFonts(MFM_Constants.LARGEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT + MFM_Constants.LARGEINT);
                    controller.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.LARGE);
                    break;
                case MFM_Constants.NORMAL:
                    SwingUtils.resizeFonts(MFM_Constants.FONTSIZEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT);
                    controller.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.NORMAL);
                    break;

                case SHOW_HISTORY:
                    controller.showHistory();
                    break;

                case SHOW_MANUAL:
                    controller.showManual();
                    break;

                case SHOW_INFO:
                    controller.showInfo();
                    break;

                case SHOW_CONTROLS_DEVICES:
                    controller.showControlsDevices();
                    break;

                case OPEN_IMAGE:
                    controller.openImage();
                    break;

                case PLAY_VIDEO:
                    controller.showVideo();
                    break;

                case "Settings":
                    controller.showSettings();
                    break;

                case OPEN_FILE:
                    controller.openFile();
                    break;

                case LOAD_DATA_SET:
                    controller.loadDataSet(true);
                    break;

                case PARSE_MAME_RUNNABLE:
                    controller.parseMAME(false);
                    break;

                case PARSE_MAME_ALL:
                    controller.parseMAME(true);
                    break;

                case MAME_COMMAND_BUILDER:
                    controller.commandDialog();
                    break;

                case "Create List":
                    controller.showListBuilder("");
                    break;

                case COPY_RESOURCES:
                    controller.copyResources(true);
                    break;

                case SAVE_RESOURCES_TO_FILE:
                    controller.copyResources(false);
                    break;

                case SCAN_RESOURCES:
                    controller.scanResources();
                    break;

                case IMPORT_LIST:
                    controller.importList();
                    break;

                case "Remove List":
                    controller.removeList();
                    break;

                case LIST_EDITOR:
                    controller.showListEditor();
                    break;

                case ADD_TO_LIST:
                    controller.addtoList();
                    break;

                case REMOVE_FROM_LIST:
                    controller.removefromList();
                    break;

                case SAVE_LIST_TO_FILE:
                    controller.listtoFile();
                    break;

                case CREATE_DAT_FROM_LIST:
                    controller.listtoDAT();
                    break;

                case CREATE_LIST_FROM_DAT:
                    controller.dattoList();
                    break;

                case VALIDATE_DAT:
                    controller.validateDAT();
                    break;

                case FILTER_DAT_BY_LIST:
                    controller.filterDATbyList();
                    break;

                case FILTER_DAT_BY_EXTERNAL_LIST:
                    controller.filterDATbyExternalList();
                    break;

                case SAVE_LIST_DATA:
                    controller.listMachinesToCSV();
                    break;

                case GOTO_CLONEOF:
                    controller.gotoCloneof();
                    break;

                case "Save Command":

                    break;

                case "Run Command":

                    break;

                case SHOW_MAME_XML:
                    controller.showMAMEtree();
                    break;

                case SHOW_MACHINE_XML:
                    controller.showItemXML();
                    break;

                case SHOW_SOFTWARE_XML:
                    controller.showItemXML();
                    break;

                case LOG:
                    controller.showLog(LOG);
                    break;

                case MAME_OUTPUT:
                    controller.showLog(MAME_OUTPUT);
                    break;

                case ERROR_LOG:
                    controller.showLog(ERROR_LOG);
                    break;

                case GC_LOG:
                    controller.showLog(GC_LOG);
                    break;

                case ZIP_LOGS:
                    controller.zipLogs();
                    break;

                case POST_ERRORS_TO_PASTIE:
                    controller.postToPastie();
                    break;

                case DUMP_WAYS_CONTROLS:
                    // controller.MAMEControlsDUMP();
                    break;

                case CLEAN_LOGS:
                    MFM_Clean_Logs.cleanLogs();
                    break;

                case UPDATE_VERSION:
                    controller.refreshVersion();
                    break;

                case EXIT:
                    MFM.getLogger().addToList("MFM Closing on user command", true);
                    MFMPlayLists.getInstance().persistPlayLists();
                    MFM.exit();
                    break;

                // NOTE this is a quick Hack for Look and Feel Settings
                default:
                    String command = e.getActionCommand();
                    if (SwingUtils.lookandFeelNames().contains(command)) {
                        controller.changeLnF(command);
                        controller.showMFMmessage("Changed UI to " + command);
                        MFMSettings.getInstance().MFMLookAndFeel(command);

                    }
                    controller.updateUI();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}

