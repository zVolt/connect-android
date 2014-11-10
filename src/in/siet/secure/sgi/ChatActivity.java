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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends ActionBarActivity{
	String title;
	ListView list;
	EditText msg;
	Calendar cal=Calendar.getInstance();
	private Cursor  c;
	MessagesAdapter adapter;
	static int user_id; //this is id of the receiver to whom user will text
	static int sender_id; //sender is the user itself who is using the app
	private static SQLiteDatabase db;
	static final String query="select messages._id,text,time from messages where sender=? or receiver=?";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		if(savedInstanceState==null){
			db=new DbHelper(getApplicationContext()).getWritableDatabase();
			String tmpq="select _id from user where login_id='"+getSharedPreferences(Constants.pref_file_name,Context.MODE_PRIVATE).getString(Constants.PreferenceKeys.user_id, "")+"'";
			c=db.rawQuery(tmpq, null);
			c.moveToFirst();
			if(!c.isAfterLast())
				sender_id=c.getInt(0);
			list=(ListView)findViewById(R.id.listViewChats);
			msg=(EditText)findViewById(R.id.editTextChats);
			Intent intent=getIntent();
			title=intent.getStringExtra("name");
			user_id=intent.getIntExtra("user_id",-1);
			String[] args={
					""+user_id,
					""+user_id
			};
			c=db.rawQuery(query, args);
	/*		
			String from[]={
			//	DbStructure.MessageTable.COLUMN_SENDER,
			//	DbStructure.MessageTable.COLUMN_STATE,
			//	DbStructure.MessageTable.COLUMN_IS_GRP_MSG,
				DbStructure.MessageTable.COLUMN_TEXT,
				DbStructure.MessageTable.COLUMN_TIME,
			};
			int to[]={
				R.id.textViewMessagesText,
				R.id.textViewMessagesTime
			};*/
			adapter=new MessagesAdapter(getApplicationContext(),c,0);
			list.setAdapter(adapter);
			
			Utility.RaiseToast(getApplicationContext(), "onCreate null "+title, false);
		}
		else
			Utility.RaiseToast(getApplicationContext(), "onCreate", false);
		
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(title!=null)
			getActionBar().setTitle(title);
		
		Utility.RaiseToast(getApplicationContext(), "resume "+user_id+" "+sender_id, false);
	}
	
	public void sendMessage(View view){
		SQLiteDatabase db=new DbHelper(getApplicationContext()).getWritableDatabase();
		ContentValues values=new ContentValues();
		
		values.put(DbStructure.MessageTable.COLUMN_TEXT,msg.getText().toString());
		values.put(DbStructure.MessageTable.COLUMN_TIME,cal.getTimeInMillis());//(String)DateUtils.getRelativeDateTimeString(getApplicationContext(), cal.getTimeInMillis(),  DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0));
		values.put(DbStructure.MessageTable.COLUMN_SENDER,sender_id);
		values.put(DbStructure.MessageTable.COLUMN_RECEIVER,user_id);
		values.put(DbStructure.MessageTable.COLUMN_STATE,Constants.MsgState.TO_SEND);
		values.put(DbStructure.MessageTable.COLUMN_IS_GRP_MSG,"false");
		
		db.insert(DbStructure.MessageTable.TABLE_NAME, null, values);
		msg.setText("");
	//	Utility.RaiseToast(getApplicationContext(), "send", false);
		//((LinearLayout)(view.getParent().getParent()));
		
	}
}
