package edu.easycalcetto.activities;

//import static edu.easycalcetto.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static edu.easycalcetto.CommonUtilities.EXTRA_MESSAGE;
import static edu.easycalcetto.CommonUtilities.SENDER_ID;
import static edu.easycalcetto.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.ServerUtilities;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECConnectionService;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.JSONParser;

public class MenuActivity extends EasyCalcettoActivity {
	/** Called when the activity is first created. */

	private ImageView nuovaPartitaButton;
	private ImageView partiteButton;
	private ImageView amiciButton;
	private ImageView profiloButton;
	private final static int EXIT_DIALOG = 0;
	private final static int INFO_DIALOG = 1;
	private static final int SEND_DIALOG = 2;
	private static final String TAG = "MenuActivity";
	private ListImageMatch[] dialogImages;
	private AsyncTask<Void, Void, Void> mRegisterTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(EXTRA_MESSAGE))
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), getIntent().getStringExtra(EXTRA_MESSAGE), Toast.LENGTH_SHORT).show();
				}
			}, 1000);
		// unregistrationToGCM();
		registrationToGCM();
		ECUser user;
		if ((user = getMyApplication().getOwner()) != null) {
			Log.d("madonna in croce!", user.generatePhotoFileName());
		} else {
			Log.e("madonna in croce!", "utente null");
		}

		// This is a workaround for http://b.android.com/15340 from
		// http://stackoverflow.com/a/5852198/132047
		/*
		 * if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		 * BitmapDrawable bg =
		 * (BitmapDrawable)getResources().getDrawable(R.drawable
		 * .dark_black_grey_stripes); bg.setTileModeXY(TileMode.REPEAT,
		 * TileMode.REPEAT); getSupportActionBar().setBackgroundDrawable(bg); }
		 */
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Menu");
		loadListImageSendEC();
		nuovaPartitaButton = (ImageView) findViewById(R.id.buttonNew);
		nuovaPartitaButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentCreaPartita = new Intent(getApplicationContext(),
						CreaPartita.class);
				startActivity(intentCreaPartita);
			}
		});

		partiteButton = (ImageView) findViewById(R.id.buttonPartite);
		partiteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentPartite = new Intent(getApplicationContext(),
						Partite.class);
				startActivity(intentPartite);
			}
		});

		amiciButton = (ImageView) findViewById(R.id.buttonFriends);
		amiciButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentAmici = new Intent(getApplicationContext(),
						Amici.class);
				startActivity(intentAmici);
			}
		});

		profiloButton = (ImageView) findViewById(R.id.buttonProfilo);
		profiloButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentProfilo = new Intent(getApplicationContext(),
						Profilo.class);
				startActivity(intentProfilo);
			}
		});

		new UpdateFriendshipsTask().execute(getContactsPhoneNumbers());
	}

	private void registrationToGCM() {
		checkNotNull(SERVER_URL, "SERVER_URL");
		checkNotNull(SENDER_ID, "SENDER_ID");
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		// registerReceiver(mHandleMessageReceiver,
		// new IntentFilter(DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			Log.v(TAG, "Registering");
			GCMRegistrar.register(this, SENDER_ID);

		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.v(TAG, "Application Already registered");
				ServerUtilities.register(this, regId, getMyApplication()
						.getOwner().get_id());
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered = ServerUtilities.register(context,
								regId, getMyApplication().getOwner().get_id());
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	private void unregistrationToGCM() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		menu.add(1, 1, 1, "Info")
				.setIcon(
						isLight ? R.drawable.info_buttondark
								: R.drawable.ic_action_help)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
		// if(ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey()){
		// if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

		/*
		 * menu.add(1, 2, 2, "More") .setIcon( isLight ?
		 * R.drawable.ic_action_overflow_black : R.drawable.ic_action_overflow)
		 * .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 */
		// }else{
		// menu.add(1,2,2,"More").setIcon(isLight ?
		// R.drawable.ic_action_overflow_black : R.drawable.ic_action_overflow)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// }
		// }

		menu.add(2, 3, 1, "Suggerisci EasyCalcetto").setIcon(R.drawable.share)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(2, 4, 2, "Mi piace EasyCalcetto").setIcon(R.drawable.rating)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		menu.add(2, 5, 3, "Info").setIcon(R.drawable.info_buttondark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		// menu.add(2, 6, 4, "Impostazioni").setIcon(R.drawable.settings)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		menu.add(2, 7, 5, "Esci").setIcon(R.drawable.ic_lock_power_off)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showDialog(EXIT_DIALOG);
			break;

		case 1: // Info button
			showDialog(INFO_DIALOG);
			break;

		case 2:
			openOptionsMenu();
			break;
		case 3:
			showDialog(SEND_DIALOG);
			break;
		case 4:
			Toast.makeText(this, "Grazie per aver votato :D ",
					Toast.LENGTH_SHORT).show();
			break;
		case 5:
			showDialog(INFO_DIALOG);
			break;
		case 6:
			Toast.makeText(this, "Cliccato: " + item.toString(),
					Toast.LENGTH_SHORT).show();
			break;
		case 7:
			showDialog(EXIT_DIALOG);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case EXIT_DIALOG:
			return createExitDialog();
		case INFO_DIALOG:
			return createInfoDialog();
		case SEND_DIALOG:
			return DialogOptionSendItem();
		default:
			return null;
		}
	}

	private final Dialog createExitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.exitDialogTitle);
		builder.setIcon(R.drawable.warning);
		builder.setMessage(R.string.exitDialogMSG);
		builder.setPositiveButton(R.string.yes_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		builder.setNegativeButton(R.string.no_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(EXIT_DIALOG);
					}
				});
		return builder.create();
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	public final Dialog DialogOptionSendItem() {
		// define the list adapter with the choices
		ListAdapter adapter = (ListAdapter) new ListImageMatchAdapter(this,
				dialogImages);

		final AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle(R.string.optionSendItemDialog);
		// define the alert dialog with the choices and the action to take
		// when one of the choices is selected
		ad.setSingleChoiceItems(adapter, -1,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// a choice has been made!
						// Drawable imageSelected =
						// dialogImages[which].getImg();
						if (which == 0) {
							Intent sendIntent = new Intent(Intent.ACTION_VIEW);
							sendIntent.setData(Uri.parse("sms:"));
							sendIntent.putExtra("sms_body", getResources()
									.getString(R.string.auto_msg_invite));
							startActivity(sendIntent);
						} else if (which == 1) {
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							emailIntent.setType("plain/text");
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT,
									getResources().getString(
											R.string.auto_msg_invite));
							startActivity(Intent.createChooser(emailIntent,
									"Con quale app vuoi mandare l'email:"));

						}
						dialog.dismiss();
					}
				});

		return ad.show();
	}

	private void loadListImageSendEC() {
		dialogImages = new ListImageMatch[2];

		// define the display string, the image, and the value to use
		// when the choice is selected
		dialogImages[0] = new ListImageMatch("     Manda un SMS ",
				getImg(R.drawable.sms_icon), "sms");
		dialogImages[1] = new ListImageMatch("     Manda un email ",
				getImg(R.drawable.email_icon), "email");
	}

	private Drawable getImg(int res) {
		Drawable img = getResources().getDrawable(res);
		img.setBounds(0, 0, 75, 75);
		return img;
	}

	private String[] getContactsPhoneNumbers() {
		Collection<String> phoneNumbersAl = new HashSet<String>();
		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));
				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						String tmpStr = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						if (tmpStr.toLowerCase().contains("+39")) {
							tmpStr = tmpStr.replace("+39", "").trim();
						}
						if (tmpStr.length() == 10)
							phoneNumbersAl.add(tmpStr);
					}
					pCur.close();
				}
			}

		}
		cur.close();
		return phoneNumbersAl.toArray(new String[0]);
	}

	private class UpdateFriendshipsTask extends AsyncTask<String[], Void, Void> {

		DefaultHttpClient client = new DefaultHttpClient();

		@Override
		protected Void doInBackground(String[]... params) {
			try {

				HttpPost request = new HttpPost(
						ECConnectionService.SERVER_HR_ADDRESS);
				ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair(
						ECConnectionMessageConstants.FUNC,
						ECConnectionMessageConstants.FUNCDESCRIPTOR_UPDATE_FRIENDS));
				list.add(new BasicNameValuePair("id", ""
						+ getMyApplication().getOwner().get_id()));
				int i = 0;
				for (String s : params[0]) {
					list.add(new BasicNameValuePair("number[" + i + "]", s
							.trim()));
					i++;
				}

				StringEntity entity = new UrlEncodedFormEntity(list);
				request.setEntity(entity);

				HttpResponse response = client.execute(request);

				JSONArray jArr = null;
				String opResult = null;
				try {
					jArr = JSONParser.getJSONArrayFromHttpResponse(response);
					opResult = jArr
							.getString(ECConnectionMessageConstants.RESIND_OPRESULT);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
				if (opResult.toString().toLowerCase()
						.contains("OK_BUDDY".toLowerCase())) {
					Log.d("update_amici", "success");
				} else if (opResult.toString().toLowerCase()
						.contains("SORRY_MY_FRIEND".toLowerCase())) {
					Log.d("update_amici", "failure");
				} else {
					Log.d("update_amici", "inconcludente");
				}
			} catch (Exception e) {
			}
			return null;
		}

	}

	@Override
	protected Handler getConnectionServiceHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException("Configuration error on: "
					+ reference.toString());
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// TODO:
		}
	};

}