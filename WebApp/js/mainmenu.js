$(document).ready(function() {

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");

	var currentUser = Parse.User.current();
	
	if (currentUser) {
		var menu = currentUser.get("menu");
		if (menu) {
			$('.menu').html("Modify Menu");
		} else {
			$('.menu').html("Add Menu");
		}
	} else {
		window.location.replace("./home.html");
	}

	$(".logoutButton").click(function() {
		Parse.User.logOut();
		window.location.replace("./home.html");
	});
});