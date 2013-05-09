package edu.easycalcetto.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class Profilo extends EasyCalcettoActivity {
	/** Called when the activity is first created. */

	private TextView field_Name;
	private TextView field_Surname;
	private TextView field_Age;
	private TextView field_Games;
	private RatingBar ratingBar;
	private ImageView avatar;
	private static String selectedImagePath = "null";
	private final static int INFO_DIALOG = 1;
	private static final int SELECT_PICTURE = 1;
	public static final String EXTRAKEY_ECUSER = "USER";

	private ECUser user;
	private Integer partiteGiocate = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilo);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Il tuo Profilo");
		field_Name = (TextView) findViewById(R.id.field_Name);
		field_Surname = (TextView) findViewById(R.id.field_Surname);
		field_Age = (TextView) findViewById(R.id.field_Age);
		field_Games = (TextView) findViewById(R.id.field_Games);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		avatar = (ImageView) findViewById(R.id.imageAvatar);
		ratingBar.setRating((float) 3.5);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (user.equals(getMyApplication().getOwner())) {
			// Used to put dark icons on light action bar
			boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
			menu.add(1, 1, 1, "Info")
					.setIcon(
							isLight ? R.drawable.info_buttondark
									: R.drawable.ic_action_help)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

			/*
			 * if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			 * menu.add(1, 2, 2, "More") .setIcon( isLight ?
			 * R.drawable.ic_action_overflow_black :
			 * R.drawable.ic_action_overflow)
			 * .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); }
			 */

			menu.add(2, 3, 2, "Cambia Foto").setIcon(R.drawable.edit_foto)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			menu.add(2, 4, 1, "Modifica Dati").setIcon(R.drawable.edit_data)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

			return true;
		} else
			return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getIntent().hasExtra(EXTRAKEY_ECUSER)
				&& getIntent().getExtras().containsKey(EXTRAKEY_ECUSER)) {
			user = (ECUser) getIntent().getExtras().get(EXTRAKEY_ECUSER);
		} else {
			user = getMyApplication().getOwner();
			avatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
					intentGallery.setType("image/*");
					intentGallery.setAction(Intent.ACTION_GET_CONTENT);
					// intentGallery.putExtra(EXTRAKEY_ECUSER, user);
					startActivityForResult(Intent.createChooser(intentGallery,
							"Seleziona Foto Profilo"), SELECT_PICTURE);
				}
			});
		}
		initializeFields();
		recuperaFoto();
	}

	private void initializeFields() {
		field_Name.setText(user.getName());
		field_Surname.setText(user.getSurname());
		field_Games.setText("");
		field_Age.setText(String.valueOf(user.getAge()));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case 1: // Info button
			showDialog(INFO_DIALOG);
			break;

		case 2:
			openOptionsMenu();
			break;
		case 3:
			Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
			intentGallery.setType("image/*");
			intentGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intentGallery,
					"Seleziona Foto Profilo"), SELECT_PICTURE);
			break;
		case 4:
			Intent modifyAccountIntent = new Intent(this, ModifyAccountInfoActivity.class);
			startActivity(modifyAccountIntent);
			break;
		case 6:
			Toast.makeText(this, "Cliccato: " + item.toString(),
					Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_DIALOG:
			return createInfoDialog();
		default:
			return null;
		}
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoProfiloDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoProfiloDialogMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				// user = data.getParcelableExtra(EXTRAKEY_ECUSER);
				selectedImagePath = getPath(selectedImageUri);
				try {
					cambiaFoto(selectedImagePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void cambiaFoto(String path) throws IOException {
		File imgFile = new File(path);
		if (imgFile.exists()) {

			File imageFile = new File(getMyApplication().getImagesDir(),
					user.generatePhotoFileName() + ".jpeg");
			ExifInterface imageExif = new ExifInterface(
					imageFile.getAbsolutePath());

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());

			avatar.setImageBitmap(myBitmap);

			avatar.setScaleType(ScaleType.FIT_XY);

			FileOutputStream fos = new FileOutputStream(imageFile);
			myBitmap.compress(CompressFormat.JPEG, 90, fos);
			fos.close();
			user.setPhotoName(imageFile.getName());
			uploadPhoto();
			getMyApplication().updateOwner(user);
		}
	}

	public void recuperaFoto() {
		File photoFile = new File(getMyApplication().getImagesDir(),
				user.getPhotoName());
		if (photoFile != null && photoFile.exists()) {
			avatar.setImageDrawable(Drawable.createFromPath(photoFile
					.toString()));
		} else {
			avatar.setImageResource(R.drawable.default_avatar);
		}
		avatar.setScaleType(ScaleType.FIT_XY);
	}

	@Override
	protected Handler getConnectionServiceHandler() {
		return serviceHandler;
	}

	private Handler serviceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg2) {
			case ECConnectionMessageConstants.RES_KIND_SUCCESS:
				switch (msg.arg1) {
				case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_CLOSED:
					Object[] oArr = msg.getData().getParcelableArray(
							ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);

					partiteGiocate = oArr.length;
					field_Games.setText("" + partiteGiocate);
					break;
				}

				break;
			case ECConnectionMessageConstants.RES_KIND_FAILURE:

				break;
			default:

				break;
			}
		}
	};

	private void uploadPhoto() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = null;
		msg = MessagesCreator
				.getUploadPhotoMessage(msnger, user.get_id(), new File(
						getMyApplication().getImagesDir(), user.getPhotoName())
						.getAbsolutePath());
		if (msg != null && messenger != null)
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	protected void onServiceConnected() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = null;
		msg = MessagesCreator.getGetClosedMatchesMessage(msnger, user.get_id());
		if (msg != null && messenger != null)
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	@Override
	protected void onServiceDisconnected() {

	}

}