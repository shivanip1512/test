<table width="150" border="0" cellspacing="0" cellpadding="6" height="50">
  <tr> 
    <td height="30" valign="bottom"> 
      <div align="left"><span class="NavHeader">Trending</span><br>
      <%   /* Retrieve all the predefined graphs for this user*/                       
      if( gData != null )
      {
        for( int i = 0; i < gData.length; i++ )                                                          
        {
          if( gData[i] != null )
          {
            if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid())
            {%>
              <img src="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET_SELECTED%>"/>" width="12" height="12"><span class="Nav"><%=gData[i][1] %></span><br>
            <%}
            else 
            {%>
              <img src="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.NAV_BULLET%>"/>" width="12" height="12"><a href="/readmeter/user_trending.jsp?<%= "gdefid=" + gData[i][0]%>"><span class="NavText"><%=gData[i][1] %></span></a><br>
            <%}
          }
        }
      }%></div>
    </td>
  </tr>
</table>