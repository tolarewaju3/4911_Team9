package com.leaddevelop.easytab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

// TODO: Auto-generated Javadoc
/**
 * The Class HomeActivity. This is our Home Screen
 */
public class HomeActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * We have pressed the order button
	 * Start the order activity
	 * @param view the view
	 */
	public void onPressOrder(View view) {
		Intent intent = new Intent(this, OrderActivity.class);
		startActivity(intent);
	}

	/**
	 * We have pressed the pay button
	 * Get the number of people in this party
	 * Start the pay activity
	 * @param view the view
	 */
	public void onPressPay(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Number of bills");
		builder.setMessage("How many people are paying in your party?");
		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), PayActivity.class);
				intent.putExtra("numBills", input.getText().toString());
				startActivity(intent);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		builder.create().show();
	}
	
	/**
	 * We have pressed the settings button
	 * Start the settings activity
	 * @param view the view
	 */
	public void onPressSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
}
