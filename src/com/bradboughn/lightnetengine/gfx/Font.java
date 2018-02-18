
package com.bradboughn.lightnetengine.gfx;

public class Font {
    
    public static final Font STANDARD = new Font("/fonts/pixFont.png");
    
    private GameImage gameImage;
    private int[] fontOffsets;
    private int[] fontWidths;
    
    public Font(String path) {
        gameImage = new GameImage(path);
        
        fontOffsets = new int[59];
        fontWidths = new int[59];
        
        int unicode = 0;
        for (int i = 0; i < gameImage.getWidth(); i++) {
            if (gameImage.getPixels()[i] == 0xff0000ff) {
                fontOffsets[unicode] = i;
            }
            if (gameImage.getPixels()[i] == 0xffffff00 ) {
                fontWidths[unicode] = i - fontOffsets[unicode];
                unicode++;
            }
        }
            
    }

    public GameImage getGameImage() {
        return gameImage;
    }

    public int[] getFontOffsets() {
        return fontOffsets;
    }

    public int[] getFontWidths() {
        return fontWidths;
    }
    

    
    

    
    
    


}
