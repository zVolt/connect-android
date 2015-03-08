package in.siet.secure.dao;

import android.provider.BaseColumns;

public final class DbStructure {
	public DbStructure() {
	}

	public static final String COLUMN_INCOMMING_ID = "id"; // column name of id
															// in mysql server

	public static abstract class UserTable implements BaseColumns {
		public static final String TABLE_NAME = "user";

		public static final String COLUMN_LOGIN_ID = "login_id";
		public static final String COLUMN_FNAME = "f_name";
		public static final String COLUMN_LNAME = "l_name";
		// public static final String COLUMN_DEPARTMENT = "department";
		public static final String COLUMN_PROFILE_PIC = "pic_url";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_LOGIN_ID + DbConstants.TYPE_TEXT
				+ DbConstants.UNIQUE + DbConstants.COMMA + COLUMN_FNAME
				+ DbConstants.TYPE_TEXT + DbConstants.COMMA + COLUMN_LNAME
				+ DbConstants.TYPE_TEXT + DbConstants.COMMA
				+ COLUMN_PROFILE_PIC + DbConstants.TYPE_TEXT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class MessageTable implements BaseColumns {
		public static final String TABLE_NAME = "messages";

		public static final String COLUMN_SENDER = "sender";
		public static final String COLUMN_RECEIVER = "receiver";
		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_IS_GRP_MSG = "is_group_msg";
		public static final String COLUMN_TIME = "time";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_TEXT + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_SENDER + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_RECEIVER + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_IS_GRP_MSG + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_TIME + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class NotificationTable implements BaseColumns {
		public static final String TABLE_NAME = "notification";

		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_SUBJECT = "subject";
		public static final String COLUMN_TIME = "time";
		public static final String COLUMN_SENDER = "sender";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_TARGET = "target";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_TEXT + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_SENDER + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_SUBJECT + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_TARGET + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_TIME + DbConstants.TYPE_TEXT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class StudentContactsTable implements BaseColumns {
		public static final String TABLE_NAME = "student";

		// public static final String COLUMN_YEAR = "year";
		public static final String COLUMN_SECTION_ID = "section_id";
		public static final String COLUMN_USER_ID = "user_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_USER_ID + DbConstants.TYPE_INT
				+ DbConstants.COMMA
				+
				// COLUMN_YEAR+DbConstants.TYPE_INT+DbConstants.COMMA+
				COLUMN_SECTION_ID + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class FcultyContactsTable implements BaseColumns {
		public static final String TABLE_NAME = "faculty";

		// public static final String COLUMN_MOB = "mobile_no";
		public static final String COLUMN_USER_ID = "user_id";
		public static final String COLUMN_BRANCH_ID = "branch_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_USER_ID + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_BRANCH_ID + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class FileTable implements BaseColumns {
		public static final String TABLE_NAME = "files";

		public static final String COLUMN_URL = "url";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_SENDER = "sender";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_NAME + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_URL + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_SENDER + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class FileNotificationMapTable {
		public static final String TABLE_NAME = "file_notification_map";

		public static final String COLUMN_NOTIFICATION_ID = "notification_id";
		public static final String COLUMN_FILE_ID = "file_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + COLUMN_NOTIFICATION_ID
				+ DbConstants.TYPE_INT + DbConstants.COMMA + COLUMN_FILE_ID
				+ DbConstants.TYPE_INT + DbConstants.BRACES_CLOSE
				+ DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static interface FileMessageMapTable {
		public static final String TABLE_NAME = "file_message_map";

		public static final String COLUMN_MESSAGE_ID = "message_id";
		public static final String COLUMN_FILE_ID = "file_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + COLUMN_MESSAGE_ID
				+ DbConstants.TYPE_INT + DbConstants.COMMA + COLUMN_FILE_ID
				+ DbConstants.TYPE_INT + DbConstants.BRACES_CLOSE
				+ DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class Courses implements BaseColumns {
		public static final String TABLE_NAME = "courses";

		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_DURATION = "duration";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_NAME + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_DURATION + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class Sections implements BaseColumns {
		public static final String TABLE_NAME = "sections";

		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_YEAR_ID = "year_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_NAME + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_YEAR_ID + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class Branches implements BaseColumns {
		public static final String TABLE_NAME = "branches";

		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_COURSE_ID = "course_id";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_NAME + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_COURSE_ID + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class Year implements BaseColumns {
		public static final String TABLE_NAME = "year";

		public static final String COLUMN_BRANCH_ID = "branch_id";
		public static final String COLUMN_YEAR = "year";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_BRANCH_ID + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_YEAR + DbConstants.TYPE_INT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class UserInfoTable implements BaseColumns {
		public static final String TABLE_NAME = "user_info";

		public static final String COLUMN_USER_ID = "user_id"; // fk of
																// users._id
		public static final String COLUMN_STREET = "street";
		public static final String COLUMN_CITY = "city";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_PIN = "pin";
		public static final String COLUMN_P_MOB = "p_mob";
		public static final String COLUMN_H_MOB = "h_mob";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_USER_ID + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_STREET + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_CITY + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_STATE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_PIN + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_P_MOB + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_H_MOB + DbConstants.TYPE_TEXT
				+ DbConstants.BRACES_CLOSE + DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}

	public static abstract class UserMapper implements BaseColumns {
		public static final String TABLE_NAME = "user_mapper";

		public static final String COLUMN_COURSE = "course";
		public static final String COLUMN_BRANCH = "branch";
		public static final String COLUMN_YEAR = "year";
		public static final String COLUMN_SECTION = "section";

		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE
				+ TABLE_NAME + DbConstants.BRACES_OPEN + _ID
				+ DbConstants.TYPE_INT + DbConstants.CONSTRAIN_PRIMARY_KEY
				+ DbConstants.COMMA + COLUMN_COURSE + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_BRANCH + DbConstants.TYPE_TEXT
				+ DbConstants.COMMA + COLUMN_YEAR + DbConstants.TYPE_INT
				+ DbConstants.COMMA + COLUMN_SECTION + DbConstants.TYPE_TEXT
				+ DbConstants.BRACES_CLOSE;

		public static final String COMMAND_DROP = DbConstants.DROP_TABLE
				+ TABLE_NAME + DbConstants.SEMICOLON;
	}
}
