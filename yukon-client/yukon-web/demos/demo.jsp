<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<HTML>
<HEAD>
  <META NAME="GENERATOR" CONTENT="Adobe PageMill 3.0 Win">
  <TITLE>Cannon Demos</TITLE>
  <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
  <META NAME="description" CONTENT="Cannon Technologies energy services include load management, demand management, energy management, load control, direct control, demand control, demand bidding, energy exchange, curtailment, AMR, aggregation, automated metering, internet metering, remote metering, meter reading, meter data collection, realtime pricing, VERSACOM, EMETCON, Substation Advisor, substation automation, substation monitoring, GE HARRIS, capacitor bank control, capacitor control, distribution line carrier, distribution automation, power line carrier, energy analysis, power quality, SCADA, off-peak energy, generation dispatch, genset monitoring, dual fuel, electric thermal storage, thermostat, LCR, load control receiver, KVAR management">
  <META NAME="keywords" CONTENT="Cannon Technologies, energy services, load management, demand management, energy management, load control, direct control, demand control, demand bidding, energy exchange, curtailment, AMR, aggregation, automated metering, internet metering, remote metering, meter reading, meter data collection, realtime pricing, VERSACOM, EMETCON, Substation Advisor, substation automation, substation monitoring, GE HARRIS, capacitor bank control, capacitor control, CBC, distribution line carrier, distribution automation, power line carrier, energy analysis, power quality, SCADA, off-peak energy, generation dispatch, genset monitoring, dual fuel, electric thermal storage, thermostat, LCR, load control receiver, KVAR management">
  <TITLE>Cannon Tour</TITLE>
</HEAD>
<BODY BGCOLOR="#ffffff" LINK="#000000" VLINK="#000000" ALINK="#000000">
<CENTER>
  <table width="740" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td>
        <p align="center"><img src="Header.gif" width="300" height="34"></p>
        <p align="center"><font face="Arial, Helvetica, sans-serif" size="2">Yukon 
          provides a variety of Web enabled services. Choose any of the options 
          below to see the services offered.
          </font><hr><br>
      </td>
    </tr>
  </table>
  <table width="540" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="33" valign="top"> 
        <table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="22">&nbsp;</td>
          </tr>
          <tr>
            <td><img src="LRC.gif"></td>
          </tr>
        </table>
        
      </td>
      <td width="707">
        <table width="500" border="0" cellspacing="0" cellpadding="5">
          <tr>
            <td>
              <table width="450" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td width="50%"> 
				  <FORM METHOD="POST" ACTION="<%= request.getContextPath() %>/servlet/LoginController">
                    <div align="center"><input type="image" src="ESOCGraphic.gif" width="126" height="150"></div>
					<input name="USERNAME" value="op1" type="hidden">
          			<input name="PASSWORD" value="op1" type="hidden">
          			<input name="ACTION" value="LOGIN" type="hidden">
					<input name="SAVE_CURRENT_USER" value="true" type="hidden">
        		  </form>
                  </td>
                  <td width="50%">
				  <FORM METHOD="POST" ACTION="<%= request.getContextPath() %>/servlet/LoginController"> 
                    <div align="center"><input type="image" src="CESGraphic.gif" width="152" height="152"></div>
                  	<input name="USERNAME" value="cust1" type="hidden">
          			<input name="PASSWORD" value="cust1" type="hidden">
          			<input name="ACTION" value="LOGIN" type="hidden">
					<input name="SAVE_CURRENT_USER" value="true" type="hidden">
                    </form>
				  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td>
              <div align="center">
                <hr>
                <img src="CESHeader.gif" width="173" height="26"></div>
            </td>
          </tr>
          <tr>
            <td>
              <table width="500" border="0" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td width="33%"> 
				  <FORM METHOD="POST" ACTION="<%= request.getContextPath() %>/servlet/LoginController">
                    <div align="center"><input type="image" src="Therm.gif" width="125" height="137"></div>
					<input name="USERNAME" value="thermostat" type="hidden">
          			<input name="PASSWORD" value="test" type="hidden">
          			<input name="ACTION" value="LOGIN" type="hidden">
					<input name="SAVE_CURRENT_USER" value="true" type="hidden">
        		  </form>
                  </td>
                  <td width="33%">
				  <FORM METHOD="POST" ACTION="<%= request.getContextPath() %>/servlet/LoginController"> 
                    <div align="center"><input type="image" src="Switch.gif" width="125" height="137"></div>
                  	<input name="USERNAME" value="switch" type="hidden">
          			<input name="PASSWORD" value="test" type="hidden">
          			<input name="ACTION" value="LOGIN" type="hidden">
					<input name="SAVE_CURRENT_USER" value="true" type="hidden">
                    </form>
		</td>
                  <td width="33%">
				  <FORM METHOD="POST" ACTION="<%= request.getContextPath() %>/servlet/LoginController"> 
			<div align="center"><a href="http://www.expressgate.net"><img src="EG.gif" width="125" height="138" border="0"></a></div>
                                 
                    </form>
		</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td> 
              <hr>
              <table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="166">&nbsp;</td>
                  <td width="166">
				  <form method="POST" action="http://demo.esubstation.com/servlet/LoginController">
                      <div align="center"><br>
                        <input type="image" src="ESGraphic.gif" width="155" height="124" border="0" name="image3">
                        <input name="USERNAME" value="cti" type="hidden">
                        <input name="PASSWORD" value="cannon" type="hidden">
                        <input name="ACTION" type="hidden" value = "LOGIN">
                      </div>
                    </form></td>
                  <td width="168">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <p>
  <table width="740" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td align="center">
        <% int crStartYear = 2002; %><%@ include file="../include/full_copyright.jsp" %>
      </td>
    </tr>
  </table>
  </CENTER>

</BODY>
</HTML>
