package in.siet.secure.contants;

import android.os.Environment;

public class Constants {
	
	//elephant Notation
	public static String SERVER="192.168.0.100";
	public static final String PORT="8080";
	public static final String COLON=":";
	//public static final String SOCKET=SERVER+COLON+PORT; //to allow user dynamic server ip change
	public static final String SPACE=" ";
	
	public static final String pathToApp=Environment.getExternalStorageDirectory().getPath()+"/in.secure.siet.sgi/download/";
	public interface DrawerIDs{
		public static int NOTIFICATION=0;
		public static int INTERACTION=1;
		public static int ADD_USER=2;
		public static int SETTING=3;
		public static int CREATE_NOTICE=4;
		public static int TRIGGER=5;
	}
	public static final String pref_file_name="in.siet.secure.preference_file";
	public interface PreferenceKeys{
		public static String user_id="UserId";
		public static String token="Token";
		public static String is_faculty="IsFaculty";
		public static String logged_in="LoggedIn";
		public static String f_name="FirstName";
		public static String l_name="LastName";
		public static String pic_url="PicUrl";
		public static String is_student="IsStudent";
		public static String branch="Branch";
		public static String course="Course";
		public static String section="Section";
		public static String year="Year";
		public static String db_id="DbId";
	}
	
	public interface QueryParameters{
		public static String PASSWORD="password";
		public static String USERNAME="username";
		public static String QUERY_ID="query_id";
		public static String TOKEN="token";
		public static String USER_TYPE="user_type";
		public static String BRANCH="branch";
		public static String COURSE="course";
		public static String SECTION="section";
		public static String YEAR="year";
		public static String LOGIN_ID="login_id";
		public static String IS_FACULTY="is_faculty";
		public static String MSGIDS="message_ids";
		public static String MESSAGES="messages";
		
	}
	public static interface MsgState{
		int RECEIVED=0;
		int SENT_SUCESSFULLY=1;
		int TO_SEND=2;
	}
	
	public static interface JSONMEssageKeys{
		public static String ID="Id";
		public static String MESSAGE="Message";
		public static String RECEIVER="Receiver";
		public static String TEXT="Text";
		public static String SENDER="Sender";
		public static String IS_GROUP_MESSAGE="is_group_msg";
		public static String TIME="Time";
	}
	
	public static interface JSONKeys{
		public static String FIRST_NAME="FirstName";
		public static String LAST_NAME="LastName";
		public static String PROFILE_IMAGE="ProfileImage";
		public static String L_ID="LoginId";
		public static String USER_ID="UserId";
		public static String BRANCH="Branch";
		public static String STATE="State";
		public static String YEAR="Year";
		public static String SECTION="Section";
		public static String COURSE="Course";
		public static String ROLL_NO="RollNo";
		public static String CITY="City";
		public static String PIN="PIN";
		public static String P_MOB="PMob";
		public static String H_MOB="HMob";
		public static String Error="Error";
		
		public static String TOKEN="Token";
		
		
		
		public static String STATUS="Status";
		public static String TAG="Tag";
		public static interface TAG_MSGS{
			public static String LOGIN="Login";
		}
	}
}


