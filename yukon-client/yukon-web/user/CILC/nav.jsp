<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%
	// Map of page name / link text
	String linkMap[][] = {{"user_ee.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, EnergyBuybackRole.ENERGY_BUYBACK_LABEL)},
						  {"user_curtail.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, DirectCurtailmentRole.CURTAILMENT_LABEL)},
						  {"user_lm_control.jsp", "Auto Control"},
						  {"user_lm_time.jsp", "Time Based"},
						  {"switch_commands.jsp", "Switch Command"},
						  {"user_trending.jsp", "Trending"},
						  {"user_ee_profile.jsp", "Profile"}
						 };
	
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkMap.length; i++) {
		if (linkMap[i][0].equalsIgnoreCase(pageName))
		{
			links.put(linkMap[i][0], "<img src=\"../../WebConfig/" + AuthFuncs.getRolePropertyValue(liteYukonUser, WebClientRole.NAV_BULLET_SELECTED) +"\" width=\"12\" height=\"12\"><span class=\"Nav\">" + linkMap[i][1] + "</span>");
		}
		else
		{
			links.put(linkMap[i][0], "<img src=\"../../WebConfig/" + AuthFuncs.getRolePropertyValue(liteYukonUser, WebClientRole.NAV_BULLET) +"\" width=\"12\" height=\"12\"><a href=\"" + linkMap[i][0] + "\" class=\"Link2\"><span class=\"NavText\">" + linkMap[i][1] + "</span></a>");
		}
	}
%>

<table width="101" border="0" cellspacing="0" cellpadding="6">
  <cti:checkMultiRole roleid="<%=Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID)%>">
  <tr> 
    <td bgcolor="#FFFFFF"> 
      <div align="left"> <span class="NavText"><%= liteCICustomer.getCompanyName() %><br>
        <%= liteContact.getContFirstName() + "  " + liteContact.getContLastName() %><br>
       <br>
       </span><br>
      </div>
    </td>
  </tr>
  </cti:checkMultiRole>

  <cti:checkRole roleid="<%=CommercialMeteringRole.ROLEID%>">
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
					<img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="<%=request.getContextPath()%>/user/CILC/user_trending.jsp?<%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a><br>
				<%}
			}
		}%></div>
    </td>
  </tr>
  <cti:checkProperty propertyid="<%=CommercialMeteringRole.TRENDING_GET_DATA_NOW_BUTTON%>">
  <tr>
    <td height="30" valign="bottom">
      <div align="left"><span class="NavHeader">Display</span><br>
        <img src='../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>' width="12" height="12"><a href="<%=request.getContextPath()%>/user/CILC/user_trending.jsp?update=now" class="link2"><span class="NavText">Get Data Now</span></a><br>
      </div>
    </td>
  </tr>
  </cti:checkProperty>
  </cti:checkRole>

  <cti:checkMultiRole roleid="<%=Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID)%>">
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader">Buyback</span><br>
        <cti:checkRole roleid="<%=EnergyBuybackRole.ROLEID%>">
		<%= links.get("user_ee.jsp") %><br>
		</cti:checkRole>
		<cti:checkRole roleid="<%=DirectCurtailmentRole.ROLEID%>">
		<%= links.get("user_curtail.jsp") %><br>
		</cti:checkRole>
      </div>
    </td>
  </tr>
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader">Administration</span><br>
        <cti:checkRole roleid="<%=AdministratorRole.ROLEID%>">
		<%= links.get("user_ee.profile.jsp") %><br>
		</cti:checkRole>
	  </div>
    </td>
  </tr>
  </cti:checkMultiRole>
</table>
