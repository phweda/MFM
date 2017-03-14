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

import Phweda.MFM.mame.Machine;

import java.util.ArrayList;
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
    public static final String PLAYBACKtoAVI = PLAYBACK + AVIWRITE;

    //    private static final String snap1 = "-snapsize ";
    public static final String snap2 = "x720";
    private static final String snapArgHorizontal = "960x720";
    private static final String snapArgVertical = "540x720";

    /**
     * Record a Games input for later playback
     * <p/>
     * Example commandline  ">mame64 ballbros -record ballbros.inp"
     *
     * @param gameName
     * @return Arguments list
     */
    public static ArrayList<String> recordGame(String gameName) {
        ArrayList<String> args = new ArrayList<String>(3);
        args.add(gameName);
        args.add(RECORD);
        args.add(gameName + ".inp");
        return args;
    }

    /**
     * Playback a previously recorded game
     * <p/>
     * Example commandline  ">mame64 –playback ballbros.inp"
     *
     * @param gameName
     * @return Arguments list
     */
    public static ArrayList<String> playbackGame(String gameName) {
        ArrayList<String> args = new ArrayList<String>(3);
        args.add(PLAYBACK);
        args.add(gameName + ".inp");
        args.add(gameName);
        return args;
    }

    /**
     * Create an AVI from a previously recorded game
     * <p/>
     * Example commandline  ">mame64 -pb ballbros.inp -aviwrite ballbros.avi ballbros"
     * <p/>
     *
     * @param gameName
     * @return Arguments list
     */
    public static ArrayList<String> createAVIfromPlayback(String gameName) {
        Machine machine = MAMEInfo.getMachine(gameName);

        ArrayList<String> args = new ArrayList<String>(5);
        args.add(PLAYBACK);
        args.add(gameName + ".inp");
        args.add(AVIWRITE);
        args.add(gameName + ".avi");

        // NOTE NO SNAPIZE!!!! http://forum.pleasuredome.org.uk/index.php?showtopic=25093&page=6#entry224277
//        args.add(SNAPSIZE);
        //    args.add(scale(game) + snap2);
        //    args.add((game.isVertical() ? "960" : "540") + snap2);

        args.add(gameName);
        return args;
    }


    /**
     * Play a game and write to AVI
     * <p/>
     * Example commandline  ">mame64 005 -aviwrite 005.avi"
     *
     * @param gameName
     * @return Arguments list
     */
    public static ArrayList<String> playGametoAVI(String gameName) {
        ArrayList<String> args = new ArrayList<String>(3);
        args.add(gameName);
        args.add(AVIWRITE);
        args.add(gameName + ".avi");
        return args;
    }

    // fixme should return Dimension
    public static int scale(MFMMachine machine) {
        AtomicInteger width = new AtomicInteger(machine.getWidth());
        AtomicInteger height = new AtomicInteger(machine.getHeight());
        // Swap if vertical
        if (machine.isVertical()) {
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
