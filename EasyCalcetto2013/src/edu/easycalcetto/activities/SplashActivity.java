package edu.easycalcetto.activities;

import static edu.easycalcetto.ApplicationStatus.REGISTRATION_PENDING;
import static edu.easycalcetto.ApplicationStatus.UNREGISTERED;
import static edu.easycalcetto.Constants.PREFS_NAME;
import static edu.easycalcetto.Constants.PREF_REGISTERED;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.google.android.gcm.GCMRegistrar;

import edu.easycalcetto.ApplicationStatus;
import edu.easycalcetto.Constants;
import edu.easycalcetto.ECApplication;
import edu.easycalcetto.EasyCalcettoActivity;
import edu.easycalcetto.R;

public class SplashActivity extends EasyCalcettoActivity {
	
	//public static final String PREFS_NAME = ECApplication.PREFS_NAME;
    //public static final String PREF_REGISTERED = ECApplication.PREFKEY_REGSTATUS;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		
        //Create an object of type SplashHandler
        SplashHandler mHandler = new SplashHandler();
        // set the layout for this activity
        setContentView(R.layout.activity_splash); 
        ImageView splashImage=(ImageView)findViewById(R.id.splashImage);
        splashImage.setImageResource(R.drawable.splash_activity_ec);
        splashImage.setScaleType(ScaleType.FIT_XY);
        // Create a Message object
        Message msg = new Message();
        
        
        if(getMyApplication().getApplicationStatus().equals(UNREGISTERED))  
        	msg.what=0;
        else if(getMyApplication().getApplicationStatus().equals(REGISTRATION_PENDING))
        	msg.what=1;
        else
        	msg.what=2;
        
        mHandler.sendMessageDelayed(msg, 1000);
    }
 

    // Handler class implementation to handle the message
    private class SplashHandler extends Handler {
        
        //This method is used to handle received messages
        public void handleMessage(Message msg)
          {
        	Intent intent= new Intent();
        	if(msg.what==0){
        		super.handleMessage(msg);
                intent.setClass(SplashActivity.this, RequestRegistrationActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
        	}else if(msg.what==1){
        		super.handleMessage(msg);
            	intent.setClass(SplashActivity.this, ConfirmRegistrationActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
        	}else{
        		super.handleMessage(msg);
            	intent.setClass(SplashActivity.this, MenuActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
        	}
            // switch to identify the message by its code
            //Toast.makeText(getApplicationContext(), "Valore "+msg.what, Toast.LENGTH_SHORT).show();
          }
    }
 
}