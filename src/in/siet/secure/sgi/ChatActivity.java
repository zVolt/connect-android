package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class ChatActivity extends ActionBarActivity{
	String title;
	ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		if(savedInstanceState==null){
			list=(ListView)findViewById(R.id.listViewChats);
		}
		Intent intent=getIntent();
		title=intent.getStringExtra("user_name");
		Utility.RaiseToast(getApplicationContext(), ""+intent.getIntExtra("user_id",-1), false);
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
}
