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

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/28/2015
 * Time: 10:41 AM
 */
public class MFMCategoryTree {

    private JCheckBoxTree jcbtree;

    public MFMCategoryTree(SortedMap<String, ArrayList<String>> treeMap) {
        buildTree(treeMap);
    }

    public JCheckBoxTree getJCBTree() {
        return jcbtree;
    }

    private void buildTree(SortedMap<String, ArrayList<String>> treeMap) {

        DefaultMutableTreeNode root;
        root = new DefaultMutableTreeNode("root");
        jcbtree = new JCheckBoxTree(root);

        for (Map.Entry<String, ArrayList<String>> entry : treeMap.entrySet()) {
            DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(entry.getKey());
            if (!entry.getValue().isEmpty()) {
                for (String childCat : entry.getValue()) {
                    catNode.add(new DefaultMutableTreeNode(childCat));
                }
            }
            root.add(catNode);
        }
    }
}
