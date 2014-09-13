package in.siet.secure.sgi;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends ActionBarActivity {
	//ProgressDialog progDialog;
	private static String TAG="in.siet.secure.sgi.MainActivity";
	private static String userid=null;
	Clientapi capi=new Clientapi();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragmentFrame, new FragmentSignin()).commit();
		}
	//	progDialog=new ProgressDialog(getApplicationContext());
		Log.d(TAG+" onCreate"," at End");
	}
	public void onClickButtonSignin(View view){
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		Log.d(TAG+" onClick"," at start");
	//	progDialog.show();
		userid=((EditText)findViewById(R.id.editText_userid)).getText().toString();
		String pwd=((EditText)findViewById(R.id.editText_userpassword)).getText().toString();
		RequestParams params =new RequestParams();
		params.put("username", userid);
		params.put("password", pwd);
		invokeWS(params);
		Log.d(TAG+" onClick"," at end");
/*	//	Toast.makeText(this, "Signin", Toast.LENGTH_SHORT).show();
		//authenticate here 
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, new FragmentMainDisplay());
		ft.addToBackStack(TAG+"Login");
		ft.commit();
*/	}
	public void startUserListActivity(){
		Intent intent=new Intent(this,UserListActivity.class);
		intent.putExtra("UserId", userid);
		Log.d(TAG,"stating new Activity");
		startActivity(intent);
	}
	public void invokeWS(RequestParams params){
		Log.d(TAG+" invokeWS"," at start");
		AsyncHttpClient client = new AsyncHttpClient();
		
		client.get("http://172.16.7.200:8080/SGI_webservice/login/dologin",params ,new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode,Header[] headers,JSONObject response){
					Log.d(TAG+" onSucess"," at start");
					try {
						if(response.getString("tag").equalsIgnoreCase("login") && response.getBoolean("status")){
							Toast.makeText(getApplicationContext(), "Login Sucessful", Toast.LENGTH_LONG).show();
							startUserListActivity();
						/*	FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, new FragmentMainDisplay());
							ft.addToBackStack(TAG+"Login");
							ft.commit();*/
						}
						else{
							
							Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
							((TextView)findViewById(R.id.textView_error_msg)).setText("Login Failed");
							
						}
					} catch (JSONException e) {
						Log.d(TAG+" invokeWS exception ",e.getLocalizedMessage());
						((TextView)findViewById(R.id.textView_error_msg)).setText("Try Again!!");
					}
				}
				
				@Override
				public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONObject errorResponse){
					Log.d(TAG+" onFailure"," at start");
				}
			
			
		});
		Log.d(TAG+" invokeWS"," at end");
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
