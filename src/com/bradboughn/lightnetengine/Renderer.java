
package com.bradboughn.lightnetengine;

import com.bradboughn.lightnetengine.gfx.Font;
import com.bradboughn.lightnetengine.gfx.GameImage;
import com.bradboughn.lightnetengine.gfx.GameImageTile;
import java.awt.image.DataBufferInt;

public class Renderer {
        
        private int pixelW, pixelH;
        private int[] pixels;
        
        private Font font = Font.STANDARD;
        
        public Renderer(GameContainer gc) {
            pixelW = gc.getWidth();
            pixelH = gc.getHeight();
            pixels = ((DataBufferInt)gc.getWindow().getBufferImage().getRaster().getDataBuffer()).getData(); //converting first DataBuffer array, from the Raster of BuffImg, to int array
                    
        }
        
        public void clear() {
            for (int i = 0; i < pixels.length; i ++) {
                pixels[i] = 0x1C2F2F;
            }
        }
        //value & 0xff : bitwise "and" (&) operator takes int "value" in binary, and compares it to binary 0xff (250). add up all bits that both share. if it is 0, then do not draw.
        //0xff(250) is full opaque (solid), our alpha channel. So, if the "value" shares no bit values with 0xff, which means it's the complete opposite, and fully transparent, then do not render
        //& can be thought of similarly to %. in value & 0xff, you will return the value of "value". just as 250%250(or 0xff)=0, 250&250(or 0xff) = 0
        
        //>> is a bitwise right shift operator. It literally means shift all bit values one place to the right, and add. Right shift by 1 on any number (x>>1) is the equivalent of dividing by two. 40>>1 = 20
        //Right shift by two (x>>2) is the same as dividing by two, and then dividing the result by two. 40>>2= 10. This pattern goes on forever.
        //<< is bitwise left shift. It functions in the complete opposite way, in that it shifts all bits to the left 1 spot. Instead of dividing by the last number, you multiply. 60<<2 = 240 == 60*2= 120 * 2 = 240
        public void setPixel(int x, int y, int value) {                                     
                                                                                                  
            if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || ((value >> 24) & 0xff) == 0 ) { // (value >>24 & 0xff == 0) is looking for only alpha channel, and not rendering
                return;
            } 
            
            pixels[x + (y*pixelW)] = value;
        }
        

        public void drawText(String text, int offsetX, int offsetY, int color) {
            int offset = 0;
            text = text.toUpperCase();
            
            for (int i = 0; i < text.length(); i++) {
                
                int unicode = text.codePointAt(i) -32;//-32, because in my font, i skipped the first 32 unicode "chars", and started from "space" 

                for (int y = 0; y < font.getGameImage().getHeight(); y++) {
                    
                    for (int x = 0; x < font.getFontWidths()[unicode]; x++) {
                        if (font.getGameImage().getPixels()[(x + font.getFontOffsets()[unicode]) + y * font.getGameImage().getWidth() ] == 0xffffffff) {
                            setPixel(x + offsetX + offset, y + offsetY, color);
                        }
                    }
                }
                offset += font.getFontWidths()[unicode];
            }
        }
        
        public void drawImage(GameImage image, int offsetX, int offsetY) {
            //"Don't Render" code
            if (offsetX < -image.getWidth()) return;
            if (offsetY < -image.getHeight())return;
            if (offsetX >= pixelW) return;
            if (offsetY >= pixelH) return;
            
            int newX = 0;
            int newY = 0;
            int newWidth = image.getWidth();
            int newHeight = image.getHeight();          
            
            //Clipping code
            if (offsetX < 0) newX -= offsetX;       
            if (offsetY < 0) newY -= offsetY;       
            if (newWidth + offsetX  >= pixelW) newWidth -= newWidth + offsetX - pixelW;
            if (newHeight + offsetY >= pixelH) newHeight -= newHeight + offsetY - pixelH;
            
            for (int y = newY; y < newHeight; y++) {
             
                for (int x = newX; x < newWidth; x++) {
                    setPixel(x + offsetX, y + offsetY, image.getPixels()[x + (y*image.getWidth())]);
                }
            }
        }
        
        public void drawImageTile(GameImageTile image, int offsetX, int offsetY, int tileX, int tileY) {
            //"Don't Render" code
            if (offsetX < -image.getTileW()) return;
            if (offsetY < -image.getTileH())return;
            if (offsetX >= pixelW) return;
            if (offsetY >= pixelH) return;
            
            int newX = 0;
            int newY = 0;
            int newWidth = image.getTileW();
            int newHeight = image.getTileH();          
            
            //Clipping code
            if (offsetX < 0) newX -= offsetX;       
            if (offsetY < 0) newY -= offsetY;       
            if (newWidth + offsetX  >= pixelW) newWidth -= newWidth + offsetX - pixelW;
            if (newHeight + offsetY >= pixelH) newHeight -= newHeight + offsetY - pixelH;
            
            for (int y = newY; y < newHeight; y++) {
             
                for (int x = newX; x < newWidth; x++) {
                   //LOOK INTO more thorough
                    setPixel(x + offsetX, y + offsetY, image.getPixels()[(x + tileX * image.getTileW()) + (y + tileY * image.getTileH()) * image.getWidth()]);
                }
            }
        }
        
        public void drawRect(int offsetX, int offsetY, int width, int height, int color) {
            for (int y = 0; y <= height; y++) {
                setPixel(offsetX, y + offsetY, color);
                setPixel(offsetX + width, y + offsetY, color);
            }
            
            for (int x = 0; x <= width; x++) {
                setPixel(x + offsetX, offsetY, color);
                setPixel(x + offsetX, offsetY + height, color);
                
            }
        }
        
        public void drawFillRect(int offsetX, int offsetY, int width, int height, int color) {
            //"Don't Render" code
            
            if (offsetX < -width) return;
            if (offsetY < -height) return;
            if (offsetX >= pixelW) return;
            if (offsetY >= pixelH) return;
            
            int newX = 0;
            int newY = 0;
            int newWidth = width;
            int newHeight = height;
            
            //Clipping code
            if (offsetX < 0) newX -= offsetX;       
            if (offsetY < 0) newY -= offsetY;       
            if (newWidth + offsetX  >= pixelW) newWidth -= newWidth + offsetX - pixelW;
            if (newHeight + offsetY >= pixelH) newHeight -= newHeight + offsetY - pixelH;
            
            for (int y = newY; y <= newHeight; y++) {
                for (int x = newX; x <= newWidth; x++) {
                    setPixel(x + offsetX, y + offsetY, color);
                }
            }
        }

        
        
}
