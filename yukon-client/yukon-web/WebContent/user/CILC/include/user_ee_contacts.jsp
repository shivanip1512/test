<table width="600" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td valign="top">
      <table width="300" border="0" cellspacing="0" cellpadding="2">
        <tr> 
          <td class="TableCell" width="100" align="right">First Name:</td>
          <td class="TableCell" width="200" align="left"> 
            <input type="text" name="textfield" value="<%=liteAddtlCont.getContFirstName()%>">
          </td>
        </tr>
        <tr>
          <td class="TableCell" width="100" align="right">Last Name:</td>
          <td class="TableCell" width="200" align="left"> 
            <input type="text" name="textfield2" value="<%=liteAddtlCont.getContLastName()%>">
          </td>
        </tr>
        <tr>
          <td class="TableCell" width="100" align="right">User Name:</td>
          <td class="TableCell" width="200" align="left">
            <input type="text" name="textfield4" value ="<%=addtlLiteYukonUser.getUsername()%>">
          </td>
        </tr>
        <tr>
          <td class="TableCell" width="100">
            <div align="right">Old Password:</div>
          </td>
          <td class="TableCell" width="200">
            <input type="password" name="textfield5" value ="<%=addtlLiteYukonUser.getPassword()%>">
          </td>
        </tr>
        <tr>
          <td class="TableCell" width="100">
            <div align="right">New Password:</div>
          </td>
          <td class="TableCell" width="200">
            <input type="password" name="textfield5" value ="<%=addtlLiteYukonUser.getPassword()%>">
          </td>
        </tr>
      </table>
    </td>
    <td valign="top">
      <table width="300" border="0" cellspacing="0" cellpadding="2">
       <%
         java.util.Vector addtlNotifications = liteAddtlCont.getLiteContactNotifications();
         for (int j = 0; j < addtlNotifications.size(); j++) {
           com.cannontech.database.data.lite.LiteContactNotification liteNot = (com.cannontech.database.data.lite.LiteContactNotification)addtlNotifications.get(j);
         %>
        <tr>
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
        </tr>
       <%}
         for (int j = addtlNotifications.size(); j < 5; j++){
         %>
        <tr>
          <td width="150" class="TableCell" align = "right">
          <select name="program">
          <%
			for (int i = 0; i < entryIDs.length; i++)
			{
				out.println("<OPTION VALUE=" + entryIDs[i] + ">" + entryTexts[i]);					
			}%>
		  </select>          
		  </td>
          <td width="150" align="left" class="TableCell">
            <input type="text" name="textfield3">
          </td>
        </tr>
       <%}%>
       </table>
    </td>
  </tr>
</table>
       <table>
        <tr>
          <td class="TableCell" align="right" height="40"> 
            <input type="submit" name="Delete" value="Delete">
          </td>
          <td class="TableCell" align="left" height="40">
            <input type="submit" name="Add" value="Update">
          </td>
        </tr>
      </table>
