package in.siet.secure.sgi;

import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState==null){
			
			
			setContentView(R.layout.activity_main);
			getSupportFragmentManager().beginTransaction()
			.add(R.id.mainFrame,new FragmentNotification()).commit();
		//set drawer
		panelOption=getResources().getStringArray(R.array.panel_options);
		drawerlayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		drawerListView=(ListView)findViewById(R.id.drawer_listview);
		drawerListView.setAdapter(new ArrayAdapter<String>(this,R.layout.list_item_drawer,panelOption));
		drawerListView.setOnItemClickListener(new DrawerClickListner());

		

		
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
	public boolean onCreateOptionsMenu(Menu menu) {
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
				fragmentTransaction.replace(R.id.mainFrame,new FragmentNotification(),TAG+panelOption[position]);
			else
				fragmentTransaction.replace(R.id.mainFrame,fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			break;
		case 1:
			if(fragment==null)
				fragmentTransaction.replace(R.id.mainFrame,new FragmentContacts(),TAG+panelOption[position]);
			else
				fragmentTransaction.replace(R.id.mainFrame,fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			break;
		case 2:
			
			UserCategoryDialog show=new UserCategoryDialog();
			show.show(fragmentManager, TAG+"UsersCategoryDialog");
		/*	if(fragment==null)
				fragmentTransaction.replace(R.id.mainFrame,new FragmentUsers(),TAG+panelOption[position]);
			else
				fragmentTransaction.replace(R.id.mainFrame,fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		*/	break;
		case 3:
			//settings
			Toast.makeText(getApplicationContext(),"settings are comming soon", Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(getApplicationContext(),getString(R.string.wrong_choice), Toast.LENGTH_SHORT).show();
			break;
		}
	}
	public class DrawerClickListner implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			drawerListView.setItemChecked(position, true);
			switch_fragment(position);
			drawerlayout.closeDrawer(drawerListView);
			
		}
	}
	
}
