package com.leaddevelop.easytab;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;

public class SMSHandler {

	public static final String ACCOUNT_SID = "AC2756482a596c5cad79cdecc8bff17dfb";
	public static final String AUTH_TOKEN = "ff6bf8ed48a460dea1a97eb842498f67";
	public static final String SOURCE_NUMBER = "+14695139822";
	
	public static void sendSMS(String number, String messageText) throws TwilioRestException {
//	    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
//	    List<NameValuePair> params = new ArrayList<NameValuePair>();
//	    params.add(new BasicNameValuePair("Body", messageText));
//	    params.add(new BasicNameValuePair("To", number));
//	    params.add(new BasicNameValuePair("From", SOURCE_NUMBER));
//	    SmsFactory messageFactory = client.getAccount().getSmsFactory();
//	    Sms message = messageFactory.create(params);
//	    message.getSid();
	}
	
}
