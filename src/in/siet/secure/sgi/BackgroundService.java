package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * SENDER IS ONE WHO IS SENDING US MESSAGE
 * 
 * @author swati
 * 
 */
public class BackgroundService extends Service {

	static SharedPreferences spref;
	static String TAG = "in.siet.secure.sgi.BackgroundActivity";
	static String sender_lid; // ye bhejny waly ki b-11-136 jaisi id
	private static SQLiteDatabase db;
	static int sender_id; // ye bhejny wali ki _id (pk in local table)

	/**
	 * if start getting ANR then start a new thread in the service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		spref = getApplicationContext().getSharedPreferences(
				Constants.pref_file_name, Context.MODE_PRIVATE);

		/*
		 * String tempquery = "select _id from user where login_id='" +
		 * (sender_lid = spref.getString( Constants.PreferenceKeys.user_id,
		 * null)) + "'";
		 * 
		 * db = new DbHelper(getApplicationContext()).getWritableDatabase(); c =
		 * db.rawQuery(tempquery, null); c.moveToFirst(); sender_id =
		 * c.getInt(0);
		 */
		if (spref != null) {
			if (spref.getString(Constants.PreferenceKeys.user_id, null) != null)
				handleIntent(intent); // not logged in
		} else {
			Utility.log(TAG, "pref is null");
		}
		return START_STICKY;
	}

	/*
	 * @SuppressWarnings("deprecation")
	 * 
	 * @Override public void onStart(Intent intent, int startId) { //
	 * handleIntent(intent); super.onStart(intent, startId); }
	 */

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
		new doPopo().execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		wake.release();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class doPopo extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Utility.log(TAG, "executing I am a service" + sender_lid);
			RequestParams reqparams = new RequestParams();

			Utility.putCredentials(reqparams, spref);
			AsyncHttpClient client = new SyncHttpClient();
			final String user_id = spref.getString(
					Constants.PreferenceKeys.user_id, null); // this is me :D
			client.get(Utility.getBaseURL() + "query/download_messages",
					reqparams, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {

							try {
								Utility.log(TAG, response.toString());
								// insert into db

								if (response.length() > 0) {

									JSONObject tmpobj;

									// for notification purpose
									tmpobj = response.getJSONObject(0);
									String sender_id, sender_name;
									sender_id = tmpobj
											.getString(Constants.JSONMEssageKeys.SENDER);
									sender_name = null;
									String noti_text = tmpobj
											.getString(Constants.JSONMEssageKeys.TEXT);
									// sender db me se nikal le yaha
									String qry = "select _id,f_name,l_name from user where login_id='"
											+ sender_id + "'";
									Utility.log(TAG, qry);
									db = new DbHelper(getApplicationContext())
											.getWritableDatabase();
									Cursor c = db.rawQuery(qry, null);
									int user_pk_id = -1;
									if (c.moveToFirst()) {
										sender_name = c.getString(1)
												+ c.getString(2);
										user_pk_id = c.getInt(0);
									} else {
										sender_name = "Guddu";
										/**
										 * no such user in local db get it from
										 * server if sender is a
										 * faculty(optional)
										 */

									}
									c.close();
									int mera_pk = -1;
									Utility.log(TAG, user_id);
									qry = "select _id from user where login_id='"
											+ user_id + "'";
									c = db.rawQuery(qry, null);
									if (c.moveToFirst()) {
										mera_pk = c.getInt(0);
									} else {
										Utility.log(TAG, "pk not found");
									}
									c.close();
									new DbHelper(getApplicationContext())
											.fillMessages(response, mera_pk);

									Intent intent = new Intent(
											getApplicationContext(),
											ChatActivity.class);
									intent.putExtra("name", sender_name);
									intent.putExtra("user_pk_id", user_pk_id); // receivers
																				// pk
									Utility.buildNotification(
											getApplicationContext(),
											ChatActivity.class, intent,
											sender_name, noti_text);

									int len = response.length();
									JSONArray ack = new JSONArray();
									for (int i = 0; i < len; i++) {
										try {
											ack.put(((JSONObject) response
													.get(i))
													.getInt(Constants.JSONMEssageKeys.ID));
										} catch (JSONException e) {
											Utility.log(TAG,
													"" + e.getMessage());
										}
									}
									sendAckForMessages(ack);
								} else {
									Utility.log(TAG, "no messages");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							Utility.log(TAG, "" + errorResponse);
						}

						// override all failure methods
					});
			return null;
		}

		public void sendAckForMessages(JSONArray ids) {
			AsyncHttpClient client = new SyncHttpClient();
			RequestParams params = new RequestParams();
			params.put(Constants.QueryParameters.USERNAME, sender_lid);
			params.put(Constants.QueryParameters.TOKEN,
					spref.getString(Constants.PreferenceKeys.token, null));
			params.put(Constants.QueryParameters.MSGIDS, ids);
			client.get(Utility.getBaseURL() + "query/receive_ack", params,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							Utility.log("TAG", response.toString());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {

						}
					});
		}
	}
}