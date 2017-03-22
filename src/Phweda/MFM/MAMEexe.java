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

package Phweda.MFM;

import Phweda.utils.FileUtils;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/1/11
 * Time: 1:33 PM
 */
public class MAMEexe {
    static MAME_Output MAMEout = new MAME_Output();
    private static ArrayList<String> args = new ArrayList<String>();
    private static Process process = null;
    private static String directory = "";

    public static void setBaseArgs(String MAMEexe) {
        // Clear previous
        args.clear();

        directory = MAMEexe.substring(0, MAMEexe.lastIndexOf(FileUtils.DIRECTORY_SEPARATOR));
        // Put in MAME exe
        args.add(MAMEexe);
    }

    private static void cleanArgs() {
        String exe = args.get(0);
        // Clear previous
        args.clear();
        args.add(exe);
    }

    private static Process run(Object output, boolean logging) throws MAME_Exception {

        ProcessBuilder pb = new ProcessBuilder(args);
        if (output != null)
            if (output instanceof File) {
                if (logging) {
                    try {
                        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter((File) output, true)));
                        pw.println("*************************************************");
                        pw.println(pb.command().toString());
                        // Must flush or close otherwise output will be blocked by the PB
                        pw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                pb.redirectOutput(ProcessBuilder.Redirect.appendTo((File) output));
                pb.redirectError(ProcessBuilder.Redirect.appendTo((File) output));
            }
        try {
            MFM.logger.addToList(pb.command().toString(), true);
            System.out.println(pb.command().toString());
            pb.directory(new File(directory));
            process = pb.start();

            if (output == null) {
                // TODO what about capturing Standard outputstream??
                //    MAMEout.setInput(process.getErrorStream(), pb.command().toString());
            }

        } catch (IOException exc) {
            exc.printStackTrace();
        }
        cleanArgs();
        return process;
    }

    public static Process run(ArrayList<String> args, File output) throws MAME_Exception {
        setArgs(args);
        return run(output, true);
    }

    /**
     *
     * @param args process arguments
     * @param output fiel to pipe process output to
     * @param logging to allow for not extraneous MFM output
     * @return
     * @throws MAME_Exception
     */
    public static Process run(ArrayList<String> args, File output, boolean logging) throws MAME_Exception {
        setArgs(args);
        return run(output, logging);
    }

    // NOTE hack to ensure we do NOT redirect process stream
    public static Process run(ArrayList<String> args) throws MAME_Exception {
        setArgs(args);
        return run((Object) "NOPE", false);
    }

    /**
     * Typically just the Machine name
     *
     * @param arg single argument to MAME
     * @return MAME process
     * @throws MAME_Exception MAME Exception
     */
    public static Process run(String arg) throws MAME_Exception {
        setArgs(arg);
        return run((File) null, true);
    }

    /**
     * This sucks need to figure out do all default to MAMEout????
     */
    public static Process run(String[] args) throws MAME_Exception {
        setArgs(args);
        return run(MFM.MAMEout, true);
    }

    public static void createConfig() throws MAME_Exception {
        run("-createconfig");
    }

    public static Process runListXML(String arg) throws MAME_Exception {
        setArgs("-listxml");
        return run(arg);
    }

    private static void setArgs(String[] args) {
        MAMEexe.args.addAll(Arrays.asList(args));
    }

    private static void setArgs(ArrayList<String> args) {
        MAMEexe.args.addAll(args);
    }

    /*
     * Used for single param Machine
     *
     */
    private static void setArgs(String machine) {
        args.add(machine);
    }

    /**
     * Failed attempt at sending key events to Mame exe
     * @param pb
     */
    private static void sendKeyboard(Process pb) {
        try {
            // Robot robot = new Robot();

/*
                // Simulate a mouse click
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
        */
/*

            // Simulate a key press
            robot.keyPress(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_RIGHT);
            robot.keyPress(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_RIGHT);

        } catch (AWTException e) {
            e.printStackTrace();
        }
*/

            OutputStream out = pb.getOutputStream();

            out.write(KeyEvent.VK_RIGHT);
            out.write(KeyEvent.VK_LEFT);
            out.write(KeyEvent.VK_RIGHT);
            out.write(KeyEvent.VK_LEFT);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class MAME_Output {
        String args;
        private BufferedInputStream bis;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        private byte[] separator = new byte[]{'\n', '\t', 0x2a, 0x2a, 0x2a, 0x2a};
        private boolean error = false;

        void setInput(InputStream inputStream, String argsIn) throws MAME_Exception {
            if (inputStream == null) {
                System.err.println("inputStream is NULL");
            }
            args = argsIn;
            bis = new BufferedInputStream(inputStream);
            read();
        }

        private void read() throws MAME_Exception {
            int read = 0;

            for (int i = 0; i < separator.length; i++) {
                baos.write(separator[i]);
            }

            try {
                while ((read = bis.read()) != -1) {
                    if (!error) {
                        // System.out.println("read is '" + read + "'");
                        error = true;
                        baos.write(args.getBytes(), 0, args.getBytes().length);
                        baos.write('\n');
                    }
                    baos.write(read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
/*            for (int i = 0; i < separator.length; i++) {
                baos.write(separator[i]);
            }*/
            // System.out.println(baos.toString());
            if (error) {
                error = false;
                throw new MAME_Exception(baos.toString());
            }
        }
    }

    public static class MAME_Exception extends Exception {
        String MAMEerror = "";

        MAME_Exception() {
            super();
            MAMEerror = "Unknown MAME error";
        }

        MAME_Exception(String error) {
            super();
            String output = error.contains("*") ? error.substring(error.lastIndexOf('*') + 1,
                    error.length()) : error;
            MAMEerror = output;
            //System.out.println(error);
        }

        public String getError() {
            return MAMEerror;
        }
    }
}
