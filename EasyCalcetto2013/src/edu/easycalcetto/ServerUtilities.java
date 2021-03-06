package edu.easycalcetto;

import static edu.easycalcetto.connection.ECConnectionMessageConstants.SERVER_API_URL;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import edu.easycalcetto.connection.ECConnectionMessageConstants;
//import static edu.easycalcetto.CommonUtilities.displayMessage;

public final class ServerUtilities {

	private static final String TAG = "ServerUtilities";
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();

	/**
	 * Register this account/device pair within the server.
	 * 
	 * @return whether the registration succeeded or not.
	 */
//	public static boolean register(final Context context, final String regId, final long userId){
//		RegisterRunnable r = new RegisterRunnable(context, regId, userId);
//		new Thread(r).start();
//		return true;
//	}
	
	public static boolean register(final Context context, final String regId,
			final long userId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		String serverUrl = SERVER_API_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put(ECConnectionMessageConstants.FUNC, "gcm_register");
		params.put("id", "" + userId);
		params.put("gcm_id", regId);
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			try {
				// displayMessage(context, "prova");
				post(serverUrl, params);
				GCMRegistrar.setRegisteredOnServer(context, true);
				// CommonUtilities.displayMessage(context, message);
				return true;
			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i, e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return false;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
		}
		// CommonUtilities.displayMessage(context, message);
		return false;
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String userId) {
		Log.i(TAG, "unregistering device (regId = " + userId + ")");
		String serverUrl = SERVER_API_URL;
		Map<String, String> params = new HashMap<String, String>();
		params.put(ECConnectionMessageConstants.FUNC, "gcm_unregister");
		params.put("id", userId);
		// params.put("regId", regId);
		try {
			post(serverUrl, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			// CommonUtilities.displayMessage(context, message);
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			// CommonUtilities.displayMessage(context, message);
		}
	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param mParams
	 *            request parameters.
	 * 
	 * @throws IOException
	 *             propagated from POST.
	 */
	private static void post(final String endpoint,final Map<String, String> mParams)
			throws IOException {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				URL url;
				try {
					url = new URL(endpoint);
				} catch (MalformedURLException e) {
					throw new IllegalArgumentException("invalid url: " + endpoint);
				}
				StringBuilder bodyBuilder = new StringBuilder();
				Iterator<Entry<String, String>> iterator = mParams.entrySet().iterator();
				// constructs the POST body using the parameters
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					bodyBuilder.append(param.getKey()).append('=')
							.append(param.getValue());
					if (iterator.hasNext()) {
						bodyBuilder.append('&');
					}
				}
				String body = bodyBuilder.toString();
				Log.v(TAG, "Posting '" + body + "' to " + url);
				byte[] bytes = body.getBytes();
				HttpURLConnection conn = null;
				try {
					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					conn.setFixedLengthStreamingMode(bytes.length);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded;charset=UTF-8");
					// post the request
					OutputStream out = conn.getOutputStream();
					out.write(bytes);
					out.close();
					// handle the response
					int status = conn.getResponseCode();
					if (status != 200) {
						throw new IOException("Post failed with error code " + status);
					}
				} catch (IOException e) {
					Log.e(TAG, "an error occurred on post", e);
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
				return null;
			}
			
		}.execute();
		
	}
}
