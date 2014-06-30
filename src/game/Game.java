package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Game extends JPanel implements KeyListener, Runnable{
	private static final long serialVersionUID = -3489344695381731317L;
	
	private String title = "DragonPunch";
	private String controls = "P1: X and C         P4: . and /";
	private String text = "Press SPACE to fight, or ESC to rage quit.";
	private int[] keybinds = { KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH };
	private String font = "Battlefield Condensed";
	
	private boolean state = false;
	private boolean running = false;
	private boolean ready = false;
	private boolean go = false;
	private boolean win = false;
	
	private float fade = 1f;
	private Timer off;
	private int left = 0;
	private int right = 0;
	private String winner= "";
	
	private Timer repainter;
	private Character[] chars;
	private boolean[] inputs;
	
	private int scrHeight, scrWidth;
	
	private ImageHash imghash;

	public static void main(String[] args){
		new Game();
	}

	public Game(){
		super();
		this.imghash = ImageHash.IMG;
		this.setSize(new Dimension(640, 480));
		this.addKeyListener(this);
		this.setFocusable(true);
		this.setBackground(Color.DARK_GRAY);
		scrHeight=getHeight(); scrWidth=getWidth();
		off = new Timer(1000,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				go = false;
				off.stop();
			}
		});
		repainter = new Timer(20,new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				repaint();
			}			
		});
		init();
		JFrame jf = new JFrame("DragonPunch");
		jf.setSize(new Dimension(640, 480));
		jf.add(this);
		jf.setFocusable(false);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Thread t = new Thread(this);
		running = true;
		t.start();
		jf.setVisible(true);	
		
		addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				scrWidth=getWidth(); scrHeight=getHeight();
				repaint();
			}
			public void componentShown(ComponentEvent arg0) {}
		});
	}
	
	public void init(){
		inputs = new boolean[4];
		for( int i = 0 ; i < 4 ; i++ )
			inputs[i] = false;
		chars = new Character[2];
		chars[0] = new Character((short)1, scrWidth, scrHeight);
		chars[1] = new Character((short)-1, scrWidth, scrHeight);
	}
	
	public void run(){
		while(running){
			while(!state){}
			if(!running)
				System.exit(0);
			ready = true;
			win = false;
			init();
			repaint();
			repainter.start();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ready = false;
			go = true;
			fade = 1f;
			repainter.stop();
			off.start();
//			init();
//			repaint();
			while(state){
				long lop = System.currentTimeMillis();
				chars[0].step(inputs[0], inputs[1]);
				chars[1].step(inputs[3], inputs[2]);
				boolean b1 = (chars[0].hitboxOut && !chars[1].crouched);
				boolean b2 = (chars[1].hitboxOut && !chars[0].crouched);
				if (b1){
					chars[1].die();
				}
				if (b2){
					chars[0].die();
				}
				if(b1 || b2){
					if (b1 && b2){
						winner = "Natural Selection";
					} else {
						if (b1){
							left++;
							winner = "P1";
						} else if (b2){
							right++;
							winner = "P4";
						}
					}
					state = false;
					go = false;
					win = true;
				}
				this.validate();
				repaint();
				inputs[1]=false;
				inputs[2]=false;
				try {
					Thread.sleep(1000/60 - System.currentTimeMillis() + lop);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < 40; i++){
				long lop = System.currentTimeMillis();
				chars[0].step(inputs[0], inputs[1]);
				chars[1].step(inputs[3], inputs[2]);
				this.validate();
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
	}

	@Override	
	public void keyPressed(KeyEvent e) {
		if(!state && e.getKeyCode() == 32)			
			state = true;	
		if(!state && e.getKeyCode() == KeyEvent.VK_ESCAPE){
			running = false;
			state = true;
		}
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

	public void title(Graphics g){
		g.setFont(new Font(font,Font.BOLD,9*scrWidth/80));
		g.drawString(title,scrWidth/2-g.getFontMetrics().stringWidth(title)/2,scrHeight/3);
		g.drawLine(5*scrWidth/32,17*scrHeight/48,27*scrWidth/32,17*scrHeight/48);
		g.setFont(new Font(font,Font.BOLD,3*scrWidth/80));
		g.drawString(controls,scrWidth/2-g.getFontMetrics().stringWidth(controls)/2,scrHeight/2);
		g.drawString(text,scrWidth/2-g.getFontMetrics().stringWidth(text)/2,2*scrHeight/3);
	}
	
	@Override
	public void paint(Graphics g){
		Image i=createImage(getWidth(), getHeight());
		render((i.getGraphics()));
		g.drawImage(i,0,0,this);
	}
	
	public void render(Graphics g){
		super.paint(g);
		g.setFont(new Font(font,Font.BOLD,3*scrWidth/80));
		g.setColor(Color.WHITE);
		if(!state && !win)
			title(g);
		else{
			g.drawString(String.valueOf(left),scrWidth/32,scrHeight/24+scrWidth/160);
			g.drawString(String.valueOf(right),31*scrWidth/32-g.getFontMetrics().stringWidth(String.valueOf(right)),scrHeight/24+scrWidth/160);
			for (Character c: chars){
				c.setSpecs(scrWidth, scrHeight);
				c.render((Graphics2D)g, imghash);
			}
			g.setColor(Color.WHITE);
			g.drawString("Score",scrWidth/2-g.getFontMetrics().stringWidth("Score")/2,scrHeight/24+scrWidth/160);
			if(ready){
				g.drawString("READY?",scrWidth/2-g.getFontMetrics().stringWidth("READY?")/2,scrHeight/3);
				g.setColor(new Color(0,0,0,fade));
				g.fillRect(0,0,scrWidth,scrHeight);
				g.setColor(Color.WHITE);
				fade-=.01f;
				if(fade<0)
					fade = 0;
			}	
			if(go)
				g.drawString("GO!",scrWidth/2-g.getFontMetrics().stringWidth("GO!")/2,scrHeight/3);
			if(win){
				g.drawString(winner + " is the winner!",scrWidth/2-g.getFontMetrics().stringWidth(winner + " is the winner!")/2, scrHeight/3);
				g.setFont(new Font(font,Font.BOLD,3*scrWidth/80));
				g.drawString(text,scrWidth/2-g.getFontMetrics().stringWidth(text)/2,2*scrHeight/3);
			}	
		}
	}

	public void keyTyped(KeyEvent e) {}
}