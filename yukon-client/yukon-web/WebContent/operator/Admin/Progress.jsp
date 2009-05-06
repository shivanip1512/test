<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.stars.util.ServletUtils" %>
<%@ page import="com.cannontech.stars.util.ProgressChecker" %>
<%@ page import="com.cannontech.stars.util.task.TimeConsumingTask" %>
<%@ page import="com.cannontech.stars.util.task.ImportCustAccountsTask" %>
<%@ page import="com.cannontech.stars.util.task.DeleteEnergyCompanyTask" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<%@ page import="com.cannontech.stars.web.StarsYukonUser" %>
<%@ page import="com.cannontech.common.version.VersionTools" %>
<%@ page import="com.cannontech.database.cache.StarsDatabaseCache"%>
<%

    LiteStarsEnergyCompany liteEC = null;
    final StarsYukonUser user = ServletUtils.getStarsYukonUser(session);
	boolean starsExists = false;
	try {
	    starsExists = VersionTools.starsExists(); 
	} catch (RuntimeException ignore) { }
	
	if ((starsExists) && (user != null)) {
	    liteEC = StarsDatabaseCache.getInstance().getEnergyCompany(user.getEnergyCompanyID());
	}
	
	long id = Long.parseLong(request.getParameter("id"));
	TimeConsumingTask task = ProgressChecker.getTask(id);
	
	if (request.getParameter("Remove") != null)
		ProgressChecker.removeTask(id);
	
	boolean isStopped = false;
	String taskProgress = null;
	String taskError = null;
	
	if (task != null) {
		if (request.getParameter("Cancel") != null) {
			ProgressChecker.cancelTask(id);
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
		}
		
		taskProgress = task.getProgressMsg();
		if (taskProgress != null)
			taskProgress = taskProgress.replaceAll(System.getProperty("line.separator"), "<br>");
		
		taskError = task.getErrorMsg();
		if (taskError != null)
			taskError = taskError.replaceAll(System.getProperty("line.separator"), "<br>");
		
		if (task.getStatus() == TimeConsumingTask.STATUS_FINISHED) {
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, taskProgress);
			
			ProgressChecker.removeTask(id);
			isStopped = true;
			if(task.getClass().equals(DeleteEnergyCompanyTask.class)) {
			    String redirect = request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT";
		        response.sendRedirect(redirect);
            }else{
				String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
				if (redirect != null) {
				    redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
				    response.sendRedirect(redirect);
				}
            }
		}
		else if (task.getStatus() == TimeConsumingTask.STATUS_ERROR ||
				task.getStatus() == TimeConsumingTask.STATUS_CANCELED)
		{
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, taskProgress);
			if (task.getStatus() == TimeConsumingTask.STATUS_ERROR) {
				if (task instanceof ImportCustAccountsTask) {
					ProgressChecker.removeTask(id);
					session.setAttribute("errorList", ((ImportCustAccountsTask) task).getErrorList());
					session.setAttribute(ServletUtils.ATT_REFERRER, request.getContextPath() + "/operator/Consumer/ImportManagerView.jsp");
				}
				if (taskError != null)
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, taskError);
				else
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "An error occurred during the operation.");
			}
			else
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Operation is canceled by user");
			
			ProgressChecker.removeTask(id);
			isStopped = true;
			
			String referer = (String) session.getAttribute(ServletUtils.ATT_REFERRER);
			if (referer != null) {
			    referer = ServletUtil.createSafeRedirectUrl(request, referer);
			    response.sendRedirect(referer);
			}
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
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <%@ include file="include/HeaderBar.jspf" %>
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
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
                    Error occurred during operation. 
                    <% } %>
                    <br>
                    <br>
                    <% if (taskProgress != null) out.write("<span class='ConfirmMsg'>" + taskProgress + "</span><br>"); %>
                    <% if (taskError != null) out.write("<span class='ErrorMsg'>" + taskError + "</span><br>"); %>
<% } else { %>
                    No operation found with the specified ID. 
<% } %>
                    <br>
                  </td>
                </tr>
              </table>
              <form name="form1" method="post" action="" onsubmit="return confirm('Are you sure you want to cancel the current operation?')">
                <input type="submit" name="Cancel" value="Cancel">
              </form>
              <p>&nbsp;</p>
              <p>&nbsp;</p>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../WebConfig/yukon/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
