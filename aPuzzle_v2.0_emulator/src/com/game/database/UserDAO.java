package com.game.database;

import java.util.ArrayList;


/**
 * Interfaccia DAO per il database
 */
public interface UserDAO {

	public void open();

	public void close();

	public User insertUser(User contact);
	
	public void deleteUser();

	public ArrayList<User> getAllUser();

}
