package edu.easycalcetto;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

import com.actionbarsherlock.app.SherlockActivity;

import edu.easycalcetto.connection.ECConnectionService;

public abstract class EasyCalcettoActivity extends SherlockActivity {
	/** Called when the activity is first created. */
	protected Messenger messenger;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		doBindConnectionService();
	}

	private void doBindConnectionService() {
		Intent intent = null;
		intent = new Intent(this, ECConnectionService.class);
		Handler handler = getConnectionServiceHandler();
		if (handler != null) {
			Messenger messenger = new Messenger(handler);
			intent.putExtra("MESSENGER", messenger);
			bindService(intent, conn, Context.BIND_AUTO_CREATE);
		}
	}

	public ECApplication getMyApplication() {
		return (ECApplication) getApplication();
	}

	@Override
	protected void onDestroy() {
		doUnbindConnectionService();
		super.onDestroy();
	}

	private void doUnbindConnectionService() {
		if(messenger != null)
		unbindService(conn);
	}

	private ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messenger = new Messenger(binder);
			EasyCalcettoActivity.this.onServiceConnected();
		}

		public void onServiceDisconnected(ComponentName className) {
			messenger = null;
			EasyCalcettoActivity.this.onServiceDisconnected();
		}
	};

	protected abstract Handler getConnectionServiceHandler();

	protected boolean isServiceConnected() {
		return (messenger != null);
	}

	protected abstract void onServiceConnected();

	protected abstract void onServiceDisconnected();
}