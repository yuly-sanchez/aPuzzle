package com.game.puzzle.singleplay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.game.puzzle.main.MainMenuActivity;
import com.game.puzzle.other.SupportClass;

import customize.button.R;

/**
 * SingleGameActivity rappresenta la modalità di gioco "single play" in cui l'utente può decidere 
 * se ricomporre una singola immagine caricata dalla gallery o dalla fotocamera del proprio dispositivo
 * 
 * @author Zambotti Nicola, Pugliese Guido, Sanchez Yuly
 */
public class SingleGameActivity extends Activity {
	private static final int IMAGEREQUESTCODE = 8242008;
	private static final int CAMERAREQUESTCODE = 9232501;
	private static final int IMAGE_NUM = 9;
	private static Button showImage, back, mainMenu;
	private static String source;
	private static Bitmap[] orderedBitmapPieces;
	private static LinearLayout[] containers;
	private static ImageView[] imagesToDrag, mixedImageView;
	private static HashMap<LinearLayout, ImageView> orderedContainerImageMap,
			mixedContainerImageMap;
	private static Dialog dialog;
	private static Bitmap bitmap;
	private static SupportClass mt = new SupportClass();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boardsingle);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			source = extras.getString("source");
		}
		if (source.equals("gallery")) {
			selectImageFromGallery();
		}
		if (source.equals("camera")) {
			selectImageFromCamera();
		}

		showImage = (Button) findViewById(R.id.showImage);
		showImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog = new Dialog(SingleGameActivity.this);
				dialog.setContentView(R.layout.showimage);
				ImageView fullImage = (ImageView) dialog
						.findViewById(R.id.fullImage);
				fullImage.setImageBitmap(bitmap);
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
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(SingleGameActivity.this, MainMenuActivity.class);
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
	 * Classe che implementa l'interfaccia onTouchListener e gestisce l'immagine trascinata
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
	 * Classe che implementa l'interfaccia onDragListener e
	 * quindi gestisce l'inversione dei tasselli, controlla 
	 * i margini della drop area ed infine ad ogni movimento 
	 * dei tasselli verifica attraverso una funzione specifica 
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
						returnMainMenu();
					}
				} else {
					// se è uguale ritorna false
					if (checkVictory()) {
						mt.setComplete(true);
						returnMainMenu();
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
	 * completato il puzzle rimanda l'utente al menu principale
	 */
	public void returnMainMenu() {
		// TODO Auto-generated method stub

		dialog = new Dialog(SingleGameActivity.this);
		dialog.setContentView(R.layout.mainmenu);
		ImageView fullImage = (ImageView) dialog.findViewById(R.id.fullImage);
		fullImage.setImageBitmap(bitmap);
		dialog.setTitle(R.string.imagecomplete);
		// dialog.setCancelable(true);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				boolean disabled = false;
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
					disabled = true;
				}
				return disabled;
			}
		});

		mainMenu = (Button) dialog.findViewById(R.id.mainMenu);
		mainMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(SingleGameActivity.this,
						MainMenuActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
		dialog.show();
	}

	/**
	 * associa ogni sezione dell'immagine alla sua imageView 
	 * nel layout  
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
	 * carica i "contenitori" delle immagini e ad ognuno di essi 
	 * imposta il suo dragListener
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
	 * carica l'immagine dalle risorse e chiama la funzione 
	 * per dividerla in tasselli
	 */
	public Bitmap[] loadAndSplitImage(Bitmap bitmap) {
		Bitmap[] bmps;
		bmps = splitBitmap(bitmap);
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
	 * richiama l'activity per caricare l'immagine dalla gallery
	 */
	private void selectImageFromGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, IMAGEREQUESTCODE);
	}

	/**
	 * richiama l'activity per caricare l'immagine dalla fotocamera
	 */
	private void selectImageFromCamera() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERAREQUESTCODE);
	}

	/**
	 * gestisce il risultato che viene ritornato quando si seleziona 
	 * un'immagine dalla gallery o dalla fotocamera procedendo all'inizializzazione
	 * del "tavolo" di gioco
	 */
	@Override
	protected final void onActivityResult(final int requestCode,
			final int resultCode, final Intent i) {
		super.onActivityResult(requestCode, resultCode, i);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IMAGEREQUESTCODE:
				Uri imageUriGallery = i.getData();

				try {
					InputStream is = getContentResolver().openInputStream(
							imageUriGallery);
					bitmap = BitmapFactory.decodeStream(is);
					is.close();
				} catch (FileNotFoundException e) {
//					showDialog(DIALOG_PICASA_ERROR_ID);
				} catch (IOException e) {
					e.printStackTrace();
					finish();
				} catch (IllegalArgumentException e) {
//					showDialog(DIALOG_PICASA_ERROR_ID);
				}

				break;
			case CAMERAREQUESTCODE:
				bitmap = (Bitmap) i.getExtras().get("data");
				break;
			}
			orderedBitmapPieces = loadAndSplitImage(bitmap);
			imagesToDrag = loadAndSetImageViews();
			containers = loadAndSetContainer();
			orderedContainerImageMap = makeContainerImageAssociation();
			mixedImageView = new ImageView[IMAGE_NUM];
			mixedContainerImageMap = new HashMap<LinearLayout, ImageView>();
			mixesImagesView();// end switch
		} // end if
		else if (resultCode == RESULT_CANCELED) {
			Intent i2 = new Intent(this, MainMenuActivity.class);
			i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i2);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isComplete = mt.isComplete();
		if (isComplete == true) {
			returnMainMenu();
		}
	}

}