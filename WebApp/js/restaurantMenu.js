$(document).ready(function() {

	var successfulChangeText = "Changes Saved Successfully!"

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	
	var currentUser = Parse.User.current();

	if (currentUser) {
		var hasMenu = currentUser.get("hasMenu");
		if (hasMenu) {
			$('.title').html("Modify Menu");
		} else {
			$('.title').html("Add Menu");
		}
		


	} else {
		window.location.replace("../");
	}
});