package com.bmstu.cg;


public class Sphere extends PrimitiveObject {
    public Vector4 center;
    Transform trans;
    private float radius;
    private ColorCG color;
    private ImageCG texture;
    private boolean is_texture;

    public Sphere() {
        center = new Vector4(0, 0, 0);
        radius = 1.0f;
        color = new ColorCG(0.5f, 0.5f, 0.5f);
        is_texture = false;
        trans = new Transform(new Vector4(0, 0, 0), new Vector4(1, 1, 1, 1));
    }

    public Sphere(Vector4 centerValue, float radiusValue, Transform tr, ColorCG colorValue) {
        center = centerValue;
        radius = radiusValue;
        color = colorValue;
        is_texture = false;
        trans = tr;
    }

    public Sphere(Vector4 centerValue, float radiusValue, Transform tr, ImageCG texture_value) {
        center = centerValue;
        radius = radiusValue;
        texture = texture_value;
        is_texture = true;
        trans = tr;
    }

    public static float getCoord(float i1, float i2, float w1,
                                 float w2, float p) {
        return ((p - i1) / (i2 - i1)) * (w2 - w1) + w1;
    }

    public Vector4 getSphereCenter() {
        return center;
    }

    public float getSphereRadius() {
        return radius;
    }

    @Override
    public ColorCG getColor(Vector4 pos) {
        if (is_texture) {
            Matrix transform = this.trans.getTransformation();
            Vector4 normal = pos.substitute(transform.multiply(this.center)).normalized();
            float x1 = (float) (0.5 + Math.atan2(normal.getZ(), normal.getX()) / (Math.PI * 2.0f)) * texture.getWidth();
            float y1 = (float) (0.5 - Math.asin(normal.getY()) / Math.PI) * texture.getWidth();

            int i1 = (int) x1, j1 = (int) y1;
            if (i1 >= 0 && j1 >= 0 && i1 < texture.getWidth() && j1 < texture.getHeight()) {
                float[] colors = texture.getPixelColor(i1, j1);
                ColorCG color_current = new ColorCG(colors[0], colors[1], colors[2], texture.specular, texture.refl, texture.refr, texture.opacity);
                return color_current;
            }
            return new ColorCG();

        }
        return this.color;

    }

    @Override
    public Vector4 getNormalAt(Vector4 point) {
        Matrix transform = this.trans.getTransformation();
        Vector4 normal_Vect = point.add(transform.multiply(this.center).negative()).normalized();
        return normal_Vect;
    }

    @Override
    public float findIntersection(Ray ray) {
        Matrix transform = this.trans.getTransformation();

        Vector4 ray_origin = ray.getRayOrigin();
        float ray_origin_x = ray_origin.getX();
        float ray_origin_y = ray_origin.getY();
        float ray_origin_z = ray_origin.getZ();

        Vector4 ray_direction = ray.getRayDirection();
        float ray_direction_x = ray_direction.getX();
        float ray_direction_y = ray_direction.getY();
        float ray_direction_z = ray_direction.getZ();

        Vector4 sphere_center = transform.multiply(this.center);
        float sphere_center_x = sphere_center.getX();
        float sphere_center_y = sphere_center.getY();
        float sphere_center_z = sphere_center.getZ();


        float b = (2 * (ray_origin_x - sphere_center_x) * ray_direction_x) + (2 * (ray_origin_y - sphere_center_y) * ray_direction_y) + (2 * (ray_origin_z - sphere_center_z) * ray_direction_z);
        float c = (float) (Math.pow(ray_origin_x - sphere_center_x, 2) + Math.pow(ray_origin_y - sphere_center_y, 2) + Math.pow(ray_origin_z - sphere_center_z, 2) - (radius * radius));

        float discriminant = b * b - 4 * c;

        if (discriminant > 0) {
            float root_1 = (float) (((-1.f * b - Math.sqrt(discriminant)) / 2) - 0.000001f);

            if (root_1 > 0) {
                return root_1;
            } else {
                float root_2 = (float) (((Math.sqrt(discriminant) - b) / 2) - 0.000001);
                return root_2;
            }
        } else {
            return -1;
        }
    }
}
