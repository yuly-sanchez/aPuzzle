package com.game.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe che crea e aggiorna la tabella "score" dove 
 * vengono salvati i nomi-punteggi degli utenti
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
	public static final String TABLE_SCORE = "score";
	public static final String COLUMN_ID = "_id";

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SCORE = "score";
	private static final String DATABASE_NAME = "score.db";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_SCORE
			+ "( " + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAME + " text not null, " + COLUMN_SCORE
			+ " text not null " + ");";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		database.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
		onCreate(db);
	}

}
