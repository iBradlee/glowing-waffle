
package com.bradboughn.game;

import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import com.sun.glass.events.KeyEvent;
import java.awt.event.MouseEvent;

public class Player extends GameObject{
         //further in development, I'd like to put these first 4 vars into the GameObject instead of here
        //keeps track of tile coordinates
        //keeps track of pixels in between tiles. from 1-16
    

    private boolean onGround = false;


    
    public Player(int posX, int posY) 
    {
        this.speed = 100;
        
        this.tag = "player";
        this.tileX = posX;
        this.tileY = posY;
        this.offsetX = 0;
        this.offsetY = 0;
        this.posX = posX * GameManager.TILE_SIZE;
        this.posY = posY * GameManager.TILE_SIZE;
        this.width = GameManager.TILE_SIZE;
        this.height = GameManager.TILE_SIZE;

        this.mass = 10;
        this.jump = -240;
        
        
    }

    @Override
    public void update(GameContainer gc, GameManager gm, float dt) 
    {
        //Directional Movement/Directional tracking
        if (gc.getInput().isKey(KeyEvent.VK_W))
        {
            direction = 0;
        }
        
        if (gc.getInput().isKey(KeyEvent.VK_A)) 
        {   
            direction = 3;
            
            //check next tile in direction you're going
            if (gm.getCollisionTile(tileX - 1, tileY) || gm.getCollisionTile(tileX -1, tileY + (int)Math.signum((int)offsetY))) 
            {  
                //makes sure we can still move as intended
                if (offsetX > 0) 
                {   
                    //if so, we move as usual
                    offsetX -= dt * speed;   
                    //woah, you moved in the direction of a collision object! Did you move too far? Let's check
                    if (offsetX < 0) 
                    {
                        //If yes, go back to point we know it should stop you at
                        offsetX = 0;
                    }                                   
                } 
                else 
                {                     
                    offsetX = 0;
                }
            } 
            else 
            {
                offsetX -= dt * speed;
            }
        }
        
        if (gc.getInput().isKey(KeyEvent.VK_S)) 
        {
            direction = 2;
        }
        
        if (gc.getInput().isKey(KeyEvent.VK_D)) 
        {
            direction = 1;
            if (gm.getCollisionTile(tileX + 1, tileY) || gm.getCollisionTile(tileX + 1, tileY + (int)Math.signum((int)offsetY))) 
            {
                if (offsetX < 0)
                {
                    offsetX += dt * speed;
                    if (offsetX > 0) 
                    {
                        offsetX = 0;
                    }
                } 
                else 
                {
                    offsetX = 0;
                } 
            } 
            else 
            {
                offsetX += dt * speed;
            }
        }
        //---End of Left and Right Movement---
        
        //Beginning of Jump and Gravity
        fallDistance += dt * mass;

        if (gc.getInput().isKey(KeyEvent.VK_SPACE) && onGround ) 
        {
            onGround = false;
            fallDistance = dt *  jump;

        }
        
        offsetY += fallDistance;
        
        //if we're moving upwards
        if (fallDistance < 0)
        {
            if((gm.getCollisionTile(tileX , tileY - 1) || gm.getCollisionTile(tileX + (int)Math.signum((int)offsetX), tileY - 1)) && offsetY < 0) 
            {
                fallDistance = 0;
                offsetY = 0; 

            }
        }
        //if we're falling downwards, check for collision underneath you (technically falling when standing still)
        if (fallDistance > 0) 
        {
                        //ground collision & better edge detection/better tile selection when near edges
            if((gm.getCollisionTile(tileX , tileY + 1) || gm.getCollisionTile(tileX + (int)Math.signum((int)offsetX), tileY + 1)) && offsetY > 0) 
            {
                fallDistance = 0;
                offsetY = 0; 
                onGround = true;
            }
            
        }
        //---End of Jump and Gravity---
        
        
        //Final Position  && Updating Tile/Offsets 
        if (offsetY > GameManager.TILE_SIZE/2) 
        {
            tileY++;
            offsetY -= GameManager.TILE_SIZE;
        }
        if (offsetY < -GameManager.TILE_SIZE/2) 
        {
            tileY--;
            offsetY += GameManager.TILE_SIZE;
        }
        if (offsetX > GameManager.TILE_SIZE/2) 
        {
            tileX++;
            offsetX -= GameManager.TILE_SIZE;
        }
        if (offsetX < -GameManager.TILE_SIZE/2) 
        {
            tileX--;
            offsetX += GameManager.TILE_SIZE;
        }
        
        posX = tileX * GameManager.TILE_SIZE + offsetX;
        posY = tileY * GameManager.TILE_SIZE + offsetY;
        //---End of Final Position---
        
        //Shooting/Projectiles
        if (gc.getInput().isButton(MouseEvent.BUTTON1))
        {
            
            gm.addObject(new Projectile(this));
        }
       
        
                //reset placement for testing
        if (gc.getInput().isKey(KeyEvent.VK_P))
        {
            System.out.println("reset");
            tileX = tileY = 2;
            offsetX = offsetY = 0;
            posX = tileX * GameManager.TILE_SIZE + offsetX;
            posY = tileY * GameManager.TILE_SIZE + offsetY;
        }
    }

    @Override
    public void render(GameContainer gc, Renderer r) 
    {
        r.drawFillRect((int)posX, (int)posY , width, height, 0xff00ff00); // TRY        RENDERING IN NON FILLED RECT!!!!!!!!!
        r.drawRect((int)posX + width/2 -1, (int)posY + height/2 -1 , 2, 2, 0xffffffff);
    }

    public float getSpeed() 
    {
        return speed;
    }

    public void setSpeed(float speed) 
    {
        this.speed = speed;
    }

}
