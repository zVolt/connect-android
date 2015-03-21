package in.siet.secure.sgi;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbConstants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
	private ArrayList<Notification> notifications = new ArrayList<Notification>();
	private NotificationAdapter adapter;
	private View rootView;
	private ListView listView;
	private BroadcastReceiver local_broadcast_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Utility.log(TAG, "received local broadcast");
			updateList();
		}
	};

	// private static ProgressBar progressBar;
	public FragmentNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_notification, container,
				false);
		adapter = new NotificationAdapter(getActivity(), notifications);

		updateList();

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
		// retain this fragment
		setRetainInstance(true);
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
		refresh();
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
		if (item.getItemId() == R.id.action_refresh_notifications) {
			Utility.log(TAG, "refresh notification");

			updateList();
			return true;
		}
		return false;
	}

	/**
	 * public method to update notification list
	 */
	public void updateList() {
		// Utility.showProgressDialog(getActivity());
		new GetNotificationsFromDB().execute();
	}

	/**
	 * This method tells the adapter to update as underlying data has been
	 * changed
	 */
	public void refresh() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * This method clear any previous data from adapter and add items in the
	 * data passed
	 * 
	 * @param data
	 *            ArrayList of Notification to be added in list
	 */
	public void setDataInAdapter(ArrayList<Notification> data) {
		Utility.log(TAG, "received " + data.size());
		adapter.clear();
		adapter.addAll(data);
	}

	class itemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			Notification notify = ((Notification) adapter
					.getItemAtPosition(position));
			Fragment fragment = getFragmentManager().findFragmentByTag(
					FragmentDetailNotification.TAG);

			if (fragment == null) {
				fragment = new FragmentDetailNotification();
				Bundle bundle = new Bundle();
				bundle.putString(Constants.NOTIFICATION.SUBJECT, notify.subject);
				bundle.putString(Constants.NOTIFICATION.TEXT, notify.text);
				bundle.putLong(Constants.NOTIFICATION.TIME, notify.time);
				bundle.putString(Constants.NOTIFICATION.SENDER_IMAGE,
						notify.image);
				bundle.putInt(Constants.NOTIFICATION.ID, notify.sender_id);
				fragment.setArguments(bundle);
			}
			getFragmentManager()
					.beginTransaction()
					.setTransitionStyle(R.anim.abc_fade_out)
					.replace(R.id.mainFrame, fragment,
							FragmentDetailNotification.TAG)
					.addToBackStack(FragmentDetailNotification.TAG).commit();
		}
	}

	/**
	 * get the data for notification list from database
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	private class GetNotificationsFromDB extends
			AsyncTask<Void, Integer, ArrayList<Notification>> {

		@Override
		protected ArrayList<Notification> doInBackground(Void... params) {
			String[] columns = { DbStructure.NotificationTable._ID,
					DbStructure.UserTable.COLUMN_PROFILE_PIC,
					DbStructure.NotificationTable.COLUMN_SUBJECT,
					DbStructure.NotificationTable.COLUMN_TEXT,
					DbStructure.NotificationTable.COLUMN_TIME,
					DbStructure.NotificationTable.COLUMN_FOR_FACULTY };
			SQLiteDatabase db = new DbHelper(getActivity()
					.getApplicationContext()).getDb();
			Cursor c = db.rawQuery("select "
					+ DbStructure.NotificationTable.TABLE_NAME
					+ DbConstants.DOT + columns[0] + DbConstants.COMMA
					+ columns[1] + DbConstants.COMMA + columns[2]
					+ DbConstants.COMMA + columns[3] + DbConstants.COMMA
					+ columns[4] + DbConstants.COMMA + columns[5] + " from "
					+ DbStructure.NotificationTable.TABLE_NAME + " join "
					+ DbStructure.UserTable.TABLE_NAME + " on "
					+ DbStructure.NotificationTable.COLUMN_SENDER + "="
					+ DbStructure.UserTable.TABLE_NAME + DbConstants.DOT
					+ DbStructure.UserTable._ID

					+ " order by " + DbStructure.NotificationTable.COLUMN_TIME,
					null);

			ArrayList<Notification> notifications = new ArrayList<Notification>();
			c.moveToFirst();
			while (c.isAfterLast() == false) {
				Utility.log(TAG, "processsing notification");
				Notification tmpnot = new Notification(
						c.getInt(c.getColumnIndexOrThrow(columns[5])),
						c.getInt(c.getColumnIndexOrThrow(columns[0])),
						c.getString(c.getColumnIndexOrThrow(columns[1])),
						c.getString(c.getColumnIndexOrThrow(columns[2])),
						c.getString(c.getColumnIndexOrThrow(columns[3])), c
								.getLong(c.getColumnIndexOrThrow(columns[4])));
				notifications.add(tmpnot);
				Utility.log(TAG, tmpnot.subject);
				c.moveToNext();
			}
			c.close();
			return notifications;
		}

		@Override
		protected void onPostExecute(ArrayList<Notification> data) {
			Utility.log(TAG, "we get data" + data.toString());
			setDataInAdapter(data);
			refresh();
			// Utility.hideProgressDialog();
		}

	}
}
