package in.siet.secure.adapters;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessagesAdapter extends CursorAdapter {
	private LinearLayout.LayoutParams params;

	public MessagesAdapter(Context con, Cursor c, int flags) {
		super(con, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(c.getString(1));
		holder.time
				.setText(Utility.getTimeString(context, c.getLong(2), false));
		holder.state = c.getInt(3);
		if (holder.state == Constants.MSG_STATE.PENDING
				|| holder.state == Constants.MSG_STATE.SENT
				|| holder.state == Constants.MSG_STATE.ACK_RECEIVED) {
			holder.text.setBackgroundResource(R.drawable.msg_right_back);
			params = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
			params.setMargins(100, 0, 0, 0);
			holder.text.setLayoutParams(params);
			holder.container.setGravity(Gravity.RIGHT);

		} else {
			holder.text.setBackgroundResource(R.drawable.msg_left_back);
			params = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
			params.setMargins(0, 0, 100, 0);
			holder.text.setLayoutParams(params);
			holder.container.setGravity(Gravity.LEFT);
		}
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.list_item_chats, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.container = (LinearLayout) view
				.findViewById(R.id.LinearLayoutMessageContainer);
		holder.text = (TextView) view.findViewById(R.id.textViewMessagesText);
		holder.time = (TextView) view.findViewById(R.id.textViewMessagesTime);
		holder.text.setText(c.getString(1));
		holder.time.setText(DateUtils.getRelativeDateTimeString(context,
				c.getLong(2), DateUtils.SECOND_IN_MILLIS,
				DateUtils.WEEK_IN_MILLIS, 0));
		holder.state = c.getInt(3);
		if (holder.state == Constants.MSG_STATE.PENDING
				|| holder.state == Constants.MSG_STATE.SENT) {
			holder.text.setBackgroundResource(R.drawable.msg_right_back);
			params = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
			params.setMargins(100, 0, 0, 0);
			holder.text.setLayoutParams(params);
			holder.container.setGravity(Gravity.RIGHT);
		} else {
			holder.text.setBackgroundResource(R.drawable.msg_left_back);
			params = (LinearLayout.LayoutParams) holder.text.getLayoutParams();
			params.setMargins(0, 0, 100, 0);
			holder.text.setLayoutParams(params);
			holder.container.setGravity(Gravity.LEFT);
		}
		view.setTag(holder);
		return view;
	}

	static class ViewHolder {
		int state;
		LinearLayout container;
		TextView text;
		TextView time;
	}

}
