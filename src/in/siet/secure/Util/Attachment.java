package in.siet.secure.Util;


public class Attachment {
	public static String NAME="Name";
	public static String SIZE="Size";
	public static String TYPE="Type";
	public static String URL="Url";
	public static String ID="id";
	
	public String name;
	public String url;
	public String size_mb;
	public int state;
	public int id;
	public Attachment(int i,String n,int s,String u) {
		id=i;
		name=n;
		state=s;
		url=u;
	}
}
