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
	// private static boolean HAVE_NEW_DATA;
	// private int NO_OF_INSTANCES;
	// private final IBinder binder = new LocalBinder();
	private boolean START_BY_GCM;
	private SharedPreferences spref;
	private DbHelper dbh;
	static String TAG = "in.siet.secure.sgi.BackgroundActivity";
	private String my_userid;
	private JSONArray json_fileid = null;

	private boolean got_new_sender;
	private Intent starter;

	public BackgroundService() {
		super("BackgroundService");
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getApplicationContext());
		return dbh;
	}

	private boolean checkGcmMsgType(Intent intent) {
		boolean res = false;
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		/*
		 * Filter messages based on message type. Since it is likely that GCM
		 * will be extended in the future with new message types, just ignore
		 * any message types you're not interested in, or that you don't
		 * recognize.
		 */
		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
				.equals(messageType)) {
			// If it's a regular GCM message, do some work.
		} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
				.equals(messageType)) {
			res = true;
		}
		return res;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Utility.log(TAG, "service has accepted a request vai intent");
		starter = intent; // intent required to release the wakeful
							// serverice wakelock
		if (!BackgroundService.isServiceRunning()) {
			setStartByGCM(checkGcmMsgType(intent));
			sync();
		}

		/*
		 * else { /** service already running., now we may have some new data to
		 * send to server so we will use alarm service to run this service later
		 * in time automatically so that the new data get synchronize with
		 * server here we will just mark that the service should set an alarm in
		 * onDestroy method
		 * 
		 * Utility.log(TAG, "service running so setting have new data");
		 * setHaveNewData(true); }
		 */
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
	/**
	 * not called from any where
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

	@Override
	public IBinder onBind(Intent intent) {
		return null;
		// return binder;
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

			client.get(Utility.getBaseURL(getApplicationContext())
					+ "query/give_me_messages", reqparams,
					new MyJsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {
							try {
								if (response.length() > 0) {

									ack_ids.put(
											Constants.JSONKEYS.MESSAGES.ACK,
											getDbHelper().fillMessages(
													response, getMyUserid()));

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
	 */

	public class StartSync extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			sync();
			return null;
		}
	}

	public void sync() {
		Utility.log(TAG, "serice is in sync");
		setServiceRunning(true);
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

			// setHaveNewData(false);
			/**
			 * going to get all the pending data so we have no new data get
			 * pending messages
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
		Utility.log(TAG, "sending data to a new thread");
		new SendDataToServer().execute(body);

	}

	/**
	 * ye bas notification aur usky sath attachments ki list de ok >? hmm
	 * irrespective of uploading..ryt?han idont know kya soach ri tu local db me
	 * notifications hongi kuch jinky sath files hongi maybe?hmm wo nikaly db se
	 * JSAONArray banaye aur return kary bas no uploding n all hmm ok uploading
	 * upar handle karengy ok hmm
	 * 
	 * @return
	 */
	private JSONArray getPendingNotifications() {
		JSONArray notifications = new JSONArray();
		JSONObject notification;
		JSONArray attachments = new JSONArray();
		JSONObject attachment = new JSONObject();
		Cursor c_attachment;
		int status;
		String query = "select n.text,n.subject,n.time,m.course,m.branch,m.year,m.section,n._id,n.for_faculty from notification as n join user_mapper as m on n.target=m._id and n.state=? and n.sender=(select _id from user where login_id=?)";

		SQLiteDatabase db = getDbHelper().getDb();
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
					query = "select f.name,f.url,f.size,f._id from files as f join file_notification_map as fnm on f._id=fnm.file_id join notification as n on fnm.notification_id=n._id where n._id ='"
							+ c.getInt(7) + "'";
					Utility.log(TAG, query);
					c_attachment = db.rawQuery(query, null);
					/**
					 * put some mechanism to retry sending file in case of
					 * failure
					 */
					Utility.log(TAG, "before sending file");
					sendfile(c_attachment, c.getLong(2),
							spref.getString(Constants.PREF_KEYS.user_id, null));
					attachment.put(Constants.JSONKEYS.FILES.ID, json_fileid);
					notification.put(
							Constants.JSONKEYS.NOTIFICATIONS.ATTACHMENTS,
							attachment);
					Utility.log(TAG, "after sending file");
					c_attachment.close();

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

	public void sendfile(Cursor c_attachment, long noti_time, String user_id) {
		JSONArray attachments = new JSONArray();
		JSONObject attachment = new JSONObject();
		RequestParams params = new RequestParams();
		try {
			int i = 0;
			// meaole
			if (c_attachment.moveToFirst()) {
				while (!c_attachment.isAfterLast()) {
					try {
						attachment = new JSONObject();
						attachment.put(Constants.JSONKEYS.FILES.NAME,
								c_attachment.getString(0));
						attachment.put(Constants.JSONKEYS.NOTIFICATIONS.TIME, // naya
																				// contant
																				// bana
																				// lo
																				// files
																				// k
																				// andar
								noti_time);
						attachment.put(Constants.JSONKEYS.FILES.ID,
								c_attachment.getString(3));
						File file =new File(c_attachment.getString(1)); 
						attachment.put(Constants.JSONKEYS.FILES.SIZE,file.length() );
						params.put(Constants.QueryParameters.FILE + i++,
								file);
						attachments.put(attachment);
					} catch (Exception e) {
						Utility.DEBUG(e);
					}
					c_attachment.moveToNext();
				}
			}
			Utility.log(TAG, "sending a file");
			params.put(Constants.QueryParameters.FILE_ID, attachments);
			params.put(Constants.QueryParameters.USERNAME, user_id);
			AsyncHttpClient client = new SyncHttpClient();
			client.post(getApplicationContext(),
					Utility.getBaseURL(getApplicationContext())
							+ "query/upload_file", params,
					new MyJsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							Utility.log(TAG, response.toString());
							Utility.log(TAG, "sendfile on success");
							try {
								if (response.has(Constants.JSONKEYS.FILES.ID))
									json_fileid = response
											.getJSONArray(Constants.JSONKEYS.FILES.ID);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject response) {
							Utility.log(TAG, "..fail1 " + response.toString());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Utility.log(TAG,
									"..fail2" + throwable.getLocalizedMessage());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONArray errorResponse) {
							Utility.log(TAG,
									"..fail3" + throwable.getLocalizedMessage());
						}
					});

		} catch (Exception e) {
			Utility.DEBUG(e);
		}
	}

	private JSONArray getPendingMessages() {
		SQLiteDatabase db = getDbHelper().getDb();
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
							Utility.encode(c.getString(1)));
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
	public void sendAck(final JSONObject msg_and_noti_ids) {
		Utility.log(TAG, "sending ack now");
		AsyncHttpClient client = new AsyncHttpClient();
		StringBuilder strb = new StringBuilder();
		/**
		 * set user credentials
		 */
		strb = Utility.putCredentials(strb, getSPreferences());
		strb.append(msg_and_noti_ids);
		HttpEntity entity = null;
		try {
			entity = new StringEntity(strb.toString());
		} catch (UnsupportedEncodingException e) {
			Utility.DEBUG(e);
		}
		Utility.log(TAG, "ack sending " + strb.toString());
		client.addHeader("Content-Type", "application/json");
		client.post(getApplicationContext(),
				Utility.getBaseURL(getApplicationContext())
						+ "query/receive_ack", entity, null,
				new MyJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// update msg and notification state state to ack_sent
						try {
							if (msg_and_noti_ids
									.has(Constants.JSONKEYS.MESSAGES.ACK))
								getDbHelper()
										.updateMsgState(
												msg_and_noti_ids
														.getJSONArray(Constants.JSONKEYS.MESSAGES.ACK));
							if (msg_and_noti_ids
									.has(Constants.JSONKEYS.NOTIFICATIONS.ACK))
								getDbHelper()
										.updateNotiState(
												msg_and_noti_ids
														.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.ACK));
						} catch (JSONException e) {
							Utility.DEBUG(e);
						}
						commonTask();
					}

					@Override
					public void commonTask() {
						Utility.log(TAG, "sending ack done");
						setServiceRunning(false);
						if (START_BY_GCM)
							GcmBroadcastReceiver.completeWakefulIntent(starter);
					}
				});
	}

	class SendDataToServer extends AsyncTask<HttpEntity, Void, Void> {

		JSONObject ids_to_send = new JSONObject();

		@Override
		protected Void doInBackground(HttpEntity... params) {
			Utility.log(TAG,
					"new thread received request to set data over network");
			SyncHttpClient client = new SyncHttpClient();
			client.addHeader("Content-Type", "application/json");
			client.post(getApplicationContext(),
					Utility.getBaseURL(getApplicationContext()) + "query/sync",
					params[0], null, new MyJsonHttpResponseHandler() {
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
									getDbHelper().receivedAck(acks);

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

								if (ids_to_get.length() > 0) {
									// hiting only if new senders are there
									setGotNewSenders(false);
									try {
										Utility.log(TAG,
												"goning to hit server again");
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
												Utility.getBaseURL(getApplicationContext())
														+ "query/get_full_user_info",
												entity,
												null,
												new MyJsonHttpResponseHandler() {
													public void onSuccess(
															int statusCode,
															Header[] headers,
															JSONObject response) {
														Utility.log(TAG,
																"sucess get full detials of new user");
														/*
														 * got the new senders
														 * now insert them into
														 * database
														 */
														getDbHelper()
																.insertUsers(
																		response);
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
														Utility.log(TAG,
																"fail to get full detials of new user");
														setGotNewSenders(false);
													};
												});

									} catch (UnsupportedEncodingException e) {
										Utility.DEBUG(e);
									}

									Utility.log(TAG, "line after server hit");
								} else {
									Utility.log(TAG, "we have no new senders");
									setGotNewSenders(true);
								}
								if (getGotNewSenders()) {

									if (response
											.has(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS)) {
										JSONArray notifs = response
												.getJSONArray(Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS);

										ids_to_send
												.put(Constants.JSONKEYS.NOTIFICATIONS.ACK,
														getDbHelper()
																.fillNotifications(
																		notifs));

										sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_NOTIFICATIONS);
									}
									// get messages and insert it into db(help
									// of
									// DbHelper)
									if (response
											.has(Constants.JSONKEYS.MESSAGES.MESSAGES)) {
										ids_to_send
												.put(Constants.JSONKEYS.MESSAGES.ACK,
														getDbHelper()
																.fillMessages(
																		response.getJSONArray(Constants.JSONKEYS.MESSAGES.MESSAGES),
																		getMyUserid()));
										sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_MESSAGES);
									}

									if (ids_to_send
											.has(Constants.JSONKEYS.MESSAGES.ACK)) {
										Intent intent = new Intent(
												getApplicationContext(),
												MainActivity.class);
										// gte pk of new sender if one if there
										// are many senders then change the
										// intent class to main activity
										Utility.buildNotification(
												getApplicationContext(),
												intent, "Message",
												"You have new messages");
									}
									if (ids_to_send
											.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)) {
										// chnage the open notification fragment
										Intent intent = new Intent(
												getApplicationContext(),
												MainActivity.class);
										intent.putExtra(
												Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW,
												FragmentNotification.TAG);
										Utility.buildNotification(
												getApplicationContext(),
												intent, "Notifications",
												"You have new notifications");
									}
								}
							} catch (JSONException e) {
								Utility.DEBUG(e);
							}
						}

						public void commonTask() {
							Utility.log(TAG,
									"network operation complete with some error");
						};
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
				if (START_BY_GCM)
					GcmBroadcastReceiver.completeWakefulIntent(starter);
			}

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

				Cursor c = getDbHelper().getDb().rawQuery(query.toString(),
						args);
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
		Utility.log(TAG, "service running " + sending);
		SERVICE_WORKING = sending;
	}

	public static boolean isServiceRunning() {
		return SERVICE_WORKING;
	}

	/*
	 * public void setHaveNewData(boolean have_new_data) { // HAVE_NEW_DATA =
	 * have_new_data; }
	 */
	public void setStartByGCM(boolean start_by_gcm) {
		START_BY_GCM = start_by_gcm;
	}

	private String getMyUserid() {
		if (my_userid == null)
			my_userid = getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE).getString(
					Constants.PREF_KEYS.user_id, null);
		return my_userid;
	}
}