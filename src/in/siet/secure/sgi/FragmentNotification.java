package in.siet.secure.sgi;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.dao.DbHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentNotification extends Fragment{
	static String TAG="in.siet.secure.sgi.FragmentNotification";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notification, container,	false);
	//	getActivity().setTitle(getResources().getStringArray(R.array.panel_options)[0]);
		
		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		Utility.RaiseToast(getActivity(), "I am Notification", 0);
	}
	@Override
	public void onStart(){
		super.onStart();
		Utility.showProgressDialog(getActivity());
		load();
		Utility.hideProgressDialog();
	}
	public void load(){
		DbHelper dbhelper=new DbHelper(getActivity());
		ListView listView=(ListView)getActivity().findViewById(R.id.fragment_notification_list);
		listView.setAdapter(new NotificationAdapter(getActivity(), dbhelper.getNotifications()));
		listView.setOnItemClickListener(new itemClickListener());
	}
	
	class itemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
			Notification notify=((Notification)adapter.getItemAtPosition(position));
			Fragment fragment=getFragmentManager().findFragmentByTag(TAG+"Notification");
			Bundle bundle =new Bundle();
			bundle.putString(Notification.SUBJECT,notify.subject);
			bundle.putString(Notification.TEXT,notify.text);
			bundle.putString(Notification.TIME,notify.time);
			bundle.putString(Notification.SENDER,notify.sender_id);
			bundle.putIntArray(Notification.ATTACHMENT, notify.attachments_id);
			bundle.putInt(Notification.ID, notify.id);
			
			if(fragment==null){
				fragment =new FragmentDetailNotification();
			}
				fragment.setArguments(bundle);
				getFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment,TAG+"Notification").addToBackStack(null).commit();
		}
	}
}
