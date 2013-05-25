
package edu.easycalcetto.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import edu.easycalcetto.R;

public class PreferenceProfilo extends SherlockPreferenceActivity{
	
	private final static int INFO_DIALOG = 1;	
	private AdView adView;
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Used to put dark icons on light action bar
        boolean isLight = SampleList.THEME == R.style.Theme_Sherlock_Light;
        
        menu.add(1,1,1,"Info").
        setIcon(isLight ? R.drawable.info_buttondark : R.drawable.ic_action_help)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
 
        return true;
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_profilo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //admob widget
	    adView = (AdView)findViewById(R.id.ad);
	    adView.loadAd(new AdRequest());
        String preferencesName = this.getPreferenceManager().getSharedPreferencesName();
        Log.i("nomePreferences",preferencesName);
    }

    
    
    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        		finish();
            	break;
        case 1:
        	showDialog(INFO_DIALOG);
        }
        return super.onOptionsItemSelected(item);
    }
 
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INFO_DIALOG:
			return createInfoDialog();
		default:
			return null;
		}
	}

	private final Dialog createInfoDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoPreferenceDialogTitle);
		builder.setIcon(R.drawable.info_button_white);
		builder.setMessage(R.string.infoPreferenceDialogMSG);
		builder.setPositiveButton(R.string.close_labelDialog,
				new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
								dismissDialog(INFO_DIALOG);
							}
						});	
		return builder.create();
	}
    
    
}
