package in.siet.secure.adapters;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.sgi.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class NotificationAttachmentAdapter extends ArrayAdapter<Attachment> {
	private static ArrayList<Attachment> files;
	ViewHolder holder;
	Context context;
	private Attachment tmp;

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
			holder.state_image = (ImageView) convertView
					.findViewById(R.id.imageViewState);
			holder.action_button = (ImageButton) convertView
					.findViewById(R.id.imageButtonAttachmentAction);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		tmp = files.get(position);
		if (tmp.state == Constants.FILE_STATE.ACK_SEND
				|| tmp.state == Constants.FILE_STATE.DOWNLOADED)
			holder.state_image.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_file_done));
		else
			holder.state_image.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_file_download));

		holder.name.setText(tmp.name);
		holder.data.setText(Utility.getSizeString(tmp.size));
		holder.action_button.setImageResource(R.drawable.ic_cancel);
		// holder.action_button.setOnClickListener();
		holder.state = tmp.state;
		holder.url = tmp.url;
		holder.id = tmp.id;

		convertView.setTag(holder);
		return convertView;
	}

	public static class ViewHolder {
		public int state; // refer to constants.STATE for info
		public int id;
		public String url;
		ImageView state_image;
		public ImageButton action_button;
		public TextView name;
		TextView data;
	}

}
