package in.siet.secure.adapters;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.sgi.R;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAdapter extends ArrayAdapter<Notification>{
	Context context;
	ArrayList<Notification> values;
	public NotificationAdapter(Context contxt,ArrayList<Notification> objects) {
		super(contxt, R.layout.list_item_notification, objects);
		context=contxt;
		values=objects;
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView= inflater.inflate(R.layout.list_item_notification, parent, false);
		
		ImageView image=(ImageView)rowView.findViewById(R.id.imageViewListNotiImage);
		TextView title=(TextView)rowView.findViewById(R.id.notification_list_title);
		TextView time=(TextView)rowView.findViewById(R.id.notification_list_time);
		
		ImageLoader.getInstance().displayImage(Utility.getUserImage(values.get(position).sender_id), image);
		title.setText(values.get(position).subject);
		time.setText(values.get(position).time);
		
		return rowView;
	}
}
