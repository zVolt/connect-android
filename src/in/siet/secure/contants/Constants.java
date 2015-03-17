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
	public static final String NEW_LINE = "\n";
	public static float INTERVAL_IN_HOUR = 1;
	public static int HOUR_TO_MILISEC = 360000;
	/**
	 * INTEGER VARIABLE TO DEFINE THE ID OF NOTIFICAITON TO MESSAGE
	 */
	public static final int notification_msg_id = 1;
	public static final String pathToApp = Environment
			.getExternalStorageDirectory().getPath()
			+ "/in.secure.siet.sgi/download/";

	public interface DRAWER_ID {
		int NOTIFICATION = 0;
		int INTERACTION = 1;
		int ADD_USER = 2;
		int CREATE_NOTICE = 3;
	}

	public interface DRAWER_IC_INACTIVE {
		int NOTIFICATION = R.drawable.ic_notifications_inactive;
		int INTERACTION = R.drawable.ic_chat_inactive;
		int ADD_USER = R.drawable.ic_adduser_inactive;
		int CREATE_NOTICE = R.drawable.ic_newnotice_inactive;
	}

	public interface DRAWER_IC_ACTIVE {
		int NOTIFICATION = R.drawable.ic_notifications_active;
		int INTERACTION = R.drawable.ic_chat_active;
		int ADD_USER = R.drawable.ic_adduser_active;
		int CREATE_NOTICE = R.drawable.ic_newnotice_active;
	}

	public static final String pref_file_name = "in.siet.secure.preference_file";

	public interface PREF_KEYS {
		String user_id = "UserId";
		String encripted_user_id = "EncriptedUserId";
		String token = "Token";
		String is_faculty = "IsFaculty";
		String logged_in = "LoggedIn";
		String f_name = "FirstName";
		String l_name = "LastName";
		String pic_url = "PicUrl";
		String is_student = "IsStudent";
		String branch = "Branch";
		String course = "Course";
		String section = "Section";
		String year = "Year";
		String db_id = "DbId";
	}

	public interface QueryParameters {
		String PASSWORD = "password";
		String USERNAME = "username";
		// String QUERY_ID = "query_id";
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
			String SUBJECT = "subject";
			String BODY = "body";
			String TIME = "time";
		}

	}

	public static interface STATE {
		/**
		 * this means that the message or notification is received from server
		 */
		int RECEIVED = 0;
		/**
		 * this means that the message or notification has been send to server
		 * but no acknowledgment is received
		 */
		int SENT = 1;
		/**
		 * this means the message or notification is not yet send to server
		 */
		int PENDING = 2;
		/**
		 * this comes after RECEIVED state it means the notification or message
		 * has been received from server and acknowledgment has been send to
		 * server
		 */
		int ACK_SEND = 3;
		/**
		 * this comes after SENT state it means the pending message or
		 * notification has been sent to server and the server send
		 * acknowledgment about the same
		 */
		int ACK_RECEIVED = 4;
	}

	public static interface IS_GROUP_MSG {
		int YES = 0;
		int NO = 1;
	}

	public static interface JSONKEYS {
		String FIRST_NAME = "FirstName";
		String LAST_NAME = "LastName";
		String PROFILE_IMAGE = "ProfileImage";
		String L_ID = "LoginId";
		String USER_ID = "UserId";
		String BRANCH = "Branch";
		String STATE = "State";
		String YEAR = "Year";
		String SECTION = "Section";
		String COURSE = "Course";
		String ROLL_NO = "RollNo";
		String CITY = "City";
		String PIN = "PIN";
		String P_MOB = "PMob";
		String H_MOB = "HMob";
		String Error = "Error";
		String STREET = "Street";
		String TOKEN = "Token";

		String STATUS = "Status";
		String TAG = "Tag";

		public static interface TAG_MSGS {
			String LOGIN = "Login";
		}

		public static interface MESSAGES {
			String ID = "Id";
			String MESSAGES = "Messages";
			String RECEIVER = "Receiver";
			String TEXT = "Text";
			String SENDER = "Sender";
			String IS_GROUP_MESSAGE = "is_group_msg";
			String TIME = "Time";
			String ACK = "Msg_ack";
		}

		public static interface NOTIFICATIONS {
			String NOTIFICATIONS = "Notifications";
			String ID = "Id";
			String TEXT = "Text";
			String SUBJECT = "Subject";
			String TIME = "Time";
			String COURSE = "Course";
			String BRANCH = "Branch";
			String YEAR = "Year";
			String SENDER = "Sender";
			String SECTION = "Section";
			String ACK = "Noti_ack";
		}
	}

	public static interface NOTIFICATION {
		String SUBJECT = "Subject";
		String TEXT = "Text";
		String TIME = "Time";
		String SENDER_IMAGE = "Image";
		String ID = "ID";
		String ATTACHMENT = "Attachments";
	}
}
