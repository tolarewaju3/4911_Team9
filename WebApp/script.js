$(document).ready(function() {
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");	
	$("#login").click(function() {
		var username = $("#username").val();
		var password = $("#password").val();
		Parse.User.logIn(username, password, {
			success: function(user) {
				$("#invalidCredentials").hide();
				alert("Login Successful!");
			},
			error: function(user, error) {
				$("#invalidCredentials").show();
			}
		});
	});
});