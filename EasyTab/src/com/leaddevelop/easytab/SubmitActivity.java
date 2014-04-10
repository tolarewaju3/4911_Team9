package com.leaddevelop.easytab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

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
