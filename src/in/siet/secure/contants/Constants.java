package in.siet.secure.contants;

import in.siet.secure.sgi.R;
import android.os.Environment;

public interface Constants {

	public String PORT = "8080";
	public String COLON = ":";
	// public static final String SOCKET=SERVER+COLON+PORT; //to allow user
	// dynamic server ip change
	public String SPACE = " ";
	public String NEW_LINE = "\n";
	// public float INTERVAL_IN_HOUR = 1;
	public int HOUR_TO_MILISEC = 360000;
	/**
	 * INTEGER VARIABLE TO DEFINE THE ID OF NOTIFICAITON TO MESSAGE
	 */
	public int notification_msg_id = 1;
	public String pathToApp = Environment.getExternalStorageDirectory()
			.getPath() + "/in.secure.siet.sgi/download/";

	public interface DRAWER_ID {
		int NOTIFICATION = 0;
		int INTERACTION = 1;
		int ADD_USER = 2;
		int CREATE_NOTICE = 3;
	}

	public interface INTENT_EXTRA {
		String NOTIFICATION_ID = "notification_id";
		String BUNDLE_NAME = "extra_bundle";
		String ATTACHMENTS_DATA = "attachments_data";
		String HAS_ATTACHMENTS = "has_attachments";
	}

	public interface ATTACHMENTS {
		String NAME = "Name";
		String SIZE = "Size";
		String TYPE = "Type";
		String URL = "Url";
		String ID = "id";
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

	public String pref_file_name = "in.siet.secure.preference_file";

	public interface LOCAL_INTENT_ACTION {
		String RELOAD_NOTIFICATIONS = "reload_notifications";
		String RELOAD_MESSAGES = "reload_messages";
		String RELOAD_CONTACTS = "reload_contacts";
		String RELOAD_ATTACHMENTS = "reload_attachments";
	}

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
		String UPDATE_INTERVAL = "update_interval";
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

	/**
	 * <b>Files:</b>
	 * <ul>
	 * <li>user uploads files goes through following states PENDING(inserted to
	 * db)->SENT(uploaded to server)->ACK_RECEIVED(upload confirmed by server)</li>
	 * <li>
	 * user receiving new files goes through following states RECEIVED(only file
	 * listed in database)->DOWNLOADED(saved to local)->ACK_SEND(Acknowledged
	 * server that we successfully received the file)</li>
	 * </ul>
	 * <b> Messages:</b>
	 * <ul>
	 * <li>user create messages goes through following states
	 * PENDING->SENT->ACK_RECEIVED</li>
	 * <li>user receiving a message will go through following states
	 * RECEIVED->ACK_SEND</li>
	 * </ul>
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	public interface STATE {

		/**
		 * this means that the message or notification is received from server
		 * i.e, not created by user
		 */
		int RECEIVED = 0;

		/**
		 * this means that the message or notification has been send to server
		 * but no acknowledgment is received (PENDING->SENT)
		 */
		int SENT = 1;

		/**
		 * this means the message or notification is not yet send to server
		 * (PENDING->SENT)
		 */
		int PENDING = 2;

		/**
		 * equivalent to DOWNLOADED in case of files
		 * 
		 * this comes after RECEIVED state it means the notification or message
		 * has been received from server and acknowledgment has been send to
		 * server (RECEIVED->ACK_SEND)
		 */
		int ACK_SEND = 3;

		/**
		 * this comes after SENT state it means the pending message or
		 * notification has been sent to server and the server send
		 * acknowledgment about the same (PENDING->SENT->ACK_RECEIVED)
		 */
		int ACK_RECEIVED = 4;

		int DOWNLOADED = 1;
	}

	public interface IS_GROUP_MSG {
		int YES = 0;
		int NO = 1;
	}
	public interface FOR_FACULTY {
		int YES = 0;
		int NO = 1;
	}
	public interface JSONKEYS {

		String STUDENT = "Student";
		String FACULTY = "Faculty";

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

		public interface TAG_MSGS {
			String LOGIN = "Login";
		}

		public interface MESSAGES {
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
			String FOR_FACULTY = "For_Faculty";
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
