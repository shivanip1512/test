

function yukonGeneral_updatePrevious(currentId, previousId, totalId) {
  var currentVal = $(currentId).firstDescendant().innerHTML;
  var previousVal = $(previousId).value;
  var totalUsage = currentVal - previousVal;
  
  $(totalId).innerHTML = totalUsage.toFixed(3);
}

