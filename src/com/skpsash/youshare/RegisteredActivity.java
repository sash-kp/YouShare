package com.skpsash.youshare;

/**
 * Author :Raj Amal
 * Email  :raj.amalw@learn2crack.com
 * Website:www.learn2crack.com
 **/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.skpsash.youshare.adapter.DatabaseHandler;

import java.util.HashMap;

public class RegisteredActivity extends Activity {


	TextView tv;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        tv = (TextView) findViewById(R.id.detailsReg);
        SpannableString content = new SpannableString("Your Registration Details");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap<String,String> user = new HashMap<String, String>();
        user = db.getUserDetails();

        /**
         * Displays the registration details in Text view
         **/

        final TextView fname = (TextView)findViewById(R.id.fname);
        final TextView lname = (TextView)findViewById(R.id.lname);
        final TextView uname = (TextView)findViewById(R.id.uname);
        final TextView email = (TextView)findViewById(R.id.email);
        final TextView created_at = (TextView)findViewById(R.id.regat);
        fname.setText(user.get("fname"));
        fname.setTextColor(getResources().getColor(R.color.whitish));
        lname.setText(user.get("lname"));
        lname.setTextColor(getResources().getColor(R.color.whitish));
        uname.setText(user.get("uname"));
        uname.setTextColor(getResources().getColor(R.color.whitish));
        email.setText(user.get("email"));
        email.setTextColor(getResources().getColor(R.color.whitish));
        created_at.setText(user.get("created_at"));
        created_at.setTextColor(getResources().getColor(R.color.whitish));






        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });


    }}
