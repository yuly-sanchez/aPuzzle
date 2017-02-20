package com.game.puzzle.highscore;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.database.UserDAO;
import com.game.database.UserDAO_DB;
import com.game.database.User;
import com.game.database.MyApplication;
import com.game.puzzle.main.MainMenuActivity;

import customize.button.R;

/**
 * Activity che inserisce nel database i punteggi e 
 * li visualizza in una tabella
 */
public class HighscoreActivity extends Activity {

	final int REQUESTCODE = 12345;
	private UserDAO dao;
	ListView contactList;
	User contact = null;
	ArrayList<User> values;
	ArrayAdapter<User> adapter;
	MenuItem resetScore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.highscore);
		dao = new UserDAO_DB();
		dao.open();
		contactList = (ListView) findViewById(R.id.listScore);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString("name");
			String score = extras.getString("score");
			contact = dao.insertUser(new User(name, score));
		}
		values = dao.getAllUser();
		adapter = new ItemsComplexAdapter(MyApplication.getAppContext(),
				R.layout.listrowlayout, values);
		contactList.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		dao.close();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent i = new Intent(HighscoreActivity.this, MainMenuActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	/**
	 * viene creato un option menu per cancellare i risultati
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int base = Menu.FIRST;
		resetScore = menu.add(base, 1, 1, "reset score");
		resetScore.setIcon(R.drawable.ic_menu_delete);
		return true;
	}

	/**
	 * menu che permette di cancellare tutti i punteggi
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			dao.deleteUser();
			dao.close();
			Toast.makeText(MyApplication.getAppContext(),
					"All record deleted!", Toast.LENGTH_SHORT).show();
		}
		values.removeAll(values);
		adapter.notifyDataSetChanged();
		return true;
	}
}

/**
 * Adapter che customizza la listView dove vengono visualizzati i punteggi
 */
class ItemsComplexAdapter extends ArrayAdapter<User> {

	private ArrayList<User> contactList;

	public ItemsComplexAdapter(Context context, int layoutId,
			ArrayList<User> values) {
		super(context, layoutId, values);
		this.contactList = values;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) MyApplication.getAppContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = vi.inflate(R.layout.listrowlayout, null);

		User contact = contactList.get(position);

		TextView nameContact = (TextView) view.findViewById(R.id.namecontact);
		nameContact.setText(contact.getName());

		TextView scoreContact = (TextView) view.findViewById(R.id.scorecontact);
		scoreContact.setText(contact.getScore());
		return view;
	}
}