/*
 * MAME FILE MANAGER - MAME resources management tool
 * Copyright (c) 2016.  Author phweda : phweda1@yahoo.com
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

package Phweda.utils;

import Phweda.MFM.MFM;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/12/11
 * Time: 8:00 PM
 */
public class SwingUtils {
    private static ArrayList<String> LookandFeelNames;
    private static HashMap<String, String> LookandFeelClasses;
    static UIManager.LookAndFeelInfo[] LnFinfo;

    public static HashMap<String, String> getLookandFeelClasses() {
        return LookandFeelClasses;
    }

    public static ArrayList LandFNames() {
        if (LookandFeelNames != null) {
            return LookandFeelNames;
        } else {
            LookandFeelNames = new ArrayList<String>();
            LookandFeelClasses = new HashMap<String, String>();
            LnFinfo = UIManager.getInstalledLookAndFeels();
            //   System.out.println(UIManager.getLookAndFeelDefaults().toString());
            for (UIManager.LookAndFeelInfo info : LnFinfo) {
                LookandFeelNames.add(info.getName());
                LookandFeelClasses.put(info.getName(), info.getClassName());
            }
            LookandFeelNames.trimToSize();
        }
        // System.out.println(LookandFeelNames);
        // System.out.println(LookandFeelClasses);
        return LookandFeelNames;
    }

    public static void changeLandF(final String name, final Container container) {
        //    System.out.println("LF : " + name);

        // Find which one has this classname
        // NOTE we should always have it since we provided the list
        // TODO now we save state so this is not always safe 7/8/13

        // NOTE should always be on the EDT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(LookandFeelClasses.get(name));
                    SwingUtilities.updateComponentTreeUI(container);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Add 3rd party LFs here.
     * <p/>
     * com.jgoodies.looks.windows.WindowsLookAndFeel
     * com.jgoodies.looks.plastic.PlasticLookAndFeel
     * com.jgoodies.looks.plastic.Plastic3DLookAndFeel
     * com.jgoodies.looks.plastic.PlasticXPLookAndFeel
     * <p/>
     * com.seaglasslookandfeel.SeaGlassLookAndFeel
     * <p/>
     * Jar names
     * seaglasslookandfeel-0.2.jar
     * JTattoo-1.6.10.jar
     * jgoodies-looks-2.6.0.jar
     * jgoodies-common-1.7.0.jar
     */

    public static void load3rdPartyLFs(String jarsDir) {
        UIManager.LookAndFeelInfo[] LnFs = UIManager.getInstalledLookAndFeels();

        ArrayList<UIManager.LookAndFeelInfo> newLnFs = new ArrayList<UIManager.LookAndFeelInfo>
                (Arrays.asList(LnFs));

/*      NOTE must add Jars to MANIFEST

*/
        // NOTE separate try/catch for each different jar so we can load whichever we have
        try {
            // JGoodies     http://www.jgoodies.com/
            if (new File(jarsDir + "jgoodies-looks-2.6.0.jar").exists() &&
                    new File(jarsDir + "jgoodies-common-1.7.0.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "com.jgoodies.looks.windows.WindowsLookAndFeel")) {
                // JGoodies
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JG-Windows", "com.jgoodies.looks.windows.WindowsLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JG-Plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo("JG-Plastic3D",
                        "com.jgoodies.looks.plastic.Plastic3DLookAndFeel"));

            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

/*   Bug Seaglass freezes
        try {
            // SeaGlass   https://seaglass.googlecode.com
            if (new File(jarsDir + "seaglasslookandfeel-0.2.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "com.seaglasslookandfeel.SeaGlassLookAndFeel")) {
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "SeaGlass", "com.seaglasslookandfeel.SeaGlassLookAndFeel"));
            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
*/

        try {
            // Synthetica   http://www.javasoft.de/synthetica/
            if (new File(jarsDir + "synthetica.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel")) {
                newLnFs.add(new UIManager.LookAndFeelInfo("Synthetica",
                        "de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel"));

            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            // EaSynth  http://www.easynth.com/
            if (new File(jarsDir + "EaSynthLookAndFeel.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "com.easynth.lookandfeel.EaSynthLookAndFeel")) {
                newLnFs.add(new UIManager.LookAndFeelInfo("EaSynth",
                        "com.easynth.lookandfeel.EaSynthLookAndFeel"));

            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            // LiquidLnF  https://java.net/projects/liquidlnf
            if (new File(jarsDir + "LiquidLnF.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "com.birosoft.liquid.LiquidLookAndFeel")) {
                newLnFs.add(new UIManager.LookAndFeelInfo("LiquidLnF",
                        "com.birosoft.liquid.LiquidLookAndFeel"));

            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        // MAC OS only  MacOS
        try {
            // Quaqua  <http://www.randelshofer.ch/quaqua/>
            if (new File(jarsDir + "quaqua.jar").exists() && // MFM.OS_version.contains("MacOS") &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "ch.randelshofer.quaqua.QuaquaLookAndFeel")) {
                newLnFs.add(new UIManager.LookAndFeelInfo("Quaqua",
                        "ch.randelshofer.quaqua.QuaquaLookAndFeel"));

            }
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        try {
            // JTattoo   http://www.jtattoo.net/index.html
            if (new File(jarsDir + "JTattoo-1.6.10.jar").exists() &&
                    null != SwingUtils.class.getClassLoader().loadClass(
                            "com.jtattoo.plaf.smart.SmartLookAndFeel")) {
                // JTattoo
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Smart", "com.jtattoo.plaf.smart.SmartLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Acryl", "com.jtattoo.plaf.acryl.AcrylLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Aero", "com.jtattoo.plaf.aero.AeroLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Aluminium", "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Bernstein", "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-HiFi", "com.jtattoo.plaf.hifi.HiFiLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Mint", "com.jtattoo.plaf.mint.MintLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Noire", "com.jtattoo.plaf.noire.NoireLookAndFeel"));
                newLnFs.add(new UIManager.LookAndFeelInfo(
                        "JT-Texture", "com.jtattoo.plaf.texture.TextureLookAndFeel"));

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if (newLnFs.size() > LnFs.length) {
            UIManager.setInstalledLookAndFeels((newLnFs.toArray(new UIManager
                    .LookAndFeelInfo[newLnFs.size()])));

            LookandFeelNames = null;
            LandFNames();

        }
    }

    /**
     * Better change font Hack
     *
     * @param component
     * @param fontSize
     */
    public static void changeFont(final Component component, final int fontSize) {
        // NOTE should always be on the EDT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    if (component == null || fontSize < 4) {
                        return;
                    }

                    Font f = component.getFont();
                    component.setFont(new Font(f.getName(), f.getStyle(), fontSize));
                    if (component instanceof JTree) {
                        updateIcons((JTree) component);
                    }
                    if (MFM.isSystemDebug()) {
                        // System.out.println(component.getClass().toString() + " : changed font");
                    }
                    if (component instanceof Container) {
                        for (Component child : ((Container) component).getComponents()) {
                            changeFont(child, fontSize);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * NOTE MAJOR HACK
     */

    public static void resizeFonts(final int num) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Font regularFont = null;
                    Font acceleratorFont = null;
                    UIDefaults uiLFd = UIManager.getLookAndFeel().getDefaults();
                    Set<Object> keys = uiLFd.keySet();
                    for (Object key : keys) {
                        if (key.toString().endsWith(".font")) {
                            if (regularFont == null) {
                                // fixme throws cast exception
                                FontUIResource font = (FontUIResource) uiLFd.get(key);
                                float flt = (font.getSize() + num);
                                if (MFM.isSystemDebug()) {
                                    System.out.println("flt is : " + flt);
                                }
                                regularFont = font.deriveFont(flt);
                            }
                            UIManager.put(key, new FontUIResource(regularFont));
                            if (MFM.isSystemDebug()) {
                                // System.out.println(key + " set to font size : " + regularFont.getSize());
                            }
                        } else if (key.toString().endsWith(".acceleratorFont")) {
                            if (acceleratorFont == null) {
                                FontUIResource font = (FontUIResource) uiLFd.get(key);
                                float flt = (font.getSize() + num);
                                if (MFM.isSystemDebug()) {
                                    System.out.println("flt is : " + flt);
                                }
                                acceleratorFont = font.deriveFont(flt);
                            }
                            UIManager.put(key, new FontUIResource(acceleratorFont));
                            if (MFM.isSystemDebug()) {
                                // System.out.println(key + " set to font size : " + acceleratorFont.getSize());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static Icon scale(Icon icon, double scaleFactor, JTree tree) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        width = (int) Math.ceil(width * scaleFactor);
        height = (int) Math.ceil(height * scaleFactor);

        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();
        g.scale(scaleFactor, scaleFactor);
        icon.paintIcon(tree, g, 0, 0);
        g.dispose();

        return new ImageIcon(image);
    }

    public static void updateIcons(JTree tree) {
        Font defaultFont = UIManager.getFont("Tree.font");
        Font currentFont = tree.getFont();

        double newScale = (double)
                currentFont.getSize2D() / defaultFont.getSize2D();

        DefaultTreeCellRenderer renderer =
                (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setOpenIcon(
                scale(UIManager.getIcon("Tree.openIcon"), newScale, tree));
        renderer.setClosedIcon(
                scale(UIManager.getIcon("Tree.closedIcon"), newScale, tree));
        renderer.setLeafIcon(
                scale(UIManager.getIcon("Tree.leafIcon"), newScale, tree));

        Collection<Integer> iconSizes = Arrays.asList(
                renderer.getOpenIcon().getIconHeight(),
                renderer.getClosedIcon().getIconHeight(),
                renderer.getLeafIcon().getIconHeight());

        // Convert points to pixels
        Point2D p = new Point2D.Float(0, currentFont.getSize2D());
        FontRenderContext context =
                tree.getFontMetrics(currentFont).getFontRenderContext();
        context.getTransform().transform(p, p);
        int fontSizeInPixels = (int) Math.ceil(p.getY());

        tree.setRowHeight(
                Math.max(fontSizeInPixels, Collections.max(iconSizes) + 2));
    }
}
