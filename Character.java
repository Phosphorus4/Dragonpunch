package game;

import java.awt.Graphics2D;

public class Character {
	public enum State {
		FREE (0), SQUAT (2), RISE (4), JAB (12), DP (30);
		private int frames;
		private State (int frames){
			this.frames = frames;
		}
		public int getAnimationLength(){
			return this.frames;
		}
	}
	public boolean crouched;
	public boolean hitboxOut;
	public boolean clankable;
	protected State state;
	private short direction;
	private int frameCounter;
	private int crouchCounter;
	public Character (short dir){
		this.state = State.FREE;
		this.direction = dir;
		this.crouched = false;
		this.hitboxOut = false;
		this.clankable = false;
		this.frameCounter = 0;
		this.crouchCounter = 61;
	}
	public void step(boolean crouch, boolean attack){
		System.out.println(crouch + "" + attack);//
		if (crouched){
			crouchCounter--;
		} else {
			crouchCounter = 61;
		}
		if (frameCounter > 0){
			switch (state){
			case JAB:
				if (frameCounter == state.getAnimationLength() - 1){
					clankable = true;
				}
				if (frameCounter == state.getAnimationLength() - 5){
					hitboxOut = true;
					System.out.println("P");//
				}
				if (frameCounter == state.getAnimationLength() - 8){
					hitboxOut = false;
					clankable = false;
				}
				break;
			case DP:
				if (frameCounter == state.getAnimationLength() - 7)
					hitboxOut = true;
				System.out.println("P");//
				if (frameCounter == state.getAnimationLength() - 10)
					crouched = false;
				if (frameCounter == state.getAnimationLength() - 18)
					hitboxOut = false;
				break;
			}
			frameCounter--;
			return;
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
	public void render(Graphics2D g2d4me){
		g2d4me.fillRect(305 - direction * 30, crouched?410:360, 30, crouched?30:80);
		int frCt = state.getAnimationLength() - frameCounter;
		boolean right = direction < 0;
		switch (state) {
		case JAB:
			if (frCt >= 2 && frCt <= 5)
				g2d4me.fillRect(320 - direction * 15 - ((right)?(int)(30 * ((frCt - 2.0) / 3)):0), 390, (int)(30 * ((frCt - 2.0) / 3)), 10);
			if (frCt > 5 && frCt <= 8)
				g2d4me.fillRect(320 - direction * 15 - ((right)?50:0), 390, 50, 10);
			if (frCt > 8)
				g2d4me.fillRect(320 - direction * 15 - ((right)?(int)(30 * ((12.0 - frCt) / 3)):0), 390, (int)(30 * ((12.0 - frCt) / 3)), 10);
			break;
		case DP:
			if (frCt >= 3 && frCt <= 18)
				g2d4me.fillOval(320 + direction * 4 * (frCt - 5), 420 - 5 * (frCt - 3), 20, 20);
			break;
		}
	}
	private void inputSquat(){
		crouched = true;
		changeState(State.SQUAT);
		System.out.println("D");//
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
