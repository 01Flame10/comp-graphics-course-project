package com.bmstu.cg;


public class Source {
    public Source() {}
    public Vector4 getLightPosition() {return new Vector4(0, 0, 0);}
    public void setLightPosition (Vector4 pos) {}
    public ColorCG getLightColor() {return new ColorCG(1, 1, 1);}
    public void setLightIntensive (float intens) { }
    public void setLightColor (ColorCG col) {  }
    public float getLightIntensive () { return 0; }
    
}
