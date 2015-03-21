package in.siet.secure.adapters;

import in.siet.secure.Util.Attachment;
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

public class NotificationAttachmentAdapter extends ArrayAdapter<Attachment> {
	private static ArrayList<Attachment> files;
	ViewHolder holder;
	Context context;

	public NotificationAttachmentAdapter(Context contxt,
			ArrayList<Attachment> objects) {
		super(contxt, R.layout.notification_attachements, objects);
		context = contxt;
		files = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.notification_attachements,
					parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.textViewNotiFileName);
			holder.data = (TextView) convertView
					.findViewById(R.id.textViewNotiFileDetail);
			holder.image = (ImageView) convertView
					.findViewById(R.id.imageViewState);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Attachment tmp = files.get(position);
		if (tmp.state == 1)
			holder.image.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_saved));
		else
			holder.image.setImageDrawable(context.getResources().getDrawable(
					R.drawable.ic_action_download));

		holder.name.setText(tmp.name);
		holder.data.setText(Utility.getSizeString(tmp.size));
		holder.state = tmp.state;
		holder.url = tmp.url;
		holder.id = tmp.id;
	/*	convertView
				.setOnClickListener((FragmentDetailNotification) ((MainActivity) context)
						.getFragmentManager().findFragmentByTag(
								FragmentDetailNotification.TAG));*/
		convertView.setTag(holder);
		return convertView;
	}

	public static class ViewHolder {
		public int state; // 1-downloaded 0-not downloaded
		public int id;
		public String url;
		ImageView image;
		public TextView name;
		TextView data;
	}

}
