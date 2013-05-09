package edu.easycalcetto.data;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Parcelable;

public interface HTTPPostable extends Parcelable{
	public List<NameValuePair> getObjectAsNameValuePairList();
}
