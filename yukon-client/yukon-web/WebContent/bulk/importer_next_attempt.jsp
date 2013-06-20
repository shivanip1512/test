<%@ taglib uri="../WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="webImpMain" scope="session"
	class="com.cannontech.yimp.util.ImportWebInfoObject"
/>	
<table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr> 
    	<td width="102" height="102" background="images/importpic.jpg">&nbsp;</td>
		<td valign="bottom" height="102"> 
        	<table width="657" cellspacing="0"  cellpadding="0" border="0">
          		<tr>
					<td colspan="3" height="74" background="images/Header.gif">&nbsp;</td>
 				</tr>
			</table> 				
 			<table width="657" cellspacing="0"  cellpadding="0" border="0">	
          		<tr bgcolor="#0066CC"> 
		            <td height = "28" class="Header3" width="420">&nbsp;&nbsp;<font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
		            	<font color="#f8f8f8" size="2" face="Arial, Helvetica, sans-serif" class="HeaderCellCrucial">Next import attempt: <%= webImpMain.getNextImportTime() %>. </font>
		      			</em></font></td>
		          	<td valign="middle" width="120">&nbsp;</td>
		            <td width="" valign="middle" nowrap> 
						<span class="MainText"><a href="<%=request.getContextPath()%>/dashboard" class="Link3">Home</a></span>
              		</td>
		            <td width="" valign="middle" nowrap> 
            			<span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span>
              		</td>
          		</tr>
        	</table>
      	</td>
	</tr>
</table>
