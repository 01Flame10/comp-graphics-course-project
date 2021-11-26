package com.bmstu.cg;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ColorCG {
    public float red, green, blue, specular, special, reflectionCoefficient, opacity;

    public ColorCG() {
        red = 0.5f;
        green = 0.5f;
        blue = 0.5f;
        specular = 0;
        special = 0;
        reflectionCoefficient = 0;
        opacity = 0;
    }


    public ColorCG(float r, float g, float b) {
        red = r;
        green = g;
        blue = b;
        specular = 0;
        special = 0;
        reflectionCoefficient = 0;
        opacity = 0;
    }

    public ColorCG(float r, float g, float b, float opacity) {
        red = r;
        green = g;
        blue = b;
        specular = 0;
        special = 0;
        reflectionCoefficient = 0;
        this.opacity = opacity;
    }

    public ColorCG(float r, float g, float b, float specular_v, float s, float refr, float opacity_v) {
        red = r;
        green = g;
        blue = b;
        specular = specular_v;
        special = s;
        reflectionCoefficient = refr;
        opacity = opacity_v;
    }

    public ColorCG colorMul(float scalar) {
        return new ColorCG(red * scalar, green * scalar, blue * scalar, specular, special, this.reflectionCoefficient, this.opacity);
    }

    public ColorCG colorAdd(ColorCG color) {
        return new ColorCG(red + color.getRed(), green + color.getGreen(), blue + color.getBlue(), specular, special, this.reflectionCoefficient, this.opacity);
    }

    public ColorCG colorMul(ColorCG color) {
        return new ColorCG(red * color.getRed(), green * color.getGreen(), blue * color.getBlue(), specular, special, this.reflectionCoefficient, this.opacity);
    }

    public ColorCG limit() {
        float alllight = red + green + blue;
        float excesslight = alllight - 3;
        if (excesslight > 0) {
            red = red + excesslight * (red / alllight);
            green = green + excesslight * (green / alllight);
            blue = blue + excesslight * (blue / alllight);
        }
        if (red > 1) {
            red = 1;
        }
        if (green > 1) {
            green = 1;
        }
        if (blue > 1) {
            blue = 1;
        }
        if (red < 0) {
            red = 0;
        }
        if (green < 0) {
            green = 0;
        }
        if (blue < 0) {
            blue = 0;
        }

        return new ColorCG(red, green, blue, specular, special, this.reflectionCoefficient, this.opacity);
    }
}
