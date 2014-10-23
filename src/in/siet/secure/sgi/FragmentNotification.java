package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.NotificationAdapter;
import in.siet.secure.dao.DbHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentNotification extends Fragment{
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
	}
}
