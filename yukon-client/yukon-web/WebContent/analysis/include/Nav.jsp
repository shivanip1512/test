<%
	String bulletImg = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_SELECTED, "Bullet.gif") + "' width='9' height='9'>";
	String bulletImgExp = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_BULLET_EXPAND, "BulletExpand.gif") + "' width='9' height='9'>";
	String connImgMid = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_MIDDLE, "MidConnector.gif") + "' width='10' height='12'>";
	String connImgBtm = "<img src='../WebConfig/" + AuthFuncs.getRolePropertyValue(lYukonUser, WebClientRole.NAV_CONNECTOR_BOTTOM, "BottomConnector.gif") + "' width='10' height='12'>";
	
	// List of String[] (link image, link html)
	Hashtable links = new Hashtable();
	
String linkHtml = null;
String linkImgExp = null;
int grpType = 0;
%>
<table width="101" border="0" cellspacing="0" cellpadding="5">
<%--<cti:checkProperty propertyid="<%= ReportingRole.ADMIN_REPORTS_GROUP %>">--%>
  <tr> 
    <td> 
      <div align="left">
	    <span class="NavHeader"><cti:getProperty propertyid="<%= ReportingRole.HEADER_LABEL %>" defaultvalue="Reporting"/></span><br>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
<%--        <cti:checkProperty propertyid="<%=ReportingRole.ADMIN_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.ADMIN_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.AMR_REPORTS_GROUPP%>">--%>
			<% grpType = ReportTypes.AMR_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.CAP_CONTROL_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.CAP_CONTROL_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.DATABASE_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.DATABASE_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.LOAD_MANAGEMENT_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.LOAD_MANAGEMENT_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.STATISTICAL_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.STATISTICAL_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>
<%--        <cti:checkProperty propertyid="<%=ReportingRole.STARS_REPORTS_GROUP%>">--%>
			<% grpType = ReportTypes.STARS_REPORTS_GROUP;%>
          <tr>
            <td width="10">&nbsp;</td>
            <td>
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<%	
				if( REPORT_BEAN.getGroupType() == grpType)
				{
					linkHtml = "<span class='Nav' style='cursor:default'>" + ReportTypes.getReportGroupName(grpType) + "</span>";
					linkImgExp = bulletImg;
				}
				else
				{
					linkHtml = "<span class='NavTextNoLink' style='cursor:default'>"+ReportTypes.getReportGroupName(grpType)+"</span>";
					linkImgExp = bulletImgExp;
				}
				%>
                <tr onmouseover="basicMenuAppear(event, this, '<%=ReportTypes.getReportGroupName(grpType)%>ReportMenu')"> 
                  <td><%=linkHtml%></td>
                  <td width="10" valign="bottom" style="padding-bottom:1"><%=linkImgExp%></td>
                </tr>
              </table>
            </td>
          </tr>
<%--        </cti:checkProperty>--%>

        </table>
	  </div>
    </td>
  </tr>
<%--</cti:checkProperty>--%>
</table>
<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/nav_menu.js"></script>
<script language="JavaScript" src="<%= request.getContextPath() %>/JavaScript/change_monitor.js"></script>
<%
for (int i = 0; i < ReportTypes.getGroupToTypeMap().length; i++)
{%>
<div id="<%=ReportTypes.getReportGroupName(i)%>ReportMenu" class="bgMenu" style="width:75px" align="left">	
<%{
	int [] rptTypes = ReportTypes.getGroupToTypeMap()[i];
	for (int j = 0; j < rptTypes.length; j++){
		String className = "navmenu1";
		String indicator = "&nbsp;&nbsp;&nbsp;";
		if( rptTypes[j] == REPORT_BEAN.getType())
		{
			className = "navmenu2";
			indicator = "&nbsp;&#149;&nbsp;";
		}%>
	  <div id="<%=ReportTypes.getReportName(rptTypes[j])%>" name="rptTypeMenu" style="width:170px;" onmouseover="changeNavStyle(this)" class = "<%=className%>" onclick = "if (warnUnsavedChanges()) location='<%=request.getContextPath()%>/analysis/reporting.jsp?type=<%=rptTypes[j]%>';">
	  <%=indicator%><%=ReportTypes.getReportName(rptTypes[j])%>
	  </div>
	  <%}
	}%>
</div>	
<%}%>
