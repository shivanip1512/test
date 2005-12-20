<%@page import="com.cannontech.roles.capcontrol.CBCSettingsRole" %>
<%@page import="com.cannontech.roles.application.CommanderRole" %>
<%@page import="com.cannontech.roles.application.ReportingRole" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td> 
	<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
		<td width="63%">
			<img src="/editor/images/gridSmart.gif" /></td>
		<td width="37%"><div style="text-align: right;">
			<img src="/editor/images/poweredByYukon.gif"/></div>
		</td>
		</tr>

		<table width="100%" border="0" cellspacing="0" cellpadding="0" height="43">
	      <tr><td class="trimBGColor"></td><td class="trimBGColor"></td>
	      </tr>
	
	      <tr><td class="primeBGColor">
	        <div style="text-align: right;"><span class="menu">Module:</span> 
                <select name="select" onChange="javascript:window.location=(this[this.selectedIndex].value);">                
					<cti:isPropertyTrue propertyid="<%= CBCSettingsRole.ACCESS %>">
						<option value="<%=request.getContextPath()%>/capcontrol/subareas.jsp" selected="selected">Cap Control</option>
					</cti:isPropertyTrue>
					<cti:checkRole roleid="<%= CommanderRole.ROLEID %>">
						<option value="<%=request.getContextPath()%>/apps/SelectDevice.jsp">Commander</option>
					</cti:checkRole>
					<option value="<%=request.getContextPath()%>/operator/Operations.jsp">Home</option>					
                    <cti:checkRole roleid="<%= ReportingRole.ROLEID %>">
						<option value="<%=request.getContextPath()%>/analysis/Reports.jsp" >Reporting</option>
                    </cti:checkRole>

                </select>
	          <!-- <a href="x" class="menuLink">Help</a> -->
	          <a href="/servlet/LoginController?ACTION=LOGOUT" class="menuLink">Log Out</a>
			</div>
	      </td></tr>
	
	      <tr><td class="secondBGColor"></td><td class="secondBGColor"></td>
	      </tr>
	
	      <tr><td class="trimBGColor"></td><td class="trimBGColor"></td>
	      </tr>
		</table>

	</td></tr>
</table>