
package com.bradboughn.game;

import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;

public abstract class GameObject {
    
    protected String tag;
    protected int posX, poxY;
    protected int width, height;
    protected boolean dead = false;
    
    public abstract void update(GameContainer gc, float dt);
    
    public abstract void render(GameContainer gc, Renderer r);

    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPoxY() {
        return poxY;
    }

    public void setPoxY(int poxY) {
        this.poxY = poxY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

}
