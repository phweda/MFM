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
import Phweda.MFM.mame.softwarelist.Software;
import Phweda.MFM.mame.softwarelist.Softwarelist;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 11/11/2017
 * Time: 7:39 PM
 */
public class SoftwareListstoJTree extends JPanel {
    static final String COPY = "Copy";
    private static final int FRAME_WIDTH = MFMUI.screenSize.width / 5;
    private static final int FRAME_HEIGHT = MFMUI.screenSize.height - 100;
    private static JTree jTree;
    private static Softwarelist root;
    private static TreeMap<String, Software> softwareLists;
    private static SoftwareListstoJTree ourInstance;
    private static MouseListener ml = null;
    private final String valueDivider = " \u00bb ";
    private final String machineDivider = " \u00A8 ";

    private SoftwareListstoJTree() {
        if (root == null) {
            softwareLists = MAMEInfo.getSoftwareLists();
        }
        this.setPreferredSize(new Dimension(FRAME_WIDTH - 10, FRAME_HEIGHT - 20));

        DefaultMutableTreeNode top = createTreeNode("SoftwareLists");
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

        ml = new MouseAdapter() {
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

    public static SoftwareListstoJTree getInstance(boolean refresh) {
        if (ourInstance == null || refresh) {
            root = null; // ensure full refresh of Data
            ourInstance = new SoftwareListstoJTree();
        }
        return ourInstance;
    }

    JTree getSoftwareListsjTree() {
        return jTree;
    }

    DefaultMutableTreeNode getMachineNode(String machineName) {
        if (machineName == null || machineName.isEmpty()) {
            return null;
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        Enumeration<DefaultMutableTreeNode> children = root.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode node = children.nextElement();

            String nodeString = node.getUserObject().toString();
            String nodeMachine = nodeString.substring(nodeString.lastIndexOf(' ') + 1, nodeString.length());
            if (nodeMachine.equals(machineName)) {
                return node;
            }
        }
        return null;
    }

    protected void copytoClipboard(String nodeValue) {
        nodeValue = nodeValue.contains(valueDivider) ?
                nodeValue.split(valueDivider)[1].trim() :
                nodeValue.split(machineDivider)[1].trim();

        StringSelection ss = new StringSelection(nodeValue);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(ss, null);
    }

    /**
     * This takes Mame root to create full MAME XML JTree
     */
    private DefaultMutableTreeNode createTreeNode(String root) {
        DefaultMutableTreeNode dmtNode = null;
        dmtNode = new DefaultMutableTreeNode(root);

        java.util.List<Software> list = new ArrayList<Software>(softwareLists.values());
        list.sort(Comparator.comparing(Software::getName));

        for (Software software : list) {
            dmtNode.add(createSoftwareNode(software));
        }
        return dmtNode;
    }

    /**
     * NOTE we do a lot of checking even for required Attrs. This is to try and cover previous XML versions
     * fixme those checks may be totally extraneous
     *
     * @param software Mame software list to display
     * @return software node
     */
    private DefaultMutableTreeNode createSoftwareNode(Software software) {
        DefaultMutableTreeNode dmtNode = null;

        // Name is always required
        String name = software.getName();
        // dmtNode.add(new DefaultMutableTreeNode("name" + valueDivider + name));
        dmtNode = new DefaultMutableTreeNode("Software List" + machineDivider + name);

        String description = software.getDescription();
        if (description != null && !description.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("description" + valueDivider + description));
        }

        String year = software.getYear();
        if (year != null && !year.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("year" + valueDivider + year));
        }

        String cloneof = software.getCloneof();
        if (cloneof != null && !cloneof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("cloneof" + valueDivider + cloneof));
        }

        // software.

        return dmtNode;
    }

}
