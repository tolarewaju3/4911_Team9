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
	$(".stateSelect").val(currentUser.get("state"));
	$(".addressField").val(currentUser.get("address"));
	$(".zipField").val(currentUser.get("zipCode"));

	$(".editSettingsForm").submit(function(event) {
		event.preventDefault();

		var location = $(".locationField").val();
		var taxPct = $(".taxPctField").val();
		var email = $(".emailField").val();
		var password = $(".passwordField").val();
		var state = $(".stateSelect").val();
		var address = $(".addressField").val();
		var zipCode = $(".zipField").val();

		var passwordChanged = password != "";

		if ((passwordChanged && validateSettings(location, taxPct, email, password, address, zipCode, this))
			|| (!passwordChanged &&validateSettingsBesidesPassword(location, taxPct, email, address, zipCode, this))) {
			var self = this;

			currentUser.set("location", location);
			currentUser.set("taxPct", taxPct);
			currentUser.set("username", email);
			currentUser.set("email", email);
			currentUser.set("state", state);
			currentUser.set("address", address);
			currentUser.set("zipCode", zipCode);
			if (passwordChanged) {
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
});