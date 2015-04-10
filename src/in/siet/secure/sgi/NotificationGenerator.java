package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;

public class NotificationGenerator extends IntentService {
	public static final String TAG = "in.siet.secure.sgi.NotificationGenerator";

	private DbHelper dbh;

	public NotificationGenerator() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent_) {
		Utility.log(TAG, "called notification Builder");
		// extract data from databse and fire notification for unread data
		// extract new unread data from local db count it and show notifications
		int no_of_msgs = 0, no_of_noti = 0, no_of_msg_senders = 0, no_of_noti_senders = 0;
		long user_pk = -1;
		Utility.log(TAG,"getting messages");
		String query = "select count(*),sender from messages where state IN(?,?) group by sender";
		Cursor c = getDbHelper().getDb().rawQuery(
				query,
				new String[] { String.valueOf(Constants.MSG_STATE.RECEIVED),
						String.valueOf(Constants.MSG_STATE.ACK_SEND) });
		c.moveToFirst();

		while (!c.isAfterLast()) {

			no_of_msgs += c.getInt(0);
			user_pk = c.getLong(1);
			no_of_msg_senders++;
			c.moveToNext();
		}
		c.close();
		Utility.log(TAG,"new sender "+no_of_msg_senders+" new messages "+no_of_msgs);
		if (no_of_msg_senders > 1) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra(Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW,
					FragmentContacts.TAG);
			Utility.buildNotification(getApplicationContext(), intent,
					Constants.MSG_NOTI_ID, no_of_msgs + " messages from "
							+ no_of_msg_senders + " users");
		} else if (no_of_msg_senders == 1) {
			Intent intent = new Intent(getApplicationContext(),
					ChatActivity.class);
			String sender_name = "";
			query = "select f_name,l_name from user where _id=?";
			c = getDbHelper().getDb().rawQuery(query,
					new String[] { String.valueOf(user_pk) });
			if (c.moveToFirst()) {
				sender_name = c.getString(0) + Constants.SPACE + c.getString(1);
			}
			c.close();

			intent.putExtra(Constants.INTENT_EXTRA.CHAT_USER_PK, user_pk);
			Utility.buildNotification(getApplicationContext(), intent,
					Constants.MSG_NOTI_ID,
					(no_of_msgs > 1 ? (no_of_msgs + " messages") : ("Message"))
							+ " from " + sender_name);
		}
		Utility.log(TAG,"getting notifications");
		query = "select count(*),sender from notification where state IN(?,?) group by sender";
		c = getDbHelper().getDb().rawQuery(
				query,
				new String[] { String.valueOf(Constants.NOTI_STATE.RECEIVED),
						String.valueOf(Constants.NOTI_STATE.ACK_SEND) });
		c.moveToFirst();
		while (!c.isAfterLast()) {
			no_of_noti += c.getInt(0);
			no_of_noti_senders++;
			user_pk = c.getLong(1);
			c.moveToNext();
		}
		Utility.log(TAG,"new sender "+no_of_noti_senders+" new notifications "+no_of_noti);
		c.close();
		if (no_of_noti_senders > 0) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.putExtra(Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW,
					FragmentNotification.TAG);
			Utility.buildNotification(getApplicationContext(), intent,
					Constants.NOTI_NOTI_ID, no_of_noti + " new notification"
							+ (no_of_noti > 1 ? "s" : ""));
		} else if (no_of_noti_senders == 1) {

		}
	}

	private DbHelper getDbHelper() {
		if (dbh == null) {
			dbh = new DbHelper(getApplicationContext());
		}
		return dbh;
	}
	/*
	 * private void dodo() throws Exception { JSONObject ids_to_send = null,
	 * response = new JSONObject(); // messages if
	 * (ids_to_send.has(Constants.JSONKEYS.MESSAGES.ACK)) { JSONArray msg_arr =
	 * response .getJSONArray(Constants.JSONKEYS.MESSAGES.MESSAGES); JSONObject
	 * msg; HashSet<String> senders = new HashSet<String>(); int msgs_cnt =
	 * msg_arr.length(); for (int i = 0; i < msgs_cnt; i++) { msg =
	 * msg_arr.getJSONObject(i);
	 * senders.add(msg.getString(Constants.JSONKEYS.MESSAGES.SENDER)); } int
	 * senders_len = senders.size(); if (senders_len > 1) { Intent intent = new
	 * Intent(getApplicationContext(), MainActivity.class);
	 * intent.putExtra(Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW,
	 * FragmentContacts.TAG);
	 * 
	 * // gte pk of new sender if one if // there // are many senders // then
	 * change the //intent class to main activity
	 * Utility.buildNotification(getApplicationContext(), intent,
	 * Constants.MSG_NOTI_ID, msgs_cnt + " messages from " + senders_len +
	 * " users");
	 * 
	 * } else if (senders_len == 1) { Intent intent = new
	 * Intent(getApplicationContext(), ChatActivity.class); String sndr =
	 * senders.iterator().next();
	 * 
	 * intent.putExtra(Constants.INTENT_EXTRA.USER_NAME_TO_CHAT, sndr);
	 * Utility.buildNotification(getApplicationContext(), intent,
	 * Constants.MSG_NOTI_ID, msgs_cnt > 1 ? (msgs_cnt + " messages") :
	 * ("Message") + " from " + sndr); }
	 * 
	 * } // notifications if
	 * (ids_to_send.has(Constants.JSONKEYS.NOTIFICATIONS.ACK)) { int noti_cnt =
	 * ids_to_send.getJSONArray(
	 * Constants.JSONKEYS.NOTIFICATIONS.NOTIFICATIONS).length(); Intent intent =
	 * new Intent(getApplicationContext(), MainActivity.class);
	 * intent.putExtra(Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW,
	 * FragmentNotification.TAG);
	 * Utility.buildNotification(getApplicationContext(), intent,
	 * Constants.NOTI_NOTI_ID, noti_cnt + " new notification" + (noti_cnt > 1 ?
	 * "s" : "")); } }
	 */
}