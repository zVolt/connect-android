package in.siet.secure.sgi;


import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.DrawerListAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.pushbots.push.Pushbots;
//import android.content.pm.ActivityInfo;


public class MainActivity extends ActionBarActivity {
	public static final String TAG="in.siet.secure.sgi.MainActivity";
	private String[] panelOption;
	private DrawerLayout drawerlayout;
	private ListView drawerListView;
	private LinearLayout fullDrawerLayout;
	private boolean back_pressed=false;
	private static ImageView user_pic;
	private static TextView user_name,user_id;
	private static ActionBarDrawerToggle drawerToggle;
	//private int active_drawer_option;
	static final UserFilterDialog show=new UserFilterDialog();
	private static SharedPreferences spf;
	public static final String EXTRA_MESSAGE = "message";
/*
	public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
*/
    String SENDER_ID = "908918972098";											/*REPLACE YOUR SENDER ID HERE*/
 //   String PUSHBOTS_APPLICATION_ID = "54634c131d0ab10f4a8b458e";
/*    
    GoogleCloudMessaging gcm=null;
    AtomicInteger msgId = new AtomicInteger();
    String regid;
    Context context;
*/
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		System.out.println("oncreate");
		if(savedInstanceState==null){
			setContentView(R.layout.activity_main);
			getFragmentManager().beginTransaction()
			.setTransitionStyle(R.anim.abc_fade_out)
			.add(R.id.mainFrame,new FragmentNotification(),FragmentNotification.TAG).commit();
		}		
		
		//	active_drawer_option=0;
		//	getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_launcher__lite_white));
		//set drawer
		
		/* CANCEL THE NOTIFICATION PRESENT IN THE NOTIFICATION DRAWER ONCE THE USER HAS VIEWED IT */	
		if(Utility.notification_msg_active==true)
			Utility.CancelMessageNotification(this);
			
		if(getApplicationContext()
				.getSharedPreferences(Constants.pref_file_name, Context.MODE_PRIVATE)
				.getBoolean(Constants.PreferenceKeys.is_faculty, false))
		{
			panelOption=getResources().getStringArray(R.array.array_panel_options_fact);
		}
		else{
			panelOption=getResources().getStringArray(R.array.array_panel_options);
		}

		drawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);		/* WHOLE ACTIVITY LAYOUT */
		drawerListView=(ListView)findViewById(R.id.drawer_listview);		/* LISTVIEW TO SHOW IN THE DRAWER */
		fullDrawerLayout=(LinearLayout)findViewById(R.id.drawer);			/* ACTUAL DRAWER IN THE LINEAR LAYOUT */
		user_name=(TextView)findViewById(R.id.textViewUserName);				/* USERNAME TO BE DISPLAYED IN THE NAVIGATION DRAWER */
		user_pic=(ImageView)findViewById(R.id.imageViewUser);				/* IMAGE TO BE DISPLAYED IN THE NAVIGATION DRAWER */
		user_id=(TextView)findViewById(R.id.textViewUserExtra);
		drawerListView.setAdapter(new DrawerListAdapter(this,panelOption));	

		drawerListView.setOnItemClickListener(new DrawerClickListner());
		drawerToggle=new ActionBarDrawerToggle(this, drawerlayout,R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		drawerlayout.setDrawerListener(drawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		DisplayImageOptions options=new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
		
		ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(options)
		.build();
		ImageLoader.getInstance().init(config);
		
		
	/*	context=getApplicationContext();
		if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
                System.out.println(TAG+"  "+regid);
            }
        } else {
            Utility.log(TAG, "No valid Google Play Services APK found.");
        	//Log.i(TAG, "No valid Google Play Services APK found.");
        } */
		}
		
	@Override
	public void onStart(){
		super.onStart();
		spf=getSharedPreferences(Constants.pref_file_name, Context.MODE_PRIVATE);
		DisplayImageOptions round_options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.displayer(new RoundedBitmapDisplayer(40)).build();
		ImageLoader.getInstance().displayImage(spf.getString(Constants.PreferenceKeys.pic_url, null), user_pic,round_options);
		user_name.setText(spf.getString(Constants.PreferenceKeys.f_name, null) +" "+spf.getString(Constants.PreferenceKeys.l_name, null) );
		System.out.println(spf.getString(Constants.PreferenceKeys.user_id,null));
		user_id.setText(spf.getString(Constants.PreferenceKeys.user_id, null));				////exception
	}
	@Override
	public void onResume(){
		super.onResume();
		back_pressed=false;
	}
	 @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        drawerToggle.syncState();
	 }

	 @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        drawerToggle.onConfigurationChanged(newConfig);
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		//super.onCreateOptionsMenu(menu);

		return true;
	}
	/*
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	*/
	/**
	 * Gets the current registration ID for application on GCM service.
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	/*
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	    	Utility.log(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Utility.log(TAG,"App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	*/
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	/*
	private SharedPreferences getGCMPreferences(Context context) {
	    return getSharedPreferences(Constants.pref_file_name,Context.MODE_PRIVATE);
	}
	*/
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	/*
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	*/
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	/*
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerInBackground() {
	    new AsyncTask() {
	  */      
	    /*	@Override
	        protected String doInBackground(String... params) {
	           
	        } */
	/*
			@Override
			protected String doInBackground(Object... arg0) {
				// TODO Auto-generated method stub
				System.out.println("in background");
				String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                    System.out.println("in background's iffff");
	    				
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;
	                System.out.println("in background " + msg);
					
	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();
	                System.out.println("registration id : "+regid);
	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                System.out.println("in backgroundcatch"+msg);
					
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
				//return null;
			}

	    //    protected void onPostExecute(String msg) {
	       //     mDisplay.append(msg + "\n");
	      //  }
	    }.execute(null, null, null);  
	}
	*/
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. 
	 */
/*
	private void sendRegistrationIdToBackend() {
		Log.d(TAG+" sendRegistrationIdToBackend"," at start");*/
/*		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://"+Constants.SERVER+Constants.COLON+Constants.PORT+"/SGI_webservice/Gcm/",
				new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode,Header[] headers,JSONObject response){ 
				Log.d(TAG+" onSucess"," at start");
				try {
			
				}catch (JSONException e) {
					Utility.log(TAG+" JSON exception ",e.getLocalizedMessage());
					
				}
			}
			
			@Override
			public void onFailure(int statusCode,Header[] headers,Throwable throwable,JSONObject errorResponse){	
				Utility.hideProgressDialog();
				Utility.RaiseToast(getApplicationContext(), "Error Connectiong server", true);
				Utility.log(TAG," incatch"+throwable.getMessage());
			}
			
		
		
	}); */
		
		// Your implementation here.
//	} 
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
/*
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Utility.log(TAG,"Saving regId on app version " + appVersion);
	    //spf=getSharedPreferences(Constants.pref_file_name, Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);			//store these variables in constant
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		//handle drawer open/close clicks
		back_pressed=false;
		if (drawerToggle.onOptionsItemSelected(item)) {
	          return true;
		}
		

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//startActivity(SettingsActivity.class);
			getFragmentManager().beginTransaction().replace(R.id.mainFrame, new FragmentSettings(),FragmentSettings.TAG)
			.commit();
			return true;
		}
		else if(id == R.id.action_logout) {
			spf.edit().clear().commit();
			Log.d(TAG,"pref cleared");
			startActivity(LoginActivity.class);
			finish();
			return true;
		}
		else if(id==R.id.action_reset){
			
			DbHelper db=new DbHelper(getApplicationContext());
			db.ClearDb(db.getWritableDatabase());
			
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed(){
		if(!back_pressed){
			back_pressed=true;
			Utility.RaiseToast(getApplicationContext(), getString(R.string.exit_warning), true);
		}
		else{
			super.onBackPressed();
		}
	}
	public void startActivity(Class<?> activity){
		Intent intent=new Intent(this,activity);
		Log.d(TAG,"stating login Activity");
		startActivity(intent);
	}
	
	
	public void switch_fragment(int position){
		FragmentManager fragmentManager=getFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction().setTransitionStyle(R.anim.abc_fade_out);
		Fragment fragment;//=fragmentManager.findFragmentByTag(TAG+panelOption[position]);
		switch(position){
		case Constants.DrawerIDs.NOTIFICATION:
			fragment=fragmentManager.findFragmentByTag(FragmentNotification.TAG);
			if(fragment==null)
				fragment=new FragmentNotification();
			fragmentTransaction.replace(R.id.mainFrame,fragment,FragmentNotification.TAG)
			.commit();
			break;
		case Constants.DrawerIDs.INTERACTION:
			fragment=fragmentManager.findFragmentByTag(FragmentContacts.TAG);
			if(fragment==null)
				fragment=new FragmentContacts();
			fragmentTransaction.replace(R.id.mainFrame,fragment,FragmentContacts.TAG)
			.commit();
			break;
		case Constants.DrawerIDs.ADD_USER:
			fragment=getFragmentManager().findFragmentByTag(TAG+"FragmentUsers");
			if(fragment==null)
				fragment=new FragmentUsers();
			fragmentTransaction.replace(R.id.mainFrame, fragment, FragmentUsers.TAG)
			.commit();	
			break;
		case Constants.DrawerIDs.SETTING:
			fragment=fragmentManager.findFragmentByTag(FragmentSettings.TAG);
			if(fragment==null)
				fragment=new FragmentSettings();
			fragmentTransaction.replace(R.id.mainFrame,fragment,FragmentSettings.TAG)
			.commit();
			break;
		case Constants.DrawerIDs.CREATE_NOTICE: //only for faculty
			fragment=fragmentManager.findFragmentByTag(FragmentNewNotification.TAG);
			if(fragment==null)
				fragment=new FragmentNewNotification();
			fragmentTransaction.replace(R.id.mainFrame,fragment,FragmentNewNotification.TAG)
			.commit();
			break;
		case Constants.DrawerIDs.TRIGGER:
			fragment=fragmentManager.findFragmentByTag(FragmentBackground.TAG);
			if(fragment==null)
				fragment=new FragmentBackground();
			fragmentTransaction.replace(R.id.mainFrame,fragment,FragmentBackground.TAG)
			.commit();
			break;
		default:
			Toast.makeText(getApplicationContext(),getString(R.string.wrong_choice), Toast.LENGTH_SHORT).show();
			return;
		}

	}
	
	public class DrawerClickListner implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			drawerListView.setItemChecked(position, true);

			Bundle bundle=new Bundle();
			if(position==Constants.DrawerIDs.ADD_USER){
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,Constants.DrawerIDs.ADD_USER);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
			}
			else
				if(position==Constants.DrawerIDs.CREATE_NOTICE){
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,Constants.DrawerIDs.CREATE_NOTICE);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
				}
				else
					switch_fragment(position);
			
			drawerlayout.closeDrawer(fullDrawerLayout);
			
		}
	}

	
	public void sendNewNotification(View view){
		
		((TextView)(view.getRootView().findViewById(R.id.editTextNewNoticeSubject))).setText("");
		((TextView)(view.getRootView().findViewById(R.id.editTextNewNoticeBody))).setText("");
		new DbHelper(getApplicationContext()).addNewNotification((FragmentNewNotification.ViewHolder)(view.getRootView().getTag()));
		Utility.RaiseToast(getApplicationContext(), "send new message", false);
	}

}
