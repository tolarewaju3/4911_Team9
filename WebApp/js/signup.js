$(document).ready(function() {

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");	

	if (Parse.User.current()) {
		window.location.replace("./home.html")
	}

	$(".signUpForm").submit(function(event) {
		event.preventDefault();
 
		var duplicateEmailText = "That email address is already in use. Please log in or sign up with a different email address.";

		var location = $(".locationField").val();
		var taxPct = $(".taxPctField").val();
		var email = $(".emailField").val();
		var password = $(".passwordField").val();
		var state = $(".stateSelect").val();
		var address = $(".addressField").val();
		var zipCode = $(".zipField").val();

		if (validateSettings(location, taxPct, email, password, address, zipCode, this)) {
			var user = new Parse.User();
			user.set("location", location);
			user.set("taxPct", taxPct);
			user.set("username", email);
			user.set("email", email);
			user.set("password", password);
			user.set("state", state);
			user.set("address", address);
			user.set("zipCode", zipCode);
			user.set("hasMenu", false);

			user.signUp(null, {
				success: function(user) {
					window.location.replace("./mainmenu.html")
				},
				error: function(user, error) {
					if (error.code == 202)
						signupError(duplicateEmailText);
					else {
						signupError(unknownErrorText);
					}
				}
			});
		}

		function signupError(message) {
			$(".signupError").removeClass("positive").addClass("negative");
			$(".signupError").text(message)
			$(".signupError").show();
		}
	});
});