package edu.easycalcetto.activities;

import static edu.easycalcetto.ApplicationStatus.REGISTRATION_PENDING;
import static edu.easycalcetto.connection.Constants.COM_RESULT_OK;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_RESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_REGISTRATION;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGRESDESCTIPTION_SUCCESS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_DATA;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_OPRESULT;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.Constants;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECRegistrationData;
import edu.easycalcetto.data.JSONParser;
import edu.easycalcetto.data.MessagesCreator;

public class RegistrazioneActivity extends EasyCalcettoActivity {
	/** Called when the activity is first created. */

	private EditText nameField;
	private EditText surnameField;
	private EditText ageField;
	private EditText numberField;
	private final static int EXIT_DIALOG = 0;
	private final static int INFO_DIALOG = 1;
	private final static int INFO_NUMBER_DIALOG = 2;
	protected static final int STARTFLAG_MENU = 1;
	private String[] prefissi = { "330", "331", "333", "334", "335", "336",
			"337", "338", "339", "360", "366", "368", "340", "342", "345",
			"346", "347", "348", "349", "320", "324", "327", "328", "329",
			"380", "383", "388", "389", "391", "392", "393" };
	private String prefScelto;
	private Spinner spinner;
	private ArrayAdapter<String> dataAdapter;
	private String number;

	private ECRegistrationData registration;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		menu.add(1, 1, 1, "Info")
				.setIcon(
						isLight ? R.drawable.info_buttondark
								: R.drawable.info_button_white)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		/*
		 * // per ora non ci serve il tasto destro menu.add(1, 2, 2, "More")
		 * .setIcon( isLight ? R.drawable.ic_action_overflow_black :
		 * R.drawable.ic_action_overflow)
		 * .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 */
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTheme(SampleList.THEME); // Used for theme switching in samples
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg1);
		ImageButton infoButton = (ImageButton) findViewById(R.id.infoButton);
		Button continueButton = (Button) findViewById(R.id.continueButton);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		nameField = (EditText) findViewById(R.id.fieldName);
		nameField.setText("");
		surnameField = (EditText) findViewById(R.id.fieldSurname);
		surnameField.setText("");
		ageField = (EditText) findViewById(R.id.fieldAge);
		ageField.setText("");
		numberField = (EditText) findViewById(R.id.fieldNumber);
		numberField.setText("");
		spinner = (Spinner) findViewById(R.id.spinner);
		addItemsOnSpinner();
		attivaListenerSpninner();
		continueButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				continuaButton();
			}
		});

		infoButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showDialog(INFO_NUMBER_DIALOG);
			}
		});

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
			Toast.makeText(this, "Non ci sono altre opzioni disponibili",
					Toast.LENGTH_LONG).show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void continuaButton() {
		if (isWellFormed()) {
			registration = new ECRegistrationData(nameField.getText()
					.toString(), surnameField.getText().toString(), ageField
					.getText().toString(), number);
			
			
			executeRegistation();
			
			// Messenger msnger = new Messenger(getConnectionServiceHandler());
			// Message mes = MessagesCreator.getRegistrationMessage(msnger,
			// registration);
			// try {
			//
			// //messenger.send(mes);
			// } catch (RemoteException e) {
			// e.printStackTrace();
			// Toast.makeText(getApplicationContext(),
			// "Si è verificato un'errore", Toast.LENGTH_SHORT).show();
			// finish();
			// }
		} else if (number.length() != 10) {
			Toast.makeText(this, "Inserisci un numero di telefono valido",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Non hai completato tutti i campi",
					Toast.LENGTH_LONG).show();
		}

	}

	public int describeContents() {
		return 0;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case EXIT_DIALOG:
			return createExitDialog();
		case INFO_DIALOG:
			return createInfoDialog();
		case INFO_NUMBER_DIALOG:
			return createInfoNumberDialog();
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
						// non fare nulla
					}
				});
		return builder.create();
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogMSGRegistrazione);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// niente da fare
					}
				});
		return builder.create();
	}

	private final Dialog createInfoNumberDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoNumberDialogMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// niente da fare
					}
				});
		return builder.create();
	}

	private void addItemsOnSpinner() {
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, prefissi);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter.sort(null);
		spinner.setAdapter(dataAdapter);
	}

	public void attivaListenerSpninner() {
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				/*
				 * Toast.makeText(parent.getContext(),
				 * "OnItemSelectedListener : " +
				 * parent.getItemAtPosition(pos).toString(),
				 * Toast.LENGTH_SHORT).show();
				 */
				prefScelto = (String) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private boolean isWellFormed() {
		int count = 0;
		if (nameField.getText().toString().length() == 0)
			count++;
		if (surnameField.getText().toString().length() == 0)
			count++;
		if ((ageField.getText().toString()).length() == 0)
			count++;
		number = prefScelto + numberField.getText().toString();
		if (number.length() != 10)
			count++;

		if (count == 0)
			return true;
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == STARTFLAG_MENU) {
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
		}
	}

	private void prepareAndSendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	@Override
	protected Handler getConnectionServiceHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					long l = msg.getData().getLong(BNDKEY_RESULT);
					String smsmsg = getResources().getString(R.string.smsfmsg,
							String.valueOf(l));
					prepareAndSendSMS(number, smsmsg);
					Intent intent = new Intent(RegistrazioneActivity.this,
							Registrazione2Activity.class);
					// intent.putExtra("new.account", registration);
					getMyApplication().setOwner(-1, registration);
					getMyApplication().setApplicationStatus(
							REGISTRATION_PENDING);
					startActivityForResult(intent, STARTFLAG_MENU);
					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					Toast.makeText(getApplicationContext(), "Epic Fail!",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};
	}

	@Override
	protected void onServiceConnected() {

	}

	@Override
	protected void onServiceDisconnected() {

	}
	
	private void executeRegistation(){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_REGISTRATION));
		params.addAll(registration.getObjectAsNameValuePairList());
		
		ECPostWithBNVPTask task = new ECPostWithBNVPTask(){
			ProgressDialog pDialog = null;
			JSONArray jArr = null;
			JSONArray dataJArr = null;
			String opResult = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(RegistrazioneActivity.this);
				pDialog.setMessage("Inviando la registrazione");
				pDialog.show();
				super.onPreExecute();
			}
			
			
			@Override
			protected void onPostExecute(Integer result) {
				pDialog.dismiss();
				int res = result.intValue();
				if(res == COM_RESULT_OK){
					try {
						;
						if((jArr = JSONParser.getJSONArrayFromHttpResponse(getResponse()))!= null){
							if((opResult = jArr.getString(RESIND_OPRESULT))!=null){
								if (opResult.toString().toLowerCase()
										.contains(MSGRESDESCTIPTION_SUCCESS.toLowerCase())){
									// RESPONSE SUCCESS
									if ((dataJArr = jArr.optJSONArray(RESIND_DATA))!=null){
										long l = Long.valueOf(dataJArr.getString(0));
										String smsmsg = getResources().getString(R.string.smsfmsg,
												String.valueOf(l));
										prepareAndSendSMS(number, smsmsg);
										Intent intent = new Intent(RegistrazioneActivity.this,
												Registrazione2Activity.class);
										getMyApplication().setOwner(-1, registration);
										getMyApplication().setApplicationStatus(
												REGISTRATION_PENDING);
										startActivityForResult(intent, STARTFLAG_MENU);
									}else{
										Log.e(LOGTAG, "no data");
										Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
										finish();
										return;
									}
								}else{
									// RESPONSE FAILURE
									Log.e(LOGTAG, "the registration is failed");
									Log.e(LOGTAG + " - DEBUG", getResponse().toString());
									Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
									finish();
									return;
								}
								
							}else{
								Log.e(LOGTAG, "no opresult");
								Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
								finish();
								return;
							}
						}else{
							Log.e(LOGTAG, "no JArr");
							Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
							finish();
							return;
						}
						
					} catch (Exception e) {
						Log.e(LOGTAG + " - DEBUG", getResponse().toString(), e);
						Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
						finish();
						return;
					}
				}else{
					Log.e(LOGTAG,"internal error");
					Toast.makeText(getApplicationContext(), "registration failed", Toast.LENGTH_LONG).show();
					finish();
				}
				super.onPostExecute(result);
			}
		};
		task.execute(params.toArray(new BasicNameValuePair[]{}));
	}
}