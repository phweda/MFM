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

import com.github.phweda.mfm.MAMEInfo;
import com.github.phweda.mfm.MAMEexe;
import com.github.phweda.mfm.MFMSettings;
import com.github.phweda.mfm.mame.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/18/2016
 * Time: 2:09 PM
 */
@SuppressWarnings("JavaDoc")
public class MAMEtoJTree extends JPanel {

    static final String COPY = "Copy";
    private static final String DEFAULT = "default";
    private static final String VALUE = "value";
    private static final String STATUS = "status";
    public static final String SOUND_CHANNELS = "sound channels";
    private static final int FRAME_WIDTH = MFMUI.screenSize.width / 5;
    private static final int FRAME_HEIGHT = MFMUI.screenSize.height - 100;
    private JTree jTree;
    private static transient Mame root;
    private static MAMEtoJTree ourInstance;
    private static final String VALUE_DIVIDER = " \u00bb ";
    private static final String MACHINE_DIVIDER = " \u00A8 ";

    private MAMEtoJTree(boolean load) {
        // 0.9.5 we do not automatically load XML
        if (!load) {
            return;
        }
        root = MAMEInfo.getMame();
        this.setPreferredSize(new Dimension(FRAME_WIDTH - 10, FRAME_HEIGHT - 20));

        DefaultMutableTreeNode top = createTreeNode(root);
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
                if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON3)) {
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
        // Dumps Mame data without any whitespace or tags - determine true data size
        // dataDump(top,"MAME_tree_data.txt");
    }

    public static MAMEtoJTree getInstance(boolean refresh) {
        if ((ourInstance == null) || refresh) {
            root = null; // ensure full refresh of Data
            ourInstance = new MAMEtoJTree(MFMSettings.getInstance().isShowXML());
        }
        return ourInstance;
    }

    private static void exit() {
        System.out.println("MAMEtoJTree exit");
        System.exit(0);
    }

    /**
     * main to test this class
     */
    public static void main(String[] args) {

        JFrame frame = new JFrame("Objects to JTree");

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();
        int screenHeight = dim.height;
        int screenWidth = dim.width;

        frame.setBounds((screenWidth - FRAME_WIDTH) / 2,
                (screenHeight - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);

        frame.setBackground(Color.lightGray);
        frame.getContentPane().setLayout(new BorderLayout());
        WindowListener wndCloser = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        };
        frame.addWindowListener(wndCloser);

        try {
            root = jaxb();
        } catch (RuntimeException | JAXBException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Exception",
                    JOptionPane.WARNING_MESSAGE);

            ex.printStackTrace();
            exit();
        }

        frame.getContentPane().add(new MAMEtoJTree(true), BorderLayout.CENTER);
        frame.validate();
        frame.setVisible(true);
    }

    /**
     * This is simply to provide MAME input to test this class
     */
    private static Mame jaxb() throws JAXBException {
        Mame mame;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(com.github.phweda.mfm.mame.Mame.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            // hard code path to mame exe
            MAMEexe.setBaseArgs("E:\\Test\\mame64.exe"); // Enter path here
            ArrayList<String> args = new ArrayList<>(Arrays.asList(MAMEexe.LISTXML, "*"));
            Process process = MAMEexe.run(args);
            InputStream inputStream = process.getInputStream();
            mame = (Mame) jaxbUnmarshaller.unmarshal(inputStream);

            System.out.println("Machines" + mame.getMachine().size());
        } catch (JAXBException e) {
            e.printStackTrace();
            throw e;
        }
        return mame;
    }

    JTree getMAMEjTree() {
        return jTree;
    }

    DefaultMutableTreeNode getMachineNode(String machineName) {
        if ((machineName == null) || machineName.isEmpty()) {
            return null;
        }
        // As of 0.9.5 tree may not exist
        else if (jTree == null) {
            return createMachineNode(MAMEInfo.getMachine(machineName));
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        Enumeration<DefaultMutableTreeNode> children = root.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode node = children.nextElement();

            String nodeString = node.getUserObject().toString();
            String nodeMachine = nodeString.substring(nodeString.lastIndexOf(' ') + 1);
            if (nodeMachine.equals(machineName)) {
                return node;
            }
        }
        return null;
    }

    /**
     * @param machine
     * @return
     * @XmlElement(required = true)
     * protected String description;
     * protected String year;
     * protected String manufacturer;
     * protected List<Biosset> biosset;
     * protected List<Rom> rom;
     * protected List<Disk> disk;
     * @XmlElement(name = "device_ref")
     * protected List<DeviceRef> deviceRef;
     * protected List<Sample> sample;
     * protected List<Chip> chip;
     * protected List<Display> display;
     * protected Sound sound;
     * protected Input input;
     * protected List<Dipswitch> dipswitch;
     * protected List<Configuration> configuration;
     * protected List<Port> port;
     * protected List<Adjuster> adjuster;
     * protected Driver driver;
     * protected List<Feature> feature;
     * protected List<Device> device;
     * protected List<Slot> slot;
     * protected List<Softwarelist> softwarelist;
     * protected List<Ramoption> ramoption;
     * @XmlAttribute(name = "name", required = true)
     * protected String name;
     * @XmlAttribute(name = "sourcefile")
     * protected String sourcefile;
     * @XmlAttribute(name = "isbios")
     * @XmlJavaTypeAdapter(CollapsedStringAdapter.class) protected String isbios;
     * @XmlAttribute(name = "isdevice")
     * @XmlJavaTypeAdapter(CollapsedStringAdapter.class) protected String isdevice;
     * @XmlAttribute(name = "ismechanical")
     * @XmlJavaTypeAdapter(CollapsedStringAdapter.class) protected String ismechanical;
     * @XmlAttribute(name = "runnable")
     * @XmlJavaTypeAdapter(CollapsedStringAdapter.class) protected String runnable;
     * @XmlAttribute(name = "cloneof")
     * protected String cloneof;
     * @XmlAttribute(name = "romof")
     * protected String romof;
     * @XmlAttribute(name = "sampleof")
     * protected String sampleof;
     */

    @SuppressWarnings("JavadocReference")
    static void copytoClipboard(String nodeValue) {
        nodeValue = nodeValue.contains(VALUE_DIVIDER) ?
                nodeValue.split(VALUE_DIVIDER)[1].trim() :
                nodeValue.split(MACHINE_DIVIDER)[1].trim();

        StringSelection ss = new StringSelection(nodeValue);
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(ss, null);
    }

    /**
     * Dumps Mame data without any whitespace - determine true data size
     *
     * @param root tree root
     * @param path file location
     */
    @SuppressWarnings("squid:UnusedPrivateMethod")
    private static void dataDump(DefaultMutableTreeNode root, String path) {
        try (PrintWriter pw = new PrintWriter(new File(path))) {

            Enumeration<DefaultMutableTreeNode> en = root.preorderEnumeration();
            while (en.hasMoreElements()) {
                DefaultMutableTreeNode node = en.nextElement();
                String nodeValue = node.getUserObject().toString();
                if (nodeValue.contains(VALUE_DIVIDER) || nodeValue.contains(MACHINE_DIVIDER)) {
                    nodeValue = nodeValue.contains(VALUE_DIVIDER) ?
                            nodeValue.split(VALUE_DIVIDER)[1].trim() :
                            nodeValue.split(MACHINE_DIVIDER)[1].trim();
                }

                pw.print(nodeValue);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * This takes Mame root to create full MAME XML JTree
     */
    private DefaultMutableTreeNode createTreeNode(Mame root) {
        DefaultMutableTreeNode dmtNode;
        dmtNode = new DefaultMutableTreeNode("Mame");

        String build = root.getBuild();
        if ((build != null) && !build.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("build" + VALUE_DIVIDER + build));
        }

        String mameconfig = root.getMameconfig();
        if ((mameconfig != null) && !mameconfig.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mameconfig" + VALUE_DIVIDER + mameconfig));
        }

        String debug = root.getDebug();
        if ((debug != null) && !debug.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("debug" + VALUE_DIVIDER + debug));
        }

        List<Machine> machines = new ArrayList<>(root.getMachine());
        machines.sort(Comparator.comparing(Machine::getName));

        for (Machine machine : machines) {
            dmtNode.add(createMachineNode(machine));
        }
        return dmtNode;
    }

    /**
     * NOTE we do a lot of checking even for required Attrs. This is to try and cover previous XML versions
     *
     * @param machine Mame machine to display
     * @return machine node
     */
    private static DefaultMutableTreeNode createMachineNode(Machine machine) {
        DefaultMutableTreeNode dmtNode;

        // Name is always required
        String name = machine.getName();
        // dmtNode.add(new DefaultMutableTreeNode("name" + VALUE_DIVIDER + name));
        dmtNode = new DefaultMutableTreeNode("Machine" + MACHINE_DIVIDER + name);

        String description = machine.getDescription();
        if ((description != null) && !description.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(Machine.DESCRIPTION + VALUE_DIVIDER + description));
        }

        String year = machine.getYear();
        if ((year != null) && !year.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("year" + VALUE_DIVIDER + year));
        }

        String manufacturer = machine.getManufacturer();
        if ((manufacturer != null) && !manufacturer.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(Machine.MANUFACTURER + VALUE_DIVIDER + manufacturer));
        }

        String sourcefile = machine.getSourcefile();
        if ((sourcefile != null) && !sourcefile.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(Machine.SOURCEFILE + VALUE_DIVIDER + sourcefile));
        }

        String isbios = machine.getIsbios();
        if ((isbios != null) && !isbios.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(Machine.ISBIOS + VALUE_DIVIDER + isbios));
        }

        String isdevice = machine.getIsdevice();
        if ((isdevice != null) && !isdevice.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("isdevice" + VALUE_DIVIDER + isdevice));
        }

        String ismechanical = machine.getIsmechanical();
        if ((ismechanical != null) && !ismechanical.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ismechanical" + VALUE_DIVIDER + ismechanical));
        }

        String runnable = machine.getRunnable();
        if ((runnable != null) && !runnable.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("runnable" + VALUE_DIVIDER + runnable));
        }

        String cloneof = machine.getCloneof();
        if ((cloneof != null) && !cloneof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("cloneof" + VALUE_DIVIDER + cloneof));
        }

        String romof = machine.getRomof();
        if ((romof != null) && !romof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("romof" + VALUE_DIVIDER + romof));
        }

        String sampleof = machine.getSampleof();
        if ((sampleof != null) && !sampleof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sampleof" + VALUE_DIVIDER + sampleof));
        }

        List<Biosset> biosets = machine.getBiosset();
        for (Biosset bioset : biosets) {
            dmtNode.add(createBiossetNode(bioset));
        }

        List<Rom> roms = machine.getRom();
        for (Rom rom : roms) {
            dmtNode.add(createRomNode(rom));
        }

        List<Disk> disks = machine.getDisk();
        for (Disk disk : disks) {
            dmtNode.add(createDiskNode(disk));
        }

        List<DeviceRef> deviceRefs = machine.getDeviceRef();
        for (DeviceRef deviceRef : deviceRefs) {
            dmtNode.add(createDeviceRefNode(deviceRef));
        }

        List<Sample> samples = machine.getSample();
        for (Sample sample : samples) {
            dmtNode.add(createSampleNode(sample));
        }

        List<Chip> chips = machine.getChip();
        for (Chip chip : chips) {
            dmtNode.add(createChipNode(chip));
        }

        List<Display> displays = machine.getDisplay();
        for (Display display : displays
        ) {
            dmtNode.add(createDisplayNode(display));
        }

        Sound sound = machine.getSound();
        if (sound != null) {
            dmtNode.add(new DefaultMutableTreeNode(SOUND_CHANNELS + VALUE_DIVIDER + sound.getChannels()));
        }

        Input input = machine.getInput();
        if (input != null) {
            dmtNode.add(createInputNode(input));
        }

        List<Dipswitch> dipswitches = machine.getDipswitch();
        for (Dipswitch dipswitch : dipswitches) {
            dmtNode.add(createDipswitchNode(dipswitch));
        }

        List<Configuration> configurations = machine.getConfiguration();
        for (Configuration configuration : configurations) {
            dmtNode.add(createConfigurationNode(configuration));
        }

        List<Port> ports = machine.getPort();
        for (Port port : ports) {
            dmtNode.add(createPortNode(port));
        }

        List<Adjuster> adjusters = machine.getAdjuster();
        for (Adjuster adjuster : adjusters) {
            dmtNode.add(createAdjusterNode(adjuster));
        }

        Driver driver = machine.getDriver();
        if (driver != null) {
            dmtNode.add(createDriverNode(driver));
        }

        List<Feature> features = machine.getFeature();
        for (Feature feature : features) {
            dmtNode.add(createFeatureNode(feature));
        }

        List<Device> devices = machine.getDevice();
        for (Device device : devices) {
            dmtNode.add(createDeviceNode(device));
        }

        List<Slot> slots = machine.getSlot();
        for (Slot slot : slots) {
            dmtNode.add(createSlotNode(slot));
        }

        List<Softwarelist> softwarelists = machine.getSoftwarelist();
        for (Softwarelist softwarelist : softwarelists) {
            dmtNode.add(createSoftwarelistNode(softwarelist));
        }

        List<Ramoption> ramoptions = machine.getRamoption();
        for (Ramoption ramoption : ramoptions) {
            dmtNode.add(createRamoptionNode(ramoption));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createBiossetNode(Biosset bioset) {
        DefaultMutableTreeNode dmtNode;

        String name = bioset.getName();
        dmtNode = new DefaultMutableTreeNode("Biosset" + VALUE_DIVIDER + name);

        String description = bioset.getDescription();
        if ((description != null) && !description.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(Machine.DESCRIPTION + VALUE_DIVIDER + description));
        }

        String adefault = bioset.getDefault();
        if ((adefault != null) && !adefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + adefault));
        }
        return dmtNode;
    }

    private static DefaultMutableTreeNode createRomNode(Rom rom) {
        DefaultMutableTreeNode dmtNode;

        String name = rom.getName();
        dmtNode = new DefaultMutableTreeNode("Rom" + VALUE_DIVIDER + name);

        String bios = rom.getBios();
        if ((bios != null) && !bios.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("bios" + VALUE_DIVIDER + bios));
        }

        String size = rom.getSize();
        if ((size != null) && !size.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("size" + VALUE_DIVIDER + size));
        }

        String crc = rom.getCrc();
        if ((crc != null) && !crc.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("crc" + VALUE_DIVIDER + crc));
        }

        String md5 = rom.getMd5();
        if ((md5 != null) && !md5.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("md5" + VALUE_DIVIDER + md5));
        }

        String sha1 = rom.getSha1();
        if ((sha1 != null) && !sha1.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sha1" + VALUE_DIVIDER + sha1));
        }

        String merge = rom.getMerge();
        if ((merge != null) && !merge.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("merge" + VALUE_DIVIDER + merge));
        }

        String region = rom.getRegion();
        if ((region != null) && !region.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("region" + VALUE_DIVIDER + region));
        }

        String offset = rom.getOffset();
        if ((offset != null) && !offset.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("offset" + VALUE_DIVIDER + offset));
        }

        String status = rom.getStatus();
        if ((status != null) && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(STATUS + VALUE_DIVIDER + status));
        }

        String optional = rom.getOptional();
        if ((optional != null) && !optional.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("optional" + VALUE_DIVIDER + optional));
        }

        String flags = rom.getFlags();
        if ((!flags.isEmpty())) {
            dmtNode.add(new DefaultMutableTreeNode("flags" + VALUE_DIVIDER + flags));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDiskNode(Disk disk) {
        DefaultMutableTreeNode dmtNode;

        String name = disk.getName();
        dmtNode = new DefaultMutableTreeNode("Disk" + VALUE_DIVIDER + name);

        String md5 = disk.getMd5();
        if ((md5 != null) && !md5.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("md5" + VALUE_DIVIDER + md5));
        }

        String sha1 = disk.getSha1();
        if ((sha1 != null) && !sha1.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sha1" + VALUE_DIVIDER + sha1));
        }

        String merge = disk.getMerge();
        if ((merge != null) && !merge.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("merge" + VALUE_DIVIDER + merge));
        }

        String region = disk.getRegion();
        if ((region != null) && !region.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("region" + VALUE_DIVIDER + region));
        }

        String index = disk.getIndex();
        if ((index != null) && !index.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("index" + VALUE_DIVIDER + index));
        }

        String writable = disk.getWritable();
        if ((writable != null) && !writable.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("writable" + VALUE_DIVIDER + writable));
        }

        String status = disk.getStatus();
        if ((status != null) && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(STATUS + VALUE_DIVIDER + status));
        }

        String optional = disk.getOptional();
        if ((optional != null) && !optional.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("optional" + VALUE_DIVIDER + optional));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDeviceRefNode(DeviceRef deviceRef) {
        DefaultMutableTreeNode dmtNode;

        String name = deviceRef.getName();
        dmtNode = new DefaultMutableTreeNode("DeviceRef" + VALUE_DIVIDER + name);

        return dmtNode;
    }

    private static DefaultMutableTreeNode createSampleNode(Sample sample) {
        DefaultMutableTreeNode dmtNode;

        String name = sample.getName();
        dmtNode = new DefaultMutableTreeNode(Machine.SAMPLE + VALUE_DIVIDER + name);

        return dmtNode;
    }

    private static DefaultMutableTreeNode createChipNode(Chip chip) {
        DefaultMutableTreeNode dmtNode;

        String name = chip.getName();
        dmtNode = new DefaultMutableTreeNode("Chip" + VALUE_DIVIDER + name);

        String tag = chip.getTag();
        if ((tag != null) && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + VALUE_DIVIDER + tag));
        }

        String clock = chip.getClock();
        if ((clock != null) && !clock.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("clock" + VALUE_DIVIDER + clock));
        }
        String type = chip.getType();
        if ((type != null) && !type.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("type" + VALUE_DIVIDER + type));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDisplayNode(Display display) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Display" + VALUE_DIVIDER + display.getType());

        String tag = display.getTag();
        if ((tag != null) && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + VALUE_DIVIDER + tag));
        }

        String rotate = display.getRotate();
        if ((rotate != null) && !rotate.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("rotate" + VALUE_DIVIDER + rotate));
        }

        String orientation = display.getOrientation();
        if ((orientation != null) && !orientation.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("orientation" + VALUE_DIVIDER + orientation));
        }

        String flipx = display.getFlipx();
        if ((flipx != null) && !flipx.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("flipx" + VALUE_DIVIDER + flipx));
        }

        String width = display.getWidth();
        if ((width != null) && !width.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("width" + VALUE_DIVIDER + width));
        }

        String height = display.getHeight();
        if ((height != null) && !height.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("height" + VALUE_DIVIDER + height));
        }

        String refresh = display.getRefresh();
        if ((refresh != null) && !refresh.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("refresh" + VALUE_DIVIDER + refresh));
        }

        String colors = display.getColors();
        if ((colors != null) && !colors.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("colors" + VALUE_DIVIDER + colors));
        }

        String pixclock = display.getPixclock();
        if ((pixclock != null) && !pixclock.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("pixclock" + VALUE_DIVIDER + pixclock));
        }

        String htotal = display.getHtotal();
        if ((htotal != null) && !htotal.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("htotal" + VALUE_DIVIDER + htotal));
        }

        String hbend = display.getHbend();
        if ((hbend != null) && !hbend.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hbend" + VALUE_DIVIDER + hbend));
        }

        String hbstart = display.getHbstart();
        if ((hbstart != null) && !hbstart.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hbstart" + VALUE_DIVIDER + hbstart));
        }

        String vbend = display.getVbend();
        if ((vbend != null) && !vbend.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vbend" + VALUE_DIVIDER + vbend));
        }

        String vtotal = display.getVtotal();
        if ((vtotal != null) && !vtotal.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vtotal" + VALUE_DIVIDER + vtotal));
        }

        String vbstart = display.getVbstart();
        if ((vbstart != null) && !vbstart.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vbstart" + VALUE_DIVIDER + vbstart));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createInputNode(Input input) {
        DefaultMutableTreeNode dmtNode;
        dmtNode = new DefaultMutableTreeNode("Input");

        String service = input.getService();
        if ((service != null) && !service.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("service" + VALUE_DIVIDER + service));
        }

        String tilt = input.getTilt();
        if ((tilt != null) && !tilt.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tilt" + VALUE_DIVIDER + tilt));
        }

        String players = input.getPlayers();
        if ((players != null) && !players.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("service" + VALUE_DIVIDER + players));
        }

        String buttons = input.getButtons();
        if ((buttons != null) && !buttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("buttons" + VALUE_DIVIDER + buttons));
        }

        String coins = input.getCoins();
        if ((coins != null) && !coins.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("coins" + VALUE_DIVIDER + coins));
        }


        List<Control> controls = input.getControl();
        for (Control control : controls) {
            dmtNode.add(createControlNode(control));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createControlNode(Control control) {
        DefaultMutableTreeNode dmtNode;

        String type = control.getType();
        dmtNode = new DefaultMutableTreeNode("Control" + VALUE_DIVIDER + type);

        String player = control.getPlayer();
        if ((player != null) && !player.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("player" + VALUE_DIVIDER + player));
        }

        String buttons = control.getButtons();
        if ((buttons != null) && !buttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("buttons" + VALUE_DIVIDER + buttons));
        }

        String reqbuttons = control.getReqbuttons();
        if ((reqbuttons != null) && !reqbuttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("reqbuttons" + VALUE_DIVIDER + reqbuttons));
        }

        String minimum = control.getMinimum();
        if ((minimum != null) && !minimum.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("minimum" + VALUE_DIVIDER + minimum));
        }

        String maximum = control.getMaximum();
        if ((maximum != null) && !maximum.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("maximum" + VALUE_DIVIDER + maximum));
        }

        String sensitivity = control.getSensitivity();
        if ((sensitivity != null) && !sensitivity.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sensitivity" + VALUE_DIVIDER + sensitivity));
        }

        String keydelta = control.getKeydelta();
        if ((keydelta != null) && !keydelta.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("keydelta" + VALUE_DIVIDER + keydelta));
        }

        String reverse = control.getReverse();
        if ((reverse != null) && !reverse.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("reverse" + VALUE_DIVIDER + reverse));
        }

        String ways = control.getWays();
        if ((ways != null) && !ways.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways" + VALUE_DIVIDER + ways));
        }

        String ways2 = control.getWays2();
        if ((ways2 != null) && !ways2.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways2" + VALUE_DIVIDER + ways2));
        }

        String ways3 = control.getWays3();
        if ((ways3 != null) && !ways3.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways3" + VALUE_DIVIDER + ways3));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDipswitchNode(Dipswitch dipswitch) {
        DefaultMutableTreeNode dmtNode;

        String name = dipswitch.getName();
        dmtNode = new DefaultMutableTreeNode("Dipswitch" + VALUE_DIVIDER + name);

        String mask = dipswitch.getMask();
        if ((mask != null) && !mask.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mask" + VALUE_DIVIDER + mask));
        }

        String tag = dipswitch.getTag();
        if ((tag != null) && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + VALUE_DIVIDER + tag));
        }

        List<Diplocation> diplocations = dipswitch.getDiplocation();
        for (Diplocation location : diplocations) {
            dmtNode.add(createDiplocationNode(location));
        }

        List<Dipvalue> dipvalues = dipswitch.getDipvalue();
        for (Dipvalue dipvalue : dipvalues) {
            dmtNode.add(createDipvalueNode(dipvalue));
        }

        List<String> entries = dipswitch.getEntry();
        for (String entry : entries) {
            dmtNode.add(new DefaultMutableTreeNode("entry" + VALUE_DIVIDER + entry));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDiplocationNode(Diplocation diplocation) {
        DefaultMutableTreeNode dmtNode;

        String name = diplocation.getName();
        dmtNode = new DefaultMutableTreeNode("Diplocation" + VALUE_DIVIDER + name);

        String number = diplocation.getNumber();
        if ((number != null) && !number.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("number" + VALUE_DIVIDER + number));
        }

        String inverted = diplocation.getInverted();
        if ((inverted != null) && !inverted.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("inverted" + VALUE_DIVIDER + inverted));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDipvalueNode(Dipvalue dipvalue) {
        DefaultMutableTreeNode dmtNode;

        String name = dipvalue.getName();
        dmtNode = new DefaultMutableTreeNode("Dipvalue" + VALUE_DIVIDER + name);

        String value = dipvalue.getValue();
        if ((value != null) && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(VALUE + VALUE_DIVIDER + value));
        }

        String aDefault = dipvalue.getDefault();
        if ((aDefault != null) && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + aDefault));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createConfigurationNode(Configuration configuration) {
        DefaultMutableTreeNode dmtNode;

        String name = configuration.getName();
        dmtNode = new DefaultMutableTreeNode("Configuration" + VALUE_DIVIDER + name);

        String mask = configuration.getMask();
        if ((mask != null) && !mask.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mask" + VALUE_DIVIDER + mask));
        }

        String tag = configuration.getTag();
        if ((tag != null) && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + VALUE_DIVIDER + tag));
        }

        List<Conflocation> conflocations = configuration.getConflocation();
        for (Conflocation conflocation : conflocations) {
            dmtNode.add(createConflocationNode(conflocation));
        }

        List<Confsetting> confsettings = configuration.getConfsetting();
        for (Confsetting confsetting : confsettings) {
            dmtNode.add(createConfsettingNode(confsetting));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createConflocationNode(Conflocation conflocation) {
        DefaultMutableTreeNode dmtNode;

        String name = conflocation.getName();
        dmtNode = new DefaultMutableTreeNode("Conflocation" + VALUE_DIVIDER + name);

        String number = conflocation.getNumber();
        if ((number != null) && !number.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("number" + VALUE_DIVIDER + number));
        }

        String inverted = conflocation.getInverted();
        if ((inverted != null) && !inverted.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("inverted" + VALUE_DIVIDER + inverted));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createConfsettingNode(Confsetting confsetting) {
        DefaultMutableTreeNode dmtNode;

        String name = confsetting.getName();
        dmtNode = new DefaultMutableTreeNode("Confsetting" + VALUE_DIVIDER + name);

        String value = confsetting.getValue();
        if ((value != null) && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(VALUE + VALUE_DIVIDER + value));
        }

        String aDefault = confsetting.getDefault();
        if ((aDefault != null) && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + aDefault));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createPortNode(Port port) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Port" + VALUE_DIVIDER + port.getTag());

        List<Analog> analogs = port.getAnalog();
        for (Analog analog : analogs
        ) {
            dmtNode.add(createAnalogNode(analog));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createAnalogNode(Analog analog) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Analog");
        dmtNode.add(new DefaultMutableTreeNode("mask" + VALUE_DIVIDER + analog.getMask()));

        return dmtNode;
    }

    private static DefaultMutableTreeNode createAdjusterNode(Adjuster adjuster) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Adjuster" + VALUE_DIVIDER + adjuster.getName());

        String aDefault = adjuster.getDefault();
        if ((aDefault != null) && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + aDefault));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDriverNode(Driver driver) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Driver");

        String status = driver.getStatus();
        if ((status != null) && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(STATUS + VALUE_DIVIDER + status));
        }

        String emulation = driver.getEmulation();
        if ((emulation != null) && !emulation.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("emulation" + VALUE_DIVIDER + emulation));
        }

        String color = driver.getColor();
        if ((color != null) && !color.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("color" + VALUE_DIVIDER + color));
        }

        String sound = driver.getSound();
        if ((sound != null) && !sound.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(SOUND_CHANNELS + VALUE_DIVIDER + sound));
        }

        String graphic = driver.getGraphic();
        if ((graphic != null) && !graphic.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("graphic" + VALUE_DIVIDER + graphic));
        }

        String cocktail = driver.getCocktail();
        if ((cocktail != null) && !cocktail.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("cocktail" + VALUE_DIVIDER + cocktail));
        }

        String protection = driver.getProtection();
        if ((protection != null) && !protection.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("protection" + VALUE_DIVIDER + protection));
        }

        String savestate = driver.getSavestate();
        if ((savestate != null) && !savestate.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hiscore" + VALUE_DIVIDER + savestate));
        }

        String colordeep = driver.getColordeep();
        if ((colordeep != null) && !colordeep.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("colordeep" + VALUE_DIVIDER + colordeep));
        }

        String hiscore = driver.getSavestate();
        if ((hiscore != null) && !hiscore.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hiscore" + VALUE_DIVIDER + hiscore));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createFeatureNode(Feature feature) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Feature" + VALUE_DIVIDER + feature.getType());

        String status = feature.getStatus();
        if ((status != null) && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(STATUS + VALUE_DIVIDER + status));
        }

        String overall = feature.getOverall();
        if ((overall != null) && !overall.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("overall" + VALUE_DIVIDER + overall));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createDeviceNode(Device device) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode(Machine.DEVICE + VALUE_DIVIDER + device.getType());

        String tag = device.getTag();
        if ((tag != null) && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + VALUE_DIVIDER + tag));
        }

        String mandatory = device.getMandatory();
        if ((mandatory != null) && !mandatory.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mandatory" + VALUE_DIVIDER + mandatory));
        }

        String anInterface = device.getInterface();
        if ((anInterface != null) && !anInterface.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("Interface" + VALUE_DIVIDER + anInterface));
        }

        List<Extension> extensions = device.getExtension();
        for (Extension extension : extensions) {
            dmtNode.add(createExtensionNode(extension));
        }

        // MAME as of 191 reverted to only 1 instance - NOTE left this in case there is any issue with older sets.
    /*    List<Instance> instances = device.getInstance();
          for (Instance instance : instances) {
            dmtNode.add(createInstanceNode(instance));
        }
*/
        Instance instance = device.getInstance();
        if (instance != null) {
            dmtNode.add(createInstanceNode(instance));
        }
        return dmtNode;
    }

    private static DefaultMutableTreeNode createExtensionNode(Extension extension) {
        return new DefaultMutableTreeNode("extension" + VALUE_DIVIDER + extension.getName());
    }

    private static DefaultMutableTreeNode createInstanceNode(Instance instance) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("instance" + VALUE_DIVIDER + instance.getName());

        String briefname = instance.getBriefname();
        if ((briefname != null) && !briefname.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("briefname" + VALUE_DIVIDER + briefname));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createSlotNode(Slot slot) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("slot" + VALUE_DIVIDER + slot.getName());

        List<Slotoption> slotoptions = slot.getSlotoption();
        for (Slotoption slotoption : slotoptions) {
            dmtNode.add(createSlotoptionNode(slotoption));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createSlotoptionNode(Slotoption slotoption) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("slotoption" + VALUE_DIVIDER + slotoption.getName());

        String devname = slotoption.getDevname();
        if ((devname != null) && !devname.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("devname" + VALUE_DIVIDER + devname));
        }

        String aDefault = slotoption.getDefault();
        if ((aDefault != null) && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + aDefault));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createSoftwarelistNode(Softwarelist softwarelist) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("softwarelist" + VALUE_DIVIDER + softwarelist.getName());

        String filter = softwarelist.getFilter();
        if ((filter != null) && !filter.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("filter" + VALUE_DIVIDER + filter));
        }

        String status = softwarelist.getStatus();
        if ((status != null) && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(STATUS + VALUE_DIVIDER + status));
        }

        return dmtNode;
    }

    private static DefaultMutableTreeNode createRamoptionNode(Ramoption ramoption) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("ramoption");

        String value = ramoption.getValue();
        if ((value != null) && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(VALUE + VALUE_DIVIDER + value));
        }

        String aDefault = ramoption.getDefault();
        if ((aDefault != null) && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode(DEFAULT + VALUE_DIVIDER + aDefault));
        }

        return dmtNode;
    }

}
