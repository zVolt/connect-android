package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
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
import android.widget.SimpleCursorAdapter;

public class ChatActivity extends ActionBarActivity{
	String title;
	ListView list;
	EditText msg;
	Cursor  c;
	SimpleCursorAdapter adapter;
	static final String query="select messages._id,text,time from messages join user on sender=user._id where sender=? or user.login_id=?)";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		if(savedInstanceState==null){
			list=(ListView)findViewById(R.id.listViewChats);
			msg=(EditText)findViewById(R.id.editTextChats);
			Intent intent=getIntent();
			title=intent.getStringExtra("user_name");
			String[] args={
					""+intent.getIntExtra("user_id",-1),
					getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE).getString(getString(R.string.user_id), "")
			};
			c=new DbHelper(getApplicationContext()).getReadableDatabase().rawQuery(query, args);
			String from[]={
				DbStructure.MessageTable.COLUMN_TEXT,
				DbStructure.MessageTable.COLUMN_TIME,
			};
			int to[]={
				R.id.textViewMessagesText,
				R.id.textViewMessagesTime
			};
			adapter=new SimpleCursorAdapter(getApplicationContext(), R.layout.list_item_chats, c, from, to, 0);
			list.setAdapter(adapter);
			Utility.RaiseToast(getApplicationContext(), ""+intent.getIntExtra("user_id",-1), false);
		}
		
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(title!=null)
			setTitle(title);
	}
	public void sendMessage(View view){
		SQLiteDatabase db=new DbHelper(getApplicationContext()).getWritableDatabase();
		ContentValues values=new ContentValues();
		Calendar cal=Calendar.getInstance();
		values.put(DbStructure.MessageTable.COLUMN_TEXT,msg.getText().toString());
		values.put(DbStructure.MessageTable.COLUMN_TIME,cal.getTime().toString());
		//values.put(DbStructure.MessageTable.COLUMN_SENDER,);
		db.insert(DbStructure.MessageTable.TABLE_NAME, null, values);
		msg.setText("");
		Utility.RaiseToast(getApplicationContext(), "send", false);
		//((LinearLayout)(view.getParent().getParent()));
	}
}
