<%
	// Map of page name / link text
	String linkMap[][] = {{"user_ee.jsp", "Peak Day Partner"},
						  {"user_curtail.jsp", "Notification"},
						  {"user_lm_control.jsp", "Auto Control"},
						  {"user_lm_time.jsp", "Time Based"},
						  {"switch_commands.jsp", "Switch Command"},
						  {"user_trending.jsp", "Trending"}
						 };
	
	String buybackLinks[][] ={linkMap[0], linkMap[1]};
	String usercontrolLinks[][] ={linkMap[2], linkMap[3], linkMap[4]};
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
				if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid() && linkMap[5][0].equalsIgnoreCase(pageName))
				{%>
					<img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="12" height="12"><span class="Nav"><%=gData[i][1] %></span><br>
				<%}
				else 
				{%>
					<img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="/user/CILC/user_trending.jsp?<%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a><br>
				<%}
			}
		}%></div>
    </td>
  </tr>
  <tr> 
    <td height="20"> 
      <div align="left"><span class="NavHeader">Buyback</span><br>
      <%
        for (int i = 0; i < buybackLinks.length; i++)
        {
          if (buybackLinks[i][0].equalsIgnoreCase(pageName))
          {%>
            <img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="12" height="12"><span class="Nav"><%=buybackLinks[i][1]%></span><br>
          <%}
          else
          {%>
            <img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="<%=buybackLinks[i][0]%>" class="Link2"><span class="NavText"><%=buybackLinks[i][1]%></span></a><br>
          <%}
        }%>
      </div>
    </td>
  </tr>
<!--  <tr> 
    <td height="20"> 
      <div align="left"><span class="NavHeader">User-Control</span><br>
      <%
        for (int i = 0; i < usercontrolLinks.length; i++)
        {
          if (usercontrolLinks[i][0].equalsIgnoreCase(pageName))
          {%>
            <img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="12" height="12"><span class="Nav"><%=usercontrolLinks[i][1]%></span><br>
          <%}
          else
          {%>
            <img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="<%=usercontrolLinks[i][0]%>" class="Link2"><span class="NavText"><%=usercontrolLinks[i][1]%></span></a><br>
          <%}
        }%>
      </div>
    </td>
  </tr>-->
</table>
