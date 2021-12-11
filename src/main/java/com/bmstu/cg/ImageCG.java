package com.bmstu.cg;

import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCG {
    private final int widthImage;
    private final int heightImage;
    private final byte[] mComponents;
    public float specular, refl, refr, opacity;

    public ImageCG(int width, int height) {
        widthImage = width;
        heightImage = height;
        mComponents = new byte[widthImage * heightImage * 4];
        refl = 0;
        refr = 0;
        opacity = 0;
        specular = 0;
    }

    public ImageCG(File fileName, float specular_v, float reflv, float refrv, float opacityv) throws IOException {
        int width = 0;
        int height = 0;
        byte[] components = null;

        BufferedImage image = ImageIO.read(fileName);

        width = image.getWidth();
        height = image.getHeight();

        int[] imgPixels = new int[width * height];
        image.getRGB(0, 0, width, height, imgPixels, 0, width);
        components = new byte[width * height * 4];

        for (int i = 0; i < width * height; i++) {
            int pixel = imgPixels[i];

            components[i * 4] = (byte) ((pixel >> 24) & 0xFF); // A
            components[i * 4 + 1] = (byte) ((pixel) & 0xFF); // B
            components[i * 4 + 2] = (byte) ((pixel >> 8) & 0xFF); // G
            components[i * 4 + 3] = (byte) ((pixel >> 16) & 0xFF); // R
        }

        widthImage = width;
        heightImage = height;
        mComponents = components;
        refl = reflv;
        refr = refrv;
        specular = specular_v;
        opacity = opacityv;
    }

    public int getWidth() {
        return widthImage;
    }

    public int getHeight() {
        return heightImage;
    }

    public byte getComponent(int index) {
        if (index >= 0 && index < mComponents.length)
            return mComponents[index];
        else
            return mComponents[0];
    }

    public void clear(byte shade) {
        Arrays.fill(mComponents, shade);
    }

    // when renders
    public void drawPixel(int x, int y, byte a, byte b, byte g, byte r) {
        int index = (x + y * widthImage) * 4;
        mComponents[index] = a;
        mComponents[index + 1] = b;
        mComponents[index + 2] = g;
        mComponents[index + 3] = r;
    }

    /// when run without render
    public void drawPixelLight(int x, int y, byte a, byte b, byte g, byte r, float lightAmt) {
        int index = (x + y * widthImage) * 4;
        mComponents[index] = (byte) ((a & 0xFF) * lightAmt);
        mComponents[index + 1] = (byte) ((b & 0xFF) * lightAmt);
        mComponents[index + 2] = (byte) ((g & 0xFF) * lightAmt);
        mComponents[index + 3] = (byte) ((r & 0xFF) * lightAmt);
    }

    public void copyPixel(int destX, int destY, int srcX, int srcY, ImageCG src, float lightAmt) {
        int destIndex = (destX + destY * widthImage) * 4;
        int srcIndex = (srcX + srcY * src.getWidth()) * 4;

        mComponents[destIndex] = (byte) ((src.getComponent(srcIndex) & 0xFF) * lightAmt);
        mComponents[destIndex + 1] = (byte) ((src.getComponent(srcIndex + 1) & 0xFF) * lightAmt);
        mComponents[destIndex + 2] = (byte) ((src.getComponent(srcIndex + 2) & 0xFF) * lightAmt);
        mComponents[destIndex + 3] = (byte) ((src.getComponent(srcIndex + 3) & 0xFF) * lightAmt);
    }

    public float[] getPixelColor(int srcX, int srcY) {

        int srcIndex = (srcX + srcY * this.getWidth()) * 4;
        float r = (this.getComponent(srcIndex + 3) & 0xFF) / 255.f;
        float g = (this.getComponent(srcIndex + 2) & 0xFF) / 255.f;
        float b = (this.getComponent(srcIndex + 1) & 0xFF) / 255.f;
        return new float[]{r, g, b};
    }

    public void copyToByteArray(byte[] dest) {
        for (int i = 0; i < widthImage * heightImage; i++) {
            dest[i * 3] = mComponents[i * 4 + 1];
            dest[i * 3 + 1] = mComponents[i * 4 + 2];
            dest[i * 3 + 2] = mComponents[i * 4 + 3];
        }
    }

    public byte[] getMComponents() {
        return mComponents;
    }
}
