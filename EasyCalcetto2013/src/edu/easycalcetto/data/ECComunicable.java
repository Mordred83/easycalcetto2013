package edu.easycalcetto.data;

import org.json.JSONObject;

public interface ECComunicable { 
	
	public abstract class Creator<T>{
		public abstract T createFromJSONObject(JSONObject jo);
		public abstract T[] newArray(int size);
	
	}
}
