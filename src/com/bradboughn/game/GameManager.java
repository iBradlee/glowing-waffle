
package com.bradboughn.game;

import com.bradboughn.lightnetengine.AbstractGame;
import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import com.bradboughn.lightnetengine.gfx.GameImage;
import com.bradboughn.lightnetengine.gfx.Light;
import java.util.ArrayList;

public class GameManager extends AbstractGame {
    
    public static final int TILE_SIZE = 16;
    private boolean[] collision;
    private int levelWidth, levelHeight;
    private ArrayList<GameObject> objects = new ArrayList();
    Light light;
    
    
    public GameManager() 
    {
        objects.add(new Player(2,2));
        light = new Light(200, 0xffffffff);
        loadLevel("/levels/testLevel.png");
        
        
    }
    
    public static void main(String[] args) 
    {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();

    }
    
    @Override
    public void init(GameContainer gc)
    {
        gc.getRenderer().setAmbientColor(0xff696969);
    }

    float counter = 0;
    @Override
    public void update(GameContainer gc, float dt) 
    {
        counter += 30 * dt;
        if (counter > 400) counter = 0;
        for (int i = 0; i < objects.size(); i++) 
        {
            objects.get(i).update(gc, this, dt);
            if (objects.get(i).isDead()) 
            {
                objects.remove(i);
                i--;
            }
        }
    } 

    @Override
    public void render(GameContainer gc, Renderer r) 
    {    r.setAmbientColor(0xff2a2a2a);
        
        r.drawLight(light, (5 * GameManager.TILE_SIZE) + (int)counter, 10 * GameManager.TILE_SIZE);

        for (int y = 0; y < levelHeight; y++) 
        {
            for (int x = 0; x < levelWidth; x++) 
            {
              if (collision[x + y * levelWidth] == true) 
              {
                  r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, 0xff0f0fff);
              } else 
              {
                  r.drawFillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, 0xfffaff00);
              }
            }
        }
        for (GameObject obj : objects) 
        {
            obj.render(gc, r);
        }
    }
    
    public void loadLevel(String path)
    {
        GameImage levelImage = new GameImage(path);
        levelWidth = levelImage.getWidth();
        levelHeight = levelImage.getHeight();
        collision = new boolean[levelWidth * levelHeight];

        for (int y = 0; y < levelHeight; y++) 
        {
            for (int x = 0; x < levelWidth; x++) 
            {
                if (levelImage.getPixels()[x + y*levelWidth] == 0xff000000)
                {
                    collision[x + y*levelWidth] = true;
                } else 
                {
                    collision[x + y*levelWidth] = false;
                }
            }
        }
    }
        //for now will just set everything outside level as solid
    public boolean getCollisionTile(int x, int y)
    {
        if (x < 0 || x >= levelWidth || y < 0 || y >= levelHeight) return true;
        return collision [x + y*levelWidth];
    }
    
    public void addObject (GameObject obj) 
    {
        objects.add(obj);
    }

    
    

    
    
}
