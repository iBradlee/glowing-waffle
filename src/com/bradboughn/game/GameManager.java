
package com.bradboughn.game;

import com.bradboughn.lightnetengine.AbstractGame;
import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import com.bradboughn.lightnetengine.audio.SoundClip;
import com.bradboughn.lightnetengine.gfx.GameImageTile;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameManager extends AbstractGame {
    

    private GameImageTile anim;
    private GameImageTile ghost;
    private GameImageTile foot;
    private SoundClip laser;
    
    public GameManager() {
        anim = new GameImageTile("/test2.png", 32,32);
        ghost = new GameImageTile("/test2ghostarm.png",32,32);
        laser = new SoundClip("/audio/test.wav");
        laser.setVolume(-10);
      

        
    }
    float counter = 0;
    @Override
    public void update(GameContainer gc, float dt) {
         
        if (gc.getInput().isButton(MouseEvent.BUTTON3)) laser.loop();
        if (gc.getInput().isKey(KeyEvent.VK_A)) laser.stop();
        
        counter += dt*9;
        if (counter > 6) counter = 0;
    }
    int count = 0;
    int walk = 0;
    int ghostCount = 0;
    int numcheck = 0;
    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawFillRect(0, 0, 10, 10, 0xff0000ff);
        r.drawRect(5, 5, 10, 10, 0xffffff00);
   
        r.drawRect(walk + 29,0, 1,400, 0xffffffff);
        
        

    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();
    }
    
}
