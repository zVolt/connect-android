package in.siet.secure.sgi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class Clientapi{
	public static final String TAG="Clientapi";
	Socket socket=null;
	PrintWriter out=null;
	BufferedReader in=null;
	public Clientapi(){
		try{
			new set_network().execute("");
//			Toast.makeText(this, "All set", Toast.LENGTH_SHORT).show();
			Log.d(TAG+" Contruuctor","All set");
		}
		catch(Exception ex){
			Log.d(TAG+" Contruuctor Catch","All not set");
			ex.printStackTrace();
		}
	}
	public class set_network extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			try{
			socket=new Socket("192.168.0.100",3333);
			out=new PrintWriter(socket.getOutputStream());
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			Log.d(TAG+" set_network","All set");
			return null;
			}catch(Exception ex){
				Log.d(TAG+" set_network catch","All not set");
				return null;
			}
			
		}
		
	}
	public boolean sendMessage(String msgText){
		try{
			new send_msg().execute(msgText);
			Log.d(TAG+" SendMessage","sent");
			return true;
		}
		catch(Exception ex){
			Log.d(TAG+" sendMeaages catch",ex.getLocalizedMessage());
			return false;
		}
	}
	
	public String getMessages(){
		try{
			AsyncTask<String,Integer,String> askt=new get_msg();//.execute("");
			askt.execute("");
			return askt.get();
		}catch(Exception ex){
			Log.d(TAG+" getMessage catch",ex.getLocalizedMessage());
			ex.printStackTrace();
			return null;
		}
	}
	public class send_msg extends AsyncTask<String,Integer,Boolean>{

		@Override
		protected Boolean doInBackground(String... msg) {
			try{
				out.println(msg[0]);
				out.flush();
				Log.d(TAG+" send_msg ","send done");
				return true;
			}catch(Exception ex){
				Log.d(TAG+" send_msg catch",ex.getLocalizedMessage());
				return false;
			}
		}
		
	}
	public class get_msg extends AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... msg) {
			try {
				String str=in.readLine();
				return str;
			} catch (IOException e) {
				Log.d(TAG+" get_msg catch",e.getLocalizedMessage());
				return null;
			}
		}
		
	}
}
