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

import Phweda.MFM.MFM;
import Phweda.MFM.MachineControllers;
import Phweda.utils.FileUtils;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 1/6/12
 * Time: 11:15 PM
 */
@SuppressWarnings("WeakerAccess")
public class MFMUI_Resources {

    static final String MFM_ICON_PNG = "MFM_Icon.png";
    // Help files
    static final String MFM_HTML = "MFM.html";
    static final String MFM_COPYRIGHT_HTML = "MFM Copyright.html";
    static final String GNU_GPL_V3 = "GNU GPL V3.html";
    // Icons
    static final String A_PNG = "A.png";
    static final String ARROW_PNG = "Arrow.png";
    static final String C_PNG = "C.png";
    static final String EX_PNG = "EX.png";
    static final String MFM_IMAGE_PNG = "MFM_Image.png";
    static final String MINUS_PNG = "Minus.png";
    static final String I_PNG = "i.png";
    static final String L_PNG = "L.png";
    static final String P_PNG = "P.png";
    static final String S_PNG = "S.png";
    static final String UPARROW_PNG = "UpArrow.png";
    static final String CHECKMARK_PNG = "CheckMark.png";
    static final String VDUB = "vdub_32.png";
    static final String MAME_LOGO = "mame-logo.png";
    static final String MAME_LOGO_SMALL = "mame-logo-SM.png";
    static final String CIRCULAR_ARROW = "circular_arrow.png";
    private static final String BUTTON_CONTROL_IMG = "Button_control.png";
    private static final String DBL_JOYSTICK_CONTROL_IMG = "2Joystick_control.png";
    private static final String DIAL_CONTROL_IMG = "Dial_control.png";
    private static final String GAMBLING_CONTROL_IMG = "Gambling_control.png";
    private static final String HANAFUDA_CONTROL_IMG = "Hanafuda_control.png";
    private static final String KEYBOARD_CONTROL_IMG = "Keyboard_control.png";
    private static final String KEYPAD_CONTROL_IMG = "Keypad_control.png";
    private static final String JOYSTICK_CONTROL_IMG = "Joystick_control.png";
    private static final String LIGHTGUN_CONTROL_PNG = "Lightgun_control.png";
    private static final String MAHJONG_CONTROL_IMG = "Mahjong_control2.png";
    private static final String MOUSE_CONTROL_IMG = "Mouse_control.png";
    private static final String PADDLE_CONTROL_IMG = "Paddle_control.png";
    private static final String PEDAL_CONTROL_IMG = "Pedal_control.png";
    private static final String POSITIONAL_CONTROL_IMG = "Positional_control.png";
    private static final String STICK_CONTROL_IMG = "Stick_control.png";
    private static final String TRACKBALL_CONTROL_IMG = "Trackball_control.png";
    private static final String RESOURCES = "Resources";
    private static final String FLAGS = "flags";
    private static final String PNG_SUFFIX = ".png";

    private static MFMUI_Resources ourInstance;
    private static final String[] resourceNames = {
            MFM_HTML, MFM_COPYRIGHT_HTML, GNU_GPL_V3,
            A_PNG, ARROW_PNG, C_PNG, CHECKMARK_PNG, CIRCULAR_ARROW, EX_PNG, MFM_ICON_PNG, MFM_IMAGE_PNG, MINUS_PNG,
            I_PNG, L_PNG, P_PNG, S_PNG,
            UPARROW_PNG, VDUB, MAME_LOGO, MAME_LOGO_SMALL,
            BUTTON_CONTROL_IMG, DBL_JOYSTICK_CONTROL_IMG, DIAL_CONTROL_IMG, GAMBLING_CONTROL_IMG, HANAFUDA_CONTROL_IMG,
            KEYBOARD_CONTROL_IMG, KEYPAD_CONTROL_IMG, MAHJONG_CONTROL_IMG, MOUSE_CONTROL_IMG,
            JOYSTICK_CONTROL_IMG, LIGHTGUN_CONTROL_PNG, PADDLE_CONTROL_IMG, PEDAL_CONTROL_IMG, POSITIONAL_CONTROL_IMG,
            STICK_CONTROL_IMG, TRACKBALL_CONTROL_IMG
    };
    private static final HashMap<String, URL> resourceURLs = new HashMap<>(resourceNames.length + 10);

    private MFMUI_Resources() {
        loadResources();
    }

    public static MFMUI_Resources getInstance() {
        if (ourInstance == null) {
            ourInstance = new MFMUI_Resources();
        }
        return ourInstance;
    }

    private void loadResources() {
        URL testURL = this.getClass().getResource("");
        MFM.getLogger().addToList("Resources : \n" + testURL, true);
        for (String name : resourceNames) {
            // NOTE works multisystem with slash it is in .jar that way
            URL url = this.getClass().getResource(RESOURCES + FileUtils.SLASH + name);
            if (url != null) {
                if (MFM.isSystemDebug()) {
                    MFM.getLogger().addToList(url.toString(), true);
                }
                resourceURLs.put(name, url);
            } else {
                MFM.getLogger().addToList("Failed to load resource : " + name, true);
            }
        }
    }

    HashMap<String, URL> resourceURLs() {
        return resourceURLs;
    }

    ImageIcon getImageIcon(String name) {
        return new ImageIcon(resourceURLs.get(name));
    }

    @SuppressWarnings("SameParameterValue")
    ImageIcon getFlagImageIcon(String countryCode) {
        URL url = this.getClass().getResource(RESOURCES + FileUtils.SLASH +
                FLAGS + FileUtils.SLASH + countryCode + PNG_SUFFIX);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
    }

    String getLabelLocation(String label) {
        String location = "";
        switch (label) {
            case MachineControllers.ONLY_BUTTONS:
                location = resourceURLs.get(BUTTON_CONTROL_IMG).toString();
                break;
            case MachineControllers.DIAL:
                location = resourceURLs.get(DIAL_CONTROL_IMG).toString();
                break;
            case MachineControllers.DOUBLEJOY:
                location = resourceURLs.get(DBL_JOYSTICK_CONTROL_IMG).toString();
                break;
            case MachineControllers.GAMBLING:
                location = resourceURLs.get(GAMBLING_CONTROL_IMG).toString();
                break;
            case MachineControllers.HANAFUDA:
                location = resourceURLs.get(HANAFUDA_CONTROL_IMG).toString();
                break;
            case MachineControllers.JOY:
                location = resourceURLs.get(JOYSTICK_CONTROL_IMG).toString();
                break;
            case MachineControllers.KEYBOARD:
                location = resourceURLs.get(KEYBOARD_CONTROL_IMG).toString();
                break;
            case MachineControllers.KEYPAD:
                location = resourceURLs.get(KEYPAD_CONTROL_IMG).toString();
                break;
            case MachineControllers.LIGHTGUN:
                location = resourceURLs.get(LIGHTGUN_CONTROL_PNG).toString();
                break;
            case MachineControllers.MAHJONG:
                location = resourceURLs.get(MAHJONG_CONTROL_IMG).toString();
                break;
            case MachineControllers.MOUSE:
                location = resourceURLs.get(MOUSE_CONTROL_IMG).toString();
                break;
            case MachineControllers.PADDLE:
                location = resourceURLs.get(PADDLE_CONTROL_IMG).toString();
                break;
            case MachineControllers.PEDAL:
                location = resourceURLs.get(PEDAL_CONTROL_IMG).toString();
                break;
            case MachineControllers.POSITIONAL:
                location = resourceURLs.get(POSITIONAL_CONTROL_IMG).toString();
                break;
            case MachineControllers.STICK:
                location = resourceURLs.get(STICK_CONTROL_IMG).toString();
                break;
            case MachineControllers.TRACKBALL:
                location = resourceURLs.get(TRACKBALL_CONTROL_IMG).toString();
                break;
            default:
                break;

        }
        return location;
    }
}
