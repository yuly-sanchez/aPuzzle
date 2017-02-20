package com.game.puzzle.arcadeplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.game.database.MyApplication;
import com.game.puzzle.highscore.HighscoreActivity;
import com.game.puzzle.main.MainMenuActivity;
import com.game.puzzle.other.SupportClass;

import customize.button.R;

/**
 * Classe che gestisce la modalità di gioco "arcade". Sceglie casualmente tre
 * immagini di difficoltà di risoluzione differente da tre set diversi, scompone
 * l'immagine, permette all'utente di compiere le sue mosse e verifica la
 * risoluzione del puzzle corrente calcolandone il punteggio. Quando vengono
 * completati gli n-livelli (tre) viene visualizzata una schermata di
 * "congratulazioni" dove all'utente viene mostrato il punteggio totalizzato e
 * viene invitato ad inserire il proprio nome.
 */
public class ArcadeGameActivity extends Activity {
	private final int NUM_IMG = 4;
	private final int MAX_LEVEL = 3;
	private Button showImage;
	private Button back;
	private Button nextLevel;
	private Button save;
	private EditText name;
	private TextView scoreTxt;
	private int index;
	private int score;
	private int level;
	private final int IMAGE_NUM = 9;
	private Bitmap[] orderedBitmapPieces;
	private LinearLayout[] containers;
	private ImageView[] imagesToDrag;
	private ImageView[] mixedImageView;
	private HashMap<LinearLayout, ImageView> orderedContainerImageMap;
	private HashMap<LinearLayout, ImageView> mixedContainerImageMap;
	private final int MAX_SCORE = 100;
	private final int POINTS_SLOT_PERIOD_S = 1;
	private final int POINTS_FOR_PERIOD = 3;
	private Chronometer chron;
	private Dialog dialog;
	private ArrayList<Integer> img_lev1;
	private ArrayList<Integer> img_lev2;
	private ArrayList<Integer> img_lev3;
	private final int NUM_IMG_GALLERY = 4;
	private int selectImgGallery;
	private int selectedImage;
	private String elapsedTime;
	private String array[];
	private SupportClass mt = new SupportClass();

	/**
	 * - legge i parametri che vengono passiti all'activity - gestisce il
	 * punteggio tatalizzato all'utente attraverso i tre livelli - crea i set di
	 * immagini e mostra all'utente l'immagine del livello corrente -
	 * inizializza il "tavolo" di gioco
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boardarcade);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			index = extras.getInt("index");
			level = extras.getInt("level");
			score = extras.getInt("score");
		}

		img_lev1 = new ArrayList<Integer>();
		img_lev1.add(R.drawable.lev1_01);
		img_lev1.add(R.drawable.lev1_02);
		img_lev1.add(R.drawable.lev1_03);
		img_lev1.add(R.drawable.lev1_04);

		img_lev2 = new ArrayList<Integer>();
		img_lev2.add(R.drawable.lev2_01);
		img_lev2.add(R.drawable.lev2_02);
		img_lev2.add(R.drawable.lev2_03);
		img_lev2.add(R.drawable.lev2_04);

		img_lev3 = new ArrayList<Integer>();
		img_lev3.add(R.drawable.lev3_01);
		img_lev3.add(R.drawable.lev3_02);
		img_lev3.add(R.drawable.lev3_03);
		img_lev3.add(R.drawable.lev3_04);

		Random randImg = new Random();
		selectImgGallery = randImg.nextInt(NUM_IMG_GALLERY);

		chron = (Chronometer) findViewById(R.id.chronometer1);

		if (level == 1) {
			orderedBitmapPieces = loadAndSplitImage(img_lev1
					.get(selectImgGallery));
			selectedImage = img_lev1.get(selectImgGallery);
		}
		if (level == 2) {
			orderedBitmapPieces = loadAndSplitImage(img_lev2
					.get(selectImgGallery));
			selectedImage = img_lev2.get(selectImgGallery);
		}
		if (level == 3) {
			orderedBitmapPieces = loadAndSplitImage(img_lev3
					.get(selectImgGallery));
			selectedImage = img_lev3.get(selectImgGallery);
		}

		mt.setComplete(false);
		imagesToDrag = loadAndSetImageViews();
		containers = loadAndSetContainer();
		orderedContainerImageMap = makeContainerImageAssociation();

		mixedImageView = new ImageView[IMAGE_NUM];
		mixedContainerImageMap = new HashMap<LinearLayout, ImageView>();
		mixesImagesView();

		showImage = (Button) findViewById(R.id.showImage);
		showImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog = new Dialog(ArcadeGameActivity.this);
				dialog.setContentView(R.layout.showimage);
				ImageView fullImage = (ImageView) dialog
						.findViewById(R.id.fullImage);
				fullImage.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), selectedImage));
				dialog.setTitle("image complete");
				dialog.setCancelable(true);

				back = (Button) dialog.findViewById(R.id.back);
				back.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.cancel();
					}
				});
				dialog.show();
			}
		});
		chron.start();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ArcadeGameActivity.this, MainMenuActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	/**
	 * mescola, in maniera casuale, le tessese del puzzle
	 */
	private void mixesImagesView() {
		int j = 0;
		int rnum = 0;
		int index = 0;
		int loops = 0;
		boolean[] usedNumbers = new boolean[IMAGE_NUM];
		Random rand = new Random();

		for (int i = 0; i < IMAGE_NUM; i++)
			usedNumbers[i] = false;

		while (loops < IMAGE_NUM) {

			rnum = rand.nextInt(IMAGE_NUM);
			if (!usedNumbers[rnum]) {
				mixedImageView[index] = imagesToDrag[rnum];
				usedNumbers[rnum] = true;
				index++;
			}

			loops++;
		}

		while ((index < IMAGE_NUM) && (j < IMAGE_NUM)) {
			if (!usedNumbers[j]) {
				mixedImageView[index] = imagesToDrag[j];
				usedNumbers[j] = true;
				index++;
			}
			j++;
		}

		for (int i = 0; i < IMAGE_NUM; i++)
			containers[i].removeAllViews();

		for (int i = 0; i < IMAGE_NUM; i++) {
			containers[i].addView(mixedImageView[i]);
			mixedContainerImageMap.put(containers[i], mixedImageView[i]);
		}

	}

	/**
	 * Classe che implementa l'interfaccia onTouchListener e gestisce l'immagine
	 * trascinata
	 */
	private final class MyTouchListener implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
						view);
				view.startDrag(data, shadowBuilder, view, 0);
				view.setVisibility(View.INVISIBLE);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Classe che implementa l'interfaccia onDragListener e quindi gestisce
	 * l'inversione dei tasselli, controlla i margini della drop area ed infine
	 * ad ogni movimento dei tasselli verifica attraverso una funzione specifica
	 * la risoluzione del puzzle
	 */
	class MyDragListener implements OnDragListener {
		Drawable enterShape = getResources().getDrawable(
				R.drawable.shape_droptarget);
		Drawable normalShape = getResources().getDrawable(R.drawable.shape);

		@Override
		public boolean onDrag(View v, DragEvent event) {
			ImageView tmp;
			// restituisce l'oggetto che sto muovendo
			View dragView = (View) event.getLocalState();
			// il padre dell'oggetto che sto muovendo
			ViewGroup originalContainer = (ViewGroup) dragView.getParent();
			// si fa restituire il padre attuale
			LinearLayout newContainer = (LinearLayout) v;

			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// Do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				v.setBackgroundDrawable(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				v.setBackgroundDrawable(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				// Se la casella di destinazione è diversa da quella di arrivo
				if (((LinearLayout) originalContainer) != newContainer) {
					// Ritorna l' ImageView associata al nuovo contenitore
					tmp = mixedContainerImageMap.get(newContainer);
					// Toglie l'ImageView che sto muovendo dal contenitore
					// originale
					originalContainer.removeView(dragView);
					// Toglie l'ImageView dal contaier attuale
					newContainer.removeView(tmp);
					// Il padre precedente prende l'oggetto del container
					// attuale
					originalContainer.addView(tmp);
					// L'oggetto viene aggiunto al padre attuale
					newContainer.addView(dragView);
					// E rende l'immagine visibile
					dragView.setVisibility(View.VISIBLE);

					mixedContainerImageMap.remove(originalContainer);
					mixedContainerImageMap.remove(newContainer);

					mixedContainerImageMap.put(
							(LinearLayout) originalContainer, tmp);
					mixedContainerImageMap.put(newContainer,
							(ImageView) dragView);

					if (checkVictory()) {
						mt.setComplete(true);
						chron.stop();
						score = score + estimateFinalScore();
						nextLevel(level);
					}
				} else {
					// se è uguale ritorna false
					if (checkVictory()) {
						mt.setComplete(true);
						chron.stop();
						score = score + estimateFinalScore();
						nextLevel(level);
					}
					return false;

				}
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				// Questo evento arriva a tutte le
				// view
				v.setBackgroundDrawable(normalShape);
				// Se si rilascia l'immagine su una view che non è una
				// drop-target allora rendi
				// l'immagine (prima resa invisibile) visibile
				if (!event.getResult())
					dragView.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
			return true;
		}
	}

	/**
	 * quando l'utente completa l'immagine lo indirizza al livello successivo o
	 * alla schermata di fine gioco
	 */
	public void nextLevel(final int levelNow) {
		if (levelNow == MAX_LEVEL) {
			dialog = new Dialog(ArcadeGameActivity.this);
			dialog.setContentView(R.layout.savescore);
			dialog.setTitle(R.string.congratulation);
			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					boolean disabled = false;
					if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
						disabled = true;
					}
					return disabled;
				}
			});
			save = (Button) dialog.findViewById(R.id.savescore);
			name = (EditText) dialog.findViewById(R.id.editname);
			name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						dialog.getWindow()
								.setSoftInputMode(
										WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
				}
			});
			scoreTxt = (TextView) dialog.findViewById(R.id.yourscore);
			scoreTxt.setText(String.valueOf(score));
			save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String txtN = name.getText().toString();
					if (txtN == null || txtN.equals("")) {
						Toast.makeText(MyApplication.getAppContext(),
								"Name required", Toast.LENGTH_SHORT).show();
					} else {
						if (txtN.length() > 7) {
							Toast.makeText(MyApplication.getAppContext(),
									"Name too long (max 15 ch)",
									Toast.LENGTH_SHORT).show();
						} else {
							Intent i = new Intent(ArcadeGameActivity.this,
									HighscoreActivity.class);
							i.putExtra("name", txtN);
							i.putExtra("score", String.valueOf(score));
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
						}
					}
				}
			});
			dialog.show();

		} else {
			dialog = new Dialog(ArcadeGameActivity.this);
			dialog.setContentView(R.layout.nextlevel);
			ImageView fullImage = (ImageView) dialog
					.findViewById(R.id.fullImage);
			fullImage.setImageBitmap(BitmapFactory.decodeResource(
					getResources(), selectedImage));
			dialog.setTitle(R.string.imagecomplete);
			// dialog.setCancelable(true);
			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					boolean disabled = false;
					if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
						disabled = true;
					}
					return disabled;
				}
			});
			
			nextLevel = (Button) dialog.findViewById(R.id.nextLevel);
			nextLevel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					level = levelNow + 1;
					if (index == NUM_IMG) {
						index = 0;
					} else {
						index = index + 1;
					}

					Intent i = new Intent(ArcadeGameActivity.this,
							ArcadeGameActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("index", index);
					i.putExtra("level", level);
					i.putExtra("score", score);
					startActivity(i);
				}
			});
			dialog.show();
		}
	}

	/**
	 * associa ogni sezione dell'immagine alla sua imageView nel layout
	 */
	public ImageView[] loadAndSetImageViews() {
		ImageView[] imgs = new ImageView[IMAGE_NUM];

		imgs[0] = (ImageView) findViewById(R.id.image1);
		imgs[1] = (ImageView) findViewById(R.id.image2);
		imgs[2] = (ImageView) findViewById(R.id.image3);
		imgs[3] = (ImageView) findViewById(R.id.image4);
		imgs[4] = (ImageView) findViewById(R.id.image5);
		imgs[5] = (ImageView) findViewById(R.id.image6);
		imgs[6] = (ImageView) findViewById(R.id.image7);
		imgs[7] = (ImageView) findViewById(R.id.image8);
		imgs[8] = (ImageView) findViewById(R.id.image9);

		for (int i = 0; i < IMAGE_NUM; i++) {
			imgs[i].setImageBitmap(orderedBitmapPieces[i]);
			imgs[i].setOnTouchListener(new MyTouchListener());
		}

		return imgs;
	}

	/**
	 * carica i "contenitori" delle immagini e ad ognuno di essi imposta il suo
	 * dragListener
	 */
	public LinearLayout[] loadAndSetContainer() {
		LinearLayout[] ll = new LinearLayout[IMAGE_NUM];
		ll[0] = (LinearLayout) findViewById(R.id.container1);
		ll[0].setOnDragListener(new MyDragListener());

		ll[1] = (LinearLayout) findViewById(R.id.container2);
		ll[1].setOnDragListener(new MyDragListener());

		ll[2] = (LinearLayout) findViewById(R.id.container3);
		ll[2].setOnDragListener(new MyDragListener());

		ll[3] = (LinearLayout) findViewById(R.id.container4);
		ll[3].setOnDragListener(new MyDragListener());

		ll[4] = (LinearLayout) findViewById(R.id.container5);
		ll[4].setOnDragListener(new MyDragListener());

		ll[5] = (LinearLayout) findViewById(R.id.container6);
		ll[5].setOnDragListener(new MyDragListener());

		ll[6] = (LinearLayout) findViewById(R.id.container7);
		ll[6].setOnDragListener(new MyDragListener());

		ll[7] = (LinearLayout) findViewById(R.id.container8);
		ll[7].setOnDragListener(new MyDragListener());

		ll[8] = (LinearLayout) findViewById(R.id.container9);
		ll[8].setOnDragListener(new MyDragListener());

		return ll;
	}

	/**
	 * carica l'immagine dalle risorse e chiama la funzione per dividerla in
	 * tasselli
	 */
	public Bitmap[] loadAndSplitImage(int imageID) {
		Bitmap sourceBitmap;
		Bitmap[] bmps;
		sourceBitmap = BitmapFactory.decodeResource(getResources(), imageID);
		bmps = splitBitmap(sourceBitmap);
		return bmps;
	}

	/**
	 * suddivide l'immagine in tessere
	 */
	public Bitmap[] splitBitmap(Bitmap picture) {
		// passare la bitmap da subsidiary
		Bitmap scaledBitmap = Bitmap
				.createScaledBitmap(picture, 300, 300, true);
		Bitmap[] imgs = new Bitmap[9];
		imgs[0] = Bitmap.createBitmap(scaledBitmap, 0, 0, 100, 100);
		imgs[1] = Bitmap.createBitmap(scaledBitmap, 100, 0, 100, 100);
		imgs[2] = Bitmap.createBitmap(scaledBitmap, 200, 0, 100, 100);
		imgs[3] = Bitmap.createBitmap(scaledBitmap, 0, 100, 100, 100);
		imgs[4] = Bitmap.createBitmap(scaledBitmap, 100, 100, 100, 100);
		imgs[5] = Bitmap.createBitmap(scaledBitmap, 200, 100, 100, 100);
		imgs[6] = Bitmap.createBitmap(scaledBitmap, 0, 200, 100, 100);
		imgs[7] = Bitmap.createBitmap(scaledBitmap, 100, 200, 100, 100);
		imgs[8] = Bitmap.createBitmap(scaledBitmap, 200, 200, 100, 100);
		return imgs;
	}

	/**
	 * restituisce una hashMap contenente la disposizione ordinata delle tessere
	 */
	public HashMap<LinearLayout, ImageView> makeContainerImageAssociation() {
		HashMap<LinearLayout, ImageView> map = new HashMap<LinearLayout, ImageView>();
		for (int i = 0; i < IMAGE_NUM; i++)
			map.put(containers[i], imagesToDrag[i]);
		return map;
	}

	/**
	 * controlla se il puzzle è risolto oppure no
	 */
	public boolean checkVictory() {

		return (orderedContainerImageMap.equals(mixedContainerImageMap));

	}

	/**
	 * calcola il punteggio di ogni livello
	 */
	public int estimateFinalScore() {
		int score = 0;
		int elapsedTimeSlots = 0;
		long elapsedSeconds = 0;
		elapsedSeconds = (SystemClock.elapsedRealtime() - chron.getBase()) / 1000;
		elapsedTimeSlots = ((int) elapsedSeconds) / POINTS_SLOT_PERIOD_S;
		score = MAX_SCORE - (elapsedTimeSlots * POINTS_FOR_PERIOD);
		if (score < 0)
			score = 0;
		// showToast("Second passed: "+elapsedMillis/1000);
		return score;
	}

	/**
	 * salva lo stato del puzzle quando l'applicazione viene messa in background
	 */
	@Override
	protected void onPause() {
		super.onPause();
		elapsedTime = (String) chron.getText();
		chron.stop();
		int stoppedMillisec = 0;
		array = elapsedTime.split(":");
		stoppedMillisec = Integer.parseInt(array[0]) * 60 * 1000
				+ Integer.parseInt(array[1]) * 1000;
		mt.setMyTime(stoppedMillisec);
		mt.setMyLevel(level);
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/**
	 * al "risveglio" dell'applicazione viene reimpostato lo stato
	 * precedentemente dalvato
	 */
	@Override
	protected void onResume() {
		super.onResume();
		boolean isComplete = mt.isComplete();
		int mylevel = mt.getMyLevel();
		if (isComplete == true && mylevel < MAX_LEVEL) {
			nextLevel(mylevel - 1);
		} else if (isComplete == true && mylevel == MAX_LEVEL) {
			nextLevel(mylevel);
		} else if (isComplete == false) {
			int n = mt.getMyTime();
			chron.setBase(SystemClock.elapsedRealtime() - n);
			chron.start();
		}
	}

}