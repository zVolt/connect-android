package in.siet.secure.adapters;

import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;
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
	private Context context;
	private ViewHolder holder;
	private StringBuilder strb;
	private Cursor cs, cf;
	private boolean show_faculty;
	private DbHelper dbh;
	private final String ss = "select user._id,f_name,l_name,pic_url,sections.name,year.year,branches.name,courses.name,login_id from user join student on user._id=student.user_id join sections on section_id=sections._id join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id",
			sf = "select user._id,f_name,l_name,pic_url,branches.name,courses.name,login_id from user join faculty on user._id=faculty.user_id join branches on branch_id=branches._id join courses on course_id=courses._id";

	public ContactsAdapter(Context con, boolean show_faculty_) {
		super(con, null, 0);
		context = con;
		// cursor = c;
		cs = getDbHelper().getDb().rawQuery(ss, null);
		cf = getDbHelper().getDb().rawQuery(sf, null);
		show_faculty = show_faculty_;
		strb = new StringBuilder();
		swap(show_faculty);

	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		holder = (ViewHolder) view.getTag();
		holder.name.setText(getName(c));
		holder.detail.setText(getDetailText());
		holder.extra.setText(getExtraText());
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

		holder.name.setText(getName(c));
		holder.detail.setText(getDetailText());

		holder.extra.setText(getExtraText());
		ImageLoader.getInstance().displayImage(c.getString(3), holder.image);
		view.setTag(holder);
		return view;
	}

	public static class ViewHolder {
		public int user_pk_id;
		//public String user_name;
		TextView name;
		TextView detail;
		TextView extra;
		ImageView image;

	}

	private String getName(Cursor c) {
		return c.getString(1) + Constants.SPACE + c.getString(2);
	}

	private String getDetailText() {
		if (show_faculty) {
			return "I am a Faculty";
		} else {
			return "I am a Student";
		}
	}

	private String getExtraText() {
		strb.setLength(0);
		if (!show_faculty) {
			strb.append(cs.getString(4));
			strb.append(Constants.SPACE);
			strb.append(cs.getString(5));
			strb.append(Constants.SPACE);
			strb.append(cs.getString(6));
			strb.append(Constants.SPACE);
			strb.append(cs.getString(7));

		} else {
			strb.append(cf.getString(4));
			strb.append(Constants.SPACE);
			strb.append(cf.getString(5));

		}
		return strb.toString();
	}

	public void swap(boolean show_faculty_) {
		if (show_faculty_)
			this.swapCursor(cf);
		else
			this.swapCursor(cs);
		show_faculty = show_faculty_;
	}

	private DbHelper getDbHelper() {
		if (dbh == null)
			dbh = new DbHelper(context);
		return dbh;
	}
}
