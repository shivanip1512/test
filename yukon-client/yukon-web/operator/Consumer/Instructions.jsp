<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
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
		  <% String pageName = "Instructions.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              
            <div align="center"> 
              <% String header = "THERMOSTAT - SCHEDULE<BR>HINTS AND DETAILS"; %>
              <%@ include file="include/InfoSearchBar.jsp" %>
                <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"> 
                      <table width="478" border="0">
                        <tr> 
                          <td class = "TableCell" width="100%" height="4" align = "right" valign="top" > 
                            
                          <div align="left">
                            <p>The schedule page allows you to set your ongoing 
                              temperature schedule. Four time frames are available 
                              for weekdays (Monday through Friday), Saturdays, 
                              and Sundays. Within each of these time frames, you 
                              may select your cooling and heating temperatures. 
                              Simply use the following procedures.</p>
                            <p><b>1)</b> Click<b> Weekday</b>, <b>Saturday</b>, 
                              or <b>Sunday</b> for the day(s) you would like to 
                              set. If you prefer to use the same settings for 
                              all days, set the<b> Weekday</b> page and check 
                              <b>Apply settings to Saturday and Sunday</b>.</p>
                            <p><b>2)</b> The utility recommended settings are 
                              displayed the first time the page is accessed. If 
                              at any time you would like to redisplay these settings, 
                              click the <b>Recommended Settings</b> button.</p>
                            <p><b>3)</b> Select<b> Cooling</b> or <b>Heating</b>. 
                              When <b>Cooling</b> is selected, the blue Cooling 
                              arrows are displayed to the left of each thermostat, 
                              while the Heating arrows are grayed to the right. 
                              When <b>Heating</b> is selected, the red Heating 
                              arrows are displayed to the right of each thermostat, 
                              while the Cooling arrows are grayed to the left. 
                            </p>
                            <p><b>4)</b> Slide the thermostats across the timeline 
                              to change time intervals for<b> Wake (W)</b>,<b> 
                              Leave (L)</b>, <b>Return (R)</b>, and<b> Sleep (S)</b>. 
                              Time frames are the same for both Cooling and Heating. 
                              Each of the time frames should be set to 1/2 hour 
                              before you would like the temperature to reach the 
                              selected level. Times may be entered directly in 
                              the text boxes.</p>
                            <p><b>5)</b> Slide the enabled (red or blue) arrows 
                              to the temperature you would like. The temperature 
                              is displayed at the top of each thermostat for each 
                              time frame. Temperatures may be set differently 
                              for Cooling and Heating.</p>
                            <p><b>6)</b> If you would like to temporarily change 
                              your thermostat, click on the<b> Manual</b> link 
                              at left or at the link provided at the top of the 
                              page. This page is also a convenient location for 
                              holding a temperature for a short amount of time 
                              that is different from your regular schedule. You 
                              may use this feature, for example, while away on 
                              vacation or while you have visiting guests.</p>
                            
                          </div>
                          </td>
                        </tr>
                      </table>
                    </td>
                </tr>
              </table>
              <br>
			  <input type="button" name="Back" value="Back" onclick="history.back()">
              <p align="center" class="MainText"><font face="Arial, Helvetica, sans-serif" size="1">Copyright 
                &copy; 2003, Cannon Technologies, Inc. All rights reserved.</font> 
              </p>
              <p align="center" class="MainText">&nbsp; </p>
            </div>
			
          </td>
		  
		  
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
</body>
</html>
