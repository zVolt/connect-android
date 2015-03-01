package in.siet.secure.sgi;

import in.siet.secure.Util.Utility;
import in.siet.secure.adapters.ContactsAdapter;
import in.siet.secure.adapters.ContactsAdapter.ViewHolder;
import in.siet.secure.contants.Constants;
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
import android.widget.TextView;

public class FragmentContacts extends Fragment implements OnItemClickListener {
	public static String TAG = "in.siet.secure.sgi.FramentContacts";

	public static ContactsAdapter adapter;
	Cursor cs, cf;
	SQLiteDatabase db;
	public static boolean student;
	final String ss = "select user._id,f_name,l_name,pic_url,sections.name,year.year,branches.name,courses.name,login_id from user join student on user._id=student.user_id join sections on section_id=sections._id join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id",
			sf = "select user._id,f_name,l_name,pic_url,branches.name,courses.name,login_id from user join faculty on user._id=faculty.user_id join branches on branch_id=branches._id join courses on course_id=courses._id";

	public FragmentContacts() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_contacts, container,
				false);

		ListView contactList = (ListView) rootView
				.findViewById(R.id.listViewContacts);
		contactList.setAdapter(adapter);
		TextView emptyTextView = (TextView) rootView
				.findViewById(R.id.test_view_empty_list);
		contactList.setEmptyView(emptyTextView);
		contactList.setOnItemClickListener(this);
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null)
			student = true;
		// retain this fragment
		setRetainInstance(true);
		db = new DbHelper(getActivity()).getReadableDatabase();
		cs = db.rawQuery(ss, null);
		cf = db.rawQuery(sf, null);
		adapter = new ContactsAdapter(getActivity(), cs, 0);
		switchContacts();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// student=true;
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.fragemnt_title_contacts);
		((MainActivity) getActivity())
				.setDrawerSelect(Constants.DrawerIDs.INTERACTION);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_contacts, menu);
		if (student)
			menu.findItem(R.id.action_switch).setIcon(R.drawable.ic_student);
		else
			menu.findItem(R.id.action_switch).setIcon(R.drawable.ic_teacher);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_switch) {
			Utility.RaiseToast(getActivity(), "change cursor", false);
			if (student)
				item.setIcon(R.drawable.ic_teacher);
			else
				item.setIcon(R.drawable.ic_student);
			switchContacts();
			return true;
		}
		return false;
	}

	public void switchContacts() {
		if (student) {
			adapter.swapCursor(cf);
		} else {
			adapter.swapCursor(cs);
		}
		student = !student;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ChatActivity.class);
		intent.putExtra("name", ((ViewHolder) view.getTag()).user_name);
		intent.putExtra("user_id", ((ViewHolder) view.getTag()).user_id);
		startActivity(intent);
	}
}
