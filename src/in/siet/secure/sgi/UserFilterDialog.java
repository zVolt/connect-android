package in.siet.secure.sgi;

import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.Utility;
import in.siet.secure.contants.Constants;
import in.siet.secure.dao.DbHelper;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class UserFilterDialog extends DialogFragment {
	static final String TAG = "in.siet.secure.sgi.UserCategoryDialog";
	static final String FRAGMENT_TO_OPEN = "fragment_id_to_open";
	private ArrayList<String> course = new ArrayList<String>();
	private ArrayList<String> branch = new ArrayList<String>();
	private ArrayList<String> year = new ArrayList<String>();
	private ArrayList<String> section = new ArrayList<String>();
	private ArrayAdapter<String> adapterYear;
	private ArrayAdapter<String> adapterDepart;
	private ArrayAdapter<String> adapterSection;
	private SQLiteDatabase db;
	Bundle bundle;
	boolean students_selected = true;
	boolean all_department = true;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final View dialog = inflater.inflate(R.layout.filter_dialog, null,
				false);
		final ViewHolder holder = new ViewHolder();

		holder.radio_group = (RadioGroup) dialog
				.findViewById(R.id.dialogFilterRadioGroup);
		holder.section_row = (LinearLayout) dialog
				.findViewById(R.id.dialogFilterSectionRow);
		holder.year_row = (LinearLayout) dialog
				.findViewById(R.id.dialogFilterYearRow);
		holder.sections = (Spinner) dialog
				.findViewById(R.id.dialogFilterSpinnerSection);
		holder.courses = (Spinner) dialog
				.findViewById(R.id.dialogFilterSpinnerCourse);
		holder.branches = (Spinner) dialog
				.findViewById(R.id.dialogFilterSpinnerDepartment);
		holder.years = (Spinner) dialog
				.findViewById(R.id.dialogFilterSpinnerYear);
		db = new DbHelper(getActivity()).getReadableDatabase();

		holder.radio_group
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int id) {
						if (id == R.id.dialogFilterRadioFaculty) {
							students_selected = false;
							holder.year_row.setVisibility(View.GONE);
							holder.section_row.setVisibility(View.GONE);
						} else {
							students_selected = true;
							holder.year_row.setVisibility(View.VISIBLE);
							// holder.section_row.setVisibility(View.VISIBLE);
						}

					}

				});

		setCorses();
		ArrayAdapter<String> courses_adapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, course);
		courses_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		holder.courses.setAdapter(courses_adapter);
		holder.courses.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				// set departments or branches
				if (position == 0) {
					holder.branches.setSelection(0);
					holder.branches.setEnabled(false);
					holder.years.setSelection(0);
					holder.years.setEnabled(false);
				} else {
					// set years and branches
					setYearsAndBranches(course.get(position));
					adapterDepart.notifyDataSetChanged();
					adapterYear.notifyDataSetChanged();
					holder.branches.setEnabled(true);
					holder.years.setEnabled(true);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		adapterYear = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, year);
		adapterYear
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		holder.years.setAdapter(adapterYear);
		holder.years.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				all_department = position == 0 ? true : false;
				if (position != 0 && students_selected
						&& holder.branches.getSelectedItemPosition() != 0) {
					holder.sections.setSelection(0);
					setSections(course.get(holder.courses
							.getSelectedItemPosition()), branch
							.get(holder.branches.getSelectedItemPosition()),
							position);
					adapterSection.notifyDataSetChanged();
					dialog.findViewById(R.id.dialogFilterSectionRow)
							.setVisibility(View.VISIBLE);
				} else {
					dialog.findViewById(R.id.dialogFilterSectionRow)
							.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				return;
			}
		});

		adapterDepart = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, branch);
		adapterDepart
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		holder.branches.setAdapter(adapterDepart);
		holder.branches.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				all_department = position == 0 ? true : false;
				if (position != 0 && students_selected
						&& holder.years.getSelectedItemPosition() != 0) {
					holder.sections.setSelection(0);
					setSections(course.get(holder.courses
							.getSelectedItemPosition()), branch.get(position),
							holder.years.getSelectedItemPosition());
					adapterSection.notifyDataSetChanged();
					dialog.findViewById(R.id.dialogFilterSectionRow)
							.setVisibility(View.VISIBLE);
				} else {
					dialog.findViewById(R.id.dialogFilterSectionRow)
							.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				return;
			}
		});

		adapterSection = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, section);
		adapterSection
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		holder.sections.setAdapter(adapterSection);

		builder.setView(dialog).setTitle(getString(R.string.filter_title))

		.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Utility.log(TAG, "click ok");
				FilterOptions.STUDENT = ((RadioButton) holder.radio_group
						.findViewById(R.id.dialogFilterRadioStudent))
						.isChecked();
				if (FilterOptions.STUDENT) {
					FilterOptions.YEAR = holder.years.getSelectedItemPosition();
					FilterOptions.SECTION = section.get(holder.sections
							.getSelectedItemPosition());
				}
				FilterOptions.BRANCH = branch.get(holder.branches
						.getSelectedItemPosition());
				FilterOptions.COURSE = course.get(holder.courses
						.getSelectedItemPosition());

				bundle = getArguments();
				switch (bundle.getInt(UserFilterDialog.FRAGMENT_TO_OPEN, -1)) {
				case Constants.DRAWER_ID.ADD_USER:
					((MainActivity) getActivity())
							.switch_fragment(Constants.DRAWER_ID.ADD_USER);
					break;
				case Constants.DRAWER_ID.CREATE_NOTICE:
					((MainActivity) getActivity())
							.switch_fragment(Constants.DRAWER_ID.CREATE_NOTICE);
					break;
				case -1:
					Utility.RaiseToast(getActivity(),
							"dont know from where you reached on this dialog",
							true);
					break;
				}
				Utility.RaiseToast(getActivity(), FilterOptions.COURSE
						+ " "
						+ (FilterOptions.STUDENT ? "Students of "
								+ FilterOptions.SECTION
								+ " section "
								+ (FilterOptions.YEAR == 0 ? "All"
										: FilterOptions.YEAR) + " year"
								: "Faculties")
						+ " from "
						+ FilterOptions.BRANCH
						+ " department"
						+ (FilterOptions.BRANCH.equalsIgnoreCase("All") ? "s"
								: ""), true);
			}
		}).setNegativeButton(R.string.cancle, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Utility.log(TAG, "click cancel");
				UserFilterDialog.this.getDialog().cancel();
				Utility.log(TAG, "dismiss");
			}
		});

		return builder.create();
	}

	static class ViewHolder {
		RadioGroup radio_group;
		LinearLayout year_row;
		LinearLayout section_row;
		Spinner years;
		Spinner branches;
		Spinner courses;
		Spinner sections;
	}

	public void setCorses() {
		course.clear();
		course.add("All");
		year.clear();
		year.add("All");
		branch.clear();
		branch.add("All");
		section.clear();
		section.add("All");
		Cursor c = db.rawQuery("select name from courses", null);// DbStructure.COURSES.TABLE_NAME,courses_columns,
																	// null,
																	// null,
																	// null,
																	// null,null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			course.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
	}

	public void setYearsAndBranches(String course_name) {
		branch.clear();
		branch.add("All");
		Cursor c = db
				.rawQuery(
						"select branches.name from branches join courses on course_id=courses._id where courses.name='"
								+ course_name + "'", null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			branch.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		year.clear();
		year.add("All");
		c = db.rawQuery(
				"select distinct year.year from year join branches on branch_id=branches._id join courses on course_id=courses._id where courses.name='"
						+ course_name + "'", null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			year.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
	}

	public void setSections(String course_name, String branch_name, int year) {
		section.clear();
		section.add("All");
		String[] args = { "" + year, branch_name, course_name };
		Cursor c = db
				.rawQuery(
						"select sections.name from sections join year on year_id=year._id join branches on branch_id=branches._id join courses on course_id=courses._id where year.year=? and branches.name=? and courses.name=? ",
						args);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			section.add(c.getString(0));
			c.moveToNext();
		}
		c.close();
		Utility.log(TAG, course_name + " " + branch_name + " " + year);
	}
}
