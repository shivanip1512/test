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
                      <td width="605" class="Main" > 
                        <p align="center"><b><br>
                            ADMINISTRATION - CUSTOMER PROFILE</b><br>
                            There must be a Primary Contact. Any number of additional 
                            contacts may also be included. 
                      </td>
                      <td width="45"> 
                        <input type="button" name="Add" value="New Contact" onclick="location.href='<%=request.getContextPath()%>/user/CILC/user_ee_new_profile.jsp'">
                      </td>
                    </tr>
                  </table>
                  <br>
                  <table width="600" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="40">
                        <hr>
                        <span class="Main"><b>CONTACT (PRIMARY)</b></span></td>
                    </tr>
                  </table>
                  <table width="600" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top">
                        <table width="300" border="0" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td class="TableCell" width="110" align="right">First Name:</td>
                            <td class="TableCell" width="190" align="left"> 
                              <input type="text" name="textfield" value="<%=liteContact.getContFirstName()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="110" align="right">Last Name:</td>
                            <td class="TableCell" width="190" align="left"> 
                             <input type="text" name="textfield2" value="<%=liteContact.getContLastName()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="110" align="right">User Name:</td>
                            <td class="TableCell" width="190" align="left">
                              <input type="text" name="textfield4" value ="<%=liteYukonUser.getUsername()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="110" align="right">Old Password:</td>
                            <td class="TableCell" width="190" align="left">
                              <input type="password" name="textfield5" value ="<%=liteYukonUser.getPassword()%>">
                            </td>
                          </tr>

                          <tr>
                            <td class="TableCell" width="110" align="right">New Password:</td>
                            <td class="TableCell" width="190" align="left">
                              <input type="password" name="textfield5" value ="<%=liteYukonUser.getPassword()%>">
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top">
                        <table width="300" border="0" cellspacing="0" cellpadding="2">
                        <%
                          java.util.Vector notifications = liteContact.getLiteContactNotifications();
                          for (int j = 0; j < notifications.size(); j++) {
                          com.cannontech.database.data.lite.LiteContactNotification liteNot = (com.cannontech.database.data.lite.LiteContactNotification)notifications.get(j);
                        %>
                          <tr>
                          <% if(liteNot != null){%>
                            <td width="150" class="TableCell" align = "right">
                              <cti:select name="program" selectValues="<%= entryIDs %>" selectNames="<%= entryTexts %>" selectedValue="<%=String.valueOf(liteNot.getNotificationCategoryID())%>"/></td>
                            <td width="150" align="left" class="TableCell">
                              <input type="text" name="textfield3"  value="<%=liteNot.getNotification()%>">
                            </td>
                          <%}%>
                          </tr>
                        <%} 
                          for (int j = notifications.size(); j < 5; j++)
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
                        <table width="600" border="0" cellspacing="0" cellpadding="2">
                          <tr>
                            <td class="TableCell" align="right" height="40"> 
                              <input type="submit" name="Delete" value="Delete">
                            </td>
                            <td class="TableCell" align="left" height="40">
                              <input type="submit" name="Add" value="Update">
                            </td>
                          </tr>
                        </table>
                  
                  <%
//                  IF THEY HAVE ADDITIONAL CONTACTS
                    com.cannontech.database.data.lite.LiteCICustomer liteCICust =  com.cannontech.database.cache.functions.ContactFuncs.getCICustomer(liteContact.getContactID());
                    java.util.Vector addtlConts = liteCICust.getAdditionalContacts();
                    for( int i = 0; i < addtlConts.size(); i++)
                    {
                      com.cannontech.database.data.lite.LiteContact liteAddtlCont = (com.cannontech.database.data.lite.LiteContact)addtlConts.get(i);
                      LiteYukonUser addtlLiteYukonUser = YukonUserFuncs.getLiteYukonUser(liteAddtlCont.getLoginID());                      
                  %>
                  <table width="600" border="0" cellspacing="0" cellpadding="0">	              
                    <tr>
                      <td height="40">
                        <hr>
                        <span class="Main"><b>CONTACT</b></span>
                      </td>
                    </tr>
                  </table>
                  <%@include file="user_ee_contacts.jsp"%>
                  <!-- <CLIP> -->
                  <%}%>                                          
                  <table width="600" border="0" cellspacing="0" cellpadding="3">
                    <tr>
                      <td align="left">
                        <p align="center" class="MainHeader"> <a href="javascript:history.back()" class="Link1"><b>Back to Previous Page</b></a><br>
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
