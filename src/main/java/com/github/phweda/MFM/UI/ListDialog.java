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


/*
 * Highly modified for MFM. 12/27/11
 * Phweda
 */

package com.github.phweda.MFM.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("squid:MaximumInheritanceDepth")
class ListDialog extends JDialog implements ActionListener {
    private String value = "";
    private JList<String> list;
    private JTextArea textArea;

    public ListDialog(Frame frame, JTextArea textArea, String labelText, String title,
                      String[] data, String initialValue, String longValue) {
        super(frame, title, true);

        this.textArea = textArea;

        //Create and initialize the buttons.
        JButton cancelButton = new JButton("Done");
        cancelButton.addActionListener(this);
        //
        final JButton addButton = new JButton("Add");
        addButton.setActionCommand("Add");
        addButton.addActionListener(this);
        getRootPane().setDefaultButton(addButton);

        //main part of the dialog
        list = new JList<>(data);

        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (longValue != null) {
            list.setPrototypeCellValue(longValue); //get extra space
        }
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setFixedCellWidth(125);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addButton.doClick();
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(300, 500));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the labelFilename and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        label.setLabelFor(list);
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(addButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

        //Initialize values.
        setValue(initialValue);
        pack();
        setLocationRelativeTo(frame);
    }

    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on;
     */
/*
    public static String showDialog(Frame frame, JTextArea textArea, String label,
                                    String title, Object[] gameNames, String initialValue,
                                    String longValue) {
*/
    public String showDialog() {
        this.setModalityType(ModalityType.MODELESS);

        this.setVisible(true);
        return value;
    }

    private void setValue(String newValue) {
        value = newValue;
        list.setSelectedValue(value, true);
    }

    //Handle clicks on the Add and Done buttons.
    public void actionPerformed(ActionEvent e) {
        if ("Add".equals(e.getActionCommand())) {
            value = list.getSelectedValue();
            textArea.append(value + "\n");
        }
        if ("Done".equals(e.getActionCommand())) {
            this.setVisible(false);
        }
    }
}
