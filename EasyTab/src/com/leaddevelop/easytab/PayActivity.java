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


// TODO: Auto-generated Javadoc
/**
 * The Class PayActivity.
 */
public class PayActivity extends Activity {
	
	/** The item objects. */
	List<ParseObject> itemObjects;
	
	/** The selected items. */
	List<ParseObject> selectedItems;
	
	/** The splitted selected items. */
	List<ParseObject> splittedSelectedItems;
	
	/** The bills. */
	HashMap<String, List<ParseObject>> bills;
	
	/** The splitted bills. */
	HashMap<ParseObject, List<String>> splittedBills;
	
	/** The order items. */
	ArrayList<String> orderItems;
	
	/** The adapter. */
	ArrayAdapter<String> adapter;
	
	/** The price for bill */
	int price;
	
	/** The number of bills on this order */
	int numBills;
	
	/** The number bills remaining. */
	int numBillsRem;
	
	/** The order. */
	ParseObject order;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
		return true;
	}
	
	/**
	 * Get the unpaid order for this table that hasn't been paid
	 * If there are no open table orders, show an error toast
	 * @return the table order
	 */
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
	
	/**
	 * Gets the items for this order
	 * Adds those items to our data structure
	 * Notify the adapter that we have new items
	 * @param the order
	 * @return the order's items
	 */
	public void getOrderItems(ParseObject order){
		this.order = order;
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
	
	/**
	 *  Update the total with all the selected items.
	 */
	public void updatePrice(){
		price = 0;
		for (ParseObject selectedItem : selectedItems){
			price += selectedItem.getInt("price");
		}
		
		TextView orderTotal = (TextView) findViewById(R.id.orderTotal);
		orderTotal.setText("$" + price + ".00");
	}
	
	/**
	 * Show a toast if we have no open orders
	 */
	public void noTableOrders(){
		Toast.makeText(getApplicationContext(), "No Open Orders!", 5).show();
		finish();
	}
	
	/**
	 * Loop through all splittled items and add corresponding items to bills
	 * Loop through non-selected items and add to bills
	 * Save all data in phone number, items key value hashmap.
	 */
	public void saveCurrentBill() {
		EditText phoneNumberField = (EditText)findViewById(R.id.phoneNumberInput);
		String phoneNumber = phoneNumberField.getText().toString();
		
		/* Build deep copy of selected items */
		List<ParseObject> selectedClone = new ArrayList<ParseObject>();
		for(ParseObject obj : selectedItems) {
			selectedClone.add(obj);
		}
		
		bills.put(phoneNumber, selectedClone);

		/* Build deep copy of splitted selected items */
		List<ParseObject> splittedSelectedClone = new ArrayList<ParseObject>();
		for(ParseObject obj : splittedSelectedItems) {
			splittedSelectedClone.add(obj);
		}
		
		
		for(ParseObject item : splittedSelectedClone) {
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
			itemObjects.remove(item);
		}
		adapter.notifyDataSetChanged();
		selectedItems.clear();
		splittedSelectedItems.clear();
		updatePrice();
		phoneNumberField.setText("");
	}
	
	/**
	 * On press submit.
	 *
	 * @param view the view
	 */
	public void onPressSubmit(View view) {
		saveCurrentBill();
		clearListItems();
		numBillsRem--;
		if(numBillsRem > 0) {
			
		} else {
			Intent intent = new Intent(this, SubmitActivity.class);
			GlobalState.setBillsHolder(bills);
			GlobalState.setSplittedBillsHolder(splittedBills);
			GlobalState.setOrderHolder(order);
			startActivity(intent);
		}
	}
	
	/**
	 * Show item split dialog. Prompt the user to either split or not split the item
	 *
	 * @param item the item
	 */
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
				updatePrice();
			}
		});
		
		builder.create().show();
	}
	
	/**
	 * Clear list items for the next bill
	 */
	public void clearListItems(){
		ListView listView1 = (ListView) findViewById(R.id.listView1);
		listView1.clearChoices();
        for (int i = 0; i < listView1.getCount(); i++){
        	CheckedTextView textView = (CheckedTextView)listView1.getChildAt(i);
       	 	textView.setChecked(false);
        }
	}

	
}
