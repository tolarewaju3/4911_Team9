Parse.Cloud.define("sendPaypalLinks", function(request, response) {
	var payTo = "twizzenator@gmail.com"//request.params.user.get("email");
	var orderId = request.params.orderId;
	var orderQuery = new Parse.Query("Order");
	orderQuery.get(orderId, {
		success: function(order) {
			var billQuery = new Parse.Query("Bill");
			billQuery.equalTo("order", order)
			billQuery.find({
				success: function(bills) {
					bills.forEach(function(bill) {
						var totalPrice = String(parseFloat(bill.get("priceWithTax")) + parseFloat(bill.get("tip"))) + "0";
						var phoneNumber = bill.get("phoneNumber");
						Parse.Cloud.run("getPaypalLink", {price: totalPrice, payTo: payTo}, {
							success: function(link) {
								Parse.Cloud.run("sendPaypalLink", {link: link, phoneNumber: phoneNumber});
							}
						});
					});
				}
			});
		}
	});
});

Parse.Cloud.define("sendPaypalLink", function(request, response) {
	Parse.Cloud.httpRequest({
		url: "https://ACddd9097f8bca2b487b79c4d8507bb154:4449c76111ba18909cdcece680484fa0@api.twilio.com/2010-04-01/Accounts/ACddd9097f8bca2b487b79c4d8507bb154/Messages.json",
		method: "POST",
		body: {
			From: "+16785155887",
			To: "+1" + request.params.phoneNumber,
			Body: "Please click the following link to make your payment: " + request.params.link
		}
	});
});

Parse.Cloud.define("getPaypalLink", function(request, response) {
	Parse.Cloud.httpRequest({
		url: "https://svcs.sandbox.paypal.com/AdaptivePayments/Pay",
		method: "POST",
		headers: {	"Content-Type": "application/json",
					"X-PAYPAL-SECURITY-USERID": "emorphis3-facilitator_api1.gatech.edu",
					"X-PAYPAL-SECURITY-PASSWORD": "1394570559",
					"X-PAYPAL-SECURITY-SIGNATURE": "A8KhtBow2Ql3SA0p7vWMiEAVNA3LAu1KMDzmNTcRICrYXzPQnGBH57RR",
					"X-PAYPAL-APPLICATION-ID": "APP-80W284485P519543T",
					"X-PAYPAL-REQUEST-DATA-FORMAT": "JSON",
					"X-PAYPAL-RESPONSE-DATA-FORMAT": "JSON"},
		body: {
			"actionType": "PAY",
			"currencyCode": "USD",
			"receiverList":{
				"receiver":[{
					"amount": request.params.price,
					"email": request.params.payTo
				}]
			},
			"returnUrl":"http://google.com",
			"cancelUrl":"http://google.com",
			"requestEnvelope":{
				"errorLanguage":"en_US",
				"detailLevel":"ReturnAll"
			}
		},
		success: function(json) {
			var payKey = json.data.payKey;
			var link = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey=" + payKey;
			response.success(link);
		}
	});
});