<%@ page import="java.util.Hashtable" %>
<%
	// Map of page name / link text
	String linkMap[][] = {{"user_trending.jsp?model=0", "Line Graph"},
						  {"user_trending.jsp?model=1", "Bar Graph"},
						  {"user_trending.jsp?model=2", "Load Duration"},
						  {"user_ee.jsp", "VBB Offers"},
						  {"user_curtail.jsp", "Notification"},
						  {"user_lm_control.jsp", "Auto Control"},
						  {"user_lm_time.jsp", "Time Based"},
						  {"switch_commands.jsp", "Switch Command"}
						 };
						   
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
			links.put(linkMap[i][0], "<img src=\"Bullet.gif\" width=\"12\" height=\"12\"><span class=\"NavText2\">" + linkMap[i][1] + "</span>");
		else
			links.put(linkMap[i][0], "<img src=\"Bullet2.gif\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
  <tr> 
    <td bgcolor="#FFFFFF"> 
      <div align="left"> <span class="NavText"><%= liteCICustomer.getCompanyName() %><br>
        <%= liteContact.getContFirstName() + "  " + liteContact.getContLastName() %><br>
       <br>
       </span><br>
      </div>
      <div align="left"> </div>
    </td>
  </tr>
  <tr> 
    <td height="30" valign="bottom"> 
      <div align="left"><span class="NavHeader">Trending</span><br>
	    <%   /* Retrieve all the predefined graphs for this user*/                       
					if( gData != null )
					{
						for( int i = 0; i < gData.length; i++ )                                                          
						{
							if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
							{%>
								<img src="Bullet.gif" width="12" height="12"><span class="NavText2"><%=gData[i][1] %></span><br>
							<%}
							else 
							{%>
								<img src="Bullet2.gif" width="12" height="12"><a href="/user/CILC/user_trending.jsp?<%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a><br>
							<%}
						}
					}%></div>
    </td>
  </tr>
  <tr> 
    <td height="20"> 
      <div align="left"><span class="NavHeader">Buyback</span><br>
        <%= links.get("user_ee.jsp") %><br>
        <%= links.get("user_curtail.jsp") %></div>
    </td>
  </tr>
  <tr> 
    <td> 
<!--      <div align="left"><span class="NavHeader">User-Control</span><br>
        <%= links.get("user_lm_control.jsp") %><br>
        <%= links.get("user_lm_time.jsp") %><br>
		<%= links.get("switch_commands.jsp") %></div> -->
    </td>
  </tr>
</table>
