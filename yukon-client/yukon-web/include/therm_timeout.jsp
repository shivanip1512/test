                <table width="80%" border="1" cellspacing = "0" cellpadding = "2">
                <tr> 
                    <td align = "center"> 
                      
                    <table width="478" border="0">
                      <tr> 
                        <td class = "MainText" width="100%" ><span class="ErrorMsg">Your 
                          thermostat gateway has not reported back for more than 
                          <%= ServletUtils.GATEWAY_TIMEOUT_HOURS %> hours. The 
                          last thermostat settings received from the gateway may 
                          not be current, and you probably will not be able to 
                          send settings to gateway successfully.</span><br>
                          <br>
                          Please check the LEDs on your gateway to make sure it's 
                          working properly. If you find any problem, please contact 
                          your utility company for support.<br>
                          <br>
                          If you want to view the thermostat settings anyway, 
                          please <a href="<%= session.getAttribute(ServletUtils.ATT_REFERRER) %>&OmitTimeout=true">click 
                          here</a>.</td>
                      </tr>
                    </table>
                    </td>
                </tr>
              </table>
              <br>
			  <input type="button" name="Back" value="Back" onclick="history.back()">
