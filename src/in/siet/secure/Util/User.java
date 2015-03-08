package in.siet.secure.Util;

public class User {
	public String f_name;
	public String l_name;

	public String picUrl;
	public String user_id;

	public User(String f_name_, String l_name_, String picUrl_, String user_id_) {
		f_name = f_name_;
		l_name = l_name_;
		picUrl = picUrl_;
		user_id = user_id_;
	}

	public User() {
	}

	@Override
	public String toString() {
		String COMMA = ",";
		return "[" + f_name + COMMA + l_name + COMMA + user_id + "]";
	}
}
