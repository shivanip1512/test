var layerLeftBnd = 8;
var layerHorDist = 50
var tenMinEqlLen = 3;
var layerRightBnd = layerLeftBnd + Math.floor(tenMinEqlLen * (24 * 6 - 1));

var arrowBottomBnd = -32;
var thermometerLen = 100;
var arrowTopBnd = arrowBottomBnd - thermometerLen;

var thermMode = 'C';	// Thermostat mode flag, 'C'/'H'

var browser = new Object();
browser.isNetscape = false;
browser.isMicrosoft = false;
if (navigator.appName.indexOf("Netscape") != -1)
	browser.isNetscape = true;
else if (navigator.appName.indexOf("Microsoft") != -1)
	browser.isMicrosoft = true;


function showTimeWake(){
  var s = document.getElementById('MovingLayer1');
  var txt = document.getElementById('time1');
  showTime(s,txt,0);
}

function showTimeLeave(){
  var s = document.getElementById('MovingLayer2');
  var txt = document.getElementById('time2');
  showTime(s,txt,1);
}
function showTimeReturn(){
  var s = document.getElementById('MovingLayer3');
  var txt = document.getElementById('time3');
  showTime(s,txt,2);
}

function showTimeSleep(){
  var s = document.getElementById('MovingLayer4');
  var txt = document.getElementById('time4');
  showTime(s,txt,3);
}

function showTime(s, txt, offset) {
  var curPos = parseInt(s.style.left, 10);
  txt.value = timeValToStr(Math.floor((curPos + offset * layerHorDist - layerLeftBnd) / tenMinEqlLen) * 10);
}


function showTemp1() {
var a = document.getElementById('div1' + thermMode);
var div = document.getElementById('temp1');
showTempIE(a, div);
}

function showTemp2() {
var a = document.getElementById('div2' + thermMode);
var div = document.getElementById('temp2');
showTempIE(a, div);
}

function showTemp3() {
var a = document.getElementById('div3' + thermMode);
var div = document.getElementById('temp3');
showTempIE(a, div);
}

function showTemp4() {
var a = document.getElementById('div4' + thermMode);
var div = document.getElementById('temp4');
showTempIE(a, div);
}

function showTempIE(a, div)
{ 
  var curPos = parseInt(a.style.top, 10);
  var temp = Math.floor((arrowBottomBnd - curPos) / thermometerLen * 43) + 45;
  div.innerHTML = temp + '&deg;';
}


function moveTempArrow(divIdArrow, newTemp) {
var arrow = document.getElementById(divIdArrow);
var pos = Math.floor((45 - newTemp) / 43 * thermometerLen) + arrowBottomBnd;
arrow.style.top = pos;
}


function moveLayer(divId, hour, minute) {
var divNo = parseInt( divId.substr(divId.length-1, 1), 10 );
var offset = (hour * 6 + Math.floor(minute / 10)) * tenMinEqlLen;
var layer = document.getElementById(divId);
layer.style.left = layerLeftBnd + offset - (divNo-1) * layerHorDist;
}


function timeChange(t, divId, nextTxtBox, prevTxtBox) {
  var val = timeStrToVal( t.value );
  if (prevTxtBox != null) {
    var prevVal = timeStrToVal( document.getElementById(prevTxtBox).value );
    if (val < prevVal) val = prevVal + 10;
  }
  if (nextTxtBox != null) {
    var nextVal = timeStrToVal( document.getElementById(nextTxtBox).value );
    if (val > nextVal) val = nextVal - 10;
  }
  t.value = timeValToStr( val );
  
  var hour = Math.floor(val / 60);
  var minute = val % 60;
  moveLayer(divId, hour, minute);
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
