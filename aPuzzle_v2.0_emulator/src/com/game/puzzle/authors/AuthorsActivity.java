package com.game.puzzle.authors;

import com.game.puzzle.main.MainMenuActivity;

import customize.button.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * activity che mostra gli autori dell'applicazione
 */
public class AuthorsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authors);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent i = new Intent(AuthorsActivity.this, MainMenuActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
}