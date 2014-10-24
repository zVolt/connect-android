package in.siet.secure.adapters;

import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.User;
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

public class UsersAdapter extends ArrayAdapter<User>{
	private static ArrayList<User> values;
	private static Context context;
	private static final String TAG="in.siet.secure.adapters.UsersAdapter";
	public UsersAdapter(Context contxt, ArrayList<User> value) {
		super(contxt, R.layout.list_item_users, value);
		values=value;
		context=contxt;
	}
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
	/*	if(values.size()==0){
			rowView=inflater.inflate(R.layout.list_empty_item, parent, false);
			Utility.log(TAG, "returning new layout");
		}
		else{
	*/		rowView=inflater.inflate(R.layout.list_item_users, parent, false);
		
			ImageView profile_image=(ImageView) rowView.findViewById(R.id.ListItemUsersImageViewPic);
			TextView name=(TextView) rowView.findViewById(R.id.ListItemUsersTextViewName);
			TextView id=(TextView) rowView.findViewById(R.id.ListItemUsersTextViewId);
			ImageView state=(ImageView) rowView.findViewById(R.id.ListItemUsersImageViewState);
			String[] year=context.getResources().getStringArray(R.array.array_year);
			
			User tmpuser=values.get(position);
			name.setText(tmpuser.name);
			if(FilterOptions.STUDENT)
				id.setText(tmpuser.dep+Constants.SPACE+year[tmpuser.year]+Constants.SPACE+context.getString(R.string.year));
			else
				id.setText(tmpuser.dep);
			if (tmpuser.state.equalsIgnoreCase("online"))
				state.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_action_online));
			ImageLoader.getInstance().displayImage(values.get(position).picUrl, profile_image);
	//	}
		return rowView;
	}
	
	
/*	public void notifyDataSetChanged(int size){
		super.notifyDataSetChanged();
		if(size==0){
			try{
				String[] emptylistarray={
						context.getString(R.string.nothing_found)
					};
				ListView listview=(ListView) ((MainActivity)context).findViewById(R.id.listViewUsers);
					listview.setAdapter(new ArrayAdapter<String>(context,R.layout.list_empty_item,emptylistarray));
					Utility.log(TAG,"show_users no users");
			}catch(Exception e){
				Utility.log(TAG,""+e.getLocalizedMessage());
			}
		}
	}
*/
}
