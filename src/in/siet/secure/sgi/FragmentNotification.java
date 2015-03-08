package in.siet.secure.sgi;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
		listView.setEmptyView(rootView
				.findViewById(R.id.notification_list_empty_view));
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
			PullNotifications();
			new DbHelper(getActivity().getApplicationContext())
					.getNotifications();
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

	/**
	 * This method tells the adapter to update as underlying data has been
	 * changed
	 */
	public static void refresh() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	/**
	 * This method clear any prevoius data from adapter and add items in the
	 * data passed
	 * 
	 * @param data
	 *            ArrayList of Notification to be added in list
	 */
	public static void setData(ArrayList<Notification> data) {
		Utility.log(TAG, "received " + data.size());
		adapter.clear();
		adapter.addAll(data);
	}

	/**
	 * Fetched New notifications from server and insert them in database
	 */
	private void PullNotifications() {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		client.get(Utility.BASE_URL + "pull_notification", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String responseString) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, responseString);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, responseString,
								throwable);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}
				});
	}
}
