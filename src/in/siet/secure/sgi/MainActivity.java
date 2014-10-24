package in.siet.secure.sgi;

import in.siet.secure.adapters.DrawerListAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MainActivity extends ActionBarActivity{
	public static final String TAG="in.siet.secure.sgi.MainActivity";
	private String[] panelOption;
	private DrawerLayout drawerlayout;
	private ListView drawerListView;
	private static ActionBarDrawerToggle drawerToggle;
	//private int active_drawer_option;
	static final UserFilterDialog show=new UserFilterDialog();
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState==null){
			setContentView(R.layout.activity_main);
			getSupportFragmentManager().beginTransaction()
			.setTransitionStyle(R.anim.abc_fade_out)
			.add(R.id.mainFrame,new FragmentNotification()).commit();
		//	active_drawer_option=0;
		//	getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_launcher__lite_white));
		//set drawer
		panelOption=getResources().getStringArray(R.array.array_panel_options);
		drawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawerListView=(ListView)findViewById(R.id.drawer_listview);
		drawerListView.setAdapter(new DrawerListAdapter(this,panelOption));
		drawerListView.setOnItemClickListener(new DrawerClickListner());
		drawerToggle=new ActionBarDrawerToggle(this, drawerlayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
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
		
		}
		
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
		if (drawerToggle.onOptionsItemSelected(item)) {
	          return true;
		}
		
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
		Intent intent=new Intent(this,LoginActivity.class);
		Log.d(TAG,"stating login Activity");
		startActivity(intent);
		finish();
	}
	
	public void switch_fragment(int position){
		FragmentManager fragmentManager=getSupportFragmentManager();
		FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
		Fragment fragment=fragmentManager.findFragmentByTag(TAG+panelOption[position]);
		switch(position){
		case 0:
			if(fragment==null)
				fragment=new FragmentNotification();
			break;
		case 1:
			if(fragment==null)
				fragment=new FragmentContacts();
			break;
		case 2:
			show.show(fragmentManager, TAG+"UsersCategoryDialog");
			return;
		case 3:
			Toast.makeText(getApplicationContext(),"settings are comming soon", Toast.LENGTH_SHORT).show();
			return;
		default:
			Toast.makeText(getApplicationContext(),getString(R.string.wrong_choice), Toast.LENGTH_SHORT).show();
			return;
		}
		fragmentTransaction.setTransitionStyle(R.anim.abc_fade_out)
		.replace(R.id.mainFrame,fragment,TAG+panelOption[position])
		.commit();
	}
	public class DrawerClickListner implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			drawerListView.setItemChecked(position, true);
			switch_fragment(position);
			//active_drawer_option=position;
			drawerlayout.closeDrawer(drawerListView);
			
		}
	}
	
}
