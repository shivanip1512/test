
function toggleShowSharedPoints(el) {
	var on = eval(el.options[el.selectedIndex].value);
	doToggleShowSharedPoints(on);
}

function doToggleShowSharedPoints(on) {
	if (on) {
		$('sharedPointsDiv').show();
		$('allPointsDiv').hide();
	} else {
		$('sharedPointsDiv').hide();
		$('allPointsDiv').show();
	}
}

function doToggleUpdatePoints(on) {
	if (on) {
		$('updatePointsSelectEl').enable();
	} else {
		$('updatePointsSelectEl').disable();
	}
}
