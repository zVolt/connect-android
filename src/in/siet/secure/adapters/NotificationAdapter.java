package in.siet.secure.adapters;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class NotificationAdapter extends CursorAdapter {

	public static String TAG = "in.siet.secure.adapters.NotificationAdapter";
	ViewHolder holder;
	private int tmp_state;

	public NotificationAdapter(Context context_, Cursor cursor_, int flags) {
		super(context_, cursor_, flags);
	}

	public static class ViewHolder {
		public long id, time;
		public String image, subject, text;
		public int state;
		ImageView image_view;
		TextView subject_view;
		TextView time_view;
		ImageView state_view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		holder = (ViewHolder) view.getTag();
		view = setData(view, holder, cursor, context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.list_item_notification, parent,
				false);
		holder = new ViewHolder();

		holder.image_view = (ImageView) view
				.findViewById(R.id.imageViewNotificationImage);
		holder.subject_view = (TextView) view
				.findViewById(R.id.textViewNotificationTitle);
		holder.time_view = (TextView) view
				.findViewById(R.id.textViewNotificationTime);
		holder.state_view = (ImageView) view
				.findViewById(R.id.imageViewNotificationState);

		view = setData(view, holder, cursor, context);
		return view;
	}

	private View setData(View view, ViewHolder holder, Cursor cursor,
			Context context) {
		holder.id = cursor.getLong(0);
		holder.image = cursor.getString(1);
		holder.subject = cursor.getString(2);
		holder.text = cursor.getString(3);
		holder.time = cursor.getLong(4);
		holder.state = cursor.getInt(6);

		ImageLoader.getInstance().displayImage(holder.image, holder.image_view);
		tmp_state = holder.state;
		SpannableString content = null;
		if (tmp_state == Constants.NOTI_STATE.PENDING) {
			holder.state_view.setImageResource(R.drawable.ic_action_wait);
			holder.state_view.setVisibility(View.VISIBLE);
		} else if (tmp_state == Constants.NOTI_STATE.SENT) {
			holder.state_view.setImageResource(R.drawable.ic_action_done);
			holder.state_view.setVisibility(View.VISIBLE);
		} else if (tmp_state == Constants.NOTI_STATE.ACK_RECEIVED) {
			holder.state_view.setImageResource(R.drawable.ic_action_done_all);
			holder.state_view.setVisibility(View.VISIBLE);
		} else {
			holder.state_view.setVisibility(View.GONE);
			if (tmp_state != Constants.NOTI_STATE.READ) {
				// set text bold
				
				content = new SpannableString(cursor.getString(2));
				content.setSpan(new StyleSpan(Typeface.BOLD), 0,
						content.length(), 0);
			}

		}
		if (content == null) {
			holder.subject_view.setText(holder.subject);

		} else {
			holder.subject_view.setText(content);

		}
		holder.time_view.setText(Utility.getTimeString(context, holder.time,
				true));

		view.setTag(holder);
		return view;
	}
}
