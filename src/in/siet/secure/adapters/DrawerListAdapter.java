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
	public DrawerListAdapter(Context contxt,String[] value) {
		super(contxt, R.layout.list_item_drawer, value);
		values=value;
		context=contxt;
	}

	@Override
	public View getView(int position, View convertView,ViewGroup parent){
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView =inflater.inflate(R.layout.list_item_drawer, parent, false);
		
		TextView textView=(TextView)rowView.findViewById(R.id.drawer_list_item_text);
		textView.setText(values[position]);
		//textView.setCompoundDrawables(context.getResources().getDrawable(R.drawable.ic_action_person), null, null, null);
		ImageView image=(ImageView)rowView.findViewById(R.id.drawer_list_item_image);
		switch(position){
		case 0:
			image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_notification));
			break;
		case 1:
			image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_chats));
			break;
		case 2:
			image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_add_user));
			break;
		case 3:
			image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_setting));
			break;
		}
		return rowView;
	}

}
