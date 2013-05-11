package edu.easycalcetto.connection;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_ID;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_MESSAGE;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_PARAM_KEYS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_POST_IMAGE_PHOTOPATH;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_POST_PARCELABLE;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_RESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNCDESCRIPTOR_UPLOAD_PHOTO;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGRESDESCTIPTION_FAILURE;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGRESDESCTIPTION_SUCCESS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_ADD_FRIEND;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_GAME;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_CREATEMATCH;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_DECLINE_GAME;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETACQUAINTANCES;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETFRIENDS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_CLOSED;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCHES_OPEN;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_REGISTRATON;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEMATCH;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.MSGTASKDESCRIPTOR_UPDATEUSER;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_DATA;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_OPRESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RES_KIND_FAILURE;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RES_KIND_NETWORK_UNAVALIABLE;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RES_KIND_SUCCESS;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RES_KIND_UNKNOWN_ERROR;
import static edu.easycalcetto.data.ECMatch.PARTECIPANT_STATUSES;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import edu.easycalcetto.Constants;
import edu.easycalcetto.data.ECMatch;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.HTTPPostable;
import edu.easycalcetto.data.JSONParser;

public class ECConnectionService extends Service {
	public final String LOGTAG = this.getClass().getSimpleName();
	public static final String SERVER_HR_ADDRESS = "http://www.sapienzaapps.it/easycalcetto/index.php";
	// "http://www.easycalcetto.altervista.org";//
	// "http://192.168.1.80";//"https://www.google.com";
	private Thread worker;
	final Messenger inMessenger = new Messenger(new IncomingHandler());
	// Used to receive messages from the Activity

	private DefaultHttpClient client; // ECHttpClient client;

	// Client for http/https connections

	@Override
	public void onCreate() {
		super.onCreate();
		client = new ECHttpClient(/* getApplication().getApplicationContext() */);
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			worker = new Thread(new RequestRunnable(msg));
			worker.run();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Bundle extras = intent.getExtras();
		// Get messager from the Activity
		if (extras != null) {
			// outMessenger = (Messenger) extras.get("MESSENGER");
		}
		// Return our messenger to the Activity to get commands
		return inMessenger.getBinder();
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	private class RequestRunnable implements Runnable {

		Message msg;

		public RequestRunnable(Message msg) {
			if (msg == null)
				throw new IllegalArgumentException();
			this.msg = msg;
		}

		@Override
		public void run() {
			HttpResponse response = null;
			Messenger outMsnger = msg.replyTo;
			Message responseMes = Message.obtain();
			responseMes.arg2 = -1;
			Bundle data, resultBundle = new Bundle();

			if (!isNetworkAvailable()) {
				Log.e(LOGTAG, "Network is Unavaliable");
				postMessage(msg.replyTo, RES_KIND_NETWORK_UNAVALIABLE, null);
			}
			HttpPost request = new HttpPost(SERVER_HR_ADDRESS);
			data = msg.getData();
			StringEntity entity = null;

			final String funcDescriptor = data.getString(FUNC);
			// final String SUCCESS_RESPONSE = data
			// .getString(MSGRESDESCTIPTION_SUCCESS);
			// final String FAILURE_RESPONSE = data
			// .getString(ECConnectionMessageConstants.MSGRESDESCTIPTION_FAILURE);

			List<NameValuePair> funcdesc = new ArrayList<NameValuePair>();

			funcdesc.add(new BasicNameValuePair(
					ECConnectionMessageConstants.FUNC, funcDescriptor));
			switch (msg.what) {
			case ECConnectionMessageConstants.MSGWHAT_POST:
				switch (msg.arg1) {
				case MSGTASKDESCRIPTOR_GETFRIENDS:
				case MSGTASKDESCRIPTOR_GETACQUAINTANCES:
				case MSGTASKDESCRIPTOR_GETMATCHES_OPEN:
				case MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS:
				case MSGTASKDESCRIPTOR_GETMATCHES_CLOSED:
				case MSGTASKDESCRIPTOR_CONFIRM_GAME:
				case MSGTASKDESCRIPTOR_DECLINE_GAME:
				case MSGTASKDESCRIPTOR_ADD_FRIEND:
				case MSGTASKDESCRIPTOR_UPDATEUSER:
					final String[] keys = data
							.getStringArray(BNDKEY_PARAM_KEYS);
					for (String key : keys)
						funcdesc.add(new BasicNameValuePair(key, data
								.getString(key)));
					break;
				case MSGTASKDESCRIPTOR_REGISTRATON:
				case MSGTASKDESCRIPTOR_CREATEMATCH:
				case MSGTASKDESCRIPTOR_UPDATEMATCH:
				case MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION:
					final HTTPPostable payload = (HTTPPostable) data
							.getParcelable(BNDKEY_POST_PARCELABLE);

					List<NameValuePair> argsList = payload
							.getObjectAsNameValuePairList();

					funcdesc.addAll(argsList);
					
					break;
				}
				try {
					entity = new UrlEncodedFormEntity(funcdesc, HTTP.UTF_8);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				request.setEntity(entity);
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response == null) {
					postMessage(
							msg.replyTo,
							ECConnectionMessageConstants.RES_KIND_UNKNOWN_ERROR,
							"");
					return;
				}
				JSONArray jArr = null;
				JSONArray dataJArr = null;
				String opResult = null;
				try {
					jArr = JSONParser.getJSONArrayFromHttpResponse(response);
					opResult = jArr.getString(RESIND_OPRESULT);
					dataJArr = jArr.optJSONArray(RESIND_DATA);
				} catch (Exception e1) {
					e1.printStackTrace();
					Log.e(LOGTAG + " - DEBUG", response.toString());
					postMessage(
							msg.replyTo,
							ECConnectionMessageConstants.RES_KIND_UNKNOWN_ERROR,
							"");
					return;
				}
				if (opResult.toString().toLowerCase()
						.contains(MSGRESDESCTIPTION_SUCCESS.toLowerCase())) {
					responseMes.arg2 = ECConnectionMessageConstants.RES_KIND_SUCCESS;
					if (dataJArr != null)
						try {
							switch (msg.arg1) {
							case MSGTASKDESCRIPTOR_REGISTRATON:
							case MSGTASKDESCRIPTOR_CONFIRM_REGISTRATION:
								long l = Long.valueOf(dataJArr.getString(0));
								resultBundle.putLong(BNDKEY_RESULT, l);
								break;
							case MSGTASKDESCRIPTOR_GETFRIENDS:
							case MSGTASKDESCRIPTOR_GETACQUAINTANCES:
								ECUser[] uArr = ECUser
										.createFromJSONArray(dataJArr);
								resultBundle.putParcelableArray(
										BNDKEY_RESULT_ARRAY, uArr);
								break;
							case MSGTASKDESCRIPTOR_GETMATCHES_OPEN:
							case MSGTASKDESCRIPTOR_GETMATCHES_CLOSED:
								ECMatch[] mArr = ECMatch
										.createFromJSONArray(dataJArr);
								resultBundle.putParcelableArray(
										BNDKEY_RESULT_ARRAY, mArr);
								break;
							case MSGTASKDESCRIPTOR_GETMATCH_PARTECIPANTS:
								ECUser[][] pMatr = new ECUser[PARTECIPANT_STATUSES.length][];
								for (int i = 0; i < PARTECIPANT_STATUSES.length; i++) {
									pMatr[i] = ECUser
											.createFromJSONArray(dataJArr
													.getJSONArray(i));
									resultBundle.putParcelableArray(
											PARTECIPANT_STATUSES[i], pMatr[i]);
								}
								resultBundle.putStringArray(
										BNDKEY_RESULT_ARRAY, PARTECIPANT_STATUSES);
								break;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				} else if (opResult.toString().toLowerCase()
						.contains(MSGRESDESCTIPTION_FAILURE.toLowerCase())) {
					responseMes.arg2 = RES_KIND_FAILURE;
				} else {
					responseMes.arg2 = RES_KIND_UNKNOWN_ERROR;
				}
				break;
			case ECConnectionMessageConstants.MSGWHAT_POST_IMAGE:
				final String function = FUNC;
				final String function_value = FUNCDESCRIPTOR_UPLOAD_PHOTO;
				final String id = BNDKEY_ID;
				final String id_value = "" + data.getLong(id);
				MultipartEntity mpEntity;
				request = new HttpPost(SERVER_HR_ADDRESS);
				File file = new File(
						data.getString(BNDKEY_POST_IMAGE_PHOTOPATH));
				if (!file.exists()) {
					postMessage(msg.replyTo, RES_KIND_UNKNOWN_ERROR, "");
					return;
				}

				mpEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				// use FileBody to transfer the data
				mpEntity.addPart("photo", new FileBody(file));
				// Normal string data
				try {
					mpEntity.addPart(function, new StringBody(function_value));
					mpEntity.addPart(id, new StringBody(id_value));
				} catch (UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				request.setEntity(mpEntity);
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response == null) {
					Log.e("WTF", "response null");
					postMessage(msg.replyTo, RES_KIND_UNKNOWN_ERROR, "");
					return;
				}
				try {
					Log.e(LOGTAG + " - DEBUG", response.toString());
					jArr = JSONParser.getJSONArrayFromHttpResponse(response);
					opResult = jArr.getString(RESIND_OPRESULT);
				} catch (Exception e1) {
					e1.printStackTrace();
					postMessage(msg.replyTo, RES_KIND_UNKNOWN_ERROR, "");
					return;
				}
				if (opResult.toString().toLowerCase()
						.contains(MSGRESDESCTIPTION_SUCCESS.toLowerCase())) {
					responseMes.arg2 = RES_KIND_SUCCESS;
				} else if (opResult.toString().toLowerCase()
						.contains(MSGRESDESCTIPTION_FAILURE.toLowerCase())) {
					responseMes.arg2 = RES_KIND_FAILURE;
				} else {
					responseMes.arg2 = RES_KIND_UNKNOWN_ERROR;
				}
				break;
			}

			responseMes.arg1 = msg.arg1;
			responseMes.setData(resultBundle);
			try {
				outMsnger.send(responseMes);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		private void postMessage(Messenger m, int what, String content) {
			Message message = Message.obtain();
			message.what = what;
			Bundle b = new Bundle();
			b.putString(BNDKEY_MESSAGE, content);
			message.setData(b);
			try {
				m.send(message);
			} catch (android.os.RemoteException e1) {
				Log.w(getClass().getName(), "Exception sending message", e1);
			}
		}
	}

}