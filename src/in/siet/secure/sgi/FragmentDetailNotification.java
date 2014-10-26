package in.siet.secure.sgi;

import in.siet.secure.Util.Attachment;
import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAttachmentAdapter;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

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
	private static ListView listViewAtachments;
	private static ArrayList<Attachment> attachments=new ArrayList<Attachment>();
	private static NotificationAttachmentAdapter adapter;
	public static View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		adapter=new NotificationAttachmentAdapter(getActivity(),attachments);
		Bundle bundle=getArguments();
		not_id=bundle.getInt(Notification.ID);
		new DbHelper(getActivity()).getFilesOfNotification(not_id);
		
		rootView = inflater.inflate(R.layout.fragment_detailed_notification, container,false);
		
		ImageView image=(ImageView)rootView.findViewById(R.id.imageViewNotiImage);
		TextView subject=(TextView)rootView.findViewById(R.id.notification_title);
		TextView text=(TextView)rootView.findViewById(R.id.notification_text);
		TextView time=(TextView)rootView.findViewById(R.id.notification_time);
		listViewAtachments=(ListView)rootView.findViewById(R.id.notification_list_attachments);
		
		subject.setText(bundle.getString(Notification.SUBJECT));
		text.setText(bundle.getString(Notification.TEXT));
		time.setText(bundle.getString(Notification.TIME));
		
		ImageLoader.getInstance().displayImage(Utility.getUserImage(bundle.getString(Notification.SENDER)), image);
		//adapter=new NotificationAttachmentAdapter(getActivity(), attachments);
		hideAttachments();
		return rootView;
	}
	@Override
	public void onStart(){
		super.onStart();
		listViewAtachments.setAdapter(adapter);
	}
	@Override
	public void onResume(){
		super.onResume();
		
	}
	public static void refresh(){
		if(adapter!=null)
			adapter.notifyDataSetChanged();
		
	}
	public static void setData(ArrayList<Attachment> data){
		adapter.clear();
		adapter.addAll(data);
		refresh();
	}
	public static void showAttachments(){
		rootView.findViewById(R.id.loading_attachments).setVisibility(View.GONE);
		listViewAtachments.setVisibility(View.VISIBLE);
	}
	public void hideAttachments(){
		listViewAtachments.setVisibility(View.GONE);
		rootView.findViewById(R.id.loading_attachments).setVisibility(View.VISIBLE);
	}
}
