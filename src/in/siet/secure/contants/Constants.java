package in.siet.secure.contants;

import in.siet.secure.sgi.R;
import android.os.Environment;

public class Constants {

	// elephant Notation
	public static String SERVER = "192.168.0.100";
	public static final String PORT = "8080";
	public static final String COLON = ":";
	// public static final String SOCKET=SERVER+COLON+PORT; //to allow user
	// dynamic server ip change
	public static final String SPACE = " ";

	public static final String pathToApp = Environment
			.getExternalStorageDirectory().getPath()
			+ "/in.secure.siet.sgi/download/";

	public interface DrawerIDs {
		public static int NOTIFICATION = 0;
		public static int INTERACTION = 1;
		public static int ADD_USER = 2;
		public static int CREATE_NOTICE = 3;
		public static int TRIGGER = 4;
	}

	public interface DrawerIconsInactive {
		public static int NOTIFICATION = R.drawable.ic_notifications_inactive;
		public static int INTERACTION = R.drawable.ic_chat_inactive;
		public static int ADD_USER = R.drawable.ic_adduser_inactive;
		public static int CREATE_NOTICE = R.drawable.ic_newnotice_inactive;
		public static int TRIGGER = R.drawable.ic_notifications_inactive;
	}

	public interface DrawerIconsActive {
		public static int NOTIFICATION = R.drawable.ic_notifications_active;
		public static int INTERACTION = R.drawable.ic_chat_active;
		public static int ADD_USER = R.drawable.ic_adduser_active;
		public static int CREATE_NOTICE = R.drawable.ic_newnotice_active;
		public static int TRIGGER = R.drawable.ic_notifications_active;
	}

	public static final String pref_file_name = "in.siet.secure.preference_file";

	public interface PreferenceKeys {
		public static String user_id = "UserId";
		public static String encripted_user_id = "EncriptedUserId";
		public static String token = "Token";
		public static String is_faculty = "IsFaculty";
		public static String logged_in = "LoggedIn";
		public static String f_name = "FirstName";
		public static String l_name = "LastName";
		public static String pic_url = "PicUrl";
		public static String is_student = "IsStudent";
		public static String branch = "Branch";
		public static String course = "Course";
		public static String section = "Section";
		public static String year = "Year";
		public static String db_id = "DbId";
	}

	public interface QueryParameters {
		String PASSWORD = "password";
		String USERNAME = "username";
		String QUERY_ID = "query_id";
		String TOKEN = "token";
		String USER_TYPE = "user_type";
		String BRANCH = "branch";
		String COURSE = "course";
		String SECTION = "section";
		String YEAR = "year";
		String LOGIN_ID = "login_id";
		String IS_FACULTY = "is_faculty";
		String MSGIDS = "message_ids";
		String MESSAGES = "messages";
		String GET_DETAILS_OF_USER_ID = "get_details_of_user";

		public interface Notification {
			public static String SUBJECT = "subject";
			public static String BODY = "body";
			public static String TIME = "time";
		}

	}


	public static interface MsgState {
		int RECEIVED = 0;
		int SENT_SUCESSFULLY = 1;
		int TO_SEND = 2;
	}

	public static interface JSONMEssageKeys {
		public static String ID = "Id";
		public static String MESSAGE = "Message";
		public static String RECEIVER = "Receiver";
		public static String TEXT = "Text";
		public static String SENDER = "Sender";
		public static String IS_GROUP_MESSAGE = "is_group_msg";
		public static String TIME = "Time";
	}

	public static interface JSONKeys {
		String FIRST_NAME = "FirstName";
		String LAST_NAME = "LastName";
		String PROFILE_IMAGE = "ProfileImage";
		String L_ID = "LoginId";
		String USER_ID = "UserId";
		public static String BRANCH = "Branch";
		public static String STATE = "State";
		public static String YEAR = "Year";
		public static String SECTION = "Section";
		public static String COURSE = "Course";
		public static String ROLL_NO = "RollNo";
		public static String CITY = "City";
		public static String PIN = "PIN";
		public static String P_MOB = "PMob";
		public static String H_MOB = "HMob";
		public static String Error = "Error";
		public static String STREET = "Street";
		public static String TOKEN = "Token";

		public static String STATUS = "Status";
		public static String TAG = "Tag";

		public static interface TAG_MSGS {
			public static String LOGIN = "Login";
		}
	}
}
