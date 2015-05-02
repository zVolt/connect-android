package in.siet.secure.sgi;

import in.siet.secure.Util.MyJsonHttpResponseHandler;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class FragmentSettings extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	public static final String TAG = "in.siet.secure.sgi.FragmentSettings";
	private SharedPreferences spf;

	public FragmentSettings() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fragment_settings);
		// setRetainInstance(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		ActionBar toolbar = ((ActionBarActivity) getActivity())
				.getSupportActionBar();
		toolbar.setTitle(R.string.action_settings);
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPref,
			String key) {
		if (key.equalsIgnoreCase(getString(R.string.pref_key_server_ip))) {
			// Set summary to be the user-description for the selected value
			getSPreferences()
					.edit()
					.putString(Constants.PREF_KEYS.SERVER_IP,
							sharedPref.getString(key, "")).commit();
			Utility.RaiseToast(getActivity(), "Server's IP Address updated",
					false);

		} else if (key
				.equalsIgnoreCase(getString(R.string.pref_key_update_interval))) {
			// useless preference now as we implemented GCM
			getSPreferences()
					.edit()
					.putString(Constants.PREF_KEYS.UPDATE_INTERVAL,
							sharedPref.getString(key, String.valueOf(1)))
					.commit();

		} else if (key
				.equalsIgnoreCase(getString(R.string.pref_key_local_server))) {
			getSPreferences()
					.edit()
					.putBoolean(Constants.PREF_KEYS.LOCAL_SERVER,
							sharedPref.getBoolean(key, false)).commit();
		} else if (key
				.equalsIgnoreCase(getString(R.string.pref_key_pwd_change))) {
			// show progress hit server to change password and update pref keys
			// if they contain password
			String new_pwd = sharedPref.getString(
					Constants.PREF_KEYS.PWD_CHANGE, null);
			if (new_pwd != null && new_pwd.trim().length() > 0) {
				Utility.showProgressDialog(getActivity());
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				Utility.putCredentials(params, getSPreferences());
				params.put(Constants.QueryParameters.NEW_PWD,
						Utility.encode(new_pwd));

				client.get(Utility.getBaseURL(getActivity())
						+ "query/pwd_change", params,
						new MyJsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONObject response) {
								try {
									if (response
											.getString(Constants.JSONKEYS.TAG)
											.equalsIgnoreCase(
													Constants.JSONKEYS.TAG_MSGS.PWD_CHANGE)
											&& response
													.getBoolean(Constants.JSONKEYS.STATUS)) {

										Utility.RaiseToast(
												getActivity(),
												"password changed successfully",
												false);

									}
									else{
										Utility.RaiseToast(
												getActivity(),
												"password changed unsuccessfully!! Try again later.",
												false);
									}
									commonTask();
								} catch (JSONException e) {
									Utility.DEBUG(e);
								}
							};

							@Override
							public void commonTask() {
								Utility.hideProgressDialog();
							}
						});

			} else {
				Utility.RaiseToast(getActivity(), "Password remain unchanged",
						false);
			}
		}
	}

	private SharedPreferences getSPreferences() {
		if (spf == null)
			spf = getActivity().getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spf;
	}
}
