package in.siet.secure.adapters;

import in.siet.secure.Util.Attachment;
import in.siet.secure.sgi.FragmentDetailNotification;
import in.siet.secure.sgi.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class NotificationAttachmentAdapter extends ArrayAdapter<Attachment>{
	private static ArrayList<Attachment> files;
	Context context;
	public NotificationAttachmentAdapter(Context contxt,ArrayList<Attachment> objects) {
		super(contxt, R.layout.notification_attachements, objects);
		context=contxt;
		files=objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView= inflater.inflate(R.layout.notification_attachements, parent, false);
		
	//	ImageView image=(ImageView)rowView.findViewById(R.id.imageViewNotiFileImage);
		TextView name=(TextView)rowView.findViewById(R.id.textViewNotiFileName);
		TextView size=(TextView)rowView.findViewById(R.id.textViewNotiFileDetail);
	//	image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_attachment));
		name.setText(files.get(position).name);
		size.setText((files.get(position).size_mb));
		return rowView;
	}
	
	@Override
	public void notifyDataSetChanged(){
		super.notifyDataSetChanged();
		if(files!=null && files.size()==0){
			FragmentDetailNotification.attachement_head.setVisibility(TextView.INVISIBLE);
		}
	}

	
}
