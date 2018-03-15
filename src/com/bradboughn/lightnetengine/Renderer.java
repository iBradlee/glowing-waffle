
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
                
    boolean isInBounds = true;
        
    public Renderer(GameContainer gc) 
    {
        pixelW = gc.getWidth();
        pixelH = gc.getHeight();
        pixels = ((DataBufferInt)gc.getWindow().getBufferImage().getRaster().getDataBuffer()).getData(); //getting the data buffer's data, from our raster, and converting it to int
        zBuffer = new int[pixels.length];
        lightMap = new int[pixels.length];
        lightBlock = new int[pixels.length];
    }
        
    public void clear() 
    {
        for (int i = 0; i < pixels.length; i ++) 
        {
            pixels[i] = 0; //0xffcfccff
            zBuffer[i] = 0;
            lightMap[i] = ambientColor;
            lightBlock[i] = 0;
        }
    }
        
    public void process() { 
        processing = true;            
        Collections.sort(imageRequestArr, new Comparator<ImageRequest>()
        {
            @Override
            public int compare(ImageRequest i0, ImageRequest i1) 
            {
                if (i0.zDepth < i1.zDepth) return -1;
                if (i0.zDepth > i1.zDepth) return 1;
                return 0;
            }
        });
        for (int i = 0; i < imageRequestArr.size(); i++) 
        {
            ImageRequest ir = imageRequestArr.get(i);
            setzDepth(ir.zDepth);
            drawImage(ir.image, ir.offsetX, ir.offsetY);
        }
        for (int i = 0; i < lightRequestArr.size(); i++) 
        {
            LightRequest lr = lightRequestArr.get(i);
            drawLightRequest(lr.light,lr.locX,lr.locY);
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
        lightRequestArr.clear();
        processing = false;
    }
        
    public void setPixel(int x, int y, int value) 
    {                                     
        int alpha = (value >>24) & 0xff;
            
        if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH) || alpha == 0 )
        { 
            return;
        } 
        int index = x + y*pixelW;
        
        if (zBuffer[index] > zDepth) 
        { 
            return;
        }
        zBuffer[index] = zDepth;
            
        if (alpha == 255) 
        { 
            pixels[index] = value;
        } 
        else 
        {            
            int pixelColor = pixels[index]; 
                
            int newRed = ((pixelColor >> 16) & 0xff) - (int)((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha/255f)) ;
            int newGreen = ((pixelColor >> 8) & 0xff) - (int)((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha/255f));
            int newBlue = (pixelColor & 0xff) - (int)(((pixelColor & 0xff) - (value & 0xff)) * (alpha/255f));
                
            pixels[index] = ( newRed << 16 | newGreen << 8 | newBlue); 
        }
    }
        
        
    public void setLightMap (int x, int y, int value) 
    {
        if ((x < 0 || x >= pixelW || y < 0 || y >= pixelH))
        {
            return;
        }
        int baseColor = lightMap[x + y*pixelW];
        int maxRed = Math.max((baseColor >>16) & 0xff, (value >> 16) & 0xff);
        int maxGreen = Math.max((baseColor >>8) & 0xff, (value >> 8) & 0xff);
        int maxBlue = Math.max(baseColor & 0xff, value & 0xff);
            
        lightMap[x + y*pixelW] = ( maxRed << 16 | maxGreen << 8 | maxBlue);

    }
        
    public void setLightBlock (int x, int y, int value) 
    {
        if (zBuffer[x + y * pixelW] > zDepth) 
        { 
            return;
        }
            //Do I need "zBuffer[x+y*pixelW] = zDepth", here? Like how each pixel's depth is set in that
            //way, in setPixel()?
        lightBlock[x + y*pixelW] = value;

    }
        
    public void drawImage(GameImage image, int offsetX, int offsetY) 
    {
        if (image.isAlpha() && !processing)
        {
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
        for (int y = newY; y < newHeight; y++) 
        {
            for (int x = newX; x < newWidth; x++) 
            {
                setPixel(x + offsetX, y + offsetY, image.getPixels()[x + (y*image.getWidth())]);
                setLightBlock(x + offsetX, y + offsetY, image.getLightBlock());

            }
        }
    }

    public void drawImageTile(GameImageTile image, int offsetX, int offsetY, int tileX, int tileY) 
    {
        if (image.isAlpha() && !processing) 
        {
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

        for (int y = newY; y < newHeight; y++) 
        {
            for (int x = newX; x < newWidth; x++)
            {
                setPixel(x + offsetX, y + offsetY, image.getPixels()[(x + (tileX * image.getTileW())) + (y + (tileY * image.getTileH())) * image.getWidth()]);
                setLightBlock(x + offsetX, y + offsetY, image.getLightBlock());
            }
        }
    }

    public void drawText(String text, int offsetX, int offsetY, int color, Font font) 
    {
        int offset = 0;
        for (int i = 0; i < text.length(); i++)
        {
            int unicode = text.codePointAt(i) - font.getUnicodeModifier();
            for (int y = 0; y < font.getGameImage().getHeight(); y++) 
            {
                for (int x = 0; x < font.getFontWidths()[unicode]; x++) 
                {
                    if (font.getGameImage().getPixels()[(x + font.getFontOffsets()[unicode]) + y * font.getGameImage().getWidth() ] == 0xffffffff) 
                    {
                        setPixel(x + offsetX + offset, y + offsetY, color);
                    }
                }
            }
            offset += font.getFontWidths()[unicode];
        }
    }        

    public void drawRect(int offsetX, int offsetY, int width, int height, int color) 
    {
        for (int y = 0; y < height; y++) 
        {
            setPixel(offsetX, y + offsetY, color);
            setPixel(offsetX + width-1, y + offsetY, color);
        }


        for (int x = 0; x < width; x++) 
        {
            setPixel(x + offsetX, offsetY, color);
            setPixel(x + offsetX, offsetY + height-1, color);

        }
    }

    public void drawFillRect(int offsetX, int offsetY, int width, int height, int color) 
    {
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

        for (int y = newY; y < newHeight; y++) 
        {
            for (int x = newX; x < newWidth; x++) 
            {
                setPixel(x + offsetX, y + offsetY, color);
            }
        }


    }
        
    public void drawLight(Light l, int offsetX, int offsetY) 
    {
        lightRequestArr.add(new LightRequest(l,offsetX,offsetY));
    }
        
    private void drawLightRequest(Light l, int offsetX, int offsetY) 
    {
        for (int i = 0; i <=l.getDiameter(); i++) 
        {
            drawLightLine(l, l.getRadius(),l.getRadius(), i, 0, offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), i, l.getDiameter(), offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), 0, i, offsetX, offsetY);
            drawLightLine(l, l.getRadius(),l.getRadius(), l.getDiameter(), i, offsetX, offsetY);
        }
    }    
    
    private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int offsetX, int offsetY)
    { 
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        //what about if they are the same? should be <= ?
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx - dy;
        int err2;
        
        while (true)
        {
            
            int screenX = x0 - l.getRadius() + offsetX;
            int screenY = y0 - l.getRadius() + offsetY;
            
            if (screenX < 0 || screenX >= pixelW || screenY < 0 || screenY >= pixelH) return;
            
            int lightColor = l.getLightValue(x0, y0);
            if (lightColor == 0) return;
            
            if (lightBlock[screenX + screenY * pixelW] == Light.FULL) return;
            
            setLightMap(screenX, screenY, lightColor);

            
            
            if (x0 == x1 && y0 == y1) break;
            
            err2 = err * 2;
            
            if (err2 > -1 * dy) 
            {
                err -= dy;
                x0 += sx;
            }
            
            if (err2 < dx)
            {
                err += dx;
                y0 += sy;
            }
            
        }
    }
    
    public int getzDepth() 
    {
        return zDepth;
    }

    public void setzDepth(int zDepth) 
    {
        this.zDepth = zDepth;
    }

    public Font getFont() 
    {
        return font;
    }

    public int getAmbientColor() 
    {
        return ambientColor;
    }

    public void setAmbientColor(int ambientColor) 
    {
        this.ambientColor = ambientColor;
    }
    
    
}
