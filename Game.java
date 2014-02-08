package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;


public class Game extends Panel implements KeyListener, Runnable{
       private Character[] chars;
       private int[] keybinds = { KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH };
       private boolean[] inputs;
       
       public static void main(String[] args){
    	   Game g = new Game();
       }

       public Game(){
    	   super();
    	   this.setSize(new Dimension(640, 480));
    	   this.addKeyListener(this);
    	   inputs = new boolean[4];
    	   for( int i = 0 ; i < 4 ; i++ )
    		   inputs[i] = false;
    	   chars = new Character[2];
    	   chars[0] = new Character((short)1);
    	   chars[1] = new Character((short)-1);
    	   JFrame jf = new JFrame("WE IN THERE");//
    	   jf.setSize(new Dimension(640, 480));
    	   jf.add(this);
    	   jf.setFocusable(false);
    	   jf.setVisible(true);
    	   jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	   this.run();
       }

       public void run(){
    	   while(true){
    		   long lop = System.currentTimeMillis();
    		   chars[0].step(inputs[0], inputs[1]);
    		   chars[1].step(inputs[3], inputs[2]);
    		   if( !(chars[0].hitboxOut && chars[0].clankable && chars[1].hitboxOut && chars[1].clankable) ){
    			   if( (chars[0].hitboxOut && !chars[1].crouched) || (chars[1].hitboxOut && !chars[0].crouched) ){
    				   System.exit(0);
    			   }
    		   }
    		   repaint();
    		   inputs[1]=false;
    		   inputs[2]=false;
    		   try {
    			   Thread.sleep(1000/60 - System.currentTimeMillis() + lop);
    		   } catch (InterruptedException e) {
    			   e.printStackTrace();
    		   }
    	   }
       }

       private void endGame(){
               
       }

       @Override
       public void keyPressed(KeyEvent e) {
               if( keybinds[0] == e.getKeyCode() ){
                       inputs[0] = true;
               }else if( keybinds[1] == e.getKeyCode() ){
                       inputs[1] = true;
               }else if( keybinds[2] == e.getKeyCode() ){
                       inputs[2] = true;
               }else if( keybinds[3] == e.getKeyCode() ){
                       inputs[3] = true;
               }
       }

       @Override
       public void keyReleased(KeyEvent e) {
               if( keybinds[0] == e.getKeyCode() ){
                       inputs[0] = false;
               }else if( keybinds[3] == e.getKeyCode() ){
                       inputs[3] = false;
               }
       }
       
       @Override
       public void paint(Graphics g){
    	   g.setColor(Color.BLACK);
    	   g.fillRect(0, 0, 640, 480);
    	   g.setColor(Color.WHITE);
    	   for (Character c: chars){
    		   c.render((Graphics2D)g);
    	   }
       }

       public void keyTyped(KeyEvent e) {        }
}