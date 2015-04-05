package in.siet.secure.contants;

import in.siet.secure.sgi.R;
import android.os.Environment;

public interface Constants {
	/**
	 * breaking the url so that the user(developer) can change the ip address of
	 * the server
	 */
	// public String PORT = "8080";
	// public String COLON = ":";

	public String SPACE = " ";
	public String NEW_LINE = "\n";
	public int HOUR_TO_MILISEC_FACTOR = 360000;
	/**
	 * INTEGER VARIABLE TO DEFINE THE ID OF NOTIFICAITON TO MESSAGE
	 */
	public int MSG_NOTI_ID = 1;
	public int NOTI_NOTI_ID = 2;
	public String PATH_TO_APP = Environment.getExternalStorageDirectory()
			.getPath() + "/in.secure.siet.sgi/download/";
	public String PREF_FILE_NAME = "in.siet.secure.preference_file";

	/**
	 * Constant IDs for notification drawer
	 * 
	 * @author Zeeshan Khan
	 * 
	 */
	public interface DRAWER_ID {

		/**
		 * Constant value for notification drawer
		 */
		int NOTIFICATION = 0;

		/**
		 * Constant value for interaction drawer
		 */
		int INTERACTION = 1;

		/**
		 * Constant value for users drawer
		 */
		int ADD_USER = 2;

		/**
		 * Constant value for new notification drawer
		 */
		int CREATE_NOTICE = 3;
	}

	public interface INTENT_EXTRA {
		String NOTIFICATION_ID = "noti_id";
		String BUNDLE_NAME = "extra_bundle";
		String ATTACHMENTS_DATA = "attachments_data";
		String HAS_ATTACHMENTS = "has_attachments";
		String NOTIFICATIONS = "notifications";

		String FRAGMENT_TO_SHOW = "fargemnt_to_show";

		String CHAT_NAME = "name";
		String CHAT_USER_PK = "user_pk";
	}

	public interface BUNDLE_DATA {
		String NOTIFICATION_ID = "not_id";
		String NOTIFICATION_SUBJECT = "Subject";
		String NOTIFICATION_TEXT = "Text";
		String NOTIFICATION_TIME = "Time";
		String NOTIFICATION_IMAGE = "Image";
		String NOTIFICATION_STATE = "State";
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
		String branch = "Branch";
		String course = "Course";
		String section = "Section";
		String year = "Year";
		String db_id = "DbId";

		String UPDATE_INTERVAL = "update_interval";
		String LOCAL_SERVER = "use_local_server";
		String PROPERTY_REG_ID = "RegId";
		String PROPERTY_APP_VERSION = "AppVersion";
		String SERVER_IP = "server_ip";
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
		String FILE = "file";
		String INPUT_STREAM = "file_input_stream";

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
	public interface STATES {

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

		/**
		 * this shows whether the user has read the message/notification or not.
		 * Used to highlight new ones
		 */
		int READ = 6;

	}

	public interface MSG_STATE extends STATES {

	}

	public interface NOTI_STATE extends STATES {

	}

	public interface FILE_STATE extends STATES {
		/**
		 * file specific this means the file is present on local system
		 */
		int DOWNLOADED = 5;
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
		String INITIAL_DATA = "initial_data";
		String USER_DATA = "user_data";

		String COURSES = "Courses";
		String BRANCHES = "Branches";
		String YEARS = "Years";
		String SECTIONS = "Sections";

		String STATUS = "Status";
		String TAG = "Tag";
		String PSWD = "pswd";
		String REG_ID = "reg_id";

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
			String ATTACHMENTS = "attachments";
		}

		public static interface FILES {
			String NAME = "name";
			String URL = "url";
			String SIZE = "size";
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

	/**
	 * GCM Specific contants
	 */
	String EXTRA_MESSAGE = "message";
	String PROPERTY_REG_ID = "registration_id";
	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "381177836120";

}
