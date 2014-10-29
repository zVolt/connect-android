package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
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
	private static boolean back_pressed=false;
	private static boolean in_settings=false;
	private static boolean is_faculty=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences spref=getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE);
		if(spref.getBoolean(getString(R.string.logged_in), false)){
			startMainActivity();
		}
		setContentView(R.layout.activity_login);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
				.setTransitionStyle(R.anim.abc_fade_out)
				.add(R.id.loginFrame, new FragmentSignin())
				.commit();
		}
		Log.d(TAG+" onCreate"," at End");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		//super.onCreateOptionsMenu(menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		//handle drawer open/close clicks
		back_pressed=false;
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Fragment fragment=new FragmentSettings();
			getFragmentManager().beginTransaction()
			.replace(R.id.loginFrame, fragment)
			.addToBackStack(null)
			.commit();
			Utility.log(TAG,"changing frame");
			in_settings=true;
			return true;
		}
		else if(id==R.id.action_about){
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed(){
		if(in_settings){
			getFragmentManager().popBackStack();
			in_settings=false;
		}
		else{
			if(!back_pressed){
				back_pressed=true;
				Utility.RaiseToast(getApplicationContext(), getString(R.string.exit_warning), true);
			}
			else{
				super.onBackPressed();
			}
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		back_pressed=false;
	}
	public void onClickButtonSignin(View view){
		back_pressed=false;
		Utility.showProgressDialog(this);
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		Log.d(TAG+" onClick"," at start");
		userid=((EditText)findViewById(R.id.editText_userid)).getText().toString().trim();
		pwd=((EditText)findViewById(R.id.editText_userpassword)).getText().toString().trim();
		if(verifyInput()){
			
			if(((RadioButton)findViewById(R.id.radioButton_faculty)).isChecked())
				is_faculty=true;
			else
				is_faculty=false;
			RequestParams params =new RequestParams();
			params.put(getString(R.string.web_prm_usr),Base64.encodeToString(userid.getBytes(),Base64.DEFAULT));
			params.put(getString(R.string.web_prm_pwd),Base64.encodeToString(Utility.sha1(pwd).getBytes(),Base64.DEFAULT));
			params.put(getString(R.string.web_prm_isfac),is_faculty);
			Log.d("username",Base64.encodeToString(userid.getBytes(),Base64.DEFAULT));
			Log.d("Password",Base64.encodeToString(pwd.getBytes(),Base64.DEFAULT));
			invokeWS(params);
			Log.d(TAG+" onClick"," at end");
		}
		else{
			clearInput();
			Utility.hideProgressDialog();
			Utility.RaiseToast(getApplicationContext(),"Input not correct! Retry",true);
		}
	}
	public void startMainActivity(){
		Intent intent=new Intent(this,MainActivity.class);
		Utility.log(TAG,"stating new Activity");
		startActivity(intent);
		finish();
	}
	public boolean verifyInput(){
		if(userid!=null && userid.length()>0 && pwd!=null && pwd.length()>0 && (((RadioButton)findViewById(R.id.radioButton_student)).isChecked() || ((RadioButton)findViewById(R.id.radioButton_faculty)).isChecked()))
			return true;
		return false;
	}
	public void clearInput(){
		((TextView)findViewById(R.id.editText_userpassword)).setText(null);
	}
	public void createdb(){
		new DbHelper(getApplicationContext());
		Utility.log(TAG,"donedb");
	}
	public void saveUser(String token){
		SharedPreferences sharedPref= getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE);
		String saved_user_id=sharedPref.getString("User Id", null);
		String saved_password=sharedPref.getString("Password", null);
		if (saved_user_id==null || saved_password!=null){
			Editor editor=sharedPref.edit();
			editor.putString(getString(R.string.user_id),userid);
			editor.putString(getString(R.string.acess_token),token);
			editor.putBoolean(getString(R.string.logged_in), true);
			editor.putBoolean(getString(R.string.is_faculty),is_faculty);
			editor.commit();
		}
		createdb();
	}
	public void invokeWS(RequestParams params){
		Log.d(TAG+" invokeWS"," at start");
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Constants.SERVER+Constants.COLON+Constants.PORT+"/SGI_webservice/login/dologin",params ,new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode,Header[] headers,JSONObject response){ 
					Log.d(TAG+" onSucess"," at start");
					try {
						Utility.hideProgressDialog();
						if(response.getString("tag").equalsIgnoreCase("login") && response.getBoolean("status")){
						//	Toast.makeText(getApplicationContext(), "Login Sucessful", Toast.LENGTH_LONG).show();
							saveUser(response.getString("token"));
							startMainActivity();
						}
						else{
							Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
							
							
						}
					} catch (JSONException e) {
						Utility.log(TAG+" invokeWS exception ",e.getLocalizedMessage());
						
					}
				}
				
				@Override
				public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONObject errorResponse){
					
					Utility.hideProgressDialog();
					Utility.RaiseToast(getApplicationContext(), "Error Connectiong server", true);
					Utility.log(TAG+" onFailure"," at start"+throwable.getMessage());
				}
			
			
		});
		Utility.log(TAG+" invokeWS"," at end");
	}
	
}
