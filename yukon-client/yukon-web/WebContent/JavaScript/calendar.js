/************************************************************************
Elant-Calendar Version 1.0
This is free and open-source software, but commercial use or publication
must first request and receive prior permission by contacting: 
support@elant.ru
Site: http://elant.ru/scripts/calendar/
************************************************************************/

var init   = new Date();
var day   = init.getDate();
var MyObj = null;
var oDiv = document.createElement("DIV");
var oShield = document.createElement("<DIV  style='position:absolute; top:0; left:0; width:100%; height:100%; background:red; filter: Alpha(Opacity=0); '></DIV>");
oShield.onclick=close_cal;
var html = "<DIV id=cal_body style='position:absolute; top:0; left:0; width:180; height:160; border:1px solid threeddarkshadow; border-top:1px solid buttonface; border-left:1px solid buttonface;'>";
html +="<DIV style='position:absolute; top:0; left:0; width:100%; height:100%; background=buttonface; border:1px solid buttonshadow; border-top:1px solid buttonhighlight; border-left:1px solid white;'>";
html +="<DIV style='position:absolute; left:0px; top:5px; width:176px;'><CENTER><SELECT ID='month' class=control onchange=displayCalendar(this.selectedIndex,document.getElementById('year').selectedIndex+1900)>";
html +="<OPTION>January<OPTION>February<OPTION>March<OPTION>April<OPTION>May<OPTION>June<OPTION>July<OPTION>August<OPTION>September<OPTION>October<OPTION>November<OPTION>December</SELECT>&nbsp;";
html +="<SELECT ID=year class=control onchange=displayCalendar(document.getElementById('month').selectedIndex,this.selectedIndex+1900)>";
for (var i=1900; i<2100; i++) html += "<OPTION>"+i;
html +="</SELECT>";
html +="&nbsp;<INPUT TYPE=BUTTON class=control VALUE='OK' onblur=document.getElementById('month').focus() onClick=eventHandlerDblClick() >";
html +="<style type=text/css>";
html +="<!--";
html +=".control {  font-size: 9px; height: 18px}";
html +=".day_disabled {  font-size: 9px; color:#999999; font-weight: bold; background-color: buttonface; height: 14px; width: 20px; text-align: center; cursor: default; border: 1px #B4B4B4 solid; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px}";
html +=".day {  font-size: 9px; font-weight: bold; background-color: buttonface; height: 14px; width: 20px; text-align: center; cursor: default; border: 1px #FFFFFF outset; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px}";
html +="-->";
html +="</style>";
html +="<table ID=CalTable border=0  cellspacing=1>";
html +="<tr class=cal_day_header><td>Su</td><td>Mo</td><td>Tu</td><td>We</td><td>Th</td><td>Fr</td><td>Sa</td></tr>";
for (var i=0; i<7; i++) html +="<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
html +="</table>";
html +="<INPUT TYPE=BUTTON class=control VALUE=' << ' onClick=setYear(-1)>";
html +="&nbsp;<INPUT TYPE=BUTTON class=control VALUE=' < ' onClick=setMonth(-1)>";
html +="&nbsp;<INPUT TYPE=BUTTON class=control VALUE=' Today ' onClick=setToday()>";
html +="&nbsp;<INPUT TYPE=BUTTON class=control VALUE=' > ' onClick=setMonth(1)>";
html +="&nbsp;<INPUT TYPE=BUTTON class=control VALUE=' >> ' onblur=document.getElementById('month').focus() onClick=setYear(1)>";
html +="</CENTER></DIV></DIV></DIV>";

function openCalendar(obj, x_l, x_t)
{
  if (!x_l) var x_l=0;	
  if (!x_t) var x_t=0;
  MyObj=obj;
  oDiv.innerHTML=html;
  oDiv.style.position="absolute";
  oDiv.style.zIndex=301;
  oShield.style.zIndex=300;
  oDiv.style.pixelLeft=calcLeft(obj)-3 + x_l;
  oDiv.style.pixelTop=calcTop(obj)+obj.offsetHeight-3 + x_t;
  document.body.appendChild(oShield);
  document.body.appendChild(oDiv);

  if (obj)
  {
    var x_month=parseInt(obj.value.substr(0,2), 10);
    var x_day=parseInt(obj.value.substr(3,2), 10);
    var x_year=parseInt(obj.value.substr(6,4), 10);
    if (x_day > 0 && x_day <= getDaysInMonth(x_month,x_year) && x_month  > 0 && x_month < 13 && x_year > 1899 && x_year < 2100)
    {
      day=x_day;
      document.getElementById('month').selectedIndex = x_month-1;
      document.getElementById('year').selectedIndex=x_year-1900;
      displayCalendar(x_month-1,x_year,x_day);
    }
    else
      setToday();
  }
  else
  setToday();
}

function eventHandlerOver(anEvObj)
{
  if (this.className!="cal_day_selected")
    this.className="cal_day_mouseover";
}

function eventHandlerOut(anEvObj) {
if (this.className!="cal_day_selected")
this.className="day";
}

function eventHandlerDblClick(anEvObj)
{
  var str_day ="";
  var str_month =document.getElementById('month').selectedIndex+1;
  var str_year=document.getElementById('year').selectedIndex+1900;
  (day.toString().length==1)? str_day="0"+day : str_day=day;
  if (str_month<10)
    str_month="0"+str_month;
  MyObj.value=(str_month + "/" + str_day + "/" + str_year);
  close_cal();
}

function close_cal()
{
  document.body.removeChild(oDiv);
  document.body.removeChild(oShield);	
}

function eventHandlerClick(anEvObj)
{
  day=this.innerHTML;
  if (this.className=="day_disabled")
  {
    (parseInt(day, 10) > 20) ? setMonth(-1): setMonth(1);
  }
  else
    displayCalendar(document.getElementById('month').selectedIndex, document.getElementById('year').selectedIndex+1900, day);
}

function setYear(val)
{
  if (!isNaN(val))
  {
    x_year=document.getElementById('year').selectedIndex+1900;
    x_year= Number(x_year) + val;
    if (x_year < 1900)
      x_year = 1900;
    if (x_year > 2099)
      x_year = 2099;
    
    document.getElementById('year').selectedIndex = x_year-1900;
    displayCalendar(document.getElementById('month').selectedIndex, document.getElementById('year').selectedIndex+1900);
  }
}

function setMonth(val)
{
  if (!isNaN(val))
  {
    var i = 0;
    var x_month = document.getElementById('month').selectedIndex;
    i=x_month + val;
    x_month=i%12;
    if (x_month < 0)
      x_month = x_month + 12;
    
    document.getElementById('month').selectedIndex = x_month;
    setYear(Math.floor(i/12));
    displayCalendar(document.getElementById('month').selectedIndex, document.getElementById('year').selectedIndex+1900);	
  }
}

function setToday()
{
  var now   = new Date();
  var x_day   = now.getDate();
  var x_month = now.getMonth();
  var x_year  = now.getYear();
  day=x_day;
  document.getElementById('month').selectedIndex = x_month;  
  document.getElementById('year').selectedIndex = x_year-1900;
  displayCalendar( document.getElementById('month').selectedIndex, document.getElementById('year').selectedIndex+1900, x_day);
}

function getDaysInMonth(x_month,x_year)
{
  var days;
  if (x_month==1 || x_month==3 || x_month==5 || x_month==7 || x_month==8 || x_month==10 || x_month==12)
    days=31;
  else if (x_month==4 || x_month==6 || x_month==9 || x_month==11)
    days=30;
  else if (x_month==2)
  {
    if (isLeapYear(x_year))
      days=29;
    else
      days=28;
  }
  
  return (days);
}
function isLeapYear (x_year)
{
  if (((x_year % 4)==0) && ((x_year % 100)!=0) || ((x_year % 400)==0))
    return (true);
  else
    return (false);
}

function displayCalendar(x_month, x_year, x_day)
{
  x_month = parseInt(x_month, 10);
  x_year = parseInt(x_year, 10);
  var days = getDaysInMonth(x_month+1,x_year);
  if (x_month > 1)
    var days_before = getDaysInMonth(x_month,x_year);
  else 
    var days_before = getDaysInMonth(12,x_year-1);

  if (day > days)
    day=days;
  if (!x_day)
    x_day=day;

  var curr_day = 0;
  var firstOfMonth = new Date (x_year, x_month, 1);
  var startingPos = firstOfMonth.getDay();
  var table = document.getElementById('CalTable');
  for (i=0; i<42; i++)
  {
    curr_day=i-startingPos+1;
    table.rows[Math.floor(i/7)+1].cells[i%7].className="day";
    if (curr_day <= 0 )
    {
      table.rows[Math.floor(i/7)+1].cells[i%7].innerHTML=curr_day + days_before;
      table.rows[Math.floor(i/7)+1].cells[i%7].className="day_disabled";
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseover="";
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseout="";
    }
    else if (curr_day > 0 && curr_day <= days)
    {
      table.rows[Math.floor(i/7)+1].cells[i%7].innerHTML=curr_day;
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseover=eventHandlerOver;
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseout=eventHandlerOut;
    }
    else if (curr_day > days)
    {
      table.rows[Math.floor(i/7)+1].cells[i%7].innerHTML=curr_day-days;
      table.rows[Math.floor(i/7)+1].cells[i%7].className="day_disabled"
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseover="";
      table.rows[Math.floor(i/7)+1].cells[i%7].onmouseout="";
    }
    
    table.rows[Math.floor(i/7)+1].cells[i%7].onclick=eventHandlerClick;
    table.rows[Math.floor(i/7)+1].cells[i%7].ondblclick=eventHandlerDblClick;
 
    if (curr_day==x_day)
      table.rows[Math.floor(i/7)+1].cells[i%7].className="cal_day_selected";
  }
}

function calcTop(x_ele)
{
  var x_ret=0;
  var oParent = x_ele.offsetParent;
  if (oParent == null)
    return 0
  else
    x_ret=x_ele.offsetTop + oParent.clientTop + calcTop(oParent);
  
  return x_ret
}

function calcLeft(x_ele)
{
  var x_ret=0;
  var oParent = x_ele.offsetParent;
  if (oParent == null)
    return 0
  else
    x_ret=x_ele.offsetLeft + oParent.clientLeft + calcLeft(oParent);
  return x_ret
}