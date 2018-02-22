
package com.bradboughn.lightnetengine.gfx;

public class Font {
    
    public static final Font STANDARD = new Font("/fonts/pixFont.png", 32);
    private final int unicodeModifier;
    public static final Font STANDARD2 = new Font("/fonts/silkscreen.png"); //Smaller, tighter font. Needs to be edited as I did with my custom font image "pixFont.png"
    
    private GameImage gameImage;
    private int[] fontOffsets;
    private int[] fontWidths;
    
    public Font(String path) {
        gameImage = new GameImage(path);
        
        fontOffsets = new int[256];
        fontWidths = new int[256];
        
        unicodeModifier = 0;
        
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
    
    public Font(String path, int uniMod) {
        gameImage = new GameImage(path);
        
        fontOffsets = new int[256];
        fontWidths = new int[256];
        
        unicodeModifier = uniMod;
        
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

    public int getUnicodeModifier() {
        return unicodeModifier;
    }
    

    
    

    
    
    


}
