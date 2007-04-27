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
          		<tr bgcolor="#0066CC"> 
		            <td height = "28" class="Header3" width="420">&nbsp;&nbsp;<font color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><em>&nbsp;
		            	<font color="#f8f8f8" size="2" face="Arial, Helvetica, sans-serif" class="HeaderCellCrucial">Next import attempt: <%= webImpMain.getNextImportTime() %>. </font>
		      			</em></font></td>
		          	<td valign="middle" width="180">&nbsp;</td>
		            <td width="57" valign="middle"> 
            			<div align="left"><span ><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">
              				<font color="#ffffff" size="2" face="Arial, Helvetica, sans-serif">
              	 			</font></a></span></div>
              		</td>
          		</tr>
        	</table>
      	</td>
	</tr>
</table>
