<html>
<%@ include file="../user_header.jsp" %>
<%@ include file="../../trending_functions.jsp" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<!-- Java script needed for the Calender Function--->
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../../JavaScript/Calendar1-82.js"></SCRIPT>
<SCRIPT Language="Javascript">
function fsub() {
	document.MForm.submit();
}
 
function TryCallFunction() { 
	var sd = document.MForm.mydate1.value.split("-");
	document.MForm.iday.value = sd[1];
	document.MForm.imonth.value = sd[0];
	document.MForm.iyear.value = sd[2];
} 

function Today() {
	var dd = new Date();
	return((dd.getMonth()+1) + "/" + dd.getDate() + "/" + dd.getFullYear());
}

</SCRIPT>
<!-- JavaScript needed for jump menu--->
<SCRIPT LANGUAGE = "JavaScript">
function jumpPage(form)
{
	form.action="/servlet/Download?";
    form.method="post";
    form.submit();
}
</SCRIPT>
<script language="JavaScript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
// -->
</script>
</head>


<body class="Background" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr>
			    <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Trending</td>   
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101"> 
            <% String pageName = "user_trending.jsp"; %> 
            <%@ include file="nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" > 
            <table width="98%" border="0" cellspacing="0" cellpadding="0" height="18">
              <tr> 
                <td align = "right"><a name = "top" href = "#marker" style="text-decoration:underlined" class = "TableCell" >More 
                  Options</a></td>
              </tr>
            </table>
            <table width="87%" border="0" cellspacing="4" cellpadding="3" align="center" height="75">
              <tr> 
                <td width="380" valign = "top" align = "right"> 
                  <form name = "MForm">
                    <div name = "date/period" align = "right" style = "border:solid 1px #666999;" > 
                      <input type="hidden" name="gdefid" value="<%=graphBean.getGdefid()%>">
                      <input type="hidden" name="view" value="<%=graphBean.getViewType()%>">
                      <input type="hidden" name="option" value = "<%=graphBean.getOption()%>" >
                      <table width="99%" border="0" cellspacing="0" cellpadding="2" height="40">
                        <tr> 
                          <td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Start 
                            Date:</font> 
                            <input type="text" name="start" value="<%= datePart.format(graphBean.getStartDate()) %>" size="9">
                            <a href="javascript:show_calendar('MForm.start')"
							onMouseOver="window.status='Pop Calendar';return true;"
							onMouseOut="window.status='';return true;"> <img src="../../Images/Icons/StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                          </td>
                          <td width="45%"><font face="Arial, Helvetica, sans-serif" size="1">Time 
                            Period: </font> 
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
                  <td>
                    <center>
                      <%
				if( graphBean.getGdefid() <= 0 )
				{%>
                      <p> No Data Set Selected 
                        <%}
				else if( graphBean.getViewType() == TrendModelType.SUMMARY_VIEW)
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else if( graphBean.getViewType() == TrendModelType.TABULAR_VIEW)
				{
					graphBean.updateCurrentPane();
					out.println(graphBean.getHtmlString());
				}
				else // "graph" is default
				{%>
                        <img id = "theGraph" src="/servlet/GraphGenerator?" > 
                        <%}
				%>
                    <br><font size="-1"><cti:text roleid="<%= RoleTypes.TRENDING_DISCLAIMER_TEXT %>"/></font>
                    </center>
                  </td>
                </tr>
              </table>
			<HR WIDTH="575">
			<table width="575" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td align="center">			
				<form name = "otherOptions">
				<div align = "center">
					<table width="575" border="0" cellspacing="0" cellpadding="3">
					<tr>
						<td>
							<table width="100%" border="0" cellspacing="0" cellpadding="0" align = "center">
							<tr> 
								<td class = "TableCell" width="18%" valign = "top"> 
									<table width="99%" border="0" cellspacing="0" cellpadding="0" class = "TableCell">
									<tr> 
										<td width="13%" >
											<input id = "min_max" type="checkbox" name="min_max" value="checkbox" onClick = "changeMinMax()">
										</td>
										<td width="87%"><strong>Plot Min/Max</strong></td>
									</tr>
									</table>
								</td>
								<td class = "TableCell" width="22%" valign = "top"> 
									<table width="98%" border="0" cellspacing="0" cellpadding="0" class = "TableCell" height="19">
									<tr> 
										<td width="12%" >
											<input type="checkbox" name="mult" value="checkbox" onClick = "changeMult()">
										</td>
										<td width="88%"><strong>Use Graph Scaling</strong></td>
									</tr>
									</table>
								</td>
								<td class = "TableCell"  width="29%" valign = "top">
									<table width="95%" border="0" cellspacing="0" cellpadding="0" class = "tableCell">
										<tr> 
											<td width="21%" align = "right" valign="top"> 
												<input type="checkbox" name="legend_min_max" value="checkbox" onclick = "showMinMax()" > 
											</td>
											<td width="79%"><strong>Show Legend Min/Max</strong></td>
										</tr>
									</table>
								</td>
								<td class = "TableCell"  width="31%" valign = "top">
									<table width="100%" border="0" cellspacing="0" cellpadding="0" class = "tableCell">
                       	            <tr> 
										<td width="13%" align = "right" valign="top"> 
   	                                    	<input type="checkbox" name="legend_load_factor" value="checkbox" onclick = "showLoadFactor()"> 
										</td>
										<td width="87%"><strong>Show Legend Load Factor</strong></td>
									</tr>
									</table>
								</td>
							</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
							<div align="center">
								<input type="button" name="Submit" value="Update" onClick = "submitMForm()">
							</div>
						</td>
					</tr>
				</table>
				</div>
				</form>
			  <br>
            <div id="viewMenu" class = "bgmenu" style = "width:120px" align = "left"> 
              <div id = "LINEID" name = "view"  style = "width:120px" onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.LINE_VIEW%>);">&nbsp;&nbsp;&nbsp;<%=TrendModelType.LINE_VIEW_STRING%></div>
              <div id = "BARID" name = "view"  style = "width:120px" onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.BAR_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.BAR_VIEW_STRING%></div>
              <div id = "3DBARID" name = "view"  style = "width:120px" onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.BAR_3D_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.BAR_3D_VIEW_STRING%></div>
              <div id = "SHAPEID" name = "view"  style = "width:120px" onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.SHAPES_LINE_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.SHAPES_LINE_VIEW_STRING%></div>
              <div id = "STEPID" name = "view" style = "width:120px"  onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.STEP_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.STEP_VIEW_STRING%></div>
              <div id = "TABULARID" name = "view"  style = "width:120px" onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.TABULAR_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.TABULAR_VIEW_STRING%></div>
              <div id = "SUMMARYID" name = "view" style = "width:120px"  onMouseOver = "changeOptionStyle(this)" class = "optmenu1" onClick = "changeView(<%=TrendModelType.SUMMARY_VIEW%>)">&nbsp;&nbsp;&nbsp;<%=TrendModelType.SUMMARY_VIEW_STRING%></div>
              <hr>
              <div id = "LDID" onMouseOver = "changeOptionStyle(this)" style = "width:120px;" class = "optmenu1" onClick = "changeLD()">&nbsp;&nbsp;&nbsp;Load 
                Duration</div>
            </div>
            <div id="trendMenu" class = "bgmenu" style = "width:75px"> 
			<div id = "LINEID" name = "view"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "jumpPage(document.exportForm)">&nbsp;&nbsp;&nbsp;Export</div>
			<div id = "PRINTID" name = "print" style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "location='../../trending_print.jsp?';">&nbsp;&nbsp;&nbsp;Print</div>
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
<script>Calendar.CreateCalendarLayer(10, 275, "");</script>
</body>
</html>
