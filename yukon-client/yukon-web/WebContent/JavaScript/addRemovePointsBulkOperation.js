
function toggleShowSharedPoints(el) {
    var on = el.options[el.selectedIndex].value;
    doToggleShowSharedPoints(on === 'true' ? true : false);
}

function doToggleShowSharedPoints(on) {
    if (on) {
        jQuery('#sharedPointsDiv').show();
        jQuery('#allPointsDiv').hide();
    } else {
        jQuery('#sharedPointsDiv').hide();
        jQuery('#allPointsDiv').show();
    }
}
