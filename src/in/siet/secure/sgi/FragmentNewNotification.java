package in.siet.secure.sgi;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentNewNotification extends Fragment{
	public static final String TAG="in.siet.secure.sgi.FragmentNewNotification";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_new_notification, container,	false);
		ViewHolder holder=new ViewHolder();
		holder.subject=(EditText)rootView.findViewById(R.id.editTextNewNoticeSubject);
		holder.body=(EditText)rootView.findViewById(R.id.editTextNewNoticeBody);
		rootView.setTag(holder);
		return rootView;
	}
	
	@Override
	public void onResume(){
		((ActionBarActivity)getActivity()).getActionBar().setTitle(R.string.fragemnt_title_new);
		((ActionBarActivity)getActivity()).getActionBar().setLogo(R.drawable.ic_action_new_notice_white);
		super.onResume();
	}
	public static class ViewHolder{
		public EditText subject;
		public EditText body;
	}
}
