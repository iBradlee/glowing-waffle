
package com.bradboughn.lightnetengine;

import com.bradboughn.lightnetengine.gfx.Font;
import com.bradboughn.lightnetengine.gfx.GameImage;
import com.bradboughn.lightnetengine.gfx.GameImageTile;
import com.bradboughn.lightnetengine.gfx.ImageRequest;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer {
        private Font font = Font.STANDARD;
        private Font font2 = Font.STANDARD2;
        private ArrayList<ImageRequest> imageRequestArr = new ArrayList();
        
        private int pixelW, pixelH;
        private int[] pixels;
        private int[] zBuffer;
        private int[] lightMap;
        private int[] lightBlock;
        
        
        private int ambientColor = 0xff5a5a5a;
        private int zDepth = 0;
        private boolean processing = false;
                
        public Renderer(GameContainer gc) {
            pixelW = gc.getWidth();
            pixelH = gc.getHeight();
            pixels = ((DataBufferInt)gc.getWindow().getBufferImage().getRaster().getDataBuffer()).getData(); //getting the data buffer's data, from our raster, and converting it to int
            zBuffer = new int[pixels.length];
            lightMap = new int[pixels.length];
            lightBlock = new int[pixels.length];
                    
        }
        
        public void clear() {
            for (int i = 0; i < pixels.length; i ++) {
                pixels[i] = 0; //0xffcfccff
                zBuffer[i] = 0;
                lightMap[i] = ambientColor;
                lightBlock[i] = 0;
            }
        }
        
        public void process() { //handles ImageRequests. Which create an order to render images with alpha channel. Is ran after all other opaque rendering is done.
            processing = true;
            
            Collections.sort(imageRequestArr, new Comparator<ImageRequest>(){//Sorting ArrList, and initializing a new Comparator, to sort, as well as implementing the needed method
                @Override
                public int compare(ImageRequest i0, ImageRequest i1) {
                    if (i0.zDepth < i1.zDepth) return -1;
                    if (i0.zDepth > i1.zDepth) return 1;
                    return 0;
                }
                
            });
            for (int i = 0; i < imageRequestArr.size(); i++) {
                ImageRequest ir = imageRequestArr.get(i);
                setzDepth(ir.zDepth);
                drawImage(ir.image, ir.offsetX, ir.offsetY);
            }
            
            for (int i = 0; i < pixels.length; i++) {
                float r = ((lightMap[i] >>16) & 0xff) / 255f;
                float g = ((lightMap[i] >>8) & 0xff) / 255f;
                float b = (lightMap[i]  & 0xff) / 255f;
                
                pixels[i] = ((int)(((pixels[i] >> 16) & 0xff) * r) << 16 |
                            (int)(((pixels[i] >> 8) & 0xff) * g) << 8 |
                            (int)((pixels[i] & 0xff) * b));
            }

            
            imageRequestArr.clear();
            processing = false;
        }
        
        //value & 0xff : bitwise "and" (&) operator takes int "value" in binary, and compares it to binary 0xff (255). add up all bits that both share. if it is 0, then do not draw.
        //0xff(255) is full opaque (solid); our alpha channel. So, if the "value" shares no bit values with 0xff, which means it's the complete opposite, and fully transparent, then do not render
        //
        
        //>> is a bitwise right shift operator. It literally means shift all bit values one place to the right, and add. Right shift by 1 on any number (x>>1) is the equivalent of dividing by two. 40>>1 = 20
        //Right shift by two (x>>2) is the same as dividing by two, and then dividing the result by two. 40>>2= 10. This pattern goes on forever.
        //<< is bitwise left shift. It functions in the complete opposite way, in that it shifts all bits to the left 1 spot. Instead of dividing, you multiply. 60<<2 = 240. 60<<2 = (60<<1= 120 + 120<<1 = 240)
        
        public void setPixel(int x, int y, int value) {                                     
            
            int alpha = (value >>24) & 0xff;
            
            if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || alpha == 0 ) { 
                return;
            } 
            
            int index = x + y*pixelW;
            
            //I don't think this does anything really, because zBuffer hasn't had info put into it. 
            //I believe it is only after this if statement, that the zBuffer[index] is set to zDepth.
            //Meaning that no matter what, the first image that renders, which has no alpha, will be
            //drawn immediately. 
            
            if (zBuffer[index] > zDepth) { 
                return;
            }
            
            zBuffer[index] = zDepth;
            
            if (alpha == 255) { //if solid color, just render as usual
                pixels[index] = value;
                
            } else {            //else we will blend colors to achieve transparency
                int pixelColor = pixels[index]; //opaque base canvas layer. pixelColor is the pixel we're looking at now, in that base layer.
                
                int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha/255f)) ;
                int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha/255f));
                int newBlue = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (value & 0xff)) * (alpha/255f));
                
                
                pixels[index] = ( newRed << 16 | newGreen << 8 | newBlue); //took out "unneccisary" (255 << 24) at beginning of = value, as it dealt with alpha and we "don't need for base layer"
                                                                          //would like to look into this to see why, and why this only is used by base layer apparently?
                
            }
        }
        
        public void setLightMap (int x, int y, int value) {
            if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH)  ) {
                return;
            }
            //Just ambient light color/data, I think?
            int baseColor = lightMap[x + y*pixelW];

            int maxRed = Math.max((baseColor >>16) & 0xff, (value >> 16) & 0xff);
            int maxGreen = Math.max((baseColor >>8) & 0xff, (value >> 8) & 0xff);
            int maxBlue = Math.max(baseColor & 0xff, value & 0xff);
            
            lightMap[x + y*pixelW] = ( maxRed << 16 | maxGreen << 8 | maxBlue);
        }

        public void drawText(String text, int offsetX, int offsetY, int color, Font font) {
            int offset = 0;
            
            //text = text.toUpperCase(); Just taking this out for now. Is needed for custom fonts
            
            for (int i = 0; i < text.length(); i++) {
                
                int unicode = text.codePointAt(i) - font.getUnicodeModifier();//if using custom-made font, and not using ALL unicode chars, you can modify this by like -32, for example, to start with "space"

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
            if (image.isAlpha() && !processing) {
                imageRequestArr.add(new ImageRequest(image, zDepth++, offsetX, offsetY));
                return;
            }
            
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
            if (image.isAlpha() && !processing) {
                imageRequestArr.add(new ImageRequest(image.getImageFromTile(tileX, tileY), zDepth, offsetX, offsetY));
                return;
            }
            
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
                   
                    setPixel(x + offsetX, y + offsetY, image.getPixels()[(x + (tileX * image.getTileW())) + (y + (tileY * image.getTileH())) * image.getWidth()]);
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

    public int getzDepth() {
        return zDepth;
    }

    public void setzDepth(int zDepth) {
        this.zDepth = zDepth;
    }

    public Font getFont() {
        return font;
    }

    public Font getFont2() {
        return font2;
    }

        
        
}
