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

package com.github.phweda.mfm.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/21/12
 * Time: 2:44 AM
 */
public class MFMInformationPanel extends JPanel {
    private static final String MESSAGE = "message";
    private static final String PROGRESS = "progress";
    private static JLabel messageLabel = new JLabel();
    private static JProgressBar progressBar = new MFMProgressBar();
    private static JPanel progressPanel = new JPanel();
    private boolean running = false;
    private Timer timer;

    MFMInformationPanel() {
        super();
        progressPanel.add(progressBar);
        CardLayout cl = new CardLayout();
        cl.addLayoutComponent(messageLabel, MESSAGE);
        cl.addLayoutComponent(progressPanel, PROGRESS);
        this.add(messageLabel);
        this.add(progressPanel);
        this.setLayout(cl);
        this.setPreferredSize(new Dimension(350, 100));
    }

    void showProgress(String title) {
        ((CardLayout) this.getLayout()).last(this);
        progressBar.setString(title);
        messageLabel.setText("");
        updateUI();
        progressPanel.setVisible(true);
        running = true;
        runProgress();
    }

    public void showProgress(String message, int fontSize) {
        progressBar.setFont(new Font("Arial", Font.BOLD, fontSize));
        showProgress(message);
    }

    public void showMessage(String message) {
        running = false;
        MFMInformationPanel.messageLabel.setText(message);
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        ((CardLayout) this.getLayout()).first(this);
    }

    private void runProgress() {
        // JOptionPane.showMessageDialog(null, "We are in runProgress()");
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
    }

}
