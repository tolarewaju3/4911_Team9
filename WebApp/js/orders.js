var currentUser;
var Order;
var user;
var orders;

$(document).ready(function() {
	
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	
	Order = Parse.Object.extend("Order");
	currentUser = Parse.User.current();
	if (!currentUser) { window.location.replace("../");}

	user = Parse.User.current();
	var retrievedOrders = all_orders_query().collection();
	if (retrievedOrders) {
		retrievedOrders.fetch({
			success: function(retrievedOrders) {
				orders = retrievedOrders;
				render_table();
				render_order_details_cards();
			}
		});
	}

	$(document).on("click", ".orderRow", function() {
		var orderID = $(this).data("parse-id");
		var orderDetails = $(".orderDetails");
		var itemsListDetails = orderDetails.find(".pricing-table[data-parse-id=" + orderID);
		itemsListDetails.removeClass("hide");
		// var query = new Parse.Query(Order);		
		// query.get($(this).data("parse-id"), {
		//   	success: function(order) {
		// 	  	order.fetch({
		// 	  		success: function(order) {
		// 				var relation = order.relation("items");
		// 				relation.query().find({
		// 					success: function(itemsList) {
		// 						if (itemsList.length == 0) {
		// 							render_no_items();
		// 						} else {
		// 							render_order_details(itemsList, order);
		// 						}
		// 					}
		// 				});
		// 			}
		// 		});
		//   	}
		// });
	})

	$(document).on("click", ".cta-button", function(e) {
		var itemsListDetails = $(this).closest(".pricing-table");
		itemsListDetails.addClass("hide");
		// e.preventDefault();
		// $(".orderDetails").empty();
	})
});

function render_order_details_cards() {
	var orderDetails = $(".orderDetails");

	orders.forEach(function(order) {
		var relation = order.relation("items");
		relation.query().find({
			success: function(items) {
				var itemList = $(".pricing-table", $(".templates")).clone();
				var closeBtn = $(".cta-button", $(".templates")).clone();
				
				if (items.length == 0) {
					$(".description", itemList).text("That's no good. We found no items for this order. Try again later.");
					itemList.append(closeBtn);
				} else {
					var subtotal = 0.00;
					items.forEach(function(item) {
						subtotal += item.get("price");

						var listItem = $(".bullet-item", $(".templates")).clone();
						$(".itemName", listItem).text(item.get("name"));
						$(".itemPrice", listItem).text("$" + item.get("price"));

						itemList.append(listItem);
					});

					itemList.append(closeBtn);

					$(".description", itemList).text(order.id);
					$(".price", itemList).text("$" + subtotal);
				}

				itemList.data("order-id", order.id);
				itemList.addClass("hide");
				orderDetails.append(itemList);
			}
		});
	});
}

function render_order_details(items, order) {
	var orderDetails = $(".orderDetails");
	orderDetails.empty();
	var subtotal = 0.00;
	
	var itemList = $(".pricing-table", $(".templates")).clone();
	
	items.forEach(function(item) {
		subtotal += item.get("price");

		var listItem = $(".bullet-item", $(".templates")).clone();
		$(".itemName", listItem).text(item.get("name"));
		$(".itemPrice", listItem).text("$" + item.get("price"));

		itemList.append(listItem);
	});

	var closeBtn = $(".cta-button", $(".templates")).clone();
	itemList.append(closeBtn);

	$(".description", itemList).text(order.id);
	$(".price", itemList).text("$" + subtotal);

	orderDetails.append(itemList);
}

function render_table() {
	var table = $('.tableBody');
	table.empty();

	if (orders.length == 0) {
		render_no_orders();
	} else {
		orders.forEach(function(order){
			render_order(order);
		});
	}
}

function all_orders_query() {
	var query = new Parse.Query(Order);
	query.equalTo("user", user);
	query.descending("createdAt");
	return query;
}

function render_order(order) {
	var orderRow = $(".orderRow", $(".templates")).clone().data("order-id", order.id);
	$(".orderID", orderRow).text(order.id);
	$(".orderDateTime", orderRow).text(render_order_date_time(order.createdAt));
	$(".orderTableNum", orderRow).text(order.get("tableNumber"));
	$(".orderStatus", orderRow).html(render_order_status(order.get("paid")));
	
	$(".tableBody").append(orderRow);
}

function render_no_orders() {
	var noOrdersRow = $(".noOrdersRow", $(".templates")).clone();
	$(".tableBody").append(noOrdersRow);
}

function render_no_items() {
	var orderDetails = $(".orderDetails");
	orderDetails.empty();

	var itemList = $(".pricing-table", $(".templates")).clone();
	$(".description", itemList).text("That's not good. We found no items for this order. Try again later.");
	var closeBtn = $(".cta-button", $(".templates")).clone();
	itemList.append(closeBtn);
	orderDetails.append(itemList);
}

function remove_no_orders() {
	$(".noOrdersRow", $(".tableBody")).remove();
}

function render_order_status(isPaid) {
	if (isPaid) {
		return $(".paid", $(".templates")).clone();
	} else {
		return $(".unpaid", $(".templates")).clone();
	}
}

function render_order_date_time(dateObj) {
	var date = $.datepicker.formatDate("DD, MM d, yy", dateObj);
	
	var hours = dateObj.getHours();
	var minutes = dateObj.getMinutes();
	var ampm = hours >= 12 ? 'pm' : 'am';
	hours = hours % 12;
	hours = hours ? hours : 12; 
	minutes = minutes < 10 ? '0' + minutes : minutes;
	var time = hours + ':' + minutes + ' ' + ampm;
	
	return date + " " + time;
}