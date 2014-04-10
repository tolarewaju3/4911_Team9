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
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.parse.ParseObject;

public class SubmitActivity extends Activity {

	private ExpandableListView expListView;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit);
		Bundle bundle = getIntent().getExtras();
		HashMap<String, List<ParseObject>> bills = GlobalState.getBillsHolder();
		HashMap<ParseObject, List<String>> splittedBills = GlobalState.getSplittedBillsHolder();

		
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
		
		HashMap<String, Double> totals = calculateTotals(bills, splittedBills);
		
		List<String> phoneNumsWithTotals = new ArrayList<String>();
		for(String num : phoneNums) {
			NumberFormat formatter = NumberFormat.getCurrencyInstance();
			phoneNumsWithTotals.add(num + ": " + formatter.format(totals.get(num)));
		}
		
		ListView listView1 = (ListView) findViewById(R.id.listView2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, phoneNumsWithTotals);       
        listView1.setAdapter(adapter);
	}

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
				totals.put(phoneNum, currTotal + divPrice);
			}
		}
		return totals;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit, menu);
		return true;
	}

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
