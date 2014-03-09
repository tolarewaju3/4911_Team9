package com.leaddevelop.easytab;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.parse.ParseRelation;


public class PayActivity extends Activity {
	
	ArrayList<String> orderItems;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		
		orderItems = new ArrayList<String>();
		getTableOrder(0); // Fix this -- Get the table number from our "Settings"
		
		ListView listView1 = (ListView) findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, orderItems);       
        listView1.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
		return true;
	}
	
	// Get the unpaid order for this table that hasn't been paid
	public void getTableOrder(int tableNumber){ 
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
		query.whereEqualTo("tableNumber", tableNumber);
		query.whereDoesNotExist("paid");
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> tableOrders, ParseException e) {
		        if (e == null) {
		        	getOrderItems(tableOrders.get(0));
		        } else {
		        	
		        }
		    }
		});
	}
	
	public void getOrderItems(ParseObject order){
		ParseRelation<ParseObject> itemsFromOrder = order.getRelation("items");
		itemsFromOrder.getQuery().findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> itemList, ParseException e) {
		      if (e != null) {
		        // There was an error
		      } else {
		        orderItems.clear();
		        for (ParseObject item : itemList) {
					orderItems.add(item.getString("name"));
				}
				adapter.notifyDataSetChanged();
		      }
		    }
		});
	}
	
	public void onPressSubmit(View view) {
		Intent intent = new Intent(this, SubmitActivity.class);
		startActivity(intent);
	}

	
}
