var currentUser;
var Order;
var user;
var orders;

$(document).ready(function() {
	/*  Initialize Parse Object */
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");

	// check if any previously unpaid orders are now paid before building page
	Parse.Cloud.run('updateAllOrdersArePaid', {}, {
  		success: function(ratings) {
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
						render_order_details();
					}
				});
			}
  		}
	});

    /* For elements added to the dom later, add click events for order rows */
	$(document).on("click", ".orderRow", function() {
		hide_all_details();
		var orderID = $(this).attr("data-order-id");
		var orderDetails = $(".orderDetails");
		var itemsListDetails = orderDetails.find(".pricing-table[data-order-id=" + orderID + "]");
		itemsListDetails.removeClass("hide");
	})

	$(document).on("click", ".cta-button", function(e) {
		var itemsListDetails = $(this).closest(".pricing-table");
		itemsListDetails.addClass("hide");
	})
});

function render_table() {

	var table = $('.tableBody');
	table.empty();

	if (orders.length == 0) {
		render_no_orders();
	} else {
		$(".noOrdersRow", $(".tableBody")).remove();
		orders.forEach(function(order){
			render_order(order);
		});
	}
}

/* Retrieve orders from Parse, create order details objects, add them to the dom */
function render_order_details() {
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
						$(".itemPrice", listItem).text("$" + parseFloat(item.get("price")).toFixed(2));

						itemList.append(listItem);
					});

					itemList.append(closeBtn);

					$(".description", itemList).text(order.id);
					$(".price", itemList).text("$" + subtotal.toFixed(2));
				}

				itemList.attr("data-order-id", order.id);
				itemList.addClass("hide");
				orderDetails.append(itemList);
			}
		});
	});
}

function render_order(order) {
	var orderRow = $(".orderRow", $(".templates")).clone();
	orderRow.attr("data-order-id", order.id);
	$(".orderID", orderRow).text(order.id);
	$(".orderDateTime", orderRow).text(render_order_date_time(order.createdAt));
	$(".orderTableNum", orderRow).text(order.get("tableNumber"));
	$(".orderStatus", orderRow).html(render_order_status(order.get("paid")));
	
	$(".tableBody").append(orderRow);
}

function render_order_status(isPaid) {
	if (isPaid) {
		return $(".paid", $(".templates")).clone();
	} else {
		return $(".unpaid", $(".templates")).clone();
	}
}

function render_no_orders() {
	var noOrdersRow = $(".noOrdersRow", $(".templates")).clone();
	$(".tableBody").append(noOrdersRow);
}

function hide_all_details() {
    var orderDetails = $(".pricing-table", $(".orderDetails")).toArray();
    orderDetails.forEach(function (table) {
        $(table).removeClass("hide").addClass("hide");
    });
}

/* Query all orders from Parse */
function all_orders_query() {
	var query = new Parse.Query(Order);
	query.equalTo("user", user);
	query.descending("createdAt");
	return query;
}

/* Format date time of place order to look nice */
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