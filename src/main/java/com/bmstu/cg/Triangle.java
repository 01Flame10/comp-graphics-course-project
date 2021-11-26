package com.bmstu.cg;

public class Triangle extends PrimitiveObject {
    public Vector4 A, B, C;
    public Vector4 An, Bn, Cn;
    public Vector4 At, Bt, Ct;
    public Vector4 normal;
    public ColorCG color;
    private ImageCG texture;
    private boolean is_texture;

    public Triangle() {
        A = new Vector4(0, 0, 1);
        B = new Vector4(0, 1, 0);
        C = new Vector4(1, 0, 0);
        color = new ColorCG(0.5f, 0.5f, 0.5f);
        is_texture = false;
    }

    public Triangle(Vector4 A_value, Vector4 B_value, Vector4 C_value, ColorCG colorValue) {
        A = A_value;
        B = B_value;
        C = C_value;
        color = colorValue;
        is_texture = false;
    }


    public Triangle(Vertex A_value, Vertex B_value, Vertex C_value, ImageCG texture_value) {

        A = A_value.getPosition();
        B = B_value.getPosition();
        C = C_value.getPosition();

        An = A_value.getNormal();
        Bn = B_value.getNormal();
        Cn = C_value.getNormal();

        At = A_value.getTexCoords();
        Bt = B_value.getTexCoords();
        Ct = C_value.getTexCoords();

        texture = texture_value;
        is_texture = true;
    }

    public Triangle(Vertex A_value, Vertex B_value, Vertex C_value, ColorCG colorValue) {
        A = A_value.getPosition();
        B = B_value.getPosition();
        C = C_value.getPosition();
        An = A_value.getNormal();
        Bn = B_value.getNormal();
        Cn = C_value.getNormal();

        At = A_value.getTexCoords();
        Bt = B_value.getTexCoords();
        Ct = C_value.getTexCoords();
        color = colorValue;
    }

    public static double getCoord(double i1, double i2, double w1,
                                  double w2, double p) {
        return ((p - i1) / (i2 - i1)) * (w2 - w1) + w1;
    }

    @Override
    public ColorCG getColor(Vector4 pos) {
        if (is_texture) {
            Vector4 vAB = A.substitute(B);
            Vector4 vAC = A.substitute(C), vPA = pos.substitute(A), tAB = At.substitute(Bt), tAC = At.substitute(Ct);

            float A1 = vAB.dot3(vAB);
            float A2 = vAC.dot3(vAB);
            float A3 = vPA.dot3(vAB);
            float B1 = vAB.dot3(vAC);
            float B2 = vAC.dot3(vAC);
            float B3 = vPA.dot3(vAC);

            float a = (A3 * B2 - A2 * B3) / (A1 * B2 - A2 * B1);
            float b = (A1 * B3 - A3 * B1) / (A1 * B2 - A2 * B1);

            float y1 = (tAB.getY() * a + tAC.getY() * b + At.getY()) * texture.getHeight();//getCoord(0, dU, Ct.getY() * dy, Bt.getY() * dy, distY);
            float x1 = (tAB.getX() * a + tAC.getX() * b + At.getX()) * texture.getWidth();//getCoord(0, dV, At.getX() * dx, Bt.getX() * dx, distX);

            int i1 = (int) x1, j1 = (int) y1;
            if (i1 >= 0 && j1 >= 0 &&
                    i1 < texture.getWidth() &&
                    j1 < texture.getHeight()) {
                float[] colors = texture.getPixelColor(i1, j1);
                ColorCG color_current = new ColorCG(colors[0], colors[1], colors[2], texture.specular, texture.refl, texture.refr, texture.opacity);
                return color_current;
            }
            return new ColorCG(0, 0, 0);

        }
        return color;
    }

    @Override
    public Vector4 getNormalAt(Vector4 pos) {
        Vector4 vAB = A.substitute(B);


        Vector4 vAC = A.substitute(C), vPA = pos.substitute(A), tAB = An.substitute(Bn), tAC = An.substitute(Cn);

        float A1 = vAB.dot(vAB);
        float A2 = vAC.dot(vAB);
        float A3 = vPA.dot(vAB);
        float B1 = vAB.dot(vAC);
        float B2 = vAC.dot(vAC);
        float B3 = vPA.dot(vAC);

        float a = (A3 * B2 - A2 * B3) / (A1 * B2 - A2 * B1);
        float b = (A1 * B3 - A3 * B1) / (A1 * B2 - A2 * B1);

        float y1 = (tAB.getY() * a + tAC.getY() * b + An.getY());
        float x1 = (tAB.getX() * a + tAC.getX() * b + An.getX());
        float z1 = (tAB.getZ() * a + tAC.getZ() * b + An.getZ());


        return new Vector4(x1, y1, z1);//An.Add(Bn).Add(Cn).Normalized(); //new Vector4f(x1, y1, z1);
    }

    @Override
    public float findIntersection(Ray ray) {
        Vector4 ray_direction = ray.getRayDirection();
        Vector4 ray_origin = ray.getRayOrigin();

        Vector4 E1 = B.substitute(A);
        Vector4 E2 = C.substitute(A);
        Vector4 T = ray_origin.substitute(A);
        Vector4 P = ray_direction.cross(E2);
        Vector4 Q = T.cross(E1);
        float znam = P.dot3(E1);
        float t = Q.dot3(E2) / znam;
        float u = P.dot3(T) / znam;
        float v = Q.dot3(ray_direction) / znam;
        float t1 = 1 - u - v;
        if (u < 1f && u > 0f && v < 1f && v > 0f && t1 < 1f && t1 > 0f)
            return t;
        else
            return -1;

    }
}   
    
 