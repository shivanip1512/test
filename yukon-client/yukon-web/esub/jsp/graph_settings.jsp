<%
    int view = Integer.parseInt(request.getParameter("view"));
    String period = request.getParameter("period");
    String start = request.getParameter("start");
   
    if(period.equalsIgnoreCase("Today")) {
        period = "1 Day";
    }
   // else 
   // if(period.equalsIgnoreCase("Yesterday")) {
   //     period = "Prev 1 Day";
   // }

    String[] periodOption = { 
        "1 Day",
        "3 Days",
        "5 Days",
        "1 Week",
        "1 Month",        
        "Yesterday", 
        "Prev 2 Days",
        "Prev 3 Days",
        "Prev 5 Days",
        "Prev 7 Days"
    };

    // make the current period  the first one
    for(int i = 0; i < periodOption.length; i++) {
        if(periodOption[i].equalsIgnoreCase(period)) {
            String temp = periodOption[0];
            periodOption[0] = period;
            periodOption[i] = temp;
            break;
        }       
    }
    
%>

<html>
<head>
<title>Graph Settings</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script langauge = "Javascript" src = "updateGraph.js"></script>
<script langauge = "Javascript" src = "refresh.js"></script>
<script  LANGUAGE="JavaScript1.2" SRC="Calendar1-82.js"></script>
</head>
<body bgcolor="#000000" text="#000000"><form name = "MForm">    
  <table width="100%" border="0" cellspacing="0" cellpadding="9" >
    <tr>
    <td>
      <table width="100%" border="2" cellspacing="0" cellpadding="3" bgcolor = "#FFFFFF">
        <tr> 
          <td height="2" width="50%" valign = "top" bgcolor = "#CCCCCC"><b><font size="2" face="Arial, Helvetica, sans-serif">Graph 
            Settings</font></b></td>
        </tr>
        <tr>
        	<td>
        		<table>
                    <tr>
        				<td>
        					<font face="Arial, Helvetica, sans-serif" size="2">Start Date:</font>
        				</td>
        				<td>
        					<input type="text" name="start" size="9" value="<%= start %>" >
        					<!--<a HREF="javascript:show_calendar('MForm.startDate')"
                            	onMouseOver="window.status='Pop Calendar';return true;"
                                onMouseOut="window.status='';return true;"><img SRC="StartCalendar.gif" WIDTH="20" HEIGHT="15" ALIGN="ABSMIDDLE" BORDER="0">
                            </a>-->
        				</td>
        				<td>
        					<font face="Arial, Helvetica, sans-serif" size="2">(mm/dd/yy)</font>
        				</td>
        			</tr>

        		    <tr>
        		    	<td>
        		    		<font face="Arial, Helvetica, sans-serif" size="2">Graph Period:</font>
        		    	</td>
        		    	<td>
        		    		<select name="period">
                                <%
                                    for(int i = 0; i < periodOption.length; i++) {
                                        out.println("<option value=\"" + periodOption[i] + "\"" + ">" + periodOption[i]);
                                    }
                                %>
        		    	    </select>
        		    	</td>
        		    </tr>
                    <tr>
                        <td>
                            <font face="Arial, Helvetica, sans-serif" size="2">Graph Type:</font>
                        </td>
                        <td>
                            <select name="view">
                                <option value="0" <% if(view==0) { out.print(" selected"); } %>>Line
                                <option value="3" <% if(view==3) { out.print(" selected"); } %>>Step Line
                                <option value="1" <% if(view==1) { out.print(" selected"); } %>>Bar
                                <option value="2" <% if(view==2) { out.print(" selected"); } %>>3D Bar
                            </select>
                        </td>
                    </tr>
        	    </table>
        	</td>
        </tr>        
      </table>
    </td>
  </tr>
  <tr>
    <td align = "center" valign = "bottom">
      <input type="submit" name="Submit2" value="Update Graph" onclick = "update()">
      <input type="submit" name="Submit" value="Cancel" onclick = "Javascript:window.close()">
    </td>
  </tr>
</table></form>
</body>
</html>
