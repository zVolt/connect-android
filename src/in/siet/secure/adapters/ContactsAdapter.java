package in.siet.secure.adapters;

import in.siet.secure.sgi.FragmentContacts;
import in.siet.secure.sgi.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ContactsAdapter extends CursorAdapter {
	Context context;
	Cursor cursor;
	private ViewHolder holder;

	public ContactsAdapter(Context con, Cursor c, int flags) {
		super(con, c, flags);
		context = con;
		cursor = c;
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (ViewHolder) view.getTag();
		holder.name.setText(holder.user_name = c.getString(1) + c.getString(2));
		holder.detail.setText(FragmentContacts.student ? "hi i am a student"
				: "hi i am a teacher");
		holder.extra.setText(FragmentContacts.student ? c.getString(4) + " "
				+ c.getString(5) + " " + c.getString(6) + " " + c.getString(7)
				: c.getString(4) + " " + c.getString(5));
		holder.image.setImageDrawable(context.getResources().getDrawable(
				R.drawable.ic_action_person));
		holder.user_pk_id = c.getInt(0);

		ImageLoader.getInstance().displayImage(c.getString(3), holder.image);
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater
				.inflate(R.layout.list_item_contacts, parent, false);
		holder = new ViewHolder();

		holder.name = (TextView) view.findViewById(R.id.textViewContactsName);
		holder.extra = (TextView) view.findViewById(R.id.textViewContactsExtra);
		holder.detail = (TextView) view
				.findViewById(R.id.textViewContactsDetails);
		holder.image = (ImageView) view
				.findViewById(R.id.imageViewContactsImage);

		holder.user_pk_id = c.getInt(0);

		holder.name.setText(holder.user_name = c.getString(1) + c.getString(2));
		holder.detail.setText(FragmentContacts.student ? "hi i am a student"
				: "hi i am a teacher");
		holder.extra.setText(FragmentContacts.student ? c.getString(4) + " "
				+ c.getString(5) + " " + c.getString(6) + " " + c.getString(7)
				: c.getString(4) + " " + c.getString(5));
		ImageLoader.getInstance().displayImage(c.getString(3), holder.image);

		view.setTag(holder);
		return view;
	}

	public static class ViewHolder {
		public int user_pk_id;
		public String user_name;
		TextView name;
		TextView detail;
		TextView extra;
		ImageView image;

	}

}
