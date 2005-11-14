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
var tempFields = ['', 'temp1', 'temp2', 'temp3', 'temp4'];
var tempDisplayFields = ['', 'tempdisp1', 'tempdisp2', 'tempdisp3', 'tempdisp4'];
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
  txt.value = timeValToStr(Math.floor((curPos + (idx - 1) * layerHorDist - layerLeftBnd) / tenMinEqlLen) * 10);
}

function handleUpdateTemp(idx) {
  var a = document.getElementById((thermMode == 'C')? tempCArrows[idx] : tempHArrows[idx]);
  var curPos = parseInt(a.style.top, 10);
  var temp = Math.floor((arrowBottomBnd - curPos) / thermometerLen * (upperLimit - lowerLimit)) + lowerLimit;
  showTemp(idx, temp);
}

function showTemp(idx, tempF) {
  var div = document.getElementById(tempDisplayFields[idx]);
  div.innerHTML = getConvertedTemp(tempF, tempUnit) + '&deg;' + tempUnit;
  document.getElementById(tempInputFields[idx]).value = getConvertedTemp(tempF, tempUnit);
  document.getElementById(tempFields[idx]).value = tempF;
}

function moveTempArrow(divIdArrow, newTemp) {
  var arrow = document.getElementById(divIdArrow);
  if (newTemp > upperLimit) {
    newTemp = upperLimit;
  }
  if (newTemp < lowerLimit) {
    newTemp = lowerLimit;
  }
  var pos = Math.floor((lowerLimit - newTemp) / (upperLimit - lowerLimit) * thermometerLen) + arrowBottomBnd;
  arrow.style.top = pos;
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
layer.style.left = layerLeftBnd + offset - (idx-1) * layerHorDist;
}


function timeValToStr(val)
{
  if (val < 0) val = 0;
  if (val >= 24 * 60) val = 24 * 60 - 1;
  val = Math.floor(val / 10) * 10;	// Align minute to the multiple of 10
  
  var hour = Math.floor(val / 60);
  var minute = val % 60;
  var ampmStr = "AM";
  if (hour >= 12)
  {
    ampmStr = "PM";
    if (hour > 12) hour = hour - 12;
  }
  var hourStr = "0" + hour;
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + minute;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  
  return hourStr + ":" + minuteStr + " " + ampmStr;
}

function timeStrMilitaryToVal(str)
{
  var time = str.split(":");
  var hour = 0;
  var minute = 0;
  if (time[0]) hour = parseInt(time[0], 10);
  if (time[1]) minute = parseInt(time[1], 10);
  
  if (hour < 0)
  {
    hour = 0;
    minute = 0;
  }
  else if (hour > 23)
  {
    hour = 23;
    minute = 59;
  }
  else
  {
    if (minute < 0) minute = 0;
    if (minute > 59) minute = 59;
  }
  
  minute = Math.floor(minute / 10) * 10;	// Align minute to the multiple of 10
  return hour * 60 + minute;
}

function timeStrToVal(str)
{
  var val = 0;
  
  var ampmStr = str.substr(str.length-2, 2).toUpperCase();
  if (ampmStr == "AM" || ampmStr == "PM")
  {
    val = timeStrMilitaryToVal( str.substr(0, str.length-2) );
    if (ampmStr == "AM")
    {
      if (val >= 12 * 60) val = 12 * 60 - 10;	// AM time cannot exceed 11:50
    }
    else	// ampmStr == "PM"
    {
      if (val >= 13 * 60) val = 12 * 60 - 10;	// PM time cannot exceed 12:50
      if (val < 12 * 60) val += 12 * 60;	// Add 12 hours to PM time unless it's 12:XX PM
    }
  }
  else
    val = timeStrMilitaryToVal( str );
  
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
  var val = timeStrToVal( t.value );
  
  for (i = idx - 1; i >= 1; i--) {
    var layer = document.getElementById(thermostats[i]);
    if (layer != null && layer.style.display == '') {
      var prevVal = timeStrToVal( document.getElementById(timeFields[i]).value );
      if (val <= prevVal) val = prevVal + 10;
    }
  }
  for (i = idx + 1; i <= 4; i++) {
    var layer = document.getElementById(thermostats[i]);
    if (layer != null && layer.style.display == '') {
      var nextVal = timeStrToVal( document.getElementById(timeFields[i]).value );
      if (val >= nextVal) val = nextVal - 10;
    }
  }
  t.value = timeValToStr( val );
  
  var hour = Math.floor(val / 60);
  var minute = val % 60;
  moveLayer(idx, hour, minute);
}

function tempChange(idx) {
  var fields = (thermMode == 'C') ? tempCArrows : tempHArrows;
  var temp = getFahrenheitTemp(document.getElementById(tempInputFields[idx]).value, tempUnit);
  moveTempArrow(fields[idx], temp);
  showTemp(idx, temp);
} 
