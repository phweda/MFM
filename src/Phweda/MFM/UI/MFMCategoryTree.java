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

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/28/2015
 * Time: 10:41 AM
 */
public class MFMCategoryTree {

    private static JCheckBoxTree jcbtree;

    public MFMCategoryTree(TreeMap<String, ArrayList<String>> treeMap) {
        buildTree(treeMap);
    }

    public static JCheckBoxTree getJCBTree() {
        return jcbtree;
    }

    private void buildTree(TreeMap<String, ArrayList<String>> treeMap) {

        DefaultMutableTreeNode root;
        root = new DefaultMutableTreeNode("root");
        jcbtree = new JCheckBoxTree(root);

        for (String category : treeMap.keySet()) {
            DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(category);
            if (!treeMap.get(category).isEmpty()) {
                for (String childCat : treeMap.get(category)) {
                    catNode.add(new DefaultMutableTreeNode(childCat));
                }
            }
            root.add(catNode);
        }
    }
}
