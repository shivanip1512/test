function calculatePreviousReadingDifference(attributeName, readingValue){
	
	var currentValueName = attributeName+"_latestReading";
	var previousReadingsSelect = attributeName+"_previousReading"
	var totalConsumptionName = attributeName+"_total";

	var currentValue = $(currentValueName).value;
	var previousVal = $(previousReadingsSelect).options[$(previousReadingsSelect).selectedIndex].value;
	var totalUsage = readingValue - previousVal;
	$(totalConsumptionName).innerHTML = totalUsage.toFixed(3);
}