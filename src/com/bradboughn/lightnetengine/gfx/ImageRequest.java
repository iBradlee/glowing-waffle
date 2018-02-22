
package com.bradboughn.lightnetengine.gfx;

public class ImageRequest {
    
    public GameImage image;
    public int zDepth;
    public int offsetX, offsetY;
    
    public ImageRequest(GameImage image, int zDepth, int offsetX, int offsetY) {
        this.image = image;
        //image.setAlpha(false); 
        this.zDepth = zDepth;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        
    }
    
    //About: image.setAlpha(false), in constructor.
    //I thought this would be an easy way to get out of the infinite loop of adding ImageRequests to our 
    //imageRequestArr, in Renderer. It, however, seems to set the alpha as false permanently, and doesn't
    //reset after every call of renderer.process(). I thought it would, and should, because we create a 
    //new instance of ImageRequest every time we detect alpha channel being used, in Renderer.drawImage()
    //I would like to look into this more, to see what actually is going on, and not allowing this to work. intradesting...

}
