var layerLeftBnd = 8;
var layerHorDist = 50
var tenMinEqlLen = 3;
var layerRightBnd = layerLeftBnd + Math.floor(tenMinEqlLen * (24 * 6 - 1));

var arrowBottomBnd = -32;
var thermometerLen = 100;
var arrowTopBnd = arrowBottomBnd - thermometerLen;

var heatMode = 'H';
var coolMode = 'C';

var currentTimePeriod = 'WEEKDAY';
var currentScheduleMode = 'WEEKDAY_SAT_SUN';

var tempUnit = 'F';     // Temperature mode, 'F'/'C'
var lowerLimitCool;  //always specified in fahrenheit, to be set by view init based on selected thermostat type
var upperLimitCool;
var lowerLimitHeat;
var upperLimitHeat;

var thermostats = ['', 'MovingLayer1', 'MovingLayer2', 'MovingLayer3', 'MovingLayer4'];
var tempCArrows = ['', 'div1C', 'div2C', 'div3C', 'div4C'];
var tempHArrows = ['', 'div1H', 'div2H', 'div3H', 'div4H'];
var timeFields = ['', 'time1', 'time2', 'time3', 'time4'];
var timeMinFields = ['', 'time1min', 'time2min', 'time3min', 'time4min'];
var tempCFields = ['', 'tempC1', 'tempC2', 'tempC3', 'tempC4'];
var tempHFields = ['', 'tempH1', 'tempH2', 'tempH3', 'tempH4'];
var tempHInputFields = ['', 'tempHin1', 'tempHin2', 'tempHin3', 'tempHin4'];
var tempCInputFields = ['', 'tempCin1', 'tempCin2', 'tempCin3', 'tempCin4'];
var checkboxes = ['', 'WakeEnabled', 'LeaveEnabled', 'ReturnEnabled', 'SleepEnabled'];

function updateLayout(hour1, min1, temp1C, temp1H, hour2, min2, temp2C, temp2H, hour3, min3, temp3C, temp3H, hour4, min4, temp4C, temp4H) {
  moveLayer(1, hour1, min1);
  moveTempArrows(1, temp1C, temp1H);
  moveLayer(2, hour2, min2);
  moveTempArrows(2, temp2C, temp2H);
  moveLayer(3, hour3, min3);
  moveTempArrows(3, temp3C, temp3H);
  moveLayer(4, hour4, min4);
  moveTempArrows(4, temp4C, temp4H);
}


function showTimeWake() {
  var s = document.getElementById('MovingLayer1');
  var txt = document.getElementById('time1');
  showTime(s,txt,1);
}

function showTimeLeave() {
  var s = document.getElementById('MovingLayer2');
  var txt = document.getElementById('time2');
  showTime(s,txt,2);
}

function showTimeReturn() {
  var s = document.getElementById('MovingLayer3');
  var txt = document.getElementById('time3');
  showTime(s,txt,3);
}

function showTimeSleep() {
  var s = document.getElementById('MovingLayer4');
  var txt = document.getElementById('time4');
  showTime(s,txt,4);
}

function showTimeOccupied() {
  var s = document.getElementById('MovingLayer1');
  var txt = document.getElementById('time1');
  showTime(s,txt,1);
}

function showTimeUnoccupied() {
  var s = document.getElementById('MovingLayer4');
  var txt = document.getElementById('time4');
  showTime(s,txt,4);
}

function showTime(s, txt, idx) {
  var curPos = parseInt(s.style.left, 10);
  
  var timeVal = Math.floor((curPos + (idx - 1) * layerHorDist - layerLeftBnd) / tenMinEqlLen) * 10;
  var formattedValue = timeValToStr(timeVal); 
  txt.value = formattedValue;
  $(timeMinFields[idx]).value = timeVal;
  
}

function handleUpdateCoolTemp(idx) {
  var a = document.getElementById(tempCArrows[idx]);
  var curPos = parseInt(a.style.top, 10);
  var temp = Math.floor((arrowBottomBnd - curPos) / thermometerLen * (upperLimitCool - lowerLimitCool)) + lowerLimitCool;
  showTemp(idx, temp, coolMode);
}

function handleUpdateHeatTemp(idx, mode) {
	var a = document.getElementById(tempHArrows[idx]);
	var curPos = parseInt(a.style.top, 10);
	var temp = Math.floor((arrowBottomBnd - curPos) / thermometerLen * (upperLimitHeat - lowerLimitHeat)) + lowerLimitHeat;
	showTemp(idx, temp, heatMode);
}

function showTemp(idx, tempF, mode) {
  
  var inputFields = (mode == coolMode) ? tempCInputFields : tempHInputFields;
  $(inputFields[idx]).value = getConvertedTemp(tempF, tempUnit);

  var showTempFields = (mode == coolMode) ? tempCFields : tempHFields;
  $(showTempFields[idx]).value = tempF;
  
}

function moveTempArrow(divIdArrow, newTemp, lowerLimit, upperLimit) {
	
  var arrow = $(divIdArrow);
  if (newTemp > upperLimit) {
    newTemp = upperLimit;
  }
  if (newTemp < lowerLimit) {
    newTemp = lowerLimit;
  }
  var pos = Math.floor((lowerLimit - newTemp) / (upperLimit - lowerLimit) * thermometerLen) + arrowBottomBnd;
  arrow.style.top = pos + 'px';
}

function moveTempArrows(idx, tempC, tempH) {
	
  if (tempC) {
    moveTempArrow(tempCArrows[idx], tempC, lowerLimitCool, upperLimitCool);
    showTemp(idx, tempC, coolMode);
  }
  
  if (tempH) {
    moveTempArrow(tempHArrows[idx], tempH, lowerLimitHeat, upperLimitHeat);
    showTemp(idx, tempH, heatMode);
  }
}


function moveLayer(idx, hour, minute) {
var layer = document.getElementById(thermostats[idx]);
var offset = (hour * 6 + Math.floor(minute / 10)) * tenMinEqlLen;
var left = layerLeftBnd + offset - (idx-1) * layerHorDist;
layer.style.left = left + 'px';
}


function timeValToStr(val)
{
  var timeStr = timeFormatter.formatTime(val, true);
  return timeStr;
}

function timeStrToVal(str)
{
  var val = timeFormatter.parseTime(str);
  return val;
}


function getLeftBound(idx) {
  var horDist = 0;
  for (i = idx - 1; i >= 1; i--) {
    horDist += layerHorDist;
    var layer = document.getElementById(thermostats[i]);
    if (layer != null && layer.style.display == '')
      return parseInt(layer.style.left,10) + tenMinEqlLen - horDist;
  }
  return layerLeftBnd - layerHorDist * (idx-1);
}

function getRightBound(idx) {
  var horDist = 0;
  for (i = idx + 1; i <= 4; i++) {
    horDist += layerHorDist;
    var layer = document.getElementById(thermostats[i]);
    if (layer != null && layer.style.display == '')
      return parseInt(layer.style.left,10) - tenMinEqlLen + horDist;
  }
  return layerRightBnd - layerHorDist * (idx-1);
}

function toggleThermostat(idx, on) {
  var layer = document.getElementById(thermostats[idx]);
  if (layer == null) {
    alert("The layer object of this time schedule doesn't exist!");
    return;
  }
  
  document.getElementById(checkboxes[idx]).checked = on;
  if (on) {
    // Show the thermostat layer
    var leftBnd = getLeftBound(idx);
    var rightBnd = getRightBound(idx);
    if (rightBnd - leftBnd < tenMinEqlLen * 2) {
      alert("There is no space for this thermostat! The interval between any two time schedules must be at least 10 minutes. Please adjust the adjacent thermostat(s) and try again");
      return;
    }
    
    layer.style.display = '';
    document.getElementById(timeFields[idx]).disabled = false;
    
    var pos = parseInt(layer.style.left,10);
    if (pos <= leftBnd || pos >= rightBnd)
      layer.style.left = leftBnd + tenMinEqlLen;
    showTime(document.getElementById(thermostats[idx]), document.getElementById(timeFields[idx]), idx);
  }
  else {
    // Hide the thermostat layer
    layer.style.display = 'none';
    document.getElementById(timeFields[idx]).disabled = true;
    document.getElementById(timeFields[idx]).value = "";
  }
}

function timeChange(t, idx) {

  var timeStr = $F(t);

  var val = timeStrToVal(timeStr);
  
  if(val == -1) {
    showTime($(thermostats[idx]), $(timeFields[idx]), idx);
    return;
  }
  
  for (i = idx - 1; i >= 1; i--) {
    var layer = $(thermostats[i]);
    if (layer != null && layer.style.display == '') {
      var prevVal = parseInt($F(timeMinFields[i]));
      if (val <= prevVal) {
        val = prevVal + 10;
      }        
    }
  }
  for (i = idx + 1; i <= 4; i++) {
    var layer = $(thermostats[i]);
    if (layer != null && layer.style.display == '') {
      var nextVal = parseInt($F(timeMinFields[i]));
      if (val >= nextVal) {
        val = nextVal - 10;
      }
    }
  }
  t.value = timeValToStr( val );
  $(timeMinFields[idx]).value = val;
  
  var hour = Math.floor(val / 60);
  var minute = val % 60;
  moveLayer(idx, hour, minute);
}

function tempChange(idx, mode) {
	
  var fields = (mode == coolMode) ? tempCArrows : tempHArrows;
  var inputFields = (mode == coolMode) ? tempCInputFields : tempHInputFields;
  
  var lowerLimit = (mode == coolMode) ? lowerLimitCool : lowerLimitHeat;
  var upperLimit = (mode == coolMode) ? upperLimitCool : upperLimitHeat;
  
  validateTemp(inputFields[idx], idx, mode);
  
  var temp = getFahrenheitTemp($(inputFields[idx]).value, tempUnit);
  moveTempArrow(fields[idx], temp, lowerLimit, upperLimit);
  showTemp(idx, temp, mode);
}

function validateTemp(input, idx, mode) {
    
	var fTemp;
    var currentTemp = $F(input);
    var currentTempUnit = tempUnit;
    
    var lowerLimit = (mode == coolMode) ? lowerLimitCool : lowerLimitHeat;
	var upperLimit = (mode == coolMode) ? upperLimitCool : upperLimitHeat;
    
    if(isNaN(currentTemp)){
    	var existingTempInputFields = (mode == coolMode) ? tempCFields : tempHFields;
        fTemp = $F(existingTempInputFields[idx]);
    } else {
    	fTemp = getFahrenheitTemp(currentTemp, currentTempUnit);
    }
    
    if(fTemp < lowerLimit) {
    	fTemp = lowerLimit;
    } else if(fTemp > upperLimit) {
    	fTemp = upperLimit;
    }
    
    // Convert current temp to celsius if needed
    var finalTemp = getConvertedTemp(fTemp, currentTempUnit);
    
    $(input).value = finalTemp;
}


function setTempUnits(newUnit){
    
    var tempUnitField = $('temperatureUnit');
    var currentTempUnit = $F(tempUnitField);

    if(newUnit == currentTempUnit){
        return;
    }

    tempUnitField.value = newUnit;
    tempUnit = newUnit;

    convertFieldTemp('tempCin1', currentTempUnit, newUnit);
    handleUpdateCoolTemp(1);
    convertFieldTemp('tempCin2', currentTempUnit, newUnit);
    handleUpdateCoolTemp(2);
    convertFieldTemp('tempCin3', currentTempUnit, newUnit);
    handleUpdateCoolTemp(3);
    convertFieldTemp('tempCin4', currentTempUnit, newUnit);
    handleUpdateCoolTemp(4);

    convertFieldTemp('tempHin1', currentTempUnit, newUnit);
    handleUpdateHeatTemp(1);
    convertFieldTemp('tempHin2', currentTempUnit, newUnit);
    handleUpdateHeatTemp(2);
    convertFieldTemp('tempHin3', currentTempUnit, newUnit);
    handleUpdateHeatTemp(3);
    convertFieldTemp('tempHin4', currentTempUnit, newUnit);
    handleUpdateHeatTemp(4);
    
    // Convert values for time periods not currently shown
    var timePeriods;
    if(currentScheduleMode == "WEEKDAY_SAT_SUN"){
    	timePeriods = ["WEEKDAY", "SATURDAY", "SUNDAY"];
    } else if(currentScheduleMode == "WEEKDAY_WEEKEND"){
    	timePeriods = ["WEEKDAY", "WEEKEND"];
    } else {
    	timePeriods = ["WEEKDAY"];
    }
    
    for(var i = 0; i < timePeriods.size(); i++){
        var timePeriod = timePeriods[i];
        if(timePeriod != currentTimePeriod){ 	//current time period has been dealt with above
            var timeTemps = schedules.get('season')[timePeriod]; 
            for(var j = 0; j < 4; j++){
                var coolTempF = getFahrenheitTemp(timeTemps[j].get('coolTemp'), currentTempUnit);
                timeTemps[j].set('coolTemp', getConvertedTemp(coolTempF, newUnit));
                var heatTempF = getFahrenheitTemp(timeTemps[j].get('heatTemp'), currentTempUnit);
                timeTemps[j].set('heatTemp', getConvertedTemp(heatTempF, newUnit));
            }
        }
    }
    
    //update UI to display correct temp unit
    if('C' == newUnit) {
        $('celsiusLink').hide();
        $('celsiusSpan').show();
        $('fahrenheitSpan').hide();
        $('fahrenheitLink').show();
        
    } else {
        $('celsiusLink').show();
        $('celsiusSpan').hide();
        $('fahrenheitSpan').show();
        $('fahrenheitLink').hide();
    }
    
}

function convertFieldTemp(fieldId, currentUnit, newUnit){
    
    var field = $(fieldId);
    var currentTemp = $F(field);
    
    if(!isNaN(currentTemp) && currentTemp != '') {
        currentTemp = getFahrenheitTemp(currentTemp, currentUnit);
        currentTemp = getConvertedTemp(currentTemp, newUnit);
        
        field.value = currentTemp;
    }
    
    $(fieldId + 'C').toggle();
    $(fieldId + 'F').toggle();
    
}

function getCurrentSchedule(timePeriod) {

    var timeTempArray = $A();

    // Get each time/temp/temp set from UI for cool AND heat
    for(var i=1;i<5;i++) {
        timeTempArray[i-1] = $H({
            'time': $F('time' + i + 'min'),
            'coolTemp': $F('tempCin' + i),
            'heatTemp': $F('tempHin' + i)
        });
    }

    // Add array of cool time/temp to schedules for current timePeriod   
    var timePeriodHash = schedules.get('season');
    if(timePeriodHash == null) {
    	timePeriodHash = {};
    }

    timePeriodHash[timePeriod] = timeTempArray;

    schedules.set('season', timePeriodHash); 

    // Store the json string into the form field        
    $('schedules').value = Object.toJSON(schedules);

}

function setCurrentSchedule(timePeriod) {
	
    // Get the array of time/temp for the current coolHeat setting and timePeriod
    var timeTempArray = schedules.get('season')[timePeriod];

    // Set the time/temp values
    if(timeTempArray != null) {
        for(var i=1;i<5;i++) {
            var timeVal = timeTempArray[i-1].get('time');
            $('time' + i + 'min').value = timeVal;
            $('time' + i).value = timeValToStr(timeVal);
            $('tempCin' + i).value = timeTempArray[i-1].get('coolTemp');
            $('tempHin' + i).value = timeTempArray[i-1].get('heatTemp');
        }
        
        // Update the slidey bar UI
        timeChange($('time1'),1);
        timeChange($('time2'),2);
        timeChange($('time3'),3);
        timeChange($('time4'),4);

        tempChange(1, coolMode);
        tempChange(2, coolMode);
        tempChange(3, coolMode);
        tempChange(4, coolMode);
    	
    	tempChange(1, heatMode);
    	tempChange(2, heatMode);
    	tempChange(3, heatMode);
    	tempChange(4, heatMode);
    }
    
}

// Function to get the current value of a radio button group
function $RF(radioId) {

    var radioGroup = $(radioId).name;
    var radioForm = $(radioId).form;
 
    var checked = $(radioForm).getInputs('radio', radioGroup).find(
        function(re) {return re.checked;}
    );
    return (checked) ? $F(checked) : null;
}

function changeTimePeriod(timePeriod) {

    // Store the current settings and update the UI to the settings for the selected timePeriod
    getCurrentSchedule(currentTimePeriod);
    setCurrentSchedule(timePeriod);
    
    currentTimePeriod = timePeriod;

    updateTimePeriodUI();
}

function changeScheduleMode() {

	if(!alertModeChange()) {
		// reselect the last mode
		$("radio" + currentScheduleMode).checked = "checked";
        return false;
    }
    
    var mode = $RF('radioALL');
    currentScheduleMode = mode;
	
    getCurrentSchedule('WEEKEND');
	getCurrentSchedule('SATURDAY');
	getCurrentSchedule('SUNDAY');
    
    // Always default to weekday selected when mode changes
    changeTimePeriod('WEEKDAY');
    return true;
}

function updateTimePeriodUI() {
	
	// Hide everything
	$('everyDayText').hide();
	$('weekdayText').hide();
	$('weekdayLink').hide();
	$('saturdayText').hide();
	$('saturdayLink').hide();
	$('sundayText').hide();
	$('sundayLink').hide();
	$('weekendText').hide();
	$('weekendLink').hide();

	if(currentScheduleMode == 'ALL') {
		$('everyDayText').show();
    } else if(currentScheduleMode == 'WEEKDAY_SAT_SUN') {

    	if(currentTimePeriod == 'WEEKDAY') {
	    	$('weekdayText').show();
	    	$('saturdayLink').show();
	    	$('sundayLink').show();
    	} else if(currentTimePeriod == 'SATURDAY') {
    		$('weekdayLink').show();
    		$('saturdayText').show();
    		$('sundayLink').show();
    	} else if(currentTimePeriod == 'SUNDAY') {
    		$('weekdayLink').show();
    		$('saturdayLink').show();
    		$('sundayText').show();
    	}

    } else if(currentScheduleMode == 'WEEKDAY_WEEKEND') {

    	if(currentTimePeriod == 'WEEKDAY') {
	    	$('weekdayText').show();
	    	$('weekendLink').show();
    	} else if(currentTimePeriod == 'WEEKEND') {
    		$('weekdayLink').show();
    		$('weekendText').show();
    	}
    }
	
}

var viewHasChanged = false;
function viewChanged() {
    viewHasChanged = true;
}
    
