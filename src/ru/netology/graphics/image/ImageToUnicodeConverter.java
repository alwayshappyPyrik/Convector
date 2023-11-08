package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;


public class ImageToUnicodeConverter implements TextGraphicsConverter {
    protected ColorShemaToUnicodeConverter schema = new ColorShemaToUnicodeConverter();
    private int width;
    private int height;
    private double maxRatio;
    private int[] arrayPixel = new int[3];

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        int width = img.getWidth();
        int height = img.getHeight();
        double ratio;
        //Проверка соотношения сторон
        if (width > height) {
            ratio = (double) width / (double) height;
            if (ratio > maxRatio) {
                throw new BadImageSizeException(ratio, maxRatio);
            }
        } else if (width < height) {
            ratio = (double) height / (double) width;
            if (ratio > maxRatio) {
                throw new BadImageSizeException(ratio, maxRatio);
            }
        }
        //Проверка высоты или/u ширины
        int newWidth = 0;
        int newHeight = 0;
        if (width > this.width || height > this.height) {
            if (width == height) {
                int coefficientWeight = width / this.width;
                int coefficientHeight = height / this.height;
                newWidth = width / coefficientWeight;
                newHeight = height / coefficientHeight;
            } else if (width > height) {
                int coefficientWeight = width / this.width;
                newWidth = width / coefficientWeight;
                newHeight = height / coefficientWeight;
            } else if (width < height) {
                int coefficientHeight = height / this.height;
                newWidth = width / coefficientHeight;
                newHeight = height / coefficientHeight;
            }
        } else {
            newWidth = width;
            newHeight = height;
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        WritableRaster bwRaster = bwImg.getRaster();
        char[][] symbols = new char[bwRaster.getHeight()][bwRaster.getWidth()];
        for (int i = 0; i < symbols.length; i++) {
            for (int j = 0; j < symbols[i].length; j++) {
                int color = bwRaster.getPixel(j, i, arrayPixel)[0];
                char c = schema.convert(color);
                symbols[i][j] = c;
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < symbols.length; i++) {
            for (int j = 0; j < symbols[i].length; j++) {
                result.append((symbols[i][j])).append(symbols[i][j]);
            }
            result.append('\n');
        }
        String resultText = result.toString();
        return resultText;
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = (ColorShemaToUnicodeConverter) schema;
    }
}