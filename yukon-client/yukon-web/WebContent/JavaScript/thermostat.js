function showTimeWake(){
  var s = document.getElementById('MovingLayer1');
  var txt = document.getElementById('time1');
  showTime(s,txt);
}

function showTimeLeave(){
  var s = document.getElementById('MovingLayer2');
  var txt = document.getElementById('time2');
  showTime(s,txt);
}
function showTimeReturn(){
  var s = document.getElementById('MovingLayer3');
  var txt = document.getElementById('time3');
  showTime(s,txt);
}

function showTimeSleep(){
  var s = document.getElementById('MovingLayer4');
  var txt = document.getElementById('time4');
  showTime(s,txt);
}



function showTime(s, txt) {
  var curPos = parseInt(s.style.left, 10);
  txt.value = timeValToStr( Math.floor((curPos - 200)/3) * 10 );
}

function showTemp1(){
var a = document.getElementById('arrow1');
var div = document.getElementById('div1');
showTempIE(a, div);
}
function showTemp2(){
var a = document.getElementById('arrow2');
var div = document.getElementById('div2');
showTempIE(a,div);
}
function showTemp3(){
var a = document.getElementById('arrow3');
var div = document.getElementById('div3');
showTempIE(a,div);
}
function showTemp4(){
var a = document.getElementById('arrow4');
var div = document.getElementById('div4');
showTempIE(a,div);
}


function showTempIE(a, div)
{ 
  var curPos = parseInt(a.style.top, 10);
  var temp = Math.floor((-curPos-35) / 100 * 43 + 45);
 div.innerHTML = temp + '&deg;';
}


function moveTempArrow(divIdArrow, divIdTempLabel, newTemp) {
var arrow = document.getElementById(divIdArrow);
var label = document.getElementById(divIdTempLabel);
var pos = Math.floor(-(((100 * newTemp - 4500)/43) + 35));
label.innerHTML = newTemp + '&deg;';
arrow.style.top = pos;
}


function moveLayer(divId, hour, minute) {
var lgMove = 18 * hour + 200;
var smMove = parseInt(18 * (Math.floor(minute/10) * 10) / 60, 10);
var layer = document.getElementById(divId);
layer.style.left = lgMove + smMove;
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
