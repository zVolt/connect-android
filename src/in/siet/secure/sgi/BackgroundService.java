package in.siet.secure.sgi;

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
import android.support.v4.content.LocalBroadcastManager;

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

	private SharedPreferences spref;
	static String TAG = "in.siet.secure.sgi.BackgroundActivity";
	String sender_lid; // ye bhejny waly ki b-11-136 jaisi id
	private WakeLock wake;
	private int upload_success= Constants.UPLOAD_STATUS.UPLOAD_DEFAULT;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		spref = getApplicationContext().getSharedPreferences(
				Constants.pref_file_name, Context.MODE_PRIVATE);
		if (spref != null) {
			if ((sender_lid = spref
					.getString(Constants.PREF_KEYS.user_id, null)) != null)
				handleIntent(intent); // if logged in
		} else {
			Utility.log(TAG, "pref is null");
		}
		return START_STICKY;
	}

	/**
	 * Hold a partial wake lock so that you don't get killed ;)
	 */
	private void holdWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		if (wake == null)
			wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		if (!wake.isHeld())
			wake.acquire();
	}

	/**
	 * release the wake lock
	 */
	private void releaseLock() {
		if (wake != null && wake.isHeld())
			wake.release();
	}

	public void handleIntent(Intent intent) {

		holdWakeLock();
		// sendToServer();
		// getMessagesFromServer();
		new StartSync().execute();
	}

	public void getMessagesFromServer() {
		new GetMessagesFromServer().execute();
	}

	public void getNotificationsFromServer() {
		// new GetNotificationsFromServer().execute();
	}

	@Override
	public void onDestroy() {
		releaseLock();
		super.onDestroy();
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
			Utility.putCredentials(reqparams, spref);
			SyncHttpClient client = new SyncHttpClient();

			final String user_id = spref.getString(Constants.PREF_KEYS.user_id,
					null); // this is me :D
			client.get(Utility.getBaseURL() + "query/give_me_messages",
					reqparams, new JsonHttpResponseHandler() {
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

									Intent intent = new Intent(
											getApplicationContext(),
											ChatActivity.class);

									Utility.buildNotification(
											getApplicationContext(),
											MainActivity.class, intent,
											"New Messages",
											"You have new messages");

								} else {
									Utility.log(TAG, "no messages");
								}
							} catch (Exception e) {
								Utility.DEBUG(e);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							Utility.log(TAG, throwable.getLocalizedMessage());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Utility.log(TAG, throwable.getLocalizedMessage());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONArray errorResponse) {
							Utility.log(TAG, throwable.getLocalizedMessage());
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
	// StartS
	public class StartSync extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			//sync ko break karna 2 part me
			//1 only message syncronize
			//2 onlt notification syncronize
			//thik ?:/
			// thik mat karna kuch sync aysy ka aysa copy paste ok hmm :/ :D chor :/
			sync();
			return null;
		}
		
	}
	public void sync() {
		JSONObject data_to_send = new JSONObject();
		StringBuilder strb = new StringBuilder();

		try {
			/**
			 * set user credentials
			 */
			Utility.putCredentials(strb, spref);
			strb.append(spref.getBoolean(Constants.PREF_KEYS.is_faculty, false));
			strb.append(Constants.NEW_LINE);
			/**
			 * set pending messages
			 */
			JSONArray pending_messages = getPendingMessages();

			if (pending_messages.length() > 0)
				data_to_send.put(Constants.JSONKEYS.MESSAGES.MESSAGES,
						pending_messages);

			/**
			 * set pending notifications if user is faculty
			 */
			if (spref.getBoolean(Constants.PREF_KEYS.is_faculty, false)) {
				JSONArray pending_notifications = getPendingNotifications();
				//mtlb not on main thread yes
				//thik to yaha sync ka use kr skte yo but...
				//hamko 2 parts me divide karna hoga kam
				//suppose 3 notifications hai hamary pas 1st having files 2nd third have nofiles 
				// so 2nd 3rd should be uploaded immediately irrespectivie of the 1st notifications state ? ok :/v ok
				// ni samjhee to bol clea karu dunara
				// ni hamko 2 set banay notifications k 
				//1 jismy files hai 2nd having no files 
				// loop karegi each notification and checD:k for files entry thi kh a?Nnnhaannn thannn thikkk haiii :D :D
				// ab jinmy file ni hai unko simple process se bhej de jaisy abhi tak jary thik ? hmm
				// aur jinmy file hai unko AsyncHttpClient se upload karny k liye laga de
				// abhi bhi to waise hi bhej rae na.. ni abhi getPending me hora kam 
				// han thik us function ko call kar le na yaha second case k lie.. han hmmmm phlen  uucassek y moen
				// usky on sucess me fir se server pe kuch notifications jayngi ok :O kya kfyilaes  wali notificaion isly to alag ki na
				// send file me jo bhej ri notification ki list
				// teko bola tha multiple files kaisy bhejty dekhna
				// aur usky onSucess pe  okknkotifcation bhi bhej dena
				// bas ? :D
				// chor bhul jaygi kya sab meko fm bolti :P backup rkhna chaiye hamesha :/
				// to samaj gai sara kam ? han server pe multiple files receive karna dekh pehly hmm dekh lungi
				
				
				// 1. receive multiple files on server
				// 2. shift sync() function code to StartSync's doInBackground()
				// 3. seperate notifications with files(call send files on each notification that will send its files to server and on sucess the notification itself) and without files(Process normally)
				// 4. send all files of one notification and onSucess pe notification ko khud bhi send karna hai (seperate function)
				// bas itta kar k bata
				// 5. clean getPendingNotifications
				// send files ko JSONObject dede na notification ho jismy aur notification k andar files ho
				
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
		 * send to server either credentials only or with some pending data
		 */
		Utility.log(TAG, "sending this " + strb.toString());
		new SendDataToServer().execute(body);

	}
/**
 * ye bas notification aur usky sath attachments ki list de
 * ok >? hmm irrespective of uploading..ryt?han idont know kya soach ri tu
 * local db me notifications hongi kuch jinky sath files hongi maybe?hmm
 * wo nikaly db se JSAONArray banaye aur return kary bas no uploding n all hmm ok
 * uploading upar handle karengy ok hmm
 * @return
 */
	private JSONArray getPendingNotifications() {
		JSONArray notifications = new JSONArray();
		JSONObject notification;
		JSONArray attachments = new JSONArray();
		JSONObject attachment;
		Cursor c_attachment;
		int status;
		String query = "select n.text,n.subject,n.time,m.course,m.branch,m.year,m.section,n._id,n.for_faculty from notification as n join user_mapper as m on n.target=m._id and n.state=? and n.sender=(select _id from user where login_id=?)";
		SQLiteDatabase db = new DbHelper(getApplicationContext()).getDb();
		String[] args = { String.valueOf(Constants.NOTI_STATE.PENDING),
				spref.getString(Constants.PREF_KEYS.user_id, null) };
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
					notification.put(Constants.JSONKEYS.NOTIFICATIONS.FOR_FACULTY,
							c.getInt(8));

					query = "select f.name,f.url,f.size,f._id from files as f join file_notification_map as fnm on f._id=fnm.file_id join notification as n on fnm.notification_id=n._id where n._id ='"
							+ c.getInt(7) + "'";
					Utility.log(TAG, query);
					c_attachment = db.rawQuery(query, null);
								/**
								 * put some mechanism to retry sending file in
								 * case of failure
								 */				
					//mai yaha JSON array jo ban raha usi ko
					// 
						sendfile(c_attachment,c.getLong(2),spref.getString(Constants.PREF_KEYS.user_id, null));
						c_attachment.close();
					// yaha pe code karna sayad teko thik hmm..likha rhne do
					// ye.. :P
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

	public void sendfile(Cursor c_attachment,long noti_time,String user_id) {
		//meko structure batao kaisa data bana k bhej ri server pe
		// hmm
		JSONArray attachments = new JSONArray();
		JSONObject attachment = new JSONObject();
		
		RequestParams params = new RequestParams();
		try {
			
			int i=0;
			if (c_attachment.moveToFirst()) {
				while (!c_attachment.isAfterLast()) {
					try {
						attachment = new JSONObject();
						attachment.put(Constants.JSONKEYS.FILES.NAME,
								c_attachment.getString(0));
						attachment.put(Constants.JSONKEYS.NOTIFICATIONS.TIME, //naya contant bana lo files k andar
								noti_time);
						attachment.put(Constants.JSONKEYS.FILES.ID,
								c_attachment.getString(3));
						params.put(Constants.QueryParameters.FILE+i++, new File(c_attachment.getString(1))); //bt fir ham fetch kaise karenge?? server pe
						attachments.put(attachment);
					}
					catch(Exception e){
						Utility.DEBUG(e);
					}
					c_attachment.moveToNext();					
				}
			}						
			Utility.log(TAG, "sending a file");			
	//		JSONObject jsonob=new JSONObject;
	//		jsonob.put(name, value);						
			params.put(Constants.QueryParameters.FILE_ID,attachments);
			params.put(Constants.QueryParameters.USERNAME, user_id);
			AsyncHttpClient client = new SyncHttpClient(); 		
			//client.addHeader("Content-Type", "multipart/form-data");
			//client.setTimeout(500000);
			client.post(getApplicationContext(), Utility.getBaseURL()
					+ "query/upload_file", params,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							Utility.log(TAG, response.toString());						
							Utility.log(TAG, "sendfile on success");
							upload_success=Constants.UPLOAD_STATUS.UPLOAD_STATUS;
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject response) {
							Utility.log(TAG, "..fail1 " + response.toString());
							upload_success=Constants.UPLOAD_STATUS.UPLOAD_FAILURE;
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Utility.log(TAG,"..fail2"+ throwable.getLocalizedMessage());
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONArray errorResponse) {
							Utility.log(TAG,"..fail3"+ throwable.getLocalizedMessage());
						}						
					});
		} catch (Exception e) {
			Utility.DEBUG(e);
		}
	//	return upload_success;
	}

	private JSONArray getPendingMessages() {
		SQLiteDatabase db = new DbHelper(getApplicationContext()).getDb();
		String query = "select u.login_id,m.text,m.is_group_msg,m.time,m._id from messages as m join user as u on m.receiver=u._id where m.sender=(select _id from user where login_id=?) and m.state=?";
		String[] args = { spref.getString(Constants.PREF_KEYS.user_id, null),
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
		strb.append(spref
				.getString(Constants.PREF_KEYS.encripted_user_id, null).trim());
		strb.append(Constants.NEW_LINE);
		strb.append(spref.getString(Constants.PREF_KEYS.token, null).trim());
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
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						Utility.log(TAG, "ack received " + response.toString());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Utility.log(
								TAG,
								"on failure sendAck "
										+ throwable.getLocalizedMessage());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						Utility.log(
								TAG,
								"on failure sendAck "
										+ throwable.getLocalizedMessage());
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						Utility.log(
								TAG,
								"on failure sendAck "
										+ throwable.getLocalizedMessage());
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
					new JsonHttpResponseHandler() {
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
									try {
										HttpEntity entity = null;
										StringBuilder strb = new StringBuilder();
										Utility.putCredentials(strb, spref);
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
												entity, null,
												new JsonHttpResponseHandler() {
													public void onSuccess(
															int statusCode,
															Header[] headers,
															JSONObject response) {
														Utility.log(
																TAG,
																"sucess get ids of new user \n"
																		+ response);
														// got the data now
														// insert
														// it into database and
														// you
														// will be free
														new DbHelper(
																getApplicationContext())
																.insertUser(response);

													};
												});

									} catch (UnsupportedEncodingException e) {
										Utility.DEBUG(e);
									}

									Utility.log(TAG, "line afeter server hit");
								}
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
								// get messages and insert it into db(help of
								// DbHelper)
								if (response
										.has(Constants.JSONKEYS.MESSAGES.MESSAGES)) {
									ids_to_send.put(
											Constants.JSONKEYS.MESSAGES.ACK,
											db.fillMessages(
													response.getJSONArray(Constants.JSONKEYS.MESSAGES.MESSAGES),
													sender_lid));
									sendBroadcast(Constants.LOCAL_INTENT_ACTION.RELOAD_MESSAGES);
								}

							} catch (JSONException e) {
								Utility.DEBUG(e);
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {
							Utility.log(
									TAG,
									"on failure sync "
											+ throwable.getLocalizedMessage());
							super.onFailure(statusCode, headers,
									responseString, throwable);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONArray errorResponse) {
							Utility.log(
									TAG,
									"on failure sync "
											+ throwable.getLocalizedMessage());
							super.onFailure(statusCode, headers, throwable,
									errorResponse);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							Utility.log(
									TAG,
									"on failure sync "
											+ throwable.getLocalizedMessage());
							super.onFailure(statusCode, headers, throwable,
									errorResponse);
						}
						// override all sucess and failure methods
					});

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// on main thread
			if (ids_to_send.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)
					|| ids_to_send.has(Constants.JSONKEYS.MESSAGES.ACK))
				sendAck(ids_to_send);
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
		// select login_id from (select 'emp-091' as login_id union all select
		// 'b-11-136'
		// union all select 'emp-000') as a where a.login_id not in (select
		// login_id from user)

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
}