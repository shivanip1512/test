
function toggleShowSharedPoints(el) {
    var on = el.options[el.selectedIndex].value;
    doToggleShowSharedPoints(on === 'true' ? true : false);
}

function doToggleShowSharedPoints(on) {
    if (on) {
        $('#sharedPointsDiv').show();
        $('#allPointsDiv').hide();
    } else {
        $('#sharedPointsDiv').hide();
        $('#allPointsDiv').show();
    }
}
