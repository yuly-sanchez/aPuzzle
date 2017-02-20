package com.game.puzzle.other;

/**
 * La classe SupportClass costituisce a tutti gli effetti un javabean per memorizzare delle 
 * informazioni per gestire la messa in background l'applicazione.
 */
public class SupportClass {
	private int myTime;
	private boolean complete;
	private int myLevel;

	public int getMyLevel() {
		return myLevel;
	}

	public void setMyLevel(int myLevel) {
		this.myLevel = myLevel;
	}

	public int getMyTime() {
		return myTime;
	}

	public void setMyTime(int myTime) {
		this.myTime = myTime;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}
