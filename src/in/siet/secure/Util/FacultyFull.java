package in.siet.secure.Util;

public class FacultyFull extends Faculty {
	public String street;
	public String city;
	public String state;
	public String pin;
	public String p_mob;
	public String h_mob;

	public FacultyFull(String f_name_, String l_name_, String branch_,
			String picUrl_, String user_id_, String street_, String city_,
			String state_, String pin_, String p_mob_, String h_mob_) {
		super(f_name_, l_name_, branch_, picUrl_, user_id_);
		street = street_;
		city = city_;
		state = state_;
		pin = pin_;
		p_mob = p_mob_;
		h_mob = h_mob_;
	}

	public FacultyFull() {
		super();
	}

	@Override
	public String toString() {
		String COMMA = ",";

		return "[" + street + COMMA + city + COMMA + state + COMMA + pin
				+ COMMA + p_mob + COMMA + h_mob + super.toString() + "]";
	}
}
