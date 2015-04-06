package in.siet.secure.adapters;

import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactsAdapter extends CursorAdapter {
	private Context context;
	private ViewHolder holder;

	private Cursor cs, cf, cm;
	int state;
	private boolean show_faculty, have_new_msg;
	private DbHelper dbh;
	private final String ss = "select user._id,f_name,l_name,pic_url,login_id,last_msg_on from user join student on user_id=user._id where user._id<>? order by last_msg_on desc",
			sf = "select user._id,f_name,l_name,pic_url,login_id,last_msg_on from user join faculty on user_id=user._id where user._id<>? order by last_msg_on desc",
			sm = "select text,state from messages where sender=? or receiver=? order by time desc limit 1";
	private String[] args = { String.valueOf(1) }; // first user will be me(the
													// user of the application)

	public ContactsAdapter(Context con, boolean show_faculty_) {
		super(con, null, 0);
		context = con;
		// cursor = c;
		// cs = getDbHelper().getDb().rawQuery(ss, null);
		// cf = getDbHelper().getDb().rawQuery(sf, null);
		show_faculty = show_faculty_;
		reQuery();
		// swap(show_faculty);

	}

	ReQuery re_query;

	public void reQuery() {
		if (re_query == null
				|| re_query.getStatus() == AsyncTask.Status.FINISHED
				|| re_query.isCancelled()) {
			re_query = new ReQuery();
			re_query.execute();
		}
	}

	private class ReQuery extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Cursor c;
			c = getDbHelper().getDb().rawQuery(sf, args);

			cf = c;
			c = getDbHelper().getDb().rawQuery(ss, args);

			cs = c;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			swap(show_faculty);
		}
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (ViewHolder) view.getTag();
		holder.last_msg.setText(getLastMsg((holder.user_pk_id = c.getLong(0))));
		if (getHaveNewMsg()) {
			holder.name.setText(getBoldName(c));
		} else {
			holder.name.setText(getName(c));
		}
		setMsgStateIcon(holder.msg_state);
		holder.last_time.setText(getLastTime(c));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_action_person));
		ImageLoader.getInstance().displayImage(c.getString(3), holder.image);
		view.setTag(holder);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater
				.inflate(R.layout.list_item_contacts, parent, false);
		holder = new ViewHolder();

		holder.name = (TextView) view.findViewById(R.id.textViewContactsName);
		holder.last_msg = (TextView) view
				.findViewById(R.id.textViewContactsLastMsg);
		holder.last_time = (TextView) view
				.findViewById(R.id.textViewContactsTime);
		holder.image = (ImageView) view
				.findViewById(R.id.imageViewContactsImage);
		holder.msg_state = (ImageView) view
				.findViewById(R.id.imageViewMsgState);

		holder.user_pk_id = c.getInt(0);

		holder.last_msg.setText(getLastMsg((holder.user_pk_id = c.getLong(0))));
		if (getHaveNewMsg()) {
			holder.name.setText(getBoldName(c));
		} else {
			holder.name.setText(getName(c));
		}
		setMsgStateIcon(holder.msg_state);
		holder.last_time.setText(getLastTime(c));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_action_person));
		ImageLoader.getInstance().displayImage(c.getString(3), holder.image);
		view.setTag(holder);
		return view;
	}

	public static class ViewHolder {
		public long user_pk_id;
		// public String user_name;
		TextView name;
		TextView last_msg;
		TextView last_time;
		ImageView image;
		ImageView msg_state;
	}

	private String getName(Cursor c) {
		return c.getString(1) + Constants.SPACE + c.getString(2);
	}

	private SpannableString getBoldName(Cursor c) {
		SpannableString content = null;
		content = new SpannableString(getName(c));
		content.setSpan(new StyleSpan(Typeface.BOLD), 0, content.length(), 0);
		return content;
	}

	/**
	 * get new msg string is any and sets the state used to change the msg state
	 * icon
	 * 
	 * @param id
	 * @return
	 */
	private String getLastMsg(long id) {
		cm = getDbHelper().getDb().rawQuery(sm,
				new String[] { String.valueOf(id), String.valueOf(id) });
		String res;

		if (cm.moveToFirst()) {
			res = cm.getString(0);
			setState(cm.getInt(1));
			if (state == Constants.MSG_STATE.RECEIVED
					|| state == Constants.MSG_STATE.ACK_SEND)
				setHaveNewMsg(true);
			else
				setHaveNewMsg(false);
		} else {
			setState(Constants.MSG_STATE.READ);
			res = "No Messages";
			setHaveNewMsg(false);
		}
		cm.close();
		return res;
	}

	private void setMsgStateIcon(ImageView view) {
		switch (getState()) {
		case Constants.MSG_STATE.PENDING:
			view.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_wait));
			return;
		case Constants.MSG_STATE.SENT:
			view.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_done));
			return;
		case Constants.MSG_STATE.ACK_RECEIVED:
			view.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_done_all));
			return;
			// for all the rest cases
		default:
			view.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_received));
		}
	}

	private int getState() {
		return state;
	}

	private void setState(int state_) {
		state = state_;
	}

	private String getLastTime(Cursor c) {
		return Utility.getTimeString(context, c.getLong(5), false);
	}

	/**
	 * swap the cursor to show faculty and students not closing the cursor as he
	 * hold the references in cf and cs and closing the cursor will make the
	 * cursor invalid
	 * 
	 * @param show_faculty_
	 */
	public void swap(boolean show_faculty_) {
		if (show_faculty_)
			this.swapCursor(cf);
		else
			this.swapCursor(cs);
		show_faculty = show_faculty_;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(context);
		return dbh;
	}

	private void setHaveNewMsg(boolean value) {
		have_new_msg = value;
	}

	private boolean getHaveNewMsg() {
		return have_new_msg;
	}
}
