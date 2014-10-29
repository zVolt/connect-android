package in.siet.secure.sgi;

import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
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
			getSupportFragmentManager().beginTransaction()
					.add(R.id.loginFrame, new FragmentSignin()).commit();
		}
		Log.d(TAG+" onCreate"," at End");
		fill_tmp_data();
	}
	public void onClickButtonSignin(View view){
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
			Utility.RaiseToast(getApplicationContext(),"Input not correct! Retry",1);
		}
	}
	public void fill_tmp_data(){
		DbHelper dbHelper=new DbHelper(getApplicationContext());
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put(DbStructure.FcultyContactsTable._ID,1);
		values.put(DbStructure.FcultyContactsTable.COLUMN_FNAME, "pogo");
		values.put(DbStructure.FcultyContactsTable.COLUMN_LNAME, "gopo");
		db.insert(DbStructure.FcultyContactsTable.TABLE_NAME,null, values);

		Utility.RaiseToast(getApplicationContext(), "inserted value", 1);
		db=dbHelper.getReadableDatabase();
		String[] projection={
				DbStructure.FcultyContactsTable._ID,
				DbStructure.FcultyContactsTable.COLUMN_FNAME,
				DbStructure.FcultyContactsTable.COLUMN_LNAME,
		};
		Cursor c=db.query(DbStructure.FcultyContactsTable.TABLE_NAME,projection, null,null,null,null,null);
		c.moveToFirst();
		Utility.RaiseToast(getApplicationContext(), c.getString(c.getColumnIndexOrThrow(DbStructure.FcultyContactsTable.COLUMN_FNAME)), 1);
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
		new DbHelper(getApplicationContext()).getReadableDatabase();
		Utility.log(TAG,"donedb");
	}
	public void saveUser(){
		SharedPreferences sharedPref= getApplicationContext().getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE);
		String saved_user_id=sharedPref.getString("User Id", null);
		String saved_password=sharedPref.getString("Password", null);
		if (saved_user_id==null || saved_password!=null){
			Editor editor=sharedPref.edit();
			editor.putString(getString(R.string.user_id),userid);
			editor.putString(getString(R.string.acess_token),Base64.encodeToString(Utility.sha1(pwd).getBytes(),Base64.DEFAULT));
			editor.putBoolean(getString(R.string.logged_in), true);
			editor.putBoolean(getString(R.string.is_faculty),is_faculty);
			editor.commit();
		}
		createdb();
	}
	public void invokeWS(RequestParams params){
		Log.d(TAG+" invokeWS"," at start");
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Constants.SOCKET+"/SGI_webservice/login/dologin",params ,new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode,Header[] headers,JSONObject response){ 
					Log.d(TAG+" onSucess"," at start");
					try {
						Utility.hideProgressDialog();
						if(response.getString("tag").equalsIgnoreCase("login") && response.getBoolean("status")){
							Toast.makeText(getApplicationContext(), "Login Sucessful", Toast.LENGTH_LONG).show();
							saveUser();
							startMainActivity();
						}
						else{
							Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
							((TextView)findViewById(R.id.textView_error_msg)).setText("Login Failed");
							
						}
					} catch (JSONException e) {
						Utility.log(TAG+" invokeWS exception ",e.getLocalizedMessage());
						((TextView)findViewById(R.id.textView_error_msg)).setText("Try Again!!");
					}
				}
				
				@Override
				public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONObject errorResponse){
					Utility.hideProgressDialog();
					Utility.RaiseToast(getApplicationContext(), "Unable to connect", 1);
					Utility.log(TAG+" onFailure"," at start");
				}
			
			
		});
		Utility.log(TAG+" invokeWS"," at end");
	}
	
}
