package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.adapters.NotificationAdapter.ViewHolder;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbConstants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentNotification extends Fragment {
	public static final String TAG = "in.siet.secure.sgi.FragmentNotification";
	// private ArrayList<Notification> notifications = new
	// ArrayList<Notification>();
	private DbHelper dbh;
	private NotificationAdapter adapter;
	private View rootView;
	private ListView listView;
	private String query;
	private BroadcastReceiver local_broadcast_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.log(TAG, "received local broadcast");
			updateList();
		}
	};

	public FragmentNotification() {
	}

	private String getQuery() {
		if (query == null) {
			String[] columns = { DbStructure.NotificationTable._ID,
					DbStructure.UserTable.COLUMN_PROFILE_PIC,
					DbStructure.NotificationTable.COLUMN_SUBJECT,
					DbStructure.NotificationTable.COLUMN_TEXT,
					DbStructure.NotificationTable.COLUMN_TIME,
					DbStructure.NotificationTable.COLUMN_FOR_FACULTY,
					DbStructure.NotificationTable.COLUMN_STATE };

			StringBuilder strb = new StringBuilder("select "
					+ DbStructure.NotificationTable.TABLE_NAME
					+ DbConstants.DOT + columns[0] + DbConstants.COMMA
					+ columns[1] + DbConstants.COMMA + columns[2]
					+ DbConstants.COMMA + columns[3] + DbConstants.COMMA
					+ columns[4] + DbConstants.COMMA + columns[5]
					+ DbConstants.COMMA + columns[6] + " from "
					+ DbStructure.NotificationTable.TABLE_NAME + " join "
					+ DbStructure.UserTable.TABLE_NAME + " on "
					+ DbStructure.NotificationTable.COLUMN_SENDER + "="
					+ DbStructure.UserTable.TABLE_NAME + DbConstants.DOT
					+ DbStructure.UserTable._ID + " order by "
					+ DbStructure.NotificationTable.COLUMN_TIME + " desc");

			query = strb.toString();
		}
		return query;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_notification, container,
				false);

		Cursor c = getDbHelper().getDb().rawQuery(getQuery(), null);
		adapter = new NotificationAdapter(getActivity(), c, 0);

		setHasOptionsMenu(true);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_notification_list);
		listView.setOnItemClickListener(new itemClickListener());
		listView.setAdapter(adapter);
		listView.setEmptyView(rootView
				.findViewById(R.id.notification_list_empty_view));
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		updateList();
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.fragemnt_title_notification);
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.registerReceiver(
						local_broadcast_receiver,
						new IntentFilter(
								Constants.LOCAL_INTENT_ACTION.RELOAD_NOTIFICATIONS));

		Utility.CancelNotification(getActivity().getApplicationContext(),
				Constants.NOTI_NOTI_ID);
	}

	@Override
	public void onPause() {
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.unregisterReceiver(local_broadcast_receiver);
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.notification_refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/**
		 * we can use this to implement special menu options for notificaion
		 */
		return false;
	}

	/**
	 * public method to update notification list
	 */
	public void updateList() {
		Cursor cursor = getDbHelper().getDb().rawQuery(getQuery(), null);
		cursor = adapter.swapCursor(cursor);
		if (cursor != null)
			cursor.close();
	}

	/**
	 * This method clear any previous data from adapter and add items in the
	 * data passed
	 * 
	 * @param data
	 *            ArrayList of Notification to be added in list
	 */
	/*
	 * public void setDataInAdapter(ArrayList<Notification> data) {
	 * Utility.log(TAG, "received " + data.size()); // adapter.clear();
	 * adapter.addAll(data); refresh(); }
	 */
	/**
	 * handling click on notification lead to fire a intent to start a
	 * notification activity
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	class itemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {

			Intent intent = new Intent(getActivity().getApplicationContext(),
					NotificationActivity.class);

			Bundle bundle = new Bundle();
			ViewHolder holder = (ViewHolder) view.getTag();
			bundle.putLong(Constants.BUNDLE_DATA.NOTIFICATION_ID, holder.id);
			bundle.putLong(Constants.BUNDLE_DATA.NOTIFICATION_TIME, holder.time);
			bundle.putString(Constants.BUNDLE_DATA.NOTIFICATION_SUBJECT, holder.subject);
			bundle.putString(Constants.BUNDLE_DATA.NOTIFICATION_TEXT, holder.text);
			bundle.putString(Constants.BUNDLE_DATA.NOTIFICATION_IMAGE, holder.image);
			
			intent.putExtra(Constants.INTENT_EXTRA.BUNDLE_NAME, bundle);
			startActivity(intent);

			// seting the notification state to read on
			// FragmentDetailNotification's on Create view
		}
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getActivity().getApplicationContext());
		return dbh;
	}
}
