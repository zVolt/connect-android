package in.siet.secure.sgi;

import in.siet.secure.Util.MyJsonHttpResponseHandler;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbConstants;
import in.siet.secure.dao.DbHelper;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * SENDER IS ONE WHO IS SENDING US MESSAGE
 * 
 * related to wake locks the GCM broadcast receiver starts the service holding
 * the wake-lock but if we start the service manually then the wake-lock should
 * be managed by the service itself(BackgroundService)
 * 
 * @author Zeeshan Khan
 * 
 */
public class BackgroundService extends IntentService {
	private static boolean SERVICE_WORKING;
	private static boolean HAVE_NEW_DATA;
	
	private boolean START_BY_GCM;
	private SharedPreferences spref;
	static String TAG = "in.siet.secure.sgi.BackgroundActivity";
	String sender_lid; // ye bhejny waly ki b-11-136 jaisi id
	
	private boolean got_new_sender;
	private Intent starter;

	public BackgroundService() {
		super("BackgroundService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Utility.log(TAG, "service hit");
		if (!SERVICE_WORKING) {
			Utility.log(TAG, "service was not running normal operation");
			setServiceRunning(true);
			starter = intent;
			if (Utility.isConnected(getApplicationContext())) {
				Bundle extras = intent.getExtras();

				setStartByGCM(false);
				if (extras != null) {
					// called by gcm braodcast receiver
					GoogleCloudMessaging gcm = GoogleCloudMessaging
							.getInstance(this);
					// The getMessageType() intent parameter must be the intent
					// you
					// received
					// in your BroadcastReceiver.
					String messageType = gcm.getMessageType(intent);

					/*
					 * Filter messages based on message type. Since it is likely
					 * that GCM will be extended in the future with new message
					 * types, just ignore any message types you're not
					 * interested in, or that you don't recognize.
					 */
					if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
							.equals(messageType)) {
						//
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
							.equals(messageType)) {
						// If it's a regular GCM message, do some work.
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
							.equals(messageType)) {
						// sync with back end and Post notification for the
						// same.

						setStartByGCM(true);
						sync();
					}

					// Release the wake lock provided by the
					// WakefulBroadcastReceiver.

				} else {
					// called by internal app components
					// holdWakeLock();
					sync();
				}
			} else {
				Utility.log(TAG, "aborting sync no internet");
				// set flag here that there is some data to be send to server
			}
		} else {
			/**
			 * service already running., now we may have some new data to send
			 * to server so we will use alarm service to run this service later
			 * in time automatically so that the new data get synchronize with
			 * server here we will just mark that the service should set an
			 * alarm in onDestroy method
			 */
			Utility.log(TAG, "service running so setting have new data");
			setHaveNewData(true);
		}
	}

	/**
	 * Hold a partial wake lock so that you don't get killed ;)
	 */
	/*
	 * private void holdWakeLock() { PowerManager pm = (PowerManager)
	 * getSystemService(POWER_SERVICE); if (wake == null) wake =
	 * pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG); if (!wake.isHeld())
	 * wake.acquire(); }
	 */
	/**
	 * release the wake lock
	 */
	/*
	 * private void releaseLock() { if (wake != null && wake.isHeld())
	 * wake.release(); }
	 */
	public void getMessagesFromServer() {
		new GetMessagesFromServer().execute();
	}

	public void getNotificationsFromServer() {
		// new GetNotificationsFromServer().execute();
	}

	@Override
	public void onDestroy() {
		Utility.log(TAG, "service destroyed");
		super.onDestroy();
	}

	private void setToStartAgain() {
		if (HAVE_NEW_DATA) {
			/**
			 * if we have some new data to send start the new service as soon as
			 * possible
			 */
			Utility.log(TAG, "setting service to run after 1 sec");
			Utility.setAlarm(getApplicationContext(), 1000);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * get data(messages and/or notification) from server if any and send
	 * acknowledgment for the same
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class GetMessagesFromServer extends
			AsyncTask<Void, JSONArray, JSONObject> {
		JSONObject ack_ids = new JSONObject();

		@Override
		protected JSONObject doInBackground(Void... params) {
			RequestParams reqparams = new RequestParams();
			Utility.putCredentials(reqparams, getSPreferences());
			SyncHttpClient client = new SyncHttpClient();

			final String user_id = getSPreferences().getString(
					Constants.PREF_KEYS.user_id, null); // this is me :D
			client.get(Utility.getBaseURL() + "query/give_me_messages",
					reqparams, new MyJsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							try {
								if (response.length() > 0) {

									DbHelper db = new DbHelper(
											getApplicationContext());

									ack_ids.put(
											Constants.JSONKEYS.MESSAGES.ACK,
											db.fillMessages(response, user_id));

								} else {
									Utility.log(TAG, "no messages");
								}
							} catch (Exception e) {
								Utility.DEBUG(e);
							}
						}
					});
			return ack_ids;
		}

		@Override
		protected void onPostExecute(JSONObject ack_ids) {
			sendAck(ack_ids);
		}
	}

	/**
	 * send JSONArray(2 size) containing JSONArray of messages and JSONArray of
	 * notifications (First two lines will be the users credentials to match) to
	 * server containing data to send and receive the the for itself if any
	 * 
	 * 1. get pending messages from database 2. get pending notifications from
	 * database in case user is faculty 3. create JSON string with user
	 * credentials out of data collected above 4. send to server 5. receive data
	 * from server (messages and notification) 6. fill database accordingly 7.
	 * trigger notification action
	 * 
	 * 
	 * break it into 2 parts 1 messages 2 notifications call both of them from
	 * here or specific as per requirement
	 */
	public void sync() {
		JSONObject data_to_send = new JSONObject();
		StringBuilder strb = new StringBuilder();

		try {
			/**
			 * set user credentials
			 */
			Utility.putCredentials(strb, getSPreferences());
			strb.append(getSPreferences().getBoolean(
					Constants.PREF_KEYS.is_faculty, false));
			strb.append(Constants.NEW_LINE);

			setHaveNewData(false);
			/**
			 * get pending messages
			 */
			JSONArray pending_messages = getPendingMessages();

			if (pending_messages.length() > 0)
				data_to_send.put(Constants.JSONKEYS.MESSAGES.MESSAGES,
						pending_messages);

			/**
			 * set pending notifications if user is faculty
			 */
			if (getSPreferences().getBoolean(Constants.PREF_KEYS.is_faculty,
					false)) {
				JSONArray pending_notifications = getPendingNotifications();
				if (pending_notifications.length() > 0)
					data_to_send.put(
							Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS,
							pending_notifications);
			}

		} catch (JSONException e) {
			Utility.DEBUG(e);
		}
		/**
		 * convert to one data to be send, hit server only if you have some data
		 */
		if (data_to_send.has(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS)
				|| data_to_send.has(Constants.JSONKEYS.MESSAGES.MESSAGES))
			strb.append(data_to_send.toString());

		HttpEntity body = null;
		try {
			body = new StringEntity(strb.toString());
		} catch (Exception e) {
			Utility.DEBUG(e);
		}
		/**
		 * send to server->credentials with some pending data if any
		 */
		Utility.log(TAG, "sending this " + strb.toString());
		new SendDataToServer().execute(body);

	}

	private JSONArray getPendingNotifications() {
		JSONArray notifications = new JSONArray();
		JSONObject notification;
		JSONArray attachments = new JSONArray();
		JSONObject attachment;
		Cursor c_attachment;
		String query = "select n.text,n.subject,n.time,m.course,m.branch,m.year,m.section,n._id,n.for_faculty from notification as n join user_mapper as m on n.target=m._id and n.state=? and n.sender=(select _id from user where login_id=?)";
		SQLiteDatabase db = new DbHelper(getApplicationContext()).getDb();
		String[] args = { String.valueOf(Constants.NOTI_STATE.PENDING),
				getSPreferences().getString(Constants.PREF_KEYS.user_id, null) };
		Cursor c = db.rawQuery(query, args);
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				try {
					notification = new JSONObject(); // ye wala konsa hai
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.TEXT,
							c.getString(0));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.SUBJECT,
							c.getString(1));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.TIME,
							c.getLong(2));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.COURSE,
							c.getString(3));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.BRANCH,
							c.getString(4));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.YEAR,
							c.getString(5));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.SECTION,
							c.getString(6));
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.ID,
							c.getInt(7));
					notification.put(
							Constants.JSONKEYS.NOTIFICATIONS.FOR_FACULTY,
							c.getInt(8));

					query = "select f.name,f.url,f.size from files as f join file_notification_map as fnm on f._id=fnm.file_id join notification as n on fnm.notification_id=n._id where n._id ='"
							+ c.getInt(7) + "'";
					Utility.log(TAG, query);
					c_attachment = db.rawQuery(query, null);
					if (c_attachment.moveToFirst()) {
						while (!c_attachment.isAfterLast()) {
							try {
								attachment = new JSONObject();
								attachment.put(Constants.JSONKEYS.FILES.NAME,
										c_attachment.getString(0));
								attachment.put(Constants.JSONKEYS.FILES.URL,
										c_attachment.getString(1));
								attachment.put(Constants.JSONKEYS.FILES.SIZE,
										c_attachment.getLong(2));
								/**
								 * put some mechanism to retry sending file in
								 * case of failure
								 */
								// Utility.log(TAG, c_attachment.getString(0));

								attachments.put(attachment);
							} catch (Exception e) {
								Utility.DEBUG(e);
							}
							c_attachment.moveToNext();
						}
						c_attachment.close();
					}
					notification.put(
							Constants.JSONKEYS.NOTIFICATIONS.ATTACHMENTS,
							attachments);
					// yaha pe code karna sayad teko thik hmm..likha rhne do
					// ye.. :P
					sendfiles(attachments);
					notifications.put(notification);
				} catch (Exception e) {
					Utility.DEBUG(e);
				}
				c.moveToNext();
			}
			c.close();
		}
		return notifications;
	}

	public void sendfiles(JSONArray attachments) {
		try {
			Utility.log(TAG, "sending a file");

			RequestParams params = new RequestParams();

			int len = attachments.length();
			for (int i = 0; i < len; i++) {
				Utility.log(
						TAG,
						"putting file no "
								+ i
								+ " "
								+ attachments.getJSONObject(i).getString(
										Constants.JSONKEYS.FILES.URL));
				params.put(
						Constants.QueryParameters.FILE + i,
						new File(attachments.getJSONObject(i).getString(
								Constants.JSONKEYS.FILES.URL)));
			}
			AsyncHttpClient client = new AsyncHttpClient();
			// client.addHeader("Content-Type", "multipart/form-data");
			// client.setTimeout(500000);

			client.post(getApplicationContext(), Utility.getBaseURL()
					+ "query/upload_file", params,
					new MyJsonHttpResponseHandler());
		} catch (Exception e) {
			Utility.DEBUG(e);
		}
	}

	private JSONArray getPendingMessages() {
		SQLiteDatabase db = new DbHelper(getApplicationContext()).getDb();
		String query = "select u.login_id,m.text,m.is_group_msg,m.time,m._id from messages as m join user as u on m.receiver=u._id where m.sender=(select _id from user where login_id=?) and m.state=?";
		String[] args = {
				getSPreferences().getString(Constants.PREF_KEYS.user_id, null),
				String.valueOf(Constants.MSG_STATE.PENDING) };
		Cursor c = db.rawQuery(query, args);
		JSONArray messages = new JSONArray();
		JSONObject message;
		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				try {
					// sender is the user no need t oput it in every message
					// object
					message = new JSONObject();
					message.put(Constants.JSONKEYS.MESSAGES.RECEIVER,
							c.getString(0));
					message.put(Constants.JSONKEYS.MESSAGES.TEXT,
							c.getString(1));
					message.put(Constants.JSONKEYS.MESSAGES.IS_GROUP_MESSAGE,
							c.getInt(2));
					message.put(Constants.JSONKEYS.MESSAGES.TIME, c.getLong(3));
					message.put(Constants.JSONKEYS.MESSAGES.ID, c.getInt(4));
					messages.put(message);
				} catch (JSONException e) {
					Utility.DEBUG(e);
				}
				c.moveToNext();
			}
		}
		c.close();
		return messages;
	}

	/**
	 * Send acknowledgment for received messages and notifications
	 * 
	 * @param msg_and_noti_ids
	 *            {@link JSONObject} containing two {@link JSONArray} first one
	 *            will contain IDs of messages and second one will contain IDs
	 *            of notification
	 */
	public void sendAck(JSONObject msg_and_noti_ids) {
		AsyncHttpClient client = new AsyncHttpClient();
		StringBuilder strb = new StringBuilder();
		/**
		 * set user credentials
		 */
		strb.append(getSPreferences().getString(
				Constants.PREF_KEYS.encripted_user_id, null).trim());
		strb.append(Constants.NEW_LINE);
		strb.append(getSPreferences()
				.getString(Constants.PREF_KEYS.token, null).trim());
		strb.append(Constants.NEW_LINE);
		strb.append(msg_and_noti_ids);
		HttpEntity entity = null;
		try {
			entity = new StringEntity(strb.toString());
		} catch (UnsupportedEncodingException e) {
			Utility.DEBUG(e);
		}
		Utility.log(TAG, "ack sending " + strb.toString());
		client.addHeader("Content-Type", "application/json");
		client.post(getApplicationContext(), Utility.getBaseURL()
				+ "query/receive_ack", entity, null,
				new MyJsonHttpResponseHandler() {
					@Override
					public void commonTask() {
						setServiceRunning(false);
						setToStartAgain();
					}
				});
	}

	class SendDataToServer extends AsyncTask<HttpEntity, Void, Void> {

		JSONObject ids_to_send = new JSONObject();

		@Override
		protected Void doInBackground(HttpEntity... params) {
			SyncHttpClient client = new SyncHttpClient();

			client.addHeader("Content-Type", "application/json");
			client.post(getApplicationContext(), Utility.getBaseURL()
					+ "query/sync", params[0], null,
					new MyJsonHttpResponseHandler() {
						/**
						 * got data(messages and notifications and
						 * acknowledgment for the data we have send) form server
						 * insert it into database
						 */
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							Utility.log(TAG, "received this: " + response);
							try {
								// get acks from data received from server and
								// update database
								JSONObject acks = new JSONObject();
								// get the ack ids for messages
								if (response
										.has(Constants.JSONKEYS.MESSAGES.ACK))
									acks.put(
											Constants.JSONKEYS.MESSAGES.ACK,
											response.getJSONArray(Constants.JSONKEYS.MESSAGES.ACK));
								// get ack ids for notifications
								if (response
										.has(Constants.JSONKEYS.NOTIFICATIONS.ACK))
									acks.put(
											Constants.JSONKEYS.NOTIFICATIONS.ACK,
											response.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.ACK));

								if (acks.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)
										|| acks.has(Constants.JSONKEYS.MESSAGES.ACK))
									new DbHelper(getApplicationContext())
											.receivedAck(acks);

								// get notification and insert it into db(help
								// of
								// DbHelper)

								// if sender of notification and/or message is
								// not
								// present fetch its data
								// and put him into contacts table before
								// inserting notification

								JSONArray ids_to_get = getNewSenders(response);
								// hit server synchronously :P
								// hiting only if new senders are there

								if (ids_to_get.length() > 0) {
									setGotNewSenders(false);
									try {
										HttpEntity entity = null;
										StringBuilder strb = new StringBuilder();
										Utility.putCredentials(strb,
												getSPreferences());
										strb.append(ids_to_get);
										entity = new StringEntity(strb
												.toString());
										SyncHttpClient client = new SyncHttpClient();
										client.addHeader("Content-Type",
												"application/json");
										client.post(
												getApplicationContext(),
												Utility.getBaseURL()
														+ "query/get_full_user_info",
												entity,
												null,
												new MyJsonHttpResponseHandler() {
													public void onSuccess(
															int statusCode,
															Header[] headers,
															JSONObject response) {
														Utility.log(
																TAG,
																"sucess get full detials of new user \n"
																		+ response);
														/*
														 * got the new senders
														 * now insert them into
														 * database
														 */
														new DbHelper(
																getApplicationContext())
																.insertUsers(response);
														setGotNewSenders(true);
													}

													/**
													 * skip rest process of
													 * inserting data to local
													 * database if we don't get
													 * senders details
													 */
													@Override
													public void commonTask() {
														setGotNewSenders(false);
													};
												});

									} catch (UnsupportedEncodingException e) {
										Utility.DEBUG(e);
									}

									Utility.log(TAG, "line after server hit");
								} else {
									setGotNewSenders(true);
								}
								if (getGotNewSenders()) {
									DbHelper db = new DbHelper(
											getApplicationContext());
									if (response
											.has(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS)) {
										JSONArray notifs = response
												.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS);

										ids_to_send
												.put(Constants.JSONKEYS.NOTIFICATIONS.ACK,
														db.fillNotifications(notifs));

										sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_NOTIFICATIONS);
									}
									// get messages and insert it into db(help
									// of
									// DbHelper)
									if (response
											.has(Constants.JSONKEYS.MESSAGES.MESSAGES)) {
										ids_to_send
												.put(Constants.JSONKEYS.MESSAGES.ACK,
														db.fillMessages(
																response.getJSONArray(Constants.JSONKEYS.MESSAGES.MESSAGES),
																sender_lid));
										sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_MESSAGES);
									}
									if ((response
											.has(Constants.JSONKEYS.MESSAGES.MESSAGES) && response
											.getJSONArray(
													Constants.JSONKEYS.MESSAGES.MESSAGES)
											.length() > 0)
											|| (response
													.has(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS) && response
													.getJSONArray(
															Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS)
													.length() > 0)) {
										Intent intent = new Intent(
												getApplicationContext(),
												MainActivity.class);

										Utility.buildNotification(
												getApplicationContext(),
												MainActivity.class, intent,
												"Some Updates",
												"You have new notification(s) and/or message(s)");
									}
								}
							} catch (JSONException e) {
								Utility.DEBUG(e);
							}
						}
					});

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// on main thread
			if (ids_to_send.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)
					|| ids_to_send.has(Constants.JSONKEYS.MESSAGES.ACK)) {
				sendAck(ids_to_send);
			} else {
				setServiceRunning(false);
				setToStartAgain();
			}

			if (START_BY_GCM)
				GcmBroadcastReceiver.completeWakefulIntent(starter);

		}
	}

	/**
	 * 
	 * @param response
	 *            {@link JSONObject} containing notifications and messages we
	 *            got from server
	 * @return {@link JSONArray} of new senders (only their IDs) among the data
	 *         provided by server
	 */
	private JSONArray getNewSenders(JSONObject response) {
		// check db for sender if not get from server
		JSONArray data_array;
		JSONArray userids_local = new JSONArray();
		JSONArray new_senders = null;
		int len;
		try {
			if (response.has(Constants.JSONKEYS.MESSAGES.MESSAGES)) {
				data_array = response
						.getJSONArray(Constants.JSONKEYS.MESSAGES.MESSAGES);
				len = data_array.length();
				if (len > 0) {
					for (int i = 0; i < len; i++) {
						// create array of userids need to be checked from local
						// db
						userids_local.put(data_array.getJSONObject(i).get(
								Constants.JSONKEYS.MESSAGES.SENDER));
					}
				}
			}
			if (response.has(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS)) {
				data_array = response
						.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS);
				len = data_array.length();
				if (len > 0) {
					for (int i = 0; i < len; i++) {
						// append userids need to be checked from local
						// db
						userids_local
								.put(data_array
										.getJSONObject(i)
										.getString(
												Constants.JSONKEYS.NOTIFICATIONS.SENDER));
					}
				}
			}
			new_senders = getUsersNotInDb(userids_local); // check userids
		} catch (JSONException e) {
			Utility.DEBUG(e);
		}
		return new_senders;
	}

	/**
	 * 
	 * @param userids
	 *            {@link JSONArray} of senders needs to be checked in local
	 *            database
	 * @return {@link JSONArray} on senders that are new i.e., do not exist in
	 *         local database
	 */
	private JSONArray getUsersNotInDb(JSONArray userids) {
		/**
		 * select login_id from (select 'emp-091' as login_id union all select
		 * 'b-11-136' union all select 'emp-000') as a where a.login_id not in
		 * (select login_id from user)
		 */

		String unionall = " union all ";
		String select = " select ";
		JSONArray ids_to_get_from_server = new JSONArray();
		StringBuilder query = new StringBuilder("select login_id from (");
		int len = userids.length();
		try {
			if (len > 0) {
				String[] args = new String[len];

				query.append("select ? as login_id ");
				for (int i = 0; i < len; i++) {
					args[i] = userids.getString(i);
					query.append(unionall);
					query.append(select);
					query.append(DbConstants.QUESTION_MARK);
				}
				query.append(") as a where a.login_id NOT IN (select login_id from user)");

				Cursor c = new DbHelper(getApplicationContext()).getDb()
						.rawQuery(query.toString(), args);
				if (c.moveToFirst()) {
					while (!c.isAfterLast()) {
						// ids that has to be get from server
						ids_to_get_from_server.put(c.getString(0));
						c.moveToNext();
					}
				}

			}
		} catch (Exception e) {
			Utility.DEBUG(e);
		}
		return ids_to_get_from_server;
	}

	private void sendBroadcast(String action) {
		Intent intent = new Intent(action);
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void setGotNewSenders(boolean set) {
		got_new_sender = set;
	}

	private boolean getGotNewSenders() {
		return got_new_sender;
	}

	private SharedPreferences getSPreferences() {
		if (spref == null)
			spref = getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spref;
	}

	private void setServiceRunning(boolean sending) {
		SERVICE_WORKING = sending;
	}

	private void setHaveNewData(boolean have_new_data) {
		HAVE_NEW_DATA = have_new_data;
	}

	private void setStartByGCM(boolean start_by_gcm) {
		START_BY_GCM = start_by_gcm;
	}
}