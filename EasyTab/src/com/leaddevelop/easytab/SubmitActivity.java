package com.leaddevelop.easytab;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

// TODO: Auto-generated Javadoc
/**
 * The Class SubmitActivity.
 */
public class SubmitActivity extends Activity {

	/** The exp list view. */
	private ExpandableListView expListView;
	
	/** The tax multiplier. */
	private final double taxMultiplier = 1.08;
	
	 /** The updated bills. */
 	HashMap<String, List<ParseObject>> updatedBills;
	 
 	/** The totals. */
 	HashMap<String, Double> totals;
	 
 	/** The order. */
 	ParseObject order;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		Bundle bundle = getIntent().getExtras();
		HashMap<String, List<ParseObject>> bills = GlobalState.getBillsHolder();
		HashMap<ParseObject, List<String>> splittedBills = GlobalState.getSplittedBillsHolder();
		order = GlobalState.getOrder();
		
		/* Build the bill summaries */
		Set<String> stringSet = bills.keySet();
		List<String> phoneNums = new ArrayList<String>();
		for(String str : stringSet) {
			phoneNums.add(str);
		}
		HashMap<String, List<String>> personalSummaries = parseBillsToText(bills);
		expListView = (ExpandableListView) findViewById(R.id.summary_view);
		final ExpandableListAdapter expListAdapter = new OrderSummaryAdapter(
				this, phoneNums, personalSummaries);
		expListView.setAdapter(expListAdapter);
		
		totals = calculateTotals(bills, splittedBills);
		
		/* Construct the totals per person */
		List<String> phoneNumsWithTotals = new ArrayList<String>();
		for(String num : phoneNums) {
			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			phoneNumsWithTotals.add(num + ": " + formatter.format(totals.get(num)));
		}
		
		/* Attach totals to the layout */
		ListView listView1 = (ListView) findViewById(R.id.listView2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, phoneNumsWithTotals);       
        listView1.setAdapter(adapter);
        
       updatedBills = updateIndividualBills(bills, splittedBills);
        
	}
	
	/**
	 * Takes in the bills and returns a data structure with calculated
	 * total per person.
	 *
	 * @param bills the bills
	 * @param splittedBills the splitted bills
	 * @return the hash map
	 */
	private HashMap<String, Double> calculateTotals(
			HashMap<String, List<ParseObject>> bills,
			HashMap<ParseObject, List<String>> splittedBills) {
		// TODO Auto-generated method stub
		HashMap<String, Double> totals = new HashMap<String, Double>();
		for(Entry<String, List<ParseObject>> entry : bills.entrySet()) {
			String phoneNum = entry.getKey();
			double total = 0;
			for(ParseObject item : entry.getValue()) {
				total += item.getInt("price");
			}
			totals.put(phoneNum, total);
		}
		for(Entry<ParseObject, List<String>> entry : splittedBills.entrySet()) {
			ParseObject item = entry.getKey();
			double divPrice = ((double)item.getInt("price"))/entry.getValue().size();
			for(String phoneNum : entry.getValue()) {
				double currTotal = totals.get(phoneNum);
				totals.put(phoneNum, (currTotal + divPrice)*taxMultiplier);
			}
		}
		return totals;
	}
	
	/**
	 * Update individual bills.
	 *
	 * @param bills the bills
	 * @param splittedBills the splitted bills
	 * @return the hash map
	 */
	public HashMap<String, List<ParseObject>> updateIndividualBills(
			HashMap<String, List<ParseObject>> bills,
			HashMap<ParseObject, List<String>> splittedBills) {
		for(Entry<ParseObject, List<String>> entry : splittedBills.entrySet()) {
			ParseObject item = entry.getKey();
			for(String phoneNum : entry.getValue()) {
				List<ParseObject> items = bills.get(phoneNum);
				items.add(item);
				bills.put(phoneNum, items);
			}
		}
		return bills;
	}
	
	/**
	 * Upload bills.
	 *
	 * @param bills the bills
	 * @param totals the totals
	 */
	public void uploadBills(HashMap<String, List<ParseObject>> bills,
			HashMap<String, Double> totals) {
		List<ParseObject> billsToSave = new ArrayList<ParseObject>();
		
		for(Entry<String, List<ParseObject>> bill : bills.entrySet()) {
			String phoneNum = bill.getKey();
			List<ParseObject> items = bill.getValue();
			double total = totals.get(phoneNum);
			
			ParseObject parseBill = new ParseObject("Bill");
			parseBill.put("priceWithTax", total);
			parseBill.put("phoneNumber", phoneNum);
			parseBill.put("tip", 1); // FIX THIS TOO
			parseBill.put("paid", false);
			parseBill.put("order", order);
			
			ParseRelation<ParseObject> itemRelation = parseBill.getRelation("items");
			
			for(ParseObject item : items) {
				itemRelation.add(item);
			}
			billsToSave.add(parseBill);
		}
		
		ParseObject.saveAllInBackground(billsToSave, new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e == null){
					sendPaypalLinks();
				}
				else{
					Toast.makeText(getApplicationContext(), "Processing Bills Failed!", 5).show();
				}
			}
			
		});
	}
	
	/**
	 * Send paypal links.
	 */
	public void sendPaypalLinks(){
		HashMap<String, Object> params = new HashMap<String, Object>();
		ParseUser user = ParseUser.getCurrentUser();
		params.put("orderId", order.getObjectId());
		params.put("user", user.getObjectId());
		ParseCloud.callFunctionInBackground("sendPaypalLinks", params, new FunctionCallback<String>() {
		   public void done(String done, ParseException e) {
			   if(e == null){
					Toast.makeText(getApplicationContext(), "Payment Links Sent!", 5).show();
					//finish();
				}
				else{
					Toast.makeText(getApplicationContext(), "Processing Links Failed!", 5).show();
					System.out.println(e.toString());
				}
		   }
		});
	}
	
	/**
	 * On press submit.
	 *
	 * @param view the view
	 */
	public void onPressSubmit(View view) {
		uploadBills(updatedBills, totals);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit, menu);
		return true;
	}

	
	/**
	 * Takes in the bill data structure and returns it in text form.
	 *
	 * @param bills the bills
	 * @return the hash map
	 */
	public HashMap<String, List<String>> parseBillsToText(HashMap<String, List<ParseObject>> bills) {
		HashMap<String, List<String>> personalSummaries = new HashMap<String, List<String>>();
		for(Entry<String, List<ParseObject>> entry : bills.entrySet()) {
			ArrayList<String> names = new ArrayList<String>();
			for(ParseObject item : entry.getValue()) {
				names.add(item.getString("name"));
			}
			personalSummaries.put(entry.getKey(), names);
		}
		return personalSummaries;
	}
	
}
