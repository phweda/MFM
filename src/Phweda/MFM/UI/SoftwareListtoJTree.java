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
import Phweda.MFM.MFM_Constants;
import Phweda.MFM.mame.softwarelist.*;

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

// TODO fixme? NOTE does this even make sense? Will anybody really want to see this as a tree?
public class SoftwareListtoJTree extends JPanel {
    private static final String COPY = "Copy";
    private static final int FRAME_WIDTH = MFMUI.screenSize.width / 5;
    private static final int FRAME_HEIGHT = MFMUI.screenSize.height - 100;
    private static JTree jTree;

    private static List<Software> softwares;
    private static SoftwareListtoJTree ourInstance;
    private final String valueDivider = " \u00bb ";
    private final String softwareDivider = " \u00A8 ";

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
            public Dimension getPreferredSize() {
                return new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
            }
        };

        jScroll.getViewport().add(jTree);

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = jTree.getRowForLocation(e.getX(), e.getY());
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
            String nodeSoftware = nodeString.substring(nodeString.lastIndexOf(' ') + 1, nodeString.length());
            if (nodeSoftware.equals(softwareName)) {
                return node;
            }
        }
        return null;
    }

    private void copytoClipboard(String nodeValue) {
        nodeValue = nodeValue.contains(valueDivider) ?
                nodeValue.split(valueDivider)[1].trim() :
                nodeValue.split(softwareDivider)[1].trim();

        StringSelection ss = new StringSelection(nodeValue);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(ss, null);
    }

    /**
     * This takes Softwarelist root and create an XML JTree
     */
    private DefaultMutableTreeNode createTreeNode(String root) {
        DefaultMutableTreeNode dmtNode = null;
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
        DefaultMutableTreeNode dmtNode = null;

        // Name is always required
        String name = software.getName();
        dmtNode = new DefaultMutableTreeNode("Software" + softwareDivider + name);

        String cloneof = software.getCloneof();
        if (cloneof != null && !cloneof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("Cloneof" + valueDivider + cloneof));
        }

        String supported = software.getSupported();
        dmtNode.add(new DefaultMutableTreeNode("Supported" + valueDivider + supported));

        String description = software.getDescription();
        dmtNode.add(new DefaultMutableTreeNode("Description" + valueDivider + description));

        String year = software.getYear();
        dmtNode.add(new DefaultMutableTreeNode("Year" + valueDivider + year));

        String publisher = software.getPublisher();
        dmtNode.add(new DefaultMutableTreeNode("Publisher" + valueDivider + publisher));

        if (software.getInfo().size() > 0) {
            dmtNode.add(getInfoNode(software));
        }

        if (software.getSharedfeat().size() > 0) {
            dmtNode.add(getSharedfeatNode(software));
        }

        if (software.getPart().size() > 0) {
            dmtNode.add(getPartNodes(software));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getInfoNode(Software software) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Info");
        for (Info info : software.getInfo()) {
            String value = info.getValue() != null ? info.getValue() : "";
            dmtNode.add(new DefaultMutableTreeNode(info.getName() + valueDivider + value));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getSharedfeatNode(Software software) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Sharedfeat");
        for (Sharedfeat sharedfeat : software.getSharedfeat()) {
            String value = sharedfeat.getValue() != null ? sharedfeat.getValue() : "";
            dmtNode.add(new DefaultMutableTreeNode(sharedfeat.getName() + valueDivider + value));
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
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Part" + softwareDivider + part.getName());
        dmtNode.add(new DefaultMutableTreeNode("Interface" + valueDivider + part.getInterface()));
        if (part.getDataarea().size() > 0) {
            dmtNode.add(getDataarea(part.getDataarea()));
        }
        if (part.getDipswitch().size() > 0) {
            dmtNode.add(getDipswitch(part.getDipswitch()));
        }
        if (part.getDiskarea().size() > 0) {
            dmtNode.add(getDiskarea(part.getDiskarea()));
        }
        if (part.getFeature().size() > 0) {
            dmtNode.add(getFeature(part.getFeature()));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode getDataarea(List<Dataarea> dataareas) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dataarea(s)");
        for (Dataarea dataarea : dataareas) {
            DefaultMutableTreeNode dataareaNode = new DefaultMutableTreeNode(dataarea.getName());
            dataareaNode.add(new DefaultMutableTreeNode("Size" + valueDivider + dataarea.getSize()));
            dataareaNode.add(new DefaultMutableTreeNode("Width" + valueDivider + dataarea.getWidth()));
            dataareaNode.add(new DefaultMutableTreeNode("Endianness" + valueDivider + dataarea.getEndianness()));
            dataareaNode.add(getRoms(dataarea.getRom()));
            dmtNode.add(dataareaNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDipswitch(List<Dipswitch> dipswitchs) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dipswitch(s)");
        for (Dipswitch dipswitch : dipswitchs) {
            DefaultMutableTreeNode dipswitchNode = new DefaultMutableTreeNode("Name" + valueDivider +
                    dipswitch.getName());
            dipswitchNode.add(new DefaultMutableTreeNode("Mask" + valueDivider +
                    dipswitch.getMask()));
            dipswitchNode.add(new DefaultMutableTreeNode("Tag" + valueDivider +
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
            DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(feature.getName() + valueDivider +
                    value);
            dmtNode.add(featureNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDipvalues(List<Dipvalue> dipvalues) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Dipvalue(s)");
        for (Dipvalue dipvalue : dipvalues) {
            DefaultMutableTreeNode dipvalueNode = new DefaultMutableTreeNode(dipvalue.getName() + valueDivider +
                    dipvalue.getValue());
            dmtNode.add(dipvalueNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getDisks(List<Disk> disks) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Disk(s)");
        for (Disk disk : disks) {
            DefaultMutableTreeNode diskNode = new DefaultMutableTreeNode(disk.getName());
            diskNode.add(new DefaultMutableTreeNode("SHA1" + valueDivider + disk.getSha1()));
            diskNode.add(new DefaultMutableTreeNode("Status" + valueDivider + disk.getStatus()));
            diskNode.add(new DefaultMutableTreeNode("Writeable" + valueDivider + disk.getWriteable()));
            dmtNode.add(diskNode);
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode getRoms(List<Rom> roms) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Disk(s)");
        for (Rom rom : roms) {
            DefaultMutableTreeNode romNode = new DefaultMutableTreeNode(rom.getName());
            romNode.add(new DefaultMutableTreeNode("Size" + valueDivider + rom.getSize()));
            romNode.add(new DefaultMutableTreeNode("CRC" + valueDivider + rom.getCrc()));
            romNode.add(new DefaultMutableTreeNode("SHA1" + valueDivider + rom.getSha1()));
            romNode.add(new DefaultMutableTreeNode("Offset" + valueDivider + rom.getOffset()));
            romNode.add(new DefaultMutableTreeNode("Value" + valueDivider + rom.getValue()));
            romNode.add(new DefaultMutableTreeNode("Status" + valueDivider + rom.getStatus()));
            romNode.add(new DefaultMutableTreeNode("Loadflag" + valueDivider + rom.getLoadflag()));
            dmtNode.add(romNode);
        }
        return dmtNode;
    }

}
