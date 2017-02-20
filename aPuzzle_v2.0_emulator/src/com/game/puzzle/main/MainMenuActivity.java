package com.game.puzzle.main;

import com.game.puzzle.arcadeplay.ArcadeGameActivity;
import com.game.puzzle.authors.AuthorsActivity;
import com.game.puzzle.highscore.HighscoreActivity;
import com.game.puzzle.other.IconContextMenu;
import com.game.puzzle.other.IconContextMenu.IconContextMenuOnClickListener;
import com.game.puzzle.singleplay.SingleGameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import customize.button.R;

/**
 * L'activity "MainMenuActivity" compone il menu principale 
 * dell'applicazione e ad ogni pulsante attribuisce il suo clickListener
 */
public class MainMenuActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	Dialog dialog;
	int index = 0;
	int score = 0;
	int level = 1;

	Button startSingle;
	Button startArcade;
	Button high;
	Button help;
	Button about;
	Button exit;

	Animation a1;
	Animation a2;
	Animation a3;
	Animation a4;
	Animation a5;
	Animation a6;
	Animation t1;
	Animation t2;

	TextView title;
	TextView titleShadow;

	private final int CONTEXT_MENU_ID = 1;
	private IconContextMenu iconContextMenu = null;

	private final int MENU_ITEM_1_ACTION = 1;
	private final int MENU_ITEM_2_ACTION = 2;

	/**
	 * @author Zambotti Nicola
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		t1 = AnimationUtils.loadAnimation(this, R.anim.animtitle);
		t2 = AnimationUtils.loadAnimation(this, R.anim.animtitle);
		title = (TextView) findViewById(R.id.title);
		titleShadow = (TextView) findViewById(R.id.titleShadow);
		title.startAnimation(t1);
		titleShadow.startAnimation(t2);

		a1 = AnimationUtils.loadAnimation(this, R.anim.animbuttonsingle);
		startSingle = (Button) findViewById(R.id.buttonSingle);
		startSingle.setOnClickListener(this);
		startSingle.startAnimation(a1);

		a2 = AnimationUtils.loadAnimation(this, R.anim.animbuttonarcade);
		startArcade = (Button) findViewById(R.id.buttonArcade);
		startArcade.setOnClickListener(this);
		startArcade.startAnimation(a2);

		a3 = AnimationUtils.loadAnimation(this, R.anim.animbuttonhighscore);
		high = (Button) findViewById(R.id.buttonHighscore);
		high.setOnClickListener(this);
		high.startAnimation(a3);

		a4 = AnimationUtils.loadAnimation(this, R.anim.animbuttonhelp);
		help = (Button) findViewById(R.id.buttonHelp);
		help.setOnClickListener(this);
		help.startAnimation(a4);

		a5 = AnimationUtils.loadAnimation(this, R.anim.animbuttonabout);
		about = (Button) findViewById(R.id.buttonAbout);
		about.setOnClickListener(this);
		about.startAnimation(a5);

		a6 = AnimationUtils.loadAnimation(this, R.anim.animbuttonexit);
		exit = (Button) findViewById(R.id.buttonExit);
		exit.setOnClickListener(this);
		exit.startAnimation(a6);
	}

	/**
	 * @author Zambotti Nicola
	 */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.buttonSingle:
			/**
			 * viene creato il menu di scelta per selezionare l'immagine dalla 
			 * gallery o dalla fotocamera
			 */
			Resources res = getResources();
			iconContextMenu = new IconContextMenu(this, CONTEXT_MENU_ID);
			iconContextMenu.addItem(res, "gallery", R.drawable.ic_menu_gallery,
					MENU_ITEM_1_ACTION);
			iconContextMenu.addItem(res, "camera", R.drawable.ic_menu_camera,
					MENU_ITEM_2_ACTION);

			iconContextMenu
					.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
						public void onClick(int menuId) {
							switch (menuId) {
							case MENU_ITEM_1_ACTION:
								Intent i = new Intent(MainMenuActivity.this,
										SingleGameActivity.class);
								i.putExtra("source", "gallery");
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
								break;
							case MENU_ITEM_2_ACTION:
								Intent i2 = new Intent(MainMenuActivity.this,
										SingleGameActivity.class);
								i2.putExtra("source", "camera");
								i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i2);
								break;
							}
						}
					});
			showDialog(CONTEXT_MENU_ID);

			break;
		case R.id.buttonArcade:
			Intent i2 = new Intent(this, ArcadeGameActivity.class);
			i2.putExtra("index", index);
			i2.putExtra("score", score);
			i2.putExtra("level", level);
			i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i2);
			break;
		case R.id.buttonHighscore:
			Intent i3 = new Intent(this, HighscoreActivity.class);
			i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i3);
			break;
		case R.id.buttonHelp:

			/**
			 * 
			 */
			dialog = new Dialog(MainMenuActivity.this);
			dialog.setContentView(R.layout.help);
			final TextView tv = (TextView) dialog.findViewById(R.id.help_desc);
			tv.setText(R.string.help_desc_en);
			Button b_it = (Button) dialog.findViewById(R.id.button_it);
			Button b_en = (Button) dialog.findViewById(R.id.button_en);
			Button b_fr = (Button) dialog.findViewById(R.id.button_fr);
			Button bt = (Button) dialog.findViewById(R.id.back);
			b_it.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tv.setText(R.string.help_desc_it);
				}
			});
			b_en.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tv.setText(R.string.help_desc_en);
				}
			});
			b_fr.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tv.setText(R.string.help_desc_fr);
				}
			});

			bt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			dialog.show();
			break;
		case R.id.buttonAbout:
			Intent i5 = new Intent(this, AuthorsActivity.class);
			i5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i5);
			break;
		case R.id.buttonExit:
			exitDialog();
			break;
		}

	}

	@Override
	public void onBackPressed() {
		exitDialog();
	}

	/**
	 * crea la finestra di dialogo per uscire dall'applicazione
	 */
	private void exitDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.puzzle_red2);
		builder.setTitle("aPuzzle");
		builder.setMessage("Quit the game?");
		builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
				return;
			}
		});

		builder.setNegativeButton("no", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		builder.create();
		builder.show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == CONTEXT_MENU_ID) {
			return iconContextMenu.createMenu("select image from..");

		}
		return super.onCreateDialog(id);
	}
}
