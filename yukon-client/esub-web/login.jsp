<%
      //Check whether this is a second attempt
      String lastAttemptFailed = request.getParameter("failed");
%>

<html>
<head>
<title>esubstation.com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">


<link rel="stylesheet" href="esubBGStyle.css" type="text/css">
</head>

<body bgcolor="#333333" text="#000000" leftmargin="0" topmargin="0" link="#FFFFFF" vlink="#FFFFFF" alink="#FFFFFF">
<table width=100% border="0" cellspacing="0" cellpadding="0" align="center">
  <tr> 
    <td bgcolor="#333333" height="80"> 
      
        <table width="100%" height="75" border="0" cellspacing="0" cellpadding="0">
          <tr> 
            
          <td width="34%" valign="top"><font color="#FFFFFF" size="-2" face="Arial, Helvetica, sans-serif">Cannon 
            Technologies, Inc.<br clear="ALL">
            8301 Golden Valley Road, Suite 300<br>
            Golden Valley, MN 55427<br>
            800-827-7966<br>
            <a href="http://www.cannontech.com">www.cannontech.com <br>
            </a></font><i><font color="#FFFFFF" size="-1" face="Times New Roman"><br clear="ALL">
              </font></i></td>
            <td width="32%" valign="bottom"> 
              <div align="center"><img src="esubHeader.gif" width="347" height="30"></div>
            </td>
            <td width="34%" valign="top"> 
              <div align="right"><img src="YukonLogoWhite.gif" width="132" height="28"></div>
              <font face="Arial, Helvetica, sans-serif" size="2"><A HREF="esub-demo/SVGView.exe">Click here to install Adobe SVG Plugin 3.0
            </A></FONT><BR>
            <BR>

            </td>
          </tr>
        </table>
        
    </td>
  </tr>
  <tr> 
    <td bgcolor="#000000" height="1"></td>
  </tr>
  <tr> 
    <td class="BGNoRepeat" height="49">&nbsp;</td>
  </tr>
  <tr> 
    <td bgcolor="#000000" height="1"></td>
  </tr>
  <tr> 
    <td bgcolor="#FFFFFF">
      <table width="760" border="0" cellspacing="0" cellpadding="20" align="center">
        <tr>
          <td width="407" valign="top"> 
            <p><font face="Arial, Helvetica, sans-serif" size="2">Esubstation.com 
              is a Web-based application that monitors one or more substations 
              using Cannon Substation <i>Advisor</i>&reg; applications, without 
              the need for a local substation computer. The Advisor applications 
              reside as a suite of algorithms on a Cannon Yukon server located 
              either at Cannon Technologies facilities, or on the customer's own 
              intranet.</font></p>
            <p><font face="Arial, Helvetica, sans-serif" size="2"> Substation 
              conditions are monitored with Cannon or third-party sensors. When 
              configurable limits are exceeded, alarm information is provided 
              via pager or e-mail message. Substation information is accessed 
              by a Web browser located on any computer with Internet or intranet 
              access.</font></p>
            <p><font face="Arial, Helvetica, sans-serif" size="2">The specifications 
              of the esubstation.com application are listed <a href="esubSpecificationGuide.pdf" class="BlackLink" target="_blank">here</a>.</font></p>
            <p><font face="Arial, Helvetica, sans-serif" size="2">Sign in below 
              to view our application.</font><br>
              <br></p>
            <p>
      <center>
        <table width="250" border="0" cellspacing="0" cellpadding="5"
    height="89">
	
		<%
 		  if( lastAttemptFailed != null )
		   {
		%>
 		 
   			 <div align="center">
    			<font FACE="Arial" color=Red size=2>
       			 Login Failed
    			</font>
			</div>
		<%
 		  }
		%>
          <tr>
          <FORM METHOD="POST" ACTION="esub-demo/sublist.jsp"> 
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
            <input name="LOGIN" type="hidden" value="true"/>
            </form>
          </tr>
        </table>
		<div align="center"><br>
                          <font face="Arial, Helvetica, sans-serif" size="2">To obtain a password, 
              or if you have forgotten your password, contact:<br>
                <a href="mailto:info@cannontech.com"><img src="Contact.gif" width="128" height="20" border="0"></a> 
                </font></div>
      </center>
			</td>
			
        
          <td width="273"><font face="Arial, Helvetica, sans-serif" size="2"><b>Why 
            esubstation.com? </b></font> 
            <p><font face="Arial, Helvetica, sans-serif" size="2">Intelligent 
              Monitoring</font></p>
            <ul>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Filters &amp; 
                interprets substation data</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Uses Cannon 
                Advisor sensors or 3rd party sensors</font></li>
            </ul>
            <p><font face="Arial, Helvetica, sans-serif" size="2">Intelligent 
              Notification</font></p>
            <ul>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Based on 
                user-defined limits/events</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Notification 
                via email and/or page</font></li>
            </ul>
            <p><font face="Arial, Helvetica, sans-serif" size="2">Web Browser 
              Access</font></p>
            <ul>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Operation 
                &amp; maintenance information</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Planning 
                information</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Engineering 
                information</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Real-time 
                data</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Historical 
                tools</font></li>
              <li><font face="Arial, Helvetica, sans-serif" size="2">Predictive 
                maintenance trends</font><font size="2"><br>
                </font></li>
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

<BLOCKQUOTE>
      <P><CENTER><FONT COLOR="#000000" SIZE="-1" FACE="Times New Roman, Helvetica"><BR
      CLEAR="ALL">
      </FONT><FONT COLOR="#ffffff" SIZE="-1" FACE="Times New Roman, Helvetica">&copy; 
      2002 Cannon Technologies, Inc. All rights reserved.<BR
      CLEAR="ALL">
      </FONT>
</CENTER></BLOCKQUOTE>
</body>

</html>
