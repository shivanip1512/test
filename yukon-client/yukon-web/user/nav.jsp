<table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
  <tr> 
    <td bgcolor="#FFFFFF"> 
      <div align="left"> <span class="NavText">Account #12345<br>
        First/Last Name<br>
        800-555-1212<br>
        800-555-2121</span><br>
      </div>
      <div align="left"> </div>
    </td>
  </tr>
  <tr> 
    <td height="30" valign="bottom"> 
      <div align="left"><span class="NavHeader">Trending</span><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_trending.jsp?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + page + "&model=0" %>" class="Link2"><span class="NavText">Line Graph</span></a></div><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_trending.jsp?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + page + "&model=1" %>" class="Link2"><span class="NavText">Bar Graph</span></a></div>
 	<img src="Bullet2.gif" width="12" height="12"><a href="/user/user_trending.jsp?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + page + "&model=2" %>" class="Link2"><span class="NavText">Load Duration</span></a></div>
    </td>
  </tr>
  <tr> 
    <td height="10"> 
      <div align="left"></div>
    </td>
  </tr>
  <tr> 
    <td> </td>
  </tr>
  <tr> 
    <td height="20"> 
      <div align="left"><span class="NavHeader">Buyback</span><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_ee.jsp" class="Link2"><span class="NavText">VBB Offers</span></a><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_curtail.jsp" class="Link2"><span class="NavText">Curtail Notification</span></a></div> 
    </td>
  </tr>
  <tr> 
    <td height="10"> 
      <div align="left"></div>
    </td>
  </tr>
  <tr> 
    <td> 
      <div align="left"><span class="NavHeader">User-Control</span><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_lm_control.jsp" class="Link2"><span class="NavText">Auto Control</span></a><br>
        <img src="Bullet2.gif" width="12" height="12"><a href="/user/user_lm_time.jsp" class="Link2"><span class="NavText">Time-Based</span></a></div>
    </td>
  </tr>
  
</table>