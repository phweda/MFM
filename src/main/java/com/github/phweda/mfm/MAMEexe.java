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

package com.github.phweda.mfm;

import com.github.phweda.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 12/1/11
 * Time: 1:33 PM
 */
public class MAMEexe {
    public static final String LISTXML = "-listxml";
    public static final String LISTINFO = "-listinfo";

    static MAME_Output mameOutput = new MAME_Output();
    private static ArrayList<String> args = new ArrayList<>();
    private static Process process = null;
    private static String directory = "";

    private MAMEexe() { // To cover implicit publc constructor
    }

    public static void setBaseArgs(String mameexe) {
        // Clear previous
        args.clear();

        directory = mameexe.substring(0, mameexe.lastIndexOf(FileUtils.DIRECTORY_SEPARATOR));
        // Put in MAME exe
        args.add(mameexe);
    }

    private static void cleanArgs() {
        String exe = args.get(0);
        // Clear previous
        args.clear();
        args.add(exe);
    }

    private static Process run(Object output, boolean logging) {

        ProcessBuilder pb = new ProcessBuilder(args);
        if (output instanceof File && logging) {
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter((File) output, true)));
                pw.println("*************************************************");
                pw.println(pb.command().toString());
                // Must flush or close otherwise output will be blocked by the PB
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo((File) output));
            pb.redirectError(ProcessBuilder.Redirect.appendTo((File) output));
        }
        try {
            MFM.getLogger().addToList(pb.command().toString(), true);
            System.out.println(pb.command().toString());
            pb.directory(new File(directory));
            process = pb.start();

        } catch (IOException exc) {
            exc.printStackTrace();
        }
        cleanArgs();
        return process;
    }

    public static Process run(List<String> args, File output) {
        setArgs(args);
        return run(output, true);
    }

    /**
     * @param args    process arguments
     * @param output  file to pipe process output to
     * @param logging to allow for not extraneous MFM output
     * @return MAME process
     */
    public static Process run(List<String> args, File output, boolean logging) {
        setArgs(args);
        return run(output, logging);
    }

    // NOTE hack to ensure we do NOT redirect process stream
    public static Process run(List<String> args) {
        setArgs(args);
        return run((Object) "NOPE", false);
    }

    /**
     * Typically just the Machine name
     *
     * @param arg single argument to MAME
     * @return MAME process
     */
    public static Process run(String arg) {
        setArgs(arg);
        return run(null, true);
    }

    /**
     * This sucks need to figure out do all default to mameOutput????
     */
    public static Process run(String[] args) {
        setArgs(args);
        return run(MFM.getMameout(), true);
    }

    public static void createConfig() {
        run("-createconfig");
    }

    public static Process runListXML(String arg) {
        setArgs(LISTXML);
        return run(arg);
    }

    private static void setArgs(String[] args) {
        MAMEexe.args.addAll(Arrays.asList(args));
    }

    private static void setArgs(List<String> args) {
        MAMEexe.args.addAll(args);
    }

    /*
     * Used for single param Machine
     *
     */
    private static void setArgs(String machine) {
        args.add(machine);
    }

    public static String getMAMEexeVersion() {
        Process process = null;
        String output;
        try {
            process = MAMEexe.run("-help");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            java.util.Scanner s = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");  //   process.getInputStream());
            output = s.next();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        }
        return output;
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
                return;
            }
            args = argsIn;
            bis = new BufferedInputStream(inputStream);
            read();
        }

        private void read() throws MAME_Exception {
            int read = 0;

            for (byte aSeparator : separator) {
                baos.write(aSeparator);
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
        String mameerror = "";

        MAME_Exception() {
            super();
            mameerror = "Unknown MAME error";
        }

        MAME_Exception(String error) {
            super();
            mameerror = error.contains("*") ? error.substring(error.lastIndexOf('*') + 1) : error;
            //System.out.println(error);
        }

        public String getError() {
            return mameerror;
        }
    }
}
