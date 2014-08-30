package in.siet.secure.sgi;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private static String TAG="in.siet.secure.sgi.MainActivity";
	Clientapi capi=new Clientapi();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragmentFrame, new FragmentSignin()).commit();
		}
		
	}
	public void onClickButtonSignin(View view){
		Toast.makeText(this, "Signin", Toast.LENGTH_SHORT).show();
		//authenticate here 
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, new FragmentMainDisplay());
		ft.addToBackStack(TAG+"Login");
		ft.commit();
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
		return super.onOptionsItemSelected(item);
	}

	public void sendMsg(View view){
		String msg=((TextView)findViewById(R.id.userMsg)).getText().toString();
		Toast.makeText(view.getContext(), "Message Sending", Toast.LENGTH_SHORT).show();
		if(capi.sendMessage(msg))
			Toast.makeText(view.getContext(), "Message Send", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(view.getContext(), "Message Not Send", Toast.LENGTH_SHORT).show();
	}
	public void getMsg(View view){
		TextView textview=(TextView)findViewById(R.id.textViewDisplayMsg);
		String tmp=capi.getMessages();
		textview.setText(textview.getText().toString()+"\n"+tmp);
	}

}
