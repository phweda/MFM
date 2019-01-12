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

package com.github.phweda.MFM.UI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 10/7/2017
 * Time: 10:45 AM
 */
@SuppressWarnings("WeakerAccess")
public class LinkEditorPane {

    private LinkEditorPane() { // Cover implicit public constructor per squid:S1118
    }

    public static JEditorPane getLinkPane(String displayText, String link) {
        JLabel label = new JLabel();
        Font font = label.getFont();

        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight:");
        style.append((font.isBold() ? "bold" : "normal"));
        style.append(";");
        style.append("font-size:");
        style.append((font.getSize() + 4));
        style.append("pt;");

        // Construct href
        String html = "<html><body style=\"" + style + "\">" +
                "<a href=\"" + link + "\">" + displayText + "</a></body></html>";
        JEditorPane ep = new JEditorPane("text/html", html);
        ep.addHyperlinkListener(e -> {
            Desktop desktop = Desktop.getDesktop();
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(link));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        ep.setEditable(false);
        ep.setBackground(label.getBackground());

        return ep;
    }
}
