
package com.bradboughn.lightnetengine.gfx;

public class GameImageTile extends GameImage {
    
    private int tileW, tileH;
    
    public GameImageTile (String path, int tileW, int tileH) {
            super(path);
            this.tileW = tileW;
            this.tileH = tileH;
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
