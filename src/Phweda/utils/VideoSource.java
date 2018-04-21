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
 * User: Phweda
 * Date: 3/31/2015
 * Time: 8:29 PM
 */

public class VideoSource implements ControllerListener {
    public static final int NOT_READY = 1;
    public static final int READY = 2;
    public static final int ERROR = 3;
    Player _player;
    String _videoFilename;
    FramePositioningControl _framePositioningControl;
    FrameGrabbingControl _frameGrabbingControl;
    private int _state;

    public VideoSource(String videoFilename) {
        _videoFilename = videoFilename;
        _state = NOT_READY;
    }

    /*
     * Create Player object and start realizing it
     */
    public void initialize() {
        try {
            _player = Manager.createPlayer(new URL(_videoFilename));
            _player.addControllerListener(this);
            // realize call will launch a chain of events,
            // see controllerUpdate()
            _player.realize();
        } catch (Exception e) {
            System.out.println("Could not create VideoSource!");
            e.printStackTrace();
            setState(ERROR);
            return;
        }
    }

    /*
     * Returns the current state
     */
    public int getState() {
        return _state;
    }

    // for setting the state internally
    private void setState(int nextState) {
        _state = nextState;
    }

    /*
     * Returns the number of frames for current video if
     * the VideoSource is ready, in any other case returns -1.
     */
    public int getFrameCount() {
        if (getState() != READY) {
            return -1;
        }
        return _framePositioningControl.
                mapTimeToFrame(_player.getDuration());
    }

    /*
     * Returns the video frame from given index as BufferedImage.
     * If VideoSource is not ready or index is out of bounds,
     *  returns null.
     */
    public BufferedImage getFrame(int index) {
        if (getState() != READY || index < 0 || index > getFrameCount()) {
            return null;
        }
        _framePositioningControl.seek(index);
        Buffer buffer = _frameGrabbingControl.grabFrame();
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
            _player.prefetch();
        } else if (event instanceof PrefetchCompleteEvent) {
            // get controls
            _framePositioningControl = (FramePositioningControl) _player.
                    getControl("javax.media.control.FramePositioningControl");
            if (_framePositioningControl == null) {
                System.out.println("Error: FramePositioningControl!");
                setState(ERROR);
                return;
            }
            _frameGrabbingControl = (FrameGrabbingControl) _player.
                    getControl("javax.media.control.FrameGrabbingControl");
            if (_frameGrabbingControl == null) {
                System.out.println("Error: FrameGrabbingControl!");
                setState(ERROR);
                return;
            }
            setState(READY);
        }
    }

    public ArrayList<BufferedImage> getFrames() {
        if (getState() == ERROR) {
            return null;
        }

        int counter = 1;
        while (getState() == NOT_READY) {
            //todo figure out if we need to break this? Or a timeout?
            //NOTE HACK!!!!!!!!
            counter++;
            if (counter % 10000000 == 1) {
                System.out.println(System.currentTimeMillis() + " WAITING " + counter);
            }
            if (counter > 1400000000) {
                return null;
            }
        }
        int count = getFrameCount();
        // NOTE extraneous but just in case
        if (count < 0) {
            return null;
        }
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>(count);
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
