var unknownErrorText = "An error occured. Please try again later.";
var invalidLocationText = "Please enter a location name.";
var invalidEmailText = "Please use a valid email address.";
var invalidPasswordText = "Password must be between 6 and 20 characters and may only use letters, numbers, and underscores.";
var validTaxPctText = "Tax percentage must be a decimal to at most two decimal places (e.g. 7.5).";

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

$(document).ready(function() {
	$('.banner').on('click', function() {
		window.location.replace("./home.html")
	});
});
