package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Character {
	public final int MAX_CROUCH_DURATION = 120;
	public enum State {
		FREE (0, 0, 0), SQUAT (7, 0, 0), RISE (9, 0, 0), JAB (27, 12, 4), DP (55, 20, 7), DEATH (18, 0, 0);
		protected int frames;
		protected int startup;
		protected int active;
		private State (int frames, int startup, int active){
			this.frames = frames;
			this.startup = startup;
			this.active = active;
		}
		public int getAnimationLength(){
			return this.frames;
		}
		//0: startup, 1: active, 2: endlag
		public int getAnimationStatus(int currentFrame){
			if (this.equals(State.FREE) || this.equals(State.SQUAT) || this.equals(State.RISE)){
				return 2;
			}
			if (currentFrame < startup){
				return 0;
			} else {
				if (currentFrame <= startup + active){
					return 1;
				}
				return 2;
			}
		}
	}
	public boolean crouched;
	public boolean hitboxOut;
	public boolean clankable;
	protected State state;
	private short direction;
	private int frameCounter;
	private int crouchCounter;
	private int idleFrameCounter;
	public Character (short dir){
		this.state = State.FREE;
		this.direction = dir;
		this.crouched = false;
		this.hitboxOut = false;
		this.clankable = false;
		this.frameCounter = 0;
		this.idleFrameCounter = 4;
		this.crouchCounter = MAX_CROUCH_DURATION;
	}
	public void step(boolean crouch, boolean attack){
		if (crouched){
			if (!state.equals(State.DEATH)){
				crouchCounter--;
			}
		} else {
			crouchCounter = MAX_CROUCH_DURATION;
			if (!state.equals(State.FREE)){
				idleFrameCounter = 4;
			}
		}
		int frCtr = state.getAnimationLength() - frameCounter;
		if (frameCounter > 0){
			switch (state){
			case JAB:
				if (frCtr == state.startup / 2){
					clankable = true;
				}
				if (frCtr == state.startup){
					hitboxOut = true;
				}
				if (frCtr == state.startup + state.active){
					hitboxOut = false;
					clankable = false;
				}
				break;
			case DP:
				if (frCtr == state.startup)
					hitboxOut = true;
				if (frCtr == state.startup + (state.active)/3)
					crouched = false;
				if (frCtr == state.startup + state.active)
					hitboxOut = false;
				break;
			}
			frameCounter--;
			return;
		}
		if (state.equals(State.DEATH)){
			return;
		}
		if (!crouched && state.equals(State.FREE)){
			if (idleFrameCounter >= 40){
				idleFrameCounter = 4;
			} else {
				idleFrameCounter++;
			}
		}
		changeState(State.FREE);
		if (crouch && crouchCounter > 0){
			if (!crouched){
				inputSquat();
			} else {
				if (attack){
					inputDP();
				}
			}
		} else {
			if (crouched){
				inputRise();
			} else {
				if (attack){
					inputJab();
				}
			}
		}
	}
	public void render(Graphics2D g2d4me, ImageHash imghash){
		int frCt = state.getAnimationLength() - frameCounter + 1;
		
		BufferedImage img = null;
		
		switch (state) {
		case FREE:
			if (crouched){
				img = imghash.getImage("crouch8");
			} else {
				img = imghash.getImage("idle" + (int)(idleFrameCounter/4));
			}
			break;
		case SQUAT:
			img = imghash.getImage("crouch" + frCt);
			break;
		case RISE:
			img = imghash.getImage("stand" + frCt);
			break;
		case JAB:
			img = imghash.getImage("jab" + frCt);
			break;
		case DP:
			img = imghash.getImage("shoryuken" + frCt);
			break;
		case DEATH:
			img = imghash.getImage("death" + ((int)(frCt/2) + 1));
			break;
		}
		
		if (img == null){
			System.err.println("Frame Error: Contact CT | EMP | Ledge");
			return;
		}
		
		switch (state){
		case DP: g2d4me.drawImage(img, 320 - 186 * direction, -52, 320 + 70 * direction, 440, 0, 0, img.getWidth(), img.getHeight(), null); break;
		case DEATH: g2d4me.drawImage(img, 320 - 442 * direction, 184, 320 + 70 * direction, 440, 0, 0, img.getWidth(), img.getHeight(), null); break;
		default: g2d4me.drawImage(img, 320 - 186 * direction, 184, 320 + 70 * direction, 440, 0, 0, img.getWidth(), img.getHeight(), null); break;
		}
		
//		int frCt = state.getAnimationLength() - frameCounter;
		
//		if((state.getAnimationStatus(frCt) == 1)){
//			g2d4me.setColor(Color.RED);
//		}else if(state == State.FREE){
//			g2d4me.setColor(Color.WHITE);
//		}else{
//			g2d4me.setColor(Color.BLUE);
//		}
		
//		g2d4me.fillRect(305 - direction * 30, crouched?410:360, 30, crouched?30:80);
//		
//		boolean right = direction < 0;
//		switch (state) {
//		case JAB:
//			switch(state.getAnimationStatus(frCt)){
//			case 0: g2d4me.fillRect(320 - direction * 15 - ((right)?(int)(30 * ((frCt + 0.0) / state.startup)):0), 390, (int)(30 * ((frCt + 0.0) / state.startup)), 10); break;
//			case 1: g2d4me.fillRect(320 - direction * 15 - ((right)?50:0), 390, 50, 10); break;
//			case 2: g2d4me.fillRect(320 - direction * 15 - ((right)?(int)(30 * ((frameCounter + 0.0) / (state.frames - state.active - state.startup))):0), 390, (int)(30 * ((frameCounter + 0.0) / (state.frames - state.active - state.startup))), 10); break;
//			}
//			break;
//		case DP:
//			if (frCt >= state.startup/2 && frCt <= state.startup + state.active)
//				g2d4me.fillOval(310 + direction * (int)(20 * ((frCt - state.startup + 0.0)/(state.active))), 420 - (int) (75 * ((frCt + 0.0)/(state.startup + state.active))), 20, 20);
//			break;
//		}
		
	}
	public void die(){
		crouched = true;
		crouchCounter = MAX_CROUCH_DURATION;
		changeState(State.DEATH);
	}
	private void inputSquat(){
		crouched = true;
		changeState(State.SQUAT);
	}
	private void inputRise(){
		crouched = false;
		changeState(State.RISE);
	}
	private void inputJab(){
		changeState(State.JAB);
	}
	private void inputDP(){
		changeState(State.DP);
	}
	private void changeState(State newState){
		this.state = newState;
		this.frameCounter = newState.getAnimationLength();
	}
}
