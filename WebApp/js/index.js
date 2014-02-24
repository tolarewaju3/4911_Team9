$(document).ready(function() {

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");	

	if (Parse.User.current()) {
		window.location.replace("/mainmenu.html");
	}

	$(".loginForm").submit(function(event) {
		event.preventDefault();

		var username = $(".emailField").val();
		var password = $(".passwordField").val();
		Parse.User.logIn(username, password, {
			success: function(user) {
				window.location.replace("/mainmenu.html");
			},
			error: function(user, error) {
				$(".invalidCredentials").show();
			}
		});
	});
});