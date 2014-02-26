package com.leaddevelop.easytab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "oNXU5Lz9lfgEiFafCil80dUAXJJjIFW3EVhUN8BF");
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
//					completeLogin();
					Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
					startActivity(intent);

				} else {
					Toast.makeText(getApplicationContext(), "Login failed!", 5).show();
//					toast.setView(getCurrentFocus());
//					toast.show();
				}
			}
		});
	}
	
	public void completeLogin() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

}
