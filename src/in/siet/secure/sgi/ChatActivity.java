package in.siet.secure.sgi;

import in.siet.secure.Util.Message;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.MessagesAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

/**
 * this activity display the chat history between two users
 * 
 * @author Zeeshan Khan
 * 
 */
public class ChatActivity extends ActionBarActivity {
	String title;
	ListView list;
	EditText msg;
	private Cursor c;
	MessagesAdapter adapter;
	SharedPreferences spref;
	// BackgroundService service;
	// boolean binded;
	long receiver_id; // this is id of the receiver to whom user will text
	String receiver_lid, user_image_url;
	long sender_id; // sender is the user itself who is using the app
	String sender_lid;
	private DbHelper dbh;
	long msg_id;
	private ContentValues values;
	private static final String TAG = "in.siet.secure.sgi.ChatActivity";
	Toolbar toolbar;
	private static final String query = "select messages._id,text,time,state from messages where sender=? or receiver=?";
	private BroadcastReceiver local_broadcast_receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.log(TAG, "received broadcast update messages");
			updateCursor();
			markAllRead();
		}
	};
	private MarkAllRead mark_all;

	private void markAllRead() {
		if (mark_all != null
				&& mark_all.getStatus() != AsyncTask.Status.FINISHED)
			mark_all.cancel(true);
		mark_all = new MarkAllRead();
		mark_all.execute();
	}

	private class MarkAllRead extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			if (values == null) {
				values = new ContentValues();
				values.put(DbStructure.MessageTable.COLUMN_STATE,
						Constants.MSG_STATE.READ);
			}
			getDbHelper().getDb().update(
					DbStructure.MessageTable.TABLE_NAME,
					values,
					" sender=? and state IN (?,?)",
					new String[] { String.valueOf(receiver_id),
							String.valueOf(Constants.MSG_STATE.RECEIVED),
							String.valueOf(Constants.MSG_STATE.ACK_SEND) });
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			updateCursor();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		list = (ListView) findViewById(R.id.listViewChats);
		msg = (EditText) findViewById(R.id.editTextChats);
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		Intent intent = getIntent();
		// title="tambu";
		// title = intent.getStringExtra(Constants.INTENT_EXTRA.CHAT_NAME);
		receiver_id = intent.getLongExtra(Constants.INTENT_EXTRA.CHAT_USER_PK,
				-1);
		// if receiver_id==-1 go back to previous activity
		if (receiver_id != -1) {
			Utility.log(TAG, "we have user pk");
			String tmpq = "select _id,login_id,pic_url,f_name,l_name from user where login_id=? or _id=?";
			c = getDbHelper().getDb().rawQuery(
					tmpq,
					new String[] {
							getSPreferences().getString(
									Constants.PREF_KEYS.user_id, null),
							String.valueOf(receiver_id) });
			c.moveToFirst();
			while (!c.isAfterLast()) {
				if (c.getLong(0) == receiver_id) {
					receiver_lid = c.getString(1);
					user_image_url = c.getString(2);
					title = c.getString(3) + Constants.SPACE + c.getString(4);
				} else {
					sender_id = c.getLong(0);
				}
				c.moveToNext();
			}
			c.close();

		} else {
			Utility.log(TAG, "we have user login id");
			receiver_lid = intent
					.getStringExtra(Constants.INTENT_EXTRA.USER_NAME_TO_CHAT);
			String tmpq = "select _id,login_id,pic_url,f_name,l_name from user where login_id IN (?,?)";
			c = getDbHelper().getDb().rawQuery(
					tmpq,
					new String[] {
							getSPreferences().getString(
									Constants.PREF_KEYS.user_id, null),
							receiver_lid });
			c.moveToFirst();
			while (!c.isAfterLast()) {
				if (c.getString(1).equalsIgnoreCase(receiver_lid)) {
					// receiver_lid = c.getString(1);
					receiver_id = c.getLong(0);
					user_image_url = c.getString(2);
					title = c.getString(3) + Constants.SPACE + c.getString(4);
				} else {
					sender_id = c.getLong(0);
				}
				c.moveToNext();
			}
			c.close();
		}

		String[] args = { String.valueOf(receiver_id),
				String.valueOf(receiver_id) };
		c = getDbHelper().getDb().rawQuery(query, args);
		adapter = new MessagesAdapter(getApplicationContext(), c, 0);
		list.setAdapter(adapter);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		/*
		 * Intent intent = new Intent(this, BackgroundService.class);
		 * bindService(intent, service_connection, Context.BIND_AUTO_CREATE);
		 */
	}

	@Override
	protected void onStop() {
		super.onStop();
		/*
		 * if (binded) { unbindService(service_connection); binded = false; }
		 */
	}

	@Override
	public void onResume() {
		super.onResume();
		if (title != null)
			getSupportActionBar().setTitle(title);
		/**
		 * register local broadcast to receive refresh pings
		 */
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(
						local_broadcast_receiver,
						new IntentFilter(
								Constants.LOCAL_INTENT_ACTION.RELOAD_MESSAGES));
		updateCursor();
		markAllRead();
		Utility.CancelNotification(getApplicationContext(),
				Constants.MSG_NOTI_ID);
	}

	@Override
	protected void onPause() {
		/**
		 * unregister local broadcast to stop refresh pings
		 */
		LocalBroadcastManager.getInstance(getApplicationContext())
				.unregisterReceiver(local_broadcast_receiver);
		super.onPause();
	}

	public void updateCursor() {
		// new UpdateCursor().execute();
		String[] args = { String.valueOf(receiver_id),
				String.valueOf(receiver_id) };
		Cursor old_cursor = adapter.swapCursor(getDbHelper().getDb().rawQuery(
				query, args));
		if (old_cursor != null)
			old_cursor.close();
	}
/*
	private class UpdateCursor extends AsyncTask<Void, Void, Cursor> {
		@Override
		protected Cursor doInBackground(Void... params) {
			String[] args = { String.valueOf(receiver_id),
					String.valueOf(receiver_id) };
			return getDbHelper().getDb().rawQuery(query, args);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			Cursor old_cursor = adapter.swapCursor(result);
			if (old_cursor != null)
				old_cursor.close();

		}
	}
*/
	/**
	 * insert new message into database move the sending part to service
	 * 
	 * @param view
	 */
	public void sendNewMessage(View view) {
		String msgtxt = msg.getText().toString();
		msg.setText("");
		if (msgtxt.trim().length() > 0) {

			getDbHelper().insertNewMessage(
					new Message(sender_id, receiver_id, msgtxt, Calendar
							.getInstance().getTimeInMillis()));
			updateCursor();
		}
		/*
		 * if (binded && Utility.isConnected(getApplicationContext())) { if
		 * (!BackgroundService.isServiceRunning()) { service.sync(); } else { //
		 * start the service after 10 sec Utility.log(TAG,
		 * "set to start after 10 sec");
		 * Utility.setAlarm(getApplicationContext(), 10000); } }
		 */
	}

	@Override
	protected void onDestroy() {
		if (c != null)
			c.close();
		Utility.log(TAG, "closed cursor");
		super.onDestroy();
	}

	private SharedPreferences getSPreferences() {
		if (spref == null)
			spref = getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spref;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getApplicationContext());
		return dbh;
	}
	/*
	 * private ServiceConnection service_connection = new ServiceConnection() {
	 * 
	 * @Override public void onServiceDisconnected(ComponentName name) { binded
	 * = false; }
	 * 
	 * @Override public void onServiceConnected(ComponentName name, IBinder
	 * service_) { LocalBinder binder_ = (LocalBinder) service_; service =
	 * binder_.getService(); binded = true; } };
	 */
}
