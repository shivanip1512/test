<html>
<%@ include file="StarsHeader.jsp" %>
<%@ include file="../oper_trendingheader.jsp" %>
<link rel="stylesheet" href="../demostyle.css" type="text/css">

<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<jsp:setProperty name="graphBean" property="startStr" param="start"/>
<jsp:setProperty name="graphBean" property="tab" param="tab"/>
<jsp:setProperty name="graphBean" property="period" param="period"/>
<jsp:setProperty name="graphBean" property="gdefid" param="gdefid"/>
<jsp:setProperty name="graphBean" property="viewType" param="view"/>
<jsp:setProperty name="graphBean" property="option" param="option"/>

<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../Consumer/Calendar1-82.js"></SCRIPT>

<!-- JavaScript needed for jump menu--->
<SCRIPT LANGUAGE = "JavaScript">
function MakeArray()
{
	this.length = MakeArray.arguments.length;
	for (var i = 0; i < this.length; i++)
		this[i+1] = MakeArray.arguments[i];
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
        i = form.ext.selectedIndex;
        if (i == 0) return;   
        form.action="/servlet/Download?";
        form.method="post";
        form.submit();
        form.ext.selectedIndex=siteopt[0];
        //window.location.href = url[i+1];
}
</SCRIPT>

<SCRIPT> <!--trend/view menu items-->
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
var viewType = parseInt(<%=graphBean.getViewType()%>);
var tabType = "<%=graphBean.getTab()%>";
var currentMenu;
var selectedItem;
var loadDur = false;

function init()
{
	initViewMenu();
	initOptions();
}

function initViewMenu()
{
	if (tabType == 'tab')
	{
		document.getElementById('TABULARID').innerHTML = "&nbsp;&#149;&nbsp;Tabular";
	}
	else if (tabType == 'summary')
	{
		document.getElementById('SUMMARYID').innerHTML = "&nbsp;&#149;&nbsp;Summary";
	}
	else if (tabType == 'graph')
	{
		switch(viewType)
		{
			case LINE:
				document.getElementById('LINEID').innerHTML = "&nbsp;&#149;&nbsp;<%=TrendModelType.LINE_VIEW_STRING%>";
				break;
			case BAR:
				document.getElementById('BARID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.BAR_VIEW_STRING%>";
				break;
			case DBAR:
				document.getElementById('3DBARID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.BAR_3D_VIEW_STRING%>";
				break;
			case STEP:
				document.getElementById('STEPID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.STEP_VIEW_STRING%>";
				break;
			case SHAPE:
				document.getElementById('SHAPEID').innerHTML =  "&nbsp;&#149;&nbsp;<%=TrendModelType.SHAPES_LINE_VIEW_STRING%>";
			break;
		}
	}
}

function initOptions()
{
	var leg_min_max;
	var leg_load_factor;
	var currentMask = parseInt(document.MForm.option.value);
	if  ((currentMask & MIN_MAX) != 0) 
		document.otherOptions.min_max.checked = true;
	if ((currentMask & LD ) !=0)
	{
		document.getElementById('LDID').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
		loadDur = true;
	}
	if ((currentMask & MULT) != 0) 
		document.otherOptions.mult.checked = true;
	if ((currentMask & LEGEND_MIN_MAX) != 0 )
	{
		leg_min_max = true;
		document.otherOptions.legend_min_max.checked = true;
	}
	if ((currentMask & LEGEND_LOAD_FACTOR) != 0)
	{
		document.otherOptions.legend_load_factor.checked = true;
		leg_load_factor = true;
	}	
	if (leg_min_max && leg_load_factor) 
		document.otherOptions.legend.checked = true;	
}

function changeView(t,viewType)
{
	document.MForm.tab.value = 'graph';
	document.MForm.view.value = viewType;
	document.MForm.submit();
}

function viewTabular()
{
	document.MForm.tab.value = 'tab';
	document.MForm.submit();
}

function viewSummary()
{
	document.MForm.tab.value = 'summary';
	document.MForm.submit();
}

function changeLegend()
{
	if (document.otherOptions.legend.checked)
	{
		document.otherOptions.legend_min_max.checked = true;
		document.otherOptions.legend_load_factor.checked = true;
		showMinMax();
		showLoadFactor();
	}
	else
	{
    	document.otherOptions.legend_min_max.checked = false;
		document.otherOptions.legend_load_factor.checked = false;
		showMinMax();
		showLoadFactor();
	}
}

function showMinMax()
{
	if (document.otherOptions.legend_min_max.checked == true)
	{
		setMask(LEGEND_MIN_MAX, true);
		document.otherOptions.legend.checked = true;
	}
	else
	{
		setMask(LEGEND_MIN_MAX, false);
		if (document.otherOptions.legend_load_factor.checked != true)
		{
			document.otherOptions.legend.checked = false;
		}
	}
}

function showLoadFactor()
{
	if (document.otherOptions.legend_load_factor.checked == true)
	{
		setMask(LEGEND_LOAD_FACTOR, true);
		document.otherOptions.legend.checked = true;
	}
	else
	{
		setMask(LEGEND_LOAD_FACTOR, false);
		if (document.otherOptions.legend_min_max.checked != true)
		{
			document.otherOptions.legend.checked = false;
		}
	}
}

function changeLD ()
{
	if (loadDur == true)
	{
    	setMask(LD, false);
	    document.getElementById('LDID').innerHTML = "&nbsp;&nbsp;&nbsp;Load Duration";
		loadDur = false;
    }
	else
	{
		setMask(LD, true);
		document.getElementById('LDID').innerHTML = "&nbsp;&#149;&nbsp;Load Duration";
		loadDur = true;
	}
	document.MForm.submit();
}

function changeMinMax()
{
	if (document.otherOptions.min_max.checked == true)
	{
		setMask(MIN_MAX, true);
	}
	else
	{
		setMask(MIN_MAX, false);
	}
}
function changeMult()
{
	if (document.otherOptions.mult.checked == true)
	{
		setMask(MULT, true);
	}
	else
	{
		setMask(MULT, false);
	}
}

function submitMForm()
{
	document.MForm.submit();
}

function changeOptionStyle(t)
{
	t.className = "optmenu2";

	if (selectedItem && t != selectedItem)
	{
		selectedItem.className = "optmenu1";
	}
	selectedItem = t;
}

function setMask (newMask,setMasked)
{
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

function menuAppear(event, divId)
{
	if (currentMenu)
	{
		currentMenu.style.visibility = 'hidden';
		if (selectedItem)
		{
			selectedItem.className = "optmenu1";
		}
	}
	currentMenu = document.getElementById(divId);
	var source;
	if (window.event)
	{
		source = window.event.srcElement;
	}
	else
	{
		source = event.target;
	}
	
	var element = document.getElementById(divId);
	coordx = parseInt(source.offsetLeft);
	coordy = parseInt(source.offsetTop) + parseInt(source.offsetHeight) + 2;
	
	while (source.offsetParent)
	{
		source = source.offsetParent;
		coordx = coordx + parseInt(source.offsetLeft);
		coordy = coordy + parseInt(source.offsetTop);
	}
	
	element.style.left = coordx + 'px';
	element.style.top = coordy + 'px';
	element.style.visibility = 'visible';
	if (window.event)
	{
 		document.attachEvent("onclick", hideMenu);
		window.event.cancelBubble = true;
    	window.event.returnValue = false;
	}
	else
	{
		document.addEventListener("click", hideMenu, true);
		event.preventDefault();
    }
	
	function hideMenu(event)
	{
		var element = document.getElementById(divId);
		element.style.visibility = 'hidden';
		if (window.event)
		{
			document.detachEvent("onclick", hideMenu);
		}
		else
		{
			document.removeEventListener("click",hideMenu, true);
			event.preventDefault();
		}
	}
}
</SCRIPT> <!--end javascript for trend/view menus-->

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
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
					<td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
					<td width="253" valign="middle">&nbsp;</td>
					<td width="58" valign="middle"> 
						<div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
					</td>
					<td width="57" valign="middle"> 
						<div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log Off</a>&nbsp;</span></div>
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
		<tr>
			<form>
			<td  valign="top" width="101">
				<% String pageName = "MeterData.jsp"; %>
				<%@ include file="Nav.jsp" %>
			</td>
			</form>
			<td width="1" bgcolor="#000000"></td>
			<td width="657" valign="top" bgcolor="#FFFFFF">
				<div align="center">
				<% String header = "METERING - INTERVAL DATA"; %>
				<%@ include file="InfoSearchBar.jsp" %>
				<hr>
				</div>
			<table width="98%" border="0" cellspacing="0" cellpadding="0" height="18">
			<tr>
				<td align = "right"><a name = "top" href = "#marker" style="text-decoration:underlined" class = "TableCell" >More Options</a></td>
			</tr>
			</table>
			<table width="87%" border="0" cellspacing="4" cellpadding="3" align="center" height="75">
			<tr>
				<td width="380" valign = "top" align = "right"> 
				<form name = "MForm">
				<div name = "date/period" align = "right" style = "border:solid 1px #666999;" >
				
				<INPUT TYPE="hidden" NAME="gdefid" VALUE="<%=graphBean.getGdefid()%>">
				<INPUT TYPE="hidden" NAME="tab" VALUE="<%=graphBean.getTab()%>">
				<INPUT TYPE="hidden" NAME="view" VALUE="<%=graphBean.getViewType()%>">
				<INPUT TYPE="hidden" NAME="option" VALUE = "<%=graphBean.getOption()%>" >

				<table width="99%" border="0" cellspacing="0" cellpadding="2" height="40">
				<tr> 
					<td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Start Date:</font> 
						<input type="text" name="start" value="<%= datePart.format(graphBean.getStart()) %>" size="9">
						<a href="javascript:show_calendar('MForm.start')"
							onMouseOver="window.status='Pop Calendar';return true;"
							onMouseOut="window.status='';return true;">
						<img src="StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
					</td>
					<td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Time Period: </font> 
						<select name="period">
						<% /* Fill in the period drop down and attempt to match the current period with one of the options */                           
		                for( int j = 0; j < com.cannontech.util.ServletUtil.historicalPeriods.length; j++ )
						{
							if( com.cannontech.util.ServletUtil.historicalPeriods[j].equals(graphBean.getPeriod()) )
								out.println("<OPTION SELECTED>" + graphBean.getPeriod());
							else
								out.println("<OPTION>" + com.cannontech.util.ServletUtil.historicalPeriods[j]);
						}%>
						</select>
					</td>
					<td width="10%"> 
						<input type="image" src="../GoButton.gif" name="image2" border="0">
					</td>
				</tr>
				</table>
				</div> 
				</form>
				</td>
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
				if( graphBean.getGdefid() <= 0 )
				{%>
					<p> No Data Set Selected 
				<%}
				else if( graphBean.getTab().equalsIgnoreCase("summary") )
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else if( graphBean.getTab().equalsIgnoreCase("tab") )
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else // "graph" is default
				{%>
					<img id = "theGraph" src="/servlet/GraphGenerator?" > 
				<%}
				%>
				<!--<font size="-1"><cti:text key="trending.disclaimer"/></font>-->
				</center></td>
			</tr>
			</table>
			<form name = "otherOptions"><hr>
			<table width="420" border="0" cellspacing="0" cellpadding="2" align = "center" height="29">
			<tr>
				<td align = "center"> 
					<div style = "border:solid 1px #666999; width:420px;" align = "center"> 
					<table width="100%" border="0" cellspacing="0" cellpadding="3" align = "center" height="11">
					<tr> 
						<td class = "TableCell" width="30%" height="27"  valign = "top"> 
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "TableCell">
						<tr> 
							<td width="7%" > 
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
					</div>
					<a name = "marker" class = "TableCell" href="#top" style="text-decoration:underlined">Back to Top</a>
					</td>
				</tr>
				</table>
				</form><br>
				<div id="viewMenu" class = "bgmenu" style = "width:120px" align = "left"> 
	              	<div id = "LINEID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,<%=TrendModelType.LINE_VIEW%>);">&nbsp;&nbsp;&nbsp;<%=TrendModelType.LINE_VIEW_STRING%></div>
  					<div id = "BARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,<%=TrendModelType.BAR_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.BAR_VIEW_STRING%></div>
  					<div id = "3DBARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,<%=TrendModelType.BAR_3D_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.BAR_3D_VIEW_STRING%></div>
					<div id = "SHAPEID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,<%=TrendModelType.SHAPES_LINE_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.SHAPES_LINE_VIEW_STRING%></div>
					<div id = "STEPID" name = "view" style = "width:120px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "changeView(this,<%=TrendModelType.STEP_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.STEP_VIEW_STRING%></div>
					<div id = "TABULARID" name = "view"  style = "width:120px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "viewTabular()">&nbsp;&nbsp;&nbsp;Tabular</div>
					<div id = "SUMMARYID" name = "view" style = "width:120px"  onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "viewSummary()">&nbsp;&nbsp;&nbsp;Summary</div>
					<hr>
					<div id = "LDID" onmouseover = "changeOptionStyle(this)" style = "width:120px" class = "optmenu1" onclick = "changeLD()">&nbsp;&nbsp;&nbsp;Load Duration</div>
				</div>
				<div id="trendMenu" class = "bgmenu" style = "width:75px"> 
					<div id = "LINEID" name = "view"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "jumpPage(document.exportForm)">&nbsp;&nbsp;&nbsp;Export</div>
					<div id = "PRINTID" name = "print" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "location='/OperatorDemos/oper_print_trend.jsp?';">&nbsp;&nbsp;&nbsp;Print</div>
				</div>
				
				<script language = "JavaScript">
				document.writeln('<FORM NAME = "exportForm">');
				document.writeln('<INPUT TYPE="hidden" NAME="ext" VALUE="CSV">');
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
