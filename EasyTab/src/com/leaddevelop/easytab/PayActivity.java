package com.leaddevelop.easytab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;


public class PayActivity extends Activity {
	List<ParseObject> itemObjects;
	List<ParseObject> selectedItems;
	List<ParseObject> splittedSelectedItems;
	HashMap<String, List<ParseObject>> bills;
	HashMap<ParseObject, List<String>> splittedBills;
	ArrayList<String> orderItems;
	ArrayAdapter<String> adapter;
	int price;
	int numBills;
	int numBillsRem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		
		numBills = Integer.parseInt(this.getIntent().getExtras().getString("numBills"));
		numBillsRem = numBills;
		orderItems = new ArrayList<String>();
		itemObjects = new ArrayList<ParseObject>();
		selectedItems = new ArrayList<ParseObject>();
		splittedSelectedItems = new ArrayList<ParseObject>();
		splittedBills = new HashMap<ParseObject, List<String>>();
		bills = new HashMap<String, List<ParseObject>>();
		price = 0;
		getTableOrder();
		
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
                	showItemSplitDialog(orderItem);
                }
                
                textView.setChecked(!textView.isChecked());
                updatePrice();
			}
        });
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
		return true;
	}
	
	// Get the unpaid order for this table that hasn't been paid
	public void getTableOrder(){ 
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int tableNumber = Integer.parseInt(sharedPref.getString("table_num", ""));
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
		query.whereEqualTo("tableNumber", tableNumber);
		query.whereEqualTo("paid", false);
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> tableOrders, ParseException e) {
		        if (e == null) {
		        	if(tableOrders.size() != 0)
		        		getOrderItems(tableOrders.get(0));
		        	else
		        		noTableOrders();
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
		    	itemObjects = itemList;
		    	orderItems.clear();
		        for (ParseObject item : itemList) {
		        	String price = "[$" + item.getInt("price") + ".00]";
					orderItems.add(item.getString("name") + " " + price);
				}
				adapter.notifyDataSetChanged();
		      }
		    }
		});
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
	
	public void noTableOrders(){
		Toast.makeText(getApplicationContext(), "No Open Orders!", 5).show();
		finish();
	}
	
	public void saveCurrentBill() {
		EditText phoneNumberField = (EditText)findViewById(R.id.phoneNumberInput);
		String phoneNumber = phoneNumberField.getText().toString();
		bills.put(phoneNumber, selectedItems);
		for(ParseObject item : splittedSelectedItems) {
			List<String> phoneNumbers;
			if(splittedBills.containsKey(item)) {
				phoneNumbers = splittedBills.get(item);
			} else {
				phoneNumbers = new ArrayList<String>();
			}
			phoneNumbers.add(phoneNumber);
			splittedBills.put(item, phoneNumbers);			
		}
		for(ParseObject item : selectedItems) {
			String price = "[$" + item.getInt("price") + ".00]";
			orderItems.remove(item.getString("name") + " " + price);
		}
		adapter.notifyDataSetChanged();
		selectedItems.clear();
		splittedSelectedItems.clear();
		phoneNumberField.setText("");
	}
	
	public void onPressSubmit(View view) {
		saveCurrentBill();
		numBillsRem--;
		if(numBillsRem > 0) {
			
		} else {
			Intent intent = new Intent(this, SubmitActivity.class);
			startActivity(intent);
		}
	}
	
	public void showItemSplitDialog(final ParseObject item){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Splitting this item?");
		// Set up the input
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		String[] options = {"Pay for whole", "Pay for part"};
		builder.setItems(options, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (which == 0){ // We are paying for whole item
					selectedItems.add(item);
				}
				else{
					splittedSelectedItems.add(item);
				}
			}
		});
		
		builder.create().show();
	}

	
}
