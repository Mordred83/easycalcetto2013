package edu.easycalcetto;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

import edu.easycalcetto.data.ECUser;

public abstract class EasyCalcettoActivity extends SherlockActivity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public ECApplication getMyApplication() {
		return (ECApplication) getApplication();
	}
	
	public ECUser getOwner(){
		return getMyApplication().getOwner();
	}
}