
package com.bradboughn.lightnetengine;

import com.bradboughn.lightnetengine.gfx.GameImage;
import java.awt.image.DataBufferInt;

public class Renderer {
        private static int counter; //TEST var
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
        /* drawImage() and setPixel()'s "x" and "y"   
        *  are the x and y values conscerning the canvas/window.
        */
        public void setPixel(int x, int y, int value) {                                     
            
            if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || value == 0xffff00ff ) {
                return;
            } 
            
            pixels[x + (y*pixelW)] = value;
        }
        
        /*  
        *   Regarding the Clipping code below:
        *   Stops from trying to set pixel data for images, or parts of images, which would be past our canvas, thus not actually drawn anyway
        *   It does this by using these conditions' new values and applying them to our loop below which uses 
        *   these new values to say: "this newWidth/newHeight" are the amount of pixels we need to actually draw" or for newX/newY : "These values are where
        *   we need to start from, in our particular image array"
        */
        
        public void drawImage(GameImage image, int offsetX, int offsetY) {
            if (counter>180) counter = 0;
            counter++;
            int newX = 0;
            int newY = 0;
            int newWidth = image.getWidth();
            int newHeight = image.getHeight();
            
            //Don't Render code
            if (offsetX < -newWidth) return;
            if (offsetY < -newHeight)return;
            
            //Clipping code
            if (offsetX < 0) newX -= offsetX;       
            if (offsetY < 0) newY -= offsetY;       
            if (newWidth + offsetX  > pixelW) newWidth -= newWidth + offsetX - pixelW;
            if (newHeight + offsetY > pixelH) newHeight -= newHeight + offsetY - pixelH;
            
            for (int y = newY; y < newHeight; y++) {
             
                for (int x = newX; x < newWidth; x++) {
                    setPixel(x + offsetX, y + offsetY, image.getPixels()[x + (y*image.getWidth())]);
                }
            }
        }

        
        
}
