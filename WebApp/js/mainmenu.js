$(document).ready(function() {

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");

	if (!Parse.User.current()) {
		window.location.replace("../");
	}

	$(".logoutButton").click(function() {
		Parse.User.logOut();
		window.location.replace("../");
	});
});