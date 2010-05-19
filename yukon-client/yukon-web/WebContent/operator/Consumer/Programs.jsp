<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean hasPrevStep = false;
	
	if (programs == null) programs = new StarsLMPrograms();
	
	boolean autoConfig = DaoFactory.getAuthDao().checkRoleProperty(lYukonUser, ConsumerInfoRole.AUTOMATIC_CONFIGURATION);
	
	String trackHwAddr = liteEC.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
	boolean useHardwareAddressing = (trackHwAddr != null) && Boolean.valueOf(trackHwAddr).booleanValue();
	
	boolean needMoreInfo = false;
	
	int hardwareCnt = 0;
	for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
		if (inventories.getStarsInventory(i).getLMHardware() != null)
			hardwareCnt++;
	}
	needMoreInfo = hardwareCnt > 1 || autoConfig && !useHardwareAddressing && hardwareCnt > 0;
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<div id = "tool" class = "tooltip" style="width: 1003px; height: 20px"></div>
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
      <script language="JavaScript">setContentChanged(<%= inWizard %>);</script>
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
<% if (!inWizard) { %>
		  <% String pageName = "Programs.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "PROGRAMS - ENROLLMENT"; %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jspf" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jspf" %>
<% } %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <cti:checkRolesAndProperties value="OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY">
              	  <%@ include file="../../include/program_enrollment_multi.jspf" %>
              </cti:checkRolesAndProperties>
              <cti:checkRolesAndProperties value="!OPERATOR_ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY">
              	  <%@ include file="../../include/program_enrollment.jspf" %>	
              </cti:checkRolesAndProperties>
              <div align="center" class="SubtitleHeader">Program History</div>
              <table width="450" border="1" cellspacing="0" align="center" cellpadding="3">
                <tr> 
                  <td class="HeaderCell" width="130" >Date</td>
                  <td class="HeaderCell" width="120" >Type</td>
                  <td class="HeaderCell" width="145" >Program</td>
                </tr>
<%
	if (programHistory != null) {
		int eventCnt = programHistory.getStarsLMProgramEventCount();
		for (int i = eventCnt - 1; i >= 0 && i >= eventCnt - 3; i--) {
			StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(i);
			
			String progNames = "";
			for (int j = 0; j < event.getProgramIDCount(); j++) {
				StarsEnrLMProgram enrProg = ServletUtils.getEnrollmentProgram(categories, event.getProgramID(j));
				if (enrProg != null)
					progNames += ServletUtils.getProgramDisplayNames(enrProg)[0] + "<br>";
			}
			if (progNames.equals("")) continue;
%>
                <tr> 
                  <td class="TableCell" width="130" ><cti:formatDate value="<%=event.getEventDateTime()%>" type="BOTH"/></td>
                  <td class="TableCell" width="120" ><%= event.getEventAction() %></td> 
                  <td class="TableCell" width="145" ><%= progNames %></td>
                </tr>
<%
		}
	}
%>
              </table>
<%
	if (programHistory != null && programHistory.getStarsLMProgramEventCount() > 3) {
%>
              <table width="300" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td> 
                    <div align="right"> 
                      <input type="button" name="More" value="More" onClick="location='ProgHist.jsp'">
                    </div>
                  </td>
                </tr>
              </table>
<%
	}
%>
              <p>&nbsp;</p>
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
