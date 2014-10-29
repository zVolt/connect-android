package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.FrameLayout;

public class FragmentSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	private static final String TAG="in.siet.secure.sgi.FragmentSettings";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fragment_settings);
		
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.action_settings);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_settings_white));
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
	public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
		if (key.equals("server_ip")) {
            // Set summary to be the user-description for the selected value
            String ip=sharedPref.getString(key, "192.168.0.100");
            Constants.SERVER=ip;
            (findPreference("server_ip")).setSummary(ip);
            Utility.RaiseToast(getActivity(), Constants.SERVER, false);
        }
		
	}
	
}
