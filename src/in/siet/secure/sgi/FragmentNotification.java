package in.siet.secure.sgi;

import in.siet.secure.Util.Notification;
import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

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
	public static ArrayList<Notification> notifications=new ArrayList<Notification>();
	public static NotificationAdapter adapter;
	public static View rootView;
	public static ListView listView;
	//private static ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_notification, container,	false);
		adapter=new NotificationAdapter(getActivity(), notifications);
		new DbHelper(getActivity()).getNotifications();
		//progressBar=(ProgressBar)rootView.findViewById(R.id.loading_notification);
		listView=(ListView)rootView.findViewById(R.id.fragment_notification_list);
		listView.setOnItemClickListener(new itemClickListener());
		listView.setAdapter(adapter);
		listView.setEmptyView(rootView.findViewById(R.id.test_view_empty_list));
		//hideList();
		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragemnt_title_notification);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_notification_white));
		refresh();
		Utility.RaiseToast(getActivity(), "FragmentNotification onResume()", 0);
	}
/*	public void hideList(){
		listView.setVisibility(View.GONE);
		//progressBar.setVisibility(View.VISIBLE);
	}
	public static void showList(){
		listView.setVisibility(View.VISIBLE);
		//progressBar.setVisibility(View.GONE);
	}
*//*	@Override
	public void onStart(){
		super.onStart();
		Utility.RaiseToast(getActivity(), "stating notifications", 0);
		//adapter.notifyDataSetChanged();
	}
*/	
	class itemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
			Notification notify=((Notification)adapter.getItemAtPosition(position));
			Fragment fragment=getFragmentManager().findFragmentByTag(TAG+"Notification");
			
			if(fragment==null){
				fragment =new FragmentDetailNotification();
				Bundle bundle =new Bundle();
				bundle.putString(Notification.SUBJECT,notify.subject);
				bundle.putString(Notification.TEXT,notify.text);
				bundle.putString(Notification.TIME,notify.time);
				bundle.putString(Notification.SENDER_IMAGE,notify.image);
				bundle.putInt(Notification.ID, notify.id);
				fragment.setArguments(bundle);
			}
				getFragmentManager()
				.beginTransaction()
				.setTransitionStyle(R.anim.abc_fade_out)
				.replace(R.id.mainFrame,fragment,TAG+"Notification")
				.addToBackStack(null)
				.commit();
		}
	}
	public static void refresh(){
		if(adapter!=null)
			adapter.notifyDataSetChanged();
	}
	public static void setData(ArrayList<Notification> data){
		adapter.clear();
		adapter.addAll(data);
	}
	
}
