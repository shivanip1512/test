function calculatePreviousReadingDifference (attributeName, readingValue) {
    
    var currentValueName = attributeName + "_latestReading",
        previousReadingsSelect = attributeName + "_previousReading",
        totalConsumptionName = attributeName + "_total",
        prevReadingSelectObj = jQuery('#' + previousReadingsSelect)[0],
        previousVal = prevReadingSelectObj.options[prevReadingSelectObj.selectedIndex].value,
        totalUsage = readingValue - previousVal;
    jQuery('#' + totalConsumptionName).html(totalUsage.toFixed(3));
}