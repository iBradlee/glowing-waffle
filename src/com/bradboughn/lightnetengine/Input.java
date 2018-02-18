
package com.bradboughn.lightnetengine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/* NOTE TO LOOK INTO:
*
* Noticed that when rendering from GameManager, using "if mouse button 1 is pressed" to render, it tracks mouse position 
* even after the pointer leaves the window/canvas. However, if not using any if statement, and just simply rendering
* to current mouse coordinates, it will stop tracking mouse as soon as it hits the canvas/window threshold.
*
*/
public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener 
{
    private GameContainer gc;
    
    private final int NUM_KEYS = 256;
    private boolean[] keys = new boolean[NUM_KEYS];
    private boolean[] keysLast = new boolean[NUM_KEYS]; //data for previous frame's key input
    
    private final int NUM_MOUSE_BUTTONS = 6;
    private boolean[] mouseButtons = new boolean[NUM_MOUSE_BUTTONS];
    private boolean[] mouseButtonsLast = new boolean[NUM_MOUSE_BUTTONS]; //data for previous frame's mouse button input
    
    private int mouseX, mouseY;
    private double scrollWheel;
    
    public Input(GameContainer gc) {
        this.gc = gc;
        mouseX = 0;
        mouseY = 0;
        scrollWheel = 0;
        
        gc.getWindow().getCanvas().addKeyListener(this);
        gc.getWindow().getCanvas().addMouseListener(this);
        gc.getWindow().getCanvas().addMouseMotionListener(this);
        gc.getWindow().getCanvas().addMouseWheelListener(this);
    }
    
    public void update() {
        scrollWheel = 0;
        System.arraycopy(keys, 0, keysLast, 0, NUM_KEYS);
        System.arraycopy(mouseButtons, 0, mouseButtonsLast, 0, NUM_MOUSE_BUTTONS);
    }
    
     public boolean isKey(int keyCode) {
         return keys[keyCode];
     }
     
     public boolean isKeyUp(int keyCode) { //return true if key[keyCode] is currently false, and if it was true in previous frame/update (keysLast[keyCode])
         return !keys[keyCode] && keysLast[keyCode];
     }
     
     public boolean isKeyDown(int keyCode) {//return true if key IS up/pressed/true currently, but was down/not pressed/false in last frame/update
         return keys[keyCode] && !keysLast[keyCode];
     }
     
     public boolean isButton(int button) {
         return mouseButtons[button];
     }
     
     public boolean isButtonUp(int button) { //same logic as isKeyUp()
         return !mouseButtons[button] && mouseButtonsLast[button];
     }
     
     public boolean isButtonDown(int button) {
         return mouseButtons[button] && !mouseButtonsLast[button];
     }
     

    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtons[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtons[e.getButton()] = false;

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = (int) (e.getX() / gc.getScale()); //would like to look into the scaling on this
        mouseY = (int) (e.getY() / gc.getScale()); //to see what happens if we don't divide by our scale
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = (int) (e.getX() / gc.getScale()); //would like to look into the scaling on this
        mouseY = (int) (e.getY() / gc.getScale()); //to see what happens if we don't divide by our scale

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {//perhaps look into the .getPreciseWheelRotation() method. see how it works.
        scrollWheel = e.getPreciseWheelRotation();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public double getScrollWheel() {
        return scrollWheel;
    }

}
