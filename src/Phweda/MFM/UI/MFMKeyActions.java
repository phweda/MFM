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

import Phweda.MFM.*;
import Phweda.MFM.Utils.AnalyzeCategories;
import Phweda.MFM.Utils.MFM_DATmaker;
import Phweda.MFM.mame.Mame;
import Phweda.utils.DirectorytoXML;
import Phweda.utils.XMLUtils;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import static Phweda.MFM.MFM_Constants.*;
import static Phweda.MFM.UI.MFMController.*;
import static Phweda.MFM.UI.MFMListActions.pickList;
import static Phweda.MFM.Utils.AnalyzeCategories.analyzeCategories;
import static Phweda.utils.FileUtils.csvFileFilter;

final class MFMKeyActions {

    static void keyTyped(KeyEvent e) {
//        System.out.println("Typed: " + e.getKeyCode() + "\t" + e.getKeyChar());
        // If ALT then it is a mnemonic skip
        if (!e.isAltDown()) {
            if (e.getKeyChar() == 'a') {
                changeList(MFMListBuilder.ALL);
            }

            if (e.getKeyChar() == 'b') {
                changeList(MFMListBuilder.BIOS);
            }

            if (e.getKeyChar() == 'c') {
                changeList(MFMListBuilder.CLONE);
            }

            if (e.getKeyChar() == 'd') {
                changeList(MFMListBuilder.DEVICES);
            }

            if (e.getKeyChar() == 'h') {
                changeList(MFMListBuilder.HORIZONTAL);
            }

            if (e.getKeyChar() == 'n') {
                changeList(MFMListBuilder.NO_CLONE);
            }

            if ((e.getKeyChar() == 'r') && MFM_Data.getInstance().getDataVersion().contains(MFMListBuilder.ALL)) {
                changeList(MFMListBuilder.RUNNABLE);
            }

            if (e.getKeyChar() == 's') {
                changeList(MFMListBuilder.SYSTEMS);
            }

            if (e.getKeyChar() == 'v') {
                changeList(MFMListBuilder.VERTICAL);
            }
        }
    }

    static void keyPressed(KeyEvent e) {

        if ((e.getKeyCode() == KeyEvent.VK_A) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.ARCADE);
        }

        if ((e.getKeyCode() == KeyEvent.VK_K) && e.isControlDown()) {
            showHelp(HOT_KEYS);
        }

        if ((e.getKeyCode() == KeyEvent.VK_N) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.NO_IMPERFECT);
        }

        if ((e.getKeyCode() == KeyEvent.VK_O) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            MFMAction.openFile();
        }

        if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.RASTER);
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) && !e.isShiftDown()) {
            changeList(MFMListBuilder.SIMULTANEOUS);
        }

        if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            changeList(MFMListBuilder.VECTOR);
        }

        if ((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            showItemXML();
        }

        // Overloaded CNTRL Z below in Special Functions
        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
            MFMAction.zipLogs();
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)) {
            showNextList(true, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && e.isControlDown()) {
            showNextList(true, false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && ((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
            showNextList(false, true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT) && ((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
            showNextList(false, false);
        }

        //================== SPECIAL FUNCTIONS  =================

        if ((e.getKeyCode() == KeyEvent.VK_D) && e.isControlDown() && e.isShiftDown()) {
            MFM_DATmaker.saveBuiltinListsDATs();
        }

        if ((e.getKeyCode() == KeyEvent.VK_J) && e.isControlDown() && e.isShiftDown()) {
            listMachinesToJSON(false);
        }

        if ((e.getKeyCode() == KeyEvent.VK_K) && e.isControlDown() && e.isShiftDown()) {
            listMachinesToJSON(true);
        }

        if ((e.getKeyCode() == KeyEvent.VK_M) && e.isControlDown() && e.isShiftDown()) {
            MAMEInfo.dumpManuDriverList();
        }

        if ((e.getKeyCode() == KeyEvent.VK_P) && e.isControlDown() && e.isShiftDown()) {
            MAMEInfo.parseListInfo();
        }

        if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown() && e.isShiftDown()) {
            new MAME_Stats().saveStats();
        }

        if ((e.getKeyCode() == KeyEvent.VK_T) && e.isControlDown() && e.isShiftDown()) {
            String listLabelText = getCurrentListText();
            MFM_Wiki.listtoWikiTable(listLabelText.substring(0, listLabelText.indexOf(SPACE_CHAR)));
        }

        if ((e.getKeyCode() == KeyEvent.VK_W) && e.isControlDown() && e.isShiftDown()) {
            MAME_Stats stats = new MAME_Stats();
            MFM_Wiki.statstoWikiTable(stats.getStatsArray(), stats.getStatsHeaders());
        }

        // Pretty print the MAME xml to file
        if ((e.getKeyCode() == KeyEvent.VK_F1) && e.isControlDown() && e.isShiftDown()) {
            Mame mame = MFM_Data.getInstance().getMame();
            String outputPath = MFM.getMfmDataDir() + mame.getBuild() + ".xml";
            boolean result = XMLUtils.prettyPrintXML(mame, outputPath);
            if (result) {

            }
        }

        // Save current DataSets info to XML
        if ((e.getKeyCode() == KeyEvent.VK_F5) && e.isControlDown() && e.isShiftDown()) {
            saveDataSetstoXML();
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && e.isShiftDown()) {
            analyzeCategories();
        }

        if ((e.getKeyCode() == KeyEvent.VK_Z) && e.isControlDown() && e.isAltDown()) {
            JFileChooser jfc = new JFileChooser(MFM.getMfmCategoryDir());
            jfc.setFileFilter(csvFileFilter);
            jfc.showOpenDialog(mainFrame);
            File file = jfc.getSelectedFile();
            String fileName = JOptionPane.showInputDialog("Enter new Categories File Name");
            if (file.exists() && !fileName.isEmpty()) {
                AnalyzeCategories.enterNewCategories(file, fileName);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Name is empty or File selected does not exist",
                        "Category input Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private static void saveDataSetstoXML() {
        DirectorytoXML dirtoXML = new DirectorytoXML();
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.showDialog(mainFrame, "Select");

        dirtoXML.saveDirectorytoXML(jfc.getSelectedFile().getAbsolutePath());
    }


    private static void listMachinesToJSON(boolean everything) {

        if (everything) {
            MFMListActions.listDataToJSON(MFMPlayLists.EVERYTHING);
            return;
        }

        String list = pickList(true, "Pick list to export MAME data to JSON");
        if (list != null) {
            MFMListActions.listDataToJSON(list);
            JOptionPane.showMessageDialog(mainFrame, FILES_ARE_IN_THE_MFM_LISTS_FOLDER);
        }
    }


}
