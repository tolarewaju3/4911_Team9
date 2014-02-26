$(document).ready(function() {

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");	

	if (Parse.User.current()) {
		window.location.replace("./home.html")
	}

	$(".signUpForm").submit(function(event) {
		event.preventDefault();

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