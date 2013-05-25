package edu.easycalcetto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import edu.easycalcetto.data.ECUser;

public abstract class EasyCalcettoActivity extends SherlockActivity {
	/** Called when the activity is first created. */
	protected static final int RESULT_NO_NETWORK = -254;
	protected static final int RESULT_EXIT = -255;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		if(!isNetworkAvaliable()){
			showNoNetworkDialog();
		}
		super.onResume();
	}

	public ECApplication getMyApplication() {
		return (ECApplication) getApplication();
	}
	
	public ECUser getOwner(){
		return getMyApplication().getOwner();
	}
	
	protected boolean isNetworkAvaliable(){
		return getMyApplication().isNetworkAvailable();
	}
	
	protected void showNoNetworkDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msg_no_network);
		builder.setPositiveButton(R.string.lbl_exit, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Toast.makeText(getApplicationContext(), "ExitYeah", Toast.LENGTH_SHORT).show();
				setResult(RESULT_NO_NETWORK);
				finish();
				
			}
		});
		builder.setNegativeButton(R.string.lbl_open_network_preferences, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
			}
		});
		builder.create().show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_NO_NETWORK || resultCode == RESULT_EXIT){
			setResult(resultCode);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}