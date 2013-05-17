package edu.easycalcetto.connection;

import static edu.easycalcetto.connection.Constants.RESULT_ERR_CONNECTION_LOST;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_GENERIC;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_NULL_JARR;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_NULL_OPRESULT;
import static edu.easycalcetto.connection.Constants.RESULT_FAILURE;
import static edu.easycalcetto.connection.Constants.RESULT_SUCCESS;
import static edu.easycalcetto.connection.Constants.RESULT_SUCCESS_WITH_NO_DATA;
import static edu.easycalcetto.connection.Constants.SUCCESS_FLAG;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_ID;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.BNDKEY_POST_IMAGE_PHOTOPATH;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.FUNC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_DATA;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.RESIND_OPRESULT;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.SERVER_HR_ADDRESS;
import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.os.AsyncTask;
import android.util.Log;
import edu.easycalcetto.data.JSONParser;

public abstract class ECPostWithMPETask extends
		AsyncTask<BasicNameValuePair, Void, Integer> {

	protected static String LOGTAG = "ECPostWithMPETask";
	HttpPost request = null;
	DefaultHttpClient client = null;
	HttpResponse response = null;
	JSONArray jArr = null;
	JSONArray dataJArr = null;
	String opResult = null;

	public ECPostWithMPETask() {
		request = new HttpPost(SERVER_HR_ADDRESS);
		client = new ECHttpClient();
	}

	@Override
	protected Integer doInBackground(BasicNameValuePair... params) {

		List<BasicNameValuePair> paramslist = new ArrayList<BasicNameValuePair>(
				Arrays.asList(params));

		Integer result = null; // the result of the entire operation
		MultipartEntity entity = null; // the entity to be passed in the post
		File file = null; // the file object related to the user photo
		try {
			if (paramsAreValid(params)) {
				entity = new MultipartEntity(BROWSER_COMPATIBLE);
				file = getFile(params);
				if (file != null && file.exists() && file.canRead()
						&& file.isFile()) {
					entity.addPart(FUNC, new StringBody(getFunction(params)));
					entity.addPart(BNDKEY_ID, new StringBody(getId(params)));
					entity.addPart("photo", new FileBody(file));
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
								}
							} else {
								// RESPONSE FAILURE
								result = RESULT_FAILURE;
								Log.d(LOGTAG, "response failure");
							}

						} else {
							Log.e(LOGTAG, "opresult null");
							result = RESULT_ERR_NULL_OPRESULT;
						}
					} else {
						Log.e(LOGTAG, "JArr null");
						result = RESULT_ERR_NULL_JARR;
					}
				}else{
					throw new IllegalArgumentException("Invalid File passed");
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

	private File getFile(BasicNameValuePair[] params) {
		File result = null;
		for (int i = 0; i < params.length && result == null; i++) {
			if (params[i].getName().equals(BNDKEY_POST_IMAGE_PHOTOPATH)) {
				result = new File(params[i].getValue());
			}
		}
		return result;
	}
	
	private String getId(BasicNameValuePair[] params){
		String result = null;
		for (int i = 0; i < params.length && result == null; i++) {
			if (params[i].getName().equals(BNDKEY_ID)) {
				result = params[i].getValue();
			}
		}
		return result;
	}
	
	private String getFunction(BasicNameValuePair[] params){
		String result = null;
		for (int i = 0; i < params.length && result == null; i++) {
			if (params[i].getName().equals(FUNC)) {
				result = params[i].getValue();
			}
		}
		return result;
	}

	protected boolean paramsAreValid(BasicNameValuePair[] params) {
		return getFunction(params)!=null && getFile(params)!=null && getId(params)!=null;
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
