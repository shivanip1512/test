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
  var curPos = parseInt(s.style.left);
  var hourStr = "0" + Math.floor(((curPos - 200)/3) * 10 / 60);
  hourStr = hourStr.substr(hourStr.length-2, 2);
  var minuteStr = "0" + Math.floor(((curPos - 200)/3)) * 10 % 60;
  minuteStr = minuteStr.substr(minuteStr.length-2, 2);
  txt.value= hourStr + ":" + minuteStr;
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
  var curPos = parseInt(a.style.top);
  var temp = Math.floor((-curPos-35) / 100 * 43 + 45);
 div.innerHTML = temp + '&deg;';
}

var changed = false;

function setChanged() {
	changed = true;
}

function switchSettings(day, mode) {
	if (!changed) return true;
	
	var form = document.form1;
	form.action = "/servlet/UpdateThermostat";
	form.elements('action').value = "SaveChanges";
	form.day2.value=day;
	form.mode2.value=mode;
	form.submit();
	return false;
}

function setDefault() {
	var form = document.form1;
	form.action = "/servlet/UpdateThermostat";
	form.elements('action').value = "SetDefault";
	form.submit();
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
var smMove = parseInt(18 * (Math.floor(minute/10) * 10) / 60);
var layer = document.getElementById(divId);
layer.style.left = lgMove + smMove;

}

function timeChange(t,divId, nextTxtBox, prevTxtBox) {
var pattern
var time = t.value.split(":");
var hour;
var minute;
var nextTime;
var nextHr = null;
var nextMin = null;
var prevTime;
var prevHr = null;
var prevMin = null;
 


if(nextTxtBox != null) {
nextTime = document.getElementById(nextTxtBox).value.split(":");
nextHr = parseInt(nextTime[0], 10);
nextMin = parseInt(nextTime[1], 10);

}


if(prevTxtBox != null) {
prevTime = document.getElementById(prevTxtBox).value.split(":");
prevHr = parseInt(prevTime[0], 10);
prevMin = parseInt(prevTime[1], 10);

}


if (time.length == 2) {
	if (time[0]) {
	hour = parseInt(time[0]);
	alert(time[0]+"/"+hour);
	}
	else 
	hour = 0;
	if (time[1])
	minute = parseInt(time[1]);
	else 
	minute = 0;
}
else if (time.length == 1){
	hour = parseInt(time[0]);
	minute = 0;}
else{
	hour = 0;
	minute = 0;}
	
if (hour > 23){   
	hour = 23;
	minute = 50;}
else 
if (hour < 0)
	hour = 0;

if (hour == 23 && minute > 50)
	minute = 50;

if (minute < 0 || minute > 59) 
	minute = 0;

if (nextHr != null) {
 if(hour > nextHr) {
 		hour = nextHr;
		minute = nextMin;
	}
}
if (nextMin != null) {
	if (hour == nextHr) {
	if (minute >= nextMin) 
			if (minute == 0) {
				hour = hour -1;
				minute = 50;
				}
			else
			minute = nextMin - 10;
	
	}
}
if (prevHr != null) {
 if(hour < prevHr) {
 		hour = prevHr;
		minute = prevMin;
	}
}
if (prevMin != null) {
	if (hour == prevHr) {
	if (minute <= prevMin) 
			if (minute == 50) {
				hour = hour + 1;
				minute = 0;
			}
			else
			minute = prevMin + 10;
	
	}
}


moveLayer(divId, hour, minute);
hour = "0" + hour;
minute = "0" + Math.floor(minute/10) * 10;
minute = minute.substr(minute.length-2, 2);
hour = hour.substr(hour.length-2, 2);
t.value = hour + ":" + minute;
}



