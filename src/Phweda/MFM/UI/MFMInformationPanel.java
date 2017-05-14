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
    private static JLabel message = new JLabel();
    private static JProgressBar progressBar = new MFMProgressBar();
    private static JPanel progressPanel = new JPanel();
    boolean running = false;
    Timer timer;

    MFMInformationPanel() {
        super();
        //    progressBar.setPreferredSize(new Dimension(400, 75));
        progressPanel.add(progressBar);
        CardLayout cl = new CardLayout();
        cl.addLayoutComponent(message, MESSAGE);
        cl.addLayoutComponent(progressPanel, PROGRESS);
        this.add(message);
        this.add(progressPanel);
        this.setLayout(cl);
        this.setPreferredSize(new Dimension(350, 100));
    }

    void showProgress(String title) {
        ((CardLayout) this.getLayout()).last(this);
        progressBar.setString(title);
        message.setText("");
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
        MFMInformationPanel.message.setText(message);
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


/*
    void runProgress() {

        final int x, y;
        final int radius = 12;
        x = progressPanel.getLocation().x + radius;
        y = progressPanel.getLocation().y + radius;

        final Graphics2D g = (Graphics2D) progressPanel.getGraphics();
        if (g == null) {
            System.out.println("g == null");
            return;
        }

        final Color bgC = progressPanel.getBackground();
        final Color clear = new Color(bgC.getRed(), bgC.getGreen(), bgC.getBlue(), 75);
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int delay = 500; //milliseconds
            ActionListener taskPerformer = new ActionListener()
            {
                double i = -Math.PI / 2;
                public void actionPerformed(ActionEvent evt) {
                    int x1, y1;
                    //    System.out.println(radius * Math.cos(i) + "  ->  " + radius * Math.sin(i));
                    g.setColor(clear);
                    g.fillRect(0, 0, 110, 110);
                    g.setColor(Color.red);
                    x1 = (int) (x + (radius * Math.cos(i)));
                    y1 = (int) (y + (radius * Math.sin(i)));
                    g.drawLine(x, y, x1, y1);
                    i += .8;
                }
            };
            timer =  new Timer(delay, taskPerformer);
            timer.start();
    }
*/
}
