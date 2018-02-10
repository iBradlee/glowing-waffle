
package com.bradboughn.lightnetengine;

public abstract class AbstractGame {
    
    public abstract void update(GameContainer gc, float dt); //dt = Delta Time = firstTime - lastTime = "passedTime"
    public abstract void render(GameContainer gc, Renderer r);

}
