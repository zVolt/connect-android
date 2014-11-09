package in.siet.secure.contants;

import android.os.Environment;

public class Constants {
	public static String SERVER="192.168.0.100";
	public static final String PORT="8080";
	//public static final String SOCKET=SERVER+":"+PORT;
	public static final String SPACE=" ";
	public static final String COLON=":";
	public static final String pathToApp=Environment.getExternalStorageDirectory().getPath()+"/in.secure.siet.sgi/download/";
	public interface DrawerIDs{
		public static int NOTIFICATION=0;
		public static int INTERACTION=1;
		public static int ADD_USER=2;
		public static int SETTING=3;
		public static int CREATE_NOTICE=4;
		public static int TRIGGER=5;
	}
}


