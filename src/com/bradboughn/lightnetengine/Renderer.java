
package com.bradboughn.lightnetengine;

import com.bradboughn.lightnetengine.gfx.GameImage;
import java.awt.image.DataBufferInt;

public class Renderer {

        private int pixelW, pixelH;
        private int[] pixels;
        
        public Renderer(GameContainer gc) {
            pixelW = gc.getWidth();
            pixelH = gc.getHeight();
            pixels = ((DataBufferInt)gc.getWindow().getBufferImage().getRaster().getDataBuffer()).getData(); //converting first DataBuffer array, from the Raster of BuffImg, to int array
                    
        }
        
        public void clear() {
            for (int i = 0; i < pixels.length; i ++) {
                pixels[i] = 0;
            }
        }
        
        public void setPixel(int x, int y, int value) {
            
            if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || value == 0xffff00ff ) {
                return;
            } 
            
            pixels[x + (y*pixelW)] = value;
        }
        
        public void drawImage(GameImage image, int offsetX, int offsetY) {
            
            for (int y = 0; y < image.getHeight(); y++) {
             
                for (int x = 0; x < image.getWidth(); x++) {
                    setPixel(x + offsetX, y + offsetY, image.getPixels()[x + (y*image.getWidth())]);
                }
            }
        }

        
        
}
