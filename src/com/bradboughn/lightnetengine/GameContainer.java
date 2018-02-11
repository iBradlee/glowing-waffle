
package com.bradboughn.lightnetengine;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
//import com.bradboughn.lightnetengine.game.GameManager;

public class GameContainer implements Runnable {
    
    private Thread thread;
    private Window window;
    private Renderer renderer;
    private Input input;
    private AbstractGame game;
    
    private boolean running = false;
    private final double UPDATE_CAP = 1.0/60.0; //Cap updates to 60 per second
    private int width = 320, height = 240;
    private float scale = 3f;
    private String title = "LightEngine v.01";

 
    public  GameContainer(AbstractGame game) {
        this.game = game;
    }
    
    public void start() {
        window = new Window(this);
        renderer = new Renderer(this);
        input = new Input(this);
        
        thread = new Thread(this);
        thread.run();
    }
    
    public void stop() {
        
    }
    
    @Override
    public void run() {
        running = true;
        
        boolean render = false;
        double firstTime = 0;
        double lastTime = System.nanoTime() / 1000000000.0; //Divided by a billion to get precise time in seconds, opposed to nanoseconds
        double passedTime = 0;
        double unprocessedTime = 0;
        
        double frameTime = 0;
        int frames = 0;
        int fps = 0;
        
        while (running) {
            render = false;
            firstTime = System.nanoTime()/1000000000.0;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            
            unprocessedTime += passedTime;
            frameTime += passedTime;
            while (unprocessedTime >= UPDATE_CAP) { //While loop, so that if Thread freezes, it will stay in while loop until it updates(runs thru) enough to make up for the time unaccounted for
                unprocessedTime -= UPDATE_CAP;
                render = true;
                
                game.update(this,(float) UPDATE_CAP);
                input.update();
                
                if (frameTime >= 1.0) { //if frameTime=1.0, we know fps is at 60, since it renders/updates 60 times per second, causing this code to run 1 time per second
                    frameTime = 0; 
                    fps = frames;
                    frames = 0;
                    System.out.println("FPS:" + fps);
                }
            }
            
            if (render) {
                renderer.clear();
                game.render(this, renderer);
                window.update(); //displays image
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e){
                    //TO-DO: Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        dispose();   
    }
    
    private void dispose() {
        
    }

    public Window getWindow() {
        return window;
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

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Input getInput() {
        return input;
    }

}
