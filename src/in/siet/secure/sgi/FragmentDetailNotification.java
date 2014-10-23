package in.siet.secure.sgi;

import java.util.ArrayList;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAttachmentAdapter;
import in.siet.secure.dao.DbHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentDetailNotification extends Fragment{
	private int not_id;
	private ListView attachments;
	private TextView attachement_head;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Utility.showProgressDialog(getActivity());
		View rootView = inflater.inflate(R.layout.fragment_detailed_notification, container,false);
		ImageView image=(ImageView)rootView.findViewById(R.id.imageViewNotiImage);
		TextView subject=(TextView)rootView.findViewById(R.id.notification_title);
		TextView text=(TextView)rootView.findViewById(R.id.notification_text);
		TextView time=(TextView)rootView.findViewById(R.id.notification_time);
		attachments=(ListView)rootView.findViewById(R.id.notification_list_attachments);
		attachement_head=(TextView)rootView.findViewById(R.id.notification_attachments);
		Bundle bundle=getArguments();
		subject.setText(bundle.getString(Notification.SUBJECT));
		text.setText(bundle.getString(Notification.TEXT));
		time.setText(bundle.getString(Notification.TIME));
		ImageLoader.getInstance().displayImage(Utility.getUserImage(bundle.getString(Notification.SENDER)), image);
		not_id=bundle.getInt(Notification.ID);
		return rootView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		DbHelper db=new DbHelper(getActivity());
		ArrayList<Attachment> file=db.getFilesOfNotification(not_id);
		if(file.size()==0){
			attachement_head.setVisibility(TextView.INVISIBLE);
		}
		attachments.setAdapter(new NotificationAttachmentAdapter(getActivity(),file));
		Utility.hideProgressDialog();
		
	}
}
