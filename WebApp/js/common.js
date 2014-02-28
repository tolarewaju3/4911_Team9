var unknownErrorText = "An error occured. Please try again later.";
var invalidLocationText = "Please enter a location name.";
var invalidEmailText = "Please use a valid email address.";
var invalidPasswordText = "Password must be between 6 and 20 characters and may only use letters, numbers, and underscores.";
var invalidTaxPctText = "Tax percentage must be a decimal to at most two decimal places (e.g. 7.5).";
var invalidAddressText = "Please enter an address.";
var invalidZipCodeText = "Please enter a valid five digit or nine digit zip code (e.g. 30305 or 30305-1452).";

function validLocation(location) {
	return location != "";
}

function validEmail(email) {
	var re = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/
	return re.test(email);
}

function validPassword(password) {
	var re = /^\w{6,20}$/
	return re.test(password);
}

function validTaxPct(taxPct) {
	var re = /^(([1-9](\d)*)|[0]?)(\.\d{1,2})?$/
	return re.test(taxPct);
}

function validAddress(address) {
	return address != "";
}

function validZipCode(zipCode) {
	var re = /^\d{5}(-\d{4})?$/
	return re.test(zipCode);
}

function validateSettings(location, taxPct, email, password, address, zipCode, section) {
	var valid = true;

	if (!validateSettingsBesidesPassword(location, taxPct, email, address, zipCode, section)) {
		valid = false;
	}

	else if (!validPassword(password)) {
		displayFeedback(section, false, invalidPasswordText);
		valid = false;
	}

	return valid;
}

function validateSettingsBesidesPassword(location, taxPct, email, address, zipCode, section) {
	var valid = true;

	if (!validLocation(location)) {
		displayFeedback(section, false, invalidLocationText);
		valid = false;
	}

	else if (!validTaxPct(taxPct)) {
		displayFeedback(section, false, invalidTaxPctText);
		valid = false;
	}

	else if(!validEmail(email)) {
		displayFeedback(section, false, invalidEmailText);
		valid = false;
	}

	else if(!validAddress(address)) {
		displayFeedback(section, false, invalidAddressText);
		valid = false;
	}

	else if(!validZipCode(zipCode)) {
		displayFeedback(section, false, invalidZipCodeText);
		valid = false;
	}

	return valid;
}

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

$(document).ready(function() {
	$('header h1').on('click', function() {
		window.location.replace("./home.html")
	});
});
