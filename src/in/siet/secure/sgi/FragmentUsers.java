package in.siet.secure.sgi;

import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.MyJsonHttpResponseHandler;
import in.siet.secure.Util.Student;
import in.siet.secure.Util.User;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.UsersAdapter;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FragmentUsers extends Fragment {
	public static final String TAG = "in.siet.secure.sgi.FragmentUsers";
	private SharedPreferences spf = null;
	// private static ArrayList<User> users = new ArrayList<User>();
	private UsersAdapter adapter;
	private ListView listview;

	// public static View emptyView;

	public FragmentUsers() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adapter = new UsersAdapter(getActivity(), new ArrayList<User>());
		View rootView = inflater.inflate(R.layout.fragment_users, container,
				false);
		// Utility.log(TAG,"onCreate"+FilterOptions.USER_TYPE);
		setHasOptionsMenu(true);
		Utility.log(TAG, "onCreateViewCalled");
		listview = (ListView) rootView.findViewById(R.id.listViewUsers);

		listview.setAdapter(adapter);
		listview.setEmptyView((TextView) rootView
				.findViewById(R.id.users_empty_list_view));
		listview.setOnItemClickListener(new ItemClickListener());
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment
		setRetainInstance(true);
	}

	@Override
	public void onStart() {
		Utility.log(TAG, "start");
		super.onStart();
		// load();// this will load data for listview
	}

	@Override
	public void onResume() {
		Utility.log(TAG, "Resume");
		super.onResume();
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.fragemnt_title_users);
		if (Utility.isConnected(getActivity()))
			load();
		else
			Utility.RaiseToast(getActivity(), "No internet", true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_users_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_filter) {
			((MainActivity) getActivity()).show.show(getFragmentManager(),
					UserFilterDialog.TAG);
			return true;
		}
		return false;
	}

	public void load() {
		Utility.log(TAG, "load");
		Utility.showProgressDialog(getActivity());
		fetch_all();
	}

	public void refresh() {
		if (adapter != null)
			adapter.notifyDataSetChanged();

	}

	public void setData(ArrayList<User> data) {
		if (adapter != null) {
			adapter.clear();
			adapter.addAll(data);
			refresh();
		}
	}

	class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			// add user to contacts. A db operation
			// (view.findViewById(R.id.ListItemUsersTextViewName)).toString();
			UsersAdapter.ViewHolder holder = (UsersAdapter.ViewHolder) view
					.getTag();
			new DbHelper(getActivity()).getAndInsertUser(holder.user,
					FilterOptions.FACULTY);
			// Utility.RaiseToast(getActivity(),
			// ((TextView)(view.findViewById(R.id.ListItemUsersTextViewName))).getText().toString()+" is added to contacts",
			// false);

			// Utility.RaiseToast(getActivity(),
			// ((TextView)(view.findViewById(R.id.ListItemUsersTextViewName))).getText().toString()+" is not added to contacts",
			// false);
		}

	}

	/**
	 * fetch users from server based on filter
	 */
	public void fetch_all() {

		RequestParams params = new RequestParams();
		Utility.putCredentials(params, getSPreferences());
		params.put(Constants.QueryParameters.USER_TYPE, FilterOptions.FACULTY);
		params.put(Constants.QueryParameters.COURSE, FilterOptions.COURSE);
		params.put(Constants.QueryParameters.BRANCH, FilterOptions.BRANCH);
		params.put(Constants.QueryParameters.YEAR, FilterOptions.YEAR);
		params.put(Constants.QueryParameters.SECTION, FilterOptions.SECTION);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Utility.getBaseURL(getActivity().getApplicationContext()) + "query/type_resolver", params,
				new MyJsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						new FillData().execute(response);
					}

					@Override
					public void commonTask() {
						Utility.hideProgressDialog();
					}
				});
	}

	private class FillData extends
			AsyncTask<JSONArray, Integer, ArrayList<User>> {

		@Override
		protected ArrayList<User> doInBackground(JSONArray... params) {
			JSONArray values = params[0];
			ArrayList<User> tmpdata = new ArrayList<User>();
			try {
				int size = values.length();
				for (int i = 0; i < size; i++) {
					JSONObject tmpobj = values.getJSONObject(i);
					User tmpusr;
					/**
					 * whether the user id student or faculty by checking the
					 * content of received data if it contains the data
					 * corresponding to year he must be a student else a faculty
					 */
					if (tmpobj.has(Constants.JSONKEYS.YEAR))
						tmpusr = new Student(tmpobj
								.getString(Constants.JSONKEYS.FIRST_NAME),
								tmpobj.getString(Constants.JSONKEYS.LAST_NAME),
								tmpobj.getString(Constants.JSONKEYS.USER_ID),
								tmpobj.getString(
										Constants.JSONKEYS.PROFILE_IMAGE)
										.replace("\\/", "/"), tmpobj
										.getInt(Constants.JSONKEYS.YEAR),
								tmpobj.getString(Constants.JSONKEYS.SECTION));
					else
						tmpusr = new Faculty(tmpobj
								.getString(Constants.JSONKEYS.FIRST_NAME),
								tmpobj.getString(Constants.JSONKEYS.LAST_NAME),
								tmpobj.getString(
										Constants.JSONKEYS.PROFILE_IMAGE)
										.replace("\\/", "/"), tmpobj
										.getString(Constants.JSONKEYS.BRANCH),
								tmpobj.getString(Constants.JSONKEYS.USER_ID));
					tmpdata.add(tmpusr);
				}
			} catch (JSONException e) {
				Utility.log("error", "FillDATA " + e.getLocalizedMessage());
			}
			return tmpdata;
		}

		@Override
		protected void onPostExecute(ArrayList<User> data) {
			setData(data);
			Utility.hideProgressDialog();
		}
	}

	private SharedPreferences getSPreferences() {
		if (spf == null)
			spf = getActivity().getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spf;
	}
}
