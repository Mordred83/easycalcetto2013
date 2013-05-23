package edu.easycalcetto;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.fragments.NoNetworkDialogFragment;

public abstract class EasyCalcettoActivity extends SherlockActivity implements OnClickListener{
	/** Called when the activity is first created. */
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.exit_button:
			Toast.makeText(getApplicationContext(), "ExitYeah", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.open_network_button:
			Toast.makeText(getApplicationContext(), "OpenYeah", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		
	}
	
	private void showNoNetworkDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msg_no_network);
		builder.setPositiveButton(R.string.lbl_exit, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), "ExitYeah", Toast.LENGTH_SHORT).show();
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
}