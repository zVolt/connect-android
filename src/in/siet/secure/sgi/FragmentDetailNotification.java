package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAttachmentAdapter;
import in.siet.secure.adapters.NotificationAttachmentAdapter.ViewHolder;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentDetailNotification extends Fragment implements
		OnClickListener {
	private long not_id;
	private LinearLayout listViewAtachments;
	// private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	private NotificationAttachmentAdapter adapter;
	private View rootView;
	private DbHelper dbh;
	public static final String TAG = "in.siet.secure.sgi.FragmentDetailNotification";
	private SharedPreferences spref;
	String files_query = "select _id,name,state,size,url from files join file_notification_map on file_id=_id where notification_id=?";
	private BroadcastReceiver refresh_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			/**
			 * check for the ID so that file list correctly corresponds to the
			 * notification displayed
			 **/
			updateCursor();
			// if
			// (intent.getBooleanExtra(Constants.INTENT_EXTRA.HAS_ATTACHMENTS,
			// false)) {
			// if (intent.getLongExtra(Constants.INTENT_EXTRA.NOTIFICATION_ID,
			// -1) == not_id) {
			// ArrayList<Attachment> data = intent
			// .<Attachment> getParcelableArrayListExtra
			// (Constants.INTENT_EXTRA.ATTACHMENTS_DATA);
			// if (data.size() > 0) {
			// //setData(data);
			// haveAttachments();
			// } else {
			// noAttachments();
			// }
			// } else {
			// noAttachments();
			// }
			// } else {
			// noAttachments();
			// }
		}

	};

	private void updateCursor() {
		processAttachments();
		Cursor cursor = getDbHelper().getDb().rawQuery(files_query,
				new String[] { String.valueOf(not_id) });
		adapter.changeCursor(cursor);
		if (adapter.getCount() > 0) {
			refresh();
			haveAttachments();
		} else
			noAttachments();
	}

	public FragmentDetailNotification() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Cursor cursor = getDbHelper().getDb().rawQuery(files_query,
				new String[] { String.valueOf(not_id) });
		adapter = new NotificationAttachmentAdapter(getActivity(), cursor);

		Bundle bundle = getArguments();
		not_id = bundle.getLong(Constants.BUNDLE_DATA.NOTIFICATION_ID);
		// getDbHelper().getFilesOfNotification(not_id);

		rootView = inflater.inflate(R.layout.fragment_detailed_notification,
				container, false);

		ImageView image = (ImageView) rootView
				.findViewById(R.id.imageViewNotificationSenderImage);
		TextView subject = (TextView) rootView
				.findViewById(R.id.textViewNotificationSubject);
		TextView text = (TextView) rootView
				.findViewById(R.id.textViewNotificationBody);
		TextView time = (TextView) rootView
				.findViewById(R.id.textViewNotificationTime);
		listViewAtachments = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutNotificationAttachmentsList);

		subject.setText(bundle
				.getString(Constants.BUNDLE_DATA.NOTIFICATION_SUBJECT));
		text.setText(bundle.getString(Constants.BUNDLE_DATA.NOTIFICATION_TEXT));

		time.setText(Utility.getTimeString(getActivity()
				.getApplicationContext(), bundle
				.getLong(Constants.BUNDLE_DATA.NOTIFICATION_TIME), true));

		ImageLoader.getInstance().displayImage(
				bundle.getString(Constants.BUNDLE_DATA.NOTIFICATION_IMAGE),
				image);
		// adapter=new NotificationAttachmentAdapter(getActivity(),
		// attachments);
		// processAttachments();
		updateCursor();
		int tmp_state = bundle.getInt(Constants.BUNDLE_DATA.NOTIFICATION_STATE);
		if (tmp_state == Constants.NOTI_STATE.RECEIVED
				|| tmp_state == Constants.NOTI_STATE.ACK_SEND)
			getDbHelper()
					.updateNotificationState(not_id, Constants.STATES.READ);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// retain this fragment
		setRetainInstance(true);
	}

	@Override
	public void onStart() {
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.registerReceiver(
						refresh_receiver,
						new IntentFilter(
								Constants.LOCAL_INTENT_ACTION.RELOAD_ATTACHMENTS));
		super.onStart();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager
				.getInstance(getActivity().getApplicationContext())
				.unregisterReceiver(refresh_receiver);
		super.onPause();
	}

	public void refresh() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			listViewAtachments.removeAllViews();
			View tmp_child;
			for (int i = 0; i < adapter.getCount(); i++) {
				tmp_child = adapter.getView(i, null, null);
				((ViewHolder) tmp_child.getTag()).action_button
						.setOnClickListener(this);
				listViewAtachments.addView(tmp_child);
			}
		}
	}

	private SharedPreferences getSPreferences() {
		if (spref == null)
			spref = getActivity().getSharedPreferences(
					Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		return spref;
	}

	/*
	 * public void setData(ArrayList<Attachment> data) { adapter.clear();
	 * adapter.addAll(data); refresh(); }
	 */
	public void haveAttachments() {
		rootView.findViewById(R.id.progressBarLoadingAttachments)
				.setVisibility(View.GONE);
		listViewAtachments.setVisibility(View.VISIBLE);
	}

	public void noAttachments() {
		listViewAtachments.setVisibility(View.GONE);
		rootView.findViewById(R.id.progressBarLoadingAttachments)
				.setVisibility(View.GONE);
	}

	public void processAttachments() {
		listViewAtachments.setVisibility(View.GONE);
		rootView.findViewById(R.id.progressBarLoadingAttachments)
				.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.imageButtonAttachmentAction:
			final ViewHolder h = (ViewHolder) ((View) view.getParent())
					.getTag();
			final String file_name = h.name.getText().toString();
			if (h.state != Constants.NOTI_STATE.RECEIVED) {
				// open file
				Utility.log(TAG, "opening file");
				File file = new File(h.url);
				if (file.exists()) {
					MimeTypeMap map = MimeTypeMap.getSingleton();
					String ext = MimeTypeMap.getFileExtensionFromUrl(file
							.getName());
					String type = map.getMimeTypeFromExtension(ext);
					if (type == null)
						type = "*/*";
					Intent intent = new Intent(Intent.ACTION_VIEW);
					Uri data = Uri.fromFile(file);
					intent.setDataAndType(data, type);
					// try {
					startActivity(intent);
					// } catch (Exception e) {
					// Utility.RaiseToast(getActivity(), "cannot open file",
					// false);
					// }
				} else {
					Utility.RaiseToast(getActivity(),
							"File does not exists on disk", false);
				}
			} else {
				// download file

				Utility.log(TAG, "downloading file state " + h.state);
				Utility.log("Yaha", "clicked on " + file_name);
				RequestParams params = new RequestParams();
				Utility.putCredentials(params, getSPreferences());
				params.put(Constants.QueryParameters.FILES.NAME, file_name);
				AsyncHttpClient client = new AsyncHttpClient();
				Utility.log(TAG, "before downloadf ile");
				Utility.SetProgressNotification(false, getActivity()
						.getApplicationContext());
				client.get(
						Utility.getBaseURL(getActivity()
								.getApplicationContext())
								+ "query/download_file", params,
						new FileAsyncHttpResponseHandler(getActivity()
								.getApplicationContext()) {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, File file) {
								Utility.log(TAG, "on success file download");
								try {

									Utility.log(TAG, "received file name: "
											+ file_name);
									long file_id = h.id;
									File dir = new File(Constants.PATH_TO_APP);
									if (isExternalStorageWritable()) {
										dir.mkdirs();
									}
									String file_url = dir + "/" + file_name;
									// else write in internal storage
									File file_new = new File(file_url);
									int read = 0;
									byte[] bytes = new byte[1024];
									InputStream inputStream = new FileInputStream(
											file);
									OutputStream outputStream = new FileOutputStream(
											file_new);
									while ((read = inputStream.read(bytes)) != -1) {
										outputStream.write(bytes, 0, read);
									}
									Utility.log(TAG, dir + "");
									outputStream.flush();
									outputStream.close();
									inputStream.close();
									getDbHelper().updateFileState(file_id,
											Constants.FILE_STATE.DOWNLOADED,
											file_url);
									Utility.completeProgressNotification(false,
											true);
								} catch (FileNotFoundException e) {
									Utility.DEBUG(e);
								} catch (IOException e) {
									Utility.DEBUG(e);
								}
								updateCursor();
							}

							@Override
							public void onFailure(int arg0, Header[] arg1,
									Throwable arg2, File arg3) {
								Utility.completeProgressNotification(false,
										false);
								Utility.log(TAG, "on failure file :" + arg2);
							}

							@Override
							public void onProgress(int bytesWritten,
									int totalSize) {
								Utility.updateProgressNotification(totalSize,
										bytesWritten, false);
								
							//	super.onProgress(bytesWritten, totalSize);
							}
						});
				Utility.log(TAG, "after download file");
			}
			break;
		}
	}

	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(getActivity().getApplicationContext());
		return dbh;
	}
}
