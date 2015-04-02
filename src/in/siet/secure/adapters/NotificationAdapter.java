package in.siet.secure.adapters;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class NotificationAdapter extends ArrayAdapter<Notification> {
	private Context context;
	private ArrayList<Notification> values;
	public static String TAG = "in.siet.secure.adapters.NotificationAdapter";
	ViewHolder holder;
	private int tmp_state;
	private Notification tmp_noti;

	public NotificationAdapter(Context contxt, ArrayList<Notification> objects) {
		super(contxt, R.layout.list_item_notification, objects);
		context = contxt;
		values = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_notification,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.imageViewNotificationImage);
			holder.subject = (TextView) convertView
					.findViewById(R.id.textViewNotificationTitle);
			holder.time = (TextView) convertView
					.findViewById(R.id.textViewNotificationTime);
			holder.state = (ImageView) convertView
					.findViewById(R.id.imageViewNotificationState);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		tmp_noti = values.get(position);

		ImageLoader.getInstance().displayImage(tmp_noti.image, holder.image);
		tmp_state = tmp_noti.state;

		if (tmp_state == Constants.NOTI_STATE.PENDING) {
			holder.state.setImageResource(R.drawable.ic_action_done);
			holder.state.setVisibility(View.VISIBLE);
		} else if (tmp_state == Constants.NOTI_STATE.SENT
				|| tmp_state == Constants.NOTI_STATE.ACK_RECEIVED) {
			holder.state.setImageResource(R.drawable.ic_action_done_all);
			holder.state.setVisibility(View.VISIBLE);
		}
		else{
			holder.state.setVisibility(View.GONE);
		}

		holder.subject.setText(tmp_noti.subject);

		holder.time.setText(Utility.getTimeString(context, tmp_noti.time));

		convertView.setTag(holder);
		return convertView;
	}

	static class ViewHolder {
		ImageView image;
		TextView subject;
		TextView time;
		ImageView state;
	}
}
