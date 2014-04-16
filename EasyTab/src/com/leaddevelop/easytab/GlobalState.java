package com.leaddevelop.easytab;
import java.util.HashMap;
import java.util.List;

import com.parse.ParseObject;

import android.app.Application;


// TODO: Auto-generated Javadoc
/**
 * The Class GlobalState to pass ParseObjects between Activities
 */
public class GlobalState extends Application {

	/** The bills holder. */
	private static HashMap<String, List<ParseObject>> billsHolder;
	
	/** The splitted bills holder. */
	private static HashMap<ParseObject, List<String>> splittedBillsHolder;
	
	/** The order. */
	private static ParseObject order;
	
	/**
	 * Gets the bills holder.
	 *
	 * @return the bills holder
	 */
	public static HashMap<String, List<ParseObject>> getBillsHolder() {
		return billsHolder;
	}
	
	/**
	 * Sets the bills holder.
	 *
	 * @param billsHolder the bills holder
	 */
	public static void setBillsHolder(HashMap<String, List<ParseObject>> billsHolder) {
		GlobalState.billsHolder = billsHolder;
	}
	
	/**
	 * Gets the splitted bills holder.
	 *
	 * @return the splitted bills holder
	 */
	public static HashMap<ParseObject, List<String>> getSplittedBillsHolder() {
		return splittedBillsHolder;
	}
	
	/**
	 * Sets the splitted bills holder.
	 *
	 * @param splittedBillsHolder the splitted bills holder
	 */
	public static void setSplittedBillsHolder(
			HashMap<ParseObject, List<String>> splittedBillsHolder) {
		GlobalState.splittedBillsHolder = splittedBillsHolder;
	}
	
	/**
	 * Sets the order holder.
	 *
	 * @param orderObject the new order holder
	 */
	public static void setOrderHolder(ParseObject orderObject) {
		GlobalState.order = orderObject;
	}
	
	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public static ParseObject getOrder() {
		return GlobalState.order;
	}
	
	
}
