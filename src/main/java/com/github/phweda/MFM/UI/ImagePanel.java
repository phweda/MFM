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

/**
 * NOT MY CODE FOUND ON THE INTERNET
 * http://stackoverflow.com/questions/695458/adding-an-image-to-a-panel-using-java-awt
 * Posted by Reverend Gonzo
 * <p/>
 * Minor modifications by phweda
 */

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class ImagePanel extends JComponent implements SwingConstants, Printable {
    private transient Image image;
    private transient Image defaultImage;
    private int verticalAlignment = CENTER;
    private int horizontalAlignment = CENTER;
    private transient Path imagePath;

    ImagePanel() {
    }

    ImagePanel(Image imageIn) {
        image = defaultImage = imageIn;
    }

    public Image image() {
        return image;
    }

    void image(Image image) {
        this.image = image;
        repaint();
    }

    void image(String path) {
        imagePath = Paths.get(path);
        image(new ImageIcon(path).getImage());
    }

    void image(File file) {
        imagePath = Paths.get(file.getAbsolutePath());
        image(new ImageIcon(file.getAbsolutePath()).getImage());
    }

    Path getImagePath() {
        return imagePath;
    }

    public void image(byte[] imageData) {
        image(imageData == null ? null : new ImageIcon(imageData).getImage());
    }

    void imageReset() {
        image = defaultImage;
        repaint();
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * enum: TOP    SwingConstants.TOP
     * CENTER SwingConstants.CENTER
     * BOTTOM SwingConstants.BOTTOM
     * attribute: visualUpdate true
     * description: The alignment of the image along the Y axis.
     */
    public void setVerticalAlignment(int verticalAlignment) {
        if ((verticalAlignment == TOP) || (verticalAlignment == CENTER) || (verticalAlignment == BOTTOM))
            this.verticalAlignment = verticalAlignment;
        else
            throw new IllegalArgumentException("Invalid Vertical Alignment: " + verticalAlignment);
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * enum: LEFT    SwingConstants.LEFT
     * CENTER SwingConstants.CENTER
     * RIGHT SwingConstants.RIGHT
     * attribute: visualUpdate true
     * description: The alignment of the image along the X axis.
     */
    public void setHorizontalAlignment(int horizontalAlignment) {
        if ((horizontalAlignment == LEFT) || (horizontalAlignment == CENTER) || (horizontalAlignment == RIGHT))
            this.horizontalAlignment = horizontalAlignment;
        else
            throw new IllegalArgumentException(
                    "Invalid Horizontal Alignment: " + horizontalAlignment);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null)
            return;

        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;

        int srcW = image.getWidth(null);
        int srcH = image.getHeight(null);

        double scaleX = ((double) w) / srcW;
        double scaleY = ((double) h) / srcH;

        double scale = Math.min(scaleX, scaleY);

        int dstW = (int) (scale * srcW);
        int dstH = (int) (scale * srcH);

        int dx = x + (w - dstW) / 2;
        if (horizontalAlignment == LEFT)
            dx = x;
        else if (horizontalAlignment == RIGHT)
            dx = x + w - dstW;

        int dy = y + (h - dstH) / 2;
        if (verticalAlignment == TOP)
            dy = y;
        else if (verticalAlignment == BOTTOM)
            dy = y + h - dstH;

        g.drawImage(image, dx, dy, dx + dstW, dy + dstH, 0, 0, srcW, srcH, null);
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {

        if (pageIndex > 0 || image == null)
            return NO_SUCH_PAGE;

        double w = pageFormat.getImageableWidth();
        double h = pageFormat.getImageableHeight();

        int srcW = image.getWidth(null);
        int srcH = image.getHeight(null);

        double scaleX = w / srcW;
        double scaleY = h / srcH;

        double scale = Math.min(scaleX, scaleY);

        int dstW = (int) (scale * srcW);
        int dstH = (int) (scale * srcH);

        int dx = (int) ((w - dstW) / 2);

        int dy = (int) ((h - dstH) / 2);

        graphics.drawImage(image, dx, dy, dx + dstW, dy + dstH, 0, 0, srcW, srcH, null);

        return PAGE_EXISTS;
    }

}
