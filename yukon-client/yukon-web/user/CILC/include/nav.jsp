<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%
	// Table of (link, page name) pairs
	String linkPairs[][] = {{"user_ee.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, EnergyBuybackRole.ENERGY_BUYBACK_LABEL)},
						  {"user_curtail.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, DirectCurtailmentRole.CURTAILMENT_LABEL)},
						  {"user_lm_control.jsp", "Auto Control"},
						  {"user_lm_time.jsp", "Time Based"},
						  {"switch_commands.jsp", "Switch Command"},
						  {"user_trending.jsp", "Trending"},
						  {"user_ee_profile.jsp", "Profile"}
						 };
	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(liteYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='9' height='9'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkPairs.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		if (linkPairs[i][0].equalsIgnoreCase(pageName))
		{
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkPairs[i][1] + "</span>";
		}
		else
		{
			linkImg = "";
			linkHtml = "<a href='" + linkPairs[i][0] + "' class='Link2'><span class='NavText'>" + linkPairs[i][1] + "</span></a>";
		}
		links.put(linkPairs[i][0], new String[] {linkImg, linkHtml});
	}
%>

<table width="150" border="0" cellspacing="0" cellpadding="6">
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
		{%>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			<%
			for( int i = 0; i < gData.length; i++ )                                                          
			{
				if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid() && linkPairs[5][0].equalsIgnoreCase(pageName))
				{%>
          <tr> 
            <td width="10"><%= bulletImg %></td>
            <td style="padding:1"><span class="Nav"><%=gData[i][1] %></span></td>
          </tr>
				<%}
				else 
				{%>
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%=request.getContextPath()%>/user/CILC/user_trending.jsp?<%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a></td>
          </tr>
				<%}
			}%>
        </table>
		<%}%>
	  </div>
    </td>
  </tr>
  <cti:checkProperty propertyid="<%=CommercialMeteringRole.TRENDING_GET_DATA_NOW_BUTTON%>">
  <tr>
    <td height="30" valign="bottom">
      <div align="left"><span class="NavHeader">Display</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%=request.getContextPath()%>/user/CILC/user_trending.jsp?update=now" class="link2"><span class="NavText">Get Data Now</span></a></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkProperty>
  </cti:checkRole>

  <cti:checkMultiRole roleid="<%=Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID)%>">
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader">Buyback</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkRole roleid="<%=EnergyBuybackRole.ROLEID%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_ee.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_ee.jsp"))[1] %></td>
          </tr>
          </cti:checkRole>
		  <cti:checkRole roleid="<%=DirectCurtailmentRole.ROLEID%>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_curtail.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_curtail.jsp"))[1] %></td>
          </tr>
          </cti:checkRole>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkMultiRole>
  <cti:checkRole roleid="<%=UserControlRole.ROLEID%>">
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader"><cti:getProperty propertyid="<%= UserControlRole.USER_CONTROL_LABEL%>"/></span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkProperty propertyid="<%=UserControlRole.AUTO%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_lm_control.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_lm_control.jsp"))[1] %></td>
          </tr>
          </cti:checkProperty>
          <cti:checkProperty propertyid="<%=UserControlRole.TIME_BASED%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_lm_time.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_lm_time.jsp"))[1] %></td>
          </tr>
          </cti:checkProperty>
          <cti:checkProperty propertyid="<%=UserControlRole.SWITCH_COMMAND%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("switch_commands.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("switch_commands.jsp"))[1] %></td>
          </tr>
          </cti:checkProperty>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkRole>
  <cti:checkRole roleid="<%=AdministratorRole.ROLEID%>"> 
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader">Administration</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_ee_profile.jsp"))[0] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_ee_profile.jsp"))[1] %></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkRole>
</table>
