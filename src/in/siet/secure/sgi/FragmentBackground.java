package in.siet.secure.sgi;


import in.siet.secure.Util.Utility;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FragmentBackground extends Fragment {
	//BackgroundResultReceiver resultreceiver;
	Button start;
	private ResultReceiver mReceiver;
	public final static String TAG="in.siet.secure.sgi.FragmentBackgound"; 
	// Constructor of fragment
	public FragmentBackground(){
		mReceiver = new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode,Bundle resultData){
				EditText showcount;
				showcount=(EditText)getView().findViewById(R.id.showcount);
				showcount.setText(String.valueOf(resultCode));				
			}						
		};		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_background, container,
				false);	
		setRetainInstance(true);
		start = (Button)rootView.findViewById(R.id.buttonstart);
		start.setOnClickListener(
				new OnClickListener() {
		            @Override
		            public void onClick(View v) {
		            	Intent intent=new Intent(getActivity().getApplicationContext(), BackgroundActivity.class);		
		        		Utility.log("onClick", "onClickButtonStart");
		        		//intent.setData(Uri.parse("Background activity"));
		        		intent.putExtra("count", "0");	
		        		intent.putExtra("receiver", getResultReceiver());
		        		getActivity().startService(intent);  		            	
		            }
		        }
				);
		
		return rootView;		
				
	}
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // retain this fragment
	        setRetainInstance(true);
	    }

	
	public ResultReceiver getResultReceiver() {
        return mReceiver;
    }
	/*	@Override
	public void onStart(){
		super.onStart();
		//onClickButtonStart();
				
	}
		
	public void onClickButtonStart(){//View view){
		Intent intent=new Intent(getActivity().getApplicationContext(), BackgroundActivity.class);		
		Utility.log("onClick", "onClickButtonStart");
		//intent.setData(Uri.parse("Background activity"));
		intent.putExtra("count", "1");	
		intent.putExtra("receiver", getResultReceiver());
		getActivity().startService(intent);
	}

	
	
	

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		EditText showcount;
		showcount=(EditText)getActivity().findViewById(R.id.showcount);
		showcount.setText(resultCode);
	}
	
	
	*/
	
	
}
