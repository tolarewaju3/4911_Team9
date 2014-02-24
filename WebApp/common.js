var unknownErrorText = "An error occured. Please try again later.";
var invalidLocationText = "Invalid location.";
var invalidEmailText = "Invalid email.";
var invalidPasswordText = "Invalid password.";
var validTaxPctText = "Invalid tax percentage.";

function validLocation(location) {
	return location != "";
}

function validEmail(email) {
	return email != "";
}

function validPassword(password) {
	return password != "";
}

function validTaxPct(taxPct) {
	return taxPct != "";
}