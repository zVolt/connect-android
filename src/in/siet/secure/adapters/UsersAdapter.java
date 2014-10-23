package in.siet.secure.adapters;

import in.siet.secure.Util.User;
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

public class UsersAdapter extends ArrayAdapter<User>{
	private final ArrayList<User> values;
	private final Context context;
	public UsersAdapter(Context contxt, ArrayList<User> value) {
		super(contxt, R.layout.list_item_users, value);
		values=value;
		context=contxt;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
	
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView =inflater.inflate(R.layout.list_item_users, parent, false);
	
		ImageView profile_image=(ImageView) rowView.findViewById(R.id.ListItemUsersImageViewPic);
		TextView name=(TextView) rowView.findViewById(R.id.ListItemUsersTextViewName);
		TextView id=(TextView) rowView.findViewById(R.id.ListItemUsersTextViewId);
		ImageView state=(ImageView) rowView.findViewById(R.id.ListItemUsersImageViewState);
		
		name.setText(values.get(position).name);
		id.setText(values.get(position).dep);
		if (values.get(position).state.equalsIgnoreCase("online"))
			state.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_online));
		ImageLoader.getInstance().displayImage(values.get(position).picUrl, profile_image);
		
		return rowView;
	}
}
