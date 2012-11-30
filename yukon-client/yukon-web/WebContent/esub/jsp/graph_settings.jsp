<%
    int view = Integer.parseInt(request.getParameter("view"));
    int events = Integer.parseInt(request.getParameter("events"));
    String period = request.getParameter("period");
    String start = request.getParameter("start");
   
    if(period.equalsIgnoreCase("Today")) {
        period = "1 Day";
    }
   // else 
   // if(period.equalsIgnoreCase("Yesterday")) {
   //     period = "Prev 1 Day";
   // }

    boolean isEvent = "Event".equalsIgnoreCase(period);

    String[] periodOption = null;
    if(isEvent){
        periodOption = new String[] { "Event" };
    } else {
        periodOption = new String[] { 
            "1 Day",
            "3 Days",
            "5 Days",
            "1 Week",
            "1 Month",        
            "Yesterday", 
            "Prev 2 Days",
            "Prev 3 Days",
            "Prev 5 Days",
            "Prev 7 Days",
        };
    }

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
<body bgcolor="#000000" text="#000000">
<form id="MForm" name="MForm">    
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
        					<input type="text" id="cal" name="start" size="10" value="<%= start %>" >
        				</td>
        				
        				<td>        				
        					 <a href="javascript:show_calendar('MForm.start')"
									onMouseOver="window.status='Pop Calendar';return true;"
                                    onMouseOut="window.status='';return true;">

                    		  <IMG SRC="<%=request.getContextPath()%>/WebConfig/yukon/Icons/StartCalendar.png" WIDTH="20" HEIGHT="15" BORDER="0">
                  			</a>
                  		</td>
        				<td>
        					<font face="Arial, Helvetica, sans-serif" size="2">(mm/dd/yy)</font>
        				</td> 
        			</tr>
                    
                    <% if(isEvent) { %>
                    <tr>
                        <td>
                            <font face="Arial, Helvetica, sans-serif" size="2">Number of Events:</font>
                        </td>
                        <td>
                            <select name="events">
                                <%
                                    for(int i = 1; i <= 50; i++) {
                                        String selected = (i == events) ? " selected" : "";                                    
                                        out.println("<option value=\"" + i + "\"" + selected + ">" + i + "</option>");
                                    }
                                %>
                            </select>
                        </td>
                    </tr>                    
                    <input type="hidden" name="period" value="Event" />
                    <% } else { %>
                    <input type="hidden" name="events" value="20" />
        		    <tr>
        		    	<td>
        		    		<font face="Arial, Helvetica, sans-serif" size="2">Graph Period:</font>
        		    	</td>
        		    	<td>
        		    		<select name="period">
                                <%
                                    for(int i = 0; i < periodOption.length; i++) {
                                        out.println("<option value=\"" + periodOption[i] + "\"" + ">" + periodOption[i] + "</option>");
                                    }
                                %>
        		    	    </select>
        		    	</td>
        		    </tr>
                    <% } %> 
                    <tr>
                        <td>
                            <font face="Arial, Helvetica, sans-serif" size="2">Graph Type:</font>
                        </td>
                        <td>
                            <select name="view">
                                <option value="0" <% if(view==0) { out.print(" selected"); } %>>Line</option>
                                <option value="1" <% if(view==1) { out.print(" selected"); } %>>Line/Shapes</option>
                                <option value="2" <% if(view==2) { out.print(" selected"); } %>>Line/Area</option>                                
                                <option value="3" <% if(view==3) { out.print(" selected"); } %>>Line/Area/Shapes</option>
                                <option value="4" <% if(view==4) { out.print(" selected"); } %>>Step</option>
                                <option value="5" <% if(view==5) { out.print(" selected"); } %>>Step/Shapes</option>
                                <option value="6" <% if(view==6) { out.print(" selected"); } %>>Step/Area</option>
                                <option value="7" <% if(view==7) { out.print(" selected"); } %>>Step/Area/Shapes</option>
                                <option value="8" <% if(view==8) { out.print(" selected"); } %>>Bar</option>
                                <option value="9" <% if(view==9) { out.print(" selected"); } %>>3D Bar</option>

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
      <input type="submit" name="Submit2" value="Update Graph" onclick ="updateGraphSettings(document.MForm.start.value,
						document.MForm.period.value,
						document.MForm.view.value,
                        document.MForm.events.value); return false;">
    </td>
  </tr>
</table></form>
</body>
</html>