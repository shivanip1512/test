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
        <p align="center"><img src="demos/Header.gif" width="300" height="34"></p>
        <p align="center"><font face="Arial, Helvetica, sans-serif" size="2">Yukon 
          provides a variety of Web enabled services. Choose any of the options 
          below to see the services offered.<br>
          </font></p>
        
        <hr>
      </td>
    </tr>
  </table>
  <table width="740" border="0" cellspacing="0" cellpadding="10">
    <tr> 
      <td width="350" valign="top"> 
        <p align="center"><a href="UserDemos/ConsumerSwitch/login.jsp"><img src="demos/CESswitch.gif" width="346" height="24" border="0"></a></p>
        <table width="290" border="0" cellspacing="0" cellpadding="0" align="center">
          <tr valign="top"> 
            <td width="160"> 
              <p><font face="Arial, Helvetica, sans-serif" size="1">Residential 
                and small commercial users can get Web access to load management 
                options.</font></p>
              <p>&nbsp;</p>
            </td>
            <td width="130"> 
              <p align="right"><a href="UserDemos/ConsumerSwitch/login.jsp"><img src="demos/CESswitchImage.gif" width="126" height="110" border="0"></a></p>
            </td>
          </tr>
        </table>
      </td>
      <td width="350" valign="top"> 
        <p align="center"><a href="UserDemos/ConsumerStat/login.jsp"><img src="demos/CESstat.gif" width="327" height="24" border="0"></a></p>
        <table width="290" border="0" cellspacing="0" cellpadding="0" align="center">
          <tr valign="top"> 
            <td width="130"> 
              <p align="left"><a href="UserDemos/ConsumerStat/login.jsp"><img src="demos/CESstatImage.gif" width="126" height="110" border="0"></a></p>
            </td>
            <td width="160"> 
              <p><font face="Arial, Helvetica, sans-serif" size="1">Residential 
                and small commercial users can get Web access to controllable 
                thermostats.</font></p>
              <p>&nbsp;</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr> 
      <td width="350" valign="top"> 
        <FORM METHOD="POST" ACTION="servlet/LoginController">
          <p align="center"> <input type="image" src="demos/CESCI.gif" width="235" height="24" border="0"></p>
          <table width="290" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr valign="top"> 
              <td width="160"> 
                <p><font face="Arial, Helvetica, sans-serif" size="1">For the 
                  commercial or industrial energy users, our Web energy information 
                  and control system can save energy dollars. </font></p>
                <p>&nbsp;</p>
              </td>
              <td width="130"> 
                <p align="right"> <input type="image" src="demos/CESCIImage.gif" width="126" height="110" border="0"></p>
              </td>
            </tr>
          </table>
          <input name="USERNAME" value="cti2" type="hidden">
          <input name="PASSWORD" value="cti2" type="hidden">
          <input name="ACTION" value="LOGIN" type="hidden">
          <input name="DATABASEALIAS" value="yukon" type="hidden">
          <input name="REDIRECT" type="hidden" value="/UserDemos/CILC/user_trending.jsp">
        </form>
      </td>
      <td width="350" valign="top"> 
        <FORM METHOD="POST" ACTION="/servlet/SOAPClient">
		  <input type="hidden" name="action" value="OperatorLogin">
          <p align="center"> <input type="image" src="demos/LMOC.gif" width="162" height="26" border="0"></p>
          <table width="290" border="0" cellspacing="0" cellpadding="0" align="center">
            <tr valign="top"> 
              <td width="130"> 
                <p align="left"> <input type="image" src="demos/LMOCImage.gif" width="126" height="110" border="0"></p>
              </td>
              <td width="160"> 
                <p><font face="Arial, Helvetica, sans-serif" size="1">Utility 
                  operators can use Yukon's Web services for dispatching demand 
                  response programs like load management, curtailment, and customer 
                  owned generation.</font></p>
                <p>&nbsp;</p>
              </td>
            </tr>
          </table>
          <input name="USERNAME" value="op1" type="hidden">
          <input name="PASSWORD" value="op1" type="hidden">
          <input name="ACTION" value="LOGIN" type="hidden">
          <input name="DATABASEALIAS" value="yukon" type="hidden">
		  <input name="REDIRECT" type="hidden" value="/OperatorDemos/Operations.jsp">
        </form>
      </td>
    </tr>
  </table>
  <form method ="POST" action="http://www.esubstation.com/servlet/LoginController">
    <p align="center"> <input type="image" src="demos/esub.gif" width="169" height="11" border="0"><br>
      <font face="Arial, Helvetica, sans-serif" size="1">Utility engineers can 
      use Yukon's Web services for substation information.</font> </p>
    <table width="290" border="0" cellspacing="0" cellpadding="0" align="center">
      <tr valign="top"> 
       <td> 
          <p align="center"> 
            <input type="image" src="demos/ES.gif" width="119" height="82" border="0" name="image">
          </p>
        </td>
      </tr>
    </table>
    <input name="USERNAME" value="marketing" type="hidden">
    <input name="PASSWORD" value="cti" type="hidden">
    <input name="ACTION" type="hidden" value = "LOGIN">
    <input name="DATABASEALIAS" type="hidden" value="yukon">
    <input name="SERVICE" type="hidden" value="ESUBSTATION">
  </form>
 
  <p>&nbsp;</p>
  </CENTER>

</BODY>
</HTML>
