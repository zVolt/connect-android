package in.siet.secure.sgi;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class UserListActivity extends ActionBarActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null){
			setContentView(R.layout.activity_chat);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.chatfragmentFrame,new FragmentUserList()).commit();
		}
	}
}
