package edu.easycalcetto.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class JSONParser {
	
	private final static String LOGTAG = JSONParser.class.getSimpleName();
	
	static InputStream is = null;
    static JSONArray jObj = null;
    static String json = "";
    
    public static JSONArray getJSONArrayFromHttpResponse(HttpResponse httpResponse)  throws Exception{
    	
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }
            is.close();
            json = sb.toString();
            //json = json.substring(json.indexOf('[')-1);
            json = json.replaceAll("<!DOCTYPE((.|\n|\r)*?)\">", "");
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            Log.e("Response", json);
        }
        // return JSON String
        return jObj;
    }
}

