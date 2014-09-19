package in.siet.secure.sgi;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends ActionBarActivity {
	//ProgressDialog progDialog;
	private static String TAG="in.siet.secure.sgi.LoginActivity"; 
	private static String userid=null;
	private static String pwd=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences spref=getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE);
		if(spref.getBoolean(getString(R.string.logged_in), false)){
			startUserListActivity();
			
		}
		setContentView(R.layout.activity_login);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.loginFrame, new FragmentSignin()).commit();
		}
	//	progDialog=new ProgressDialog(getApplicationContext());
		Log.d(TAG+" onCreate"," at End");
	}
	public void onClickButtonSignin(View view){
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		Log.d(TAG+" onClick"," at start");
	//	progDialog.show();
		userid=((EditText)findViewById(R.id.editText_userid)).getText().toString().trim();
		pwd=((EditText)findViewById(R.id.editText_userpassword)).getText().toString().trim();
		RequestParams params =new RequestParams();
		params.put(getString(R.string.web_prm_usr),Base64.encodeToString(userid.getBytes(),Base64.DEFAULT));
		params.put(getString(R.string.web_prm_pwd),Base64.encodeToString(pwd.getBytes(),Base64.DEFAULT));
		Log.d("username",Base64.encodeToString(userid.getBytes(),Base64.DEFAULT));
		Log.d("Password",Base64.encodeToString(pwd.getBytes(),Base64.DEFAULT));
		invokeWS(params);
		//function call
		Log.d(TAG+" onClick"," at end");
/*	//	Toast.makeText(this, "Signin", Toast.LENGTH_SHORT).show();
		//authenticate here 
		FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, new FragmentMainDisplay());
		ft.addToBackStack(TAG+"Login");
		ft.commit();
*/	}
	public void startUserListActivity(){
		Intent intent=new Intent(this,MainActivity.class);
		Log.d(TAG,"stating new Activity");
		startActivity(intent);
		finish();
	}
	public void saveUser(){
		SharedPreferences sharedPref= getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE);
		String saved_user_id=sharedPref.getString("User Id", null);
		String saved_password=sharedPref.getString("Password", null);
		if (saved_user_id==null || saved_password!=null){
			//save password
			Editor editor=sharedPref.edit();
			editor.putString(getString(R.string.user_id),userid);
			editor.putString(getString(R.string.password),pwd);
			editor.putBoolean(getString(R.string.logged_in), true);
			editor.commit();
		}
	}
	public void invokeWS(RequestParams params){
		Log.d(TAG+" invokeWS"," at start");
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://192.168.0.100:8080/SGI_webservice/login/dologin",params ,new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode,Header[] headers,JSONObject response){ 
					Log.d(TAG+" onSucess"," at start");
					try {
						if(response.getString("tag").equalsIgnoreCase("login") && response.getBoolean("status")){
							Toast.makeText(getApplicationContext(), "Login Sucessful", Toast.LENGTH_LONG).show();
							saveUser();
							startUserListActivity();
						/*	FragmentTransaction ft=getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, new FragmentMainDisplay());
							ft.addToBackStack(TAG+"Login");
							ft.commit();*/
						}
						else{
							//else mee aa jayga .. ok
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
					// fail me like n/w acess hi ni hai user k par to bas message lod kary ok..??
					Log.d(TAG+" onFailure"," at start");
				}
			
			
		});
		Log.d(TAG+" invokeWS"," at end");
	}
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/

}
