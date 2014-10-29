package in.siet.secure.adapters;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
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

public class NotificationAdapter extends ArrayAdapter<Notification>{
	Context context;
	private static ArrayList<Notification> values;
	private static String TAG="in.siet.secure.adapters.NotificationAdapter";
	ViewHolder holder;
	public NotificationAdapter(Context contxt,ArrayList<Notification> objects) {
		super(contxt, R.layout.list_item_notification, objects);
		context=contxt;
		values=objects;
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		if(convertView==null){
			LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView= inflater.inflate(R.layout.list_item_notification, parent, false);
			holder=new ViewHolder();
			holder.image=(ImageView)convertView.findViewById(R.id.imageViewListNotiImage);
			holder.subject=(TextView)convertView.findViewById(R.id.notification_list_title);
			holder.time=(TextView)convertView.findViewById(R.id.notification_list_time);
			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder)convertView.getTag();
		}
		ImageLoader.getInstance().displayImage(values.get(position).image, holder.image);
		holder.subject.setText(values.get(position).subject);
		holder.time.setText(values.get(position).time);
		Utility.log(TAG, "view for "+position);
		return convertView;
	}
	
	static class ViewHolder{
		ImageView image;
		TextView subject;
		TextView time;
	}
}
