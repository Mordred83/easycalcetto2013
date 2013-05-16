package edu.easycalcetto.activities;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
//import edu.easycalcetto.activities.Amici.TabID;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;
import edu.easycalcetto.data.MyCheckable;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class InvitedPlayers extends EasyCalcettoActivity implements
		ActionBar.TabListener {

	private final static int INFO_DIALOG = 1;
	private static final int ID_PROFILE = 1;
	private static final int ID_SEND = 2;
	private static final int ID_AGGIUNGI = 3;

	private int mSelectedRow = 0;
	private PullToRefreshListView mList;
	private MyCheckable<ECUser>[] match_confirmed_partecipants;
	private MyCheckable<ECUser>[] match_invited_partecipants;
	private String tabString;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_navigation_confermati);
		Parcelable[] tmpParcelableArr = getIntent().getExtras()
				.getParcelableArray(SchedaPartita.EXTRAKEY_CONFIRMED);
		match_confirmed_partecipants = new MyCheckable[tmpParcelableArr.length];
		for (int i = 0; i < tmpParcelableArr.length; i++) {
			match_confirmed_partecipants[i] = new ECUserWrapper(
					(ECUser) tmpParcelableArr[i], false);
		}
		tmpParcelableArr = getIntent().getExtras().getParcelableArray(
				SchedaPartita.EXTRAKEY_INVITED);
		
		Parcelable[] tmpParcelableArr2 = getIntent().getExtras()
				.getParcelableArray(SchedaPartita.EXTRAKEY_REFUSED);
		/*
		if (tmpParcelableArr == null){
			Log.d("Invitati", "null");
			tmpParcelableArr = new Parcelable[0];
		}
		if (tmpParcelableArr2 == null){
			Log.d("Refused", "null");
			tmpParcelableArr2 = new Parcelable[0];
		}
		*/
		match_invited_partecipants = new MyCheckable[tmpParcelableArr.length+ tmpParcelableArr2.length];
		for (int i = 0, j = tmpParcelableArr.length; i < ((tmpParcelableArr.length > tmpParcelableArr2.length) ? tmpParcelableArr.length
				: tmpParcelableArr2.length); i++) {
			if (i < tmpParcelableArr.length)
				match_invited_partecipants[i] = new ECUserWrapper(
						(ECUser) tmpParcelableArr[i], false);
			if (i < tmpParcelableArr2.length)
				match_invited_partecipants[i + j] = new ECUserWrapper(
						(ECUser) tmpParcelableArr2[i], true);
		}

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Partecipanti");
		// creo tab
		ActionBar.Tab tabAmici = getSupportActionBar().newTab();
		tabAmici.setText("Confermati");
		tabAmici.setTabListener(this);
		getSupportActionBar().addTab(tabAmici);
		ActionBar.Tab tabOspiti = getSupportActionBar().newTab();
		tabOspiti.setText("Invitati");
		tabOspiti.setTabListener(this);
		getSupportActionBar().addTab(tabOspiti);

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction transaction) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		tabString = tab.getText().toString();
		if (tab.getText().equals("Confermati")) {
			setContentView(R.layout.tab_navigation_confermati);
			caricaConfermati();
		} else if (tab.getText().equals("Invitati")) {
			setContentView(R.layout.tab_navigation_invitati);
			caricaInvitati();
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
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
		 * if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
		 * menu.add(1,2,2,"More").setIcon(isLight ?
		 * R.drawable.ic_action_overflow_black : R.drawable.ic_action_overflow)
		 * .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); }
		 */
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

		case 2:
			openOptionsMenu();
			break;
		case 3:
			Toast.makeText(this, "Cliccato: " + item.toString(),
					Toast.LENGTH_SHORT).show();
			break;
		case 4:
			Toast.makeText(this, "Cliccato: " + item.toString(),
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
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void caricaConfermati() {

		mList = (PullToRefreshListView) findViewById(R.id.list);

		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents goes here

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				mList.postDelayed(new Runnable() {

					@Override
					public void run() {
						((PullToRefreshListView) mList).onRefreshComplete();
					}
				}, 2000);
			}
		});

		NewQAAdapterInvitedPlayers adapter = new NewQAAdapterInvitedPlayers(
				this);

		adapter.setData(match_confirmed_partecipants);
		mList.setAdapter(adapter);
		aggiungiQuickActionConfermati();

	}

	public void caricaInvitati() {
		mList = (PullToRefreshListView) findViewById(R.id.list);

		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// Your code to refresh the list contents goes here

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
				mList.postDelayed(new Runnable() {

					@Override
					public void run() {
						((PullToRefreshListView) mList).onRefreshComplete();
					}
				}, 2000);
			}
		});

		NewQAAdapterInvitedPlayers adapter = new NewQAAdapterInvitedPlayers(
				this);

		adapter.setData(match_invited_partecipants);
		mList.setAdapter(adapter);
		aggiungiQuickActionInvitati();
	}

	public void aggiungiQuickActionInvitati() {

		ActionItem profileItem = new ActionItem(ID_PROFILE, "Profilo",
				getResources().getDrawable(R.drawable.profilo_utente));
		ActionItem sendItem = new ActionItem(ID_SEND, "Messaggio",
				getResources().getDrawable(R.drawable.send_email));
		ActionItem addFriendItem = new ActionItem(ID_AGGIUNGI,
				"Aggiungi Amico", getResources().getDrawable(
						R.drawable.add_friend));
		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(profileItem);
		mQuickAction.addActionItem(sendItem);
		mQuickAction.addActionItem(addFriendItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);
						switch (actionId) {		
						
						case ID_PROFILE:
							Intent intentProfilo = new Intent(InvitedPlayers.this,
									ProfiloAmico.class);
							MyCheckable<ECUser>[] tmpArr = (tabString.equals("Confermati") ? match_confirmed_partecipants
									: match_invited_partecipants);
							intentProfilo.putExtra(Profilo.EXTRAKEY_ECUSER,
									tmpArr[mSelectedRow].getData());
							startActivity(intentProfilo);
							break;
						
						case ID_SEND:
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							emailIntent.setType("plain/text");
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT, "");
							startActivity(Intent.createChooser(emailIntent,
									"Con quale app vuoi mandare l'email:"));
							break;

						case ID_AGGIUNGI:
							MyCheckable<ECUser>[] tmpArr2 = tabString
							.equals("Confermati") ? match_confirmed_partecipants
							: match_invited_partecipants;
							if(tmpArr2[mSelectedRow].getData().get_id() == getMyApplication().getOwner().get_id()){
								Toast.makeText(getApplicationContext(), "Non puoi aggiungere te stesso come amico", Toast.LENGTH_SHORT).show();
							}else{
								addFriend(tmpArr2[mSelectedRow].getData());
							}
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"Funzionalità in fase di sviluppo",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				// mMoreIv.setImageResource(R.drawable.ic_list_more);
			}
		});

		mList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedRow = position-1; // set the selected row
				mQuickAction.show(view);
			}
		});

	}

	public void aggiungiQuickActionConfermati() {

		ActionItem profileItem = new ActionItem(ID_PROFILE, "Profilo",
				getResources().getDrawable(R.drawable.profilo_utente));
		// ActionItem sendItem = new ActionItem(ID_SEND, "Messaggio",
		// getResources().getDrawable(R.drawable.ic_menu_send));
		ActionItem addFriendItem = new ActionItem(ID_AGGIUNGI,
				"Aggiungi Amico", getResources().getDrawable(
						R.drawable.add_friend));
		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(profileItem);
		// mQuickAction.addActionItem(sendItem);
		mQuickAction.addActionItem(addFriendItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						switch (actionId) {		
						
						case ID_PROFILE:
							Intent intentProfilo = new Intent(InvitedPlayers.this,
									ProfiloAmico.class);
							MyCheckable<ECUser>[] tmpArr = (tabString.equals("Confermati") ? match_confirmed_partecipants
									: match_invited_partecipants);
							intentProfilo.putExtra(Profilo.EXTRAKEY_ECUSER,
									tmpArr[mSelectedRow].getData());
							startActivity(intentProfilo);
							break;
						
						case ID_SEND:
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							emailIntent.setType("plain/text");
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT, "");
							startActivity(Intent.createChooser(emailIntent,
									"Con quale app vuoi mandare l'email:"));
							break;
							
						case ID_AGGIUNGI:
							MyCheckable<ECUser>[] tmpArr2 = tabString
									.equals("Confermati") ? match_confirmed_partecipants
									: match_invited_partecipants;
							if(tmpArr2[mSelectedRow].getData().get_id() == getMyApplication().getOwner().get_id()){
								Toast.makeText(getApplicationContext(), "Non puoi aggiungerti come amico", Toast.LENGTH_SHORT).show();
							}else{
							addFriend(tmpArr2[mSelectedRow].getData());
							}
							break;
						default:
							Toast.makeText(getApplicationContext(),
									"Funzionalit� in fase di sviluppo",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				// mMoreIv.setImageResource(R.drawable.ic_list_more);
			}
		});

		mList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedRow = position-1; // set the selected row
				mQuickAction.show(view);
			}
		});

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
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogAmiciMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	/*
	private void addFriend(ECUser friend) {
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		long userID = getMyApplication().getOwner().get_id();
		String friendPhone = friend.getNum_tel();
		Message msg = MessagesCreator.getAddFriendMessage(msnger, userID,
				friendPhone);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	
	
	private void addFriend(ECUser friend) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC,
				ECConnectionMessageConstants.FUNCDESCRIPTOR_ADD_FRIEND));
		params.add(new BasicNameValuePair("user_id", String.valueOf(getMyApplication().getOwner().get_id())));
		params.add(new BasicNameValuePair("num_tel", String.valueOf(friend.getNum_tel())));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(InvitedPlayers.this);
				pDialog.setMessage("Aggiungo amico...");
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
				Toast.makeText(getApplicationContext(),
						"Hai aggiunto un'amico alla tua lista",
						Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(),
						"Impossibile aggiungere l'amico selezionato",
						Toast.LENGTH_SHORT).show();
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
		// Message msg = MessagesCreator.getConfirmRegistrationMessage(msnger,
		// registration);

	}
	
	
	@Override
	protected Handler getConnectionServiceHandler() {

		return new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_ADD_FRIEND:
						Toast.makeText(getApplicationContext(),
								"Hai aggiunto un'amico alla tua lista",
								Toast.LENGTH_SHORT).show();
						break;
					}

					// Log.d("friends", "" + friends.length);
					// Log.d("acquietances", "" + acquietances.length);
					// ((NewQAAdapter)((HeaderViewListAdapter)
					// mList.getAdapter()).getWrappedAdapter())
					// .notifyDataSetChanged();
					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_ADD_FRIEND:
						Toast.makeText(getApplicationContext(),
								"Impossibile aggiungere l'amico selezionato",
								Toast.LENGTH_SHORT).show();
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

	private class ECUserWrapper implements MyCheckable<ECUser> {

		private ECUser ecu;
		private boolean checked;

		public ECUserWrapper(ECUser ecu, boolean checked) {
			this.ecu = ecu;
			this.checked = checked;
		}

		@Override
		public ECUser getData() {
			return ecu;
		}

		@Override
		public boolean isChecked() {
			return checked;
		}

		@Override
		public void setChecked(boolean value) {
			checked = value;
		}

		@Override
		public void toggle() {
			checked = !checked;
		}

	}

}
