package in.siet.secure.Util;

import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.sgi.BackgroundService;
import in.siet.secure.sgi.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

public class Utility {
	// private static boolean LOCA_SERVER = false;
	private final static String TAG = "in.siet.secure.sgi.Utility";
	// public static String SERVER = LOCA_SERVER ? "192.168.0.101"
	// : "sgitomcat-1093.rhcloud.com";
	private static ProgressDialog progress_dialog;
	private static SharedPreferences spf;
	public static int MAX_NOTIFICATION_TEXT_LINES = 20;
	/**
	 * STRING TO HOLD THE TEXT OF THE NOTIFICATION TO BE RAISED FOR INCOMING
	 * MESSAGE
	 */
	// public static String notification_msg_text[] = new
	// String[MAX_NOTIFICATION_TEXT_LINES];
	/**
	 * BOOLEAN VARIABLE TO CHECK WHETHER NOTIFICATION IS ACTIVE IN THE
	 * NOTIFICATION DRAWER OR NOT
	 */
	public static Boolean notification_msg_active = false;

	public static String BASE_IMG_URL = "http://www.secure.siet.in/images/std_photo/";

	public static void RaiseToast(Context context, String msg,
			boolean for_long_time) {
		Toast.makeText(context, msg,
				for_long_time ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
	}

	public static void log(String TAG, String msg) {
		Log.d(TAG, msg);
	}

	public static void DEBUG(Exception e) {
		e.printStackTrace();
	}

	private static SharedPreferences getSPreferences(Context context) {
		if (spf == null)
			spf = context.getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spf;
	}

	public static String getBaseURL(Context context) {
		String str;
		if (getSPreferences(context).getBoolean(
				Constants.PREF_KEYS.LOCAL_SERVER, false)) {
			str = String.format(
					"http://%s:%s/SGI_webservice/",
					getSPreferences(context).getString(
							Constants.PREF_KEYS.SERVER_IP, "192.168.0.100"),
					"8080");
		} else {
			str = String.format("http://%s/SGI_webservice/",
					"sgitomcat-1093.rhcloud.com");
		}

		return str;
	}

	public static String getUserLoginId(Context context) {
		return getSPreferences(context).getString(Constants.PREF_KEYS.user_id,
				"");
	}

	public static void showProgressDialog(Context context) {
		try {
			progress_dialog = ProgressDialog.show(context, null,
					"Please wait..", true, false);
			Utility.log(TAG, "progress is OK");
		} catch (Exception ex) {
			Utility.DEBUG(ex);
		}
	}

	public static void hideProgressDialog() {
		try {
			progress_dialog.dismiss();
		} catch (Exception ex) {
			Utility.DEBUG(ex);
		}
	}

	@SuppressLint("DefaultLocale")
	public static String getUserImage(String id) {
		return BASE_IMG_URL + id.toLowerCase().trim() + ".jpg";
	}

	// public static User getUser(String id) {
	// return null;
	// }

	public static String sha1(String input) {
		try {
			MessageDigest mDigest = MessageDigest.getInstance("SHA1");
			byte[] result = mDigest.digest(input.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (Exception ex) {
			Utility.DEBUG(ex);
			return null;
		}
	}

	public static String encode(String str) {
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
	}

	public static String decode(String str) {
		return new String(Base64.decode(str, Base64.DEFAULT));
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork
				.isConnectedOrConnecting());
	}

	/**
	 * FUNCTION TO BUILD AND RAISE NOTIFICATION IN THE NOTIFICAION DRAWER
	 * 
	 * @param context
	 * @param resultantclass
	 * @param id
	 * @param title
	 * @param text
	 */
	public static void buildNotification(Context context, Intent action,
			String title, String text) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_stat_launcher)
				.setContentTitle(title).setContentText(text);
		// .setDefaults(Notification.DEFAULT_ALL);

		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle(title);

		inboxStyle.addLine(text);

		mBuilder.setStyle(inboxStyle);
		PendingIntent p_intent = PendingIntent.getActivity(context, 0, action,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(p_intent);
		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(
				title.equalsIgnoreCase("Message") ? Constants.MSG_NOTI_ID
						: Constants.NOTI_NOTI_ID, mBuilder.build());
		notification_msg_active = true;
	}

	/**
	 * 
	 * FUNCTION TO ADD TEXT IN THE notification_msg_text FOR EXPANDABLE
	 * NOTIFICATION TO BE DISPLAYED IN THE NOTOFICATION DRAWER set the text in
	 * the notification according to length of the array add only 10-15 lines...
	 * 
	 * @param txt
	 */
	/*
	 * public static void AddLines(String txt) { int i = 0; while
	 * (notification_msg_text[i] != null) { i++; } notification_msg_text[i] =
	 * txt; }
	 */
	/**
	 * FUCTION TO CANCEL THE MESSAGE PRESENT IN THE NOTIFICATION DRAWER AND TO
	 * CLEAR THE TEXT OF notification_msg_text
	 * 
	 * @param context
	 */
	public static void CancelNotification(Context context, int notification_id) {
		NotificationManager mNotifyMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.cancel(notification_id);
	}

	public static RequestParams putCredentials(RequestParams params,
			SharedPreferences sharedPreferences) {

		params.put(Constants.QueryParameters.USERNAME, sharedPreferences
				.getString(Constants.PREF_KEYS.encripted_user_id, "").trim());
		params.put(Constants.QueryParameters.TOKEN, sharedPreferences
				.getString(Constants.PREF_KEYS.token, "").trim());
		return params;
	}

	public static StringBuilder putCredentials(StringBuilder strb,
			SharedPreferences sharedPreferences) {

		strb.append(sharedPreferences.getString(
				Constants.PREF_KEYS.encripted_user_id, "").trim());
		strb.append(Constants.NEW_LINE);
		strb.append(sharedPreferences.getString(Constants.PREF_KEYS.token, "")
				.trim());
		strb.append(Constants.NEW_LINE);
		return strb;
	}

	public static class DownloadFile extends
			AsyncTask<String, Integer, Boolean> {
		int id;
		Context context;

		public DownloadFile(Context context_) {
			context = context_;
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			id = Integer.valueOf(arg0[2]);
			try {
				URL url = new URL(arg0[0]);
				String filename = arg0[1];
				URLConnection conection = url.openConnection();
				conection.connect();
				InputStream inputStream = new BufferedInputStream(
						url.openStream(), 1024);
				File file = new File(Constants.PATH_TO_APP);
				file.mkdirs();
				OutputStream fileOutput = new FileOutputStream(new File(file,
						filename));
				byte buffer[] = new byte[1024];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);
				}
				fileOutput.close();
				return true;
			} catch (Exception e) {
				Utility.DEBUG(e);
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Utility.log(TAG, "download done");
				SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
				db.execSQL("update files set state=1 where _id=" + id);
				db.close();
			} else
				Utility.log(TAG, "download failed");
		}
	}

	public static void startActivity(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		Log.d(TAG, "stating login Activity");
		context.startActivity(intent);
	}

	public static void setAlarm(Context context, int time_in_milisec) {
		AlarmManager alarmmanager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, BackgroundService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
		alarmmanager.cancel(pi);
		alarmmanager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + time_in_milisec, pi);
		Utility.log(TAG, "alarm reset on " + time_in_milisec / 1000 + " sec");
	}

	public static String getTimeString(Context context, long time_in_milisec,
			boolean preposition) {
		return DateUtils.getRelativeTimeSpanString(context, time_in_milisec,
				preposition).toString();
		// ,DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0).toString();
	}

	public static String getSizeString(float bytes) {
		String res = " Bytes";
		if (bytes >= 1000) {
			bytes /= 1000; // KB
			res = " KB";
			if (bytes >= 1000) {
				bytes /= 1000; // MB
				res = " MB";
				if (bytes >= 1000) {
					bytes /= 1000;// GB
					res = " GB";
				}
			}
		}
		DecimalFormat format = new DecimalFormat(".##");
		return String.valueOf(format.format(bytes)) + res;
	}

	public static void startBackgroundService(Context context) {
		if (!BackgroundService.isServiceRunning()) {
			Intent intent = new Intent(context, BackgroundService.class);
			context.startService(intent);
		} else {
			Utility.setAlarm(context, 10000);
		}
	}
}
