
package com.bradboughn.lightnetengine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;               //IMPORTANT POSSIBLE FIXES NEED TO BE MADE BELOW!!!!
import java.awt.image.BufferStrategy;   // figure out how to use JPanel or whatevs instead of JFrame, and figure out why exactly
import java.awt.image.BufferedImage;
import java.awt.Canvas; //Bookmarked StackOverflow answer stating NOT to use Canvas with SWING(JFrame), instead to try with JPanel or JComponent
import javax.swing.JFrame; //SWING COMPONENT

public class Window {
    
    private JFrame frame;
    private BufferedImage bufferImage;
    private Canvas canvas;
    private BufferStrategy bufferStrat;
    private Graphics graphics;
    
    public Window(GameContainer gc) {
        bufferImage = new BufferedImage(gc.getWidth(),gc.getHeight(),BufferedImage.TYPE_INT_RGB);
        
        canvas = new Canvas();
        Dimension d = new Dimension((int)(gc.getWidth() * gc.getScale()), (int)(gc.getHeight() * gc.getScale()));
        canvas.setPreferredSize(d);
        canvas.setMaximumSize(d);
        canvas.setMinimumSize(d);
        
        frame = new JFrame(gc.getTitle());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout()); //Need more reading of doc's for these couple classes+methods
        frame.add(canvas,BorderLayout.CENTER);
        frame.pack(); //this sets the frame's size, using canvas (and? BorderLayout?) needs more research into JFrame,canvas,BorderLayout docs.
        frame.setLocationRelativeTo(null);//center frame
        frame.setResizable(false);
        frame.setVisible(true);
        
        canvas.createBufferStrategy(2); //would like to look into these three lines further, to see how data is transferred to each instance/class
        bufferStrat = canvas.getBufferStrategy();
        graphics = bufferStrat.getDrawGraphics();
    }
    
    public void update() {
        graphics.drawImage(bufferImage, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
        bufferStrat.show();
    }
        
        

}
