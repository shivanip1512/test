 /** Calculate previous reading difference
     *  @param {string} attributeName - Name of the attribute
     *  @param {number} readingValue - Current reading value  
    */
function calculatePreviousReadingDifference (attributeName, readingValue) {
    
    var currentValueName = attributeName + "_latestReading",
        previousReadingsSelect = attributeName + "_previousReading",
        totalConsumptionName = attributeName + "_total",
        prevReadingSelectObj = $('#' + previousReadingsSelect)[0],
        previousVal = prevReadingSelectObj.options[prevReadingSelectObj.selectedIndex].value,
        totalUsage = readingValue - previousVal;
    $('#' + totalConsumptionName).html(totalUsage.toFixed(3));
}