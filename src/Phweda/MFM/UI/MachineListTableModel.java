/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2017.  Author phweda : phweda1@yahoo.com
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
import Phweda.MFM.MFM;
import Phweda.MFM.mame.Machine;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/3/11
 * Time: 9:36 PM
 */
class MachineListTableModel extends AbstractTableModel {

    // NOTE!! MACHINE_FULL_NAME is the Machine.DESCRIPTION
    private String[] columnNames = {Machine.MACHINE_FULL_NAME, Machine.MACHINE_NAME, Machine.MANUFACTURER_CAPS,
            Machine.YEAR_CAPS, Machine.CATEGORY_CAPS, Machine.STATUS_CAPS, Machine.CLONEOF_CAPS};

    private ArrayList<String[]> data = new ArrayList<String[]>();
    private ArrayList<String[]> tempdata = new ArrayList<String[]>();

    private String[] list;

    /* How wide for each column? Not worth the extra time to calculate each time
      *   "Machine", "Category", "Year", "Manufacturer", "Status", "Cloneof"
      *    16(13)               6                         12        16
      *    Above are character sizes = columns are in pixels
      *
      * */

    public void setData(TreeSet<String> list) {
        loadMachineList(list);
    }

    /* TODO Hide and show columns */
    private void loadMachineList(TreeSet<String> list) {
        this.list = list.toArray(new String[list.size()]);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (list == null) return 0;
        return list.length;
    }

    @Override
    public int getColumnCount() {
        if (columnNames == null) return 0;
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (list == null || rowIndex < 0) {
            return "No DATA!";
        }

        Machine machine = MAMEInfo.getMachine(list[rowIndex]);
        // Expected for some lists
        if (machine == null) {
            if (columnIndex == MachineListTable.keyColumn) {
                String message = "MachineListTableModel.java:107 machine is null " + list[rowIndex];
                System.out.println(message);
                MFM.logger.out(message);
                return list[rowIndex];
            }
            return "No DATA!";
        }
        return machine.getValueOf(columnNames[columnIndex]);
    }

    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return super.getColumnClass(columnIndex);
    }

    public void addRow(String[] newRow) {
        tempdata.add(newRow);
    }

    private void addRows(List<String[]> rows) {
        tempdata.addAll(rows);
    }

    /**
     * Update table data after TableSwingWorker completes
     * Effectively buffers new list till completed creating
     */
    private void update() {
        data = tempdata;
        fireTableDataChanged();
        tempdata = new ArrayList<String[]>();
    }

    private class TableSwingWorker extends SwingWorker<MachineListTableModel, String[]> {

        private final MachineListTableModel tableModel;
        private final TreeSet<String> list;

        TableSwingWorker(MachineListTableModel tableModel, TreeSet<String> list) {
            this.tableModel = tableModel;
            this.list = list;
        }

        @Override
        protected MachineListTableModel doInBackground() throws Exception {
            if (MFM.isSystemDebug()) {
                System.out.println("Start list populating");
            }
            // NOTE parallelStream untested for performance but suspect for 10K order lists may improve performance
            list.parallelStream()
                    .forEach(machineName ->
                    {
                        String[] machineData = null;
                        Machine machine = MAMEInfo.getMachine(machineName);
                        if (machine != null) {
                            machineData = getMachineArray(machine);
                        }
                        publish(machineData);
                    });
            Thread.yield();
            return tableModel;
        }

        private String[] getMachineArray(Machine machine) {
            String[] data = new String[columnNames.length];
            IntStream.range(0, data.length)
                    .forEach(x -> data[x] = machine.getValueOf(columnNames[x]));
            return data;
        }

        @Override
        protected void done() {
            super.done();
            tableModel.update();
        }

        @Override
        protected void process(List<String[]> chunks) {
            if (MFM.isSystemDebug()) {
                System.out.println("Adding " + chunks.size() + " rows");
            }
            tableModel.addRows(chunks);
        }
    }
}
