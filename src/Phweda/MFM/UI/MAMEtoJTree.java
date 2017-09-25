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

import Phweda.MFM.MAMEInfo;
import Phweda.MFM.MAMEexe;
import Phweda.MFM.mame.*;

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
public class MAMEtoJTree extends JPanel {

    static final String COPY = "Copy";
    private static final int FRAME_WIDTH = MFMUI.screenSize.width / 5;
    private static final int FRAME_HEIGHT = MFMUI.screenSize.height - 100;
    private static JTree jTree;
    private static Mame root;
    private static MAMEtoJTree ourInstance;
    private static MouseListener ml = null;
    private final String valueDivider = " \u00bb ";
    private final String machineDivider = " \u00A8 ";

    private MAMEtoJTree() {
        if (root == null) {
            root = MAMEInfo.getMame();
        }
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
        // Dumps Mame data without any whitespace or tags - determine true data size
        // dataDump(top,"MAME_tree_data.txt");
    }

    public static MAMEtoJTree getInstance(boolean refresh) {
        if (ourInstance == null || refresh) {
            root = null; // ensure full refresh of Data
            ourInstance = new MAMEtoJTree();
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
            public void windowClosing(WindowEvent e) {
                exit();
            }
        };
        frame.addWindowListener(wndCloser);

        try {
            root = JAXB();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Exception",
                    JOptionPane.WARNING_MESSAGE);

            ex.printStackTrace();
            exit();
        }

        frame.getContentPane().add(new MAMEtoJTree(), BorderLayout.CENTER);
        frame.validate();
        frame.setVisible(true);
    }

    /**
     * This is simply to provide MAME input to test this class
     */
    private static Mame JAXB() throws JAXBException, MAMEexe.MAME_Exception {
        Mame mame = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Phweda.MFM.mame.Mame.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            // hard code path to mame exe
            MAMEexe.setBaseArgs("E:\\Test\\177\\mame64.exe");
            ArrayList<String> args = new ArrayList<String>(Arrays.asList("-listxml", "*"));
            Process process = MAMEexe.run(args);
            InputStream inputStream = process.getInputStream();
            mame = (Mame) jaxbUnmarshaller.unmarshal(inputStream);

            System.out.println("Machines" + mame.getMachine().size());
        } catch (JAXBException | MAMEexe.MAME_Exception e) {
            e.printStackTrace();
            throw e;
        }
        return mame;
    }

    JTree getMAMEjTree() {
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

    protected void copytoClipboard(String nodeValue) {
        nodeValue = nodeValue.contains(valueDivider) ?
                nodeValue.split(valueDivider)[1].trim() :
                nodeValue.split(machineDivider)[1].trim();

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
    private void dataDump(DefaultMutableTreeNode root, String path) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Enumeration<DefaultMutableTreeNode> en = root.preorderEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = en.nextElement();
            String nodeValue = node.getUserObject().toString();
            if (nodeValue.contains(valueDivider) || nodeValue.contains(machineDivider)) {
                nodeValue = nodeValue.contains(valueDivider) ?
                        nodeValue.split(valueDivider)[1].trim() :
                        nodeValue.split(machineDivider)[1].trim();
            }

            pw.print(nodeValue);
        }
    }

    /**
     * This takes Mame root to create full MAME XML JTree
     */
    private DefaultMutableTreeNode createTreeNode(Mame root) {
        DefaultMutableTreeNode dmtNode = null;
        dmtNode = new DefaultMutableTreeNode("Mame");

        String build = root.getBuild();
        if (build != null && !build.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("build" + valueDivider + build));
        }

        String mameconfig = root.getMameconfig();
        if (mameconfig != null && !mameconfig.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mameconfig" + valueDivider + mameconfig));
        }

        String debug = root.getDebug();
        if (debug != null && !debug.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("debug" + valueDivider + debug));
        }

        List<Machine> machines = new ArrayList<Machine>(root.getMachine());
        machines.sort(Comparator.comparing(Machine::getName));

        for (Machine machine : machines) {
            dmtNode.add(createMachineNode(machine));
        }
        return dmtNode;
    }

    /**
     * NOTE we do a lot of checking even for required Attrs. This is to try and cover previous XML versions
     * fixme those checks may be totally extraneous
     *
     * @param machine Mame machine to display
     * @return machine node
     */
    private DefaultMutableTreeNode createMachineNode(Machine machine) {
        DefaultMutableTreeNode dmtNode = null;

        // Name is always required
        String name = machine.getName();
        // dmtNode.add(new DefaultMutableTreeNode("name" + valueDivider + name));
        dmtNode = new DefaultMutableTreeNode("Machine" + machineDivider + name);

        String description = machine.getDescription();
        if (description != null && !description.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("description" + valueDivider + description));
        }

        String year = machine.getYear();
        if (year != null && !year.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("year" + valueDivider + year));
        }

        String manufacturer = machine.getManufacturer();
        if (manufacturer != null && !manufacturer.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("manufacturer" + valueDivider + manufacturer));
        }

        String sourcefile = machine.getSourcefile();
        if (sourcefile != null && !sourcefile.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sourcefile" + valueDivider + sourcefile));
        }

        String isbios = machine.getIsbios();
        if (isbios != null && !isbios.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("isbios" + valueDivider + isbios));
        }

        String isdevice = machine.getIsdevice();
        if (isdevice != null && !isdevice.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("isdevice" + valueDivider + isdevice));
        }

        String ismechanical = machine.getIsmechanical();
        if (ismechanical != null && !ismechanical.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ismechanical" + valueDivider + ismechanical));
        }

        String runnable = machine.getRunnable();
        if (runnable != null && !runnable.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("runnable" + valueDivider + runnable));
        }

        String cloneof = machine.getCloneof();
        if (cloneof != null && !cloneof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("cloneof" + valueDivider + cloneof));
        }

        String romof = machine.getRomof();
        if (romof != null && !romof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("romof" + valueDivider + romof));
        }

        String sampleof = machine.getSampleof();
        if (sampleof != null && !sampleof.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sampleof" + valueDivider + sampleof));
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
            dmtNode.add(new DefaultMutableTreeNode("sound channels" + valueDivider + sound.getChannels()));
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

    private DefaultMutableTreeNode createBiossetNode(Biosset bioset) {
        DefaultMutableTreeNode dmtNode = null;

        String name = bioset.getName();
        dmtNode = new DefaultMutableTreeNode("Biosset" + valueDivider + name);

        String description = bioset.getDescription();
        if (description != null && !description.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("description" + valueDivider + description));
        }

        String adefault = bioset.getDefault();
        if (adefault != null && !adefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + adefault));
        }
        return dmtNode;
    }

    private DefaultMutableTreeNode createRomNode(Rom rom) {
        DefaultMutableTreeNode dmtNode = null;

        String name = rom.getName();
        dmtNode = new DefaultMutableTreeNode("Rom" + valueDivider + name);

        String bios = rom.getBios();
        if (bios != null && !bios.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("bios" + valueDivider + bios));
        }

        String size = rom.getSize();
        if (size != null && !size.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("size" + valueDivider + size));
        }

        String crc = rom.getCrc();
        if (crc != null && !crc.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("crc" + valueDivider + crc));
        }

        String md5 = rom.getMd5();
        if (md5 != null && !md5.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("md5" + valueDivider + md5));
        }

        String sha1 = rom.getSha1();
        if (sha1 != null && !sha1.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sha1" + valueDivider + sha1));
        }

        String merge = rom.getMerge();
        if (merge != null && !merge.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("merge" + valueDivider + merge));
        }

        String region = rom.getRegion();
        if (region != null && !region.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("region" + valueDivider + region));
        }

        String offset = rom.getOffset();
        if (offset != null && !offset.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("offset" + valueDivider + offset));
        }

        String status = rom.getStatus();
        if (status != null && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("status" + valueDivider + status));
        }

        String optional = rom.getOptional();
        if (optional != null && !optional.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("optional" + valueDivider + optional));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDiskNode(Disk disk) {
        DefaultMutableTreeNode dmtNode = null;

        String name = disk.getName();
        dmtNode = new DefaultMutableTreeNode("Disk" + valueDivider + name);

        String md5 = disk.getMd5();
        if (md5 != null && !md5.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("md5" + valueDivider + md5));
        }

        String sha1 = disk.getSha1();
        if (sha1 != null && !sha1.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sha1" + valueDivider + sha1));
        }

        String merge = disk.getMerge();
        if (merge != null && !merge.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("merge" + valueDivider + merge));
        }

        String region = disk.getRegion();
        if (region != null && !region.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("region" + valueDivider + region));
        }

        String index = disk.getIndex();
        if (index != null && !index.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("index" + valueDivider + index));
        }

        String writable = disk.getWritable();
        if (writable != null && !writable.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("writable" + valueDivider + writable));
        }

        String status = disk.getStatus();
        if (status != null && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("status" + valueDivider + status));
        }

        String optional = disk.getOptional();
        if (optional != null && !optional.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("optional" + valueDivider + optional));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDeviceRefNode(DeviceRef deviceRef) {
        DefaultMutableTreeNode dmtNode = null;

        String name = deviceRef.getName();
        dmtNode = new DefaultMutableTreeNode("DeviceRef" + valueDivider + name);

        return dmtNode;
    }

    private DefaultMutableTreeNode createSampleNode(Sample sample) {
        DefaultMutableTreeNode dmtNode = null;

        String name = sample.getName();
        dmtNode = new DefaultMutableTreeNode("Sample" + valueDivider + name);

        return dmtNode;
    }

    private DefaultMutableTreeNode createChipNode(Chip chip) {
        DefaultMutableTreeNode dmtNode = null;

        String name = chip.getName();
        dmtNode = new DefaultMutableTreeNode("Chip" + valueDivider + name);

        String tag = chip.getTag();
        if (tag != null && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + valueDivider + tag));
        }

        String clock = chip.getClock();
        if (clock != null && !clock.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("clock" + valueDivider + clock));
        }
        String type = chip.getType();
        if (type != null && !type.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("type" + valueDivider + type));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDisplayNode(Display display) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Display" + valueDivider + display.getType());

        String tag = display.getTag();
        if (tag != null && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + valueDivider + tag));
        }

        String rotate = display.getRotate();
        if (rotate != null && !rotate.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("rotate" + valueDivider + rotate));
        }

        String orientation = display.getOrientation();
        if (orientation != null && !orientation.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("orientation" + valueDivider + orientation));
        }

        String flipx = display.getFlipx();
        if (flipx != null && !flipx.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("flipx" + valueDivider + flipx));
        }

        String width = display.getWidth();
        if (width != null && !width.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("width" + valueDivider + width));
        }

        String height = display.getHeight();
        if (height != null && !height.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("height" + valueDivider + height));
        }

        String refresh = display.getRefresh();
        if (refresh != null && !refresh.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("refresh" + valueDivider + refresh));
        }

        String pixclock = display.getPixclock();
        if (pixclock != null && !pixclock.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("pixclock" + valueDivider + pixclock));
        }

        String htotal = display.getHtotal();
        if (htotal != null && !htotal.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("htotal" + valueDivider + htotal));
        }

        String hbend = display.getHbend();
        if (hbend != null && !hbend.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hbend" + valueDivider + hbend));
        }

        String hbstart = display.getHbstart();
        if (hbstart != null && !hbstart.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("hbstart" + valueDivider + hbstart));
        }

        String vbend = display.getVbend();
        if (vbend != null && !vbend.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vbend" + valueDivider + vbend));
        }

        String vtotal = display.getVtotal();
        if (vtotal != null && !vtotal.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vtotal" + valueDivider + vtotal));
        }

        String vbstart = display.getVbstart();
        if (vbstart != null && !vbstart.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("vbstart" + valueDivider + vbstart));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createInputNode(Input input) {
        DefaultMutableTreeNode dmtNode = null;
        dmtNode = new DefaultMutableTreeNode("Input");

        String service = input.getService();
        if (service != null && !service.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("service" + valueDivider + service));
        }

        String tilt = input.getTilt();
        if (tilt != null && !tilt.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tilt" + valueDivider + tilt));
        }

        String players = input.getPlayers();
        if (players != null && !players.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("service" + valueDivider + players));
        }

        String buttons = input.getButtons();
        if (buttons != null && !buttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("buttons" + valueDivider + buttons));
        }

        String coins = input.getCoins();
        if (coins != null && !coins.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("coins" + valueDivider + coins));
        }


        List<Control> controls = input.getControl();
        for (Control control : controls) {
            dmtNode.add(createControlNode(control));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createControlNode(Control control) {
        DefaultMutableTreeNode dmtNode = null;

        String type = control.getType();
        dmtNode = new DefaultMutableTreeNode("Control" + valueDivider + type);

        String player = control.getPlayer();
        if (player != null && !player.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("player" + valueDivider + player));
        }

        String buttons = control.getButtons();
        if (buttons != null && !buttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("buttons" + valueDivider + buttons));
        }

        String reqbuttons = control.getReqbuttons();
        if (reqbuttons != null && !reqbuttons.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("reqbuttons" + valueDivider + reqbuttons));
        }

        String minimum = control.getMinimum();
        if (minimum != null && !minimum.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("minimum" + valueDivider + minimum));
        }

        String maximum = control.getMaximum();
        if (maximum != null && !maximum.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("maximum" + valueDivider + maximum));
        }

        String sensitivity = control.getSensitivity();
        if (sensitivity != null && !sensitivity.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sensitivity" + valueDivider + sensitivity));
        }

        String keydelta = control.getKeydelta();
        if (keydelta != null && !keydelta.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("keydelta" + valueDivider + keydelta));
        }

        String reverse = control.getReverse();
        if (reverse != null && !reverse.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("reverse" + valueDivider + reverse));
        }

        String ways = control.getWays();
        if (ways != null && !ways.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways" + valueDivider + ways));
        }

        String ways2 = control.getWays2();
        if (ways2 != null && !ways2.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways2" + valueDivider + ways2));
        }

        String ways3 = control.getWays3();
        if (ways3 != null && !ways3.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("ways3" + valueDivider + ways3));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDipswitchNode(Dipswitch dipswitch) {
        DefaultMutableTreeNode dmtNode = null;

        String name = dipswitch.getName();
        dmtNode = new DefaultMutableTreeNode("Dipswitch" + valueDivider + name);

        String mask = dipswitch.getMask();
        if (mask != null && !mask.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mask" + valueDivider + mask));
        }

        String tag = dipswitch.getTag();
        if (tag != null && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + valueDivider + tag));
        }

        List<Diplocation> diplocations = dipswitch.getDiplocation();
        for (Diplocation location : diplocations) {
            dmtNode.add(createDiplocationNode(location));
        }

        List<Dipvalue> dipvalues = dipswitch.getDipvalue();
        for (Dipvalue dipvalue : dipvalues) {
            dmtNode.add(createDipvalueNode(dipvalue));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDiplocationNode(Diplocation diplocation) {
        DefaultMutableTreeNode dmtNode = null;

        String name = diplocation.getName();
        dmtNode = new DefaultMutableTreeNode("Diplocation" + valueDivider + name);

        String number = diplocation.getNumber();
        if (number != null && !number.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("number" + valueDivider + number));
        }

        String inverted = diplocation.getInverted();
        if (inverted != null && !inverted.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("inverted" + valueDivider + inverted));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDipvalueNode(Dipvalue dipvalue) {
        DefaultMutableTreeNode dmtNode = null;

        String name = dipvalue.getName();
        dmtNode = new DefaultMutableTreeNode("Dipvalue" + valueDivider + name);

        String value = dipvalue.getValue();
        if (value != null && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("value" + valueDivider + value));
        }

        String aDefault = dipvalue.getDefault();
        if (aDefault != null && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + aDefault));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createConfigurationNode(Configuration configuration) {
        DefaultMutableTreeNode dmtNode = null;

        String name = configuration.getName();
        dmtNode = new DefaultMutableTreeNode("Configuration" + valueDivider + name);

        String mask = configuration.getMask();
        if (mask != null && !mask.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mask" + valueDivider + mask));
        }

        String tag = configuration.getTag();
        if (tag != null && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + valueDivider + tag));
        }

        List<Conflocation> conflocations = configuration.getConflocation();
        for (Conflocation conflocation : conflocations ) {
            dmtNode.add(createConflocationNode(conflocation));
        }

        List<Confsetting> confsettings = configuration.getConfsetting();
        for (Confsetting confsetting : confsettings ) {
            dmtNode.add(createConfsettingNode(confsetting));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createConflocationNode(Conflocation conflocation) {
        DefaultMutableTreeNode dmtNode = null;

        String name = conflocation.getName();
        dmtNode = new DefaultMutableTreeNode("Conflocation" + valueDivider + name);

        String number = conflocation.getNumber();
        if (number != null && !number.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("number" + valueDivider + number));
        }

        String inverted = conflocation.getInverted();
        if (inverted != null && !inverted.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("inverted" + valueDivider + inverted));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createConfsettingNode(Confsetting confsetting) {
        DefaultMutableTreeNode dmtNode = null;

        String name = confsetting.getName();
        dmtNode = new DefaultMutableTreeNode("Confsetting" + valueDivider + name);

        String value = confsetting.getValue();
        if (value != null && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("value" + valueDivider + value));
        }

        String aDefault = confsetting.getDefault();
        if (aDefault != null && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + aDefault));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createPortNode(Port port) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Port" + valueDivider + port.getTag());

        List<Analog> analogs = port.getAnalog();
        for (Analog analog : analogs
                ) {
            dmtNode.add(createAnalogNode(analog));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createAnalogNode(Analog analog) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Analog");
        dmtNode.add(new DefaultMutableTreeNode("mask" + valueDivider + analog.getMask()));

        return dmtNode;
    }

    private DefaultMutableTreeNode createAdjusterNode(Adjuster adjuster) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Adjuster" + valueDivider + adjuster.getName());

        String aDefault = adjuster.getDefault();
        if (aDefault != null && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + aDefault));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDriverNode(Driver driver) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Driver");

        String status = driver.getStatus();
        if (status != null && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("status" + valueDivider + status));
        }

        String emulation = driver.getEmulation();
        if (emulation != null && !emulation.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("emulation" + valueDivider + emulation));
        }

        String color = driver.getColor();
        if (color != null && !color.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("color" + valueDivider + color));
        }

        String sound = driver.getSound();
        if (sound != null && !sound.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("sound channels" + valueDivider + sound));
        }

        String graphic = driver.getGraphic();
        if (graphic != null && !graphic.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("graphic" + valueDivider + graphic));
        }

        String cocktail = driver.getCocktail();
        if (cocktail != null && !cocktail.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("cocktail" + valueDivider + cocktail));
        }

        String protection = driver.getProtection();
        if (protection != null && !protection.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("protection" + valueDivider + protection));
        }

        String savestate = driver.getSavestate();
        if (savestate != null && !savestate.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("savestate" + valueDivider + savestate));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createFeatureNode(Feature feature) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Feature" + valueDivider + feature.getType());

        String status = feature.getStatus();
        if (status != null && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("status" + valueDivider + status));
        }

        String overall = feature.getOverall();
        if (overall != null && !overall.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("overall" + valueDivider + overall));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createDeviceNode(Device device) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("Device" + valueDivider + device.getType());

        String tag = device.getTag();
        if (tag != null && !tag.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("tag" + valueDivider + tag));
        }

        String mandatory = device.getMandatory();
        if (mandatory != null && !mandatory.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("mandatory" + valueDivider + mandatory));
        }

        String anInterface = device.getInterface();
        if (anInterface != null && !anInterface.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("Interface" + valueDivider + anInterface));
        }

        List<Extension> extensions = device.getExtension();
        for (Extension extension : extensions) {
            dmtNode.add(createExtensionNode(extension));
        }

        List<Instance> instances = device.getInstance();
        for (Instance instance : instances) {
            dmtNode.add(createInstanceNode(instance));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createExtensionNode(Extension extension) {
        return new DefaultMutableTreeNode("extension" + valueDivider + extension.getName());
    }

    private DefaultMutableTreeNode createInstanceNode(Instance instance) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("instance" + valueDivider + instance.getName());

        String briefname = instance.getBriefname();
        if (briefname != null && !briefname.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("briefname" + valueDivider + briefname));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createSlotNode(Slot slot) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("slot" + valueDivider + slot.getName());

        List<Slotoption> slotoptions = slot.getSlotoption();
        for (Slotoption slotoption : slotoptions) {
            dmtNode.add(createSlotoptionNode(slotoption));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createSlotoptionNode(Slotoption slotoption) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("slotoption" + valueDivider + slotoption.getName());

        String devname = slotoption.getDevname();
        if (devname != null && !devname.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("devname" + valueDivider + devname));
        }

        String aDefault = slotoption.getDefault();
        if (aDefault != null && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + aDefault));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createSoftwarelistNode(Softwarelist softwarelist) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("softwarelist" + valueDivider + softwarelist.getName());

        String filter = softwarelist.getFilter();
        if (filter != null && !filter.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("filter" + valueDivider + filter));
        }

        String status = softwarelist.getStatus();
        if (status != null && !status.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("status" + valueDivider + status));
        }

        return dmtNode;
    }

    private DefaultMutableTreeNode createRamoptionNode(Ramoption ramoption) {
        DefaultMutableTreeNode dmtNode = new DefaultMutableTreeNode("ramoption");

        String value = ramoption.getValue();
        if (value != null && !value.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("value" + valueDivider + value));
        }

        String aDefault = ramoption.getDefault();
        if (aDefault != null && !aDefault.isEmpty()) {
            dmtNode.add(new DefaultMutableTreeNode("default" + valueDivider + aDefault));
        }

        return dmtNode;
    }

}
