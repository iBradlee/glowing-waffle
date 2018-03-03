
package com.bradboughn.lightnetengine;

import com.bradboughn.game.GameManager;
import com.bradboughn.lightnetengine.gfx.Font;
import com.bradboughn.lightnetengine.gfx.GameImage;
import com.bradboughn.lightnetengine.gfx.GameImageTile;
import com.bradboughn.lightnetengine.gfx.ImageRequest;
import com.bradboughn.lightnetengine.gfx.Light;
import com.bradboughn.lightnetengine.gfx.LightRequest;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Renderer {
    
    private Font font = Font.STANDARD;
    private Font font2 = Font.STANDARD2;
    private ArrayList<ImageRequest> imageRequestArr = new ArrayList();
    private ArrayList<LightRequest> lightRequestArr = new ArrayList();
        
    private int pixelW, pixelH;
    private int[] pixels;
    private int[] zBuffer;
    private int[] lightMap;
    private int[] lightBlock;
        
        
    private int ambientColor = 0xff5a5a5a;
    private int zDepth = 0;
    private boolean processing = false;
                
        //for drawLightLine. not sure if it should be here, or down below. Seems like it would obviously
        //be less "quick" if it created a new instance every time it's ran
    boolean isInBounds = true;
        
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
        
        //handles ImageRequests. Which create an order to render images with alpha channel. Is ran after all other opaque rendering is done.
    public void process() { 
        processing = true;            
//<editor-fold defaultstate="collapsed" desc="sorting comment">
//Sorting ArrList, and initializing a new Comparator, to sort by zDepth, as well as
//implementing/overriding the needed method, in order to sort all by zDepth
//</editor-fold>
        Collections.sort(imageRequestArr, new Comparator<ImageRequest>(){
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
            //I still think we should sort lights/store a zdepth somehow...
        for (int i = 0; i < lightRequestArr.size(); i++) {
            LightRequest lr = lightRequestArr.get(i);
            drawLightRequest(lr.light,lr.locX,lr.locY);
        }
            
            
//<editor-fold defaultstate="collapsed" desc="lightMap comment">
/*   

Using our lightMap, this sets an individual R,G,B value from 0 to 1, as a float.
If our individual color value from lightMap was max (255), then our result for that color would be 1,
and when we set the actual FINAL color in pixel array (last part of this loop), that
specific color value (R,G or B) would stay exactly the same as it was before running thru our lightMap.
Otherwise, it will use these small decimal numbers (like .24334) and multiply them to their
corresponding R,G,B values in our base "finished" pixel array for the entire screen/canvas ("finished" as in,
every pixel has been set properly at this point, with all opaque and transparent images factored in. Everything but lightMap)

*/
//</editor-fold>

        for (int i = 0; i < pixels.length; i++) {
            float r = ((lightMap[i] >>16) & 0xff) / 255f;
            float g = ((lightMap[i] >>8) & 0xff) / 255f;
            float b = (lightMap[i]  & 0xff) / 255f;
                
            pixels[i] = ((int)(((pixels[i] >> 16) & 0xff) * r) << 16 |
                        (int)(((pixels[i] >> 8) & 0xff) * g) << 8 |
                        (int)((pixels[i] & 0xff) * b));
            }

        imageRequestArr.clear();
        lightRequestArr.clear();
        processing = false;
    }
        
    public void setPixel(int x, int y, int value) {                                     
            
        int alpha = (value >>24) & 0xff;
            
        if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || alpha == 0 ) { 
            return;
        } 
            
        int index = x + y*pixelW;
            
//<editor-fold defaultstate="collapsed" desc="zBuffer concern comment">
//I don't think this does what it's supposed to 100%. because zBuffer hasn't had info put into it.
//I believe it is only after this if statement, that the zBuffer[index] is set to zDepth.
//Meaning that no matter what, the first image that renders, which has no alpha, will be
//drawn immediately.
//</editor-fold>
            
        if (zBuffer[index] > zDepth) { 
            return;
        }
            
        zBuffer[index] = zDepth;
            
            //if solid color, just render as usual
        if (alpha == 255) { 
            pixels[index] = value;
        } else {            
            //else we will blend colors to achieve transparency
            int pixelColor = pixels[index]; 
                
            int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha/255f)) ;
            int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha/255f));
            int newBlue = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (value & 0xff)) * (alpha/255f));
                
            pixels[index] = ( newRed << 16 | newGreen << 8 | newBlue); 
        }
    }
        
        
        //<editor-fold defaultstate="collapsed" desc="setLightMap modification comment">
        /*
        *   As far as I know, we don't have any zBuffer values for any lights, in this engine. That is
        *   completely wrong. Lights CAN be in the distance(on a lower zBuffer) or in front of everything
        *   (highest zBuffer) or even anywhere in between the two. If a light is in the far distance,
        *   it will not affect anything in the foreground, only items also in the background. If a 
        *   light is in the "center"(with the character), there are MANY possible interactions with
        *   light and shadows. It could be in character's right hand (character is in sidescroller
        *   format, facing right), meaning it lights up most everything in the "center" or "main" depth
        *   Only items EVEN CLOSER "to the camera" than his right hand would not be brightened by this
        *   light. Then it could be in his left hand, not brightening even the character fully, but would
        *   cast SOME sort of a "glow" (this is not currently supported id think);
        */
//</editor-fold>
    public void setLightMap (int x, int y, int value) {
        if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH)  ) {
            return;
        }
            //Ambient light color/data
        int baseColor = lightMap[x + y*pixelW];
            //Gets the higher value, using individual R,G,B data, between our current ambient light
            //(ambientColor), and our source of light (value)
        int maxRed = Math.max((baseColor >>16) & 0xff, (value >> 16) & 0xff);
        int maxGreen = Math.max((baseColor >>8) & 0xff, (value >> 8) & 0xff);
        int maxBlue = Math.max(baseColor & 0xff, value & 0xff);
            
            //Sets pixel of lightMap to the result of combining the highest R,G, and B values up above. 
        lightMap[x + y*pixelW] = ( maxRed << 16 | maxGreen << 8 | maxBlue);

    }
        
    public void setLightBlock (int x, int y, int value) {
        if (zBuffer[x + y * pixelW] > zDepth) { 
            return;
        }
            //Do I need "zBuffer[x+y*pixelW] = zDepth", here? Like how each pixel's depth is set in that
            //way, in setPixel()?
            
        lightBlock[x + y*pixelW] = value;

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
                setLightBlock(x + offsetX, y + offsetY, image.getLightBlock());

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
                setLightBlock(x + offsetX, y + offsetY, image.getLightBlock());
            }
        }
    }

    public void drawText(String text, int offsetX, int offsetY, int color, Font font) {
        int offset = 0;

        //text = text.toUpperCase(); Just taking this out for now. Is needed for custom fonts

        for (int i = 0; i < text.length(); i++) {

            //if using custom-made font, and not using ALL unicode chars, you can modify this by
            //like -32, for example, to start with "space"
            int unicode = text.codePointAt(i) - font.getUnicodeModifier();

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
        
    public void drawLight(Light l, int offsetX, int offsetY) {
        lightRequestArr.add(new LightRequest(l,offsetX,offsetY));
    }
        
//<editor-fold defaultstate="collapsed" desc="drawLight explained comment">
        /*
        * I believe each one of these "drawLightLine"'s are ran "l.getDiameter" times, and each individual
        * method handles one of the four "sides" of our Light. So for example our first drawLightLine
        * function handles drawing every line from the centerpoint, thru to the top of our Light.
        * So on first iteration where i=0, a line will be drawn from center to the top left corner. On
        * the last iteration where i=diameter, a line will be drawn from center to the top right corner.
        * I can ascertain this because: Starting at the center(x0=radius, y0=radius) our x1(the end point
        * of our line) will iterate thru each pixel from 0 to our diameter. Since this iterator
        * is in the parameter that handles our endpoint(x1) for x, it will cycle thru every possible
        * line starting from the center, and ending at all points along the TOP of our Light.
        */
//</editor-fold>
    private void drawLightRequest(Light l, int offsetX, int offsetY) {
        for (int i = 0; i <=l.getDiameter(); i++) {
            drawLightLine(l, l.getRadius(),l.getRadius(), i, 0, offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), i, l.getDiameter(), offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), 0, i, offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), l.getDiameter(), i, offsetX, offsetY);
        }
    }    
    
    private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offsetX, int offsetY) { 
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        //what about if they are the same? should be <= ?
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx - dy;
        int err2;
        
        while (true) {
            
            int screenX = x0 - l.getRadius() + offsetX;
            int screenY = y0 - l.getRadius() + offsetY;
            
            if (screenX < 0 || screenX >= pixelW || screenY < 0 || screenY >= pixelH) return;
            
            int lightColor = l.getLightValue(x0, y0);
            if (lightColor == 0) return;
            
            if (lightBlock[screenX + screenY * pixelW] == Light.FULL) return;
            
            setLightMap(screenX, screenY, lightColor);

            
            
            if (x0 == x1 && y0 == y1) break;
            
            err2 = err * 2;
            
            if (err2 > -1 * dy) {
                err -= dy;
                x0 += sx;
            }
            
            if (err2 < dx) {
                err += dx;
                y0 += sy;
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





    
    
            //TESTING METHODS. DELETE AFTER USE
        
    public void testLightMapClear() {
        for (int i = 0; i < pixels.length; i++) {
            GameManager.testMap[i] = 0;
        }
    }
    public void testLightMapSetPixel(int x, int y, int value) {
        if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH)) { 
            return;
        } 
        int index = x + y*pixelW;
        pixels[index] = value;
    }
        
    public void testLightMapRender() {
        for (int y = 0; y < pixelH; y++) { 
            for (int x = 0; x < pixelW; x++) {
                testLightMapSetPixel(x, y, GameManager.testMap[x + y*pixelW]);
            }
        }
    }
        
            // END OF TESTING METHODS. DELETE AFTER USE

        
        
}
