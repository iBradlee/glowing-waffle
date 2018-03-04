
package com.bradboughn.game;

import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;

public class Player extends GameObject{
    
    public  Player(int posX, int posY) {
        this.tag = "player";
        this.posX = posX * 16;
        this.poxY = posY * 16;
        this.width = 16;
        this.height = 16;
    }

    @Override
    public void update(GameContainer gc, float dt) {
        
    }

    @Override
    public void render(GameContainer gc, Renderer r) {
        r.drawFillRect((int)posX + gc.getInput().getMouseX(), (int)posX + gc.getInput().getMouseY(), width + (int)gc.getInput().getScrollWheel(), height, 0xff00ff00);
    }

}
