$(document).ready(function() {
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");	
	$(".loginForm").submit(function(event) {
		var username = $(".emailField").val();
		var password = $(".passwordField").val();
		Parse.User.logIn(username, password, {
			success: function(user) {
				alert("Login Successful!"); //placeholder until menu page is written
			},
			error: function(user, error) {
				$(".invalidCredentials").show();
			}
		});
		event.preventDefault();
	});
});