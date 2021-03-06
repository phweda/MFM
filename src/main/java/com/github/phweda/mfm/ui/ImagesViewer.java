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

import com.github.phweda.mfm.MFM;
import com.github.phweda.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 4/2/2015
 * Time: 8:11 PM
 */

/**
 * Provides scrolling through individual images from a list of images.<br>
 * MFM can parse GIFs & AVIs
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ImagesViewer extends JFrame {

    private File imagesDir = new File(MFM.getMfmDir() + "/Images/");
    private JScrollPane scrollPane = new JScrollPane();
    private JPanel mainpanel = new JPanel();
    private ImagePanel imagePanel = new ImagePanel();

    private JLabel labelFilename = new JLabel();
    private JLabel labelFrameNumber = new JLabel();

    private JPanel northPanel = new JPanel();
    private JPanel nextPanel = new JPanel(new GridLayout(3, 1));
    private JPanel previousPanel = new JPanel(new GridLayout(3, 1));

    private JButton next1 = new JButton(">");
    private JButton previous1 = new JButton("<");
    private JButton next10 = new JButton(">>");
    private JButton previous10 = new JButton("<<");
    private JButton next100 = new JButton(">>>");
    private JButton previous100 = new JButton("<<<");
    private JButton save = new JButton("Save to file");

    private transient List<BufferedImage> frames;
    private File inputFile;
    int size;
    private int index = 0;
    private static final String ARIAL = "Arial";

    public ImagesViewer(List<BufferedImage> framesIn, File file) {
        super();
        frames = framesIn;
        size = frames.size();
        inputFile = file;
        labelFilename.setText(file.getName());
        setFrameNumber();
        init();
        // Release all Native for Garbage Collection
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (!imagesDir.exists() && !imagesDir.mkdir()) {
            MFM.getLogger().out("Images Viewer failed to create /Images/ directory");
        }
    }

    private void init() {
        addListeners();

        next1.setFont(new Font(ARIAL, Font.BOLD, 24));
        previous1.setFont(new Font(ARIAL, Font.BOLD, 24));
        next10.setFont(new Font(ARIAL, Font.BOLD, 24));
        previous10.setFont(new Font(ARIAL, Font.BOLD, 24));
        next100.setFont(new Font(ARIAL, Font.BOLD, 24));
        previous100.setFont(new Font(ARIAL, Font.BOLD, 24));

        GridLayout gl = new GridLayout(1, 4, 5, 0);
        northPanel.setLayout(gl);
        northPanel.add(new JLabel("Frames : " + size));
        northPanel.add(labelFilename);
        northPanel.add(labelFrameNumber);

        nextPanel.add(next1);
        nextPanel.add(next10);
        nextPanel.add(next100);

        previousPanel.add(previous1);
        previousPanel.add(previous10);
        previousPanel.add(previous100);

        mainpanel.setLayout(new BorderLayout(5, 5));
        scrollPane.setViewportView(imagePanel);
        mainpanel.add(scrollPane, BorderLayout.CENTER);
        mainpanel.add(previousPanel, BorderLayout.WEST);
        mainpanel.add(northPanel, BorderLayout.NORTH);
        mainpanel.add(nextPanel, BorderLayout.EAST);
        mainpanel.add(save, BorderLayout.SOUTH);

        imagePanel.image(frames.get(index));
        this.add(mainpanel);
        this.pack();
        this.setLocation(100, 100);
        this.setMinimumSize(new Dimension(750, 750));
        this.setVisible(true);
    }

    private void addListeners() {

        this.addWindowListener(new ImagesWindow());

        next1.addActionListener(event -> {
            //System.out.println("1 - index is : " + index);
            index++;
            if (index == size) {
                index = 0;
            }
            //System.out.println("2 - index is : " + index);
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        previous1.addActionListener(event -> {
            index--;
            if (index == -1) {
                index = size - 1;
            }
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        next10.addActionListener(event -> {
            index += 10;
            if (index >= size) {
                index = index - size;
            }
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        previous10.addActionListener(event -> {
            index -= 10;
            if (index < 0) {
                index = index + size;
            }
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        next100.addActionListener(event -> {
            index += 100;
            if (index >= size) {
                index = index - size;
            }
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        previous100.addActionListener(event -> {
            index -= 100;
            if (index < 0) {
                index = index + size;
            }
            imagePanel.image(frames.get(index));
            setFrameNumber();
        });

        save.addActionListener(event -> saveImagetoFile());
    }

    private void setFrameNumber() {
        labelFrameNumber.setText("Frame : " + (index + 1));
    }

    // Directory + filename + 'frame'# + .png
    private void saveImagetoFile() {
        try {
            String fileName = inputFile.getName();
            File file = new File(imagesDir.getCanonicalPath() + FileUtils.DIRECTORY_SEPARATOR +
                    fileName.substring(0, fileName.lastIndexOf('.')) + "-Frame" + (index + 1) + ".png");
            // retrieve image
            BufferedImage bi = frames.get(index);
            ImageIO.write(bi, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ImagesWindow extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            MFM.getLogger().out("Images frame closing");
            frames = null;
        }
    }
}

