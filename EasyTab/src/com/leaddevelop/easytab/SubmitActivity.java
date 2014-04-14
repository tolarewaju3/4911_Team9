package com.leaddevelop.easytab;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseRelation;

public class SubmitActivity extends Activity {

	private ExpandableListView expListView;
	private final double taxMultiplier = 1.08;
	
	 HashMap<String, List<ParseObject>> updatedBills;
	 HashMap<String, Double> totals;
	 ParseObject order;

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
	 * total per person
	 * @param bills
	 * @param splittedBills
	 * @return
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
	
	public void uploadBills(HashMap<String, List<ParseObject>> bills,
			HashMap<String, Double> totals) {
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
			
			parseBill.saveInBackground();
		}
	}
	
	public void onPressSubmit(View view) {
		uploadBills(updatedBills, totals);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit, menu);
		return true;
	}

	
	/**
	 * Takes in the bill data structure and returns it in text form
	 * @param bills
	 * @return
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
