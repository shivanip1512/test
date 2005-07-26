<script language="JavaScript">
function dispStatusMsg(msgStr) 
{
  status=msgStr;
  document.statVal = true;
}
</script>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%
	//index vars for LinkPairs
	int FILE = 0;
	int LABEL = 1;
	int DIR = 2;
	
	//index vars for links (hashtable) key=filename value=String[]link image, link html)
	int IMG = 0;
	int HTML = 1;
	// Table of (link, page name, dir location) pairs
	String linkPairs[][] = {{"user_ee.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, EnergyBuybackRole.ENERGY_BUYBACK_LABEL), "/user/CILC/"},
						  {"user_curtail.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, DirectCurtailmentRole.CURTAILMENT_LABEL), "/user/CILC/"},
  						  {"user_direct.jsp", AuthFuncs.getRolePropertyValue(liteYukonUser, DirectLoadcontrolRole.LOADCONTROL_LABEL), "/user/CILC/"},
						  {"user_lm_control.jsp", "Auto Control", "/user/CILC/"},
						  {"user_lm_time.jsp", "Time Based", "/user/CILC/"},
						  {"switch_commands.jsp", "Switch Command", "/user/CILC/"},
						  {"user_trending.jsp", "Trending", "/user/CILC/"},
  						  {"user_reporting.jsp", "Reporting", "/user/CILC/"},
  						  {"user_get_data_now.jsp", "Get Data Now", "/user/CILC/"},
						  {"user_ee_profile.jsp", "Profile", "/user/CILC/"}
						 };
	String bulletImg = "<img src='../../WebConfig/" + AuthFuncs.getRolePropertyValue(liteYukonUser, WebClientRole.NAV_BULLET_SELECTED) + "' width='9' height='9'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	for (int i = 0; i < linkPairs.length; i++) {
		String linkImg = null;
		String linkHtml = null;
		if (linkPairs[i][FILE].equalsIgnoreCase(pageName))
		{
			linkImg = bulletImg;
			linkHtml = "<span class='Nav'>" + linkPairs[i][LABEL] + "</span>";
		}
		else
		{
			linkImg = "";
			linkHtml = "<a href='" + request.getContextPath()+ linkPairs[i][DIR] + linkPairs[i][FILE] + "' class='Link2'><span class='NavText'>" + linkPairs[i][LABEL] + "</span></a>";
		}
		links.put(linkPairs[i][FILE], new String[] {linkImg, linkHtml});
	}
%>

<table width="150" border="0" cellspacing="0" cellpadding="6">
  <cti:checkMultiRole roleid="<%=Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID)%>">
  <tr> 
    <td bgcolor="#FFFFFF">
	<% if (liteCICustomer != null)
	{%>
	  <div align="left"> <span class="NavText"><%= liteCICustomer.getCompanyName() %><br>
	  <%= liteContact.getContFirstName() + "  " + liteContact.getContLastName() %><br>
		<br>
	  </span><br>
	  </div>
	<%}%>
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
				if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid() && linkPairs[6][FILE].equalsIgnoreCase(pageName))
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
            <td style="padding:1"><a href="<%=request.getContextPath()+linkPairs[6][DIR] + linkPairs[6][FILE]+'?'%><%= "gdefid=" + gData[i][0]%>" class = "link2"><span class="NavText"><%=gData[i][1] %></span></a></td>
          </tr>
				<%}
			}%>
        </table>
		<%}%>
	  </div>
    </td>
  </tr>
  <cti:checkProperty propertyid="<%=CommercialMeteringRole.GET_DATA_NOW_ENABLED%>">
  <tr>
    <td height="30" valign="bottom">
      <div align="left"><span class="NavHeader">Display</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_get_data_now.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_get_data_now.jsp"))[HTML]%></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkProperty>
  </cti:checkRole>

  <cti:checkRole roleid="<%=ReportingRole.ROLEID%>">
  <tr> 
    <td height="30" valign="bottom">
      <div align="left"><span class="NavHeader">Reporting</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_reporting.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_reporting.jsp"))[HTML] %></td>
          </tr>
        </table>
	  </div>
    </td>
  </tr>
  </cti:checkRole>
  <cti:checkMultiRole roleid="<%=Integer.toString(EnergyBuybackRole.ROLEID) + ',' + Integer.toString(DirectCurtailmentRole.ROLEID)+ ',' + Integer.toString(DirectLoadcontrolRole.ROLEID)%>">
  <tr>
    <td height="20">
      <div align="left"><span class="NavHeader">Buyback</span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <cti:checkRole roleid="<%=EnergyBuybackRole.ROLEID%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_ee.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_ee.jsp"))[HTML] %></td>
          </tr>
          </cti:checkRole>
		  <cti:checkRole roleid="<%=DirectCurtailmentRole.ROLEID%>"> 
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_curtail.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_curtail.jsp"))[HTML] %></td>
          </tr>
          </cti:checkRole>
          <cti:isPropertyTrue propertyid="<%= DirectLoadcontrolRole.DIRECT_CONTROL %>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_direct.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_direct.jsp"))[HTML] %></td>
          </tr>
          </cti:isPropertyTrue>

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
            <td width="10"><%= ((String[]) links.get("user_lm_control.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_lm_control.jsp"))[HTML] %></td>
          </tr>
          </cti:checkProperty>
          <cti:checkProperty propertyid="<%=UserControlRole.TIME_BASED%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("user_lm_time.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_lm_time.jsp"))[HTML] %></td>
          </tr>
          </cti:checkProperty>
          <cti:checkProperty propertyid="<%=UserControlRole.SWITCH_COMMAND%>">
          <tr> 
            <td width="10"><%= ((String[]) links.get("switch_commands.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("switch_commands.jsp"))[HTML] %></td>
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
            <td width="10"><%= ((String[]) links.get("user_ee_profile.jsp"))[IMG] %></td>
            <td style="padding:1"><%= ((String[]) links.get("user_ee_profile.jsp"))[HTML] %></td>
          </tr>
        </table>
      </div>
    </td>
  </tr>
  </cti:checkRole>
</table>
