<html>
<%@ include file="../user_header.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList"%>
<%@ page import="com.cannontech.common.constants.YukonListFuncs"%>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs"%>
<%@ page import="com.cannontech.common.constants.YukonListEntry"%>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<%
	YukonSelectionList yukonSelectionList = YukonListFuncs.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE);
	java.util.ArrayList yukonListEntries = yukonSelectionList.getYukonListEntries();
	String [] entryIDs = new String[yukonListEntries.size() + 1];
	String [] entryTexts = new String[yukonListEntries.size() + 1];
	entryIDs[0] = String.valueOf(-1);
	entryTexts[0] = "(none)";
	for(int i = 0; i < yukonListEntries.size(); i++)
	{
		//Using an index + 1 so we can have the "(none)" value in the notification type box also.
		YukonListEntry listEntry = (YukonListEntry)yukonListEntries.get(i);
		entryIDs[i + 1] = String.valueOf(listEntry.getEntryID());
		entryTexts[i + 1] = listEntry.getEntryText().toString();
	}
%>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td  valign="top" width="101">
          <%//this pageName is only used for nav.jsp highlighting the correct nav.%>
          <% String pageName = "user_ee_profile.jsp"; %>
          <%@ include file="nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF">
            <table width="657" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="650" class="Main" valign="top">
                <form method="GET" action="<%=pageName%>" name="MForm">
                <center>
                  <table width="650" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="Main" > 
                        <p align="center"><b><br>
                            ADMINISTRATION - CUSTOMER PROFILE</b><br>
                            Enter the contact information and click Submit to add this contact to your profile.<br>
                            A contact may have up to four distinct notification types.
                      </td>
                    </tr>
                  </table>
                  <br>
                  <table width="600" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="40">
                        <hr>
                        <span class="Main"><b>NEW CONTACT</b></span></td>
                    </tr>
                  </table>
                  <table width="600" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td>
                        <table width="300" border="0" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td class="TableCell" width="130" align="right">First Name:</td>
                            <td class="TableCell" width="170" align="left"> 
                              <input type="text" name="textfield">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">Last Name:</td>
                            <td class="TableCell" width="170" align="left"> 
                             <input type="text" name="textfield2">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">User Name:</td>
                            <td class="TableCell" width="170" align="left">
                              <input type="text" name="textfield4">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130">
                              <div align="right">Password:</div>
                            </td>
                            <td class="TableCell" width="170">
                              <input type="password" name="textfield5">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130">
                              <div align="right">Confirm Password:</div>
                            </td>
                            <td class="TableCell" width="170">
                              <input type="password" name="textfield5">
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top">
                        <table width="300" border="0" cellspacing="0" cellpadding="2">
                        <%
                          for (int j = 0; j < 5; j++)
                          { %>
                          <tr>
                            <td width="150" class="TableCell" align = "right">
                              <cti:select name="program" selectValues="<%= entryIDs %>" selectNames="<%= entryTexts %>" selectedValue="<%="-1"%>"/></td>
	                            <td width="150" align="left" class="TableCell">
                              <input type="text" name="textfield3">
                            </td>
                          </tr>
                        <%} %>                    
                        </table>
                      </td>
                    </tr>
                  </table>
                  <table width="600" border="0" cellspacing="0" cellpadding="3">
                    <tr>
                      <td width="300" align="right"> 
                        <input type="submit" name="Submit" value="Submit">
                      </td>
                      <td width="300" align="left">
                        <input type="button" name="Cancel" value="Cancel" onclick="javascript:history.back()">
                      </td>
                    </tr>
                  </table>
                </center>
                </form>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
