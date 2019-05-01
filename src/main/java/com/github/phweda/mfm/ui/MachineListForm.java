package com.github.phweda.mfm.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MachineListForm {
  private JPanel pnlMachineListView;
  private JTextField txtSearch;
  private JPanel pnlSearch;
  private JScrollPane scpPane;
  private JTable machineListTable;
  private TableRowSorter<MachineListTableModel> mltms;

  public MachineListForm() {
    txtSearch.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        handleFilter();
      }

      public void removeUpdate(DocumentEvent e) {
        handleFilter();
      }

      public void insertUpdate(DocumentEvent e) {
        handleFilter();
      }

      public void handleFilter() {
        DefaultRowSorter sorter = (DefaultRowSorter) machineListTable.getRowSorter();
        sorter.setRowFilter(new RowFilter() {
          @Override
          public boolean include(RowFilter.Entry entry) {
            String value = (String) entry.getValue(0);
            return value.toLowerCase().contains(txtSearch.getText().toLowerCase());
          }
        });
      }
    });
  }

  public JPanel getPnlMachineListView() {
    return pnlMachineListView;
  }

  private void createUIComponents() {
    machineListTable = MachineListTable.getInstance();
  }
}
