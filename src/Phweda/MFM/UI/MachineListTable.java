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

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 6/16/2015
 * Time: 4:06 PM
 */
class MachineListTable extends JTable {
    static int keyColumn = 0;
    private static MachineListTable ourInstance;
    private static int fullNameColumn = 0;

    static {
        ourInstance = new MachineListTable() {
            @Override
            public void tableChanged(TableModelEvent e) {
                super.tableChanged(e);
                // If table isn't empty
                if (!(e.getFirstRow() == TableModelEvent.HEADER_ROW)) {
                    this.getSelectionModel().setSelectionInterval(0, 0);
                    this.requestFocus();
                }
            }

            @Override
            public JToolTip createToolTip() {
                JMultiLineToolTip JMLITT = new JMultiLineToolTip();
                JMLITT.setFixedWidth(this.getWidth());
                return JMLITT;
            }

            //Implement table cell tool tips.
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                if (colIndex == fullNameColumn) {
                    try {
                        tip = MAMEInfo.getMachine(getValueAt(rowIndex, keyColumn).toString()).getTruncatedHistory();
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

        MFMAction historyAction = new MFMAction("Show History", null);
        historyAction.putValue(Action.ACTION_COMMAND_KEY, "Show History");
        this.getInputMap().put(KeyStroke.getKeyStroke("F1"), "Show History");
        this.getActionMap().put("Show History", historyAction);

        MFMAction manualAction = new MFMAction("Show Manual", null);
        manualAction.putValue(Action.ACTION_COMMAND_KEY, "Show Manual");
        this.getInputMap().put(KeyStroke.getKeyStroke("F2"), "Show Manual");
        this.getActionMap().put("Show Manual", manualAction);

        MFMAction infoAction = new MFMAction("Show Info", null);
        infoAction.putValue(Action.ACTION_COMMAND_KEY, "Show Info");
        this.getInputMap().put(KeyStroke.getKeyStroke("F3"), "Show Info");
        this.getActionMap().put("Show Info", infoAction);

        MFMAction runGameAction = new MFMAction("Run Machine", null);
        runGameAction.putValue(Action.ACTION_COMMAND_KEY, "Run Machine");
        this.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "Run Machine");
        this.getActionMap().put("Run Machine", runGameAction);

        MFMAction recordGameAction = new MFMAction("Record Machine", null);
        recordGameAction.putValue(Action.ACTION_COMMAND_KEY, "Record Machine");
        this.getInputMap().put(KeyStroke.getKeyStroke("F4"), "Record Machine");
        this.getActionMap().put("Record Machine", recordGameAction);

        MFMAction playbacktoAVIAction = new MFMAction(MFMAction.PlaybacktoAVIAction, null);
        playbacktoAVIAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.PlaybacktoAVIAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F5"),
                MFMAction.PlaybacktoAVIAction);
        this.getActionMap().put(MFMAction.PlaybacktoAVIAction, playbacktoAVIAction);

        MFMAction playbackGameAction = new MFMAction(MFMAction.PlaybackGameAction, null);
        playbackGameAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.PlaybackGameAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F6"), MFMAction.PlaybackGameAction);
        this.getActionMap().put(MFMAction.PlaybackGameAction, playbackGameAction);

        MFMAction playGametoAVIAction = new MFMAction(MFMAction.PlayGametoAVIAction, null);
        playGametoAVIAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.PlayGametoAVIAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F7"), MFMAction.PlayGametoAVIAction);
        this.getActionMap().put(MFMAction.PlayGametoAVIAction, playGametoAVIAction);

        MFMAction VidAction = new MFMAction("Play Video", null);
        VidAction.putValue(Action.ACTION_COMMAND_KEY, "Play Video");
        this.getInputMap().put(KeyStroke.getKeyStroke("F8"), "Play Video");
        this.getActionMap().put("Play Video", VidAction);

        MFMAction EditVidAction = new MFMAction(MFMAction.EditVideoAction, null);
        VidAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.EditVideoAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F9"), MFMAction.EditVideoAction);
        this.getActionMap().put(MFMAction.EditVideoAction, EditVidAction);

        MFMAction ListBuilderAction = new MFMAction(MFMAction.ListBuilderAction, null);
        VidAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.ListBuilderAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F10"), MFMAction.ListBuilderAction);
        this.getActionMap().put(MFMAction.ListBuilderAction, ListBuilderAction);

/*      // Legacy capability
        MFMAction EditVidAction = new MFMAction(MFMAction.CropAVIAction, null);
        VidAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.EditVideoAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F11"), MFMAction.EditVideoAction);
        this.getActionMap().put(MFMAction.EditVideoAction, EditVidAction);
*/

        MFMAction ConvertVideoAction = new MFMAction(MFMAction.ConvertCommandAction, null);
        VidAction.putValue(Action.ACTION_COMMAND_KEY, MFMAction.ConvertCommandAction);
        this.getInputMap().put(KeyStroke.getKeyStroke("F12"), MFMAction.ConvertCommandAction);
        this.getActionMap().put(MFMAction.ConvertCommandAction, ConvertVideoAction);

        //    this.add(((JPopupMenu)new MFM_Components()));
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

        keyColumn = columnModel.getColumnIndex(Machine.MACHINE_NAME);
        fullNameColumn = columnModel.getColumnIndex(Machine.MACHINE_FULL_NAME);


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

        this.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                final JComponent comp = (JComponent) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                comp.setBackground(row % 2 == 0 ? Color.GREEN : MFMUI.getMFMLightGreen());
                comp.setForeground(Color.BLACK);

                int newRow = ourInstance.convertRowIndexToModel(row);
                final String machineName = (String) ourInstance.getModel().getValueAt(newRow, keyColumn);

                if (!MAMEInfo.getRunnableMachines().contains(machineName)) {
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
