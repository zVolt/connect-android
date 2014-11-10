package in.siet.secure.adapters;

import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MessagesAdapter extends CursorAdapter{

	Context context;
	Cursor cursor;
	public MessagesAdapter(Context con, Cursor c, int flags) {
		super(con, c, flags);
		context=con;
		cursor=c;
	}


	@Override
	public void bindView(View view, Context context, Cursor c) {
		ViewHolder holder=(ViewHolder)view.getTag();
		holder.text.setText(c.getString(1));
		holder.time.setText(DateUtils.getRelativeDateTimeString(context, c.getLong(2), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0));
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.list_item_chats, null);
		ViewHolder holder=new ViewHolder();
		holder.text=(TextView)view.findViewById(R.id.textViewMessagesText);
		holder.time=(TextView)view.findViewById(R.id.textViewMessagesTime);
		view.setTag(holder);
		holder.text.setText(c.getString(1));
		holder.time.setText(DateUtils.getRelativeDateTimeString(context, c.getLong(2), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,0));
		return view;
	}
	static class ViewHolder{
		TextView text;
		TextView time;
	}

}
