<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<cti:standardPage title="Consumer Energy Services" module="commercialcurtailment_user" htmlLevel="transitional">
<cti:standardMenu />

<%@ include file="../include/user_header.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList"%>
<%@ page import="com.cannontech.database.cache.functions.YukonListFuncs"%>
<%@ page import="com.cannontech.common.constants.YukonSelectionListDefs"%>
<%@ page import="com.cannontech.common.constants.YukonListEntry"%>

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

<table width="609" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="600" class="MainText" valign="top">
                <form method="get" action="user_ee_profile_sp.jsp" name="MForm">
                <center>
                  <table width="590" height="70" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="TitleHeader" align="center">ADMINISTRATION - CUSTOMER PROFILE</td>
                    </tr>
                    <tr>
                      <td class="MainText" align="center">Enter the contact information and click Submit to add this contact to your profile.<br>
                            A contact may have up to four distinct notification types.
                      </td>
                    </tr>
                  </table>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="40" class="TitleHeader">
                        <hr>NEW CONTACT</td>
                    </tr>
                  </table>
                  <table width="590" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td>
                        <table width="295" border="0" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td class="TableCell" width="130" align="right">First Name:</td>
                            <td class="TableCell" width="165" align="left"> 
                              <input type="text" name="textfield">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">Last Name:</td>
                            <td class="TableCell" width="165" align="left"> 
                             <input type="text" name="textfield2">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130" align="right">User Name:</td>
                            <td class="TableCell" width="165" align="left">
                              <input type="text" name="textfield4">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130">
                              <div align="right">Password:</div>
                            </td>
                            <td class="TableCell" width="165">
                              <input type="password" name="textfield5">
                            </td>
                          </tr>
                          <tr>
                            <td class="TableCell" width="130">
                              <div align="right">Confirm Password:</div>
                            </td>
                            <td class="TableCell" width="165">
                              <input type="password" name="textfield5">
                            </td>
                          </tr>
                        </table>
                      </td>
                      <td valign="top">
                        <table width="295" border="0" cellspacing="0" cellpadding="2">
                        <%
                          for (int j = 0; j < 5; j++)
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
                  <table width="590" border="0" cellspacing="0" cellpadding="3">
                    <tr>
                      <td width="50%" align="right"> 
                        <input type="submit" name="Submit" value="Submit">
                      </td>
                      <td width="50%" align="left">
                        <input type="button" name="Cancel" value="Cancel" onclick="javascript:history.back()">
                      </td>
                    </tr>
                  </table>
                </center>
                </form>
                </td>
              </tr>
            </table>
</cti:standardPage>