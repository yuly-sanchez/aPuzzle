package com.game.database;

/**
 * Classe che definisce l'utente per la nostra applicazione 
 * (id, nome, punteggio) 
 */
public class User {
	private int id;
	private String name;
	private String score;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return name + "\t\t" + score;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public User(int id, String name, String score) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.score = score;
	}
	
	public User(String name, String score) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.score = score;
	}

}
