package com.skpsash.youshare;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skpsash.youshare.adapter.UserFunctions;


public class PasswordResetActivity extends Activity {

private static String KEY_SUCCESS = "success";
private static String KEY_ERROR = "error";

  EditText email;
  Button resetpass;
  TextView tv;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        setContentView(R.layout.activity_passwordreset);
        
        tv = (TextView) findViewById(R.id.resetPass11);
        SpannableString content = new SpannableString("Reset Your Password");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);
        Button login = (Button) findViewById(R.id.bktohome);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivity(myIntent);
                finish();
            }

        });


        email = (EditText) findViewById(R.id.chPass);
        resetpass = (Button) findViewById(R.id.changePass);
        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            	
              
		            	if(!email.getText().toString().equals("")){
		            		if(isValidEmail(email.getText().toString())){
		            			new AlertDialog.Builder(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK)
		        		        .setTitle("Reset Password")
		        		        .setMessage("Are You Sure You want to Reset Your Password?")
		        		        .setNegativeButton("No", null)
		        		        .setPositiveButton("Yes", new OnClickListener() {

		        		            public void onClick(DialogInterface arg0, int arg1) {
		        		            	NetAsync(view);		        		            	
		        		            }		        
		        		        }).create().show();
		            		
		            	  }
		            		else{
		            			Toast.makeText(getApplicationContext(),
			                            "Are you sure you entered your Email Id correctly?", Toast.LENGTH_LONG).show();
		            		}
		            	}
		            	
		            	else{
		            		 Toast.makeText(getApplicationContext(),
		                             "Email field can't be empty", Toast.LENGTH_SHORT).show();
		            	}
		    
            	
            	

            }



        });}
    
    @Override
	public void onBackPressed() {
		 this.startActivity(new Intent(PasswordResetActivity.this,MainActivity.class));  
    	 return;  
	}
    
	public final static boolean isValidEmail(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
		    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		  }
		}

    private class NetCheck extends AsyncTask<String,String,Boolean>

                {
                    private ProgressDialog nDialog;

                    @Override
                    protected void onPreExecute(){
                        super.onPreExecute();
                        nDialog = new ProgressDialog(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK);
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
                            new ProcessRegister().execute();
                        }
                        else{
                            nDialog.dismiss();
                            new AlertDialog.Builder(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK)
        			        .setTitle("Network Error")
        			        .setMessage("There is some problme in the network.Please make sure you have access to the internet.Try again later if problem persists.")
        			        .setPositiveButton("Ok", null)
        			        .create().show();
                        }
                    }
                }





                private class ProcessRegister extends AsyncTask<String, String, JSONObject> {


                    private ProgressDialog pDialog;

                    String forgotpassword;
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        forgotpassword = email.getText().toString();

                        pDialog = new ProgressDialog(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK);
                        pDialog.setTitle("Contacting Servers");
                        pDialog.setMessage("Getting Data ...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(true);
                        pDialog.show();
                    }

                    @Override
                    protected JSONObject doInBackground(String... args) {


                        UserFunctions userFunction = new UserFunctions();
                        JSONObject json = userFunction.forPass(forgotpassword);
                        return json;


                    }


                    @Override
                    protected void onPostExecute(JSONObject json) {
                  /**
                   * Checks if the Password Change Process is sucesss
                   **/
                        try {
                            if (json.getString(KEY_SUCCESS) != null) {
                                String res = json.getString(KEY_SUCCESS);
                                String red = json.getString(KEY_ERROR);


                                if(Integer.parseInt(res) == 1){
                                   pDialog.dismiss();
//                                    alert.setText("A recovery email is sent to you, see it for more details.");
                                new AlertDialog.Builder(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK)
               			        .setTitle("Reset Successful")
               			        .setMessage("A recovery email has been sent to your Email Id.Please go through it")
               			         .setPositiveButton("Ok", null)
               			        .create().show();


                                }
                                else if (Integer.parseInt(red) == 2)
                                {    pDialog.dismiss();
//                                    alert.setText("Your email does not exist in our database.");
                                new AlertDialog.Builder(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK)
               			        .setTitle("Reset Failed")
               			        .setMessage("Any user with this email id is not registered with YouShare")
               			         .setPositiveButton("Ok", null)
               			        .create().show();
                                }
                                else {
                                    pDialog.dismiss();
//                                    alert.setText("Error occured in changing Password");
                                    new AlertDialog.Builder(PasswordResetActivity.this,AlertDialog.THEME_HOLO_DARK)
                   			        .setTitle("Reset Failed")
                   			        .setMessage("Some unknown error occured.Please try again later.Sorry for the inconvenience")
                   			         .setPositiveButton("Ok", null)
                   			        .create().show();
                                }




                            }}
                        catch (JSONException e) {
                            e.printStackTrace();


                        }
                    }}
            public void NetAsync(View view){
                new NetCheck().execute();
            }}





