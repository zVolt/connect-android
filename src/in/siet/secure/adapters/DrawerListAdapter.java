package in.siet.secure.adapters;

import in.siet.secure.sgi.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends ArrayAdapter<String>{
	private final String[] values;
	private final Context context;
	ViewHolder holder;
	public DrawerListAdapter(Context contxt,String[] value) {
		super(contxt, R.layout.list_item_drawer, value);
		values=value;
		context=contxt;
	}

	@Override
	public View getView(int position, View convertView,ViewGroup parent){
		
		if(convertView==null){
			LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=inflater.inflate(R.layout.list_item_drawer, parent, false);
			holder=new ViewHolder();
			holder.option=(TextView)convertView.findViewById(R.id.drawer_list_item_text);
			holder.image=(ImageView)convertView.findViewById(R.id.drawer_list_item_image);
			convertView.setTag(holder);
		}
		else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.option.setText(values[position]);
		switch(position){
		case 0:
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_notification));
			break;
		case 1:
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_chats));
			break;
		case 2:
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_add_user));
			break;
		case 3:
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_setting));
			break;
		case 4:
			holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_new_notice));
			break;
		}
		return convertView;
	}
	static class ViewHolder{
		ImageView image;
		TextView option;
	}
}
