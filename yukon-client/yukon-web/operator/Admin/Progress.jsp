<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.stars.util.ProgressChecker" %>
<%@ page import="com.cannontech.stars.util.task.TimeConsumingTask" %>
<%
	long id = Long.parseLong(request.getParameter("id"));
	TimeConsumingTask task = ProgressChecker.getTask(id);
	
	boolean isStopped = false;
	
	if (task != null) {
		if (request.getParameter("Cancel") != null) {
			task.cancel();
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
		}
		
		if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED ||
			task.getStatus() == TimeConsumingTask.STATUS_CANCELED ||
			task.getStatus() == TimeConsumingTask.STATUS_ERROR)
		{
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
			if (task.getStatus() == TimeConsumingTask.STATUS_ERROR)
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
			else if (task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Operation is canceled by user");
			
			ProgressChecker.removeTask(id);
			isStopped = true;
			
			String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
			if (redirect != null) response.sendRedirect(redirect);
		}
	}
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<% if (task != null && !isStopped) { %>
<meta http-equiv="Refresh" content="5">
<% } %>
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="AdminImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Administration</td>
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="TitleHeader">OPERATION PROGRESS</span><br>
              <p>&nbsp;</p>
              <table width="450" border="1" cellspacing="0" cellpadding="2" bgcolor="#CCCCCC" height="150">
                <tr> 
                  <td height="30" align="center" class="MainText"> 
<% if (task != null) { %>
                    <% if (task.getStatus() == TimeConsumingTask.STATUS_NOT_INIT) { %>
                    Operation is not started yet.
                    <% } else if (task.getStatus() == TimeConsumingTask.STATUS_RUNNING) { %>
                    Operation is in progress... 
                    <% } else if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) { %>
                    Operation is finished. 
                    <% } else if (task.getStatus() == TimeConsumingTask.STATUS_CANCELING) { %>
                    Waiting for operation to be canceled... 
                    <% } else if (task.getStatus() == TimeConsumingTask.STATUS_CANCELED) { %>
                    Operation is canceled. 
                    <% } else if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) { %>
                    Error occured during operation. 
                    <% } %>
                    <br>
                    <br>
                    <% if (task.getProgressMsg() != null) out.write("<span class='ConfirmMsg'>" + task.getProgressMsg() + "</span><br>"); %>
                    <% if (task.getErrorMsg() != null) out.write("<span class='ErrorMsg'>" + task.getErrorMsg() + "</span><br>"); %>
<% } else { %>
                    No operation found with the specified ID. 
<% } %>
                    <br>
                  </td>
                </tr>
              </table>
              <form name="form1" method="post" action="">
                <input type="submit" name="Cancel" value="Cancel">
              </form>
              <p>&nbsp;</p>
              <p>&nbsp;</p>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
