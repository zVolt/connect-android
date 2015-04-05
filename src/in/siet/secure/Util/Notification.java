package in.siet.secure.Util;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {

	public String image, text, subject, branch, section, course;
	public long time, noti_id;
	public int sender_id, state, year;
	public int sid; // sender ki primary key according to local table
	public ArrayList<Attachment> files = new ArrayList<Attachment>();
	public int for_faculty;

	/**
	 * Notification fetched from server should be instantiated using this
	 * constructor
	 * 
	 * @param pk_user_id
	 * @param image_
	 * @param subject_
	 * @param text_
	 * @param time_
	 */
	public Notification(int for_faculty_, int pk_user_id, String image_,
			String subject_, String text_, long time_, int state_) {
		for_faculty = for_faculty_;
		sender_id = pk_user_id;
		time = time_;
		subject = subject_;
		image = image_;
		text = text_;
		state = state_;
	}

	/**
	 * New notification created should be instantiated using this constructor
	 * The parameters course_ branch_ section_ year_ is used to identify target
	 * 
	 * @param subject_
	 *            String Subject of the notification
	 * @param text_
	 *            String Content of notification
	 * @param time_
	 *            Long Time in milliseconds
	 * @param pk_user_id
	 *            integer Sender's primary key
	 * @param course_
	 *            String Course name
	 * @param branch_
	 *            String branch name
	 * @param section_
	 *            String section name
	 * @param year_
	 *            String year
	 * 
	 * 
	 */
	public Notification(int for_faculty_, String subject_, String text_,
			long time_, int pk_user_id, String course_, String branch_,
			String section_, int year_, ArrayList<Attachment> files_) {
		for_faculty = for_faculty_;
		time = time_;
		subject = subject_;
		text = text_;
		sid = pk_user_id;

		// target audience
		course = course_;
		branch = branch_;
		year = year_;
		section = section_;
		files = files_;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(for_faculty);
		dest.writeLong(time);
		dest.writeString(subject);
		dest.writeString(text);
		dest.writeInt(sid);

		dest.writeString(course);
		dest.writeString(branch);
		dest.writeInt(year);
		dest.writeString(section);
		dest.writeTypedList(files);

		dest.writeInt(sender_id);
		dest.writeString(image);
		dest.writeInt(state);
	}

	public Notification(Parcel in) {
		for_faculty = in.readInt();
		time = in.readLong();
		subject = in.readString();
		text = in.readString();
		sid = in.readInt();

		// target audience
		course = in.readString();
		branch = in.readString();
		year = in.readInt();
		section = in.readString();

		in.readTypedList(files, Attachment.CREATOR);

		sender_id = in.readInt();
		image = in.readString();
		state = in.readInt();
	}

	public static final Creator<Notification> CREATOR = new Creator<Notification>() {

		@Override
		public Notification createFromParcel(Parcel source) {
			return new Notification(source);
		}

		@Override
		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};
}
