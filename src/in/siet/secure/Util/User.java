package in.siet.secure.Util;

public class User {
/*	public static String FIRST_NAME="first_name";
	public static String LAST_NAME="last_name";
	public static String PROFILE_IMAGE="profile_image";
	public static String L_ID="l_id";
	public static String DEPARTMENT="department";
	
	public static String USER_ID="u_id";
	public static String STATE="state";
	public static String YEAR="year";
	public static String SECTION="section";
	public static String COURSE="course";
	public static String MOBILE="mobile";
*/	
	
	public String l_name;
	public String f_name;
	public String dep;
	public String picUrl;
	public int id; //reference id 
	public int year;
	public String section;
	public String course;
	public String user_id;
	
	
	//for student
	public User(String f_n,String l_n,int l_id,String pu,String dp,int y,String sec,String cou){
		f_name=f_n;
		l_name=l_n;
		picUrl=pu;
		id=l_id;
		dep=dp;
		section=sec;
		year=y;
		course=cou;
	}
	//for faculty
	public User(String f_n,String l_n,int l_id,String pu,String dp,String cou){
		f_name=f_n;
		l_name=l_n;
		id=l_id;
		picUrl=pu;
		dep=dp;
		course=cou;
	}
	
}
