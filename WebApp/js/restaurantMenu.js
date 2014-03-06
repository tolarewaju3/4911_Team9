var currentUser;
var successfulChangeText = "Changes Saved Successfully!";
var menu;

$(document).ready(function() {
	
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	currentUser = Parse.User.current();
	if (!currentUser) { window.location.replace("../");}

	var retrievedMenu = currentUser.get("menu");
	if (retrievedMenu) {
		$('.title').html("Modify Menu");
		show_menu_edit_panels();
		retrievedMenu.fetch({
			success: function(retrievedMenu) {
				menu = retrievedMenu;
				$('.menuNameField').val(menu.get("name"));
					render_table();
				}
		});
	} 
	else {
		$('.title').html("Add Menu");
	}

	setup_buttons();
});

function setup_buttons() {
	$('.menuNameForm').submit(function(e) {
		e.preventDefault();
		if (menu) {
			update_menu_name();
		} else {
			create_new_menu();
		}
	});

	$('.deleteMenu').click(function(e) {
		e.preventDefault();
		delete_menu();
	});

	$('.addItem').click(function(e) {
		e.preventDefault();
		hide_action_panel();
		show_item_adder();
	});

	$(".cancelItem").click(function(e) {
		e.preventDefault();
		hide_item_adder();
		clear_item_adder();
		show_action_panel();
	});

	$(".submitItem").click(function(e) {
		e.preventDefault();
		hide_item_adder();

		var itemName = $(".itemNameField").val();
		var itemPrice = $(".itemPriceField").val();
		var itemDescription = $(".itemDescriptionField").val();

		add_item(itemName, itemPrice, itemDescription);
		clear_item_adder();
		show_action_panel();
	});

	$(".removeItems").click(function(e) {
		e.preventDefault();
		$(".deleteBox").show();
		hide_action_panel();
		show_item_remover();
	});

	$(".submitRemove").click(function(e) {
		e.preventDefault();
		var selectedRows = $(".itemRow").not(".templates .itemRow").has(".deleteBox input:checked");
	});

	$(".cancelRemove").click(function(e) {
		e.preventDefault();
		hide_item_remover();
		$(".deleteBox").hide();
		$(".deleteBox input").attr("checked", false);
		show_action_panel();
	});
}

function delete_menu() {
	menu.destroy({
		success: function(menuObject) {
			currentUser.unset("menu");
			var all_items = all_items_query().collection();
			delete_items(all_items);
			currentUser.save(null, {
				success: function() {
					alert("Menu deletion complete.");
					window.location.replace("./mainmenu.html");
				}
			});
		},
		error: function(menuObject, error) {
			alert("Menu deletion failed.");
		}
	});
}

function delete_items(items) {
	items.fetch({
		success: function(items) {
			items.forEach(function(item) {
				item.destroy({});
			});
		}
	});
}

function show_menu_edit_panels() {
	$('.actionPanel').removeClass('hide');
}

function create_new_menu() {
	var Menu = Parse.Object.extend("Menu");
	var newMenu = new Menu();

	newMenu.set('name', $('.menuNameField').val());
	newMenu.save();

	currentUser.set("menu", newMenu);
	menu = newMenu;

	currentUser.save(null, {
		success: function() {
			$('.helperText').html("Now you need to add some items. Use the buttons on the right to add or remove items.");
			$('.itemsTable').removeClass('hide');
			$('.actionPanel').removeClass('hide');
		},
		error: function() {
			displayFeedback(self, false, unknownErrorText);
		}
	});
}

function update_menu_name() {
	menu.set('name', $('.menuNameField').val());

	currentUser.save(null, {
		success: function() {
			alert(successfulChangeText);
		},
		error: function() {
			displayFeedback(self, false, unknownErrorText);
		}
	});
}

function show_action_panel() {
	$(".actionPanel").removeClass("hide");
}

function hide_action_panel() {
	$(".actionPanel").addClass("hide");
}

function show_item_adder() {
	$(".itemAdder").removeClass("hide");
}

function hide_item_adder() {
	$(".itemAdder").addClass("hide");
}

function show_items_table() {
	$('.itemsTable').removeClass('hide');
}

function clear_item_adder() {
	$("input[type='text']", $(".itemForm")).val("");
}

function show_item_remover() {
	$(".itemRemover").removeClass('hide');
}

function hide_item_remover() {
	$(".itemRemover").addClass('hide');
}

function add_item(name, price, description) {
	var Item = Parse.Object.extend("Item");
	var item = new Item();
	item.set("name", name);
	item.set("price", price);
	item.set("description", description);
	item.set("menu", menu);
	item.save(null, {
		success: function(item) {
			render_table();
		}
	});
}

function render_table() {
	show_items_table();
	var table = $('.tableBody');
	table.empty();

	var all_items = all_items_query().collection();

	all_items.fetch({
		success: function(items) {
			if (items.length == 0) {
				render_no_items(table);
			}

			items.forEach(function(item){
				render_item(table, item);
			});
		}
	});
}

function all_items_query() {
	var Item = Parse.Object.extend("Item");
	var query = new Parse.Query(Item);
	query.equalTo("menu", menu);
	return query;
}

function render_item(table, item) {	
	var itemRow = $(".itemRow", $(".templates")).clone().attr("data-parse-id", item.id);
	$(".itemName", itemRow).text(item.get("name"));
	$(".itemPrice", itemRow).text(item.get("price"));
	$(".itemDescription", itemRow).text(item.get("description"));
	table.append(itemRow);
}

function render_no_items(table) {
	var noItemsRow = $(".noItemsRow", $(".templates")).clone();
	table.append(noItemsRow);
}