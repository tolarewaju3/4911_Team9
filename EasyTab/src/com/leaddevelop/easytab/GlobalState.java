package com.leaddevelop.easytab;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseObject;

import android.app.Application;


public class GlobalState extends Application {

	private static HashMap<String, List<ParseObject>> billsHolder;
	private static HashMap<ParseObject, List<String>> splittedBillsHolder;
	
	private static ParseObject order;
	
	public static HashMap<String, List<ParseObject>> getBillsHolder() {
		return billsHolder;
	}
	public static void setBillsHolder(HashMap<String, List<ParseObject>> billsHolder) {
		GlobalState.billsHolder = billsHolder;
	}
	public static HashMap<ParseObject, List<String>> getSplittedBillsHolder() {
		return splittedBillsHolder;
	}
	public static void setSplittedBillsHolder(
			HashMap<ParseObject, List<String>> splittedBillsHolder) {
		GlobalState.splittedBillsHolder = splittedBillsHolder;
	}
	public static void setOrderHolder(ParseObject orderObject) {
		GlobalState.order = orderObject;
	}
	
	public static ParseObject getOrder() {
		return GlobalState.order;
	}
	
	
}
