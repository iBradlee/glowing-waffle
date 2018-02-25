
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
        
        //BufferStrat returns Graphics obj, stored in "graphics". Canvas returns BufferStrat, stored in "bufferStrat". Canvas creates BufferStrat. Canvas was added to "frame"
        canvas.createBufferStrategy(2); //create the double buffer strategy from canvas
        bufferStrat = canvas.getBufferStrategy(); //applies the strategy into bufferStrat instance
        graphics = bufferStrat.getDrawGraphics(); //applies the strategy to graphics 
    }
    
    public void update() {
        graphics.drawImage(bufferImage, 0, 0, canvas.getWidth(), canvas.getHeight(), null); //drawing gfx, using current buffImg., from graphics, which (right above) uses our buffStrat, which in turn is using our canvas
        bufferStrat.show();//Makes next available buffer visible to our "frame", bc "bufferStrat" is instantiated thru canvas, which was added to "frame".
        
//        System.out.println(bufferStrat.contentsLost()); THIS IS BEGINNING OF AN IDEA ON HOW TO FIX BUFFERSTRAT LOSING ALL PIXEL DATA WHEN DRAGGING WINDOW THRU MULTIPLE MONITORS
//        if (bufferStrat.contentsLost()) ;
    }

    public BufferedImage getBufferImage() {
        return bufferImage;
    }

    public Canvas getCanvas() {
        return canvas;
    }
    
    
        
        

}
