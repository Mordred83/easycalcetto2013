package edu.easycalcetto.activities;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_GAME;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_DECLINE_GAME;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_CLOSED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_OPEN;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECMatch;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class Partite extends EasyCalcettoActivity implements
		ActionBar.TabListener {

	private final static int INFO_DIALOG = 1;
	private static final int ID_ACCEPT = 1;
	private static final int ID_MAYBE = 2;
	private static final int ID_NO = 3;
	private static final int ID_VOTA = 1;
	protected static final String EXTRAKEY_MATCH = "MATCH";
	protected static final int STARTFLAG_SHEDA_PARTITA = 1;
	private int mSelectedRow = -1;
	private PullToRefreshListView mList;
	private ImageView mImage;
	private TextView mCounterPlayerText;
	private TextView mDataDisplayed;
	private TextView mNameMatchText;
	private ECMatch[] matchs = new ECMatch[0];
	private ECMatch[] matchs_played = new ECMatch[0];

	public int posElement;
	private TabID currentTab = null;
	private AdapterView.OnItemClickListener rowListener = new RowListner();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_navigation_match_opened);
		currentTab = TabID.APERTE;
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Partite");
		// creo tab
		ActionBar.Tab tabAperte = getSupportActionBar().newTab();
		tabAperte.setText("Aperte");
		tabAperte.setTabListener(this);
		getSupportActionBar().addTab(tabAperte);
		ActionBar.Tab tabChiuse = getSupportActionBar().newTab();
		tabChiuse.setText("Giocate");
		tabChiuse.setTabListener(this);
		getSupportActionBar().addTab(tabChiuse);
	}

	@Override
	public void onResume() {
		super.onResume();
		mList.setRefreshing();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction transaction) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		if (tab.getText().equals("Aperte")) {
			currentTab = TabID.APERTE;
			setContentView(R.layout.tab_navigation_match_opened);
			caricaAperte();
		} else if (tab.getText().equals("Giocate")) {
			setContentView(R.layout.tab_navigation_match_closed);
			currentTab = TabID.GIOCATE;
			caricaChiuse();
		}
		mList.setRefreshing();
		getListsFromServer();
	}

	private void getListsFromServer() {
		mList.setRefreshing();
		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = null;
		switch (currentTab) {
		case APERTE:
			// msg = MessagesCreator.getGetOpenMatchesMessage(msnger,
			// getMyApplication().getOwner().get_id());
			getOpenMatches();
			break;
		case GIOCATE:
			// msg = MessagesCreator.getGetClosedMatchesMessage(msnger,
			// getMyApplication().getOwner().get_id());
			getClosedMatches();
			break;
		}
		// if (msg != null && messenger != null)
		// try {
		// messenger.send(msg);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// if (mList.isRefreshing()) {
		// }

	}

	private void getClosedMatches() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_GETMATCHES_CLOSED));
		params.add(new BasicNameValuePair("id", String
				.valueOf(getMyApplication().getOwner().get_id())));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Partite.this);
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
					matchs_played = ECMatch.createFromJSONArray(getDataJArr());
					caricaChiuse();
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

	}

	private void getOpenMatches() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_GETMATCHES_OPEN));
		params.add(new BasicNameValuePair("id", String
				.valueOf(getMyApplication().getOwner().get_id())));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Partite.this);
				pDialog.setMessage("Carico le partite in sospeso");
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
					matchs = ECMatch.createFromJSONArray(getDataJArr());
					caricaAperte();
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
		 * menu.add(1, 2, 2, "More") .setIcon( isLight ?
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
			// openOptionsMenu();
			Toast.makeText(this, "Non ci sono altre opzioni disponibili",
					Toast.LENGTH_SHORT).show();
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

	@SuppressWarnings("deprecation")
	public void caricaAperte() {

		mList = (PullToRefreshListView) findViewById(R.id.list);

		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getListsFromServer();
			}
		});

		NewQAAdapterMatchs adapter = new NewQAAdapterMatchs(this);
		adapter.setData(matchs);
		mList.setAdapter(adapter);

		mList.setOnItemClickListener(rowListener);

		aggiungiQuickActionAperte();

	}

	public void caricaChiuse() {
		mList = (PullToRefreshListView) findViewById(R.id.list);

		mList.setOnRefreshListener(new OnRefreshListener() {
			//
			@Override
			public void onRefresh() {
				getListsFromServer();
			}
		});

		NewQAAdapterMatchs adapter = new NewQAAdapterMatchs(this);
		adapter.setData(matchs_played);
		mList.setAdapter(adapter);

		mList.setOnItemClickListener(rowListener);

		aggiungiQuickActionChiuse();

	}

	public void aggiungiQuickActionAperte() {

		ActionItem acceptItem = new ActionItem(ID_ACCEPT, "Partecipo",
				getResources().getDrawable(R.drawable.btn_check_buttonless_on));
		// ActionItem maybeItem = new ActionItem(ID_MAYBE, "Non lo so",
		// getResources().getDrawable(R.drawable.ic_menu_help));
		ActionItem removeItem = new ActionItem(ID_NO, "Non partecipo",
				getResources().getDrawable(R.drawable.ic_delete));

		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(acceptItem);
		// mQuickAction.addActionItem(maybeItem);
		mQuickAction.addActionItem(removeItem);
		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						int playerMax;
						int counter;
						mCounterPlayerText = (TextView) mList.getChildAt(
								mSelectedRow + 1).findViewById(
								R.id.matchPlayers);
						mDataDisplayed = (TextView) mList.getChildAt(
								mSelectedRow + 1).findViewById(R.id.matchDate);
						mImage = (ImageView) mList.getChildAt(mSelectedRow + 1)
								.findViewById(R.id.matchRightImage);
						ECMatch match = (ECMatch) mList
								.getItemAtPosition(mSelectedRow + 1);

						if (actionId == ID_ACCEPT) {
							// if (match.getUserStatus() ==
							// R.drawable.btn_check_buttonless_on) {
							// Toast.makeText(getApplicationContext(),
							// "Partecipi a questa partita",
							// Toast.LENGTH_LONG).show();
							// } else {
							// playerMax = matchs[mSelectedRow]
							// .getNumberMaxPlayer();
							// counter = matchs[mSelectedRow]
							// .getCounterPlayer() + 1;
							// matchs[mSelectedRow].setCounterPlayer(counter);
							// String partecipanti = (counter) + "/"
							// + playerMax;
							// mCounterPlayerText.setText("Partecipanti: "
							// + partecipanti);
							// //
							// matchs[mSelectedRow].setRightImage(R.drawable.btn_check_buttonless_on);
							// mImage.setImageResource(R.drawable.btn_check_buttonless_on);
							// Toast.makeText(getApplicationContext(),
							// "Aggiunto alla Partita",
							// Toast.LENGTH_LONG).show();
							// }
							confirmGame();
						} else if (actionId == ID_NO) {
							declineGame();
							// if (match.getRightImage() ==
							// R.drawable.btn_check_buttonless_on) {
							// playerMax = matchs[mSelectedRow]
							// .getNumberMaxPlayer();
							// counter = matchs[mSelectedRow]
							// .getCounterPlayer() - 1;
							// matchs[mSelectedRow].setCounterPlayer(counter);
							// String partecipanti = (counter) + "/"
							// + playerMax;
							// mCounterPlayerText.setText("Partecipanti: "
							// + partecipanti);
							// //
							// matchs[mSelectedRow].setRightImage(R.drawable.ic_delete);
							// mImage.setImageResource(R.drawable.ic_delete);
							// Toast.makeText(getApplicationContext(),
							// "Rimosso dalla partita",
							// Toast.LENGTH_LONG).show();
							// } else
							// Toast.makeText(getApplicationContext(),
							// "Non partecipo", Toast.LENGTH_LONG)
							// .show();
							// mImage.setImageResource(R.drawable.ic_delete);
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
		/*
		 * mList.setOnItemClickListener(new OnItemClickListener() { public void
		 * onItemClick(AdapterView<?> parent, View view, int position, long id)
		 * { mSelectedRow = position; //set the selected row
		 * mQuickAction.show(view); } });
		 */

		mList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedRow = position - 1; // set the selected row
				mQuickAction.show(view);
				return false;
			}
		});
	}

	public void aggiungiQuickActionChiuse() {

		ActionItem ratingItem = new ActionItem(ID_VOTA, "Vota i partecipanti",
				getResources().getDrawable(R.drawable.btn_star_big_on));
		// ActionItem sendItem = new ActionItem(ID_SEND, "Email",
		// getResources().getDrawable(R.drawable.ic_menu_send));
		// ActionItem blockItem = new ActionItem(ID_BLOCK, "Blocca",
		// getResources().getDrawable(R.drawable.ic_menu_blocked_user));
		// ActionItem deleteItem = new ActionItem(ID_BLOCK, "Elimina",
		// getResources().getDrawable(R.drawable.ic_menu_delete));

		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(ratingItem);
		// mQuickAction.addActionItem(sendItem);
		// mQuickAction.addActionItem(blockItem);
		// mQuickAction.addActionItem(deleteItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						// TODO
						// ActionItem actionItem =
						// quickAction.getActionItem(pos);
						// Match match = (Match) mList
						// .getItemAtPosition(mSelectedRow + 1);
						// mImage = (ImageView) mList.getChildAt(mSelectedRow +
						// 1)
						// .findViewById(R.id.matchRightImage);
						// if (actionId == ID_VOTA) {
						// if (match.getRightImage() ==
						// R.drawable.btn_rating_star_off_normal) {
						// Toast.makeText(getApplicationContext(),
						// "lancio Activity votazione",
						// Toast.LENGTH_LONG).show();
						// //
						// matchs[mSelectedRow].setRightImage(R.drawable.btn_rating_star_off_pressed);
						// mImage.setImageResource(R.drawable.btn_rating_star_off_pressed);
						// } else if (match.getRightImage() ==
						// R.drawable.btn_rating_star_off_pressed)
						// Toast.makeText(getApplicationContext(),
						// "Hai gi� votato", Toast.LENGTH_LONG)
						// .show();
						// }
					}
				});

		// setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				// mMoreIv.setImageResource(R.drawable.ic_list_more);
			}
		});

		/*
		 * mList.setOnItemClickListener(new OnItemClickListener() { public void
		 * onItemClick(AdapterView<?> parent, View view, int position, long id)
		 * { mSelectedRow = position; //set the selected row Intent
		 * intentPartita=new Intent(getApplicationContext(),
		 * SchedaPartita.class); startActivity(intentPartita); } });
		 */

		mList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelectedRow = position - 1; // set the selected row
				mQuickAction.show(view);
				return false;
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
		builder.setMessage(R.string.infoDialogPartiteMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
	}

	// TODO
	// public void ordinaPerData(Match[] matchs) {
	// Arrays.sort(matchs, new Comparator<Match>() {
	// int i = -1;
	// int j = -1;
	// int temp = -1;
	// Person tempPerson;
	//
	// @Override
	// public int compare(Match lhs, Match rhs) {
	// MyData d1 = new MyData(lhs.getDate().getGiorno(), lhs.getDate()
	// .getMese(), lhs.getDate().getAnno());
	// MyData d2 = new MyData(rhs.getDate().getGiorno(), rhs.getDate()
	// .getMese(), rhs.getDate().getAnno());
	// Person person1 = lhs.getOwner();
	// Person person2 = rhs.getOwner();
	// Log.e(person1.getName(), person1.getSurname());
	// Log.e(person2.getName(), person2.getSurname());
	//
	// if (d1.getAnno() < d2.getAnno()) {
	// return -1;
	// } else if (d1.getAnno() == d2.getAnno()) {
	// if (d1.getMese() < d2.getMese()) {
	// return -1;
	// } else if (d1.getMese() == d2.getMese()) {
	// if (d1.getGiorno() < d2.getGiorno()) {
	// return -1;
	// } else if (d1.getGiorno() == d2.getGiorno()) {
	// return 0;
	// } else if (d1.getGiorno() > d2.getGiorno()) {
	// /*
	// * i=(Arrays.asList(people)).indexOf(person1);
	// * j=(Arrays.asList(people)).indexOf(person2);
	// * tempPerson=people[i]; people[i]=people[j];
	// * people[j]=tempPerson; Log.i("indice:"+i,
	// * people[i].getName()); Log.i("indice:"+j,
	// * people[j].getName());
	// */
	// return 1;
	//
	// }
	// } else if (d1.getMese() >= d2.getMese()) {
	// /*
	// * i=(Arrays.asList(people)).indexOf(person1);
	// * j=(Arrays.asList(people)).indexOf(person2);
	// * tempPerson=people[i]; people[i]=people[j];
	// * people[j]=tempPerson; Log.i("indice:"+i,
	// * people[i].getName()); Log.i("indice:"+j,
	// * people[j].getName());
	// */
	// return 1;
	// }
	// } else {
	// /*
	// * i=(Arrays.asList(people)).indexOf(person1);
	// * j=(Arrays.asList(people)).indexOf(person2);
	// * tempPerson=people[i]; people[i]=people[j];
	// * people[j]=tempPerson; Log.i("indice:"+i,
	// * people[i].getName()); Log.i("indice:"+j,
	// * people[j].getName()); return 1;
	// */
	// }
	// return 0;
	// }
	// });
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (STARTFLAG_SHEDA_PARTITA):
			getListsFromServer();
			break;
		case (2): {
			// TODO
			// if (resultCode == Activity.RESULT_OK) {
			// boolean changes = data.getBooleanExtra("changes", false);
			// Log.i("changes", "valore: " + changes);
			// int idMatch = data.getIntExtra("idMatch", -1);
			// int giornoMatch = data.getIntExtra("dataMatch.giorno", -1);
			// int meseMatch = data.getIntExtra("dataMatch.mese", -1);
			// int annoMatch = data.getIntExtra("dataMatch.anno", -1);
			// String startHour = data.getStringExtra("startHourMatch");
			// String newPlaceMatch = data.getStringExtra("newPlaceMatch");
			// boolean changesData = data
			// .getBooleanExtra("changesData", false);
			// String nameMatch = data.getStringExtra("nomeMatch");
			// mDataDisplayed = (TextView) mList.getChildAt(mSelectedRow)
			// .findViewById(R.id.matchDate);
			// mNameMatchText = (TextView) mList.getChildAt(mSelectedRow + 1)
			// .findViewById(R.id.matchName);
			// mNameMatchText.setText(nameMatch);
			// if (changes == true) {
			// for (int i = 0; i < matchs.length; i++) {
			// if (matchs[i].getIdMatch() == idMatch) {
			// if (changesData == true) {
			// mDataDisplayed = (TextView) mList.getChildAt(
			// i + 1).findViewById(R.id.matchDate);
			// MyData dataToDisplay = new MyData(giornoMatch,
			// meseMatch, annoMatch);
			// matchs[i].setDate(dataToDisplay);
			// mDataDisplayed
			// .setText(dataToDisplay.toString());
			// }
			// matchs[i].setPlace(newPlaceMatch);
			// matchs[i].setName(nameMatch);
			// }
			// }
			// }
			// }
		}
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
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_OPEN:
						matchs = (ECMatch[]) msg
								.getData()
								.getParcelableArray(
										ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);
						caricaAperte();
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_CLOSED:
						matchs_played = (ECMatch[]) msg
								.getData()
								.getParcelableArray(
										ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);

						caricaChiuse();
						break;
					}

					break;
				case ECConnectionMessageConstants.RES_KIND_FAILURE:
					break;
				default:
					break;
				}
				mList.onRefreshComplete();
			}
		};
	}

	private void confirmGame() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC,
				FUNCDESCRIPTOR_CONFIRM_GAME));
		params.add(new BasicNameValuePair("player_id", String
				.valueOf(getMyApplication().getOwner().get_id())));
		params.add(new BasicNameValuePair("match_id", String
				.valueOf(matchs[mSelectedRow].getIdMatch())));
		params.add(new BasicNameValuePair("data_id", String.valueOf(1)));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Partite.this);
				pDialog.setMessage("Invio Informazioni");
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
			protected void onJArrNULL() {
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

		task.execute(params.toArray(new BasicNameValuePair[] {}));
	}

	private void declineGame() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC,
				FUNCDESCRIPTOR_DECLINE_GAME));
		params.add(new BasicNameValuePair("player_id", String
				.valueOf(getMyApplication().getOwner().get_id())));
		params.add(new BasicNameValuePair("match_id", String
				.valueOf(matchs[mSelectedRow].getIdMatch())));
		params.add(new BasicNameValuePair("data_id", String.valueOf(1)));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Partite.this);
				pDialog.setMessage("Invio Informazioni");
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
			protected void onJArrNULL() {
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

		task.execute(params.toArray(new BasicNameValuePair[] {}));
	}

	@Override
	protected void onServiceConnected() {
		getListsFromServer();
	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

	private enum TabID {
		APERTE, GIOCATE
	}

	private class RowListner implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mSelectedRow = position - 1; // set the selected row
			// Log.i("indice","riga"+mSelectedRow);
			Intent intentPartita;

			ECMatch[] tmpMatchs = currentTab == TabID.APERTE ? matchs
					: matchs_played;

			if (tmpMatchs[mSelectedRow].getOwner().equals(
					getMyApplication().getOwner())) {
				if (currentTab == TabID.APERTE) {
					intentPartita = new Intent(getApplicationContext(),
							SchedaPartitaOwner.class);
				} else {
					intentPartita = new Intent(getApplicationContext(),
							SchedaPartitaGiocata.class);
				}
			} else {
				if (currentTab == TabID.APERTE)
					intentPartita = new Intent(getApplicationContext(),
							SchedaPartita.class);
				else
					intentPartita = new Intent(getApplicationContext(),
							SchedaPartitaGiocata.class);

			}

			intentPartita.putExtra(EXTRAKEY_MATCH, tmpMatchs[mSelectedRow]);
			startActivityForResult(intentPartita, STARTFLAG_SHEDA_PARTITA);
		}

	}

}
