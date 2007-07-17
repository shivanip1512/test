function yukonGeneral_moveOptionPositionInSelect(selectElement, direction) {
    var routeIndex = selectElement.selectedIndex;
    if (routeIndex == -1) {
        return false;
    }
    var routeIndex2 = routeIndex + direction;
    if (routeIndex2 == -1 || routeIndex2 == selectElement.options.length) {
        return false;
    }
    var selectList = selectElement;
    var selectedIndex = selectList.selectedIndex;
    var one = selectList.options[routeIndex];
    var two = selectList.options[routeIndex2];
    selectList.options[routeIndex] = new Option(two.text, two.value);
    selectList.options[routeIndex2] = new Option(one.text, one.value);
    selectList.selectedIndex = selectedIndex + direction;
}

function yukonGeneral_updatePrevious(idPrefix, currentUsage) {
  var previousVal = $(idPrefix + '_prevSelect').value;
  var totalUsage = currentUsage - previousVal;
  
  $(idPrefix + '_totalConsumption').innerHTML = totalUsage.toFixed(3);
}

