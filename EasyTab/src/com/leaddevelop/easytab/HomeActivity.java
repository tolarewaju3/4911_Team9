package com.leaddevelop.easytab;

import com.twilio.sdk.TwilioRestException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Parse.initialize(this, "X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "oNXU5Lz9lfgEiFafCil80dUAXJJjIFW3EVhUN8BF");
		setContentView(R.layout.activity_home);
		
//		ParseObject testObject = new ParseObject("TestObject");
//		testObject.put("foo", "bar");
//		testObject.saveInBackground();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onPressPay(View view) throws TwilioRestException {
		Intent intent = new Intent(this, PayActivity.class);
		startActivity(intent);
	}
	
}
