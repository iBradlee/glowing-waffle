
package com.bradboughn.lightnetengine;

import java.awt.image.DataBufferInt;

public class Renderer {

        private int pW, pH;
        private int[] pixels;
        
        public Renderer(GameContainer gc) {
            pW = gc.getWidth();
            pH = gc.getHeight();
            pixels = ((DataBufferInt)gc.getWindow().getBufferImage().getRaster().getDataBuffer()).getData(); //converting first DataBuffer array, from the Raster of BuffImg, to int array
                    
        }
}
