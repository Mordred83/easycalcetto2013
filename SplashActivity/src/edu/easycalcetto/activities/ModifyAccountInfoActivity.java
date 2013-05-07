package edu.easycalcetto.activities;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class ModifyAccountInfoActivity extends EasyCalcettoActivity {

	// CONSTANTS
	private static final String PHONE_CHECK_REGEX = "\\d{10}";
	private static final String DATE_FORMAT_STRING = "%02d - %02d - %4d";
	private static final int DLG_DATEPICKER = 1000;

	// ***** MODEL VARS
	private String nameStr;
	private String surnameStr;
	// private String phoneStr;
	private Calendar dob;

	private ECUser currentUser;

	// ***** CONTROL VARS
	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			update();
			saveButton.setEnabled(checkData());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			update();
			saveButton.setEnabled(checkData());
		}
	};

	private View.OnClickListener saveButtonListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			updateUser();
		}
	};

	private View.OnClickListener changeDOBListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			showDialog(DLG_DATEPICKER);
		}
	};

	private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int month, int day) {
			dob.set(Calendar.DAY_OF_MONTH, day);
			dob.set(Calendar.MONTH, month);
			dob.set(Calendar.YEAR, year);
			update();
		}
	};

	// ***** VIEW VARS
	private EditText nameEditText, surnameEditText;
	private TextView dobTextView;
	private Button saveButton, changeDOBButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_modification);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Modifica Profilo");
		nameEditText = (EditText) findViewById(R.id.name_EditText);
		surnameEditText = (EditText) findViewById(R.id.surname_EditText);
		dobTextView = (TextView) findViewById(R.id.dobTextView);
		changeDOBButton = (Button) findViewById(R.id.dob_changeButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		initContents();
		nameEditText.addTextChangedListener(watcher);
		surnameEditText.addTextChangedListener(watcher);
		changeDOBButton.setOnClickListener(changeDOBListner);
		saveButton.setOnClickListener(saveButtonListner);
	}

	private void initContents() {
		currentUser = getMyApplication().getOwner();
		nameEditText.setText(currentUser.getName());
		surnameEditText.setText(currentUser.getSurname());
		dob = Calendar.getInstance();
		dob.setTimeInMillis(Long.valueOf(currentUser.getYob()));
		String data = String.format(DATE_FORMAT_STRING,
				dob.get(Calendar.DAY_OF_MONTH), dob.get(Calendar.MONTH) + 1,
				dob.get(Calendar.YEAR));
		dobTextView.setText(data);
		dobTextView.setText(data);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean checkData() {
		return nameStr.trim().length() > 0 && surnameStr.trim().length() > 0;
	}

	private void update() {
		nameStr = nameEditText.getText().toString().trim();
		surnameStr = surnameEditText.getText().toString().trim();
		String data = String.format(DATE_FORMAT_STRING,
				dob.get(Calendar.DAY_OF_MONTH), dob.get(Calendar.MONTH) + 1,
				dob.get(Calendar.YEAR));
		dobTextView.setText(data);
	}

	private void updateUser() {
		saveButton.setEnabled(false);
		currentUser.setName(nameStr);
		currentUser.setSurname(surnameStr);
		currentUser.setYob(String.valueOf(dob.getTimeInMillis()));
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = MessagesCreator.getUpdateUserMessage(msnger,
				currentUser.get_id(), currentUser.getName(),
				currentUser.getSurname(), Long.valueOf(currentUser.getYob()));
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
			saveButton.setEnabled(true);
		}
	}

	@Override
	protected Handler getConnectionServiceHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEUSER:
						getMyApplication().updateOwner(currentUser);
						Toast.makeText(getApplicationContext(),
								"Dati Modificati Correttamente",
								Toast.LENGTH_SHORT).show();
						saveButton.setEnabled(true);
						finish();
						break;
					}
					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEUSER:
						Toast.makeText(getApplicationContext(),
								"Impossibile modificare le informazioni",
								Toast.LENGTH_SHORT).show();
						saveButton.setEnabled(true);
						finish();
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
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DLG_DATEPICKER:
			return new DatePickerDialog(this, pDateSetListener,
					dob.get(Calendar.YEAR), dob.get(Calendar.MONTH),
					dob.get(Calendar.DAY_OF_MONTH));
		default:
			return super.onCreateDialog(id);
		}
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
