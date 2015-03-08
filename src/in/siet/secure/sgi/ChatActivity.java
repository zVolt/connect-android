package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.MessagesAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ChatActivity extends ActionBarActivity {
	String title;
	ListView list;
	EditText msg;
	private Cursor c;
	MessagesAdapter adapter;
	SharedPreferences spref;
	static int receiver_id; // this is id of the receiver to whom user will text
	static String receiver_lid, user_image_url;
	static int sender_id; // sender is the user itself who is using the app
	static String sender_lid;
	private static SQLiteDatabase db;
	long msg_id;
	private static final String TAG = "in.siet.secure.sgi.ChatActivity";
	Toolbar toolbar;
	static final String query = "select messages._id,text,time,state from messages where sender=? or receiver=?";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// if(savedInstanceState==null)
		Intent intent = getIntent();
		title = intent.getStringExtra("name");
		receiver_id = intent.getIntExtra("user_id", -1);
		Utility.RaiseToast(getApplicationContext(), "onCreate null " + title,
				false);
		db = new DbHelper(getApplicationContext()).getWritableDatabase();
		spref = getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE);
		String tmpq = "select _id,login_id,pic_url from user where login_id='"
				+ (sender_lid = spref.getString(
						Constants.PreferenceKeys.user_id, null)) + "' or _id="
				+ receiver_id;
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

		list = (ListView) findViewById(R.id.listViewChats);
		msg = (EditText) findViewById(R.id.editTextChats);
		String[] args = { "" + receiver_id, "" + receiver_id };
		c = db.rawQuery(query, args);
		adapter = new MessagesAdapter(getApplicationContext(), c, 0);
		list.setAdapter(adapter);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Utility.RaiseToast(getApplicationContext(), "onCreate " + title, false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();

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
			fetchMessages();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();	
		if (title != null)
			getSupportActionBar().setTitle(title);
		Utility.RaiseToast(getApplicationContext(), "resume " + receiver_id
				+ " " + sender_id, false);
		// Utility.log(TAG,"count is "+adapter.getCount());
		// list.smoothScrollToPosition(adapter.getCount()); //to show latest
		// messages
	}

	public void updateCursor() {
		Cursor cc;
		String[] args = { "" + receiver_id, "" + receiver_id };
		cc = db.rawQuery(query, args);
		adapter.changeCursor(cc);
	}

	public void fetchMessages() {
		RequestParams params = new RequestParams();
		params.put(Constants.QueryParameters.USERNAME, sender_lid);
		params.put(Constants.QueryParameters.TOKEN,
				spref.getString(Constants.PreferenceKeys.token, null));

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Utility.BASE_URL + "query/download_messages", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						Utility.log(TAG, response.toString());
						// insert into db
						if (response.length() > 0) {
							new DbHelper(getApplicationContext()).fillMessages(
									response, sender_id);
							// send server msg to change state of messages
							updateCursor();
							int len = response.length();
							JSONArray ack = new JSONArray();
							for (int i = 0; i < len; i++) {
								try {
									ack.put(((JSONObject) response.get(i))
											.getInt(Constants.JSONMEssageKeys.ID));
								} catch (JSONException e) {
									Utility.log(TAG, "" + e.getMessage());
								}
							}
							sendAckForMessages(ack);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Utility.log(TAG, "" + errorResponse);
					}

					// override all failure methods
				});
	}

	public void sendAckForMessages(JSONArray ids) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put(Constants.QueryParameters.USERNAME, sender_lid);
		params.put(Constants.QueryParameters.TOKEN,
				spref.getString(Constants.PreferenceKeys.token, null));
		params.put(Constants.QueryParameters.MSGIDS, ids);
		client.get(Utility.BASE_URL + "query/receive_ack", params,
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

	public void sendMessage(View view) {
		String msgtxt = msg.getText().toString();
		if (msgtxt.trim().length() > 0) {
			SQLiteDatabase db = new DbHelper(getApplicationContext())
					.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(DbStructure.MessageTable.COLUMN_TEXT, msgtxt);
			values.put(DbStructure.MessageTable.COLUMN_TIME, Calendar
					.getInstance().getTimeInMillis());// (String)DateUtils.getRelativeDateTimeString(getApplicationContext(),
														// cal.getTimeInMillis(),
														// DateUtils.SECOND_IN_MILLIS,
														// DateUtils.WEEK_IN_MILLIS,
														// 0));
			values.put(DbStructure.MessageTable.COLUMN_SENDER, sender_id);
			values.put(DbStructure.MessageTable.COLUMN_RECEIVER, receiver_id);
			values.put(DbStructure.MessageTable.COLUMN_STATE,
					Constants.MsgState.TO_SEND);
			values.put(DbStructure.MessageTable.COLUMN_IS_GRP_MSG, "false");

			msg_id = db.insert(DbStructure.MessageTable.TABLE_NAME, null,
					values);

			// Utility.RaiseToast(getApplicationContext(), "send", false);
			// ((LinearLayout)(view.getParent().getParent()));
			updateCursor();
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
			msg.setText("");
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(Utility.BASE_URL + "query/upload_message", params,
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
