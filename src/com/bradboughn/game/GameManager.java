
package com.bradboughn.game;

import com.bradboughn.lightnetengine.AbstractGame;
import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import com.bradboughn.lightnetengine.audio.SoundClip;
import com.bradboughn.lightnetengine.gfx.GameImage;
import com.bradboughn.lightnetengine.gfx.GameImageTile;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameManager extends AbstractGame {
    

    private GameImage image;
    private GameImage redDotAlpha;
    private GameImageTile beltShake;

    private GameImageTile alphaTest;
    private SoundClip laser;
    
    public GameManager() {
      
        image = new GameImage("/beltshake.png");
        //image.setAlpha(true);
        redDotAlpha = new GameImage("/redDotAlpha.png");
       redDotAlpha.setAlpha(true);
        beltShake = new GameImageTile("/beltshake2.png",32,32);
        alphaTest = new GameImageTile("/alphatest.png", 32, 32);
        alphaTest.setAlpha(true);
        laser = new SoundClip("/audio/test.wav");
        laser.setVolume(-30);
      

        
    }
    float counter = 0;
    @Override
    public void update(GameContainer gc, float dt) {
         
        if (gc.getInput().isButton(MouseEvent.BUTTON3)) laser.loop();
        if (gc.getInput().isKey(KeyEvent.VK_A)) laser.stop();
        
        counter += dt*9;
        if (counter > 6) counter = 0;
    }
    
    //I had a thought, that setting zDepth with the drawImage/ImageTile would be better suited. Although I could be completely wrong about that.
    //You could have two methods for each of the "drawImage"'s. The first method would not take in a zDepth, and would therefore set it to 0 when that method runs.
    //The seoncd, obviously, would take in a zDepth, and set it. That way you only specify when you have an actual image that needs to have it's depth set, instead
    //of setting it back to 0 after every time you render an alpha image.
    @Override
    public void render(GameContainer gc, Renderer r) {
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                r.setLightMap(x , y, image.getPixels()[x + y * image.getWidth()]);
            }
        }
        r.setzDepth(3);
        r.drawImage(redDotAlpha, 15, 15);
        
        
        r.setzDepth(1);
        r.drawImageTile(beltShake, gc.getInput().getMouseX(), gc.getInput().getMouseY(), 0, 0);
        r.drawImageTile(beltShake, 28, 28, 0, 0);
        
 
        

        


        
        

    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();

    }
    
}
