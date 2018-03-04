
package com.bradboughn.game;

import com.bradboughn.lightnetengine.AbstractGame;
import com.bradboughn.lightnetengine.GameContainer;
import com.bradboughn.lightnetengine.Renderer;
import java.util.ArrayList;

public class GameManager extends AbstractGame {
    
//<editor-fold defaultstate="collapsed" desc="Need to create class to put all testing objects/methods in">
    //OBJECTS FOR TESTING PURPOSES ONLY. THEY BELONG TO THE "initTestMap()" METHOD AT BOTTOM
    public static int[] testMap;
    public static int[] rgbArr1;
    public static int[] rgbArr2;
    public static int[] rgbArr3;
    public static int[] rgbArr4;
    public int r;
    public int g;
    public int b;
    public int rgb;
    public int color;
    //END OF OBJECTS FOR TESTING PURPOSES. DELETE AFTER USE
//</editor-fold>

    ArrayList<GameObject> objects = new ArrayList();
    
    public GameManager() {
        objects.add(new Player(3,3));
    }
    
    public static void main(String[] args) {
        GameContainer gc = new GameContainer(new GameManager());
        gc.start();

    }
    
    
    @Override
    public void update(GameContainer gc, float dt) {
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).update(gc, dt);
            if (objects.get(i).isDead()) {
                objects.remove(i);
                i--;
            }
        }
    }
    
//<editor-fold defaultstate="collapsed" desc="idea? comment">
    //I had a thought, that setting zDepth with the drawImage/ImageTile would be better suited. Although I could be completely wrong about that.
    //You could have two methods for each of the "drawImage"'s. The first method would not take in a zDepth, and would therefore set it to 0 when that method runs.
    //The seoncd, obviously, would take in a zDepth, and set it. That way you only specify when you have an actual image that needs to have it's depth set, instead
    //of setting it back to 0 after every time you render an alpha image.
//</editor-fold>
    @Override
    public void render(GameContainer gc, Renderer r) {        
        for (GameObject obj : objects) {
            obj.render(gc, r);
        }
    }
    
    
//<editor-fold defaultstate="collapsed" desc="testLightMap stuff. needs to be moved to own class">
    //<editor-fold defaultstate="collapsed" desc="HOW-TO INITIALIZE TEST LIGHT MAP">
    /*
    * To run Light Map Test:
    *
    *                           ADD/CHANGE THE FOLLOWING CODE
    *
    *-Add two parameters to Renderer.drawLineLight(). They will be "int rgbArray" and an "int iterator"
    *-Add two arguments to all 4 calls of drawLineLight(), inside Renderer.drawLight(). These will
    * be: "GameManager.rgbArr1" and "i". Change "rgbArr1" to "rgbArr2"..."rgbArr3"..."rgbArr4" for
    * every consecutive call, obviously. "i" will be used for all 4, as it will naturally iterate.
    *-In Renderer.drawLineLight(), on FIRST line, add: "GameManager.color = rgbArray[iterator]"
    *-In Renderer.drawLineLight(), DIRECTLY BEFORE setLightMap(), set lightColor = GameManager.color
    *-In Renderer.setLightMap(), at the END, add "GameManager.testMap[x+y*pixelW] = value"
    *
    *                           BE SURE TO TAKE THESE STEPS AS WELL
    *
    *-THE INT ARRAY "testMap" IS INITIALIZED BELOW WITHOUT REFERENCING THE ACTUAL WIDTH AND HEIGHT
    * VARIABLES. IF AND WHEN WE CHANGE THE GAME CONTAINER'S WIDTH/HEIGHT, WE WILL NEED TO CHANGE
    * THAT DOWN BELOW AS WELL.
    *-Stop Renderer.process() from manipulating pixel data, by commenting it out in GameContainer.
    *-You may want to again run Renderer.clear() after all other normal rendering processes, just
    * to double check you're not showing/using any data other than your Test Light Map.
    *-The Test Light Map REQUIRES everything to render normally FIRST (for now...), because it
    * currently relies on methods (such as setLightMap/drawLineLight/etc.) that I have not duplicated
    * and modified for use solely with this debugger/tester.
    * Run this initializer to set randomized color arrays, variables, and the testMap array itself.
    * AFTER ALL THIS IS DONE (I think i've gotten every step?) you can then run the "testLightMapRender()"
    * in this class' render function, to kick everything off!
    */
//</editor-fold>
//    public void initTestLightMap() {
//        rgbArr1 = new int[light.getDiameter() +5];
//        rgbArr2 = new int[light.getDiameter() + 5];
//        rgbArr3 = new int[light.getDiameter() + 5];
//        rgbArr4 = new int[light.getDiameter() + 5];
//        for (int i = 0; i < light.getDiameter(); i++) {
//
//        r = (int) (Math.random()*255);
//        g = (int) (Math.random()*255);
//        b = (int) (Math.random()*255);
//        rgb =  (r <<16) | (g<<8) | b;
//            rgbArr1[i] = rgb;
//            rgbArr2[i] = rgb;
//            rgbArr3[i] = rgb;
//            rgbArr4[i] = rgb;
//        }
//
//        color = 0;
//        testMap = new int[480*270];
//    }
//</editor-fold>

    
    
}
