<td>
<div align="left"><span class="NavHeader">Trends</span><br>
	<%   /* Retrieve all the predefined graphs for this user*/                       
	if( gData != null )
	{%>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
		<%
		for( int i = 0; i < gData.length; i++ )                                                          
		{
			if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
			{%>
          <tr> 
            <td width="10"><img src="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="9" height="9"></td>
            <td style="padding:1"><span class="Nav"><%=gData[i][1] %></span></td>
          </tr>
			<%}
			else 
			{%>
          <tr> 
            <td width="10">&nbsp;</td>
            <td style="padding:1"><a href="<%=request.getContextPath()%>/operator/Metering/Metering.jsp?gdefid=<%=gData[i][0]%>" class="Link2"><span class="NavText"><%=gData[i][1] %></span></a></td>
          </tr>
			<%}
		}%>
        </table>
	<%}%>
</div>
</td>