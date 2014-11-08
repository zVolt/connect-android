package in.siet.secure.sgi;



import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.ContactsAdapter;
import in.siet.secure.adapters.ContactsAdapter.ViewHolder;
import in.siet.secure.dao.DbHelper;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class FragmentContacts extends Fragment implements OnItemClickListener{
	public static String TAG="in.siet.secure.sgi.FramentContacts";

	public static ContactsAdapter adapter;
	Cursor cs,cf;
	SQLiteDatabase db;
	public static boolean student=true;
	final String ss="select user._id,f_name,l_name,pic_url,sections.name,year.year,branches.name,courses.name,login_id from user join student on user._id=student.user_id join sections on section_id=sections._id join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id",
				 sf="select user._id,f_name,l_name,pic_url,branches.name,courses.name,login_id from user join faculty on user._id=faculty.user_id join branches on branch_id=branches._id join courses on course_id=courses._id";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts, container,	false);
		db=new DbHelper(getActivity()).getReadableDatabase();
		
		cs=db.rawQuery(ss,null);
		cf=db.rawQuery(sf,null);
/*		String[] from={

				DbStructure.UserTable.COLUMN_FNAME,
				DbStructure.UserTable.COLUMN_LNAME
		};
		int[] to={
				R.id.textViewContactsName,
				R.id.textViewContactsDetails
		};

*/		adapter=new ContactsAdapter(getActivity(),cs,0);
		ListView contactList=(ListView)rootView.findViewById(R.id.listViewContacts);
		contactList.setAdapter(adapter);
		contactList.setOnItemClickListener(this);
		setHasOptionsMenu(true);

		return rootView;
	}
	@Override
	public void onResume(){
		super.onResume();
		student=true;
		((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.fragemnt_title_contacts);
		((MainActivity)getActivity()).getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_chats_white));
	}
	@Override
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_contacts, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==R.id.action_switch){
			Utility.RaiseToast(getActivity(), "change cursor", false);
			if(student){
				adapter.swapCursor(cf);
				item.setIcon(R.drawable.ic_teacher);
			}
			else{
				adapter.swapCursor(cs);
				
				item.setIcon(R.drawable.ic_student);
			}
			student=!student;
			
			return true;
		}
		return false;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		Intent intent=new Intent();
		intent.setClass(getActivity(), ChatActivity.class);
		intent.putExtra("name", ((ViewHolder)view.getTag()).user_name);
		intent.putExtra("user_id", ((ViewHolder)view.getTag()).user_id);
		startActivity(intent);
	}
}
