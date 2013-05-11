package edu.easycalcetto.activities;

import static edu.easycalcetto.ApplicationStatus.REGISTERED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_CONFIRM_REGISTRATION;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETFRIENDS;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.ECApplication;
import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECConnectionService;
import edu.easycalcetto.connection.ECHttpClient;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class Amici extends EasyCalcettoActivity implements
		ActionBar.TabListener {
	private final static int INFO_DIALOG = 1;
	private static final int SEND_DIALOG = 2;
	private static final int ID_PROFILE = 1;
	private static final int ID_SEND = 2;
	private static final int ID_BLOCK = 3;
	private static final int ID_AGGIUNGI = 2;
	private ListImageMatch[] dialogImages;
	private Button addFriends;

	private TabID currentTab = null;
	private ECUser[] friends = new ECUser[0];
	private ECUser[] acquietances = new ECUser[0];
	private Adapter adapter;
	private int mSelectedRow = 0;
	private PullToRefreshListView mList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_navigation_friend);
		currentTab = TabID.AMICI;
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Amici");
		// creo tab
		ActionBar.Tab tabAmici = getSupportActionBar().newTab();
		tabAmici.setText("Amici");
		tabAmici.setTabListener(this);
		getSupportActionBar().addTab(tabAmici);
		ActionBar.Tab tabOspiti = getSupportActionBar().newTab();
		tabOspiti.setText("Altri");
		tabOspiti.setTabListener(this);
		getSupportActionBar().addTab(tabOspiti);
		loadListImageSendEC();
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
		if (tab.getText().equals("Amici")) {
			currentTab = TabID.AMICI;
			setContentView(R.layout.tab_navigation_friend);
			addFriends = (Button) findViewById(R.id.addFriend);
			addFriends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					showDialog(SEND_DIALOG);
				}

			});

			caricaAmici();
		} else if (tab.getText().equals("Altri")) {
			currentTab = TabID.ALTRI;
			setContentView(R.layout.tab_navigation_others);
			caricaOspiti();
		}
		mList.setRefreshing();
		getListsFromServer();

	}

	private void getListsFromServer() {

		Messenger msnger = new Messenger(getConnectionServiceHandler());
		Message msg = null;
		switch (currentTab) {
		case AMICI:
//			msg = MessagesCreator.getGetFriendsMessage(msnger,
//					getMyApplication().getOwner().get_id());
			getFriendsFromServer();
			break;
		case ALTRI:
//			msg = MessagesCreator.getGetAcquaintanceMessage(msnger,
//					getMyApplication().getOwner().get_id());
			getAcquietancesFromServer();
			break;
		}
		if (msg != null && messenger != null)
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			menu.add(1, 2, 2, "More")
					.setIcon(
							isLight ? R.drawable.ic_action_overflow_black
									: R.drawable.ic_action_overflow)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
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

	public void caricaAmici() {

		mList = (PullToRefreshListView) findViewById(R.id.list);
		// Set the onRefreshListener on the list. You could also use
		// listView.setOnRefreshListener(this); and let this Activity
		// implement OnRefreshListener.
		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				getListsFromServer();
			}
		});

		NewQAAdapter adapter = new NewQAAdapter(this);
		adapter.setData(friends);
		mList.setAdapter(adapter);
		aggiungiQuickActionAmici();

	}

	public void caricaOspiti() {

		mList = (PullToRefreshListView) findViewById(R.id.list);
		// mList = (ListView) findViewById(R.id.list);
		mList.setOnRefreshListener(new OnRefreshListener() {
			//
			@Override
			public void onRefresh() {
				getListsFromServer();
			}
		});
		NewQAAdapter adapter = new NewQAAdapter(this);
		adapter.setData(acquietances);
		mList.setAdapter(adapter);
		aggiungiQuickActionOspiti();
	}

	public void aggiungiQuickActionOspiti() {

		ActionItem profileItem = new ActionItem(ID_PROFILE, "Profilo",
				getResources().getDrawable(R.drawable.profilo_utente));
		ActionItem addItem = new ActionItem(ID_AGGIUNGI, "Aggiungi",
				getResources().getDrawable(R.drawable.add_friend));
		/*
		ActionItem deleteItem = new ActionItem(ID_BLOCK, "Elimina",
				getResources().getDrawable(R.drawable.elimina_amico));
	*/
		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(profileItem);
		mQuickAction.addActionItem(addItem);
		//mQuickAction.addActionItem(deleteItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);

						switch (actionId) {
						case ID_AGGIUNGI:
							addFriend(acquietances[mSelectedRow]);
						break;
						case ID_PROFILE:
							Intent intentProfilo = new Intent(Amici.this,
									ProfiloAmico.class);
							ECUser[] tmpArr = currentTab.equals(TabID.AMICI) ? friends
									: acquietances;
							intentProfilo.putExtra(Profilo.EXTRAKEY_ECUSER,
									tmpArr[mSelectedRow]);
							startActivity(intentProfilo);
							break;
						default:
							Toast.makeText(getApplicationContext(), "Funzionalità in fase di sviluppo", Toast.LENGTH_SHORT).show();
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
				mSelectedRow = position - 1; // set the selected row
				mQuickAction.show(view);
			}
		});

	}

	public void aggiungiQuickActionAmici() {

		ActionItem profileItem = new ActionItem(ID_PROFILE, "Profilo",
				getResources().getDrawable(R.drawable.profilo_utente));
		ActionItem sendItem = new ActionItem(ID_SEND, "Email", getResources()
				.getDrawable(R.drawable.send_email));
		/*
		ActionItem blockItem = new ActionItem(ID_BLOCK, "Blocca",
				getResources().getDrawable(R.drawable.blocca_amico));
		ActionItem deleteItem = new ActionItem(ID_BLOCK, "Elimina",
				getResources().getDrawable(R.drawable.elimina_amico));
		*/
		final QuickAction mQuickAction = new QuickAction(this);

		mQuickAction.addActionItem(profileItem);
		mQuickAction.addActionItem(sendItem);
		//mQuickAction.addActionItem(blockItem);
		//mQuickAction.addActionItem(deleteItem);

		// setup the action item click listener
		mQuickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction quickAction, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);
						if (actionId == ID_PROFILE) {
							Intent intentProfilo = new Intent(Amici.this,
									ProfiloAmico.class);
							ECUser[] tmpArr = currentTab.equals(TabID.AMICI) ? friends
									: acquietances;
							intentProfilo.putExtra(Profilo.EXTRAKEY_ECUSER,
									tmpArr[mSelectedRow]);
							startActivity(intentProfilo);
						}
						if (actionId == ID_SEND) {
							Intent emailIntent = new Intent(
									android.content.Intent.ACTION_SEND);
							emailIntent.setType("plain/text");
							emailIntent.putExtra(
									android.content.Intent.EXTRA_TEXT, "");
							startActivity(Intent.createChooser(emailIntent,
									"Con quale app vuoi mandare l'email:"));
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
				mSelectedRow = position - 1; // set the selected row
				mQuickAction.show(view);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_DIALOG:
			return createInfoDialog();
		case SEND_DIALOG:
			return DialogOptionSendItem();
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

	/**
	 * Create all the choices for the list
	 */
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

	/**
	 * Handle the event to display the AlertDialog with the list
	 */
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

	private void updatePhotos(ArrayList<ECUser> al) {

		DefaultHttpClient client = new ECHttpClient();

		new ImageDownloadTask(client, getMyApplication().getImagesDir())
				.execute(al.toArray(new ECUser[0]));

	}
	
	private void getFriendsFromServer() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_GETFRIENDS));
		params.add(new BasicNameValuePair("id", String.valueOf(getMyApplication().getOwner().get_id())));
		
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Amici.this);
				pDialog.setMessage("Caricando la lista degli amici");
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
				ArrayList<ECUser> al = new ArrayList<ECUser>();
				try {
					friends = ECUser
						.createFromJSONArray(getDataJArr());
				
					for (ECUser user : friends) {
						if (!user.getPhotoName().equalsIgnoreCase(
								ECUser.IMAGE_FILE_NAME_DEFAULT)) {
							File f = new File(getMyApplication()
									.getImagesDir(), user.getPhotoName());
							if (!f.exists())
								al.add(user);
						}
					}

					caricaAmici();
					if (!al.isEmpty())
						updatePhotos(al);		
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
			protected void onJArrNULLCB() {
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
		
		task.execute(params.toArray(new BasicNameValuePair[]{}));
	}

	private void getAcquietancesFromServer() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, ECConnectionMessageConstants.FUNCDESCRIPTOR_GETACQUAINTANCES));
		params.add(new BasicNameValuePair("id", String.valueOf(getMyApplication().getOwner().get_id())));
		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;
			
			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(Amici.this);
				pDialog.setMessage("Caricando la lista degli ospiti");
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
				ArrayList<ECUser> al = new ArrayList<ECUser>();
				try {
					acquietances = ECUser
						.createFromJSONArray(getDataJArr());
				
					for (ECUser user : acquietances) {
						if (!user.getPhotoName().equalsIgnoreCase(
								ECUser.IMAGE_FILE_NAME_DEFAULT)) {
							File f = new File(getMyApplication()
									.getImagesDir(), user.getPhotoName());
							if (!f.exists())
								al.add(user);
						}
					}

					caricaOspiti();
					if (!al.isEmpty())
						updatePhotos(al);		
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
			protected void onJArrNULLCB() {
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
		
		task.execute(params.toArray(new BasicNameValuePair[]{}));
	}
	@Override
	protected Handler getConnectionServiceHandler() {

		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ArrayList<ECUser> al = new ArrayList<ECUser>();
				switch (msg.arg2) {
				case ECConnectionMessageConstants.RES_KIND_SUCCESS:
					switch (msg.arg1) {
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETFRIENDS:
						friends = (ECUser[]) msg
								.getData()
								.getParcelableArray(
										ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);

						for (ECUser user : friends) {
							if (!user.getPhotoName().equalsIgnoreCase(
									ECUser.IMAGE_FILE_NAME_DEFAULT)) {
								File f = new File(getMyApplication()
										.getImagesDir(), user.getPhotoName());
								if (!f.exists())
									al.add(user);
							}
						}

						caricaAmici();
						if (!al.isEmpty())
							updatePhotos(al);
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETACQUAINTANCES:
						acquietances = (ECUser[]) msg
								.getData()
								.getParcelableArray(
										ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);
						for (ECUser user : acquietances) {
							if (user.getPhotoName().equalsIgnoreCase(
									ECUser.IMAGE_FILE_NAME_DEFAULT)) {
								File f = new File(getMyApplication()
										.getImagesDir(), user.getPhotoName());
								if (!f.exists())
									al.add(user);
							}
						}

						caricaOspiti();
						if (!al.isEmpty())
							updatePhotos(al);
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_ADD_FRIEND:
						Toast.makeText(getApplicationContext(),
								"Hai aggiunto un'amico alla tua lista",
								Toast.LENGTH_SHORT).show();
						break;
					}
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
				mList.onRefreshComplete();
			}
		};
	}

	@Override
	protected void onServiceConnected() {
		getListsFromServer();
	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub
	}

	private class ImageDownloadTask extends AsyncTask<ECUser, Void, Void> {

		DefaultHttpClient client;
		File imageDir;

		public ImageDownloadTask(DefaultHttpClient client, File imageDir) {
			this.client = client;
			this.imageDir = imageDir;
		}

		@Override
		protected Void doInBackground(ECUser... params) {
			for (ECUser ecu : params) {
				try {
					final long userId = ecu.get_id();
					final File file = new File(getMyApplication()
							.getImagesDir(), ecu.getPhotoName());
					URL url = new URL(ECConnectionService.SERVER_HR_ADDRESS
							+ "/images/" + ecu.getPhotoName());
					URLConnection ucon = url.openConnection();
					InputStream is = ucon.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					ByteArrayBuffer baf = new ByteArrayBuffer(50);
					int current = 0;
					while ((current = bis.read()) != -1) {
						baf.append((byte) current);
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(baf.toByteArray());
					is.close();
					fos.close();
					FilenameFilter filter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							return filename.matches(userId + "*")
									&& !filename.equals(file.getName());
						}
					};
					for (File f : getMyApplication().getImagesDir().listFiles(
							filter)) {
						f.delete();
					}
					ecu.setPhotoName(file.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			if(currentTab.equals(TabID.AMICI))
				caricaAmici();
			else 
				caricaOspiti();
			super.onPostExecute(result);
		}
	}

	private enum TabID {
		AMICI, ALTRI
	}

}
