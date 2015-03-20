package in.siet.secure.sgi;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentNewNotification extends Fragment implements
		OnClickListener {
	public static final String TAG = "in.siet.secure.sgi.FragmentNewNotification";
	private LinearLayout attachment_layout;
	private ArrayList<String> file_list;

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
		file_list = new ArrayList<String>();
		attachment_layout = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutAttachmetns);

		((Button) rootView.findViewById(R.id.buttonFileSelector))
				.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonFileSelector:
			selectFiles();
		}

	}

	public void selectFiles() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		if (request == 1 && result == Activity.RESULT_OK) {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View child = inflater.inflate(R.layout.notification_attachements,
					attachment_layout, false);
			((TextView) child.findViewById(R.id.textViewNotiFileName))
					.setText(data.getData().getPath());
			attachment_layout.addView(child);
			file_list.add(data.getData().getPath());
		}
	}
}
