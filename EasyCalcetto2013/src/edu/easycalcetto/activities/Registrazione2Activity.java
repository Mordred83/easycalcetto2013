package edu.easycalcetto.activities;

import static edu.easycalcetto.ApplicationStatus.REGISTERED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_RESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.ApplicationStatus;
import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.data.ECRegistrationData;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class Registrazione2Activity extends EasyCalcettoActivity {
	private static final int INFO_SMS_DIALOG = 3;
	/** Called when the activity is first created. */

	private EditText SMSfield;
	private ProgressBar progBar;
	private Button validateButton;
	private ImageView tickImage;
	private String status = "null";
	public static final String PREFS_NAME = "PrefLogFile";
	public static final String PREF_REGISTERED = "registered";

	ECRegistrationData registration;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(SampleList.THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg2);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final SharedPreferences pref = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().containsKey("new.account"))
			registration = (ECRegistrationData) getIntent().getExtras()
					.getParcelable("new.account");
		
		SMSfield = (EditText) findViewById(R.id.fieldSMS);
		SMSfield.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				validateButton.setEnabled(registration.setCode(s.toString()
						.trim()));
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
		validateButton = (Button) findViewById(R.id.validateButton);
		validateButton.setEnabled(false);
		progBar = (ProgressBar) findViewById(R.id.progressBar);
		validateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendConfirmation();
			}
		});
	}

	private void sendConfirmation() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = MessagesCreator.getConfirmRegistrationMessage(msnger,
				registration);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void aggiornaStatus() {
		// Toast.makeText(this, "CIAO-3", Toast.LENGTH_SHORT).show();
		ImageView tickImage = (ImageView) findViewById(R.id.tick);
		tickImage.setVisibility(View.VISIBLE);
		// progBar.refreshDrawableState();
		tickImage.refreshDrawableState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;

		menu.add(1, 1, 1, "Info")
				.setIcon(
						isLight ? R.drawable.info_buttondark
								: R.drawable.info_button_white)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add(1, 2, 2, "More")
				.setIcon(
						isLight ? R.drawable.ic_action_overflow_black
								: R.drawable.ic_action_overflow)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case 1: // Info button
			showDialog(INFO_SMS_DIALOG);
			break;

		case 2:
			Toast.makeText(this, "Non ci sono altre opzioni disponibili",
					Toast.LENGTH_LONG).show();
			openOptionsMenu();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_SMS_DIALOG:
			return createInfoSMSDialog();
		default:
			return null;
		}
	}

	private final Dialog createInfoSMSDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoSMSDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoSMSDialogMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// niente da fare
					}
				});
		return builder.create();
	}

	@Override
	protected Handler getConnectionServiceHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					switch (msg.arg1) {
					case MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION:
						long l = msg.getData().getLong(BNDKEY_RESULT);
						getMyApplication().setOwner(l, registration);
						getMyApplication().setApplicationStatus(
								REGISTERED);
						Toast.makeText(getApplicationContext(),
								"Ti sei Registrato correttamente",
								Toast.LENGTH_SHORT).show();
						finish();
						break;
					}
					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION:
						Toast t = Toast.makeText(getApplicationContext(),
								"Il codice inserito non è valido",
								Toast.LENGTH_LONG);
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								SMSfield.requestFocus();
							}
						}, t.getDuration());
						t.show();
						break;
					}
					break;
				default:
					break;
				}

			}
		};
	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

}