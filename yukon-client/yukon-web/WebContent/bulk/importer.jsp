<%@ page language="java" %>
<%@ taglib uri="../WEB-INF/cti.tld" prefix="cti" %>

<jsp:useBean id="refreshRate" scope="session"
	class="com.cannontech.yimp.util.ImportWebInfoObject"
/>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>Yukon MCT-410 Bulk Importer</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="theme/YimpStyle.css" type="text/css">

<style type="text/css">
<!--
input {
	background-color: white;
	font-weight: bold;
	text-align: center
}

.BodyTitleText {
	color: black;
	font-weight: bold;
	font-size: 14pt
}

th {
	color: black;
	font-weight: bold
}

.HeaderCellCrucial {
	color: white;
	font-weight: bold;
	background-color: #0066CC
}
-->
</style>
<script type="text/javascript" src="/JavaScript/prototype.js"></script>
<script type="text/javascript">
	function init() {
		new Ajax.PeriodicalUpdater("attempt_refresher", 'importer_next_attempt.jsp', {"frequency":<%=refreshRate.REFRESH_RATE%>});
	    new Ajax.PeriodicalUpdater("status_refresher", 'importer_status_screen.jsp', {"frequency":<%=refreshRate.REFRESH_RATE%>});
	}
	
</script>
</head>

<body bgcolor="#0066CC" leftmargin="0" topmargin="0" text="#CCCCCC" link="#000000" vlink="#000000" alink="#000000" onload="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
    	<div id="attempt_refresher">
			<%@ include file="importer_next_attempt.jsp" %>
		</div>   
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr>
          <td width="759" bgcolor="#000000" valign="top"></td>
		  <td width="1" bgcolor="#000000" height="2"></td>
        </tr>
        <tr>
		  <td width="5" bgcolor="#FFFFFF" height="5"></td>
        </tr>
        <tr> 
          <td width="759" valign="top" bgcolor="#FFFFFF"> 
            <table width="740" border="0" cellspacing="0" cellpadding="0" align="center">
              <tr>
                <td></td>
              </tr>
            </table>
				<div align="right">
				<table border="0" cellspacing="0" cellpadding="0">
					<tbody>
						<tr>
							<td valign="top" class="BodyTitleText" align="center" width="757">
								<br>
								<form name="loadDataForm" method="POST" action="<%= request.getContextPath() %>/servlet/ImporterServlet" enctype="multipart/form-data">
									<table border="1" width="740">
										<tbody>
											<tr class="HeaderCell">
												<td width="231">
													<SPAN class="HeaderCellCrucial">&nbsp;Load data from spreadsheet: 
														<font color="#000000"> </font>
													</SPAN>
												</td>
							                    <td width="314"> 
							                      	<input type="file" name="dataFile" size="35">
							                    </td>
							                    <td width="200">
							                    	<div align="center">
							                    		<input type="submit" name="loadData" value="Submit">
							                    	</div>
												</td>
												
												<% if ((String) session.getAttribute("LOAD_IMPORTDATA_SUCCESS") != null) out.write("<span class=\"ConfirmMsg\"> " + ((String) session.getAttribute("LOAD_IMPORTDATA_SUCCESS")).replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
												<% if ((String) session.getAttribute("LOAD_IMPORTDATA_ERROR") != null) out.write("<span class=\"ErrorMsg\"> " + ((String) session.getAttribute("LOAD_IMPORTDATA_ERROR")).replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
											</tr>
										</tbody>
									</table>
								</form>
								<br>
								<div id="status_refresher">
									<%@ include file="importer_status_screen.jsp" %>
								</div> 
							</td>
						</tr>
					</tbody>
				</table>
				</div>
			</td>
    	</tr>
      </table>
    </td>
	</tr>
</table>
</body>
</html>
