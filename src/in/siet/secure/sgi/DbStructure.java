package in.siet.secure.sgi;

import android.provider.BaseColumns;

public final class DbStructure {
	public DbStructure(){}
	public static abstract class MessageTable implements BaseColumns{
		public static final String TABLE_NAME = "messages";
		public static final String COLUMN_SENDER = "sender";
		public static final String COLUMN_RECEIVER = "receiver";
		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_TIME = "time";
	}
}
