<%@ include file="include/StarsHeader.jsp" %>
<%
	boolean inWizard = request.getParameter("Wizard") != null;
	if (!inWizard && accountInfo == null) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean hasPrevStep = false;
	
	if (inWizard) {
		programs = new StarsLMPrograms();
		
		MultiAction actions = (MultiAction) session.getAttribute(ServletUtils.ATT_NEW_ACCOUNT_WIZARD);
		if (actions != null) {
			SOAPMessage reqMsg = actions.getRequestMessage( ProgramSignUpAction.class );
			if (reqMsg != null) {
				StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);
				StarsProgramSignUp progSignUp = reqOper.getStarsProgramSignUp();
				if (progSignUp != null) {
					for (int i = 0; i < progSignUp.getStarsSULMPrograms().getSULMProgramCount(); i++) {
						SULMProgram suProg = progSignUp.getStarsSULMPrograms().getSULMProgram(i);
						StarsLMProgram program = new StarsLMProgram();
						program.setProgramID( suProg.getProgramID() );
						program.setApplianceCategoryID( suProg.getApplianceCategoryID() );
						program.setStatus("Not Enrolled");
						programs.addStarsLMProgram( program );
					}
				}
			}
			
			hasPrevStep = actions.getRequestMessage( CreateLMHardwareAction.class ) != null;
		}
	}
	
	if (programs == null) programs = new StarsLMPrograms();
	
	boolean autoConfig = AuthFuncs.checkRoleProperty(lYukonUser, ConsumerInfoRole.AUTOMATIC_CONFIGURATION);
	
	String trackHwAddr = liteEC.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
	boolean useHardwareAddressing = (trackHwAddr != null) && Boolean.valueOf(trackHwAddr).booleanValue();
	
	boolean needMoreInfo = false;
	if (inWizard) {
		needMoreInfo = autoConfig && !useHardwareAddressing;
	}
	else {
		int hardwareCnt = 0;
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			if (inventories.getStarsInventory(i).getLMHardware() != null)
				hardwareCnt++;
		}
		needMoreInfo = hardwareCnt > 1 || autoConfig && !useHardwareAddressing && hardwareCnt > 0;
	}
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
      <%@ include file="include/HeaderBar.jsp" %>
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
          <%@ include file="include/Nav.jsp" %>
<% } %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.WEB_TITLE_ENROLLMENT, "PROGRAMS - ENROLLMENT"); %>
<% if (!inWizard) { %>
              <%@ include file="include/InfoSearchBar.jsp" %>
<% } else { %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
<% } %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <%@ include file="../../include/program_enrollment.jsp" %>
<% if (request.getParameter("Wizard") == null) { %>
              <div align="center" class="SubtitleHeader">Program History</div>
              <table width="366" border="1" cellspacing="0" align="center" cellpadding="3">
                <tr> 
                  <td class="HeaderCell" width="75" >Date</td>
                  <td class="HeaderCell" width="120" >Type - Duration</td>
                  <td class="HeaderCell" width="145" >Program</td>
                </tr>
<%
	if (programHistory != null) {
		int eventCnt = programHistory.getStarsLMProgramEventCount();
		for (int i = eventCnt - 1; i >= 0 && i >= eventCnt - 3; i--) {
			StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(i);
			
			String durationStr = "";
			if (event.hasDuration())
				durationStr = ServletUtils.getDurationFromHours(event.getDuration());
			
			String scheduledStr = "";
			if (event.hasDuration() && event.getEventDateTime().after(new Date()))
				scheduledStr = "(Scheduled)";
			
			String progNames = "";
			for (int j = 0; j < event.getProgramIDCount(); j++) {
				StarsEnrLMProgram enrProg = ServletUtils.getEnrollmentProgram(categories, event.getProgramID(j));
				if (enrProg != null)
					progNames += ServletUtils.getProgramDisplayNames(enrProg)[0] + "<br>";
			}
			if (progNames.equals("")) continue;
%>
                <tr> 
                  <td class="TableCell" width="75" ><%= datePart.format(event.getEventDateTime()) %></td>
                  <td class="TableCell" width="120" ><%= event.getEventAction() %> 
                    <% if (event.hasDuration()) { %>
                    - <%= durationStr %> 
                    <% } %>
                    <%= scheduledStr %> </td>
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
