var currentUser;
var successfulChangeText = "Changes Saved Successfully!";
var hasMenu;

$(document).ready(function() {
	
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	currentUser = Parse.User.current();
	if (!currentUser) { window.location.replace("../");}

	hasMenu = currentUser.get("hasMenu");

	if (hasMenu) {
		$('.title').html("Modify Menu");
		show_menu_edit_panels();
		retreive_menu_info();
	} else {
		$('.title').html("Add Menu");
	}

	$('.menuNameForm').submit(function(e) {
		e.preventDefault();
		if (hasMenu) {
			update_menu_name();
		} else {
			create_new_menu();
		}
	});

	$('.deleteMenu').on('click', function(e) {
		e.preventDefault();
		delete_menu();
	});
});

function delete_menu() {
	var menuPointer = currentUser.get("Menu");
	var menuID = menuPointer.id;

	var Menu = Parse.Object.extend("Menu");
	var query = new Parse.Query(Menu);
	query.get(menuID, {
		success: function(menuObject) {
			menuObject.destroy({
				success: function(menuObject) {
					alert("Menu deletion complete.");
					currentUser.unset("Menu");
					currentUser.set("hasMenu", false);
					currentUser.save();
					hasMenu = false;
					window.location.replace("./mainmenu.html");
				},
				error: function(menuObject, error) {
					alert("Menu deletion failed.");
				}
			});
		},
		error: function() {
			displayFeedback(self, false, "We could not find your menu. Please try again later.");
			return null;
		}
	});
}

function show_menu_edit_panels() {
	$('.itemsTable').removeClass('hide');
	$('.actionPanel').removeClass('hide');
}

function create_new_menu() {
	var Menu = Parse.Object.extend("Menu");
	var newMenu = new Menu();

	newMenu.set('name', $('.menuNameField').val());
	newMenu.set('items', []);

	currentUser.set("Menu", newMenu);
	currentUser.set("hasMenu", true);
	hasMenu = true;

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
	var userMenu = currentUser.get("Menu");
	userMenu.set('name', $('.menuNameField').val());

	currentUser.save(null, {
		success: function() {
			alert(successfulChangeText);
		},
		error: function() {
			displayFeedback(self, false, unknownErrorText);
		}
	});
}

function retreive_menu_info() {
	var menuPointer = currentUser.get("Menu");
	var menuID = menuPointer.id;

	var Menu = Parse.Object.extend("Menu");
	var query = new Parse.Query(Menu);
	query.get(menuID, {
		success: function(menuObject) {
			$('.menuNameField').val(menuObject.get("name"));
			load_items_to_table(menuObject);
		},
		error: function() {
			displayFeedback(self, false, unknownErrorText);
			return null;
		}
	});

	$('.helperText').html("Use the buttons on the right to add or remove items. When you're finished, hit save changes.");
}

function load_items_to_table(menu) {
	var items = menu.get("items");

	if (items && items.length > 0) {
		$('.noItems').addClass('hide');

		var table = $('.tableBody');

		items.forEach(function(){
			var item = $(this);
			table.append(
				"<tr>" +
					"<td>" + item.get("name") + "</td>" +
					"<td>" + item.get("price") + "</td>" +
					"<td>" + item.get("description") + "</td>" +
				"</tr>"
			);
		});
	}
}