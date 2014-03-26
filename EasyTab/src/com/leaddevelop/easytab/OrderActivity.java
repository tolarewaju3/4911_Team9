package com.leaddevelop.easytab;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class OrderActivity extends Activity {

	List<ParseObject> itemObjects;
	List<ParseObject> selectedItems;
	ArrayList<String> orderItems;
	ArrayAdapter<String> adapter;
	int price;
	
	ParseObject menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		// Show the Up button in the action bar.
		setupActionBar();

		orderItems = new ArrayList<String>();
		itemObjects = new ArrayList<ParseObject>();
		selectedItems = new ArrayList<ParseObject>();
		price = 0;
		getMenuItems(); // Fix this -- Get the table number from our "Settings"

		ListView listView1 = (ListView) findViewById(R.id.listView1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, orderItems);       
		listView1.setAdapter(adapter);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				CheckedTextView textView = (CheckedTextView)view;

				ParseObject orderItem = itemObjects.get(position);
				if(textView.isChecked()){
					selectedItems.remove(orderItem);
				}
				else{
					selectedItems.add(orderItem);
				}

				textView.setChecked(!textView.isChecked());
				updatePrice();
			}
		});

	}

	// Get the menu for this restaurant
	public void getMenuItems(){ 
		ParseUser user = ParseUser.getCurrentUser();
		menu = user.getParseObject("menu");

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
		query.whereEqualTo("menu", menu);

		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> menuItems, ParseException e) {
				if (e == null) {
					listMenuItems(menuItems);
				} else {

				}
			}
		});
	}

	public void listMenuItems(List<ParseObject> menuItems) {
		for(ParseObject item : menuItems) {
			String price = "[$" + item.getInt("price") + ".00]";
			orderItems.add(item.getString("name") + " " + price);
		}
		this.itemObjects = menuItems;
		adapter.notifyDataSetChanged();
	}

	public void updatePrice(){
		price = 0;
		for (ParseObject selectedItem : selectedItems){
			price += selectedItem.getInt("price");
			System.out.println(selectedItem.get("name"));
		}

		TextView orderTotal = (TextView) findViewById(R.id.orderTotal);
		orderTotal.setText("$" + price + ".00");
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.order, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onPressSubmit(View view) {
		// do something
		if(selectedItems.size() == 0) {
			Toast.makeText(getApplicationContext(), "No Items Selected!", 5).show();
		} else submitOrder();
	}
	
	public void submitOrder(){	
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int tableNumber = Integer.parseInt(sharedPref.getString("table_num", ""));
		
		if(menu != null){
			ParseUser user = ParseUser.getCurrentUser();
			ParseObject order = new ParseObject("Order");
			order.put("user", user);
			order.put("paid", false);
			order.put("tableNumber", tableNumber); // Fix this -- currently hard coding table number
			
			ParseRelation<ParseObject> items = order.getRelation("items");
			
			for(ParseObject item : selectedItems) {
				items.add(item);
			}
			
			order.saveInBackground(new SaveCallback(){

				@Override
				public void done(ParseException e) {
					if(e == null){
						Toast.makeText(getApplicationContext(), "Order Submitted!", 5).show();
						finish();
					}
					else{
						Toast.makeText(getApplicationContext(), "Submitting Order Failed!", 5).show();
					}
					
				}
				
			});
		}
	}

}
