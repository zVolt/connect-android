package in.siet.secure.sgi;


import in.siet.secure.dao.DbHelper;
import in.siet.secure.dao.DbStructure;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FragmentContacts extends Fragment{
	public static String TAG="in.siet.secure.sgi.FramentContacts";
	public static SimpleCursorAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts, container,	false);
		SQLiteDatabase db=new DbHelper(getActivity()).getReadableDatabase();
		Cursor c=db.rawQuery("select _id,f_name,l_name from user",null);
		String[] from={
				DbStructure.UserTable.COLUMN_FNAME,
				DbStructure.UserTable.COLUMN_LNAME
		};
		int[] to={
				R.id.textViewContactsName,
				R.id.textViewContactsDetails
		};
		adapter=new SimpleCursorAdapter(getActivity(), R.layout.list_item_contacts, c, from, to, 0);
		ListView contactList=(ListView)rootView.findViewById(R.id.listViewContacts);
		contactList.setAdapter(adapter);
		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragemnt_title_contacts);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_chats_white));
	}
}
