/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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
    private static MFMController controller = MFMUI_Setup.getController();

    public MFMAction(String text, Icon icon) {
        super(text, icon);
    }

    public void actionPerformed(ActionEvent e) {
        try {

            if (MFM.isDebug()) {
                MFM.logger.addToList("MFMAction action command is: " + e.getActionCommand());
            }
            // First run condition
            if (controller == null) {
                return;
            }
            switch (e.getActionCommand()) {

                case "File Operations":
                    MFMFileOps.fileOps(((JMenuItem) e.getSource()).getText());
                    break;

                case "Help":
                    controller.showHelp(((JMenuItem) e.getSource()).getText());
                    break;

                case "Show List":
                    // Get the Play List name from JMenuItem text
                    controller.changeList(((JMenuItem) e.getSource()).getText());
                    break;

                /* TODO remove
                case "Change List":
                    // Get the Play List name from JMenuItem text
                    controller.changeList(e.getActionCommand());
                    break;*/

                case "Run Machine":
                    if (MFMController.getMachineListTable().hasFocus()) {
                        controller.runGame("");
                    }
                    break;

                case "Record Machine":
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
                    MFMSettings.MFMFontSize(MFM_Constants.VERYLARGE);
                    break;
                case MFM_Constants.LARGE:
                    SwingUtils.resizeFonts(MFM_Constants.LARGEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT + MFM_Constants.LARGEINT);
                    controller.updateUI();
                    MFMSettings.MFMFontSize(MFM_Constants.LARGE);
                    break;
                case MFM_Constants.NORMAL:
                    SwingUtils.resizeFonts(MFM_Constants.FONTSIZEINT);
                    SwingUtils.changeFont(MFMController.getFrame(), MFM_Constants.FONTSIZEINT);
                    controller.updateUI();
                    MFMSettings.MFMFontSize(MFM_Constants.NORMAL);
                    break;

                case "Show History":
                    controller.showHistory();
                    break;

                case "Show Manual":
                    controller.showManual();
                    break;

                case "Show Info":
                    controller.showInfo();
                    break;

                case ShowControlsDevicesAction:
                    controller.showControlsDevices();
                    break;

                case "Open Image":
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

                case MAMECommandBuilderAction:
                    controller.commandDialog();
                    break;

                // TODO should we remove this one see Show List?
                case "Create List":
                    controller.showListBuilder("");
                    break;

                case CopyResourcesAction:
                    // controller.populateList();
                    controller.copyResources(true);
                    break;

                case SaveResourcesAction:
                    controller.copyResources(false);
                    break;

                case ScanResourcesAction:
                    controller.scanResources();
                    break;

                case ImportListCommand:
                    MFMListBuilder.importList(MFMController.getFrame());
                    break;

                case "Remove List":
                    controller.removeList();
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

                case SaveListDataAction:
                    controller.ListMachinesDUMP();
                    break;

                case GOTOcloneofAction:
                    controller.GOTOcloneof();
                    break;

                case "Save Command":

                    break;

                case "Run Command":

                    break;

                case MAME_TREEAction:
                    controller.showMAMEtree();
                    break;

                case MACHINE_TREEAction:
                    controller.showMachinetree();
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
                    controller.MAMEControlsDUMP();
                    break;

                case CleanLogsAction:
                    MFM_Clean_Logs.cleanLogs();
                    break;

                case UpdateVersionAction:
                    controller.refreshVersion();
                    break;

                case ExitAction:
                    // NOTE duplicates MFMUI$MFMWindow windowClosing TODO Combine these how?
                    // TODO make this safer
                    MFM.logger.addToList("MFM Closing on user command", true);
                    MFMPlayLists.getInstance().persistPlayLists();
                    System.exit(0);
                    break;

                // NOTE this is a quick Hack for Look and Feel Settings
                default:
                    String command = e.getActionCommand();
                    if (SwingUtils.LandFNames().contains(command)) {
                        controller.changeLnF(command);
                        controller.showMFMmessage("Changed UI to " + command);
                        MFMSettings.MFMLookAndFeel(command);

                    }
                    controller.updateUI();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private final String ExitAction = "Exit";
    static final String ErrorLogAction = "Error Log";
    static final String LogAction = "Log";
    static final String GCLogAction = "GC Log";
    static final String CleanLogsAction = "Clean Logs";
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
    static final String RemovefromListAction = "Remove from List";
    static final String GOTOcloneofAction = "GOTO cloneof";
    private final String GameVideoInfoAction = "Machine Video Info";
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
    static final String ListtoDATAction = "Create List DAT";
    public static final String UpdateVersionAction = "updateVersion";
    static final String MAME_TREEAction = "Show MAME tree";
    static final String MACHINE_TREEAction = "Show Machine tree";
    static final String OpenFileAction = "Open File";
    /*
    public static final String Action = "";
    public static final String Action = "";
    public static final String Action = "";
*/


}

