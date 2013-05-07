package edu.easycalcetto;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import edu.easycalcetto.connection.ECConnectionMessageConstants;
import edu.easycalcetto.data.ECUser;
import edu.easycalcetto.data.MessagesCreator;

public class MyActivity extends EasyCalcettoActivity {
	private final String LOGTAG = this.getClass().getSimpleName();
	
	EditText _id_EditText, num_tel_EditText, name_EditText, surname_EditText,
			yob_EditText, role_EditText, rating_EditText, votes_EditText,
			_id_comment_EditText;

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			Log.i(LOGTAG, "what = " + message.what);
			String maramao = "ERROR";
			switch (message.arg2) {
			case ECConnectionMessageConstants.RES_KIND_SUCCESS:

				// maramao = "SUCCESS";
				ECUser[] ecuArr = (ECUser[]) message.getData().getParcelableArray(ECConnectionMessageConstants.BNDKEY_RESULT_ARRAY);
				maramao = "Results:";
				if (ecuArr != null) {
					for(ECUser ecu : ecuArr)
						maramao += ecu.getName()+"\n";
//					int i = 0;
//					for (ECUser[] ecuArr : ecuMatr) {
//						maramao += ECMatch.PARTECIPANTS_STATUSES[i++] + ": ";
//						for (ECUser ecu : ecuArr)
//							maramao += ecu.getName() + " ";
//						maramao += "\n";
//					}
				} else
					maramao = "ma che caz";
				break;
			case ECConnectionMessageConstants.RES_KIND_FAILURE:
				maramao = "FAILURE";
				break;
			default:
				maramao = "DEFAULT ERROR";
				break;
			}
			// Toast.makeText(getApplicationContext(), maramao,
			// Toast.LENGTH_LONG)
			// .show();

			// switch (message.what) {
			// case ECConnectionMessageConstants.MSGWHAT_GET:
			// ECUser[] result = (ECUser[])
			// message.getData().getParcelableArray("RESULTS");
			// _id_EditText.setText(""+result[0].get_id());
			// num_tel_EditText.setText(result[0].getNum_tel());
			// name_EditText.setText(result[0].getName());
			// surname_EditText.setText(result[0].getSurname());
			// yob_EditText.setText(result[0].getYob());
			// role_EditText.setText(result[0].getRole());
			// rating_EditText.setText(""+result[0].getRating());
			// votes_EditText.setText(""+result[0].getVotes());
			// _id_comment_EditText.setText(""+result[0].get_id_comment());
			// break;
			// }
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onClick(View view) {
		Messenger msnger = new Messenger(handler);
		ECUser creator = new ECUser(5, "0000000", "ciccio", "baliccio", "1964");
		// ECMatch ecm = new ECMatch(ECMatch.NEW_MATCH_FLAG_ID, creator,
		// "mondiale1", ECMatch.Status.TO_PLAY.toString(), "campetto", 11,
		// new Date(2012, 12, 25).getTime(),
		// new Date(2012, 12, 31).getTime(),
		// new Date(2013, 1, 1).getTime(), new Date(2013, 1, 2).getTime(),
		// new Date(2013, 1, 3).getTime());
		// Message msg = MessagesCreator.getCreateMatchMessage(new Messenger(
		// handler), ecm);
		// msg = MessagesCreator.getRegistrationMessage(new Messenger(handler),
		// registration);
		Message msg = MessagesCreator.getGetAcquaintanceMessage(msnger, new Long(4).longValue());
		// MessagesCreator.getGetFriendsMessage(msnger, new Long(1).longValue());
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
//			Toast.makeText(getApplicationContext(), "DAMN!", Toast.LENGTH_SHORT)
//					.show();
			e.printStackTrace();
		}
	}

	@Override
	protected Handler getConnectionServiceHandler() {
		return handler;
	}

	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onServiceDisconnected() {
		// TODO Auto-generated method stub
		
	}
}