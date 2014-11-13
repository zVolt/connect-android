package in.siet.secure.sgi;

import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
	private static String f_name=null;
	private static String l_name=null;
	private static String profile_url=null;
	private static String branch=null;
	private static String section=null;
	private static String year=null;
	private static boolean back_pressed=false;
	private static boolean in_settings=false;
	private static boolean is_faculty=false;
	private SharedPreferences spref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spref=getApplicationContext().getSharedPreferences(Constants.pref_file_name,Context.MODE_PRIVATE);

		if(spref.getBoolean(Constants.PreferenceKeys.logged_in, false)){
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
		if(Utility.isConnected(getApplicationContext())){
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
				params.put(Constants.QueryParameters.USERNAME,Utility.encode(userid));
				params.put(Constants.QueryParameters.PASSWORD,Utility.encode(Utility.sha1(pwd)));
				params.put(Constants.QueryParameters.IS_FACULTY,is_faculty);
				
				Log.d("username",Utility.encode(userid));
				Log.d("Password",Utility.encode(Utility.sha1(pwd)));
				queryServer(params,true);
				Log.d(TAG+" onClick"," at end");
			}
			else{
				clearInput();
				Utility.hideProgressDialog();
				Utility.RaiseToast(getApplicationContext(),"Input not correct! Retry",true);
			}
		}else{
			Utility.RaiseToast(getApplicationContext(), getString(R.string.no_internet), true);
		}
	}

	public void startMainActivity(){
		Intent intent=new Intent(this,MainActivity.class);
		Utility.log(TAG,"stating new Activity");
		intent.putExtra(Constants.PreferenceKeys.is_faculty,is_faculty);
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
		//get data from server
		RequestParams params =new RequestParams();
		params.put(Constants.QueryParameters.USERNAME, Utility.encode(spref.getString(Constants.PreferenceKeys.user_id, null)));
		params.put(Constants.QueryParameters.TOKEN, Utility.encode(spref.getString(Constants.PreferenceKeys.token, null)));
		queryServer(params, false);
	//	new DbHelper(getApplicationContext());
	//	Utility.log(TAG,"donedb");
	}
	public void saveUser(String token){
		SharedPreferences sharedPref= getApplicationContext().getSharedPreferences(Constants.pref_file_name,Context.MODE_PRIVATE);
		String saved_user_id=sharedPref.getString(Constants.PreferenceKeys.user_id, null);
		String saved_token=sharedPref.getString(Constants.PreferenceKeys.token, null);
		if (saved_user_id==null || saved_token!=null){ // if detail doesnt exist
			Editor editor=sharedPref.edit();
			editor.putString(Constants.PreferenceKeys.user_id,userid);
			editor.putString(Constants.PreferenceKeys.token,token);
			editor.putString(Constants.PreferenceKeys.f_name,f_name);
			editor.putString(Constants.PreferenceKeys.l_name,l_name);
			editor.putString(Constants.PreferenceKeys.pic_url,profile_url);
			if(is_faculty)
				editor.putString(Constants.PreferenceKeys.branch,branch);
			else {
				editor.putString(Constants.PreferenceKeys.section,section);
				editor.putString(Constants.PreferenceKeys.year,year);
			}
			editor.putBoolean(Constants.PreferenceKeys.logged_in, true);
			editor.putBoolean(Constants.PreferenceKeys.is_faculty,is_faculty);
			editor.commit();
		}
		createdb();
	}
	
	public void queryServer(RequestParams params,boolean login){
		Log.d(TAG+" queryServer"," at start");
		AsyncHttpClient client = new AsyncHttpClient();
		if(login){
			client.get("http://"+Constants.SERVER+Constants.COLON+Constants.PORT+"/SGI_webservice/login/dologin",params ,new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode,Header[] headers,JSONObject response){ 
						Log.d(TAG+" onSucess"," at start");
						try {
						//	Utility.hideProgressDialog();
							
							if(response.getString(Constants.JSONKeys.TAG).equalsIgnoreCase(Constants.JSONKeys.TAG_MSGS.LOGIN) && response.getBoolean(Constants.JSONKeys.STATUS)){
							//	Toast.makeText(getApplicationContext(), "Login Sucessful", Toast.LENGTH_LONG).show();
								f_name=response.getString(Constants.JSONKeys.FIRST_NAME);
								l_name=response.getString(Constants.JSONKeys.LAST_NAME);
								profile_url=response.getString(Constants.JSONKeys.PROFILE_IMAGE);
								
								if(is_faculty)
									branch=response.getString(Constants.JSONKeys.BRANCH);
								else{
									section=response.getString(Constants.JSONKeys.SECTION);
									year=response.getString(Constants.JSONKeys.YEAR);
								}
								saveUser(response.getString(Constants.JSONKeys.TOKEN));
							//	startMainActivity(); //hide this line 
							}
							else{
								Utility.hideProgressDialog();
								Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							Utility.log(TAG+" queryServer exception ",e.getLocalizedMessage());
							
						}
					}
					
					@Override
					public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONObject errorResponse){
						
						Utility.hideProgressDialog();
						Utility.RaiseToast(getApplicationContext(), "Error Connectiong server", true);
						Utility.log(TAG+" onFailure"," at start"+throwable.getMessage());
					}
					
				
				
			});
		}
		else{
			client.get("http://"+Constants.SERVER+Constants.COLON+Constants.PORT+"/SGI_webservice/login/getInitial",params ,new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers,JSONArray response){
					Utility.log(TAG,"in on Sucess");
					try{
						JSONArray tmparry=response.getJSONArray(0);
						InitialData idata=new InitialData();
						int x=tmparry.length();
						InitialData.Courses c;
						for(int i=0;i<x;i++){
							c=new InitialData.Courses();
							JSONObject obj=tmparry.getJSONObject(i);
							c.id=obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
							c.duration=obj.getInt(DbStructure.COURSES.COLUMN_DURATION);
							c.name=obj.getString(DbStructure.COURSES.COLUMN_NAME);
							idata.courses.add(c);
						}
						tmparry=response.getJSONArray(1);
						x=tmparry.length();
						InitialData.Branches b;
						for(int i=0;i<x;i++){
							b=new InitialData.Branches();
							JSONObject obj=tmparry.getJSONObject(i);
							b.course_id=obj.getInt(DbStructure.BRANCHES.COLUMN_COURSE_ID);
							b.id=obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
							b.name=obj.getString(DbStructure.BRANCHES.COLUMN_NAME);
							idata.branches.add(b);
						}
						tmparry=response.getJSONArray(2);
						x=tmparry.length();
						InitialData.Sections s;
						for(int i=0;i<x;i++){
							s=new InitialData.Sections();
							JSONObject obj=tmparry.getJSONObject(i);
							s.year_id=obj.getInt(DbStructure.SECTIONS.COLUMN_YEAR_ID);
							s.id=obj.getInt(DbStructure.COLUMN_INCOMMING_ID); 
							s.name=obj.getString(DbStructure.SECTIONS.COLUMN_NAME);
							idata.sections.add(s);
						}
						tmparry=response.getJSONArray(3);
						x=tmparry.length();
						InitialData.Year y;
						for(int i=0;i<x;i++){
							y=new InitialData.Year();
							JSONObject obj=tmparry.getJSONObject(i);
							y.branch_id=obj.getInt(DbStructure.YEAR.COLUMN_BRANCH_ID);
							y.id=obj.getInt(DbStructure.COLUMN_INCOMMING_ID); 
							y.year=obj.getInt(DbStructure.YEAR.COLUMN_YEAR);
							idata.years.add(y);
						}
						Utility.log(TAG,"data building sucessfull");
					(new DbHelper(getApplicationContext())).addInitialData(idata,userid);
					startMainActivity();
					}catch(Exception e){
						Utility.log(TAG,e.getMessage());
						Utility.RaiseToast(getApplicationContext(), "Fail to process data. Try Again!", false);
					}
					finally{
						Utility.hideProgressDialog();

					}
				}
				
				@Override
				public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONArray errorResponse){
					Utility.RaiseToast(getApplicationContext(), "Fail to receive data. Try Again!", false);
					Utility.hideProgressDialog();
				}
			});
		}
		Utility.log(TAG+" queryServer"," at end");
	}
}
