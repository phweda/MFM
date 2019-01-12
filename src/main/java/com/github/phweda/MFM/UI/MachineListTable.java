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

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.mame.Machine;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Objects;

import static Phweda.MFM.UI.MFMAction.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 6/16/2015
 * Time: 4:06 PM
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
class MachineListTable extends JTable {
    static final int KEY_COLUMN = 1;
    int categoryColumn = 4;
    private static final MachineListTable ourInstance;
    static final int FULL_NAME_COLUMN = 0;

    static {
        ourInstance = new MachineListTable() {
            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
                // If table isn't empty
                if (e.getFirstRow() != TableModelEvent.HEADER_ROW) {
                    this.getSelectionModel().setSelectionInterval(0, 0);
                    this.requestFocus();
                }
            }

            @Override
            public JToolTip createToolTip() {
                JMultiLineToolTip jMultiLineToolTip = new JMultiLineToolTip();
                jMultiLineToolTip.setFixedWidth(this.getWidth());
                return jMultiLineToolTip;
            }

            //Implement table cell tool tips.
            @Override
            public String getToolTipText(@NotNull MouseEvent e) {
                String tip = null;
                java.awt.Point p = Objects.requireNonNull(e).getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                if (colIndex == FULL_NAME_COLUMN) {
                    try {
                        tip = MAMEInfo.getMachine(getValueAt(rowIndex, KEY_COLUMN).toString()).getTruncatedHistory();
                    } catch (RuntimeException e1) {
                        //catch null pointer exception if mouse is over an empty line
                    }
                }
                return tip;
            }
        };
    }

    private MachineListTable() {
        init();
    }

    public static MachineListTable getInstance() {
        return ourInstance;
    }

    private void init() {
        MachineListTableModel machineListTableModel = new MachineListTableModel();
        this.setModel(machineListTableModel);
        machineListTableModel.addTableModelListener(this);

        MFMAction historyAction = new MFMAction(SHOW_HISTORY, null);
        historyAction.putValue(Action.ACTION_COMMAND_KEY, SHOW_HISTORY);
        this.getInputMap().put(KeyStroke.getKeyStroke("F1"), SHOW_HISTORY);
        this.getActionMap().put(SHOW_HISTORY, historyAction);

        MFMAction manualAction = new MFMAction(SHOW_MANUAL, null);
        manualAction.putValue(Action.ACTION_COMMAND_KEY, SHOW_MANUAL);
        this.getInputMap().put(KeyStroke.getKeyStroke("F2"), SHOW_MANUAL);
        this.getActionMap().put(SHOW_MANUAL, manualAction);

        MFMAction infoAction = new MFMAction(SHOW_INFO, null);
        infoAction.putValue(Action.ACTION_COMMAND_KEY, SHOW_INFO);
        this.getInputMap().put(KeyStroke.getKeyStroke("F3"), SHOW_INFO);
        this.getActionMap().put(SHOW_INFO, infoAction);

        MFMAction runGameAction = new MFMAction(RUN_MACHINE, null);
        runGameAction.putValue(Action.ACTION_COMMAND_KEY, RUN_MACHINE);
        this.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), RUN_MACHINE);
        this.getActionMap().put(RUN_MACHINE, runGameAction);

        MFMAction recordGameAction = new MFMAction(RECORD_MACHINE, null);
        recordGameAction.putValue(Action.ACTION_COMMAND_KEY, RECORD_MACHINE);
        this.getInputMap().put(KeyStroke.getKeyStroke("F4"), RECORD_MACHINE);
        this.getActionMap().put(RECORD_MACHINE, recordGameAction);

        MFMAction playbacktoAVIAction = new MFMAction(PLAYBACK_TO_AVI, null);
        playbacktoAVIAction.putValue(Action.ACTION_COMMAND_KEY, PLAYBACK_TO_AVI);
        this.getInputMap().put(KeyStroke.getKeyStroke("F5"),
                PLAYBACK_TO_AVI);
        this.getActionMap().put(PLAYBACK_TO_AVI, playbacktoAVIAction);

        MFMAction playbackGameAction = new MFMAction(PLAYBACK_MACHINE, null);
        playbackGameAction.putValue(Action.ACTION_COMMAND_KEY, PLAYBACK_MACHINE);
        this.getInputMap().put(KeyStroke.getKeyStroke("F6"), PLAYBACK_MACHINE);
        this.getActionMap().put(PLAYBACK_MACHINE, playbackGameAction);

        MFMAction playGametoAVIAction = new MFMAction(PLAY_RECORD_TO_AVI, null);
        playGametoAVIAction.putValue(Action.ACTION_COMMAND_KEY, PLAY_RECORD_TO_AVI);
        this.getInputMap().put(KeyStroke.getKeyStroke("F7"), PLAY_RECORD_TO_AVI);
        this.getActionMap().put(PLAY_RECORD_TO_AVI, playGametoAVIAction);

        MFMAction videoAction = new MFMAction(PLAY_VIDEO, null);
        videoAction.putValue(Action.ACTION_COMMAND_KEY, PLAY_VIDEO);
        this.getInputMap().put(KeyStroke.getKeyStroke("F8"), PLAY_VIDEO);
        this.getActionMap().put(PLAY_VIDEO, videoAction);

        MFMAction editVideoAction = new MFMAction(EDIT_VIDEO, null);
        videoAction.putValue(Action.ACTION_COMMAND_KEY, EDIT_VIDEO);
        this.getInputMap().put(KeyStroke.getKeyStroke("F9"), EDIT_VIDEO);
        this.getActionMap().put(EDIT_VIDEO, editVideoAction);

        MFMAction listBuilderAction = new MFMAction(LIST_BUILDER, null);
        videoAction.putValue(Action.ACTION_COMMAND_KEY, LIST_BUILDER);
        this.getInputMap().put(KeyStroke.getKeyStroke("F10"), LIST_BUILDER);
        this.getActionMap().put(LIST_BUILDER, listBuilderAction);

        MFMAction convertVideoAction = new MFMAction(CONVERT_FILES, null);
        videoAction.putValue(Action.ACTION_COMMAND_KEY, CONVERT_FILES);
        this.getInputMap().put(KeyStroke.getKeyStroke("F12"), CONVERT_FILES);
        this.getActionMap().put(CONVERT_FILES, convertVideoAction);

        this.setRowHeight(25);
        this.setAutoCreateRowSorter(true);
        this.getRowSorter().toggleSortOrder(0);

        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        /*
         *
         * DESCRIPTION, FILE NAME, MANUFACTURER, YEAR, CATEGORY, STATUS, CLONEOF
         *    0             1          2          3         4      5        6
         *
         **/
        // Hard coded relative widths for now ** Pixels?? yes
        TableColumnModel columnModel = this.getColumnModel();

        int index = columnModel.getColumnIndex(Machine.MACHINE_NAME);
        columnModel.getColumn(index).setPreferredWidth(155);
        columnModel.getColumn(index).setMaxWidth(175);
        columnModel.getColumn(index).setMinWidth(125);

        index = columnModel.getColumnIndex(Machine.CATEGORY_CAPS);
        columnModel.getColumn(index).setPreferredWidth(250);
        columnModel.getColumn(index).setMaxWidth(325);
        columnModel.getColumn(index).setMinWidth(125);

        index = columnModel.getColumnIndex(Machine.MACHINE_FULL_NAME);
        columnModel.getColumn(index).setPreferredWidth(350);
        columnModel.getColumn(index).setMaxWidth(650);
        columnModel.getColumn(index).setMinWidth(120);

        index = columnModel.getColumnIndex(Machine.YEAR_CAPS);
        columnModel.getColumn(index).setMaxWidth(60);
        columnModel.getColumn(index).setMinWidth(60);

        index = columnModel.getColumnIndex(Machine.MANUFACTURER_CAPS);
        columnModel.getColumn(index).setPreferredWidth(115);
        columnModel.getColumn(index).setMinWidth(95);
        columnModel.getColumn(index).setMaxWidth(325);

        index = columnModel.getColumnIndex(Machine.STATUS_CAPS);
        columnModel.getColumn(index).setMinWidth(95);
        columnModel.getColumn(index).setMaxWidth(95);

        index = columnModel.getColumnIndex(Machine.CLONEOF_CAPS);
        columnModel.getColumn(index).setPreferredWidth(95);
        columnModel.getColumn(index).setMinWidth(95);
        columnModel.getColumn(index).setMaxWidth(150);

        this.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus, int row, int column) {
                        final JComponent comp = (JComponent) super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        comp.setBackground(row % 2 == 0 ? Color.GREEN : MFMUI.getMFMLightGreen());
                        comp.setForeground(Color.BLACK);

                        int newRow = ourInstance.convertRowIndexToModel(row);
                        String itemName = (String) ourInstance.getModel().getValueAt(newRow, KEY_COLUMN);
                        String systemName = (String) ourInstance.getModel().getValueAt(newRow, categoryColumn);
                        if (!MAMEInfo.isSoftwareList(systemName)) {
                            systemName = null;
                        }

                        // Overly complex logic due to support of mixed lists fixme
                        if (!(systemName != null && MFMController.checkRunnableSoftware(itemName, systemName)) &&
                                !MAMEInfo.getRunnableMachines().contains(itemName) &&
                                !MFMController.checkRunnableSoftware(itemName)) {
                            comp.setBackground(MFMUI.getMFMLightRed());
                        }

                        if (isRowSelected(row)) {
                            int top = (row > 0 && isRowSelected(row - 1)) ? 1 : 2;
                            int left = column == 0 ? 2 : 0;
                            int bottom = (row < getRowCount() - 1 && isRowSelected(row + 1)) ? 1 : 2;
                            int right = column == getColumnCount() - 1 ? 2 : 0;

                            if (comp.getBackground() == MFMUI.getMFMLightRed()) {
                                comp.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.yellow));
                            } else {
                                comp.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.blue));
                            }
                        }
                        return this;
                    }
                });
    }
}
