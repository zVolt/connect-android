package in.siet.secure.sgi;

import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.FacultyFull;
import in.siet.secure.Util.InitialData;
import in.siet.secure.Util.MyJsonHttpResponseHandler;
import in.siet.secure.Util.Student;
import in.siet.secure.Util.StudentFull;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;

public class LoginActivity extends ActionBarActivity {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static String TAG = "in.siet.secure.sgi.LoginActivity";
	private String user_id = null;
	private String pwd = null;
	private DbHelper dbh;
	private User user = null;
	private boolean back_pressed = false;
	private boolean in_settings = false;
	private boolean is_faculty = false;
	private SharedPreferences spref;
	Toolbar toolbar;
	Context context;
	/**
	 * GCM specific variables
	 */
	GoogleCloudMessaging gcm;
	String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (checkPlayServices()) {
			// spref = getApplicationContext().getSharedPreferences(
			// Constants.pref_file_name, Context.MODE_PRIVATE);
			context = getApplicationContext();
			/**
			 * register for gcm if not already registered
			 */
			gcm = GoogleCloudMessaging.getInstance(this);
			/*
			 * regid = getRegistrationId(this.getApplicationContext()); if
			 * (regid.isEmpty()) { registerInBackground(); }
			 */

			if (getSPreferences().getBoolean(Constants.PREF_KEYS.logged_in,
					false)) {
				startMainActivity();
			}
			setContentView(R.layout.activity_login);
			if (savedInstanceState == null) {
				getFragmentManager().beginTransaction()
						.setTransitionStyle(R.anim.abc_fade_out)
						.add(R.id.loginFrame, new FragmentSignin()).commit();
			}
			toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
		} else {
			Utility.log(TAG, "No valid Google Play Services APK found.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Handle action bar item clicks here. The action bar will automatically
	 * handle clicks on the Home/Up button, so long as you specify a parent
	 * activity in AndroidManifest.xml.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		back_pressed = false;
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Utility.startActivity(this, SettingActivity.class);
			return true;
		} else if (id == R.id.action_about) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (in_settings) {
			getFragmentManager().popBackStack();
			in_settings = false;

		} else {
			if (!back_pressed) {
				back_pressed = true;
				Utility.RaiseToast(getApplicationContext(),
						getString(R.string.exit_warning), true);
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		checkPlayServices();
		back_pressed = false;
	}

	public void onClickButtonSignin(View view) {

		back_pressed = false;
		if (Utility.isConnected(getApplicationContext())) {
			Utility.showProgressDialog(this);
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			user_id = ((EditText) findViewById(R.id.editText_userid)).getText()
					.toString().toUpperCase(Locale.US).trim();
			pwd = ((EditText) findViewById(R.id.editText_userpassword))
					.getText().toString().trim();
			if (verifyInput()) {

				is_faculty = ((RadioButton) findViewById(R.id.radioButton_faculty))
						.isChecked();

				regid = getRegistrationId(getApplicationContext());
				if (regid.equalsIgnoreCase("")) {
					Utility.log(TAG, "reg id not in preferences");
					registerInBackground();
				} else {
					Utility.log(TAG, "reg id is in preferences " + regid);

					JSONObject tmpobj = new JSONObject();
					try {
						tmpobj.put(Constants.JSONKEYS.USER_ID,
								Utility.encode(user_id));
						tmpobj.put(Constants.JSONKEYS.PSWD,
								Utility.encode(Utility.sha1(pwd)));
						tmpobj.put(Constants.JSONKEYS.FACULTY, is_faculty);
						tmpobj.put(Constants.JSONKEYS.REG_ID, regid);
					} catch (JSONException e) {
						Utility.DEBUG(e);
					}

					login(tmpobj.toString());
				}

			} else {
				clearInput();
				Utility.hideProgressDialog();
				Utility.RaiseToast(getApplicationContext(),
						"Input not correct! Retry", true);
			}
		} else {
			Utility.RaiseToast(getApplicationContext(),
					getString(R.string.no_internet), true);
		}
	}

	public void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(Constants.PREF_KEYS.is_faculty, is_faculty);
		startActivity(intent);
		finish();
	}

	/**
	 * verify that is the input is not null and at least one radio button is
	 * checked
	 * 
	 * @return true or false depending on the validity of input data
	 */
	public boolean verifyInput() {
		if (user_id != null
				&& user_id.length() > 0
				&& pwd != null
				&& pwd.length() > 0
				&& (((RadioButton) findViewById(R.id.radioButton_student))
						.isChecked() || ((RadioButton) findViewById(R.id.radioButton_faculty))
						.isChecked()))
			return true;
		return false;
	}

	/**
	 * clears the input fields
	 */
	public void clearInput() {
		((TextView) findViewById(R.id.editText_userpassword)).setText(null);
	}

	/**
	 * saving user details in preference file
	 * 
	 * @param token
	 *            save token to query with server
	 */
	public void saveUser(String token) {
		String saved_user_id = getSPreferences().getString(
				Constants.PREF_KEYS.user_id, null);
		Utility.log(TAG, "previously user logged in was " + saved_user_id);
		if (saved_user_id != null && user_id.equalsIgnoreCase(saved_user_id)) {
			getDbHelper().clearCourseData();
		} else {
			getDbHelper().hardReset();
		}
		Editor editor = getSPreferences().edit();
		editor.remove(Constants.PREF_KEYS.user_id)
				.putString(Constants.PREF_KEYS.user_id, user.user_id)
				.putString(Constants.PREF_KEYS.encripted_user_id,
						Utility.encode(user.user_id))
				.putString(Constants.PREF_KEYS.token, Utility.encode(token))
				.putString(Constants.PREF_KEYS.f_name, user.f_name)
				.putString(Constants.PREF_KEYS.l_name, user.l_name)
				.putString(Constants.PREF_KEYS.pic_url, user.picUrl);
		if (is_faculty) {
			editor.putString(Constants.PREF_KEYS.branch,
					((Faculty) user).branch);

		} else {
			Student s_user = (Student) user;
			editor.putString(Constants.PREF_KEYS.section, s_user.section)
					.putString(Constants.PREF_KEYS.year,
							String.valueOf(s_user.year));

		}
		editor.putBoolean(Constants.PREF_KEYS.logged_in, true)
				.putBoolean(Constants.PREF_KEYS.is_faculty, is_faculty)
				.commit();
	}

	/**
	 * hit the server with some post data to perform login(authenticate user
	 * details)
	 * 
	 * @param data
	 *            data to send to server for login
	 * 
	 */
	public void login(String data) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.addHeader("Content-Type", "application/json");
		HttpEntity entity = null;
		try {
			entity = new StringEntity(data);
		} catch (UnsupportedEncodingException e1) {
			Utility.DEBUG(e1);
		}

		client.post(getApplicationContext(),
				Utility.getBaseURL(getApplicationContext()) + "login/dologin",
				entity, null, new MyJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							Utility.log(TAG, " resp: " + response);
							/**
							 * if the tag in response JSOObject is about login
							 * and the status is true
							 */
							if (response.getString(Constants.JSONKEYS.TAG)
									.equalsIgnoreCase(
											Constants.JSONKEYS.TAG_MSGS.LOGIN)
									&& response
											.getBoolean(Constants.JSONKEYS.STATUS)) {
								// get the details of user who just logged in
								JSONObject userobj = response
										.getJSONObject(Constants.JSONKEYS.USER_DATA);
								// parse the data according to whether he is a
								// faculty or a student and create a user object
								// (Common superclass of FacultyFull and
								// SrudentFull)
								if (is_faculty) {
									FacultyFull f_user = new FacultyFull();

									f_user.branch = userobj
											.getString(Constants.JSONKEYS.BRANCH);
									if (response.has(Constants.JSONKEYS.STREET))
										f_user.street = userobj
												.getString(Constants.JSONKEYS.STREET);
									if (response.has(Constants.JSONKEYS.CITY))

										f_user.city = userobj
												.getString(Constants.JSONKEYS.CITY);
									if (response.has(Constants.JSONKEYS.STATE))

										f_user.state = userobj
												.getString(Constants.JSONKEYS.STATE);
									if (response.has(Constants.JSONKEYS.P_MOB))

										f_user.p_mob = userobj
												.getString(Constants.JSONKEYS.P_MOB);
									if (response.has(Constants.JSONKEYS.H_MOB))

										f_user.h_mob = userobj
												.getString(Constants.JSONKEYS.H_MOB);
									if (response.has(Constants.JSONKEYS.PIN))

										f_user.pin = userobj
												.getString(Constants.JSONKEYS.PIN);
									user = f_user;
								} else {
									StudentFull s_user = new StudentFull();
									s_user.section = userobj
											.getString(Constants.JSONKEYS.SECTION);
									s_user.year = Integer.parseInt(userobj
											.getString(Constants.JSONKEYS.YEAR));
									s_user.u_roll_no = userobj
											.getString(Constants.JSONKEYS.ROLL_NO);
									user = s_user;
								}
								user.user_id = user_id;
								user.f_name = userobj
										.getString(Constants.JSONKEYS.FIRST_NAME);
								user.l_name = userobj
										.getString(Constants.JSONKEYS.LAST_NAME);
								user.picUrl = userobj
										.getString(Constants.JSONKEYS.PROFILE_IMAGE);

								saveUser(response
										.getString(Constants.JSONKEYS.TOKEN));

								InitialData idata = parseInitialData(response
										.getJSONObject(Constants.JSONKEYS.INITIAL_DATA));

								getDbHelper().addInitialData(idata);
								getDbHelper().insertUser(user, is_faculty);
								startMainActivity();
							} else {

								Toast.makeText(getApplicationContext(),
										"Login Failed", Toast.LENGTH_LONG)
										.show();
							}
						} catch (JSONException e) {
							Utility.log(TAG + " queryServer exception ",
									e.getLocalizedMessage());
						} finally {
							Utility.hideProgressDialog();
						}
					}

					@Override
					public void commonTask() {
						Utility.hideProgressDialog();
						Utility.RaiseToast(getApplicationContext(),
								"Error Connectiong server", true);
					}

				});

	}

	/**
	 * parse the initial data i.e., the list of course branches sections years
	 * we received as a set of initial data and return the intialData object
	 * 
	 * we can skip this and pass the JSONObject to databse Helper class
	 * 
	 * @param response
	 * @throws JSONException
	 */
	private InitialData parseInitialData(JSONObject iObjData)
			throws JSONException {

		JSONArray tmparry = iObjData.getJSONArray(Constants.JSONKEYS.COURSES);
		InitialData idata = new InitialData();
		int x = tmparry.length();
		InitialData.Courses c;
		for (int i = 0; i < x; i++) {
			c = new InitialData.Courses();
			JSONObject obj = tmparry.getJSONObject(i);
			c.id = obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
			c.duration = obj.getInt(DbStructure.Courses.COLUMN_DURATION);
			c.name = obj.getString(DbStructure.Courses.COLUMN_NAME);
			idata.courses.add(c);
		}
		tmparry = iObjData.getJSONArray(Constants.JSONKEYS.BRANCHES);
		x = tmparry.length();
		InitialData.Branches b;
		for (int i = 0; i < x; i++) {
			b = new InitialData.Branches();
			JSONObject obj = tmparry.getJSONObject(i);
			b.course_id = obj.getInt(DbStructure.Branches.COLUMN_COURSE_ID);
			b.id = obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
			b.name = obj.getString(DbStructure.Branches.COLUMN_NAME);
			idata.branches.add(b);
		}
		tmparry = iObjData.getJSONArray(Constants.JSONKEYS.SECTIONS);
		x = tmparry.length();
		InitialData.Sections s;
		for (int i = 0; i < x; i++) {
			s = new InitialData.Sections();
			JSONObject obj = tmparry.getJSONObject(i);
			s.year_id = obj.getInt(DbStructure.Sections.COLUMN_YEAR_ID);
			s.id = obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
			s.name = obj.getString(DbStructure.Sections.COLUMN_NAME);
			idata.sections.add(s);
		}
		tmparry = iObjData.getJSONArray(Constants.JSONKEYS.YEARS);
		x = tmparry.length();
		InitialData.Year y;
		for (int i = 0; i < x; i++) {
			y = new InitialData.Year();
			JSONObject obj = tmparry.getJSONObject(i);
			y.branch_id = obj.getInt(DbStructure.Year.COLUMN_BRANCH_ID);
			y.id = obj.getInt(DbStructure.COLUMN_INCOMMING_ID);
			y.year = obj.getInt(DbStructure.Year.COLUMN_YEAR);
			idata.years.add(y);
		}
		Utility.log(TAG, "parsing initial data sucessfull");
		return idata;

	}

	private SharedPreferences getSPreferences() {
		if (spref == null)
			spref = getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spref;
	}

	/**
	 * for GCM
	 * 
	 */

	/**
	 * check google play service is installed and working
	 * 
	 * @return
	 */
	private boolean checkPlayServices() {
		Utility.log(TAG, "getting play service status");
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Utility.log(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		Utility.log(TAG, "getting GCM registration ID from preferences");
		String registrationId = getSPreferences().getString(
				Constants.PREF_KEYS.PROPERTY_REG_ID, "");
		if (registrationId.equalsIgnoreCase("")) {
			Utility.log(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing registration ID is not guaranteed to work with
		// the new app version.
		int registeredVersion = getSPreferences().getInt(
				Constants.PREF_KEYS.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Utility.log(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		Utility.log(TAG, "getting app version");
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		Utility.log(TAG, "getting GCM registration ID from google gcm server");
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				boolean res = true;
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(Constants.SENDER_ID);
					// Persist the registration ID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					Utility.log(TAG, "Error :" + ex.getMessage());
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
					res = false;
				}
				return res;
			}

			/**
			 * if GCM registration was successful then hit the server with login
			 * details else show error toast
			 */
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					JSONObject tmpobj = new JSONObject();
					try {
						tmpobj.put(Constants.JSONKEYS.USER_ID,
								Utility.encode(user_id));
						tmpobj.put(Constants.JSONKEYS.PSWD,
								Utility.encode(Utility.sha1(pwd)));
						tmpobj.put(Constants.JSONKEYS.FACULTY, is_faculty);
						tmpobj.put(Constants.JSONKEYS.REG_ID, regid);
					} catch (JSONException e) {
						Utility.DEBUG(e);
					}
					login(tmpobj.toString());

				} else {
					Utility.log(TAG, "gcm registration failed aborting");
					Utility.hideProgressDialog();
					Utility.RaiseToast(getApplicationContext(),
							"registration failed try again", false);
				}

			}
		}.execute();
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		Utility.log(TAG, "saving reg id and app veriosn in pref");
		int appVersion = getAppVersion(context);
		Utility.log(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = getSPreferences().edit();
		editor.putString(Constants.PREF_KEYS.PROPERTY_REG_ID, regId);
		editor.putInt(Constants.PREF_KEYS.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getApplicationContext());
		return dbh;
	}
}
