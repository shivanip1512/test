var layerLeftBnd = 8;
var layerHorDist = 50
var tenMinEqlLen = 3;
var layerRightBnd = layerLeftBnd + Math.floor(tenMinEqlLen * (24 * 6 - 1));

var arrowBottomBnd = -32;
var thermometerLen = 100;
var arrowTopBnd = arrowBottomBnd - thermometerLen;

var thermMode = 'C';	// Thermostat mode flag, 'C'/'H'
var tempUnit = 'F';     // Temperature mode, 'F'/'C'
var lowerLimit = 45;  //always specified in fahrenheit
var upperLimit = 88;  //always specified in fahrenheit

var thermostats = ['', 'MovingLayer1', 'MovingLayer2', 'MovingLayer3', 'MovingLayer4'];
var tempCArrows = ['', 'div1C', 'div2C', 'div3C', 'div4C'];
var tempHArrows = ['', 'div1H', 'div2H', 'div3H', 'div4H'];
var timeFields = ['', 'time1', 'time2', 'time3', 'time4'];
var timeMinFields = ['', 'time1min', 'time2min', 'time3min', 'time4min'];
var tempFields = ['', 'temp1', 'temp2', 'temp3', 'temp4'];
var tempDisplayFields = ['', 'tempdisp1', 'tempdisp2', 'tempdisp3', 'tempdisp4'];
var tempDisplayFahrenheitFields = ['tempdisp1F', 'tempdisp2F', 'tempdisp3F', 'tempdisp4F'];
var tempDisplayCelsiusFields = ['tempdisp1C', 'tempdisp2C', 'tempdisp3C', 'tempdisp4C'];
var tempInputFields = ['', 'tempin1', 'tempin2', 'tempin3', 'tempin4'];
var checkboxes = ['', 'WakeEnabled', 'LeaveEnabled', 'ReturnEnabled', 'SleepEnabled'];

function updateLayout(hour1, min1, temp1C, temp1H, hour2, min2, temp2C, temp2H, hour3, min3, temp3C, temp3H, hour4, min4, temp4C, temp4H) {
  moveLayer(1, hour1, min1);
  moveTempArrows(1, temp1C, temp1H);
  showEitherTemp(1, temp1C, temp1H);
  moveLayer(2, hour2, min2);
  moveTempArrows(2, temp2C, temp2H);
  showEitherTemp(2, temp2C, temp2H);
  moveLayer(3, hour3, min3);
  moveTempArrows(3, temp3C, temp3H);
  showEitherTemp(3, temp3C, temp3H);
  moveLayer(4, hour4, min4);
  moveTempArrows(4, temp4C, temp4H);
  showEitherTemp(4, temp4C, temp4H);
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

function showEitherTemp(idx, tempCool, tempHeat) {
  if (thermMode == 'C') {
    showTemp(idx, tempCool);
  } else {
    showTemp(idx, tempHeat);
  }
}

function showTime(s, txt, idx) {
  var curPos = parseInt(s.style.left, 10);
  
  var timeVal = Math.floor((curPos + (idx - 1) * layerHorDist - layerLeftBnd) / tenMinEqlLen) * 10;
  var formattedValue = timeValToStr(timeVal); 
  txt.value = formattedValue;
  $(timeMinFields[idx]).value = timeVal;
  
}

function handleUpdateTemp(idx) {
  var a = document.getElementById((thermMode == 'C')? tempCArrows[idx] : tempHArrows[idx]);
  var curPos = parseInt(a.style.top, 10);
  var temp = Math.floor((arrowBottomBnd - curPos) / thermometerLen * (upperLimit - lowerLimit)) + lowerLimit;
  showTemp(idx, temp);
}

function showTemp(idx, tempF) {
  var div = document.getElementById(tempDisplayFields[idx]);
  div.innerHTML = getConvertedTemp(tempF, tempUnit);
  $(tempInputFields[idx]).value = getConvertedTemp(tempF, tempUnit);
  $(tempFields[idx]).value = tempF;
  
  var fSpan = $(tempDisplayFahrenheitFields[idx-1]);
  var cSpan = $(tempDisplayCelsiusFields[idx-1]);
  if(tempUnit == 'F') {
    fSpan.show();
    cSpan.hide();
  } else {
    fSpan.hide();
    cSpan.show();
  }  
}

function moveTempArrow(divIdArrow, newTemp) {
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
    moveTempArrow(tempCArrows[idx], tempC);
    showTemp(idx, tempC);
  }
  if (tempH) {
    moveTempArrow(tempHArrows[idx], tempH);
    showTemp(idx, tempH);
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
  var timeStr = timeFormatter.formatTime(val);
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

function tempChange(idx) {
  var fields = (thermMode == 'C') ? tempCArrows : tempHArrows;
  var temp = getFahrenheitTemp($(tempInputFields[idx]).value, tempUnit);
  moveTempArrow(fields[idx], temp);
  showTemp(idx, temp);
} 


function setTempUnits(newUnit){
    
    var tempUnitField = $('temperatureUnit');
    var currentTempUnit = $F(tempUnitField);

    if(newUnit == currentTempUnit){
        return;
    }

    tempUnitField.value = newUnit;
    tempUnit = newUnit;

    convertFieldTemp('tempin1', currentTempUnit, newUnit);
    handleUpdateTemp(1);
    convertFieldTemp('tempin2', currentTempUnit, newUnit);
    handleUpdateTemp(2);
    convertFieldTemp('tempin3', currentTempUnit, newUnit);
    handleUpdateTemp(3);
    convertFieldTemp('tempin4', currentTempUnit, newUnit);
    handleUpdateTemp(4);
    
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

function updateMode(mode) {

    $('mode').value = mode;
    var cool = (mode == 'COOL');
    
    // Save the current settings for the mode we were just on
    getCurrentSchedule(currentTimePeriod, (cool)? 'HEAT' : 'COOL');
    
    // Show/hide the blue/red/gray arrows based on mode
    if(cool) {
        thermMode = 'C';
    
        $('arrow1C').show();
        $('arrow1C_Gray').hide();
        $('arrow1H').hide();
        $('arrow1H_Gray').show();

        $('arrow2C').show();
        $('arrow2C_Gray').hide();
        $('arrow2H').hide();
        $('arrow2H_Gray').show();

        $('arrow3C').show();
        $('arrow3C_Gray').hide();
        $('arrow3H').hide();
        $('arrow3H_Gray').show();

        $('arrow4C').show();
        $('arrow4C_Gray').hide();
        $('arrow4H').hide();
        $('arrow4H_Gray').show();
    } else {
        thermMode = 'H';
        
        $('arrow1C').hide();
        $('arrow1C_Gray').show();
        $('arrow1H').show();
        $('arrow1H_Gray').hide();
        
        $('arrow2C').hide();
        $('arrow2C_Gray').show();
        $('arrow2H').show();
        $('arrow2H_Gray').hide();
        
        $('arrow3C').hide();
        $('arrow3C_Gray').show();
        $('arrow3H').show();
        $('arrow3H_Gray').hide();
        
        $('arrow4C').hide();
        $('arrow4C_Gray').show();
        $('arrow4H').show();
        $('arrow4H_Gray').hide();
    
    }
   
    // Load the settings for the new mode
    setCurrentSchedule(currentTimePeriod);
     
}

function getCurrentSchedule(timePeriod, currentCoolHeat) {

    var timeTempArray = $A();

    // Get each time/temp pair from UI
    for(var i=1;i<5;i++) {
        var time = $F('time' + i + 'min');
        var temp = $F('tempin' + i);
        var timeTemp = $H();
        timeTemp.time = time;
        timeTemp.temp = temp;
        timeTempArray[i-1] = timeTemp;
    }

    // Add array of time/temp to schedules for current heatCool and timePeriod   
    var coolHeat = currentCoolHeat;     
    if(coolHeat == null) {
        coolHeat = $RF('coolHeat');
    }
    var timePeriodHash = schedules[coolHeat];
    if(timePeriodHash == null) {
        timePeriodHash = $H();
    }

    timePeriodHash[timePeriod] = timeTempArray;

    schedules[coolHeat] = timePeriodHash; 

    // Store the json string into the form field        
    $('schedules').value = schedules.toJSON();

}

function setCurrentSchedule(timePeriod) {

    // Get the array of time/temp for the current coolHeat setting and timePeriod
    var coolHeat = $RF('coolHeat');
    var timeTempArray = schedules[coolHeat][timePeriod];

    // Set the time/temp values
    if(timeTempArray != null) {
        for(var i=1;i<5;i++) {
            var timeVal = timeTempArray[i-1].time;
            $('time' + i + 'min').value = timeVal;
            $('time' + i).value = timeValToStr(timeVal);
            $('tempin' + i).value = timeTempArray[i-1].temp;
        }
        
    
        // Update the slidey bar UI
        tempChange(1);
        timeChange($('time1'),1);
        tempChange(2);
        timeChange($('time2'),2);
        tempChange(3);
        timeChange($('time3'),3);
        tempChange(4);
        timeChange($('time4'),4);
    }
    
}

function switchSchedule(timePeriod) {

    $('weekdayText').hide();
    $('saturdayText').hide();
    $('sundayText').hide();
    $('weekdayLink').hide();
    $('saturdayLink').hide();
    $('sundayLink').hide();
    $('applyToWeekendSpan').hide();

    if(timePeriod == 'WEEKDAY') {
        $('weekdayText').show();
        $('saturdayLink').show();
        $('sundayLink').show();
        $('applyToWeekendSpan').show();
    } else if(timePeriod == 'SATURDAY') {
        $('weekdayLink').show();
        $('saturdayText').show();
        $('sundayLink').show();
    } else if(timePeriod == 'SUNDAY') {
        $('weekdayLink').show();
        $('saturdayLink').show();
        $('sundayText').show();
    }
    
    $('timeOfWeek').value = timePeriod;
    
    // Store the current settings and update the UI to the settings for the selected timePeriod
    getCurrentSchedule(currentTimePeriod);
    setCurrentSchedule(timePeriod);
    
    currentTimePeriod = timePeriod;

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

function applySettingsToWeekend(input) {

    var apply = $F(input);
    if(apply == null) {
        apply = false;
    }
    
    if(apply) {
        
        $('saturdayText').hide();
        $('sundayText').hide();
        $('saturdayLink').hide();
        $('sundayLink').hide();
        
        getCurrentSchedule('SATURDAY');
        getCurrentSchedule('SUNDAY');
        
    } else {
        $('saturdayLink').show();
        $('sundayLink').show();
    }
}
    
