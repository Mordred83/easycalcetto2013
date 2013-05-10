package edu.easycalcetto.connection;

import static edu.easycalcetto.connection.Constants.COM_RESULT_OK;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_CONNECTION_LOST;
import static edu.easycalcetto.connection.Constants.RESULT_ERR_GENERIC;
import static edu.easycalcetto.connection.ECConnectionMessageConstants.SERVER_HR_ADDRESS;

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

import android.os.AsyncTask;
import android.util.Log;

public class ECPostWithBNVPTask extends AsyncTask<BasicNameValuePair, Void, Integer> {
	
	protected static String LOGTAG = "ECPostWithBNVPTask";
	HttpPost request = null;
	DefaultHttpClient client = null;
	HttpResponse response = null;
	
	public ECPostWithBNVPTask(){
		request = new HttpPost(SERVER_HR_ADDRESS);
		//request.addHeader("accept", "application/json");
		client = new ECHttpClient();
	}
	
	@Override
	protected Integer doInBackground(BasicNameValuePair... params) {
		
		List<BasicNameValuePair> paramslist = new ArrayList<BasicNameValuePair>(Arrays.asList(params));
		
		Integer result = null;
		StringEntity entity = null;
		try{
			if(paramsAreValid(params)){
				entity = new UrlEncodedFormEntity(paramslist);
				request.setEntity(entity);
				response = client.execute(request);
				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(
				// response.getEntity().getContent(), "iso-8859-1"), 8);
				// StringBuffer sb = new StringBuffer();
				// String line = null;
				// while ((line = reader.readLine()) != null) {
				// sb.append(line+"\n");
				// }
				// Log.d(LOGTAG, "RESPONSE: " +sb.toString());
				result = COM_RESULT_OK;
			}else{
				String msg = "The params passed are not valid";
				Log.e(LOGTAG, msg);
				throw new IllegalArgumentException(msg);
			}
			
		}catch (IOException e) {
			Log.e(LOGTAG, "connection lost exception", e);
			result = RESULT_ERR_CONNECTION_LOST;
		}catch(Exception e){
			Log.e(LOGTAG, "unexpected exception", e);
			result = RESULT_ERR_GENERIC;
		}
		return result;
	}
	
	protected HttpResponse getResponse(){
		return response;
	}
	
	protected boolean paramsAreValid(BasicNameValuePair[] params){
		boolean result = false;
		//Checking if contains operation param
		for(BasicNameValuePair pair : params){
			if(pair.getName().equals(ECConnectionMessageConstants.FUNC)){
				result = true;
			}
		}
		return result;
	}
	
}
