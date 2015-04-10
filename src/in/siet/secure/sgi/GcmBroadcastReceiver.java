package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String TAG = "in.siet.secure.sgi.GcmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(),
				BackgroundService.class.getName());
		SharedPreferences spf = context.getSharedPreferences(
				Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		// Start the service, keeping the device awake while it is launching.

		if (Utility.isConnected(context)) {
			if (spf.getBoolean(Constants.PREF_KEYS.logged_in, false)
					&& !BackgroundService.isServiceRunning()) {
				startWakefulService(context, (intent.setComponent(comp)));
			} else {

				Utility.log(TAG,
						"aborting sync not logged in or service is running settign to start in 10 sec ");
				Utility.setAlarm(context, 10000);
				// set flag here that there is some data to be send to server
			}
			setResultCode(Activity.RESULT_OK);
		} else {
			// if not connected to internet then backing off anf relaying on the
			// network state listener broadcast receiver
			Utility.log(TAG, "No internet hence backing off");
		}
	}
}
