package in.siet.secure.sgi;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentContacts extends Fragment{
	public static String TAG="in.siet.secure.sgi.FramentContacts";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts, container,	false);
		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragemnt_title_contacts);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_chats_white));
	}
}
