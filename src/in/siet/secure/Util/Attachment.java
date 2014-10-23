package in.siet.secure.Util;

public class Attachment {
	public static String NAME="Name";
	public static String SIZE="Size";
	public static String TYPE="Type";
	public static String URL="Url";
	
	public String name;
	public String url;
	public String size_mb;
	public String type;
	public Attachment(String n,String s,String t,String u) {
		name=n;
		size_mb=s;
		type=t;
		url=u;
	}
}
