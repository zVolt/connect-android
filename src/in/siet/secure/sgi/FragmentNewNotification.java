package in.siet.secure.sgi;

import in.siet.secure.contants.Constants;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class FragmentNewNotification extends Fragment {
	public static final String TAG = "in.siet.secure.sgi.FragmentNewNotification";

	public FragmentNewNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_new_notification,
				container, false);
		ViewHolder holder = new ViewHolder();
		holder.subject = (EditText) rootView
				.findViewById(R.id.editTextNewNoticeSubject);
		holder.body = (EditText) rootView
				.findViewById(R.id.editTextNewNoticeBody);
		ImageButton send = (ImageButton) rootView
				.findViewById(R.id.buttonSendNotice);
		send.setTag(holder);
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
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.fragemnt_title_new);
		
		super.onResume();
	}

	public static class ViewHolder {
		public EditText subject;
		public EditText body;
	}
}
