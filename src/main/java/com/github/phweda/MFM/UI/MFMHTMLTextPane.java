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
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: Phweda
 * Date: 5/12/2015
 * Time: 10:08 PM
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
class MFMHTMLTextPane extends JTextPane {

    MFMHTMLTextPane() {
        this.setContentType("text/html");
        this.addHyperlinkListener(new UrlHyperlinkListener());
    }

    private class UrlHyperlinkListener implements HyperlinkListener {
        @Override
        public void hyperlinkUpdate(final HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                } catch (final IOException | URISyntaxException e) {
                    throw new RuntimeException("Can't open URL", e);
                }
            }
        }
    }
}
