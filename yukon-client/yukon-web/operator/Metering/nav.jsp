<td>
<div align="left"><span class="PageHeader">Trends</span><br>
	<%   /* Retrieve all the predefined graphs for this user*/                       
	if( gData != null )
	{
		for( int i = 0; i < gData.length; i++ )                                                          
		{
			if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
			{%>
				<img src="<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="12" height="12">&nbsp;<span class="Nav"><%=gData[i][1] %></span><br>
			<%}
			else 
			{%>
				<img src="<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12">&nbsp;<a href="<%=request.getContextPath()%>/operator/Metering/Metering.jsp?<%= "gdefid=" + gData[i][0]%>" class="Link2"><span class="NavText"><%=gData[i][1] %></span></a><br>
			<%}
		}
	}%>
</div>
</td>