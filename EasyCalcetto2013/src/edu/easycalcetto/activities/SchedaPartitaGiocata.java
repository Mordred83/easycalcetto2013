package edu.easycalcetto.activities;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class SchedaPartitaGiocata extends EasyCalcettoActivity {
	/** Called when the activity is first created. */
	
	private static final String PARTECIPANT_STATUS_CONFIRMED = ECMatch.PARTECIPANT_STATUS_CONFIRMED; 
	private static final String PARTECIPANT_STATUS_PENDING = ECMatch.PARTECIPANT_STATUS_PENDING;
	private static final String PARTECIPANT_STATUS_REFUSED = ECMatch.PARTECIPANT_STATUS_REFUSED;
	
	public static final String EXTRAKEY_CONFIRMED = PARTECIPANT_STATUS_CONFIRMED;
	public static final String EXTRAKEY_INVITED = PARTECIPANT_STATUS_PENDING;
	public static final String EXTRAKEY_REFUSED = PARTECIPANT_STATUS_REFUSED;
	
	private static final String[] IMPLEMENTED_STATUSES = new String[] {
			PARTECIPANT_STATUS_CONFIRMED,
			PARTECIPANT_STATUS_PENDING,
			PARTECIPANT_STATUS_REFUSED };
	
	private TextView field_Date;
	private TextView field_Owner;
	private TextView field_Confermati;
	private TextView field_Luogo;
	private TextView field_StartHour;
	private TextView field_Name;
	// private TextView field_EndHour;
	private ImageView buttonViewPlayers;
	private ImageView indicatorYes;
	//private ImageView indicatorNo;
	private Button buttonYes;
	//private Button buttonNo;
	// private ImageView avatar;
	// private static String selectedImagePath="null";
	private final static int INFO_DIALOG = 1;
	// private static final int SELECT_PICTURE = 1;
	// private FileOutputStream fos;
	
	
	private ECMatch match;
	private HashMap<String, ECUser[]> partecipantsMap;
	private String status;

	// private ListImageMatch[] immaggini;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.partita_giocata);
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
		field_Luogo = (TextView) findViewById(R.id.field_Luogo);
		field_StartHour = (TextView) findViewById(R.id.field_StartHour);
		field_Name = (TextView) findViewById(R.id.field_Name);
		indicatorYes = (ImageView) findViewById(R.id.indicatorYes);
		//indicatorNo = (ImageView) findViewById(R.id.indicatorNo);
		buttonYes = (Button) findViewById(R.id.buttonYes);
		//buttonNo = (Button) findViewById(R.id.buttonNo);
		View.OnClickListener buttonListner = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v.getId() == buttonYes.getId()){
					confirmGame();
				}
			}
		};
		buttonViewPlayers = (ImageView) findViewById(R.id.buttonViewPlayers);
		caricaDatiPartita();

		buttonYes.setOnClickListener(buttonListner);
		//buttonNo.setOnClickListener(buttonListner);

		buttonViewPlayers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.pressed));
				Intent intentInvited = new Intent(getApplicationContext(),
						InvitedPlayers.class);
				intentInvited.putExtra(EXTRAKEY_CONFIRMED,  partecipantsMap.get(PARTECIPANT_STATUS_CONFIRMED));
				intentInvited.putExtra(EXTRAKEY_INVITED, partecipantsMap.get(PARTECIPANT_STATUS_PENDING));
				intentInvited.putExtra(EXTRAKEY_REFUSED, partecipantsMap.get(PARTECIPANT_STATUS_REFUSED));
				startActivity(intentInvited);

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		aggiornaPreferenze();
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
		*/
		//TODO: Stefeno che michia fa sta roba?
		// if (isOwner()) {
		//
		// menu.add(2, 3, 2, "Cambia Foto").setIcon(R.drawable.ic_menu_crop)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		//
		// menu.add(2, 4, 1, "Modifica Dati")
		// .setIcon(R.drawable.ic_menu_compose)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		// }
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, 1);
			onKeyDown(KeyEvent.KEYCODE_BACK, event);
			break;

		case 1: // Info button
			showDialog(INFO_DIALOG);
			break;

		case 2:
			openOptionsMenu();
			break;
		case 3:
			showDialog(2);
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
		builder.setMessage(R.string.infoDialogSchedaPartitaMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	private void aggiornaPreferenze() {
		if( status == null || status.equals(PARTECIPANT_STATUS_PENDING)){
			buttonYes.setEnabled(true);
			//buttonNo.setEnabled(true);
			indicatorYes.setVisibility(View.INVISIBLE);
			//indicatorNo.setVisibility(View.INVISIBLE);
		}else if(status.equals(PARTECIPANT_STATUS_CONFIRMED)){
			buttonYes.setEnabled(false);
			//buttonNo.setEnabled(true);
			indicatorYes.setVisibility(View.VISIBLE);
			//indicatorNo.setVisibility(View.INVISIBLE);
		}else if(status.equals(PARTECIPANT_STATUS_REFUSED)){
			buttonYes.setEnabled(true);
			//buttonNo.setEnabled(false);
			indicatorYes.setVisibility(View.INVISIBLE);
			//indicatorNo.setVisibility(View.VISIBLE);
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

	

	public void caricaDatiPartita() {
		
		match = (ECMatch) getIntent().getExtras().getParcelable(
				Partite.EXTRAKEY_MATCH);
		
		Calendar c = match.getDates()[0];
		String dateStr = String.format("%1$02d/%2$02d/%3$4d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
		String startHourString = String.format("%1$02d:%2$02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		field_Date.setText(dateStr);
		field_Owner.setText(match.getOwner().getName() + " "
				+ match.getOwner().getSurname());
		field_Confermati.setText(""
				+ getPartecipantsNumberByStatus(PARTECIPANT_STATUS_CONFIRMED));
		field_Luogo.setText(match.getPlace());
		field_StartHour.setText(startHourString);
		field_Name.setText(match.getName());
		aggiornaPreferenze();
		
	}

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
						
						setStatus();
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_GAME:
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_DECLINE_GAME:
						Log.d("messaggio", "accettato/rifiutato");
						SchedaPartitaGiocata.this.finish();
						break;
					}
					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					Log.d("fallimento", "accettato/rifiutato");
					break;
				default:
					Log.d("default", "accettato/rifiutato");
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
		
		
		
		
		
//		Messenger msnger = new Messenger(getConnectionServiceHandler());
//		Message msg = MessagesCreator.getGamePartecipantsMessage(msnger, match.getIdMatch());
//		try {
//			messenger.send(msg);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}
	
	
/*	VECCHIO METODO COMMENTATO DA STEFANO
	private void confirmGame() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = MessagesCreator.getConfirmGameMessage(msnger, getMyApplication().getOwner().get_id(), match.getIdMatch(), 1);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
*/
	
	private void confirmGame() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_GAME));
		params.add(new BasicNameValuePair("player_id", String.valueOf(getMyApplication().getOwner().get_id())));
		params.add(new BasicNameValuePair("match_id",String.valueOf(match.getIdMatch())));
		params.add(new BasicNameValuePair("data_id",String.valueOf(1)));
		
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(SchedaPartitaGiocata.this);
				pDialog.setMessage("Invio informazioni...");
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
			
			}
			
			@Override
			protected void onOpResultNULL() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onJArrNULLCB() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onGenericError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onFailure() {
				
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
	
	
	
	
	
	
/*VECCHIO METODO COMMENTATO DA STEFANO	
	private void declineGame() {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = MessagesCreator.getDeclineGameMessage(msnger, getMyApplication().getOwner().get_id(), match.getIdMatch(), 1);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
*/

	
	private void declineGame() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, ECConnectionMessageConstants.FUNCDESCRIPTOR_DECLINE_GAME));
		params.add(new BasicNameValuePair("player_id", String.valueOf(getMyApplication().getOwner().get_id())));
		params.add(new BasicNameValuePair("match_id",String.valueOf(match.getIdMatch())));
		params.add(new BasicNameValuePair("data_id",String.valueOf(1)));
		
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(SchedaPartitaGiocata.this);
				pDialog.setMessage("Invio informazioni...");
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
			
			}
			
			@Override
			protected void onOpResultNULL() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onJArrNULLCB() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onGenericError() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void onFailure() {
				
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
	
	private void setStatus(){
		String tmpStr = null; 
		for(String key : partecipantsMap.keySet()){
			ECUser[] ecuArr = partecipantsMap.get(key);
			if(ecuArr != null)
			for( int i = 0; i < ecuArr.length && tmpStr == null; i++){
				Log.d(key, getMyApplication().getOwner().get_id()+" == "+ecuArr[i].get_id()+" ? "+getMyApplication().getOwner().equals(ecuArr[i]));
				if(getMyApplication().getOwner().equals(ecuArr[i]))
					tmpStr = key;
			}
		}
		status = tmpStr;
		Log.d("status", status);
		caricaDatiPartita();
	}

	@Override
	protected void onServiceDisconnected() {
	}

}
