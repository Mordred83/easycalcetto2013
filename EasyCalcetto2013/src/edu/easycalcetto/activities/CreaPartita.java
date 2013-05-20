package edu.easycalcetto.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;

public class CreaPartita extends EasyCalcettoActivity {
	/** Called when the activity is first created. */

	private EditText field_name;
	private EditText field_place;
	// private TextView field_Status;
	private Spinner spinner;
	private ImageButton buttonNewDate;
	private Button buttonAvanti;
	private ArrayAdapter<String> dataAdapter;
	private String tipoScelto;
	private final static int INFO_DIALOG = 1;
	private static final int DATE_DIALOG_ID = 2;
	private static final int TIME_DIALOG_ID = 3;
	private static final int[] tipoSquadra = { 5, 6, 7, 8, 9, 10, 11 };
	protected static final int STARTFLAG_SELEZIONA_AMICI = 1;
	private LinearLayout listaData;
	
	private String dateFormatString;
	private String timeFormatString;

	private ArrayList<Calendar> dates;
	//private DateAdapter datesAdapter;
	//private ListView datesListView;
	// private TextView currentTextDate;
	// private TextView currentTextTime;
	private Button buttonData;
	private boolean dataSetted;
	private Button buttonTime;
	private boolean timeSetted;
	private boolean arraySlot[] = { true, true, true, true, true };
	int i = 0;
	int j = 0;

	private String match_name = "";
	private String match_place = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crea_partita);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Nuova Partita 1/2");
		
		dateFormatString = getResources().getString(R.string.rowView_date_format);
		timeFormatString = getResources().getString(R.string.rowView_time_format);
		dataSetted =false;
		timeSetted =false;
		dates = new ArrayList<Calendar>();
		addElementData();

		field_name = (EditText) findViewById(R.id.campo_nome);
		field_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				match_name = s.toString();
				buttonAvanti.setEnabled(isValidMatch());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});
		field_place = (EditText) findViewById(R.id.campo_luogo);
		field_place.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				match_place = s.toString();
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});
		spinner = (Spinner) findViewById(R.id.spinnerSquadre);
		// spinner.setBackgroundColor(R.color.red);
		buttonNewDate = (ImageButton) findViewById(R.id.buttonAddDate);
		buttonNewDate.setEnabled(false);

		buttonAvanti = (Button) findViewById(R.id.buttonAvanti);
		buttonAvanti.setEnabled(false);
		addItemsOnSpinner();
		attivaListenerSpninner();
		buttonNewDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dates.size() == 0) {
					addElementData();
				} else {
					Toast.makeText(getApplicationContext(),
							"Funzionalità in fase di sviluppo",
							Toast.LENGTH_SHORT).show();
				}
				// else if(dates.size()!=0){
				// buttonData=(Button)findViewById(10000000+(j));
				// buttonTime=(Button)findViewById(20000000+j);
				// if(buttonData.getText().equals("Imposta Data") ||
				// buttonTime.getText().equals("Imposta ora d'inizio")){
				// Toast.makeText(getApplicationContext(),
				// "Completa prima lo slot vuoto", Toast.LENGTH_SHORT).show();
				// }
				// else{
				// addElementData();
				// }
				// }
			}
		});
		
		//datesListView = (ListView) findViewById(R.id.datesListView);
		dates = new ArrayList<Calendar>();
		dates.add(Calendar.getInstance());
		//datesAdapter = new DateAdapter(this, dates);
		//datesListView.setAdapter(datesAdapter);
		

		buttonAvanti.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentSelezionaAmici = new Intent(
						getApplicationContext(), InvitaAmiciNuovaPartita.class);
				long id = 0;
				ECUser creator = getMyApplication().getOwner();
				String name = match_name;
				String status = ECMatch.Status.TO_PLAY.toString();
				String[] userStatus = new String[] { ECMatch.UserStatus.CONFIRMED
						.toString() };
				String location = match_place;
				int number = tipoSquadra[spinner.getFirstVisiblePosition()];
				long date = dates.get(0).getTimeInMillis();

				Log.d("DATA ON CHANGE", "" + date);

				ECMatch match = new ECMatch(id, creator, name, status,
						userStatus, location, number, date);
				intentSelezionaAmici.putExtra(
						InvitaAmiciNuovaPartita.EXTRAKEY_MATCH, match);
				startActivityForResult(intentSelezionaAmici,
						STARTFLAG_SELEZIONA_AMICI);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == STARTFLAG_SELEZIONA_AMICI) {
			finish();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
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
			showDialog(INFO_DIALOG);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_DIALOG:
			return createInfoDialog();
		case DATE_DIALOG_ID:
			Calendar cal = dates.get(0);
			int giorno = cal.get(Calendar.DAY_OF_MONTH);
			int mese = cal.get(Calendar.MONTH);
			int anno = cal.get(Calendar.YEAR);
			return new DatePickerDialog(this, pDateSetListener, anno, mese,
					giorno);
		case TIME_DIALOG_ID:
			cal = dates.get(0);
			int ora = cal.get(Calendar.HOUR_OF_DAY);
			int minuti = cal.get(Calendar.MINUTE);
			return new TimePickerDialog(this, timeListener, ora, minuti, true);
		default:
			return null;
		}
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogNuovaPartitaMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	private void addItemsOnSpinner() {
		String[] tipoSquadraLabel = new String[tipoSquadra.length];
		for (int i = 0; i < tipoSquadra.length; i++)
			tipoSquadraLabel[i] = String
					.format("%1$d vs. %1$d", tipoSquadra[i]);
		dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, tipoSquadraLabel);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// dataAdapter.sort(null);
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
				tipoScelto = (String) parent.getItemAtPosition(pos);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
//	public void addElementData(){
//		dates.add(Calendar.getInstance());
//		datesAdapter.notifyDataSetChanged();
//	}

	public void addElementData() {
		// prendo il layout in cui inserire gli elementi
		listaData = (LinearLayout) findViewById(R.id.datesLinearLayout);
		// chiamo il servizio per inserire layout dinamicamente
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		// prendo l'elemento da inserire
		View itemView = inflater.inflate(R.layout.layout_new_data, null);
		// cambio il numero dell'elemento nella lista
		Calendar c = Calendar.getInstance();
		dates.add(c);
		itemView.setId(dates.size());
		TextView text = (TextView) itemView.findViewById(R.id.numberElement);
		text.setText(dates.size() + ")");
		// lo aggiungo nel layout
		listaData.addView(itemView);

		// collego gli elementi del layout
		buttonData = (Button) findViewById(R.id.buttonSetData);
	
		buttonTime = (Button) findViewById(R.id.buttonSetTime);
	
		ImageButton buttonRemove = (ImageButton) findViewById(R.id.buttonRemove);
		i = i + 1;
		j = j + 1;
		buttonData.setId(10000000 + j);
		buttonTime.setId(20000000 + j);
		buttonRemove.setId(700000 + i);

		buttonData = (Button) findViewById(10000000 + j);
		buttonTime = (Button) findViewById(20000000 + j);
		buttonRemove = (ImageButton) findViewById(700000 + i);
		buttonRemove.setTag(dates.size());

		arraySlot[dates.size() - 1] = false;

		buttonData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				showDialog(DATE_DIALOG_ID);
			}
		});

		buttonTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		buttonRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = (Integer) v.getTag();
				View view = (View) findViewById(index);
				if (index != dates.size()) {
					Toast.makeText(getApplicationContext(),
							"Puoi eliminare solo l'ultimo slot",
							Toast.LENGTH_SHORT).show();
				} else {
					if (index > 1)
						listaData.removeViewAt(listaData.indexOfChild(view));
					else {
						Log.i("ultimo", (String) v.getTag().toString());
						listaData.removeViewAt(0);
					}
					dates.remove(dates.size() - 1);
					Log.i("rimuovo", (String) v.getTag().toString());
					i--;
					j--;
				}
			}
		});
		buttonRemove.setEnabled(false);
		// if (dates.size() == 1)
		// Toast.makeText(getApplicationContext(),
		// "Slot numero " + dates.size() + " aggiunto.",
		// Toast.LENGTH_SHORT).show();
		// else
		// Toast.makeText(
		// getApplicationContext(),
		// "Slot data numero " + dates.size()
		// + " aggiunto. Scorri la lista", Toast.LENGTH_SHORT)
		// .show();

	}

	private TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			view.setIs24HourView(true);

			dates.get(0).set(Calendar.HOUR_OF_DAY, hourOfDay);
			dates.get(0).set(Calendar.MINUTE, minute);
			timeSetted=true;
			buttonAvanti.setEnabled(isValidMatch());
			updateOra();
		}
	};

	/** Updates the time in the TextView */
	private void updateOra() {
		Calendar c = dates.get(0);
		String time = String.format("%02d : %02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		buttonTime.setText(time);
	}

	private DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int month, int day) {
			dates.get(0).set(Calendar.DAY_OF_MONTH, day);
			dates.get(0).set(Calendar.MONTH, month);
			dates.get(0).set(Calendar.YEAR, year);
			dataSetted = true;
			buttonAvanti.setEnabled(isValidMatch());
			updateData();
		}
	};

	/** Updates the date in the TextView */
	private void updateData() {
		Calendar c = dates.get(0);
		String data = String.format("%02d / %02d / %4d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
		buttonData.setText(data);
	}

	private int findSlotEmpty() {
		for (int i = 0; i < arraySlot.length; i++) {
			if (arraySlot[i] == true)
				return i;
		}
		return -1;
	}
	
	private boolean isValidMatch() {
		return (match_name.length()>0 && match_place.length()>0 && dataSetted && timeSetted);
	}

}