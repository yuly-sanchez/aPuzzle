package com.game.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Implementazione dell'interfaccia UserDAO
 */
public class UserDAO_DB implements UserDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_SCORE };

	/**
	 * Apre la connessione al database
	 */
	@Override
	public void open() {
		// TODO Auto-generated method stub
		if (dbHelper == null)
			dbHelper = new MySQLiteHelper(MyApplication.getAppContext());
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Chiude la connessione al database
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
		dbHelper.close();
	}

	/**
	 * Inserisce un utente (nome, punteggio) nel database
	 */
	@Override
	public User insertUser(User contact) {
		// TODO Auto-generated method stub
		long insertId = database.insert(MySQLiteHelper.TABLE_SCORE, null,
				contactToValues(contact));
		// Now read from DB the inserted person and return it
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCORE, allColumns,
				MySQLiteHelper.COLUMN_ID + " = ?",
				new String[] { "" + insertId }, null, null, null);
		cursor.moveToFirst();
		User c = cursorToContact(cursor);
		cursor.close();
		return c;
	}

	/**
	 * Prende le inforazioni presenti nel database e restituisce 
	 * un oggetto utente
	 */
	private User cursorToContact(Cursor cursor) {
		// TODO Auto-generated method stub
		int id = cursor.getInt(0);
		String name = cursor.getString(1);
		String score = cursor.getString(2);
		return new User(id, name, score);
	}

	/**
	 * Trasforma un oggetto user in un oggetto contentValue 
	 * per essere inserito nel database
	 */
	private ContentValues contactToValues(User contact) {
		// TODO Auto-generated method stub
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_SCORE, contact.getScore());
		return values;
	}

	/**
	 * Cancella gli utenti dal database
	 */
	@Override
	public void deleteUser() {
		// TODO Auto-generated method stub
		open();
		database.delete(MySQLiteHelper.TABLE_SCORE, null, null);
		close();
	}

	/**
	 * Restituisce la lista di tutti gli oggetti utente 
	 * presenti nel database
	 */
	@Override
	public ArrayList<User> getAllUser() {
		// TODO Auto-generated method stub
		ArrayList<User> contactList = new ArrayList<User>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_SCORE, allColumns,
				null, null, null, null, MySQLiteHelper.COLUMN_SCORE);
		cursor.moveToLast();
		while (!cursor.isBeforeFirst()) {
			User contact = cursorToContact(cursor);
			contactList.add(contact);
			cursor.moveToPrevious();
		}
		cursor.close(); // Remember to always close the cursor!
		return contactList;

	}

}
