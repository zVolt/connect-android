package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MainActivity extends ActionBarActivity {
	public static final String TAG = "in.siet.secure.sgi.MainActivity";
	public static boolean UNI_IMG_LOADER_SET = false;

	private DrawerLayout drawerlayout;
	private ScrollView fullDrawerLayout;
	private boolean back_pressed = false;
	private ActionBarDrawerToggle drawerToggle;
	UserFilterDialog show = new UserFilterDialog();
	private SharedPreferences spf;
	private static String ACTIVE_FRAGMENT_TAG;
	private Toolbar toolbar;
	private DbHelper dbh;
	private View[] drawerItemHolder;
	private int[] drawerInactiveIconIds, drawerActiveIconIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (!UNI_IMG_LOADER_SET) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisk(true).build();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getApplicationContext())
					.defaultDisplayImageOptions(options).build();
			ImageLoader.getInstance().init(config);
			UNI_IMG_LOADER_SET = true;
		}

		ACTIVE_FRAGMENT_TAG = getIntent().getStringExtra(
				Constants.INTENT_EXTRA.FRAGMENT_TO_SHOW);
		if (ACTIVE_FRAGMENT_TAG == null) {
			ACTIVE_FRAGMENT_TAG = FragmentNotification.TAG;
		}
		Fragment notification = getFragmentManager().findFragmentByTag(
				ACTIVE_FRAGMENT_TAG);
		if (notification == null) {
			notification = getNewFragment(ACTIVE_FRAGMENT_TAG);
			Utility.log(TAG, "active fragment null");
		}

		getFragmentManager().beginTransaction()
				.setTransitionStyle(R.anim.abc_fade_out)
				.replace(R.id.mainFrame, notification, ACTIVE_FRAGMENT_TAG)
				.commit();

		/**
		 * CANCEL THE NOTIFICATION PRESENT IN THE NOTIFICATION DRAWER ONCE THE
		 * USER HAS VIEWED IT
		 * 
		 * if (Utility.notification_msg_active == true)
		 * Utility.CancelMessageNotification(this);
		 */
		initDrawerItems();
		drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerToggle = new ActionBarDrawerToggle(this, drawerlayout,
				R.string.drawer_open, R.string.drawer_close);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		drawerlayout.setDrawerListener(drawerToggle);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

	}

	private Fragment getNewFragment(String fragment_tag) {
		Fragment fragment = null;
		if (fragment_tag.equalsIgnoreCase(FragmentNotification.TAG))
			fragment = new FragmentNotification();
		else if (fragment_tag.equalsIgnoreCase(FragmentContacts.TAG))
			fragment = new FragmentContacts();

		return fragment;
	}

	private void initDrawerItems() {
		// setting user name and pic
		TextView user_name = (TextView) findViewById(R.id.textViewUserName);
		TextView user_id = (TextView) findViewById(R.id.textViewUserExtra);
		ImageView user_pic = (ImageView) findViewById(R.id.imageViewUser);
		DisplayImageOptions round_options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(
						new RoundedBitmapDisplayer(getResources()
								.getDimensionPixelSize(
										R.dimen.drawer_user_image_radius)))
				.build();
		ImageLoader.getInstance().displayImage(
				getSPreferences().getString(Constants.PREF_KEYS.pic_url, null),
				user_pic, round_options);
		user_name
				.setText(getSPreferences().getString(
						Constants.PREF_KEYS.f_name, null)
						+ Constants.SPACE
						+ getSPreferences().getString(
								Constants.PREF_KEYS.l_name, null));
		user_id.setText(getSPreferences().getString(
				Constants.PREF_KEYS.user_id, null));
		// end setting user details drawer header

		fullDrawerLayout = (ScrollView) findViewById(R.id.drawer);
		LinearLayout parent = (LinearLayout) findViewById(R.id.drawer_item_parent);
		// options defined by users
		String[] panelOption;
		if (getSPreferences().getBoolean(Constants.PREF_KEYS.is_faculty, false)) {
			panelOption = getResources().getStringArray(
					R.array.array_panel_options_fact);
		} else {
			panelOption = getResources().getStringArray(
					R.array.array_panel_options);
		}
		drawerInactiveIconIds = new int[] {
				Constants.DRAWER_IC_INACTIVE.NOTIFICATION,
				Constants.DRAWER_IC_INACTIVE.INTERACTION,
				Constants.DRAWER_IC_INACTIVE.ADD_USER,
				Constants.DRAWER_IC_INACTIVE.CREATE_NOTICE };
		drawerActiveIconIds = new int[] {
				Constants.DRAWER_IC_ACTIVE.NOTIFICATION,
				Constants.DRAWER_IC_ACTIVE.INTERACTION,
				Constants.DRAWER_IC_ACTIVE.ADD_USER,
				Constants.DRAWER_IC_ACTIVE.CREATE_NOTICE };
		drawerItemHolder = new View[panelOption.length];
		int pos = 0;
		View v;
		DrawerClickListner listner = new DrawerClickListner();
		DrawerItemViewHolder holder;
		LayoutInflater li = LayoutInflater.from(getApplicationContext());
		for (String str : panelOption) {
			v = li.inflate(R.layout.list_item_drawer, parent, false);
			v.setOnClickListener(listner);
			holder = new DrawerItemViewHolder();
			holder.img = (ImageView) v.findViewById(R.id.drawer_item_image);
			holder.txt = (TextView) v.findViewById(R.id.drawer_item_text);
			holder.txt.setText(str);
			holder.img.setImageResource(drawerInactiveIconIds[pos]);
			holder.position = pos;
			v.setTag(holder);
			parent.addView(v);
			drawerItemHolder[pos] = v;
			pos++;
		}

	}

	private static class DrawerItemViewHolder {
		ImageView img;
		TextView txt;
		int position;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		back_pressed = false;
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
		// super.onCreateOptionsMenu(menu);
		return true;
	}

	/**
	 * Handle action bar item clicks here. The action bar will automatically
	 * handle clicks on the Home/Up button, so long as you specify a parent
	 * activity in AndroidManifest.xml. handle drawer open/close clicks
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		back_pressed = false;
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Utility.startActivity(this, SettingActivity.class);
			return true;
		} else if (id == R.id.action_logout) {
			// clear pref but retain gcm registration ID and version number of
			// application
			String user_id = getSPreferences().getString(
					Constants.PREF_KEYS.user_id, "");
			String reg_id = getSPreferences().getString(
					Constants.PREF_KEYS.PROPERTY_REG_ID, "");
			int app_version = getSPreferences()
					.getInt(Constants.PREF_KEYS.PROPERTY_APP_VERSION,
							Integer.MIN_VALUE);
			getSPreferences()
					.edit()
					.clear()
					.putString(Constants.PREF_KEYS.user_id, user_id)
					.putString(Constants.PREF_KEYS.PROPERTY_REG_ID, reg_id)
					.putInt(Constants.PREF_KEYS.PROPERTY_APP_VERSION,
							app_version).commit();

			// new DbHelper(getApplicationContext()).softReset();

			Log.d(TAG, "pref cleared");
			Utility.startActivity(this, LoginActivity.class);
			finish();
			return true;
		} else if (id == R.id.action_reset) {
			getDbHelper().clearUserData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (!back_pressed) {
			back_pressed = true;
			Utility.RaiseToast(getApplicationContext(),
					getString(R.string.exit_warning), true);
		} else {
			/**
			 * set service to run as per user pref stored min 1hr
			 */
			/*
			 * Utility.setAlarm( getApplication(),
			 * Integer.parseInt(spf.getString(
			 * Constants.PREF_KEYS.UPDATE_INTERVAL, String.valueOf(1)))
			 * Constants.HOUR_TO_MILISEC);
			 */
			super.onBackPressed();
		}
	}

	private void setSelectedItem(int pos) {
		int i = 0;
		for (View v : drawerItemHolder) {
			if (i == pos) {
				v.setSelected(true);
				((DrawerItemViewHolder) v.getTag()).img
						.setImageResource(drawerActiveIconIds[i]);
			} else {
				v.setSelected(false);
				((DrawerItemViewHolder) v.getTag()).img
						.setImageResource(drawerInactiveIconIds[i]);
			}
			i++;
		}
	}

	public void switch_fragment(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction().setTransitionStyle(R.anim.abc_fade_out);
		Fragment fragment = null;// =fragmentManager.findFragmentByTag(TAG+panelOption[position]);

		switch (position) {
		case Constants.DRAWER_ID.NOTIFICATION:
			ACTIVE_FRAGMENT_TAG = FragmentNotification.TAG;
			fragment = fragmentManager
					.findFragmentByTag(FragmentNotification.TAG);
			if (fragment == null)
				fragment = new FragmentNotification();
			break;
		case Constants.DRAWER_ID.INTERACTION:
			ACTIVE_FRAGMENT_TAG = FragmentContacts.TAG;
			fragment = fragmentManager.findFragmentByTag(FragmentContacts.TAG);
			if (fragment == null) {
				fragment = new FragmentContacts();
			}
			break;
		case Constants.DRAWER_ID.ADD_USER:
			ACTIVE_FRAGMENT_TAG = FragmentUsers.TAG;
			Utility.log(TAG, "add user dialog");
			fragment = getFragmentManager()
					.findFragmentByTag(FragmentUsers.TAG);
			if (fragment == null) {
				Utility.log(TAG, "Fragment is null");
				fragment = new FragmentUsers();
			} else {
				// method to reload here causing fc null
				((FragmentUsers) fragment).load();
			}
			break;
		case Constants.DRAWER_ID.CREATE_NOTICE: // only for faculty
			ACTIVE_FRAGMENT_TAG = FragmentNewNotification.TAG;
			fragment = fragmentManager
					.findFragmentByTag(FragmentNewNotification.TAG);
			if (fragment == null)
				fragment = new FragmentNewNotification();
			break;
		default:
			Toast.makeText(getApplicationContext(),
					getString(R.string.wrong_choice), Toast.LENGTH_SHORT)
					.show();
			return;
		}

		Utility.log(TAG, "" + ACTIVE_FRAGMENT_TAG);
		fragmentTransaction.replace(R.id.mainFrame, fragment,
				ACTIVE_FRAGMENT_TAG).commit();
		setSelectedItem(position);
	}

	public class DrawerClickListner implements OnClickListener {

		@Override
		public void onClick(View view) {
			Utility.log(TAG, "m clicked :D");
			// remove previous selection and add new selection

			DrawerItemViewHolder holder = (DrawerItemViewHolder) view.getTag();
			Bundle bundle = new Bundle();
			if (holder.position == Constants.DRAWER_ID.ADD_USER) {
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,
						Constants.DRAWER_ID.ADD_USER);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
			} else if (holder.position == Constants.DRAWER_ID.CREATE_NOTICE) {
				bundle.putInt(UserFilterDialog.FRAGMENT_TO_OPEN,
						Constants.DRAWER_ID.CREATE_NOTICE);
				show.setArguments(bundle);
				show.show(getFragmentManager(), UserFilterDialog.TAG);
			} else {
				switch_fragment(holder.position);
			}
			drawerlayout.closeDrawer(fullDrawerLayout);

		}

	}

	private SharedPreferences getSPreferences() {
		if (spf == null)
			spf = getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spf;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getApplicationContext());
		return dbh;
	}
}
