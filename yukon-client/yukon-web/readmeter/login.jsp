<%	//rid of any session that may be already running.  We do not want to have someone else's data.
String loggingOut = request.getParameter("logout");
if( loggingOut != null && loggingOut.equalsIgnoreCase("true"))
{
	session.invalidate();
}
%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%
      //Check whether this is a second attempt
      String lastAttemptFailed = request.getParameter("failed");
%>
<html>
<head>
<title>readmeter.com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">


<link rel="stylesheet" href="RMBGStyle.css" type="text/css">
</head>

<body bgcolor="#666699" text="#000000" leftmargin="0" topmargin="0" link="#FFFFFF" vlink="#FFFFFF" alink="#FFFFFF">
<table width=100% border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td bgcolor="#666699" height="80"> 
      
        <table width="100%" height="75" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            
          <td width="34%" valign="top"><font color="#FFFFFF" size="-2" face="Arial, Helvetica, sans-serif">Cannon 
            Technologies, Inc.<br clear="ALL">
            8301 Golden Valley Road, Suite 300<br>
            Golden Valley, MN 55427<br>
            800-827-7966<br>
            <a href="http://www.cannontech.com">www.cannontech.com </a><br>
            </font></td>
            <td width="32%" valign="bottom"> 
              <div align="center"><img src="RMHeader.gif" width="313" height="30"></div>
            </td>
            <td width="34%" valign="top"> 
              <div align="right"><img src="../YukonLogo.gif" width="132" height="28"></div>
            </td>
          </tr>
        </table>
        
    </td>
  </tr>
  <tr> 
    <td bgcolor="#000000" height="1"></td>
  </tr>
  <tr> 
    <td class="BGNoRepeat" height="80">&nbsp;</td>
  </tr>
  <tr> 
    <td bgcolor="#000000" height="1"></td>
  </tr>
  <tr> 
    <td bgcolor="#FFFFFF">
      <table width="760" border="0" cellspacing="0" cellpadding="20" align="center">
        <tr>
          <td valign="top" width="350"> 
            <p><font face="Arial, Helvetica, sans-serif" size="2"><b>Energy Information, 
              Curtailment, and Control<br>
              &#133; for your most important customers</b></font></p>
            <p><font face="Arial, Helvetica, sans-serif" size="2">Readmeter.com 
              makes it easy for a utility to offer advanced energy services quickly, 
              with a very low investment. The system collects data directly from 
              revenue meters, and provides sophisticated direct and indirect control 
              of customer-side loads. Cannon offers the experience that comes 
              from serving nearly 300 utilities, and 12 years of delivering energy 
              management systems.</font></p>
            <p><font face="Arial, Helvetica, sans-serif" size="2">Sign in below 
              to view our application.</font><br>
              <font size="1">(<font color="#000000" face="Arial, Helvetica, sans-serif">Best 
              viewed by Internet Explorer 5.0 or greater, or Netscape Navigator 
              6.0 or greater)</font></font><br>
            </p>

            <p><table width="250" border="0" cellspacing="0" cellpadding="5"
    height="89">
 
	<!---Failed message -->
	          <%                                        
   				if( lastAttemptFailed != null )
  				 {
				%>

  					<div align="center">
    					<font FACE="Arial" size="2" color=Red>
       							 Login Failed
    					</font>
    				</div>   
			<%
			   }
				
			%>
       
	<!--End Failed message   -->
		
	      <tr>
          <FORM METHOD="POST" ACTION="/scripts/jrun.dll/servlet/LoginController"> 
            <td width="40%" height="20"> 
              <p align=RIGHT>&nbsp;<font size="-1" face="Arial">User Name:</font>
            </td>
            <td width="60%" height="20"> <font size="-1" face="Arial">
              <input name="USERNAME" type="text" 
        size="20">
              </font> </td>
          </tr>
          <tr> 
            <td width="40%" height="20"> 
              <p align=RIGHT>&nbsp;<font size="-1" face="Arial">Password:</font>
            </td>
            <td width="60%" height="20"> <font size="-1" face="Arial">
              <input name="PASSWORD" type="password" 
        size="20">
              </font> </td>
          </tr>
          <tr> 
            <td width="40%" height="20">&nbsp; </td>
            <td width="60%" height="20"> <input type="image" src="SubmitButton.gif" width="58" height="20" border="0"></td>
            <INPUT NAME="ACTION" TYPE="hidden" VALUE = "LOGIN">
            <INPUT NAME="DATABASEALIAS" TYPE="hidden" VALUE="yukon">
           </form> 
          </tr>
        </table>
            <div align="center"><br>
              <font face="Arial, Helvetica, sans-serif" size="2">To obtain a password, 
              or if you have forgotten your password, contact:<br>
              <a href="mailto:info@cannontech.com"><img src="Contact.gif" width="128" height="20" border="0"></a> 
              </font></div>
          </td>
          <td width="410" valign="top"> 
            <p><font face="Arial, Helvetica, sans-serif" size="2"><b>About readmeter.com&#133;</b></font></p>
            <ul>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Fast, easy 
                implementation of energy services with a small investment</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Supports 
                popular digital meters</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Supports 
                the GE-Harris RTU series for high-end applications</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Supports 
                popular automation protocols through the GE-Harris gateway</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Supports 
                voluntary and mandatory curtailment options</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Supports 
                a variety of communication links</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Allows customers 
                direct control of their own loads</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Ideal for 
                genset monitoring &amp; control</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Produces 
                billing-ready files</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Download 
                data</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Own the Master 
                Station at any time (subscribe now, own later)</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Perfect for 
                trials</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Uses LCR 
                5000 load control receiver for nationwide broadcast control (using 
                900 MHz paging)</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">E-mail alarms 
                for event notification</font></li>
            </ul>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr> 
    <td  bgcolor="#000000" height="1"></td>
  </tr>
</table>
<div align="center">
  <p><FONT COLOR="#ffffff">
    <% int crStartYear = 2002; %><%@ include file="../include/copyright.jsp" %>
  </FONT></p>
</div>
</body>

</html>
