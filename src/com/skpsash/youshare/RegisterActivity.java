package com.skpsash.youshare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.skpsash.youshare.adapter.DatabaseHandler;
import com.skpsash.youshare.adapter.UserFunctions;

public class RegisterActivity extends Activity {
	  /**
     *  JSON Response node names.
     **/

	private static Pattern pattern;
	private static Matcher matcher;
	private static final String USERNAME_PATTERN = "^[a-z0-9_-]{5,15}$";
	private static final String PASSWORD_PATTERN = "(?-imsx:^(?=[\\w@0&*.]{6,15})(?:.*[@0&*._]+.*)$)";
    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_USERNAME = "uname";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    /**
     * Defining layout items.
     **/

    EditText inputFirstName;
    EditText inputLastName;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
	private TextView link_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
				 /**
	     * Defining all layout items
	     **/
	        inputFirstName = (EditText) findViewById(R.id.reg_firstname);
	        inputLastName = (EditText) findViewById(R.id.reg_lastname);
	        inputUsername = (EditText) findViewById(R.id.userName);
	        inputEmail = (EditText) findViewById(R.id.reg_email);
	        inputPassword = (EditText) findViewById(R.id.password);
	        btnRegister = (Button) findViewById(R.id.btnRegister);
	        
		link_login = (TextView) findViewById(R.id.link_to_login);
		link_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				link_login.setTextColor(getResources().getColor(R.color.brownish));
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		   /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputFirstName.getText().toString().equals("")) && ( !inputLastName.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals("")) )
                {
                    if ( isValidUsername(inputUsername.getText().toString()) ){
                    if(isValidEmail(inputEmail.getText().toString())){
                    	if(isValidPassword(inputPassword.getText().toString())){
                    		new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
	        		        .setMessage("Are you sure to register with these details?")
	        		        .setNegativeButton("No", null)
	        		        .setPositiveButton("Yes", new OnClickListener() {

	        		            public void onClick(DialogInterface arg0, int arg1) {
	        		            	NetAsync(view);		        		            	
	        		            }		        
	        		        }).create().show();
                    	}
                    	else{
                    		new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
        			        .setTitle("Invalid Password")
        			        .setMessage("Password should be of minimum 6 and maximum 15 characters.It should contain only a-z, A-Z, 0-9, '_', '@', '&', '*', '.' and MUST CONTAIN AT LEAST ONE of these mentioned special characters")
        			         .setPositiveButton("Ok", null)
        			        .create().show();
                    	}
                    }
                    else{
//                    	Toast.makeText(getApplicationContext(),
//                                "Are you sure you enetered a correct email?Please check again.", Toast.LENGTH_LONG).show();
                    	new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
    			        .setTitle("Invalid Email")
    			        .setMessage("Please provide a valid Email id")
    			         .setPositiveButton("Ok", null)
    			        .create().show();
                    }
                   }
                    else
                    {
//                        Toast.makeText(getApplicationContext(),
//                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    	new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
    			        .setTitle("Invalid Username")
    			        .setMessage("Username should be of minimum 5 and maximum 15 characters and should contain only a-z, 0-9, underscore(_) and hyphen(-)")
    			         .setPositiveButton("Ok", null)
    			        .create().show();
                    }
                }
                else
                {
//                    Toast.makeText(getApplicationContext(),
//                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                	new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
			        .setTitle("Empty Fields")
			        .setMessage("One or More fields are empty.Please fill them up")
			        .setPositiveButton("Ok", null)
			        .create().show();
                }
            }
        });
	}
	
	@Override
	public void onBackPressed() {
		 this.startActivity(new Intent(RegisterActivity.this,MainActivity.class));  
    	 return;  
	}
	public final static boolean isValidEmail(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
		    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		  }
		}
	
	public final static boolean isValidUsername(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
			pattern = Pattern.compile(USERNAME_PATTERN);  
			 /**
			   * Validate username with regular expression
			   * @param username username for validation
			   * @return true valid username, false invalid username
			   */
			 matcher = pattern.matcher(target);
			 return matcher.matches();
		  }
		}
	
	public final static boolean isValidPassword(CharSequence target) {
		  if (TextUtils.isEmpty(target)) {
		    return false;
		  } else {
			pattern = Pattern.compile(PASSWORD_PATTERN);  
			 matcher = pattern.matcher(target);
			 return matcher.matches();
		  }
		}
  /**
   * Async Task to check whether internet connection is working
   **/

  private class NetCheck extends AsyncTask<String,String,Boolean>
  {
      private ProgressDialog nDialog;

      @Override
      protected void onPreExecute(){
          super.onPreExecute();
          nDialog = new ProgressDialog(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK);
          nDialog.setMessage("Loading..");
          nDialog.setTitle("Checking Network");
          nDialog.setIndeterminate(false);
          nDialog.setCancelable(true);
          nDialog.show();
      }

      @Override
      protected Boolean doInBackground(String... args){


/**
* Gets current device state and checks for working internet connection by trying Google.
**/
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
              new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
		        .setTitle("Network Error")
		        .setMessage("Connection Time Out.Please try again later.")
		        .setPositiveButton("Ok", null)
		        .create().show();
          }
      }
  }





  private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

/**
* Defining Process dialog
**/
      private ProgressDialog pDialog;

      String email,password,fname,lname,uname;
      @Override
      protected void onPreExecute() {
          super.onPreExecute();
          inputUsername = (EditText) findViewById(R.id.userName);
          inputPassword = (EditText) findViewById(R.id.password);
             fname = inputFirstName.getText().toString();
             lname = inputLastName.getText().toString();
              email = inputEmail.getText().toString();
              uname= inputUsername.getText().toString();
              password = inputPassword.getText().toString();
          pDialog = new ProgressDialog(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK);
          pDialog.setTitle("Contacting Servers");
          pDialog.setMessage("Registering ...");
          pDialog.setIndeterminate(false);
          pDialog.setCancelable(true);
          pDialog.show();
      }

      @Override
      protected JSONObject doInBackground(String... args) {


      UserFunctions userFunction = new UserFunctions();
      JSONObject json = userFunction.registerUser(fname, lname, email, uname, password);

          return json;


      }
     @Override
      protected void onPostExecute(JSONObject json) {
     /**
      * Checks for success message.
      **/
              try {
                  if (json.getString(KEY_SUCCESS) != null) {
                      String res = json.getString(KEY_SUCCESS);

                      String red = json.getString(KEY_ERROR);

                      if(Integer.parseInt(res) == 1){
                          pDialog.setTitle("Getting Data");
                          pDialog.setMessage("Loading Info");

                          DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                          JSONObject json_user = json.getJSONObject("user");

                          /**
                           * Removes all the previous data in the SQlite database
                           **/

                          UserFunctions logout = new UserFunctions();
                          logout.logoutUser(getApplicationContext());
                          db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                          /**
                           * Stores registered data in SQlite Database
                           * Launch Registered screen
                           **/

                          final Intent registered = new Intent(getApplicationContext(), RegisteredActivity.class);

                          /**
                           * Close all views before launching Registered screen
                          **/
                          registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                          pDialog.dismiss();
                          new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
          		          .setTitle("One Last Step")
          		          .setMessage("A confirmation mail has been sent to your Email id.Please go through it in order to login to your YouShare account.")
          		          .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

      			            public void onClick(DialogInterface arg0, int arg1) {
      			            	
      			            	startActivity(registered);
                                finish();
      			    						
      			    			      			       	    
      			            }
      			        })
          		          .create().show();
                          
                      }

                      else if (Integer.parseInt(red) ==2){
                          pDialog.dismiss();
                          new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
          		        .setTitle("Duplicate Email")
          		        .setMessage("An user with this Email already exists.If you registered before then please login instead of registering again.")
          		        .setPositiveButton("Ok", null)
          		        .create().show();
                      }
                      else if (Integer.parseInt(red) ==3){
                          pDialog.dismiss();
                          new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
            		        .setTitle("Duplicate Username")
            		        .setMessage("Sorry, this username has already been taken by someone else.Please try a different one.")
            		        .setPositiveButton("Ok", null)
            		        .create().show();
                      }
                      else if (Integer.parseInt(red) ==4){
                          pDialog.dismiss();
                          new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
          		        .setTitle("Invalid Email")
          		        .setMessage("Please type in your correct email id.")
          		        .setPositiveButton("Ok", null)
          		        .create().show();
                      }

                  }


                      else{
                      pDialog.dismiss();
                      new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_DARK)
        		        .setTitle("Error")
        		        .setMessage("Sorry but some unknown error occured while registering.Please try again later.")
        		        .setPositiveButton("Ok", null)
        		        .create().show();
                    
                      }

              } catch (JSONException e) {
                  e.printStackTrace();


              }
          }}
      public void NetAsync(View view){
          new NetCheck().execute();
      }}



