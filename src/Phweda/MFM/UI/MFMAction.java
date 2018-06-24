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
import Phweda.MFM.Utils.MFMFileOps;
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
    public static final String UpdateVersionAction = "updateVersion";
    static final String ErrorLogAction = "Error Log";
    static final String LogAction = "Log";
    static final String GCLogAction = "GC Log";
    static final String CleanLogsAction = "Clean Logs";
    static final String RecordMachineAction = "Record Machine";
    static final String PlaybacktoAVIAction = "Playback to AVI";
    static final String PlayGametoAVIAction = "Play & Record to AVI";
    static final String PlaybackGameAction = "Playback getMachine";
    static final String MAME_OUTPUTAction = "MAME_OUTPUT";
    static final String VDUBAction = "VDub";
    static final String HandbrakeAction = "Handbrake";
    static final String FFmpegAction = "FFmpeg";
    static final String GIFImagesAction = "Extract GIF Images";
    static final String AVIImagesAction = "Extract AVI Images";
    static final String AddtoListAction = "Add to List";
    static final String ShowListAction = "Show List";
    static final String RemovefromListAction = "Remove from List";
    static final String HelpAction = "Help";
    static final String GOTOcloneofAction = "GOTO cloneof";
    static final String EditVideoAction = "Edit Video";
    static final String MAMECommandBuilderAction = "MAME Command Builder";
    static final String ConvertCommandAction = "Convert Files";
    static final String CropAVIAction = "Crop AVI";
    static final String ListBuilderAction = "List Builder";
    static final String PlayVideoAction = "Play Video";
    static final String ShowControlsDevicesAction = "Show Controls & Devices";
    static final String CopyResourcesAction = "Copy Resources";
    static final String SaveResourcesAction = "Save Resources to File";
    static final String ImportListCommand = "Import List";
    static final String ZipLogsAction = "Zip Logs";
    static final String PostToPastieAction = "Post Errors to Pastie";
    static final String MAMEControlsDUMPAction = "DUMP WAYS Controls";
    static final String SaveListDataAction = "Save List Data";
    static final String ScanResourcesAction = "Scan Resources";
    static final String SaveListtoFileAction = "Save List to File";
    static final String ListEditorAction = "List Editor";
    static final String ListtoDATAction = "Create DAT from List";
    static final String DATtoListAction = "Create List from DAT";
    static final String ValidateDATAction = "Validate DAT";
    static final String FilterDATbyListAction = "Filter DAT by List";
    static final String FilterDATbyExternalListAction = "Filter DAT by External List";
    static final String MAME_XMLAction = "Show MAME XML";
    static final String MACHINE_XMLAction = "Show Machine XML";
    static final String SOFTWARE_XMLAction = "Show Software XML";

    static final String ShowInfoAction = "Show Info";
    static final String ShowManualAction = "Show Manual";
    static final String ShowHistoryAction = "Show History";
    static final String OpenImageAction = "Open Image";
    static final String OpenFileAction = "Open File";

    //    static final String OpenFileAction = "Open File";
    public static final String LoadDataSetAction = "Load Data Set";
    static final String ParseMAME_AllAction = "Parse MAME All";
    static final String ParseMAMEAction = "Parse MAME Runnable";

    private static MFMController controller = MFMUI_Setup.getInstance().getController();
    private final String ExitAction = "Exit";
    private final String GameVideoInfoAction = "Machine Video Info";

    public MFMAction(String text, Icon icon) {
        super(text, icon);
    }

    public void actionPerformed(ActionEvent e) {
        try {

            if (MFM.isDebug()) {
                MFM.logger.addToList("MFMAction action command is: " + e.getActionCommand());
            }
            // First run condition fixme ?? no longer needed?
            if (controller == null) {
                return;
            }
            switch (e.getActionCommand()) {

                case "File Operations":
                    MFMFileOps.fileOps(((JMenuItem) e.getSource()).getText());
                    break;

                case HelpAction:
                    controller.showHelp(((JMenuItem) e.getSource()).getText());
                    break;

                case ShowListAction:
                    // Get the Play List name from JMenuItem text
                    controller.changeList(((JMenuItem) e.getSource()).getText());
                    break;

                case "Run Machine":
                    if (MFMController.getListTable().hasFocus()) {
                        controller.runGame("");
                    }
                    break;

                case RecordMachineAction:
                    controller.runGame(MAMECommands.RECORD);
                    break;

                case GameVideoInfoAction:
                    controller.showGameVideoInfo();
                    break;

                case PlaybackGameAction:
                    controller.runGame(MAMECommands.PLAYBACK);
                    break;

                case PlaybacktoAVIAction:
                    controller.runGame(MAMECommands.PLAYBACKtoAVI);
                    break;

                case PlayGametoAVIAction:
                    controller.runGame(MAMECommands.AVIWRITE);
                    break;

                case VDUBAction:
                    controller.VDUB();
                    break;

                case EditVideoAction:
                    controller.VDUB();
                    break;

                case CropAVIAction:
                    controller.CropAVI();
                    break;

                case FFmpegAction:
                    controller.FFmpegSettings();
                    break;

                case ConvertCommandAction:
                    controller.runFFmpeg();

                case GIFImagesAction:
                    if (e.getSource() instanceof JMenuItem) {
                        controller.videoAction(GIFImagesAction);
                    }
                    break;

                case AVIImagesAction:
                    controller.videoAction(AVIImagesAction);
                    break;

                case ListBuilderAction:
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

                case ShowHistoryAction:
                    controller.showHistory();
                    break;

                case ShowManualAction:
                    controller.showManual();
                    break;

                case ShowInfoAction:
                    controller.showInfo();
                    break;

                case ShowControlsDevicesAction:
                    controller.showControlsDevices();
                    break;

                case OpenImageAction:
                    controller.openImage();
                    break;

                case "Play Video":
                    controller.showVideo();
                    break;

                case "Settings":
                    controller.showSettings();
                    break;

                case OpenFileAction:
                    controller.openFile();
                    break;

                case LoadDataSetAction:
                    controller.loadDataSet(true);
                    break;

                case ParseMAMEAction:
                    controller.parseMAME(false);
                    break;

                case ParseMAME_AllAction:
                    controller.parseMAME(true);
                    break;

                case MAMECommandBuilderAction:
                    controller.commandDialog();
                    break;

                // TODO should we remove this one see Show List?
                case "Create List":
                    controller.showListBuilder("");
                    break;

                case CopyResourcesAction:
                    controller.copyResources(true);
                    break;

                case SaveResourcesAction:
                    controller.copyResources(false);
                    break;

                case ScanResourcesAction:
                    controller.scanResources();
                    break;

                case ImportListCommand:
                    controller.importList();
                    break;

                case "Remove List":
                    controller.removeList();
                    break;

                case ListEditorAction:
                    controller.showListEditor();
                    break;

                case AddtoListAction:
                    controller.addtoList();
                    break;

                case RemovefromListAction:
                    controller.removefromList();
                    break;

                case SaveListtoFileAction:
                    controller.ListtoFile();
                    break;

                case ListtoDATAction:
                    controller.ListtoDAT();
                    break;

                case DATtoListAction:
                    controller.DATtoList();
                    break;

                case ValidateDATAction:
                    controller.ValidateDAT();
                    break;

                case FilterDATbyListAction:
                    controller.FilterDATbyList();
                    break;

                case FilterDATbyExternalListAction:
                    controller.FilterDATbyExternalList();
                    break;

                case SaveListDataAction:
                    controller.listMachinesToCSV();
                    break;

                case GOTOcloneofAction:
                    controller.GOTOcloneof();
                    break;

                case "Save Command":

                    break;

                case "Run Command":

                    break;

                case MAME_XMLAction:
                    controller.showMAMEtree();
                    break;

                case MACHINE_XMLAction:
                    controller.showItemXML();
                    break;

                case SOFTWARE_XMLAction:
                    controller.showItemXML();
                    break;

                case LogAction:
                    controller.showLog(LogAction);
                    break;

                case MAME_OUTPUTAction:
                    controller.showLog(MAME_OUTPUTAction);
                    break;

                case ErrorLogAction:
                    controller.showLog(ErrorLogAction);
                    break;

                case GCLogAction:
                    controller.showLog(GCLogAction);
                    break;

                case ZipLogsAction:
                    controller.zipLogs();
                    break;

                case PostToPastieAction:
                    controller.postToPastie();
                    break;

                case MAMEControlsDUMPAction:
                    // TODO should wire this into Parsing?
                    // controller.MAMEControlsDUMP();
                    break;

                case CleanLogsAction:
                    MFM_Clean_Logs.cleanLogs();
                    break;

                case UpdateVersionAction:
                    controller.refreshVersion();
                    break;

                case ExitAction:
                    MFM.logger.addToList("MFM Closing on user command", true);
                    // fixme is this needed anymore? 4/7/2017
                    MFMPlayLists.getInstance().persistPlayLists();
                    MFM.exit();
                    break;

                // NOTE this is a quick Hack for Look and Feel Settings
                default:
                    String command = e.getActionCommand();
                    if (SwingUtils.LandFNames().contains(command)) {
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

