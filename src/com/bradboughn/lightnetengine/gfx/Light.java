
package com.bradboughn.lightnetengine.gfx;

public class Light {
    
    public static final int NONE = 0;
    public static final int FULL = 1;
    
    int radius, diameter, color;
    private int[] lightMap;
    
    public Light (int radius, int color) {
        
        this.radius = radius;
        this.diameter = radius * 2;
        this.color = color;
        lightMap = new int[diameter * diameter];
        
        
        //<editor-fold defaultstate="collapsed" desc="circle geometry comment">
        /*
        
        Two nested "for loops", set to increment thru to the length of our diameter, will naturally map
        out a SQUARE shape, with a length and width both of our diameter. We, however, need to map out
        a circle shape. Using the Pythagorean Theorem(a^2 + b^2 = c^2), we can ascertain a set of "all"
        possible distances/radii for a circle, within our square "map"/grid; "Distance" being the 
        same thing as a radius, here.
        Our distance is the "c^2" part of the algorithm. We will cycle thru our x and y coordinates 
        (our "a^2" and "b^2"), "square" them, and in return get a new possible distance (or radii).
        We then check to make sure our distance is NOT larger than our initial radii, which we set as 
        effectively our "max distance/radius" when creating the Lightclass itself.
        
        */
//</editor-fold>
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                double distance = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));
                
//<editor-fold defaultstate="collapsed" desc="setting light map comment">
//power will get the "intensity" of the light, by effectively checking how close each
//pixel is from the center. Power is then multiplied to each individual R,G,B value
//of our light's color. "1-" is there to fix the fact that simply dividing radius from
//distance yeilds the exact OPPOSITE of what we want. 1- inverts it. Instead of .10,
//it's now .90, for example
//</editor-fold>
                if (distance < radius) {
                    double power = 1 - (distance/radius);
                    lightMap[x + (y*diameter)] = ((int)(((color >> 16) & 0xff) * power) << 16 | (int)(((color >> 8) & 0xff) * power) << 8 | (int)((color & 0xff) * power));
                }
                else {
                    lightMap[x + (y*diameter)] = 0;
                }
            }
        }
    }
    
    public void generateNewColorLight(int color) {
        this.color = color;
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                double distance = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));
                
                if (distance < radius) {
                    double power = 1 - (distance/radius);
                    lightMap[x + (y*diameter)] = ((int)(((color >> 16) & 0xff) * power) << 16 | (int)(((color >> 8) & 0xff) * power) << 8 | (int)((color & 0xff) * power));
                }
                else {
                    lightMap[x + (y*diameter)] = 0;
                }
            }
        }
        
    }
//<editor-fold defaultstate="collapsed" desc="getLightValue CONFUSED comment">
    /*
    *   I'm not sure about the "if statement" here. If it is larger than the size of our current
    *   individual light map, then it is set to black? What about the fact that all lights are mapped
    *   out seperately, and then added to our Renderer's light map, and applied after everything's
    *   said and done? Wouldn't that mean if this particular light had a diameter of 50, but our
    *   drawLightLine() (the method that calls this, and uses it's x and y as arguments) tried to set
    *   "light lines" up to 250, it would not pass the "if", and therefore set all those extra coordinates
    *   to black, therefore potentially overwriting other lights AND ALSO darkening random parts of
    *   the canvas?? I R SO CUNFUZED
    */
//</editor-fold>
    public int getLightValue(int x, int y) {
        if (x < 0 || x >= diameter || y < 0 || y >=diameter) {
            return 0;
        }
        return lightMap[x + y*diameter];
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public int getColor() {
        return color;
    }

    public int[] getLightMap() {
        return lightMap;
    }

    public void setLightMap(int[] lightMap) {
        this.lightMap = lightMap;
    }

}
