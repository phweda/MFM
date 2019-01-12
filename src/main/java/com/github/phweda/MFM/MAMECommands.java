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

package com.github.phweda.MFM;

import com.github.phweda.MFM.mame.Machine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 9/11/14
 * Time: 2:02 PM
 */
public class MAMECommands {
    /**
     * Explicit hard coded strings for commands MFM uses internally
     */
    public static final String RECORD = "-record";
    public static final String PLAYBACK = "-playback";
    public static final String AVIWRITE = "-aviwrite";
    public static final String SNAPSIZE = "-snapsize";
    public static final String PLAYBACK_TO_AVI = PLAYBACK + AVIWRITE;

    //    private static final String snap1 = "-snapsize ";
    public static final String SNAP_2 = "x720";
    // private static final String SNAP_ARG_HORIZONTAL = "960x720";
    // private static final String SNAP_ARG_VERTICAL = "540x720";

    private MAMECommands() { // To cover implicit public constructor
    }


    /**
     * Record a Games input for later playback
     * <p/>
     * Example commandline  ">mame64 ballbros -record ballbros.inp"
     *
     * @param machineName Machine to play and record to inp
     * @return Arguments list
     */
    public static List<String> recordGame(String machineName) {
        ArrayList<String> args = new ArrayList<>(3);
        args.add(machineName);
        args.add(RECORD);
        args.add(machineName + ".inp");
        return args;
    }

    /**
     * Playback a previously recorded game
     * <p/>
     * Example commandline  ">mame64 –playback ballbros.inp"
     *
     * @param machineName Machine to playback from INP
     * @return Arguments list
     */
    public static List<String> playbackGame(String machineName) {
        ArrayList<String> args = new ArrayList<>(3);
        args.add(PLAYBACK);
        args.add(machineName + ".inp");
        args.add(machineName);
        return args;
    }

    /**
     * Create an AVI from a previously recorded game
     * <p/>
     * Example commandline  ">mame64 -pb ballbros.inp -aviwrite ballbros.avi ballbros"
     * <p/>
     *
     * @param machineName Machine to run INP and record to AVI
     * @return Arguments list
     */
    public static List<String> createAVIfromPlayback(String machineName) {

        ArrayList<String> args = new ArrayList<>(5);
        args.add(PLAYBACK);
        args.add(machineName + ".inp");
        args.add(AVIWRITE);
        args.add(machineName + ".avi");
        args.add(machineName);
        return args;
    }


    /**
     * Play a game and write to AVI
     * <p/>
     * Example commandline  ">mame64 005 -aviwrite 005.avi"
     *
     * @param machineName Machine to run and record to AVI
     * @return Arguments list
     */
    public static List<String> playGametoAVI(String machineName) {
        ArrayList<String> args = new ArrayList<>(3);
        args.add(machineName);
        args.add(AVIWRITE);
        args.add(machineName + ".avi");
        return args;
    }

    // fixme should return Dimension
    public static int scale(Machine machine) {
        AtomicInteger width = new AtomicInteger(Integer.parseInt(machine.getWidth()));
        AtomicInteger height = new AtomicInteger(Integer.parseInt(machine.getHeight()));
        // Swap if vertical
        if (machine.getIsVertical().equalsIgnoreCase(Machine.YES)) {
            width.set(height.getAndSet(width.intValue()));
        }

        float scale = (float) height.intValue() / 720;
        float newHeight = height.intValue() / scale;
        float newWidth = width.intValue() / scale;
        height.set((int) newHeight);
        width.set((int) newWidth);

        return width.intValue();
    }

    public static String aspectRatio(Machine machine) {
        return machine.getIsVertical().equalsIgnoreCase("Vertical") ? "3:4" : "4:3";
    }
}
