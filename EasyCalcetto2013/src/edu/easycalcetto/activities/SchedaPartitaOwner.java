package edu.easycalcetto.activities;

import static edu.easycalcetto.ApplicationStatus.REGISTERED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_REGISTRATION;
import static edu.easycalcetto.data.ECMatch.PARTECIPANT_STATUSES;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class SchedaPartitaOwner extends EasyCalcettoActivity {
	/** Called when the activity is first created. */

	private static final String PARTECIPANT_STATUS_CONFIRMED = ECMatch.PARTECIPANT_STATUS_CONFIRMED;
	private static final String PARTECIPANT_STATUS_PENDING = ECMatch.PARTECIPANT_STATUS_PENDING;
	private static final String PARTECIPANT_STATUS_REFUSED = ECMatch.PARTECIPANT_STATUS_REFUSED;

	public static final String EXTRAKEY_CONFIRMED = PARTECIPANT_STATUS_CONFIRMED;
	public static final String EXTRAKEY_INVITED = PARTECIPANT_STATUS_PENDING;
	public static final String EXTRAKEY_REFUSED = PARTECIPANT_STATUS_REFUSED;

	private static final String[] IMPLEMENTED_STATUSES = new String[] {
			PARTECIPANT_STATUS_CONFIRMED, PARTECIPANT_STATUS_PENDING,
			PARTECIPANT_STATUS_REFUSED };

	private TextView field_Date;
	private TextView field_Owner;
	private TextView field_Confermati;
	private EditText field_Luogo;
	private TextView field_StartHour;
	private EditText field_Nome;
	private ImageView buttonViewPlayers;
	private Button buttonChangeDate;
	private Button buttonChangeHour;
	private Button buttonSaveValue;
	private final static int INFO_DIALOG = 1;
	private static final int SELECT_PICTURE = 2;
	private static final int DATE_DIALOG_ID = 3;
	private static final int TIME_DIALOG_ID = 4;

	private ECMatch match;
	private HashMap<String, ECUser[]> partecipantsMap;
	private String status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partita_owner);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Scheda Partita");

		partecipantsMap = new HashMap<String, ECUser[]>();
		for (String s : IMPLEMENTED_STATUSES) {
			partecipantsMap.put(s, null);
		}
		status = null;

		field_Date = (TextView) findViewById(R.id.field_Date);
		field_Owner = (TextView) findViewById(R.id.field_Owner);
		field_Confermati = (TextView) findViewById(R.id.field_Confermati);
		field_Luogo = (EditText) findViewById(R.id.field_Luogo);
		field_Luogo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				match.setPlace(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		field_StartHour = (TextView) findViewById(R.id.field_StartHour);
		field_Nome = (EditText) findViewById(R.id.field_Nome);
		field_Nome.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				match.setName(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		buttonChangeDate = (Button) findViewById(R.id.buttonChangeDate);
		buttonChangeHour = (Button) findViewById(R.id.buttonChangeHour);
		buttonSaveValue = (Button) findViewById(R.id.buttonSaveValue);
		buttonViewPlayers = (ImageView) findViewById(R.id.buttonViewPlayers);
		// field_EndHour=(TextView)findViewById(R.id.field_EndHour);
		match = (ECMatch) getIntent().getExtras().get(Partite.EXTRAKEY_MATCH);
		caricaDatiPartita();

		buttonSaveValue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateMatch();
			}
		});

		buttonViewPlayers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentInvited = new Intent(getApplicationContext(),
						InvitedPlayers.class);
				intentInvited.putExtra(EXTRAKEY_CONFIRMED,
						partecipantsMap.get(PARTECIPANT_STATUS_CONFIRMED));
				intentInvited.putExtra(EXTRAKEY_INVITED,
						partecipantsMap.get(PARTECIPANT_STATUS_PENDING));
				intentInvited.putExtra(EXTRAKEY_REFUSED,
						partecipantsMap.get(PARTECIPANT_STATUS_REFUSED));
				startActivity(intentInvited);

			}
		});

		buttonChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		buttonChangeHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
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
		/*
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			menu.add(1, 2, 2, "More")
					.setIcon(
							isLight ? R.drawable.ic_action_overflow_black
									: R.drawable.ic_action_overflow)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		menu.add(2, 3, 2, "Cambia Foto").setIcon(R.drawable.ic_menu_crop)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		menu.add(2, 4, 1, "Modifica Dati").setIcon(R.drawable.ic_menu_compose)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		*/
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
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
			showDialog(2);
			/*
			 * Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
			 * intentGallery.setType("image/*");
			 * intentGallery.setAction(Intent.ACTION_GET_CONTENT);
			 * startActivityForResult
			 * (Intent.createChooser(intentGallery,"Seleziona Foto Partita"),
			 * SELECT_PICTURE); break;
			 */
		case 4:
			Intent intentPreferencePartita = new Intent(
					getApplicationContext(), PreferencePartita.class);
			startActivity(intentPreferencePartita);
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
		Calendar c = null;
		switch (id) {

		case INFO_DIALOG:
			return createInfoDialog();
		case DATE_DIALOG_ID:
			c = match.getDate();
			return new DatePickerDialog(this, pDateSetListener,
					c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
		case TIME_DIALOG_ID:
			c = match.getDate();
			return new TimePickerDialog(this, timeListener,
					c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
		default:
			return null;
		}
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoProfiloDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogSchedaPartitaOwnerMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	public void caricaDatiPartita() {
		
		Calendar c = match.getDate();
		String dataString = String.format("%1$02d/%2$02d/%3$4d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
		String oraString = String.format("%1$02d:%2$02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		
		field_Date.setText(dataString);
		field_Owner.setText(match.getOwner().getName() + " "
				+ match.getOwner().getSurname());
		field_Confermati
				.setText(getPartecipantsNumberByStatus(PARTECIPANT_STATUS_CONFIRMED)
						+ "/" + match.getNumberMaxPlayer());
		field_Luogo.setText(match.getPlace());
		field_StartHour.setText(oraString);
		field_Nome.setText(match.getName());
	}

	private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			view.setIs24HourView(true);
			Calendar c = match.getDate();
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			match.setDate(c);
			caricaDatiPartita();
		}
	};

	/** Updates the time in the TextView */

	private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = match.getDate();
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, day);
			match.setDate(c);
			caricaDatiPartita();
		}
	};

	private String getPartecipantsNumberByStatus(String key) {
		String result = "";
		if (partecipantsMap.containsKey(key)) {
			ECUser[] ecuArr = partecipantsMap.get(key);
			if (ecuArr != null) {
				result = "" + ecuArr.length;
			} else {
				result = "Downloading...";
			}
		}
		return result;
	}

	@Override
	protected Handler getConnectionServiceHandler() {

		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS:
						Bundle b = msg.getData();
						String[] resultKeys = b
								.getStringArray(ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);

						for (String iStatus : partecipantsMap.keySet()) {
							for (String rStatus : resultKeys) {
								if (iStatus.trim().equalsIgnoreCase(
										rStatus.trim())) {
									partecipantsMap.put(iStatus, ((ECUser[]) b
											.getParcelableArray(rStatus)));
								}
							}
						}
						field_Confermati
								.setText(getPartecipantsNumberByStatus(PARTECIPANT_STATUS_CONFIRMED));
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEMATCH:
						finish();
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
	}
	@Override
	protected void onServiceConnected() {
		downloadPartecipants();
	}

	private void downloadPartecipants() {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(
				FUNC,
				ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCH_PARTECIPANTS));
		params.add(new BasicNameValuePair("id", String.valueOf(match
				.getIdMatch())));
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(SchedaPartitaOwner.this);
				pDialog.setMessage("Caricando la lista dei partecipanti");
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
					for (int i = 0; i < PARTECIPANT_STATUSES.length; i++) {
						partecipantsMap.put(PARTECIPANT_STATUSES[i], ECUser
								.createFromJSONArray(getDataJArr()
										.getJSONArray(i)));
					}
					field_Confermati
							.setText(getPartecipantsNumberByStatus(PARTECIPANT_STATUS_CONFIRMED));
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
				// TODO Auto-generated method stub
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
//		Messenger msnger = new Messenger(getConnectionServiceHandler());
//		Message msg = MessagesCreator.getGamePartecipantsMessage(msnger,
//				match.getIdMatch());
//		try {
//			messenger.send(msg);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}
/*VECCHIO METODO COMMENTATO DA STEFANO
	private void updateMatch() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = MessagesCreator.getUpdateMatchMessage(msnger,
				match);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
*/
	
	private void updateMatch() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, ECConnectionMessageConstants.FUNCDESCRIPTOR_UPDATEMATCH));
		params.addAll( match.getObjectAsNameValuePairList());
		
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(SchedaPartitaOwner.this);
				pDialog.setMessage("Aggiorno dati partita...");
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
				finish();
			}
			
			@Override
			protected void onSuccess() {
				
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
				Toast.makeText(getApplicationContext(),"Dati non salvati! Riprova", Toast.LENGTH_LONG).show();
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
		
		task.execute(params.toArray(new BasicNameValuePair[]{}));
//		Messenger msnger = new Messenger(getConnectionServiceHandler());
//		Message msg = MessagesCreator.getConfirmRegistrationMessage(msnger,
//				registration);
		
		
		

	}
	
	
	
	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

	/** Updates the date in the TextView */
	// TODO
	// private void updateData(MyData dataString) {
	// field_Date.setText(
	// new StringBuilder()
	// .append(dataString.toString()));
	//
	// }

	/*
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * //Handle the back button if (keyCode == KeyEvent.KEYCODE_BACK) {
	 * 
	 * return true; } return super.onKeyDown(keyCode, event); }
	 */

}
