package in.siet.secure.dao;

import android.provider.BaseColumns;

public final class DbStructure {
	public DbStructure(){}
	
	public static abstract class MessageTable implements BaseColumns{
		public static final String TABLE_NAME = "messages";
		public static final String COLUMN_SENDER = "sender";
		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_STATE = "state";
		public static final String COLUMN_IS_GRP_MSG = "is_group_msg";
		public static final String COLUMN_TIME = "time";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA+COLUMN_TEXT+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_SENDER+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_STATE+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_IS_GRP_MSG+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_TIME+DbConstants.TYPE_TEXT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class NotificationTable implements BaseColumns{
		public static final String TABLE_NAME = "notification";
		public static final String COLUMN_TEXT = "text";
		public static final String COLUMN_SENDER = "sender";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA+COLUMN_TEXT+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_SENDER+DbConstants.TYPE_INT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class StudentContactsTable implements BaseColumns{
		public static final String TABLE_NAME = "students";
		public static final String COLUMN_FNAME = "f_name";
		public static final String COLUMN_LNAME = "l_name";
		public static final String COLUMN_BRANCH = "branch";
		public static final String COLUMN_YEAR = "year";
		public static final String COLUMN_SECTION = "section";
		public static final String COLUMN_PROFILE_PIC_ID = "pic_file_id";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA+COLUMN_FNAME+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_LNAME+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_BRANCH+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_YEAR+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_SECTION+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_PROFILE_PIC_ID+DbConstants.TYPE_TEXT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE =DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class FcultyContactsTable implements BaseColumns{
		public static final String TABLE_NAME = "faculty";
		public static final String COLUMN_FNAME = "f_name";
		public static final String COLUMN_LNAME = "l_name";
		public static final String COLUMN_DEPARTMENT = "department";
		public static final String COLUMN_MOB = "mobile_no";
		public static final String COLUMN_PROFILE_PIC_ID = "pic_file_id";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA+COLUMN_FNAME+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_LNAME+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_DEPARTMENT+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_MOB+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_PROFILE_PIC_ID+DbConstants.TYPE_TEXT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class FileTable implements BaseColumns{
		public static final String TABLE_NAME = "files";
		public static final String COLUMN_PATH = "path";
		public static final String COLUMN_SENDER = "sender";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+_ID+DbConstants.TYPE_INT+DbConstants.CONSTRAIN_PRIMARY_KEY+DbConstants.COMMA+COLUMN_PATH+DbConstants.TYPE_TEXT+DbConstants.COMMA+COLUMN_SENDER+DbConstants.TYPE_INT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class FileNotificationMapTable{
		public static final String TABLE_NAME = "file_notification_map";
		public static final String COLUMN_NOTIFICATION_ID = "notification_id";
		public static final String COLUMN_FILE_ID = "file_id";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+COLUMN_NOTIFICATION_ID+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_FILE_ID+DbConstants.TYPE_INT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
	public static abstract class FileMessageMapTable{
		public static final String TABLE_NAME = "file_message_map";
		public static final String COLUMN_MESSAGE_ID = "message_id";
		public static final String COLUMN_FILE_ID = "file_id";
		
		public static final String COMMAND_CREATE = DbConstants.CREATE_TABLE+TABLE_NAME+DbConstants.BRACES_OPEN+COLUMN_MESSAGE_ID+DbConstants.TYPE_INT+DbConstants.COMMA+COLUMN_FILE_ID+DbConstants.TYPE_INT+DbConstants.BRACES_CLOSE+DbConstants.SEMICOLON;
		public static final String COMMAND_TRUNCATE = DbConstants.TRUNCATE_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
		public static final String COMMAND_DROP = DbConstants.DROP_TABLE+TABLE_NAME+DbConstants.SEMICOLON;
	}
}
