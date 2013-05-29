package edu.easycalcetto.connection;

import static edu.easycalcetto.ApplicationStatus.REGISTRATION_PENDING;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_NULL_JARR;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_NULL_OPRESULT;
import static edu.easycalcetto.connection.Constants.RESULT_FAILURE;
import static edu.easycalcetto.connection.Constants.RESULT_SUCCESS;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_CONNECTION_LOST;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_GENERIC;
import static edu.easycalcetto.connection.Constants.RESULT_SUCCESS;
import static edu.easycalcetto.connection.Constants.RESULT_SUCCESS_WITH_NO_DATA;
import static edu.easycalcetto.connection.Constants.SUCCESS_FLAG;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_DATA;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_OPRESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.SERVER_API_URL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import edu.easycalcetto.R;
import edu.easycalcetto.activities.ConfirmRegistrationActivity;
import edu.easycalcetto.activities.RequestRegistrationActivity;
import edu.easycalcetto.data.JSONParser;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public abstract class ECPostWithBNVPTask extends
		AsyncTask<BasicNameValuePair, Void, Integer> {

	protected static String LOGTAG = "ECPostWithBNVPTask";
	HttpPost request = null;
	DefaultHttpClient client = null;
	HttpResponse response = null;
	JSONArray jArr = null;
	JSONArray dataJArr = null;
	String opResult = null;

	public ECPostWithBNVPTask() {
		request = new HttpPost(SERVER_API_URL);
		client = new ECHttpClient();
	}

	@Override
	protected Integer doInBackground(BasicNameValuePair... params) {

		List<BasicNameValuePair> paramslist = new ArrayList<BasicNameValuePair>(
				Arrays.asList(params));

		Integer result = null;
		StringEntity entity = null;
		try {
			if (paramsAreValid(params)) {
				entity = new UrlEncodedFormEntity(paramslist);
				request.setEntity(entity);
				response = client.execute(request);
				if ((jArr = JSONParser
						.getJSONArrayFromHttpResponse(getResponse())) != null) {
					if ((opResult = jArr.getString(RESIND_OPRESULT)) != null) {
						if (opResult.toString().toLowerCase()
								.contains(SUCCESS_FLAG.toLowerCase())) {
							// RESPONSE SUCCESS
							if ((dataJArr = jArr.optJSONArray(RESIND_DATA)) != null) {
								result = RESULT_SUCCESS;
							} else {
								result = RESULT_SUCCESS_WITH_NO_DATA;
								Log.e(LOGTAG, "no data");
								// onDataNULL();
							}
						} else {
							// RESPONSE FAILURE
							result = RESULT_FAILURE;
							Log.d(LOGTAG, "response failure");
						}

					} else {
						Log.e(LOGTAG, "opresult null");
						result = RESULT_ERR_NULL_OPRESULT;
						// onOpResultNULL();
					}
				} else {
					Log.e(LOGTAG, "JArr null");
					result = RESULT_ERR_NULL_JARR;
					// onJArrnullCB();
				}

			} else {
				String msg = "The params passed are not valid";
				Log.e(LOGTAG, msg);
				throw new IllegalArgumentException(msg);
			}

		} catch (IOException e) {
			Log.e(LOGTAG, "connection lost exception", e);
			result = RESULT_ERR_CONNECTION_LOST;
		} catch (Exception e) {
			Log.e(LOGTAG, "unexpected exception", e);
			result = RESULT_ERR_GENERIC;
		}
		return result;
	}

	@Override
	protected void onPostExecute(Integer result) {
		switch (result.intValue()) {
		case RESULT_SUCCESS:
			onSuccess();
			break;
		case RESULT_FAILURE:
			onFailure();
			break;
		case RESULT_SUCCESS_WITH_NO_DATA:
			onSuccessWithNoData();
			break;
		case RESULT_ERR_CONNECTION_LOST:
			onConnectionLost();
			break;
		case RESULT_ERR_NULL_JARR:
			onJArrNULL();
			break;
		case RESULT_ERR_NULL_OPRESULT:
			onOpResultNULL();
			break;
		case RESULT_ERR_GENERIC: // the break is missing voluntary
		default:
			onGenericError();
		}
		super.onPostExecute(result);
	}

	protected HttpResponse getResponse() {
		return response;
	}

	protected JSONArray getDataJArr() {
		return dataJArr;
	}

	protected boolean paramsAreValid(BasicNameValuePair[] params) {
		boolean result = false;
		// Checking if contains operation param
		for (BasicNameValuePair pair : params) {
			if (pair.getName().equals(ECConnectionMessageConstants.FUNC)) {
				result = true;
			}
		}
		return result;
	}

	abstract protected void onSuccess();

	abstract protected void onSuccessWithNoData();

	abstract protected void onFailure();

	abstract protected void onJArrNULL();

	abstract protected void onOpResultNULL();

	abstract protected void onDataNULL();

	abstract protected void onConnectionLost();

	abstract protected void onGenericError();

}
