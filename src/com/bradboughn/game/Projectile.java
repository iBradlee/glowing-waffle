
package com.bradboughn.game;

import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;

public class Projectile extends GameObject
{
    
    private float lastOffsetX;
    private float lastOffsetY;
    private float currentOffsetX;
    private float currentOffsetY;
    private int lastTileX;
    private int lastTileY;
    
    private boolean deleted = false;
    
    //I think adding a GameObject as a parameter would be nice. That way you can get the current
    //location of the gameobject easily, by just making a projectile starting at the current gameobject
    //location, yeah?
    
//    public Projectile(float posX, float posY, int direction) 
//    {
//        this.posX = posX;
//        this.posY = posY;
//        this.direction = direction;
//        this.tileX = (int) posX / GameManager.TILE_SIZE;
//        this.tileY = (int) posY / GameManager.TILE_SIZE;
//        this.offsetX = ((int) posX % GameManager.TILE_SIZE) - GameManager.TILE_SIZE/2;
//        this.offsetY = ((int) posY % GameManager.TILE_SIZE) - GameManager.TILE_SIZE/2;
//    }
    
    public Projectile(GameObject gObj) 
    {
        this.width = 4;
        this.height = 4;
        this.speed = 200;
        this.direction = gObj.direction;
        this.mass = 10;
        
        this.posX = gObj.posX;
        this.posY = gObj.posY;

        this.tileX = gObj.tileX;
        this.tileY = gObj.tileY;
        this.offsetX = gObj.offsetX + (gObj.width/2 - this.width/2); 
        this.offsetY = gObj.offsetY+ (gObj.height/2 - this.height/2);
//        System.out.println("\noffsetX, offsetY = " + "[" + offsetX + ", " + offsetY + "]");
//        System.out.println("tileX = " + tileX + "\ntileY = " + tileY);
    }


    int counter = 0;
    @Override
    public void update(GameContainer gc, GameManager gm, float dt)
    {
        if (deleted)
        {
            this.dead = true;
        }
        
        lastOffsetX = offsetX;
        lastOffsetY = offsetY;
        /*
        NOTE TO FUTURE SELF:
        
        TO DO WITH: DETECTING PROJECTILE COLLISION
        
        Get rid of current projectile collision detection, which is based on the button being pressed
        that changes the direction (this is in Player class. "W" sets dir=0, "A" sets dir=1, etc.)
        And only looks for the next collidable tile based on current button press.
        
        I think a MUCH better way, would be to just use the below switch statement to get the change
        in velocity/ projectile position, and THAT'S IT. THE ONLY CHANGE TO SWITCH STATEMENT would be
        to add a "lastValue" variable to each one, which stores the previous movement along the X and/or
        Y axis. So each time speed is changed in any switch "case", we set current X or Y coordinate
        (based on direction moving, store either X or Y) to an individual "lastValue" for each axis
        THEN we update the speed as usual. So now we have current "offsetX", for example, and a 
        "lastXvalue" value which stored the previous location of X axis.
        
        Then, below the switch, and after we've calculated the change in movement, and stored the
        last known X or Y coordinate, we can take our "lastXvalue" and "lastYvalue" and compare it to 
        the current "posX" and "posY" value of projectile, to find out if we have moved up, down, left
        or right. From there we can decide what kind of collision detection to run. If X had increased
        in value, we would then check for collision along the [tileX +1, tileY] position, and if Y had
        decreased in value, we know it's also travelling upwards, so we also check for collision 
        along the [tileX, tileY-1] position. The only thing I need to test/figure out, is to see if
        we are moving along two axis, as described in the previous sentance, if we would need to check
        for [tileX +1, tileY-1] instead of checking both independantly. I'M EXCITED TO PUT IT INTO PLAY
        THOUGH!!!!!!!!!
        */
        
        /*
        I need to add in code to not allow bullets to clip through top/bottom of tiles.
        I need to take account for the fact that bullets are tracked from top left corner
        When fixing clipping from topside, maybe you could check if we're travelling on the x axis,
        and have a colliding tile on tileX+1, and tileY+1 (FORWARD ONE, AND DOWN ONE) and THEN check
        if the offsetY is between like 13.1 (TILE_SIZE - height + 0.1) and 15 (TILE_SIZE). This effectively
        would check if the projectile would be clipping the top side of the tile, because you have to
        be on the Y tile above the clipped block, and be travelling through the object's space (13.1 - 15)
        */
        
        //Gravity/FallDistance
        fallDistance += dt * mass;
        offsetY += fallDistance;
        
        switch (direction)
        {   
        //Upwards Movement
        case 0: 
        {
            if (gm.getCollisionTile(tileX, tileY -1))
            {
                if (offsetY > 0)
                {
                    offsetY -= dt * speed;
                    if (offsetY <= 0)
                    {
                        offsetY = 0;
                        deleted = true;
                    }
                }
                else
                {
                    offsetY = 0;
                    deleted = true;
                }
            }
            else
            {
            offsetY -= dt * speed;
            }
        } 
        break;
        
        //Right Movement
        case 1:
        {
           if (gm.getCollisionTile(tileX + 1, tileY))
           {
               if (offsetX + width < GameManager.TILE_SIZE) 
               {
                    offsetX += dt * speed;
                    if (offsetX + width >= GameManager.TILE_SIZE)
                    {
                        
                        offsetX = GameManager.TILE_SIZE - width;
                        deleted = true;
                    }
               }
               else
               {       
                   offsetX = GameManager.TILE_SIZE - width;
                   deleted = true;
               }
               
           }
           else
           {
               offsetX += dt * speed;
           }
        }
        break;
        //Downward Movement
        case 2: 
        {
            if (gm.getCollisionTile(tileX, tileY +1))
            {
                if (offsetY + width< GameManager.TILE_SIZE)
                {
                    offsetY += dt * speed;
                    if (offsetY + width >= GameManager.TILE_SIZE)
                    {
                        offsetY = GameManager.TILE_SIZE - this.width;
                        deleted = true;
                    }
                }
                else
                {
                    offsetY = GameManager.TILE_SIZE - this.width;
                    deleted = true;
                }
            }
            else
            {

            offsetY += dt * speed;
            }
        } 
        break;
        //Left Movement
        case 3: offsetX -= dt * speed; break;
        }
        
        //Final Position

        if (offsetY > GameManager.TILE_SIZE) 
        {
            tileY++;
            offsetY -= GameManager.TILE_SIZE;
        }
        if (offsetY < 0) 
        {
            tileY--; 
            offsetY += GameManager.TILE_SIZE;
        }
        if (offsetX > GameManager.TILE_SIZE) 
        {
            tileX++;
            offsetX -= GameManager.TILE_SIZE;
        }
        if (offsetX < 0) 
        {
            tileX--;
            offsetX += GameManager.TILE_SIZE;
        }
        
        //MY WAY that works 99.9%
//        if (offsetY >= GameManager.TILE_SIZE/2) 
//        {
//            tileY++;
//            offsetY -= GameManager.TILE_SIZE;
//        }
//        if (offsetY <= -GameManager.TILE_SIZE/2 ) 
//        {
//            tileY--; 
//            offsetY += GameManager.TILE_SIZE;
//        }
//        if (offsetX >= GameManager.TILE_SIZE/2) 
//        {
//            tileX++;
//            offsetX -= GameManager.TILE_SIZE;
//        }
//        if (offsetX <= -GameManager.TILE_SIZE/2 ) 
//        {
//            tileX--;
//            offsetX += GameManager.TILE_SIZE;
//        }
   
//       System.out.println("\ntileX, tileY = " + "[" + tileX + ", " + tileY + "]");
//       System.out.println("\noffsetX, offsetY = " + "[" + offsetX + ", " + offsetY + "]");
//       
//        
//        if (gm.getCollisionTile(tileX, tileY)) 
//        {
//            this.dead = true;
//        }

        System.out.println("end offsetY = " + offsetY);
        posX = tileX * GameManager.TILE_SIZE + offsetX ;
        posY = tileY * GameManager.TILE_SIZE + offsetY ;
        
        currentOffsetX = offsetX;
        currentOffsetY = offsetY;
        
        //can change this if you want to track projectiles further off 
        checkOutOfBounds(posX, posY, gc);
    }

    @Override
    public void render(GameContainer gc, Renderer r)
    {
        r.drawFillRect((int)posX, (int)posY, width, height, 0xffff000f);
    }
    
    public void checkOutOfBounds(float posX, float posY, GameContainer gc)
    {
        if (posX > gc.getWidth() + this.width || posX < 0 - this.width) 
            this.dead = true;
        if (posY > gc.getHeight() + this.height || posY < 0 - this.height) 
            this.dead = true;
    }
    
    public void checkDirectionalMovement(float dt) 
    {
       
        

    }
    
    public void checkCollision(GameManager gm)
    {
        //Checking if moving upwards
        if (lastOffsetY > currentOffsetY)
        {
            if (gm.getCollisionTile(lastTileX, lastTileY -1))
            {
                if (currentOffsetY <= 0)
                {
                    offsetX = 0;
                    deleted = true;
                    tileY = lastTileY;
                }
            }
        }
        
        //Checking if moving right
        if (lastOffsetX < currentOffsetX)
        {
            if (gm.getCollisionTile(lastTileX +1, lastTileY))
            {
                if (currentOffsetX >= GameManager.TILE_SIZE  - width)
                {
                    offsetX = GameManager.TILE_SIZE - width;
                    deleted = true;
                    tileX = lastTileX;
                }
            }
        }
        
        //Checking if moving downwards
        if (lastOffsetY > currentOffsetY)
        {
            if (gm.getCollisionTile(lastTileX, lastTileY +1))
            {
                if (currentOffsetY <= GameManager.TILE_SIZE - height)
                {
                    offsetX = GameManager.TILE_SIZE - height;
                    deleted = true;
                    tileY = lastTileY;                    
                }
            }
        }
        
        //Checking if moving left
        if (lastOffsetX > currentOffsetX)
        {
            if (gm.getCollisionTile(lastTileX -1, lastTileY))
            {
                if (currentOffsetX <= 0)
                {
                    offsetX = 0;
                    deleted = true;
                    tileX = lastTileX;
                }
            }
        }
            
    }
}
