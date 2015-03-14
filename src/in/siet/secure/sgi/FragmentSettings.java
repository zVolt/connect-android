package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	public static final String TAG="in.siet.secure.sgi.FragmentSettings";
	public FragmentSettings(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fragment_settings);
		//setRetainInstance(true);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    ActionBar toolbar=((ActionBarActivity)getActivity()).getSupportActionBar();
	    toolbar.setTitle(R.string.action_settings);
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    
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
