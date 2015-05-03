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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
		OnClickListener, TextWatcher {
	public static final String TAG = "in.siet.secure.sgi.FragmentNewNotification";
	private LinearLayout container_layout;
	private ArrayList<Attachment> file_list;
	private SharedPreferences spf;
	private EditText subject, body;
	private DbHelper dbh;
	private ImageButton sendButton;

	public FragmentNewNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_new_notification,
				container, false);
		// ViewHolder holder = new ViewHolder();
		subject = (EditText) rootView
				.findViewById(R.id.editTextNewNoticeSubject);
		body = (EditText) rootView.findViewById(R.id.editTextNewNoticeBody);
		subject.addTextChangedListener(this);
		body.addTextChangedListener(this);
		// send.setTag(holder);
		file_list = new ArrayList<Attachment>();
		container_layout = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutContainer);

		((Button) rootView.findViewById(R.id.buttonFileSelector))
				.setOnClickListener(this);

		sendButton = ((ImageButton) rootView
				.findViewById(R.id.buttonSendNotice));

		sendButton.setOnClickListener(this);
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

	@Override
	public void onStart() {
		checkInput();
		super.onStart();
	}

	private void checkInput() {
		if (subject.getText().toString().trim().isEmpty()
				|| body.getText().toString().trim().isEmpty()) {
			sendButton.setEnabled(false);
		} else {
			sendButton.setEnabled(true);
		}
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
		intent.setType("*/*");
		startActivityForResult(intent, 1);
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		if (request == 1 && result == Activity.RESULT_OK) {
			Utility.log(
					TAG,
					data.getDataString()
							+ " path: "
							+ getPath(getActivity().getApplicationContext(),
									data.getData()));
			File file = new File(getPath(getActivity().getApplicationContext(),
					data.getData()));
			Attachment tmp_atc = new Attachment(file.getName(),
					file.getAbsolutePath(), file.length());
			Utility.log(TAG, "file: " + data.getData().getPath());
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

	private void clearAttachments() {
		for (int i = 3; i < container_layout.getChildCount(); i++)
			container_layout.removeViewAt(i);
		file_list = new ArrayList<Attachment>();
	}

	/**
	 * Creates a new Notification from data provided, Insert it in local
	 * database.
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
			int for_faculty = FilterOptions.FACULTY ? Constants.FOR_FACULTY.YES
					: Constants.FOR_FACULTY.NO;
			// fid string pk of user

			int pk_user = getDbHelper().getUserPk(
					getSPreferences().getString(Constants.PREF_KEYS.user_id,
							null));

			long time = Calendar.getInstance().getTimeInMillis();
			subject_txt = subject.getText().toString();
			body_txt = body.getText().toString();
			Notification new_noti = new Notification(for_faculty, subject_txt,
					body_txt, time, pk_user, course, branch, section, year,
					file_list);
			// state if filled by db class

			getDbHelper().insertNewNotification(new_noti);
			// go to detailed notification now
			subject.getText().clear();
			body.getText().clear();
			clearAttachments();

			Utility.RaiseToast(getActivity(), "send new notification", false);
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

	private SharedPreferences getSPreferences() {
		if (spf == null)
			spf = getActivity().getSharedPreferences(Constants.PREF_FILE_NAME,
					Context.MODE_PRIVATE);
		return spf;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getActivity().getApplicationContext());
		return dbh;
	}

	@Override
	public void afterTextChanged(Editable s) {
		checkInput();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);

				final String[] split = docId.split(":");
				final String type = split[0];

				Utility.log(TAG, "docID:" + docId + "\ntype:" + split[0]
						+ "\n s1:" + split[1]);
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				} else {
					return Environment.getExternalStoragePublicDirectory(type)
							+ "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}
}
