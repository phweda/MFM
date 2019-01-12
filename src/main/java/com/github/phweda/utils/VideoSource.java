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

package Phweda.utils;

import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: phweda
 * Date: 3/31/2015
 * Time: 8:29 PM
 */

public class VideoSource implements ControllerListener {
    private static final int NOT_READY = 1;
    private static final int READY = 2;
    private static final int ERROR = 3;
    private Player player;
    private String videoFilename;
    private FramePositioningControl framePositioningControl;
    private FrameGrabbingControl frameGrabbingControl;
    private int state;

    VideoSource(String videoFilename) {
        this.videoFilename = videoFilename;
        state = NOT_READY;
    }

    /*
     * Create Player object and start realizing it
     */
    void initialize() {
        try {
            player = Manager.createPlayer(new URL(videoFilename));
            player.addControllerListener(this);
            // realize call will launch a chain of events,
            // see controllerUpdate()
            player.realize();
        } catch (Exception e) {
            System.out.println("Could not create VideoSource!");
            e.printStackTrace();
            setState(ERROR);
        }
    }

    /*
     * Returns the current state
     */
    private int getState() {
        return state;
    }

    // for setting the state internally
    private void setState(int nextState) {
        state = nextState;
    }

    /*
     * Returns the number of frames for current video if
     * the VideoSource is ready, in any other case returns -1.
     */
    private int getFrameCount() {
        if (getState() != READY) {
            return -1;
        }
        return framePositioningControl.
                mapTimeToFrame(player.getDuration());
    }

    /*
     * Returns the video frame from given index as BufferedImage.
     * If VideoSource is not ready or index is out of bounds,
     *  returns null.
     */
    private BufferedImage getFrame(int index) {
        if (getState() != READY || index < 0 || index > getFrameCount()) {
            return null;
        }
        framePositioningControl.seek(index);
        Buffer buffer = frameGrabbingControl.grabFrame();
        Image img = new BufferToImage((VideoFormat) buffer.
                getFormat()).createImage(buffer);
        // image creation may also fail!
        if (img != null) {
            BufferedImage bi = new BufferedImage(img.getWidth(null),
                    img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.drawImage(img, 0, 0, null);
            return bi;
        }
        return null;
    }

    // callback for ControllerListener
    public void controllerUpdate(ControllerEvent event) {
        if (event instanceof RealizeCompleteEvent) {
            player.prefetch();
        } else if (event instanceof PrefetchCompleteEvent) {
            // get controls
            framePositioningControl = (FramePositioningControl) player.
                    getControl("javax.media.control.FramePositioningControl");
            if (framePositioningControl == null) {
                System.out.println("Error: FramePositioningControl!");
                setState(ERROR);
                return;
            }
            frameGrabbingControl = (FrameGrabbingControl) player.
                    getControl("javax.media.control.FrameGrabbingControl");
            if (frameGrabbingControl == null) {
                System.out.println("Error: FrameGrabbingControl!");
                setState(ERROR);
                return;
            }
            setState(READY);
        }
    }

    ArrayList<BufferedImage> getFrames() {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        if (getState() == ERROR) {
            return frames;
        }

        int counter = 1;
        while (getState() == NOT_READY) {
            counter++;
            if (counter % 10000000 == 1) {
                System.out.println(System.currentTimeMillis() + " WAITING " + counter);
            }
            if (counter > 1400000000) {
                return frames;
            }
        }
        int count = getFrameCount();
        // NOTE extraneous but just in case
        if (count < 0) {
            return frames;
        }
        frames = new ArrayList<>(count);
        int index = 0;
        do {
            count--; //CORRECT? 0 based should be
            BufferedImage bi = getFrame(index);
            if (bi != null) {
                frames.add(bi);
            }
            index++;
        } while (count > 0);

        return frames;
    }

}
