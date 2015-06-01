package com.skpsash.youshare;

/**
 * Author :Raj Amal
 * Email  :raj.amalw@learn2crack.com
 * Website:www.learn2crack.com
**/

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skpsash.youshare.adapter.DatabaseHandler;
import com.skpsash.youshare.adapter.UserFunctions;

public class ChangePasswordActivity extends Activity {

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";



    EditText newpass;
    Button changepass;
    Button cancel;
    TextView tv;
    private static Pattern pattern;
	private static Matcher matcher;
	private static final String PASSWORD_PATTERN = "(?-imsx:^(?=[\\w@0&*.]{6,15})(?:.*[@0&*._]+.*)$)";



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_changepassword);
        
        tv = (TextView) findViewById(R.id.resetPass11);
        SpannableString content = new SpannableString("Password Change");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);
        cancel = (Button) findViewById(R.id.bktohome);
        cancel.setOnClickListener(new View.OnClickListener(){
        public void onClick(View arg0){

                Intent login = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(login);
                finish();
            }

        });



        newpass = (EditText) findViewById(R.id.chPass);
        changepass = (Button) findViewById(R.id.changePass);

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            	
		            	if(isValidPassword(newpass.getText().toString())){
		            		new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
	        		        .setTitle("Change Password")
	        		        .setMessage("Are You Sure You want to Change Your Password?")
	        		        .setNegativeButton("No", null)
	        		        .setPositiveButton("Yes", new OnClickListener() {

	        		            public void onClick(DialogInterface arg0, int arg1) {
	        		            	NetAsync(view);		        		            	
	        		            }		        
	        		        }).create().show();
		            	}
		            	else{
		            		new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
					        .setTitle("Invalid Password")
					        .setMessage("Password should be of minimum 6 and maximum 15 characters.It should contain only a-z, A-Z, 0-9, '_', '@', '&', '*', '.' and MUST CONTAIN AT LEAST ONE of these mentioned special characters")
					         .setPositiveButton("Ok", null)
					        .create().show();
		            	}
		     
            	
            }
        });}
    
    public final static boolean isValidPassword(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
			pattern = Pattern.compile(PASSWORD_PATTERN);  
			 matcher = pattern.matcher(target);
			 return matcher.matches();
		  }
		}

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com/");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
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
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
//                alert.setText("Error in Network Connection");
                new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
		        .setTitle("Network Error")
		        .setMessage("There is some error in the network.Make sure you have internet access in your device.Try again later if problem persists.")
		        .setPositiveButton("Ok", null)
		        .create().show();
            }
        }
    }

    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String newpas,email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> user = new HashMap<String, String>();
            user = db.getUserDetails();




            newpas = newpass.getText().toString();
            email = user.get("email");

            pDialog = new ProgressDialog(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.chgPass(newpas, email);
            Log.d("Button", "Register");
            return json;


        }


        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);


                    if (Integer.parseInt(res) == 1) {
                        /**
                         * Dismiss the process dialog
                         **/
                        pDialog.dismiss();
//                        alert.setText("Your Password is successfully changed.");
                        new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
    			        .setTitle("Password Changed")
    			        .setMessage("Password changed successfully.A confirmation mail has also been sent to your registered Email id.From now onwards use this new password to login.")
    			         .setPositiveButton("Ok", null)
    			        .create().show();


                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
//                        alert.setText("Invalid old Password.");
                        new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
    			        .setTitle("Error")
    			        .setMessage("Your request can't be processed.")
    			         .setPositiveButton("Ok", null)
    			        .create().show();
                    } else {
                        pDialog.dismiss();
//                        alert.setText("Error occured in changing Password.");
                        new AlertDialog.Builder(ChangePasswordActivity.this,AlertDialog.THEME_HOLO_DARK)
    			        .setTitle("Error")
    			        .setMessage("Your request can't be processed at this moment.")
    			         .setPositiveButton("Ok", null)
    			        .create().show();
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();


            }

        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }}






















