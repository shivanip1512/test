<%@ include file="StarsHeader.jsp" %>
<%@ include file="../oper_trendingheader.jsp" %>
<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<html>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../Consumer/Calendar1-82.js"></SCRIPT>
<SCRIPT Language="Javascript">



function MakeArray()
        {
        this.length = MakeArray.arguments.length
        for (var i = 0; i < this.length; i++)
        this[i+1] = MakeArray.arguments[i]
        }

var siteopt = new MakeArray("",
                            "CSV");
                            //"PDF");
							//"XLS");

var userFriendlyName = new MakeArray("Select Format",
                            "Comma Separated(.csv)");
                            //"Adobe Acrobat(.pdf)");
							//"Microsoft Excel(.xls)");

//var url = new MakeArray("",
//						"/servlet/Download?",
//						"/servlet/Download?",
//                        "/servlet/Download?");



function jumpPage(form)
{
        form.action="/servlet/Download?";
        form.method="post";
        form.submit();
}

//view types

var LINE  = parseInt(<%=TrendModelType.LINE_VIEW%>);
var BAR   = parseInt(<%=TrendModelType.BAR_VIEW%>);
var DBAR  = parseInt(<%=TrendModelType.BAR_3D_VIEW%>);
var STEP  = parseInt(<%=TrendModelType.STEP_VIEW%>);
var SHAPE = parseInt(<%=TrendModelType.SHAPES_LINE_VIEW%>);

//options

var BASIC = parseInt(<%=TrendModelType.BASIC_MASK%>); 
var LD = parseInt(<%=TrendModelType.LOAD_DURATION_MASK%>); 
var MULT = parseInt(<%=TrendModelType.GRAPH_MULTIPLIER%>); 
var MIN_MAX = parseInt(<%=com.cannontech.graph.model.TrendModelType.PLOT_MIN_MAX_MASK%>);

//legend

var LEGEND_MIN_MAX = parseInt(<%=TrendModelType.LEGEND_MIN_MAX_MASK%>);
var LEGEND_LOAD_FACTOR = parseInt(<%=TrendModelType.LEGEND_LOAD_FACTOR_MASK%>);

//Global variables
var viewType = parseInt(<%= modelType%>);
var type = "<%=tab%>";
var currentMenu;
var selectedItem;
var loadDur = false;

function init() {
	initViewMenu();
	initOptions();
}

function initViewMenu() {

if (type == 'tab') {
document.getElementById('TABULAR').innerHTML = "&nbsp;&#149;&nbsp;Tabular";
}
else if (type == 'summary') {
document.getElementById('SUMMARY').innerHTML = "&nbsp;&#149;&nbsp;Summary";
}
else if (type == 'graph') {
switch(viewType) {

case LINE:
	document.getElementById('LINE').innerHTML = "&nbsp;&#149;&nbsp;Line Graph";
	break;
case BAR:
	document.getElementById('BAR').innerHTML =  "&nbsp;&#149;&nbsp;Bar Graph";
	break;
case DBAR:
	document.getElementById('3DBAR').innerHTML =  "&nbsp;&#149;&nbsp;3D Bar Graph";
	break;
case STEP:
	document.getElementById('STEP').innerHTML =  "&nbsp;&#149;&nbsp;Step Graph";
	break;
case SHAPE:
	document.getElementById('SHAPE').innerHTML =  "&nbsp;&#149;&nbsp;Line & Shape Graph";
	break;

}
}

}
function initOptions() {
var leg_min_max;
var leg_load_factor;
var currentMask = parseInt(document.MForm.option.value);
if  ((currentMask & MIN_MAX) != 0) 
	document.otherOptions.min_max.checked = true;
if ((currentMask & LD ) !=0) {
	document.getElementById('LD').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
	loadDur = true;
	}
if ((currentMask & MULT) != 0) 
	document.otherOptions.mult.checked = true;
if ((currentMask & LEGEND_MIN_MAX) != 0 ) {
	leg_min_max = true;
	document.otherOptions.legend_min_max.checked = true;
	}
if ((currentMask & LEGEND_LOAD_FACTOR) != 0)  {
	document.otherOptions.legend_load_factor.checked = true;
	leg_load_factor = true;
	}	
if (leg_min_max && leg_load_factor) 
	document.otherOptions.legend.checked = true;	
}

function changeView(t,modelType) {
document.MForm.tab.value = 'graph';
document.MForm.model.value = modelType;
document.MForm.submit();}

function viewTabular() {
document.MForm.tab.value = 'tab';
document.MForm.submit();}
 
function viewSummary() {
document.MForm.tab.value = 'summary';
document.MForm.submit();}

function changeLegend() {
if (document.otherOptions.legend.checked) {
	document.otherOptions.legend_min_max.checked = true;
	document.otherOptions.legend_load_factor.checked = true;
	showMinMax();
	showLoadFactor();
}
else {
    document.otherOptions.legend_min_max.checked = false;
	document.otherOptions.legend_load_factor.checked = false;
	showMinMax();
	showLoadFactor();
}}

function showMinMax() {
if (document.otherOptions.legend_min_max.checked == true) {
	setMask(LEGEND_MIN_MAX, true);
	document.otherOptions.legend.checked = true;}
else {
	setMask(LEGEND_MIN_MAX, false);
	if (document.otherOptions.legend_load_factor.checked != true) {
		document.otherOptions.legend.checked = false;}
}
}
function showLoadFactor() {
if (document.otherOptions.legend_load_factor.checked == true) {
	setMask(LEGEND_LOAD_FACTOR, true);
	document.otherOptions.legend.checked = true;
}
else { 
	setMask(LEGEND_LOAD_FACTOR, false);
	if (document.otherOptions.legend_min_max.checked != true) {
		document.otherOptions.legend.checked = false;
	}
}}

function changeLD () {
if (loadDur == true)  {
    setMask(LD, false);
    document.getElementById('LD').innerHTML = "&nbsp;&nbsp;&nbsp;Load Duration";
	loadDur = false;
    }
else {
	setMask(LD, true);
	document.getElementById('LD').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
	loadDur = true;
}
document.MForm.submit();
}

function changeMinMax() {
if (document.otherOptions.min_max.checked == true) {
setMask(MIN_MAX, true);
}
else {
setMask(MIN_MAX, false);
}
}
function changeMult() {
if (document.otherOptions.mult.checked == true) {
setMask(MULT, true);
}
else {
setMask(MULT, false);
}
}

function submitMForm() {
document.MForm.submit();

}
function changeOptionStyle(t) {
t.className = "optmenu2";

if (selectedItem && t != selectedItem) {
	
	selectedItem.className = "optmenu1";
}
selectedItem = t;
}

function setMask (newMask,setMasked) {
var currentMask = parseInt(document.MForm.option.value);
if(setMasked)
		currentMask |= newMask;
	else
	{
		//check to make sure it's there if we are going to remove it
		if( (currentMask & newMask) != 0)
		{
			currentMask ^= newMask;
		}
	}
	document.MForm.option.value = currentMask;	
	}

function menuAppear(event, divId) {
	if (currentMenu) {
	currentMenu.style.visibility = 'hidden';
	if (selectedItem) {
		selectedItem.className = "optmenu1";
	}
	}
	currentMenu = document.getElementById(divId);
	var source;
	if (window.event)  {
      source = window.event.srcElement;
	}
	else {
      source = event.target;
	}
	
	var element = document.getElementById(divId);
	
	coordx = parseInt(source.offsetLeft);
	coordy = parseInt(source.offsetTop) + parseInt(source.offsetHeight) + 2;
	
	while (source.offsetParent) {
		source = source.offsetParent;
		coordx = coordx + parseInt(source.offsetLeft);
		coordy = coordy + parseInt(source.offsetTop);
		}
	
	
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
	if (window.event) {
 	document.attachEvent("onclick", hideMenu);
	window.event.cancelBubble = true;
    window.event.returnValue = false;
	}
	else {
	
	document.addEventListener("click", hideMenu, true);
	event.preventDefault();
    }

function hideMenu(event) {
	
	var element = document.getElementById(divId);
	element.style.visibility = 'hidden';
	if (window.event) {
	document.detachEvent("onclick", hideMenu);
	}
	else {
	document.removeEventListener("click",hideMenu, true);
	event.preventDefault();
	}
}
}
	
</SCRIPT>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Consumer/ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="../Consumer/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> <form>
          <td  valign="top" width="101">
		  <% String pageName = "MeterData.jsp"; %>
          <%@ include file="Nav.jsp" %>
            </td>
          </form>
          <td width="1" bgcolor="#000000"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
           <% String header = "METERING - INTERVAL DATA"; %>
              <%@ include file="InfoSearchBar.jsp" %>
              <hr>
            <table width="98%" border="0" cellspacing="0" cellpadding="0" height="18">
              <tr>
                <td align = "right"><a name = "top" href = "#marker" style="text-decoration:underlined" class = "TableCell" > 
                  More Options</a></td>
              </tr>
            </table>
            <table width="87%" border="0" cellspacing="4" cellpadding="3" align="center" height="75">
              <tr>
                  
                <td width="380" valign = "top" align = "right"> 
                  <form name = "MForm">
                    <div name = "date/period" align = "right" style = "border:solid 1px #666999;" >
                 	 <INPUT TYPE="hidden" NAME="gdefid" VALUE="<%=graphDefinitionId%>">
					 <INPUT TYPE="hidden" NAME="tab" VALUE="<%=tab%>">
					 <INPUT TYPE="hidden" NAME="model" VALUE="<%=modelType %>">
					 <INPUT TYPE="hidden" NAME="option" VALUE = "<%=option%>" >
                       
                      <table width="99%" border="0" cellspacing="0" cellpadding="2" height="40">
                        <tr> 
                          <td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Start 
                            Date:</font> 
                            <input type="text" name="start" value="<%= datePart.format(saveStart) %>" size="9">
                          <a href="javascript:show_calendar('MForm.start')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="../Consumer/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                        </td>
                          <td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Time 
                            Period: </font> 
                            <select name="select">
                            <% /* Fill in the period drop down and attempt to match the current period
                 with one of the options */                           
                for( int j = 0; j < com.cannontech.util.ServletUtil.historicalPeriods.length; j++ )
                {
                    if( com.cannontech.util.ServletUtil.historicalPeriods[j].equals(selectedPeriod) )                 
                        out.println("<OPTION SELECTED>" + selectedPeriod);                  
                    else                  
                        out.println("<OPTION>" + com.cannontech.util.ServletUtil.historicalPeriods[j]);
                }      
           %>
                          </select>
                        </td>
                          <td width="10%"> 
                            <input type="image" src="../GoButton.gif" name="image2" border="0">
                            
                          </td>
                      </tr>
                    </table>
                     </div> 
                  </form></td>
                  
                <td width="168" valign = "top" align= "center"> 
                  <table width="99%" border="0" class = "Main" cellspacing = "2" height="16">
                    <tr> 
                      <td width = "50%"> 
                        <div name = "trend" align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'trendMenu')" >Trend</div>
                      </td>
                      <td width = "50%"> 
                        <div align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'viewMenu')">View</div>
                      </td>
                    </tr>
                  </table>
                  
                </td>
              </tr>
            </table>
        
            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
              <tr> 
          <td><center> 
            <%
             if( graphDefinitionId <= 0 )
             {
            %>
                    <p> No Data Set Selected 
            <%
             }
             else             
             //Check to see which tab is selected (tab paramater) and show the appropriate content
             if( tab.equalsIgnoreCase("summary") )
             {
              %>
            <%@ include file="../../trendingsummary.jsp" %>
            <%
             }
             else
             if( tab.equalsIgnoreCase("tab") )
             {
              %>
            <%@ include file="../../trendingtabular.jsp" %>
            <%
             }
             else // "graph" is default
             {
              %>
            <img id = "theGraph" src="/servlet/GraphGenerator?<%="db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&width=" + width + "&height=" + height +"&format=png&start=" + dateFormat.format(start) + "&end=" + dateFormat.format(stop)+ "&model=" + modelType + "&option=" + option %>" > 
            <%
             }
          %>
          <!--<font size="-1"><cti:text key="trending.disclaimer"/></font>-->
	  </center> 
          </td>
        </tr>
      </table><form name = "otherOptions"><hr>
             <table width="420" border="0" cellspacing="0" cellpadding="2" align = "center" height="29">
                <tr>
                    <td align = "center"> 
                    <div style = "border:solid 1px #666999; width:420px;" align = "center"> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="3" align = "center" height="11">
                        <tr> 
                          <td class = "TableCell" width="30%" height="27"  valign = "top"> 
                            <table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell">
                              <tr> 
                                <td  width="7%" > 
                                  <input type="checkbox" name="legend" value="checkbox" onclick = "changeLegend()">
                                </td>
                                <td  width="93%">Legend </td>
                              </tr>
                            </table>
                            <table width="100%" border="0" cellspacing="0" cellpadding="0" class = "tableCell">
                              <tr> 
                                <td width="23%" align = "right"> 
                                  <input type="checkbox" name="legend_min_max" value="checkbox" onclick = "showMinMax()" >
                                </td>
                                <td width="77%">Show Min/Max</td>
                              </tr>
                              <tr> 
                                <td width="23%" align = "right"> 
                                  <input type="checkbox" name="legend_load_factor" value="checkbox" onclick = "showLoadFactor()">
                                </td>
                                <td width="77%">Show Load Factor</td>
                              </tr>
                            </table>
                          </td>
                          <td class = "TableCell" height="27"  width="25%" valign = "top"> 
                            <table width="99%" border="0" cellspacing="0" cellpadding="0" class = "TableCell">
                              <tr> 
                                <td width="13%" > 
                                  <input id = "min_max" type="checkbox" name="min_max" value="checkbox" onClick = "changeMinMax()">
                                </td>
                                <td width="87%">Plot Min/Max</td>
                              </tr>
                            </table>
                          </td>
                          <td class = "TableCell" height="27"  width="25%" valign = "top"> 
                            <table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
                              <tr> 
                                <td width="12%" > 
                                  <input type="checkbox" name="mult" value="checkbox" onClick = "changeMult()">
                                </td>
                                <td width="88%">Graph Multiplier</td>
                              </tr>
                            </table>
                          </td>
                          <td class = "TableCell" height="27"  width="20%" align = "center"> 
                            <input type="button" name="Submit" value="Update" onClick = "submitMForm()">
                          </td>
                        </tr>
                      </table>
                      </div><a name = "marker" class = "TableCell" href="#top" style="text-decoration:underlined">Back to Top</a>
                  </td>
                  </tr>
                </table>
                </form><br>

            <div id="viewMenu" class = "bgmenu" style = "width:150px" align = "left"> 
              	<div id = "LINE" name = "view1"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,LINE);">&nbsp;&nbsp;&nbsp;Line Graph</div>
  				<div id = "BAR" name = "view"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,BAR)">&nbsp;&nbsp;&nbsp;Bar Graph</div>
  				<div id = "3DBAR" name = "view"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,DBAR)">&nbsp;&nbsp;&nbsp;3D Bar Graph</div>
				<div id = "SHAPE" name = "view"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,SHAPE)">&nbsp;&nbsp;&nbsp;Line & Shapes Graph</div>
				<div id = "STEP" name = "view" style = "width:150px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,STEP)">&nbsp;&nbsp;&nbsp;Step Line Graph</div>							
				<div id = "TABULAR" name = "view"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "viewTabular()">&nbsp;&nbsp;&nbsp;Tabular</div>
				<div id = "SUMMARY" name = "view" style = "width:150px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "viewSummary()">&nbsp;&nbsp;&nbsp;Summary</div>
				<hr>
				<div id = "LD" onmouseover = "changeOptionStyle(this)" style = "width:150px" class = "optmenu1" onclick = "changeLD()">&nbsp;&nbsp;&nbsp;Load Duration</div>
			</div>
			<div id="trendMenu" class = "bgmenu" style = "width:150px"> 
              	<div id = "LINE" name = "view"  style = "width:150px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "jumpPage(document.exportForm)">&nbsp;&nbsp;&nbsp;Export .csv</div>
  				</div>
            <script language = "JavaScript">
document.writeln('<FORM NAME = "exportForm">');
document.writeln('<INPUT TYPE="hidden" NAME="ext" VALUE="CSV">');
document.writeln('<INPUT TYPE="hidden" NAME="gdefid" VALUE="<%=graphDefinitionId%>">');
document.writeln('<INPUT TYPE="hidden" NAME="start" VALUE="<%=dateFormat.format(saveStart) %>">');
document.writeln('<INPUT TYPE="hidden" NAME="period" VALUE="<%=period%>">');
document.writeln('<INPUT TYPE="hidden" NAME="tab" VALUE="<%=tab %>">');
document.writeln('<INPUT TYPE="hidden" NAME="model" VALUE="<%=modelType %>">');
document.writeln('<INPUT TYPE="hidden" NAME="db" VALUE="<%=dbAlias %>">');
if (navigator.userAgent.indexOf("Mozilla/2") != -1)
document.writeln('<INPUT TYPE = BUTTON VALUE="Jump!">');
document.writeln('</FORM>');
</script>
          </td>
        
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
