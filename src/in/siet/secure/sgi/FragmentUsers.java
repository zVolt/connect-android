package in.siet.secure.sgi;

import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.FilterOptions;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FragmentUsers extends Fragment {
	public static final String TAG = "in.siet.secure.sgi.FragmentUsers";
	SharedPreferences sharedPreferences = null;
	private static ArrayList<User> users = new ArrayList<User>();
	private static UsersAdapter adapter;
	public static ListView listview;
	public static TextView emptyText;

	// public static View emptyView;

	public FragmentUsers() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adapter = new UsersAdapter(getActivity(), users);
		sharedPreferences = getActivity().getSharedPreferences(
				Constants.pref_file_name, Context.MODE_PRIVATE);
		View rootView = inflater.inflate(R.layout.fragment_users, container,
				false);
		// Utility.log(TAG,"onCreate"+FilterOptions.USER_TYPE);
		setHasOptionsMenu(true);
		Utility.log(TAG, "onCreateViewCalled");
		listview = (ListView) rootView.findViewById(R.id.listViewUsers);

		listview.setAdapter(adapter);
		listview.setEmptyView(rootView.findViewById(R.id.test_view_empty_list));
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
		load();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_users_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_filter) {
			MainActivity.show.show(getFragmentManager(), UserFilterDialog.TAG);
			return true;
		}
		return false;
	}

	public void load() {
		Utility.log(TAG, "load");
		listview.setVisibility(View.GONE);
		Utility.showProgressDialog(getActivity());
		fetch_all();
	}

	public static void refresh() {
		if (adapter != null)
			adapter.notifyDataSetChanged();
		else
			Utility.log(TAG + " refresh()", "adapter is null");
	}

	public static void setData(ArrayList<User> data) {
		if (adapter != null) {
			adapter.clear();
			adapter.addAll(data);
			adapter.notifyDataSetChanged();
		} else {
			Utility.log(TAG, "adapetr empty");
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
			new DbHelper(getActivity()).addUser(holder.user,
					FilterOptions.STUDENT);
			// Utility.RaiseToast(getActivity(),
			// ((TextView)(view.findViewById(R.id.ListItemUsersTextViewName))).getText().toString()+" is added to contacts",
			// false);

			// Utility.RaiseToast(getActivity(),
			// ((TextView)(view.findViewById(R.id.ListItemUsersTextViewName))).getText().toString()+" is not added to contacts",
			// false);
		}

	}

	public void fetch_all() {

		RequestParams params = new RequestParams();
		Utility.putCredentials(params, sharedPreferences);
		params.put(Constants.QueryParameters.USER_TYPE, FilterOptions.STUDENT);
		params.put(Constants.QueryParameters.COURSE, FilterOptions.COURSE);
		params.put(Constants.QueryParameters.BRANCH, FilterOptions.BRANCH);
		params.put(Constants.QueryParameters.YEAR, FilterOptions.YEAR);
		params.put(Constants.QueryParameters.SECTION, FilterOptions.SECTION);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Utility.BASE_URL + "query/type_resolver", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						new FillData().execute(response);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// just in case we receive a object instead of an array
						Utility.log(TAG + " onSucess()", response.toString());
						Utility.hideProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						Utility.log(TAG, "failure" + errorResponse);
						Utility.hideProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Utility.log(TAG, "failure" + errorResponse);
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
					if (tmpobj.has(Constants.JSONKeys.YEAR))
						tmpusr = new Student(tmpobj
								.getString(Constants.JSONKeys.FIRST_NAME),
								tmpobj.getString(Constants.JSONKeys.LAST_NAME),
								tmpobj.getString(Constants.JSONKeys.USER_ID),
								tmpobj.getString(
										Constants.JSONKeys.PROFILE_IMAGE)
										.replace("\\/", "/"), tmpobj
										.getInt(Constants.JSONKeys.YEAR),
								tmpobj.getString(Constants.JSONKeys.SECTION));
					else
						tmpusr = new Faculty(tmpobj
								.getString(Constants.JSONKeys.FIRST_NAME),
								tmpobj.getString(Constants.JSONKeys.LAST_NAME),
								tmpobj.getString(
										Constants.JSONKeys.PROFILE_IMAGE)
										.replace("\\/", "/"), tmpobj
										.getString(Constants.JSONKeys.BRANCH),
								tmpobj.getString(Constants.JSONKeys.USER_ID));
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
			// refresh();
			listview.setVisibility(View.VISIBLE);
			Utility.hideProgressDialog();
		}
	}
}
