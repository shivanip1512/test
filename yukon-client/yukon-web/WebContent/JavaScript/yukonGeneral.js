

function yukonGeneral_updatePrevious(idPrefix, currentUsage) {
  var previousVal = $(idPrefix + '_prevSelect').value;
  var totalUsage = currentUsage - previousVal;
  
  $(idPrefix + '_totalConsumption').innerHTML = totalUsage.toFixed(3);
}

