package com.skpsash.youshare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skpsash.youshare.adapter.DatabaseHandler;
import com.skpsash.youshare.adapter.UserFunctions;
import com.skpsash.youshare.adapter.secureSharedPref;

public class LoginFragment extends Fragment {
	private TextView link_register;
	private Button btn_Login;
	private Button btn_ForgotPass;
	private EditText inputEmail;
    private EditText inputPassword;

    
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_USERNAME = "uname";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_login, container, false);
		link_register = (TextView) rootView.findViewById(R.id.link_to_register);
		btn_Login = (Button) rootView.findViewById(R.id.btnLogin);
		btn_ForgotPass = (Button) rootView.findViewById(R.id.btnForgotPass);
		inputEmail = (EditText) rootView.findViewById(R.id.loginEmail);
		inputPassword = (EditText) rootView.findViewById(R.id.loginPassword);
		
		link_register.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				link_register.setTextColor(getResources().getColor(R.color.brownish));
				Intent intent = new Intent(getActivity(),RegisterActivity.class);
				getActivity().startActivity(intent);				
				getActivity().finish();
				
			}
		});
		
		  btn_ForgotPass.setOnClickListener(new View.OnClickListener() {
			  	@Override
		        public void onClick(View view) {
		        Intent myIntent = new Intent(getActivity(), PasswordResetActivity.class);
		        getActivity().startActivity(myIntent);
		        getActivity().finish();
		        }});
		
		btn_Login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				Intent intent = new Intent(getActivity(),HomeScreenActivity.class);
//				getActivity().startActivity(intent);				
//				getActivity().finish();
				 if (  ( !inputEmail.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
	                {
					 if(isValidEmail(inputEmail.getText().toString())){
	                    NetAsync(view);
					 }
					 else{
						 Toast.makeText(getActivity(),
		                            "Are you sure you entered your Email Id correctly?", Toast.LENGTH_LONG).show();
					 }
	                }
	                else if ( ( !inputEmail.getText().toString().equals("")) )
	                {
	                    Toast.makeText(getActivity(),
	                            "Password field can't be empty", Toast.LENGTH_LONG).show();
	                }
	                else if ( ( !inputPassword.getText().toString().equals("")) )
	                {
	                    Toast.makeText(getActivity(),
	                            "Email field can't be empty", Toast.LENGTH_LONG).show();
	                }
	                else
	                {
	                    Toast.makeText(getActivity(),
	                            "Email and Password fields can't be empty", Toast.LENGTH_LONG).show();
	                }
			}
		});
		
		SharedPreferences settings = getActivity().getSharedPreferences("MysecurePrefs",0);	
        if(settings.getString("encPass", null) != null){
        	Intent upanel = new Intent(getActivity(), HomeScreenActivity.class);
        	startActivity(upanel);
        }
		return rootView;
		
	}
	
	public final static boolean isValidEmail(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
		    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		  }
		}
	
	/**
	 * Async Task to check whether internet connection is working.
	 **/

	    private class NetCheck extends AsyncTask<String,String,Boolean>
	    {
	        private ProgressDialog nDialog;

	        @Override
	        protected void onPreExecute(){
	            super.onPreExecute();
	            nDialog = new ProgressDialog(getActivity(),AlertDialog.THEME_HOLO_DARK);
	            nDialog.setTitle("Checking Network");
	            nDialog.setMessage("Loading..");
	            nDialog.setIndeterminate(false);
	            nDialog.setCancelable(true);
	            nDialog.show();
	        }
	        /**
	         * Gets current device state and checks for working internet connection by trying Google.
	        **/
	        @Override
	        protected Boolean doInBackground(String... args){



	            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo netInfo = cm.getActiveNetworkInfo();
	            if (netInfo != null && netInfo.isConnected()) {
	                try {
	                    URL url = new URL("http://www.google.com/");
	                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	                    urlc.setConnectTimeout(15000);
	                    urlc.connect();
	                    if (urlc.getResponseCode() == 200) {
	                        return true;
	                    }
	                } catch (MalformedURLException e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            return false;

	        }
	        @Override
	        protected void onPostExecute(Boolean th){

	            if(th == true){
	                nDialog.dismiss();
	                new ProcessLogin().execute();
	            }
	            else{
	                nDialog.dismiss();
//	                loginErrorMsg.setText("Error in Network Connection");
	                new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK)
   			        .setTitle("Network Error")
   			        .setMessage("An error in network occured.Make sure you have internet access in your device.Try again later if problem persists")
   			         .setPositiveButton("Ok", null)
   			        .create().show();
	            }
	        }
	    }

	    /**
	     * Async Task to get and send data to My Sql database through JSON respone.
	     **/
	    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


	        private ProgressDialog pDialog;

	        String email,password;

	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();

	            inputEmail = (EditText) getActivity().findViewById(R.id.loginEmail);
	            inputPassword = (EditText) getActivity().findViewById(R.id.loginPassword);
	            email = inputEmail.getText().toString();
	            password = inputPassword.getText().toString();
	            pDialog = new ProgressDialog(getActivity(),AlertDialog.THEME_HOLO_DARK);
	            pDialog.setTitle("Contacting Servers");
	            pDialog.setMessage("Logging in ...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }

	        @Override
	        protected JSONObject doInBackground(String... args) {

	            UserFunctions userFunction = new UserFunctions();
	            JSONObject json = userFunction.loginUser(email, password);
	            return json;
	        }

	        @Override
	        protected void onPostExecute(JSONObject json) {
	        	
	        		
	            try {
	               if (json.getString(KEY_SUCCESS) != null) {

	                    String res = json.getString(KEY_SUCCESS);

	                    if(Integer.parseInt(res) == 1){
	                        pDialog.setMessage("Loading User Space");
	                        pDialog.setTitle("Getting Data");
	                        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
	                        JSONObject json_user = json.getJSONObject("user");
	                        final SharedPreferences prefs = new secureSharedPref( 
	        	        		    getActivity(), getActivity().getSharedPreferences("MysecurePrefs", Context.MODE_PRIVATE) );

	        	        	prefs.edit().putString("encPass",password).commit();
	                        /**
	                         * Clear all previous data in SQlite database.
	                         **/
	                        UserFunctions logout = new UserFunctions();
	                        logout.logoutUser(getActivity().getApplicationContext());
	                        db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
	                       /**
	                        *If JSON array details are stored in SQlite it launches the User Panel.
	                        **/
	                        Intent upanel = new Intent(getActivity().getApplicationContext(), HomeScreenActivity.class);
	                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                        pDialog.dismiss();
	                        startActivity(upanel);
	                        /**
	                         * Close Login Screen
	                         **/
	                        getActivity().finish();
	                    }else{

	                        pDialog.dismiss();
//	                        loginErrorMsg.setText("Incorrect username/password");
	                        new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK)
           			        .setTitle("Incorrect Credentials")
           			        .setMessage("Please make sure you have entered your registered Email Id and correct YouShare password.If you forgot your password then tap on the 'Forgot Password' button.Also make sure if you have activated your account by going through the confirmation email which was sent to you when you first registered.")
           			        .setPositiveButton("Ok", null)
           			        .create().show();
	                    }
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	       }
	    }
	    public void NetAsync(View view){
	        new NetCheck().execute();
	    }
	}
	

