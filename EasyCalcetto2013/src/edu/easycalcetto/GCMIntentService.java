package edu.easycalcetto;

import static edu.easycalcetto.CommonUtilities.SENDER_ID;
import static edu.easycalcetto.CommonUtilities.EXTRA_MESSAGE;
//import static edu.easycalcetto.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import edu.easycalcetto.activities.MenuActivity;


public class GCMIntentService extends GCMBaseIntentService {

	@SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, "register");
        ServerUtilities.register(context, registrationId, ((ECApplication)getApplication()).getOwner().get_id());
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, "unregister");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        
        if(intent.hasExtra(EXTRA_MESSAGE)){
        	String message = intent.getStringExtra(EXTRA_MESSAGE);
        	Log.v(TAG, message);
        	generateNotification(context, message);
        }else{
        	Log.e(TAG, "no message field");
        }
        //displayMessage(context, message);
        // notifies user
        
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = "deleted "+total;
        //displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, "error id: "+errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, "recoverable error id: "+errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.referee_x16;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context); 
        Notification notification = builder.getNotification();
        notification.icon = icon;
        notification.tickerText = message;
        notification.when = when;
        //Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MenuActivity.class);
        notificationIntent.putExtra(EXTRA_MESSAGE, message);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
