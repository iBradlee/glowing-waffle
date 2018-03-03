
package com.bradboughn.lightnetengine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
Would like to try redesigning this class into a SpriteSheet-like class, with a Sprite subclass,
unless this design flaw (IMO) is dealt with soon, or already has reasoning behind it that
renders such functionality obsolete.
*/

public class GameImage { 
    
    private int width, height;
    private int[] pixels;
    private boolean alpha = false;
    /* I THINK WE COULD ADD THIS VALUE TO THE CONSTRUCTOR OF AN IMAGE, SO IT CAN BE SET THERE INSTEAD
    *  of having to call a method in GameManager. So, i'm thinking we may eventually have subclasses
    *  of ImageTile (image also? idk) for all standard objects in game, that could have a randomly
    *  generated image. For instance, for single rocks, we could have a rock subclass of 
    *  ImageTile that uses an image with many different rock images(a spritesheet). When rock class
    *  is created, it generates a random number, picking the image which corresponds to that number.
    *  And because we would know that (large) rocks will always cast a shadow, we could have the 
    *  WHOLE CLASS' "lightBlock" variable set to permanently be FULL(will block light)
    */
    
    private int lightBlock = Light.NONE;  

    public GameImage (String path) {
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(GameImage.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        width = image.getWidth();
        height = image.getHeight();
        pixels = image.getRGB(0, 0, width, height, null, 0, width);
        
        image.flush();
    }
    
    public GameImage(int[] pixel, int width, int height) {
        this.pixels = pixel;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public boolean isAlpha() {
        return alpha;
    }
    
    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    public int getLightBlock() {
        return lightBlock;
    }

    public void setLightBlock(int lightBlock) {
        this.lightBlock = lightBlock;
    }

    
}
