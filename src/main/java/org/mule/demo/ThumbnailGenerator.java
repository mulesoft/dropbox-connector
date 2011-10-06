package org.mule.demo;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ThumbnailGenerator
{

    private int width = 100;
    private int height = 100;

    public InputStream createThumbnail(InputStream is) throws Exception
    {
        Image image = javax.imageio.ImageIO.read(is);

        double thumbRatio = (double) width / (double) height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio)
        {
            height = (int) (width / imageRatio);
        } else
        {
            width = (int) (height * imageRatio);
        }

        if (imageWidth < width && imageHeight < height)
        {
            width = imageWidth;
            height = imageHeight;
        } else if (imageWidth < width)
        {
            width = imageWidth;
        } else if (imageHeight < height)
        {
            height = imageHeight;
        }

        BufferedImage thumbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, width, height, null);

        final ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * 64);
        javax.imageio.ImageIO.write(thumbImage, "JPG", os);

        return new ByteArrayInputStream(os.toByteArray());
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
}
