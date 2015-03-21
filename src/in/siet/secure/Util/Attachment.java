package in.siet.secure.Util;

import android.os.Parcel;
import android.os.Parcelable;

public class Attachment implements Parcelable {

	public String name;
	public String url;
	/**
	 * size in Bytes
	 */
	public long size;
	public int state;
	public int id;

	public Attachment(int id_, String name_, int state_, String url_) {
		id = id_;
		name = name_;
		state = state_;
		url = url_;
	}

	public Attachment(Parcel in) {
		name = in.readString();
		url = in.readString();
		size = in.readLong();
		state = in.readInt();
		id = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(url);
		dest.writeLong(size);
		dest.writeInt(state);
		dest.writeInt(id);
	}

	public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {

		@Override
		public Attachment createFromParcel(Parcel source) {
			return new Attachment(source);
		}

		@Override
		public Attachment[] newArray(int size) {
			return new Attachment[size];
		}
	};
}
