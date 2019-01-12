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

package com.github.phweda.MFM.UI;

import com.github.phweda.MFM.MAMEInfo;
import com.github.phweda.MFM.MFM_Constants;
import com.github.phweda.MFM.mame.softwarelist.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/11/2017
 * Time: 7:39 PM
 */
public class SoftwareListtoJTree extends JPanel {
    private static final String COPY = "Copy";
    private static final int FRAME_WIDTH = MFMUI.screenSize.width / 5;
    private static final int FRAME_HEIGHT = MFMUI.screenSize.height - 100;
    private JTree jTree;

    private transient List<Software> softwares;
    private static SoftwareListtoJTree ourInstance;
    private static final String VALUE_DIVIDER = " \u00bb ";
    private static final String SOFTWARE_DIVIDER = " \u00A8 ";

    private SoftwareListtoJTree(String softwareListName) {

        softwares = MAMEInfo.getSoftwareLists().getSoftwarelistsMap().get(softwareListName).getSoftware();
        this.setPreferredSize(new Dimension(FRAME_WIDTH - 10, FRAME_HEIGHT - 20));

        DefaultMutableTreeNode top = createTreeNode("SoftwareList");
        DefaultTreeModel dtModel = new DefaultTreeModel(top);
        jTree = new JTree(dtModel);

        Font font = jTree.getFont();
        jTree.setFont(new Font(font.getName(), font.getStyle(), font.getSize() + 4));
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setShowsRootHandles(true);
        jTree.setEditable(false);

        JScrollPane jScroll = new JScrollPane() {
            // This keeps the scrollpane a reasonable size
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
            }
        };

        jScroll.getViewport().add(jTree);

        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = jTree.getPathForLocation(e.getX(), e.getY());
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                    Object obj = selPath.getLastPathComponent();
                    if (obj != null) {
                        copytoClipboard(obj.toString());
                    }
                }
            }
        };
        jTree.addMouseListener(ml);

        jTree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), COPY);
        jTree.getActionMap().put(COPY,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DefaultMutableTreeNode node
                                = (DefaultMutableTreeNode) jTree.getSelectionPath().getLastPathComponent();
                        String value = node.getUserObject().toString();
                        copytoClipboard(value);
                    }
                }
        );
        add(jScroll);
    }

    public static SoftwareListtoJTree getInstance(String softwareListName) {
        if (ourInstance == null) {
            ourInstance = new SoftwareListtoJTree(softwareListName);
        }
        return ourInstance;
    }

    DefaultMutableTreeNode getSoftwareNode(String softwareName) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        Enumeration<DefaultMutableTreeNode> children = root.children();

        softwareName = softwareName.split(MFM_Constants.SOFTWARE_LIST_SEPARATER)[1];
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode node = children.nextElement();

            String nodeString = node.getUserObject().toString();
            String nodeSoftware = nodeString.substring(nodeString.lastIndexOf(' ') + 1);
            if (nodeSoftware.equals(softwareName)) {
                return node;
            }
        }
        return null;
    }

    private void copytoClipboard(String nodeValue) {
        nodeValue = nodeValue.contains(VALUE_DIVIDER) ?
                nodeValue.split(VALUE_DIVIDER)[1].trim() :
                nodeValue.split(SOFTWARE_DIVIDER)[1].trim();

        StringSelection ss = new StringSelection(nodeValue);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(ss, null);
    }

    /**
     * This takes Softwarelist root and create an XML JTree
     */
    @SuppressWarnings("SameParameterValue")
    private DefaultMutableTreeNode createTreeNode(String root) {
        DefaultMutableTreeNode dmtNode;
        dmtNode = new DefaultMutableTreeNode(root);

        softwares.sort(Comparator.comparing(Software::getName));

        for (Software software : softwares) {
            dmtNode.add(createSoftwareNode(software));
        }
        return dmtNode;
    }

    /**
     * @param software Mame software list to display
     * @return software node
     */
    private DefaultMutableTreeNode createSoftwareNode(Software software) {
        DefaultMutableTreeNode dmtNode;

        // Name is always required
        String name = software.getName();
        dmtNode = new DefaultMutableTreeNode("Software" + SOFTWARE_DIVIDER + name);

        String cloneof = software.getCloneof();
        if (cloneof != null && !cloneof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("Cloneof" + VALUE_DIVIDER + cloneof));
        }

        String supported = software.getSupported();
        dmtNode.add(new DefaultMutableTreeNode("Supported" + VALUE_DIVIDER + supported));

        String description = software.getDescription();
        dmtNode.add(new DefaultMutableTreeNode("Description" + VALUE_DIVIDER + description));

        String year = software.getYear();
        dmtNode.add(new DefaultMutableTreeNode("Year" + VALUE_DIVIDER + year));

        String publisher = software.getPublisher();
        dmtNode.add(new DefaultMutableTreeNode("Publisher" + VALUE_DIVIDER + publisher));

        if (!software.getInfo().isEmpty()) {
            dmtNode.add(getInfoNode(software));
        }

        if (!software.getSharedfeat().isEmpty()) {
            dmtNode.add(getSharedfeatNode(software));
        }

        if (!software.getPart().isEmpty()) {
            dmtNode.add(getPartNodes(software));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getInfoNode(Software software) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Info");
        for (Info info : software.getInfo()) {
            String value = info.getValue() != null ? info.getValue() : "";
            dmtNode.add(new DefaultMutableTreeNode(info.getName() + VALUE_DIVIDER + value));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getSharedfeatNode(Software software) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Sharedfeat");
        for (Sharedfeat sharedfeat : software.getSharedfeat()) {
            String value = sharedfeat.getValue() != null ? sharedfeat.getValue() : "";
            dmtNode.add(new DefaultMutableTreeNode(sharedfeat.getName() + VALUE_DIVIDER + value));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getPartNodes(Software software) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Parts");
        for (Part part : software.getPart()) {
            dmtNode.add(getPart(part));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getPart(Part part) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Part" + SOFTWARE_DIVIDER + part.getName());
        dmtNode.add(new DefaultMutableTreeNode("Interface" + VALUE_DIVIDER + part.getInterface()));
        if (!part.getDataarea().isEmpty()) {
            dmtNode.add(getDataarea(part.getDataarea()));
        }
        if (!part.getDipswitch().isEmpty()) {
            dmtNode.add(getDipswitch(part.getDipswitch()));
        }
        if (!part.getDiskarea().isEmpty()) {
            dmtNode.add(getDiskarea(part.getDiskarea()));
        }
        if (!part.getFeature().isEmpty()) {
            dmtNode.add(getFeature(part.getFeature()));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode getDataarea(List<Dataarea> dataareas) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dataarea(s)");
        for (Dataarea dataarea : dataareas) {
            DefaultMutableTreeNode dataareaNode = new DefaultMutableTreeNode(dataarea.getName());
            dataareaNode.add(new DefaultMutableTreeNode("Size" + VALUE_DIVIDER + dataarea.getSize()));
            dataareaNode.add(new DefaultMutableTreeNode("Width" + VALUE_DIVIDER + dataarea.getWidth()));
            dataareaNode.add(new DefaultMutableTreeNode("Endianness" + VALUE_DIVIDER + dataarea.getEndianness()));
            dataareaNode.add(getRoms(dataarea.getRom()));
            dmtNode.add(dataareaNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDipswitch(List<Dipswitch> dipswitchs) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dipswitch(s)");
        for (Dipswitch dipswitch : dipswitchs) {
            DefaultMutableTreeNode dipswitchNode = new DefaultMutableTreeNode("Name" + VALUE_DIVIDER +
                    dipswitch.getName());
            dipswitchNode.add(new DefaultMutableTreeNode("Mask" + VALUE_DIVIDER +
                    dipswitch.getMask()));
            dipswitchNode.add(new DefaultMutableTreeNode("Tag" + VALUE_DIVIDER +
                    dipswitch.getTag()));
            dipswitchNode.add(getDipvalues(dipswitch.getDipvalue()));
            dmtNode.add(dipswitchNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDiskarea(List<Diskarea> diskareas) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Diskarea(s)");
        for (Diskarea diskarea : diskareas) {
            DefaultMutableTreeNode diskareaNode = new DefaultMutableTreeNode(diskarea.getName());
            diskareaNode.add(getDisks(diskarea.getDisk()));
            dmtNode.add(diskareaNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getFeature(List<Feature> features) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Feature(s)");
        for (Feature feature : features) {
            String value = feature.getValue() != null ? feature.getValue() : "";
            DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(feature.getName() + VALUE_DIVIDER +
                    value);
            dmtNode.add(featureNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDipvalues(List<Dipvalue> dipvalues) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dipvalue(s)");
        for (Dipvalue dipvalue : dipvalues) {
            DefaultMutableTreeNode dipvalueNode = new DefaultMutableTreeNode(dipvalue.getName() + VALUE_DIVIDER +
                    dipvalue.getValue());
            dmtNode.add(dipvalueNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDisks(List<Disk> disks) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Disk(s)");
        for (Disk disk : disks) {
            DefaultMutableTreeNode diskNode = new DefaultMutableTreeNode(disk.getName());
            diskNode.add(new DefaultMutableTreeNode("SHA1" + VALUE_DIVIDER + disk.getSha1()));
            diskNode.add(new DefaultMutableTreeNode("Status" + VALUE_DIVIDER + disk.getStatus()));
            diskNode.add(new DefaultMutableTreeNode("Writeable" + VALUE_DIVIDER + disk.getWriteable()));
            dmtNode.add(diskNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getRoms(List<Rom> roms) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Disk(s)");
        for (Rom rom : roms) {
            DefaultMutableTreeNode romNode = new DefaultMutableTreeNode(rom.getName());
            romNode.add(new DefaultMutableTreeNode("Size" + VALUE_DIVIDER + rom.getSize()));
            romNode.add(new DefaultMutableTreeNode("CRC" + VALUE_DIVIDER + rom.getCrc()));
            romNode.add(new DefaultMutableTreeNode("SHA1" + VALUE_DIVIDER + rom.getSha1()));
            romNode.add(new DefaultMutableTreeNode("Offset" + VALUE_DIVIDER + rom.getOffset()));
            romNode.add(new DefaultMutableTreeNode("Value" + VALUE_DIVIDER + rom.getValue()));
            romNode.add(new DefaultMutableTreeNode("Status" + VALUE_DIVIDER + rom.getStatus()));
            romNode.add(new DefaultMutableTreeNode("Loadflag" + VALUE_DIVIDER + rom.getLoadflag()));
            dmtNode.add(romNode);
        }
        return dmtNode;
    }

}
