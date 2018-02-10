
package com.bradboughn.game;

import com.bradboughn.lightnetengine.AbstractGame;
import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import com.bradboughn.lightnetengine.gfx.GameImage;
import java.awt.event.KeyEvent;

public class GameManager extends AbstractGame {
    
    public GameManager() {
        
    }

    @Override
    public void update(GameContainer gc, float dt) {
        
    }

    @Override
    public void render(GameContainer gc, Renderer r) {

    }

    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();
    }
    
}
