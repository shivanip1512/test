<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<cti:standardPage title="Consumer Energy Services" module="commercialcurtailment_user" htmlLevel="transitional">
<cti:standardMenu />

<%@ include file="../include/user_header.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList"%>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs"%>
<%@ page import="com.cannontech.common.constants.YukonListEntry"%>

<%
	YukonSelectionList yukonSelectionList = DaoFactory.getYukonListDao().getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE);
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

            <table width="609" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="600" valign="top">
                <form method="GET" action="user_ee_profile_sp.jsp" name="MForm">
                <center>
                  <table width="590" height="70" border="0" cellspacing="0" cellpadding="0">
                    <tr><br>
                      <td class="TitleHeader" align="center">ADMINISTRATION - CUSTOMER PROFILE</td>
                    </tr>
                    <tr>
                      <td class="Subtext" align="center">* There must be a Primary Contact. Any number of additional contacts may also be included.</td>
                    </tr>
                    <tr align="right">
                      <td> 	 
                        <input type="button" name="Add" value="New Contact" onclick="location.href='<%=request.getContextPath()%>/user/CILC/user_ee_new_profile_sp.jsp'">
                      </td>
                    </tr>
                  </table>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="40" class="TitleHeader">
                        <hr>CONTACT (PRIMARY)</td>
                    </tr>
                  </table>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td valign="top">
                        <table width="295" border="0" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td class="TableCell" width="130" align="right">First Name:</td>
                            <td class="TableCell" width="165" align="left"> 
                              <input type="text" name="textfield" value="<%=liteContact.getContFirstName()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">Last Name:</td>
                            <td class="TableCell" width="165" align="left"> 
                             <input type="text" name="textfield2" value="<%=liteContact.getContLastName()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">User Name:</td>
                            <td class="TableCell" width="165" align="left">
                              <input type="text" name="textfield4" value ="<%=liteYukonUser.getUsername()%>">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">Old Password:</td>
                            <td class="TableCell" width="165" align="left">
                              <input type="password" name="textfield5" value ="<%=liteYukonUser.getPassword()%>">
                            </td>
                          </tr>

                          <tr>
                            <td class="TableCell" width="130" align="right">New Password:</td>
                            <td class="TableCell" width="165" align="left">
                              <input type="password" name="textfield5" value ="<%=liteYukonUser.getPassword()%>">
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top">
                        <table width="295" border="0" cellspacing="0" cellpadding="2">
                        <%
                          java.util.Vector notifications = liteContact.getLiteContactNotifications();
                          for (int j = 0; j < notifications.size(); j++) {
                          com.cannontech.database.data.lite.LiteContactNotification liteNot = (com.cannontech.database.data.lite.LiteContactNotification)notifications.get(j);
                        %>
                          <tr>
                          <% if(liteNot != null){%>
                            <td width="150" class="TableCell" align = "right">
							  <select name="program">
                              <%
                           	  for (int i = 0; i < entryIDs.length; i++)
   	                       	  {
   	                       	  	if( String.valueOf(liteNot.getNotificationCategoryID()).equalsIgnoreCase(entryIDs[i]))
									out.println("<OPTION VALUE=" + entryIDs[i] + " SELECTED>" + entryTexts[i]);
								else
									out.println("<OPTION VALUE=" + entryIDs[i] + ">" + entryTexts[i]);
							  }%>
							  </select>                            
                            </td>
                            <td width="150" align="left" class="TableCell">
                              <input type="text" name="textfield3"  value="<%=liteNot.getNotification()%>">
                            </td>
                          <%}%>
                          </tr>
                        <%} 
                          for (int j = notifications.size(); j < 5; j++)
                          { %>
                          <tr>
                            <td width="50%" class="TableCell" align = "right">
							  <select name="program">
                            <%
                              for (int i = 0; i < entryIDs.length; i++)
   	                       	  {
								out.println("<OPTION VALUE=" + entryIDs[i] + ">" + entryTexts[i]);
							  }%>
							  </select>
							</td>
	                        <td width="50%" align="left" class="TableCell">
                              <input type="text" name="textfield3">
                            </td>
                          </tr>
                        <%} %>
                        </table>
                      </td>
                    </tr>
                  </table>
                        <table width="590" border="0" cellspacing="0" cellpadding="2">
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
                    com.cannontech.database.data.lite.LiteCICustomer liteCICust =  DaoFactory.getContactDao().getCICustomer(liteContact.getContactID());
                    java.util.Vector addtlConts = liteCICust.getAdditionalContacts();
                    for( int x = 0; x < addtlConts.size(); x++)
                    {
                      com.cannontech.database.data.lite.LiteContact liteAddtlCont = (com.cannontech.database.data.lite.LiteContact)addtlConts.get(x);
                      LiteYukonUser addtlLiteYukonUser = DaoFactory.getYukonUserDao().getLiteYukonUser(liteAddtlCont.getLoginID());                      
                  %>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">	              
                    <tr>
                      <td height="40" class="TitleHeader">
                        <hr>CONTACT</td>
                    </tr>
                  </table>
                  <%@include file="include/user_ee_contacts.jspf"%>
                  <!-- <CLIP> -->
                  <%}%>                                          
                </center>
                </form>
                </td>
              </tr>
            </table>
</cti:standardPage>