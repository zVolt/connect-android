package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * this is helpful when user create or send somthing in offline mode and after
 * creation he comes online then this broadcast receiver will start the
 * background service and sync will be performed
 * 
 * @author Zeeshan Khan
 * 
 */
public class NewtorkBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences spf = context.getSharedPreferences(
				Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		if (Utility.isConnected(context)
				&& spf.getBoolean(Constants.PREF_KEYS.logged_in, false)
				&& !BackgroundService.isServiceRunning()) {
			Intent new_intent = new Intent(context, BackgroundService.class);
			context.startService(new_intent);
		}
	}
}
