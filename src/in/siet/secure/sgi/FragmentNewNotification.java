package in.siet.secure.sgi;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentNewNotification extends Fragment implements
		OnClickListener {
	public static final String TAG = "in.siet.secure.sgi.FragmentNewNotification";
	private LinearLayout container_layout;
	private ArrayList<Attachment> file_list;
	private SharedPreferences spf;
	private EditText subject, body;

	public FragmentNewNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		spf = getActivity().getSharedPreferences(Constants.pref_file_name,
				Context.MODE_PRIVATE);
		View rootView = inflater.inflate(R.layout.fragment_new_notification,
				container, false);
		// ViewHolder holder = new ViewHolder();
		subject = (EditText) rootView
				.findViewById(R.id.editTextNewNoticeSubject);
		body = (EditText) rootView.findViewById(R.id.editTextNewNoticeBody);

		// send.setTag(holder);
		file_list = new ArrayList<Attachment>();
		container_layout = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutContainer);

		((Button) rootView.findViewById(R.id.buttonFileSelector))
				.setOnClickListener(this);

		((ImageButton) rootView.findViewById(R.id.buttonSendNotice))
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

	private static class ViewHolder {
		public int index;
		public View rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonFileSelector:
			selectFiles();
			break;
		case R.id.buttonSendNotice:
			sendNewNotification(v);
			break;
		case R.id.imageButtonAttachmentAction:
			removeFile(v);
			break;
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

			File file = new File(data.getData().getPath());
			Attachment tmp_atc = new Attachment(file.getName(),
					file.getAbsolutePath(), file.length());
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View child = inflater.inflate(R.layout.notification_attachements,
					container_layout, false);

			((TextView) child.findViewById(R.id.textViewNotiFileName))
					.setText(tmp_atc.name);

			((TextView) child.findViewById(R.id.textViewNotiFileDetail))
					.setText(Utility.getSizeString(tmp_atc.size));

			((ImageView) child.findViewById(R.id.imageViewState))
					.setImageResource(R.drawable.ic_file_upload);

			ImageView action_button = ((ImageView) child
					.findViewById(R.id.imageButtonAttachmentAction));
			ViewHolder holder = new ViewHolder();
			/**
			 * new index of inserting child
			 */
			holder.index = file_list.size();
			holder.rootView = child;
			action_button.setImageResource(R.drawable.ic_cancel);
			action_button.setTag(holder);
			action_button.setOnClickListener(this);
			/**
			 * set state image and action image set view holder with each action
			 * button so that actions can be performed upon clicking them
			 */
			container_layout.addView(child);
			file_list.add(tmp_atc);
		}
	}

	private void removeFile(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();

		int indx = holder.index;
		file_list.remove(indx);
		/**
		 * 3 view are fixed already in the linear layout see layout xml
		 * 
		 */
		indx += 3;
		container_layout.removeView(holder.rootView);
		/**
		 * modify all view holder index to match with new index after removing
		 * the file_list item
		 */
		View tmp_child = container_layout.getChildAt(indx);
		while (tmp_child != null) {
			((ViewHolder) tmp_child.findViewById(
					R.id.imageButtonAttachmentAction).getTag()).index -= 1;
			indx += 1;
			tmp_child = container_layout.getChildAt(indx);
		}
	}

	/**
	 * Creates a new Notification from data provided, Insert it in database and
	 * send it to server.
	 * 
	 * sending part should be moved to server
	 * 
	 * @param view
	 *            View on which the action is performed (ImageButton in this
	 *            case)
	 */
	public void sendNewNotification(View view) {

		if (verifyNewNotificationData()) {

			int year;
			// data copied in case use change it suddenly
			String course = FilterOptions.COURSE;
			String branch = FilterOptions.BRANCH;
			String section = FilterOptions.SECTION;
			String subject_txt, body_txt;
			year = FilterOptions.YEAR;
			int for_faculty = FilterOptions.STUDENT ? Constants.FOR_FACULTY.NO
					: Constants.FOR_FACULTY.YES;
			// fid string pk of user
			DbHelper db = new DbHelper(getActivity().getApplicationContext());
			int pk_user = db.getUserPk(spf.getString(
					Constants.PREF_KEYS.user_id, null));

			long time = Calendar.getInstance().getTimeInMillis();
			subject_txt = subject.getText().toString();
			body_txt = body.getText().toString();
			Notification new_noti = new Notification(for_faculty, subject_txt,
					body_txt, time, pk_user, course, branch, section, year,
					file_list); 
			// state if filled by db class
			db.insertNewNotification(new_noti);
			
			Intent intent = new Intent(getActivity().getApplicationContext(),
					NotificationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constants.NOTIFICATION.SUBJECT, new_noti.subject);
			bundle.putString(Constants.NOTIFICATION.TEXT, new_noti.text);
			bundle.putLong(Constants.NOTIFICATION.TIME, new_noti.time);
			bundle.putString(Constants.NOTIFICATION.SENDER_IMAGE, new_noti.image);
			bundle.putInt(Constants.NOTIFICATION.ID, new_noti.sender_id);		
			intent.putExtra(Constants.INTENT_EXTRA.BUNDLE_NAME, bundle);
			startActivity(intent);			
// go to detailed notification now
			subject.getText().clear();
			body.getText().clear();
			Utility.RaiseToast(getActivity(), "send new message", false);
		} else {
			Utility.RaiseToast(getActivity(), "cannot create notification",
					false);
		}
	}

	private boolean verifyNewNotificationData() {
		String subject_txt = subject.getText().toString().trim();
		String body_txt = body.getText().toString().trim();
		return !(subject_txt.length() == 0 || body_txt.length() == 0);

	}
}
