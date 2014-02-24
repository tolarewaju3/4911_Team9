$(document).ready(function() {

	var successfulChangeText = "Changes Saved Successfully!"

	Parse.initialize("X2S2BQQcTvCtg1bFVtHViTyy4bKXCvWrOuahnMut", "6m2FrVnFYbapf0mRID6nSdsDeAxOoNcA9On30fSV");
	var currentUser = Parse.User.current();

	if (!currentUser) {
		window.location.replace("../");
	}

	$(".locationField").val(currentUser.get("location"));
	$(".taxPctField").val(currentUser.get("taxPct"));
	$(".emailField").val(currentUser.get("email"));
	$(".paypalEmailField").val(currentUser.get("paypalEmail"));

	$(".detailsForm").submit(function(event) {
		event.preventDefault();

		var location = $(".locationField").val();
		var taxPct = $(".taxPctField").val();

		if (!validLocation(location)) {
			displayFeedback(this, false, invalidLocationText);
		}

		else if (!validTaxPct(taxPct)) {
			displayFeedback(this, false, invalidTaxPctText);
		}

		else {
			var self = this;

			currentUser.set("location", location);
			currentUser.set("taxPct", taxPct);
			currentUser.save(null, {
				success: function() {
					displayFeedback(self, true, successfulChangeText);
				},
				error: function() {
					displayFeedback(self, false, unknownErrorText);
				}
			});
		}
	});

	$(".loginForm").submit(function(event) {
		var duplicateEmailText = "That email address is already in use. Please choose another."

		event.preventDefault();

		var email = $(".emailField").val();
		var password = $(".passwordField").val();

		if (!validEmail(email)) {
			displayFeedback(this, false, invalidEmailText);
		}

		else if (password != "" && !validPassword(password)) {
			displayFeedback(this, false, invalidPasswordText);
		}

		else {
			var self = this;

			currentUser.set("username", email);
			currentUser.set("email", email);
			if (password != "") {
				currentUser.set("password", password);
			}
			currentUser.save(null, {
				success: function() {
					displayFeedback(self, true, successfulChangeText);
				},
				error: function() {
					displayFeedback(self, false, unknownErrorText);
				}
			});
		}
	});

	$(".paypalForm").submit(function(event) {
		event.preventDefault();

		var paypalEmail = $(".paypalEmailField").val();

		if (!validEmail(paypalEmail)) {
			displayFeedback(this, false, invalidEmailText);
		}

		else {
			var self = this;

			currentUser.set("paypalEmail", paypalEmail);
			currentUser.save(null, {
				success: function() {
					displayFeedback(self, true, successfulChangeText);
				},
				error: function() {
					displayFeedback(self, false, unknownErrorText);
				}
			});
		}
	});

	function displayFeedback(section, positive, message) {
		var allFeedback = $(".feedback");
		allFeedback.hide();

		var feedbackObj = $(".feedback", section);
		if (positive) {
			feedbackObj.removeClass("negative").addClass("positive");
		}
		else {
			feedbackObj.removeClass("positive").addClass("negative");
		}
		feedbackObj.text(message);
		feedbackObj.show();
	}
});