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

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 8/18/13
 * Time: 5:54 PM
 */
public class MFMOptionPane {
    JTextArea textArea = new JTextArea();
    JDialog dialog;

    public MFMOptionPane(String title, String text, Frame frame, int messageType,
                         int optionType) {

        final JScrollPane scrollPane = new JScrollPane();
        textArea.setFont(frame.getFont());

        textArea.setColumns(50);
        textArea.setRows(20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setSize(textArea.getPreferredSize().width, 1);
        textArea.setText(text);
        textArea.setComponentPopupMenu(createPopup());

        scrollPane.setViewportView(textArea);

        // Note fixme above this forces scroll to the top
        if (text.length() > 2) { // otherwise you get a IllegalArgumentException: bad position: 2
            textArea.setCaretPosition(DefaultCaret.OUT_TOP);
        }

        // NOTE we do all of this to have a resizable dialog.
        Object[] array = {
                new JLabel(),
                scrollPane,
        };
        JOptionPane pane = new JOptionPane(array, JOptionPane.INFORMATION_MESSAGE);
        dialog = pane.createDialog(frame, title);
        //    dialog.add(createPopup());
        dialog.setLocation(150, 150);
        dialog.setResizable(true);
        dialog.setVisible(true);
    }

    private JPopupMenu createPopup() {
        JPopupMenu popupMenu = new JPopupMenu(null);

        popupMenu.add(new MFMOptionAction(MFMOptionAction.SELECTALL));
        popupMenu.add(new MFMOptionAction(MFMOptionAction.COPY));
        popupMenu.add(new MFMOptionAction(MFMOptionAction.SAVETOFILE));
        return popupMenu;
    }

    private class MFMOptionAction extends AbstractAction {
        static final String SELECTALL = "Select All";
        static final String COPY = "Copy";
        static final String SAVETOFILE = "Save to File";

        private MFMOptionAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Just for logger
                // JOptionPane.showConfirmDialog(null, e.getActionCommand());

                switch (e.getActionCommand()) {

                    case SELECTALL:
                        textArea.selectAll();
                        textArea.requestFocusInWindow();
                        break;

                    case COPY:
                        textArea.copy();
                        break;
                    case SAVETOFILE:
                        // TODO
                        break;

                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}
