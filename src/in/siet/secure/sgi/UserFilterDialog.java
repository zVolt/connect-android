package in.siet.secure.sgi;

import in.siet.secure.Util.FilterOptions;
import in.siet.secure.Util.Utility;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class UserFilterDialog extends DialogFragment{
	private static final String TAG="in.siet.secure.sgi.UserCategoryDialog";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		LayoutInflater inflater=(LayoutInflater) getActivity().getLayoutInflater();
		AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
		final View dialog=inflater.inflate(R.layout.filter_dialog, null);
		
		final RadioGroup radiogruop=(RadioGroup)dialog.findViewById(R.id.dialogFilterRadioGroup);
		radiogruop.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				if(id==R.id.dialogFilterRadioFaculty){
					dialog.findViewById(R.id.dialogFilterSpinnerYear).setEnabled(false);
				}
				else{
					dialog.findViewById(R.id.dialogFilterSpinnerYear).setEnabled(true);
				}
				
			}
			
		});
		
		final Spinner yearSpinner=(Spinner)dialog.findViewById(R.id.dialogFilterSpinnerYear);
		ArrayAdapter<CharSequence> adapterYear=ArrayAdapter.createFromResource(getActivity(), R.array.array_year,android.R.layout.simple_spinner_item);
		adapterYear.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		yearSpinner.setAdapter(adapterYear);

		final Spinner departSpinner=(Spinner)dialog.findViewById(R.id.dialogFilterSpinnerDepartment);
		ArrayAdapter<CharSequence> adapterDepart=ArrayAdapter.createFromResource(getActivity(), R.array.array_department, android.R.layout.simple_spinner_item);
		adapterDepart.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
		departSpinner.setAdapter(adapterDepart);
		builder.setView(dialog)
		.setTitle(getString(R.string.filter_title))
		
		.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FilterOptions.STUDENT=((RadioButton)radiogruop.findViewById(R.id.dialogFilterRadioStudent)).isChecked();
				if(FilterOptions.STUDENT){
					FilterOptions.YEAR=yearSpinner.getSelectedItemPosition();
				}
				FilterOptions.DEPARTMENT=getResources().getStringArray(R.array.array_department)[departSpinner.getSelectedItemPosition()];
				
				Fragment fragment=getFragmentManager().findFragmentByTag(TAG+"FragmentUsers");
				if(fragment==null){
					fragment=new FragmentUsers();
					getFragmentManager()
					.beginTransaction()
					.setTransitionStyle(R.anim.abc_fade_out)
					.replace(R.id.mainFrame, fragment, TAG+"FragmentUsers")
					.commit();
				}
				else{
					((FragmentUsers)fragment).load();
				}
				
				Utility.RaiseToast(getActivity(), FilterOptions.STUDENT+" "+FilterOptions.YEAR+" "+FilterOptions.DEPARTMENT, false);
			}
		})
		.setNegativeButton(R.string.cancle,new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserFilterDialog.this.getDialog().cancel();
				
			}
		});
		
	/*	builder.setItems(R.array.array_user_type, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//String[] type=getActivity().getResources().getStringArray(R.array.userTypeArray);
				//userType=which;
				Fragment fragment=getFragmentManager().findFragmentByTag(TAG+"FragmentUsers"+which);
				if(fragment==null){
					fragment=new FragmentUsers();
					FilterOptions.USER_TYPE=which;
					getFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment,TAG+"FragmentUsers"+which).commit();
				}
				else{
					FilterOptions.USER_TYPE=which;
					getFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment).commit();
				}
				//((FragmentUsers)fragment).fetch_all();
				//Utility.RaiseToast(getActivity(), ""+which, 0);
			}
		});
*/		return builder.create();
	}
}
