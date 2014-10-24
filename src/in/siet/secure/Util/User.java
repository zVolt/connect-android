package in.siet.secure.Util;

public class User {
	public String name;
	public String picUrl;
	public String id;
	public String state;
	public int year;
	public String dep;
	public User(String n,String pu,String dp,int y){
		name=n;
		picUrl=pu;
		dep=dp;
		state="offline";
		year=y;
	}
	public User(String n,String pu,String dp){
		name=n;
		picUrl=pu;
		dep=dp;
		state="online";
		year=0;
	}
	
}
