$(document).ready(function() {
	
	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	$(".signupForm").submit(function(event) {

		var unknownErrorText = "An error occured. Please try again later.";
		var duplicateEmailText = "That email address is already in use. Please log in or sign up with a different email address.";
		var invalidLocationText = "Invalid location.";
		var invalidEmailText = "Invalid email.";
		var invalidPasswordText = "Invalid password.";

		var location = $(".locationField").val();
		var email = $(".emailField").val();
		var password = $(".passwordField").val();

		if (!validLocation(location)) {
			signupError(invalidLocationText);
		}

		else if (!validEmail(email)) {
			signupError(invalidEmailText);
		}

		else if (!validPassword(password)) {
			signupError(invalidPasswordText);
		}

		else {
			var user = new Parse.User();
			user.set("location", location);
			user.set("username", email);
			user.set("email", email);
			user.set("password", password);

			user.signUp(null, {
				success: function(user) {

				},
				error: function(user, error) {
					if (error.code == 202)
						signupError(duplicateEmailText);
					else {
						signupError(unknownErrorText);
					}
				}
			})
		}

		event.preventDefault();

		function signupError(message) {
			$(".signupError").text(message)
			$(".signupError").show();
		}

		function validLocation(location) {
			return location != "";
		}

		function validEmail(email) {
			return email != "";
		}

		function validPassword(password) {
			return password != "";
		}
	});
});