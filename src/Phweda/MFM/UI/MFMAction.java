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
import Phweda.utils.FileUtils;
import Phweda.utils.SwingUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Paths;

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
    static final String MAME_COMMAND_BUILDER = CommandDialog.MAME_COMMAND_BUILDER;
    static final String CONVERT_FILES = "Convert Files";
    static final String CROP_AVI = "Crop AVI";
    static final String LIST_BUILDER = "List Builder";
    static final String PLAY_VIDEO = "Play Video";
    static final String SHOW_CONTROLS_DEVICES = "Show Controls & Devices";
    static final String COPY_RESOURCES = "Copy Resources";
    static final String SAVE_RESOURCES_TO_FILE = "Save Resources to File";
    public static final String IMPORT_LIST = "Import List";
    static final String ZIP_LOGS = "Zip Logs";
    static final String POST_ERRORS_TO_PASTIE = "Post Errors to Pastie";
    static final String DUMP_WAYS_CONTROLS = "DUMP WAYS Controls";
    static final String SAVE_LIST_DATA = "Save List Data";
    static final String SCAN_RESOURCES = "Scan Resources";
    static final String SAVE_LIST_TO_FILE = "Save List to File";
    @SuppressWarnings("WeakerAccess")
    public static final String LIST_EDITOR = "List Editor";
    static final String CREATE_DAT_FROM_LIST = "Create DAT from List";
    static final String CREATE_LIST_FROM_DAT = "Create List from DAT";
    static final String VALIDATE_DAT = "Validate DAT";
    static final String VALIDATE_XML = "Validate XML";
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
    @SuppressWarnings("WeakerAccess")
    public static final String REMOVE_LIST = "Remove List";
    public static final String SETTINGS = "Settings";

    public MFMAction(String text, Icon icon) {
        super(text, icon);
    }

    private static void filterDATbyList() {
        MFMListActions.filterDATbyList();
    }

    private static void filterDATbyExternalList() {
        MFMListActions.filterDATbyExternalList();
    }

    private static void listtoDAT() {
        MFMListActions.listtoDAT();
    }

    private static void listtoFile() {
        MFMListActions.listtoFile();
    }

    private static void runFFmpeg() {
        MFMVideoActions.runFFmpeg();
    }

    private static void showListBuilder(String baseList) {
        MFMListActions.openListBuilder(baseList);
    }

    private static void validateDAT() {
        MFMListActions.pickValidateDAT();
    }

    /**
     * Scan all resource roots for resources for this list.
     * Log and report results
     *
     * @param copy If true copy the files else just report results
     */
    private static void copyResources(final boolean copy) {
        MFMListActions.copyResources(copy);
    }

    private static void scanResources() {
        MFMListActions.scanResources();
    }

    private static void showLog(String log) {
        switch (log) {
            case LOG:
                FileUtils.openFileFromOS(Paths.get(MFM.getLog().getAbsolutePath()));
                break;
            case MAME_OUTPUT:
                FileUtils.openFileFromOS(Paths.get(MFM.getMameout().getAbsolutePath()));
                break;
            case ERROR_LOG:
                FileUtils.openFileFromOS(Paths.get(MFM.getErrorLog().getAbsolutePath()));
                break;
            case GC_LOG:
                FileUtils.openFileFromOS(Paths.get(MFM.getMfmLogsDir() + "MFM_GC_log.txt"));
                break;
            default:
                break;
        }
    }

    private static void showSettings() {
        MFM_SettingsPanel.showSettingsPanel(MFMUI.getSettings());
    }

    @SuppressWarnings("squid:S1479")
    public final void actionPerformed(ActionEvent e) {
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
                    MFMController.showHelp(((AbstractButton) e.getSource()).getText());
                    break;

                case SHOW_LIST:
                    // Get the Play List name from JMenuItem text
                    MFMController.changeList(((AbstractButton) e.getSource()).getText());
                    break;

                case RUN_MACHINE:
                    if (MFMController.getMachineListTable().hasFocus()) {
                        MFMController.runGame("");
                    }
                    break;

                case RECORD_MACHINE:
                    MFMController.runGame(MAMECommands.RECORD);
                    break;

                case MACHINE_VIDEO_INFO:
                    MFMController.showGameVideoInfo();
                    break;

                case PLAYBACK_MACHINE:
                    MFMController.runGame(MAMECommands.PLAYBACK);
                    break;

                case PLAYBACK_TO_AVI:
                    MFMController.runGame(MAMECommands.PLAYBACK_TO_AVI);
                    break;

                case PLAY_RECORD_TO_AVI:
                    MFMController.runGame(MAMECommands.AVIWRITE);
                    break;

                case VDUB:
                    MFMController.vDUB();
                    break;

                case EDIT_VIDEO:
                    MFMController.vDUB();
                    break;

                case CROP_AVI:
                    MFMController.cropAVI();
                    break;

                case FFMPEG:
                    MFMController.ffmpegSettings();
                    break;

                case CONVERT_FILES:
                    runFFmpeg();
                    break;

                case EXTRACT_GIF_IMAGES:
                    if (e.getSource() instanceof JMenuItem) {
                        MFMController.videoAction(EXTRACT_GIF_IMAGES);
                    }
                    break;

                case EXTRACT_AVI_IMAGES:
                    MFMController.videoAction(EXTRACT_AVI_IMAGES);
                    break;

                case LIST_BUILDER:
                    showListBuilder(((AbstractButton) e.getSource()).getText());
                    break;

                case MFM_Constants.VERYLARGE:
                    // Order matters
                    SwingUtils.resizeFonts(MFM_Constants.VERYLARGEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT + MFM_Constants.VERYLARGEINT);
                    MFMController.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.VERYLARGE);
                    break;
                case MFM_Constants.LARGE:
                    SwingUtils.resizeFonts(MFM_Constants.LARGEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT + MFM_Constants.LARGEINT);
                    MFMController.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.LARGE);
                    break;
                case MFM_Constants.NORMAL:
                    SwingUtils.resizeFonts(MFM_Constants.FONTSIZEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT);
                    MFMController.updateUI();
                    MFMSettings.getInstance().MFMFontSize(MFM_Constants.NORMAL);
                    break;

                case SHOW_HISTORY:
                    MFMController.showHistory();
                    break;

                case SHOW_MANUAL:
                    MFMController.showManual();
                    break;

                case SHOW_INFO:
                    MFMController.showInfo();
                    break;

                case SHOW_CONTROLS_DEVICES:
                    MFMController.showControlsDevices();
                    break;

                case OPEN_IMAGE:
                    MFMController.openImage();
                    break;

                case PLAY_VIDEO:
                    MFMController.showVideo();
                    break;

                case SETTINGS:
                    showSettings();
                    break;

                case OPEN_FILE:
                    MFMController.openFile();
                    break;

                case LOAD_DATA_SET:
                    MFMController.loadDataSet(true);
                    break;

                case PARSE_MAME_RUNNABLE:
                    MFMController.parseMAME(false);
                    break;

                case PARSE_MAME_ALL:
                    MFMController.parseMAME(true);
                    break;

                case MAME_COMMAND_BUILDER:
                    MFMController.commandDialog();
                    break;

                case MFM_Constants.CREATE_LIST:
                    showListBuilder("");
                    break;

                case COPY_RESOURCES:
                    copyResources(true);
                    break;

                case SAVE_RESOURCES_TO_FILE:
                    copyResources(false);
                    break;

                case SCAN_RESOURCES:
                    scanResources();
                    break;

                case IMPORT_LIST:
                    MFMController.importList();
                    break;

                case REMOVE_LIST:
                    MFMController.removeList();
                    break;

                case LIST_EDITOR:
                    MFMController.showListEditor();
                    break;

                case ADD_TO_LIST:
                    MFMController.addtoList();
                    break;

                case REMOVE_FROM_LIST:
                    MFMController.removefromList();
                    break;

                case SAVE_LIST_TO_FILE:
                    listtoFile();
                    break;

                case CREATE_DAT_FROM_LIST:
                    listtoDAT();
                    break;

                case CREATE_LIST_FROM_DAT:
                    MFMController.dattoList();
                    break;

                case VALIDATE_DAT:
                    validateDAT();
                    break;

                case VALIDATE_XML:
                    MFMController.validateXML();
                    break;

                case FILTER_DAT_BY_LIST:
                    filterDATbyList();
                    break;

                case FILTER_DAT_BY_EXTERNAL_LIST:
                    filterDATbyExternalList();
                    break;

                case SAVE_LIST_DATA:
                    MFMController.listMachinesToCSV();
                    break;

                case GOTO_CLONEOF:
                    MFMController.gotoCloneof();
                    break;

                case CommandDialog.SAVE_COMMAND:
                    break; // Legacy but maybe future?

                case CommandDialog.RUN_COMMAND:
                    break; // Legacy but maybe future?

                case SHOW_MAME_XML:
                    MFMController.showMAMEXML();
                    break;

                case SHOW_MACHINE_XML:
                    MFMController.showItemXML();
                    break;

                case SHOW_SOFTWARE_XML:
                    MFMController.showItemXML();
                    break;

                case LOG:
                    showLog(LOG);
                    break;

                case MAME_OUTPUT:
                    showLog(MAME_OUTPUT);
                    break;

                case ERROR_LOG:
                    showLog(ERROR_LOG);
                    break;

                case GC_LOG:
                    showLog(GC_LOG);
                    break;

                case ZIP_LOGS:
                    MFMController.zipLogs();
                    break;

                case POST_ERRORS_TO_PASTIE:
                    MFMController.postToPastie();
                    break;

                case DUMP_WAYS_CONTROLS:
                    // MFMController.MAMEControlsDUMP();
                    break;

                case CLEAN_LOGS:
                    MFM_Clean_Logs.cleanLogs();
                    break;

                case UPDATE_VERSION:
                    MFMController.refreshVersion();
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
                        MFMController.changeLnF(command);
                        MFMController.showMFMmessage("Changed UI to " + command);
                        MFMSettings.getInstance().MFMLookAndFeel(command);

                    }
                    MFMController.updateUI();
            }
        } catch (RuntimeException exc) {
            exc.printStackTrace();
        }
    }
}

