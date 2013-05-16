package edu.easycalcetto.activities;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_CREATEMATCH;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETFRIENDS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_CLOSED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_GETMATCHES_OPEN;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.connection.ECConnectionService;
import edu.easycalcetto.connection.ECHttpClient;
import edu.easycalcetto.connection.ECPostWithBNVPTask;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class InvitaAmiciNuovaPartita extends EasyCalcettoActivity {

	public final static String EXTRAKEY_MATCH = "MATCH";
	private final static int INFO_DIALOG = 1;
	private final static int PROGRESS_DIALOG = 2;
	private ListView mList;
	private CheckBox checkAll;
	private Button buttonCreaPartita;
	private CheckWrapper<ECUser>[] people = new CheckWrapper[0];
	private int peopleSize;
	ProgressDialog progDialog;
	int delay = 500000; // Milliseconds of delay in the update loop
	int maxBarValue = 200;

	private ECMatch match;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_layout_select_friend);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Nuova partita 2/2");
		getSupportActionBar().setSubtitle("Spunta gli amici che vuoi invitare");
		caricaAmici();

		buttonCreaPartita = (Button) findViewById(R.id.buttonCreaPartita);

		buttonCreaPartita.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ECUser[] selectedFriends = new ECUser[peopleSize];
				int countSelected = 0;
				for (int i = 0; i < peopleSize; i++) {
					if (people[i].isChecked()) {
						selectedFriends[countSelected] = people[i].getData();
						countSelected++;
					}
				}
				if (countSelected > 0) {
					showDialog(PROGRESS_DIALOG);
					sendCreateMatch();
				} else {
					Toast.makeText(getApplicationContext(),
							"Devi scegliere almeno un amico",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		checkAll = (CheckBox) findViewById(R.id.checkBoxAll);
		checkAll.setChecked(false);

		checkAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if ((checkAll.isChecked())) {

					for (int i = 0; i < peopleSize; i++) {
						// isCheckedArray[i] = true;
						people[i].setChecked(true);
						Log.i(people[i].getData().getName(), "valore"
								+ people[i].isChecked());
						((BaseAdapter) mList.getAdapter())
								.notifyDataSetChanged();
					}
				}

				else if (!(checkAll.isChecked())) {
					for (int i = 0; i < peopleSize; i++) {
						// isCheckedArray[i] = false;
						people[i].setChecked(false);
						Log.i(people[i].getData().getName(), "valore"
								+ people[i].isChecked());
						((BaseAdapter) mList.getAdapter())
								.notifyDataSetChanged();
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			match = (ECMatch) getIntent().getExtras().getParcelable(
					EXTRAKEY_MATCH);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"Si è verificato un'Errore", Toast.LENGTH_SHORT).show();
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

	public void caricaAmici() {
		mList = (ListView) findViewById(R.id.list);
		peopleSize = people.length;
		NewQAAdapterSelectFriends adapter = new NewQAAdapterSelectFriends(this,
				people);
		// adapter.setData(people);
		mList.setAdapter(adapter);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_DIALOG:
			return createInfoDialog();
		case PROGRESS_DIALOG:
			ProgressDialog progressDialog = ProgressDialog.show(this,
					"Attendere", "Creazione partita in corso...");
			progressDialog.setCancelable(true);
			progressDialog.setMax(10);
			// progDialog = new ProgressDialog(this);
			// progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// progDialog.setMessage("Loading...");
			return progressDialog;
		default:
			return null;
		}
	}

	private final Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoDialogNuovaPartitaInvitaAmiciMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismissDialog(INFO_DIALOG);
					}
				});
		return builder.create();
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
						ECUser[] friends = (ECUser[]) msg
								.getData()
								.getParcelableArray(
										ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);

						for (ECUser user : friends) {
							File f = new File(
									getMyApplication().getImagesDir(),
									user.getPhotoName());
							if (!f.exists())
								al.add(user);
						}
						if (al.isEmpty()) {
							caricaAmici();
						} else {
							updatePhotos(al);
						}
						people = new CheckWrapper[friends.length];
						for (int i = 0; i < friends.length; i++)
							people[i] = new CheckWrapper<ECUser>(friends[i]);
						caricaAmici();
						break;
					case ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CREATEMATCH:
						Toast.makeText(getApplicationContext(),
								"Partita Creata", Toast.LENGTH_LONG).show();
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

	private void getAmici() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_GETFRIENDS));
		params.add(new BasicNameValuePair("id", String
				.valueOf(getMyApplication().getOwner().get_id())));

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(InvitaAmiciNuovaPartita.this);
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
					ECUser[] friends = ECUser
							.createFromJSONArray(getDataJArr());

					for (ECUser user : friends) {
						if (!user.getPhotoName().equalsIgnoreCase(
								ECUser.IMAGE_FILE_NAME_DEFAULT)) {
							File f = new File(
									getMyApplication().getImagesDir(),
									user.getPhotoName());
							if (!f.exists())
								al.add(user);
						}
					}
					if (al.isEmpty()) {
						caricaAmici();
					} else {
						updatePhotos(al);
					}
					people = new CheckWrapper[friends.length];
					for (int i = 0; i < friends.length; i++)
						people[i] = new CheckWrapper<ECUser>(friends[i]);
					caricaAmici();
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
		// long id = getMyApplication().getOwner().get_id();
		// Messenger msnger = new Messenger(getConnectionServiceHandler());
		// Message msg = MessagesCreator.getGetFriendsMessage(msnger, id);
		// try {
		// messenger.send(msg);
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private void sendCreateMatch() {
		ECUser[] ecuArr = new ECUser[people.length];
		for (int i = 0; i < people.length; i++) {
			ecuArr[i] = people[i].getData();
		}
		match.setPartecipants(ecuArr);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(FUNC, FUNCDESCRIPTOR_CREATEMATCH));
		params.addAll(match.getObjectAsNameValuePairList());

		ECPostWithBNVPTask task = new ECPostWithBNVPTask() {
			ProgressDialog pDialog = null;

			@Override
			protected void onPreExecute() {
				pDialog = new ProgressDialog(InvitaAmiciNuovaPartita.this);
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
				Toast.makeText(getApplicationContext(),"Partita Creata", Toast.LENGTH_LONG).show();
				finish();
			}

			@Override
			protected void onSuccess() {
				// TODO Auto-generated method stub
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
	protected void onServiceConnected() {
		getAmici();
	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub

	}

	private void updatePhotos(ArrayList<ECUser> al) {

		DefaultHttpClient client = new ECHttpClient();

		new ImageDownloadTask(client, getMyApplication().getImagesDir())
				.execute(al.toArray(new ECUser[0]));

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
					File file = new File(getMyApplication().getImagesDir(),
							ecu.getPhotoName());
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
					ecu.setPhotoName(file.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}

}
