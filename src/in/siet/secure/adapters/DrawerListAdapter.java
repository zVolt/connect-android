package in.siet.secure.adapters;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends ArrayAdapter<String> {
	private final String[] values;
	private final Context context;
	ViewHolder holder;
	int hpad, vpad, tpad;

	public DrawerListAdapter(Context contxt, String[] value) {
		super(contxt, R.layout.list_item_drawer, value);
		values = value;
		context = contxt;
		hpad = getContext().getResources().getDimensionPixelSize(
				R.dimen.nav_drawer_hpad);
		tpad = getContext().getResources().getDimensionPixelSize(
				R.dimen.nav_drawer_toppad);
		vpad = getContext().getResources().getDimensionPixelSize(
				R.dimen.nav_drawer_vpad);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.drawer_header, parent,
					false);
			TextView user_name = (TextView) convertView
					.findViewById(R.id.textViewUserName);
			TextView user_id = (TextView) convertView
					.findViewById(R.id.textViewUserExtra);
			ImageView user_pic = (ImageView) convertView
					.findViewById(R.id.imageViewUser);
			SharedPreferences spf = getContext().getSharedPreferences(
					Constants.pref_file_name, Context.MODE_PRIVATE);
			DisplayImageOptions round_options = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.displayer(
							new RoundedBitmapDisplayer(getContext()
									.getResources().getDimensionPixelSize(
											R.dimen.drawer_user_image_radius)))
					.build();
			ImageLoader.getInstance().displayImage(
					spf.getString(Constants.PreferenceKeys.pic_url, null),
					user_pic, round_options);
			user_name.setText(spf.getString(Constants.PreferenceKeys.f_name,
					null)
					+ " "
					+ spf.getString(Constants.PreferenceKeys.l_name, null));
			user_id.setText(spf.getString(Constants.PreferenceKeys.user_id,
					null));
			// setting bounds

			convertView.setPadding(hpad, tpad, hpad, vpad);
			return convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item_drawer, parent,
					false);
			holder = new ViewHolder();
			holder.option = (TextView) convertView
					.findViewById(R.id.drawer_list_item_text);
			holder.image = (ImageView) convertView
					.findViewById(R.id.drawer_list_item_image);
			convertView.setTag(holder);
			convertView.setPadding(hpad, 0, hpad, 0);
			holder.option.setText(values[position]);
			switch (position) {
			case 1:
				holder.image.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.ic_action_notification));
				break;
			case 2:
				holder.image.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.ic_action_chats));
				break;
			case 3:
				holder.image.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.ic_action_add_user));
				break;
			case 4:
				holder.image.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.ic_action_setting));
				break;
			case 5:
				holder.image.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.ic_action_new_notice));
				break;
			}
			return convertView;
		}
	}

	static class ViewHolder {
		ImageView image;
		TextView option;
	}
}
