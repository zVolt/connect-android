package in.siet.secure.sgi;

import in.siet.secure.Util.Notification;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
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
	static String TAG = "in.siet.secure.sgi.FragmentNotification";
	public static ArrayList<Notification> notifications = new ArrayList<Notification>();
	public static NotificationAdapter adapter;
	public static View rootView;
	public static ListView listView;

	// private static ProgressBar progressBar;
	public FragmentNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_notification, container,
				false);
		adapter = new NotificationAdapter(getActivity(), notifications);
		new DbHelper(getActivity()).getNotifications();
		setHasOptionsMenu(true);
		// progressBar=(ProgressBar)rootView.findViewById(R.id.loading_notification);
		listView = (ListView) rootView
				.findViewById(R.id.fragment_notification_list);
		listView.setOnItemClickListener(new itemClickListener());
		listView.setAdapter(adapter);
		// listView.setEmptyView(rootView.findViewById(R.id.e));
		// hideList();
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
		refresh();

		// Utility.RaiseToast(getActivity(), "FragmentNotification onResume()",
		// false);
	}

	/*
	 * public void hideList(){ listView.setVisibility(View.GONE);
	 * //progressBar.setVisibility(View.VISIBLE); } public static void
	 * showList(){ listView.setVisibility(View.VISIBLE);
	 * //progressBar.setVisibility(View.GONE); }
	 *//*
		 * @Override public void onStart(){ super.onStart();
		 * Utility.RaiseToast(getActivity(), "stating notifications", 0);
		 * //adapter.notifyDataSetChanged(); }
		 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.notification_refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh_notifications) {

			new DbHelper(getActivity()).getNotifications();
			return true;
		}
		return false;
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
				bundle.putString(Notification.SUBJECT, notify.subject);
				bundle.putString(Notification.TEXT, notify.text);
				bundle.putString(Notification.TIME, notify.time);
				bundle.putString(Notification.SENDER_IMAGE, notify.image);
				bundle.putInt(Notification.ID, notify.sender_id);
				fragment.setArguments(bundle);
			}
			getFragmentManager()
					.beginTransaction()
					.setTransitionStyle(R.anim.abc_fade_out)
					.replace(R.id.mainFrame, fragment,
							FragmentDetailNotification.TAG)
					.addToBackStack(null).commit();
		}
	}

	public static void refresh() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	public static void setData(ArrayList<Notification> data) {
		adapter.clear();
		adapter.addAll(data);
	}

	public static class PullNotifications extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// pull notifications
			return null;
		}

	}
}
