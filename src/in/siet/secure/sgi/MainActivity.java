package in.siet.secure.sgi;


import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.DrawerListAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


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
	static final UserFilterDialog show=new UserFilterDialog();
	private static SharedPreferences spf;
	public static final String EXTRA_MESSAGE = "message";
	public static String ACTIVE_FRAGMENT_TAG;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(savedInstanceState==null)
		{
			ACTIVE_FRAGMENT_TAG=FragmentNotification.TAG;
	}
		DisplayImageOptions options=new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
		
		ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(options)
		.build();
		ImageLoader.getInstance().init(config);
		
		Fragment notification=getFragmentManager().findFragmentByTag(ACTIVE_FRAGMENT_TAG);	
		if(notification==null || ACTIVE_FRAGMENT_TAG==null){
			notification=new FragmentNotification();
			Utility.log(TAG,"active fragment null");
		}
	
		getFragmentManager().beginTransaction()
		.setTransitionStyle(R.anim.abc_fade_out)
		.replace(R.id.mainFrame,notification,ACTIVE_FRAGMENT_TAG).commit();
		
		/* CANCEL THE NOTIFICATION PRESENT IN THE NOTIFICATION DRAWER ONCE THE USER HAS VIEWED IT */	
		if(Utility.notification_msg_active==true)
			Utility.CancelMessageNotification(this);
		spf=getSharedPreferences(Constants.pref_file_name, Context.MODE_PRIVATE);
		
		if(spf.getBoolean(Constants.PreferenceKeys.is_faculty, false)){
			panelOption=getResources().getStringArray(R.array.array_panel_options_fact);
		}
		else{
			panelOption=getResources().getStringArray(R.array.array_panel_options);
		}
		
		drawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawerListView=(ListView)findViewById(R.id.drawer_listview);
		fullDrawerLayout=(LinearLayout)findViewById(R.id.drawer);
		user_name=(TextView)findViewById(R.id.textViewUserName);
		user_id=(TextView)findViewById(R.id.textViewUserExtra);
		user_pic=(ImageView)findViewById(R.id.imageViewUser);
		drawerListView.setAdapter(new DrawerListAdapter(this,panelOption));
		drawerListView.setOnItemClickListener(new DrawerClickListner());
		drawerToggle=new ActionBarDrawerToggle(this, drawerlayout,R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
		drawerlayout.setDrawerListener(drawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		
			
	}
	@Override
	public void onStart(){
		super.onStart();
		
		DisplayImageOptions round_options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.drawer_user_image_radius))).build();
		ImageLoader.getInstance().displayImage(spf.getString(Constants.PreferenceKeys.pic_url, null), user_pic,round_options);
		user_name.setText(spf.getString(Constants.PreferenceKeys.f_name, null) +" "+spf.getString(Constants.PreferenceKeys.l_name, null) );
		user_id.setText(spf.getString(Constants.PreferenceKeys.user_id, null));
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
			ACTIVE_FRAGMENT_TAG=FragmentNotification.TAG;
			fragment=fragmentManager.findFragmentByTag(FragmentNotification.TAG);
			if(fragment==null)
				fragment=new FragmentNotification();
			break;
		case Constants.DrawerIDs.INTERACTION:
			ACTIVE_FRAGMENT_TAG=FragmentContacts.TAG;
			fragment=fragmentManager.findFragmentByTag(FragmentContacts.TAG);
			if(fragment==null)
				fragment=new FragmentContacts();
			break;
		case Constants.DrawerIDs.ADD_USER:
			ACTIVE_FRAGMENT_TAG=FragmentUsers.TAG;
			fragment=getFragmentManager().findFragmentByTag(FragmentUsers.TAG);
			if(fragment==null)
				fragment=new FragmentUsers();	
			break;
		case Constants.DrawerIDs.SETTING:
			ACTIVE_FRAGMENT_TAG=FragmentSettings.TAG;
			fragment=fragmentManager.findFragmentByTag(FragmentSettings.TAG);
			if(fragment==null)
				fragment=new FragmentSettings();
			break;
		case Constants.DrawerIDs.CREATE_NOTICE: //only for faculty
			ACTIVE_FRAGMENT_TAG=FragmentNewNotification.TAG;
			fragment=fragmentManager.findFragmentByTag(FragmentNewNotification.TAG);
			if(fragment==null)
				fragment=new FragmentNewNotification();

			break;
		case Constants.DrawerIDs.TRIGGER:
			ACTIVE_FRAGMENT_TAG=FragmentBackground.TAG;
			fragment=fragmentManager.findFragmentByTag(FragmentBackground.TAG);
			if(fragment==null)
				fragment=new FragmentBackground();
			break;
		default:
			Toast.makeText(getApplicationContext(),getString(R.string.wrong_choice), Toast.LENGTH_SHORT).show();
			return;
		}
		fragmentTransaction.replace(R.id.mainFrame,fragment,ACTIVE_FRAGMENT_TAG).commit();
	}
	
	public class DrawerClickListner implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			

			Bundle bundle=new Bundle();
			if(position==Constants.DrawerIDs.ADD_USER){
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,Constants.DrawerIDs.ADD_USER);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
			}
			else if(position==Constants.DrawerIDs.CREATE_NOTICE){
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,Constants.DrawerIDs.CREATE_NOTICE);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
			}
			else{
				switch_fragment(position);
				drawerListView.setItemChecked(position, true);
			}
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
