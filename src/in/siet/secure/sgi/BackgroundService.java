package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.loopj.android.http.RequestParams;

public class BackgroundService extends Service {

	SharedPreferences spref;
	static String TAG = "in.siet.secure.sgi.BackgroundActivity";

	/**
	 * if start getting ANR then start a new thread in the service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		handleIntent(intent);
		return START_STICKY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		handleIntent(intent);
		super.onStart(intent, startId);
	}

	WakeLock wake;

	private void handleIntent(Intent intent) {
		/**
		 * Hold a partial wake lock so that you don't get killed :D
		 */
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		if (wake == null)
			wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		if (!wake.isHeld())
			wake.acquire();
		spref = getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE);
		RequestParams params = new RequestParams();
		params.put(Constants.QueryParameters.USERNAME,
				spref.getString(Constants.PreferenceKeys.user_id, null));
		params.put(Constants.QueryParameters.TOKEN,
				spref.getString(Constants.PreferenceKeys.token, null));
		new doPopo().execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		wake.release();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private static class doPopo extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Utility.log(TAG, "executing I am a service");
			return null;
		}

	}
}
