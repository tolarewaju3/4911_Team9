package com.leaddevelop.easytab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void onPress(View view) {
		EditText usernameField = (EditText)findViewById(R.id.usernameField);
		EditText passwordField = (EditText)findViewById(R.id.passwordField);
		login(usernameField.getText().toString(), passwordField.getText().toString());
	}
	
	public void login(String username, String password) {
		ParseUser.logInInBackground(username, password, new LogInCallback() {		
			@Override
			public void done(ParseUser user, ParseException ex) {
				// TODO Auto-generated method stub
				if(user != null) {
					completeLogin();
				} else {
					// failure
				}
			}
		});
	}
	
	public void completeLogin() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

}
