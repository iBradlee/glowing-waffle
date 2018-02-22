
package com.bradboughn.lightnetengine.gfx;

public class GameImageTile extends GameImage {
    
    private int tileW, tileH;

    
    /*Would like to redesign this class, possibly. I'm thinking: GameImage stays, but is treated more
    * as a SpriteSheet (perhaps?) 
    * 
    * I'd like to have the exact x and y coordinates for whatever tile we're rendering, instead of leaving
    * that up to the render to do every time.
    * 
    * I also feel that the Image classes should have their "zDepth" kept locally, instead of ONLY having
    * a persistant zDepth variable in Renderer, handling the layers of renders/images. I think having
    * both a local GameImage zDepth, and having one in Renderer (maybe just a "maxzDepth"??) to deal
    * with the issue could possibly be interesting at the least?
    */
    public GameImageTile (String path, int tileW, int tileH) {
            super(path);
            this.tileW = tileW;
            this.tileH = tileH;
    }
    
    public GameImage getImageFromTile(int tileX, int tileY) {
        int[]pixels = new int[tileW * tileH];
        
        for (int y = 0; y < tileH; y++ ) {
            for (int x = 0; x < tileW; x++) {
                pixels[x + y * tileW] = this.getPixels()[(x + (tileX * tileW)) + ((y + tileY * tileH) * this.getWidth())];
                
            }
            
        }
       
        
        return new GameImage(pixels, tileW, tileH);
    }

    public int getTileW() {
        return tileW;
    }

    public void setTileW(int tileW) {
        this.tileW = tileW;
    }

    public int getTileH() {
        return tileH;
    }

    public void setTileH(int tileH) {
        this.tileH = tileH;
    }

}
