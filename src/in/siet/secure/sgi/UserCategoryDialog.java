package in.siet.secure.sgi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

public class UserCategoryDialog extends DialogFragment{
	private static String TAG="in.siet.secure.sgi.UserCategoryDialog";
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.userType)
		.setItems(R.array.userTypeArray, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//String[] type=getActivity().getResources().getStringArray(R.array.userTypeArray);
				//userType=which;
				Fragment fragment=getFragmentManager().findFragmentByTag(TAG+"FragmentUsers"+which);
				if(fragment==null){
					fragment=new FragmentUsers();
					((FragmentUsers)fragment).userType=which;
					getFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment,TAG+"FragmentUsers"+which).commit();
				}
				else{
					((FragmentUsers)fragment).userType=which;
					getFragmentManager().beginTransaction().replace(R.id.mainFrame,fragment).commit();
				}
				//((FragmentUsers)fragment).fetch_all();
				Utility.RaiseToast(getActivity(), ""+which, 0);
			}
		});
		return builder.create();
	}
}