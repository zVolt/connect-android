package in.siet.secure.Util;

public class User {
	public static String FIRST_NAME="first_name";
	public static String LAST_NAME="last_name";
	public static String PROFILE_IMAGE="profile_image";
	public static String ID="id";
	public static String DEPARTMENT="department";
	public static String STATE="state";
	public static String YEAR="year";
	public static String SECTION="section";
	public static String MOBILE="mobile";
	
	
	public String l_name;
	public String f_name;
	public String picUrl;
	public String id;
	public String dep;
	public int state;
	
	public int year;
	public int section;
	
	public String mob;
	
	//for student
	public User(String f_n,String l_n,String l_id,String pu,String dp,int y,int sec,int sta){
		f_name=f_n;
		l_name=l_n;
		picUrl=pu;
		id=l_id;
		dep=dp;
		state=sta;
		section=sec;
		year=y;
	}
	//for faculty
	public User(String f_n,String l_n,String l_id,String pu,String dp,int sta,String m){
		f_name=f_n;
		l_name=l_n;
		id=l_id;
		picUrl=pu;
		dep=dp;
		state=sta;
		mob=m;
	}
}
