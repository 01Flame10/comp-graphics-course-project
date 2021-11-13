package com.bmstu.cg;

import java.util.ArrayList;
import java.util.List;

public class Torus extends PrimitiveObject {
    static float K_EPSILON = 0.1f;
    public Vector4 center;
    public Transform trans;
    private float sweptRadius;
    private float tubeRadius;
    private ColorCG color;
    private ImageCG texture;
    private boolean is_texture;

    public Torus() {
        center = new Vector4(0, 0, 0);
        sweptRadius = 2.0f;
        tubeRadius = 0.4f;
        color = new ColorCG(1f, 0.5f, 0.5f);
        is_texture = false;
        trans = new Transform(new Vector4(0, 0, 0, 1), new Vector4(1, 1, 1, 1));
    }

    public Torus(float sweptRadius_v, float tubeRadius_v, Transform trans_v, ColorCG colorValue) {
        sweptRadius = sweptRadius_v;
        tubeRadius = tubeRadius_v;
        trans = trans_v;
        color = colorValue;
        is_texture = false;
    }

    public Torus(float sweptRadius_v, float tubeRadius_v, Transform trans_v, ImageCG texture_value) {
        sweptRadius = sweptRadius_v;
        tubeRadius = tubeRadius_v;
        trans = trans_v;
        texture = texture_value;
        is_texture = true;
    }

    public static float getCoord(float i1, float i2, float w1,
                                 float w2, float p) {
        return ((p - i1) / (i2 - i1)) * (w2 - w1) + w1;
    }

    public Vector4 getSphereCenter() {
        return center;
    }

    public float getSphereRadius() {
        return tubeRadius;
    }

    @Override
    public ColorCG getColor(Vector4 pos) {
        if (is_texture) {

            Vector4 normal = getNormalAt(pos);

            float x1 = (float) (0.5 + Math.atan2(normal.getZ(), normal.getX()) / (Math.PI * 2.0f)) * texture.getWidth();//getCoord(0.0f, (float)Math.PI * 2.0f, 0.0f, texture.getWidth() - 1, theta);//theta * texture.getWidth();//
            float y1 = (float) (0.5 + Math.atan2(normal.getY(), Math.sqrt(normal.getX() * normal.getX() + normal.getZ() * normal.getZ()) - this.sweptRadius) / (Math.PI * 2.0f)) * texture.getHeight();//getCoord(0.0f, (float)Math.PI, 0.0f, texture.getHeight() - 1, phi);//phi * texture.getHeight();//

            int i1 = (int) x1, j1 = (int) y1;
            //System.out.println(x1 + " " + y1);
            if (i1 >= 0 && j1 >= 0 && i1 < texture.getWidth() && j1 < texture.getHeight()) {
                float[] colors = texture.get_pixel_color(i1, j1);
                ColorCG color_current = new ColorCG(colors[0], colors[1], colors[2]);
                return color_current;
            }
            return new ColorCG();

        }
        return this.color;

    }

    @Override
    public Vector4 getNormalAt(Vector4 point) {
        float paramSquared = this.sweptRadius * this.sweptRadius + this.tubeRadius * this.tubeRadius;

        float x = point.getX();
        float y = point.getY();
        float z = point.getZ();
        float sumSquared = x * x + y * y + z * z;

        Vector4 tmp = new Vector4(
                4.0f * x * (sumSquared - paramSquared),
                4.0f * y * (sumSquared - paramSquared + 2.0f * this.sweptRadius * this.sweptRadius),
                4.0f * z * (sumSquared - paramSquared));

        return tmp.normalized();
    }

    @Override
    public float findIntersection(Ray ray) {
        float ox = ray.origin.getX();
        float oy = ray.origin.getY();
        float oz = ray.origin.getZ();

        float dx = ray.direction.getX();
        float dy = ray.direction.getY();
        float dz = ray.direction.getZ();

        float sum_d_sqrd = dx * dx + dy * dy + dz * dz;
        float e = ox * ox + oy * oy + oz * oz -
                this.sweptRadius * this.sweptRadius - this.tubeRadius * this.tubeRadius;
        float f = ox * dx + oy * dy + oz * dz;
        float four_a_sqrd = 4.0f * this.sweptRadius * this.sweptRadius;

        List<Float> coeffs = new ArrayList();
        coeffs.add(e * e - four_a_sqrd * (this.tubeRadius * this.tubeRadius - oy * oy));
        coeffs.add(4.0f * f * e + 2.0f * four_a_sqrd * oy * dy);
        coeffs.add(2.0f * sum_d_sqrd * e + 4.0f * f * f + four_a_sqrd * dy * dy);
        coeffs.add(4.0f * sum_d_sqrd * f);
        coeffs.add(sum_d_sqrd * sum_d_sqrd);
        Solver solv = new Solver();


        List<Float> solution = Solver.solve4(coeffs);
        if (solution == null)
            return -1;

        float mint = Float.POSITIVE_INFINITY;
        for (int i = 0; i < solution.size(); i++) {
            float t = solution.get(i);
            if ((t > K_EPSILON) && (t < mint)) {
                mint = t;
            }
        }
        if (Float.isFinite(mint))
            return mint;

        return -1;

    }
}
