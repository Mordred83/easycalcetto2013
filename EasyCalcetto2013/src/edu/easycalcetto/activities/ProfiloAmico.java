package edu.easycalcetto.activities;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_CLOSED;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;

public class ProfiloAmico extends EasyCalcettoActivity {
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
	private AdView adView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilo);
		//admob widget
	    adView = (AdView)findViewById(R.id.ad);
	    adView.loadAd(new AdRequest());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Profilo Amico");
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

			// menu.add(2, 4, 1, "Modifica Dati").setIcon(R.drawable.edit_data)
			// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

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
//			avatar.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
//					intentGallery.setType("image/*");
//					intentGallery.setAction(Intent.ACTION_GET_CONTENT);
//					// intentGallery.putExtra(EXTRAKEY_ECUSER, user);
//					startActivityForResult(Intent.createChooser(intentGallery,
//							"Seleziona Foto Profilo"), SELECT_PICTURE);
//				}
//			});
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
			Intent intentPreference = new Intent(getApplicationContext(),
					PreferenceProfilo.class);
			startActivity(intentPreference);
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
				// try {
				// //cambiaFoto(selectedImagePath);
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
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

	private void uploadPhoto() {
//		Messenger msnger = new Messenger(getConnectionServiceHandler());
//		Message msg = null;
//		msg = MessagesCreator
//				.getUploadPhotoMessage(msnger, user.get_id(), new File(
//						getMyApplication().getImagesDir(), user.getPhotoName())
//						.getAbsolutePath());
//		if (msg != null && messenger != null)
//			try {
//				messenger.send(msg);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}

	private void getClosedMatches() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC,
				FUNCDESCRIPTOR_GETMATCHES_CLOSED));
		params.add(new BasicNameValuePair("id", String.valueOf(user.get_id())));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(ProfiloAmico.this);
				pDialog.setMessage("Carico le partite terminate");
				pDialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Integer result) {
				pDialog.dismiss();
				super.onPostExecute(result);
			}

			@Override
			protected void onSuccessWithNoData() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onSuccess() {
				try {
					Object[] oArr = ECMatch.createFromJSONArray(getDataJArr());
					partiteGiocate = oArr.length;
					field_Games.setText("" + partiteGiocate);
				} catch (NumberFormatException e) {
					Log.e(LOGTAG, "number format exception", e);
					onGenericError();
				} catch (JSONException e) {
					Log.e(LOGTAG, "JSON malformed", e);
					onGenericError();
				}
			}

			@Override
			protected void onOpResultNULL() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onJArrNULL() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onGenericError() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onFailure() {
				// TODO: Auto-generated method stub
			}

			@Override
			protected void onDataNULL() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void onConnectionLost() {
				// TODO Auto-generated method stub

			}
		};

		task.execute(params.toArray(new BasicNameValuePair[] {}));

		// Messenger msnger = new Messenger(getConnectionServiceHandler());
		// Message msg = null;
		// msg = MessagesCreator.getGetClosedMatchesMessage(msnger,
		// user.get_id());
		// if (msg != null && messenger != null)
		// try {
		// messenger.send(msg);
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}