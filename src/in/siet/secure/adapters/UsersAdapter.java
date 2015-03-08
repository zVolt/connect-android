package in.siet.secure.adapters;

import in.siet.secure.Util.Faculty;
import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.Student;
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

public class UsersAdapter extends ArrayAdapter<User> {
	private static ArrayList<User> values;
	private static Context context;
	private static final String TAG = "in.siet.secure.adapters.UsersAdapter";

	ViewHolder holder;

	public UsersAdapter(Context contxt, ArrayList<User> value) {
		super(contxt, R.layout.list_item_users, value);
		values = value;
		context = contxt;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_users, parent,
					false);
			holder = new ViewHolder();
			holder.profile_image = (ImageView) convertView
					.findViewById(R.id.ListItemUsersImageViewPic);
			holder.name = (TextView) convertView
					.findViewById(R.id.ListItemUsersTextViewName);
			holder.data = (TextView) convertView
					.findViewById(R.id.ListItemUsersTextViewId);
			// holder.state=(ImageView)
			// convertView.findViewById(R.id.ListItemUsersImageViewState);
			holder.user = values.get(position);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.user = values.get(position);
		}
		// String[]
		// year=context.getResources().getStringArray(R.array.array_year_4);
		User tmpuser = values.get(position);

		holder.name.setText(tmpuser.f_name + Constants.SPACE + tmpuser.l_name);
		holder.profile_image.setImageDrawable(context.getResources()
				.getDrawable(R.drawable.ic_action_person));
		if (FilterOptions.STUDENT) {
			Student s_user = (Student) tmpuser;
			holder.data.setText(s_user.year + " Year (" + s_user.section + ")");
		} else {
			Faculty f_user = (Faculty) tmpuser;
			holder.data.setText(f_user.branch);
		}
		ImageLoader.getInstance().displayImage(tmpuser.picUrl,
				holder.profile_image);
		return convertView;
	}

	public static class ViewHolder {
		ImageView profile_image;
		ImageView state;
		TextView name;
		TextView data;
		public User user;
	}
}
