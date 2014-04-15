Parse.Cloud.define("updateAllOrdersArePaid", function(request, response) {
	var user = request.user;
	var orderQuery = new Parse.Query("Order");
	orderQuery.equalTo("user", user);
	orderQuery.equalTo("paid", false);
	orderQuery.find({
		success: function(orders) {
			var numUnpaidOrders = orders.length;
			var numOrders = orders.length;
			orders.forEach(function(order) {
				Parse.Cloud.run("updateOrderIsPaid", {orderId: order.id}, {
					success: function(isPaid) {
						if (isPaid) {
							numUnpaidOrders--;
						}
						numOrders--;
						if (numUnpaidOrders == 0) {
							response.success(true);
						}
						else if (numOrders == 0) {
							response.success(false);
						}
					}
				});
			});
		}
	});
});

Parse.Cloud.define("updateOrderIsPaid", function(request, response) {
	var orderId = request.params.orderId;
	var orderQuery = new Parse.Query("Order");
	orderQuery.get(orderId, {
		success: function(order) {
			var billQuery = new Parse.Query("Bill");
			billQuery.equalTo("order", order);
			billQuery.equalTo("paid", false);
			billQuery.find({
				success: function(bills) {
					var numUnpaidBills = bills.length;
					var numBills = bills.length;
					bills.forEach(function(bill) {
						Parse.Cloud.run("updateBillIsPaid", {billId: bill.id}, {
							success: function(isPaid) {
								if (isPaid) {
									numUnpaidBills--;
								}
								numBills--;
								if (numUnpaidBills == 0) {
									order.set("paid", true);
									order.save();
									response.success(true);
								}
								else if (numBills == 0) {
									response.success(false);
								}
							}
						});
					});
				}
			});
		}
	});
});

Parse.Cloud.define("updateBillIsPaid", function(request, response) {
	var billId = request.params.billId;
	var billQuery = new Parse.Query("Bill");
	billQuery.get(billId, {
		success: function(bill) {
			if (bill.get("payKey") && !bill.get("paid")) {
				Parse.Cloud.httpRequest({
					url: "https://svcs.sandbox.paypal.com/AdaptivePayments/PaymentDetails",
					method: "POST",
					headers: {	"Content-Type": "application/json",
								"X-PAYPAL-SECURITY-USERID": "emorphis3-facilitator_api1.gatech.edu",
								"X-PAYPAL-SECURITY-PASSWORD": "1394570559",
								"X-PAYPAL-SECURITY-SIGNATURE": "A8KhtBow2Ql3SA0p7vWMiEAVNA3LAu1KMDzmNTcRICrYXzPQnGBH57RR",
								"X-PAYPAL-APPLICATION-ID": "APP-80W284485P519543T",
								"X-PAYPAL-REQUEST-DATA-FORMAT": "JSON",
								"X-PAYPAL-RESPONSE-DATA-FORMAT": "JSON"},
					body: {
						"payKey": bill.get("payKey"),
						"requestEnvelope": {
							"errorLanguage":"en_US"
						}
					},
					success: function(json) {
						var paid = json.data.status == "COMPLETED";
						bill.set("paid", paid);
						bill.save();
						response.success(paid);
					}
				});
			}
			else if (!bill.get("payKey")) {
				response.success(false);
			}
			else if(bill.get("paid")) {
				response.success(true);
			}
		}
	});
});

Parse.Cloud.define("sendPaypalLinks", function(request, response) {
	var payTo;
	if (request.params.test) {
		payTo = "twizzenator@gmail.com";
	}
	else {
		payTo = request.user.get("email");
	}
	var orderId = request.params.orderId;
	var orderQuery = new Parse.Query("Order");
	orderQuery.get(orderId, {
		success: function(order) {
			var billQuery = new Parse.Query("Bill");
			billQuery.equalTo("order", order);
			billQuery.find({
				success: function(bills) {
					bills.forEach(function(bill) {
						var totalPrice = String(parseFloat(bill.get("priceWithTax")) + parseFloat(bill.get("tip"))) + "0";
						var phoneNumber = bill.get("phoneNumber");
						Parse.Cloud.run("getPaypalLink", {price: totalPrice, payTo: payTo}, {
							success: function(payKey) {
								var link = "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey=" + payKey;
								bill.set("payKey", payKey);
								bill.save();
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
			response.success(payKey);
		}
	});
});