
package com.bradboughn.game;

import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;

public abstract class GameObject {
    
    protected String tag;
    //Coordinates
    protected float posX, posY;
    protected int tileX, tileY;
    protected float offsetX, offsetY;
    //Object's size properties
    protected int width, height;
    //Other GameObject Status'
    protected float speed;
    protected int direction;
    protected boolean dead = false;
    protected float fallDistance;
    protected float mass;
    protected float jump;
   
    
    public abstract void update(GameContainer gc, GameManager gm, float dt);
    
    public abstract void render(GameContainer gc, Renderer r);

    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
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

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

}
