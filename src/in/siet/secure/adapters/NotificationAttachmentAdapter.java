package in.siet.secure.adapters;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class NotificationAttachmentAdapter extends CursorAdapter {

	// private static ArrayList<Attachment> files;
	ViewHolder holder;
	Context context;

	// private Attachment tmp;

	public NotificationAttachmentAdapter(Context contxt, Cursor cursor_) {
		super(contxt, cursor_, 0);// R.layout.notification_attachements,
									// objects);
		context = contxt;
		// files = objects;

	}

	public View setView(View view, Cursor c) {

		holder = (ViewHolder) view.getTag();
		// tmp = files.get(position);
		int state = c.getInt(2);
		String name = c.getString(1);
		long size = c.getLong(3);
		holder.state = state;
		holder.url = c.getString(4);
		holder.id = c.getLong(0);

	

		holder.name.setText(name);
		holder.data.setText(Utility.getSizeString(size));
		switch (state) {
		case Constants.FILE_STATE.RECEIVED:
		case Constants.FILE_STATE.ACK_SEND:
			holder.action_button
					.setImageResource(R.drawable.ic_action_download);
			
			break;
		case Constants.FILE_STATE.DOWNLOADED:
			holder.action_button.setImageResource(R.drawable.ic_action_file);
			break;
		case Constants.FILE_STATE.PENDING:
			holder.action_button.setImageResource(R.drawable.ic_action_wait);
			break;
		case Constants.FILE_STATE.SENT:
			holder.action_button.setImageResource(R.drawable.ic_action_done);
			break;
		case Constants.FILE_STATE.ACK_RECEIVED:
			holder.action_button
					.setImageResource(R.drawable.ic_action_done_all);
			break;
		}

		// holder.action_button.setOnClickListener();

		view.setTag(holder);
		return view;
	}

	public static class ViewHolder {
		public int state; // refer to constants.STATE for info
		public long id;
		public String url;
		//ImageView state_image;
		public ImageButton action_button;
		public TextView name;
		TextView data;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		view = setView(view, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.notification_attachements,
				parent, false);
		holder = new ViewHolder();
		holder.name = (TextView) view.findViewById(R.id.textViewNotiFileName);
		holder.data = (TextView) view.findViewById(R.id.textViewNotiFileDetail);
	//	holder.state_image = (ImageView) view.findViewById(R.id.imageViewState);
		holder.action_button = (ImageButton) view
				.findViewById(R.id.imageButtonAttachmentAction);
		view.setTag(holder);
		return setView(view, cursor);
	}

}
