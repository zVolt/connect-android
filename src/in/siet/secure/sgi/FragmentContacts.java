package in.siet.secure.sgi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentContacts extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts, container,	false);
		getActivity().setTitle(getResources().getStringArray(R.array.panel_options)[1]);
		return rootView;
	}
}
