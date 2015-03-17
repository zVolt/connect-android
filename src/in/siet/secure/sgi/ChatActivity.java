package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.MessagesAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
	int receiver_id; // this is id of the receiver to whom user will text
	String receiver_lid, user_image_url;
	int sender_id; // sender is the user itself who is using the app
	String sender_lid;

	long msg_id;
	private static final String TAG = "in.siet.secure.sgi.ChatActivity";
	Toolbar toolbar;
	private static final String query = "select messages._id,text,time,state from messages where sender=? or receiver=?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		title = intent.getStringExtra("name");
		receiver_id = intent.getIntExtra("user_pk_id", -1);
		spref = getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE);
		list = (ListView) findViewById(R.id.listViewChats);
		msg = (EditText) findViewById(R.id.editTextChats);
		toolbar = (Toolbar) findViewById(R.id.toolbar);

		String tmpq = "select _id,login_id,pic_url from user where login_id='"
				+ (sender_lid = spref.getString(
						Constants.PREF_KEYS.user_id, null)) + "' or _id="
				+ receiver_id; // me or the receiver

		SQLiteDatabase db = new DbHelper(getApplicationContext()).getDb();
		c = db.rawQuery(tmpq, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			if (c.getInt(0) == receiver_id) {
				receiver_lid = c.getString(1);
				user_image_url = c.getString(2);
			} else
				sender_id = c.getInt(0);
			c.moveToNext();
		}
		c.close();

		String[] args = { String.valueOf(receiver_id),
				String.valueOf(receiver_id) };
		c = db.rawQuery(query, args);
		adapter = new MessagesAdapter(getApplicationContext(), c, 0);
		list.setAdapter(adapter);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		Utility.setAlarm(getApplication(), 5000);
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
		case R.id.action_refresh_messages:
			//you cannot refresh the list 
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (title != null)
			getSupportActionBar().setTitle(title);
	}

	public void updateCursor() {
		Cursor new_cursor;
		String[] args = { "" + receiver_id, "" + receiver_id };

		new_cursor = new DbHelper(getApplicationContext()).getDb().rawQuery(
				query, args);
		Cursor old_cursor = adapter.swapCursor(new_cursor);
		if (old_cursor != null)
			old_cursor.close();
	}


	/**
	 * insert new message into database
	 * move the sending part to service
	 * 
	 * @param view
	 */
	public void insertMessageToDb(View view) {
		String msgtxt = msg.getText().toString();
		msg.setText("");
		if (msgtxt.trim().length() > 0) {
			SQLiteDatabase db = new DbHelper(getApplicationContext())
					.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(DbStructure.MessageTable.COLUMN_TEXT, msgtxt);
			values.put(DbStructure.MessageTable.COLUMN_TIME, Calendar
					.getInstance().getTimeInMillis());
			values.put(DbStructure.MessageTable.COLUMN_SENDER, sender_id);
			values.put(DbStructure.MessageTable.COLUMN_RECEIVER, receiver_id);
			values.put(DbStructure.MessageTable.COLUMN_STATE,
					Constants.STATE.PENDING);
			values.put(DbStructure.MessageTable.COLUMN_IS_GRP_MSG, Constants.IS_GROUP_MSG.NO);

			msg_id = db.insert(DbStructure.MessageTable.TABLE_NAME, null,
					values);
			updateCursor();
			
			/*
			 // send to server
			RequestParams params = new RequestParams();
			Utility.putCredentials(params, spref);
			JSONObject mobj = new JSONObject();
			try {
				mobj.put(Constants.JSONMEssageKeys.SENDER, sender_lid);
				mobj.put(Constants.JSONMEssageKeys.TEXT, msgtxt);
				mobj.put(Constants.JSONMEssageKeys.TIME, Calendar.getInstance()
						.getTimeInMillis());
				mobj.put(Constants.JSONMEssageKeys.RECEIVER, receiver_lid);
			} catch (JSONException e) {
				Utility.log(TAG, "" + e.getMessage());
			}
			params.put(Constants.QueryParameters.MESSAGES, mobj);
			
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(Utility.getBaseURL() + "query/upload_message", params,
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							Utility.log(TAG, response.toString());
							// if status is false then reset message state to
							// send again id in msg_id
							// update messages
							try {
								if (response
										.getBoolean(Constants.JSONKeys.STATUS)) {
									ContentValues values = new ContentValues();
									values.put(
											DbStructure.MessageTable.COLUMN_STATE,
											Constants.MsgState.SENT_SUCESSFULLY);
									new DbHelper(getApplicationContext())
											.getWritableDatabase()
											.update(DbStructure.MessageTable.TABLE_NAME,
													values,
													"_id=?",
													new String[] { msg_id + "" });
								}
							} catch (Exception e) {
								Utility.log(TAG, "" + e.getMessage());
							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							// set status of message to not sent or to be send
						}

					});
			 */
		}
	}

	@Override
	protected void onDestroy() {
		if (c != null)
			c.close();
		Utility.log(TAG, "closed cursor");

		super.onDestroy();
	}
}
