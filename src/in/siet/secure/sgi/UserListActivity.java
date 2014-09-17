package in.siet.secure.sgi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class UserListActivity extends ActionBarActivity{
	public static final String TAG="in.siet.secure.sgi.UserListActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null){
			setContentView(R.layout.activity_chat);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.chatfragmentFrame,new FragmentUserList()).commit();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == R.id.action_logout) {
			getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE).edit().clear().commit();
			Log.d(TAG,"pref cleared");
			startLoginActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void startLoginActivity(){
		Intent intent=new Intent(this,MainActivity.class);
		Log.d(TAG,"stating login Activity");
		startActivity(intent);
		finish();
	}
}
