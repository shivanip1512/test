<html>
<%@ include file="../oper_header.jsp" %>
<%@ include file="../oper_trendingheader.jsp" %>
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<title>Metering</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
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
                            //"PDF");
							//"XLS");

var userFriendlyName = new MakeArray("Select Format",
                            "Comma Separated(.csv)");
                            //"Adobe Acrobat(.pdf)");
							//"Microsoft Excel(.xls)");

//var url = new MakeArray("",
//						"/scripts/jrun.dll/servlet/Download?",
//						"/scripts/jrun.dll/servlet/Download?",
//                        "/scripts/jrun.dll/servlet/Download?");

function jumpPage(form)
{
        i = form.ext.selectedIndex;
        if (i == 0) return;   
        form.action="/scripts/jrun.dll/servlet/Download?";
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
          <td width="102" height="102" background="MeterImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="Header3" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Commercial Metering&nbsp;&nbsp;</td>
                  
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
        <tr> <form>
          <td  valign="top" width="101" class="TableCell1">
		      <div align="center">
                <p><b><br>
                  Views:</b> 
                  <select name="select">
                    <option>Pre-Defined</option>
                    <!--  <option>Metering Points</option> -->
                  </select>
                </p>
              </div> 
              <%   /* Retrieve all the predefined graphs for this user*/                       
             if( gData != null )
             {                                       
                for( int i = 0; i < gData.length; i++ )                                                          
                {
                   if( gData[i] != null )
                   {
                   %>
              <br clear="ALL">
              <img src="Bullet.gif" width="12" height="12">&nbsp;<a href="/OperatorDemos/Metering/Metering.jsp?<%= "gdefid=" + gData[i][0] + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&model=" + modelType + "&page=1" %>" class="Link2"><%=gData[i][1] %></a> 
              <%
                   }
                }
             }
          %>
          </td>
          </form>
          <td width="1" bgcolor="#000000"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              
            </div>
            <table width="610" border="0" cellspacing="0" cellpadding="2" align="center">
              <tr> 
                <td width="18"> </td>
                <td width="280" valign="top"> 
                  <table width="280" border="2" cellspacing="0" cellpadding="3">
                    <tr bgcolor="#CCCCCC"> 
                      <form method="GET" action="Metering.jsp" name="MForm">
                        <td width="101"><font face="Arial, Helvetica, sans-serif" size="1">Start 
                          Date:</font><br>
                          <input type="text" name="start" value="<%= datePart.format(saveStart) %>" size="8">
                          <a href="javascript:show_calendar('MForm.start')"
						onMouseOver="window.status='Pop Calendar';return true;"
						onMouseOut="window.status='';return true;"> <img src="StartCalendar.gif" width="20" height="15" align="ABSMIDDLE" border="0"></a> 
                        </td>
                        <td width="82"><font face="Arial, Helvetica, sans-serif" size="1">Time 
                          Period:</font><br>
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
                        <td width="69"> 
                          <center>
                            <input type="image" src="GoButton.gif" name="image" border="0">
                          </center>
                        </td>
                        <input type="hidden" name="gdefid" value="<%= graphDefinitionId %>">
                      </form>
                    </tr>
                  </table>
                </td>
                <td width="340"> 
                  <table width="338" valign="top" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td width="86" valign="top"> 
                        <form method="Get" action="/OperatorDemos/Metering/Metering.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=graph&page=1&model=" + modelType%>" target="_self">
                          <div align="center"> 
                            <input type="submit" name="tab" value="Graph">
                          </div>
                        </form>
                      </td>
                      <td width="52" valign="top"> 
                        <form method="Get" action="/OperatorDemos/Metering/Metering.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=tab&page=1&model=" + modelType%>" target="_self">
                          <div align="left">
                            <input type="submit" name="tab" value="Tab">
                          </div>
                        </form>
                      </td>
                      <td width="99" valign="top"> 
                        <form method="Get" action="/OperatorDemos/Metering/Metering.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=summary&page=1&model=" + modelType %>" target="_self">
                          <div align="left">
                            <input type="submit" name="tab" value="Summary">
                          </div>
                        </form>
                      </td>
                      <td width="99" valign="top"> 
                        <form method="Get" action="/OperatorDemos/oper_print_trend.jsp?<%= "db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(saveStart) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=1&model=" + modelType %>" target="_self">
                          <div align="left">
                            <input type="submit" name="print" value="Print View">
                          </div>
                        </form>
						</td>
                    </tr>
                    <tr width="340"> </tr>
                  </table>
                  <table width="340" border="0" cellspacing="0" cellpadding="3">
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
            </table>
            <table width="574" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td><center> 
            <%
             if( graphDefinitionId <= 0 )
             {
            %>
                    <p class="Main"> No Data Set Selected 
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
            <img src="/scripts/jrun.dll/servlet/GraphGenerator?<%="db=" + dbAlias + "&gdefid=" + graphDefinitionId + "&width=556&height=433&format=gif&start=" + dateFormat.format(start) + "&end=" + dateFormat.format(stop)+ "&model=" + modelType%>" width="556" height="433"> 
            <%
             }
          %>
          <!--<cti:text key="trending.disclaimer"/>-->
	  </center> 
          </td>
        </tr>
      </table></td>
        <td width="1" bgcolor="#000000"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>

</html>
