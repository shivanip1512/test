<html>
<%@ include file="user_header.jsp" %>
<%@ include file="user_trendingheader.jsp" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<!-- Java script needed for the Calender Function--->
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></SCRIPT>
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

function MakeArray()
        {
        this.length = MakeArray.arguments.length
        for (var i = 0; i < this.length; i++)
        this[i+1] = MakeArray.arguments[i]
        }

var siteopt = new MakeArray("",
                            "CSV");
//                            "PDF");
							//"XLS");

var userFriendlyName = new MakeArray("Select Format",
                            "Comma Separated(.csv)");
//                            "Adobe Acrobat(.pdf)");
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
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="Mom.jpg">&nbsp;</td>
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
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
            <% String pageName = "user_trending.jsp?db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=graph&page=1&model=" + modelType; %>
            <%@ include file="nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          
            <td width="657" valign="top" bgcolor="#FFFFFF" align="center"> <br>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="400" class="Main" valign="top"> 
                    <table valign="top" width="400" border="0" cellspacing="0" cellpadding="0">
                      <tr> 
                        <td valign="top" width="300"> 
                          <table width="450" border="0" cellspacing="0" cellpadding="0">
                            <tr> 
                              <td valign="top"> 
                                <table width="265" border="2" cellspacing="0" cellpadding="3">
                                  <tr bgcolor="#CCCCCC"> 
                                    <form method="GET" action="user_trending.jsp" name="MForm">
									<td width="110" class="TableCell" height="49"> 
                                      Start Date:<br>
                                      <input type="text" name="start" value="<%= datePart.format(saveStart) %>" size="8">
                                      <a href="javascript:show_calendar('MForm.start')"
						              onMouseOver="window.status='Pop Calendar';return true;"
						              onMouseOut="window.status='';return true;"><img src="StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                                    </td>
                                    <td width="63" class="TableCell" height="49">Time 
                                      Period:<br>
                                      <select name="period">
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
                                    <td width="68" height="49"> 
                                      <center>
                                        <input type="image" src="GoButton.gif" name="image" border="0">
                                      </center>
                                    </td>
                                    <input type="hidden" name="gdefid" value="<%= graphDefinitionId %>">
									</form>
                                  </tr>
                                </table>
                              </td>
							  
                              <td width="140"> 
                                <table width="338" valign="top" cellpadding="0" cellspacing="0">
                                  <tr> 
                                    <td width="89" valign="top"> 
                                      <div align="center"><a href="user_trending.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=graph&page=1&model=" + modelType%>" target="_self"><img src="GraphButton.gif" border="0"></a>
                                    </div>
                                    </td>
                                    <td width="83" valign="top"> 
                                      <div align="center"><a href="user_trending.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=tab&page=1&model=" + modelType%>" target="_self"><img src="TabularButton.gif" border="0"></a>
									  </div>
                                    </td>
                                    <td width="89" valign="top"> 
                                      
                                    <div align="center"><a href="user_trending.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=summary&page=1&model=" + modelType %>" target="_self"><img src="SummaryButton.gif" border="0"></a> 
                                      <br>
                                    </div>
                                    </td>
                                    <td width="75" valign="top"> 
                                      <div align="center"><a href="user_print_trend.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=1&model=" + modelType %>" target="_self"><img src="FullButton.gif" border="0"></a>
									  </div>
                                    </td>
                                  </tr>
                                </table>
                                <table width="335" border="0" cellspacing="0" cellpadding="3">
                                  <tr> 
                                    <td align="right"> 
                                      <script language = "JavaScript">
document.writeln('<FORM><span class="Main">Export:</span>');
document.writeln('<SELECT NAME="ext" onchange="jumpPage(this.form)">');
tot = siteopt.length;
        for (var i = 1; i <= tot; i++)
        document.write("<OPTION value=" +siteopt[i]+ ">" +userFriendlyName[i]+ "</OPTION>");
document.writeln('</SELECT>');
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
                            <table align="center" width="600" border="0" cellspacing="0" cellpadding="0">
                              <tr> 
                                <td> 
                                  <center>
                                    <%
             if( graphDefinitionId <= 0 )
             {
            %>
                                    
                                  <p> No Data Set Selected </p>
                                    <%
             }
             else             
             //Check to see which tab is selected (tab paramater) and show the appropriate content
             if( tab.equalsIgnoreCase("summary") )
             {
              %>
                                    <%@ include file="../trendingsummary.jsp" %>
                                    <%
             }
             else
             if( tab.equalsIgnoreCase("tab") )
             {
              %>
                                    <%@ include file="../trendingtabular.jsp" %>
                                    <%
             }
             else // "graph" is default
             {
              %>
                                    <img src="/servlet/GraphGenerator?<%="db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&width=556&height=433&format=gif&start=" + dateFormat.format(start) + "&end=" + dateFormat.format(stop)+ "&model=" + modelType%>" width="556" height="433"> 
                                    <%
             }
          %>
                                    <br>
<span class="TableCell"><cti:text key="trending.disclaimer"/></span> 
                                  </center>
                                </td>
                              </tr>
                            </table>
                          </table>
                        </td>
                      </tr>
                    </table>
              </table>
            </td>
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<script>Calendar.CreateCalendarLayer(10, 275, "");</script>
</body>
</html>
