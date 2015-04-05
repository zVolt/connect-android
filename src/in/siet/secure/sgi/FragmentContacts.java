package in.siet.secure.sgi;

import in.siet.secure.adapters.ContactsAdapter;
import in.siet.secure.adapters.ContactsAdapter.ViewHolder;
import in.siet.secure.contants.Constants;
import android.app.Fragment;
import android.content.Intent;
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

	private ContactsAdapter adapter;
	/**
	 * to maintain the which list to show students or faculty
	 */
	private boolean student;

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
				.findViewById(R.id.contacts_empty_list_view);
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
		adapter = new ContactsAdapter(getActivity(), student);
		switchContacts();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.fragemnt_title_contacts);
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
		student = !student;
		adapter.swap(!student);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ChatActivity.class);
		intent.putExtra(Constants.INTENT_EXTRA.CHAT_USER_PK,
				((ViewHolder) view.getTag()).user_pk_id);
		startActivity(intent);
	}
}
