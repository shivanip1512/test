package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.jasper.runtime.*;


public class login$jsp extends HttpJspBase {


    static {
    }
    public login$jsp( ) {
    }

    private static boolean _jspx_inited = false;

    public final void _jspx_init() throws org.apache.jasper.runtime.JspException {
    }

    public void _jspService(HttpServletRequest request, HttpServletResponse  response)
        throws java.io.IOException, ServletException {

        JspFactory _jspxFactory = null;
        PageContext pageContext = null;
        HttpSession session = null;
        ServletContext application = null;
        ServletConfig config = null;
        JspWriter out = null;
        Object page = this;
        String  _value = null;
        try {

            if (_jspx_inited == false) {
                synchronized (this) {
                    if (_jspx_inited == false) {
                        _jspx_init();
                        _jspx_inited = true;
                    }
                }
            }
            _jspxFactory = JspFactory.getDefaultFactory();
            response.setContentType("text/html;charset=ISO-8859-1");
            pageContext = _jspxFactory.getPageContext(this, request, response,
            			"", true, 8192, true);

            application = pageContext.getServletContext();
            config = pageContext.getServletConfig();
            session = pageContext.getSession();
            out = pageContext.getOut();

            // begin [file="/login.jsp";from=(0,2);to=(3,0)]
                
                      //Check whether this is a second attempt
                      String lastAttemptFailed = request.getParameter("failed");
            // end
            // HTML // begin [file="/login.jsp";from=(3,2);to=(82,2)]
                out.write("\r\n\r\n<html>\r\n<head>\r\n<title>esubstation.com</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n\r\n\r\n<link rel=\"stylesheet\" href=\"esubBGStyle.css\" type=\"text/css\">\r\n</head>\r\n\r\n<body bgcolor=\"#333333\" text=\"#000000\" leftmargin=\"0\" topmargin=\"0\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FFFFFF\">\r\n<table width=100% border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\r\n  <tr> \r\n    <td bgcolor=\"#333333\" height=\"80\"> \r\n      \r\n        <table width=\"100%\" height=\"75\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n          <tr> \r\n            \r\n          <td width=\"34%\" valign=\"top\"><font color=\"#FFFFFF\" size=\"-2\" face=\"Arial, Helvetica, sans-serif\">Cannon \r\n            Technologies, Inc.<br clear=\"ALL\">\r\n            8301 Golden Valley Road, Suite 300<br>\r\n            Golden Valley, MN 55427<br>\r\n            800-827-7966<br>\r\n            <a href=\"http://www.cannontech.com\">www.cannontech.com <br>\r\n            </a></font><i><font color=\"#FFFFFF\" size=\"-1\" face=\"Times New Roman\"><br clear=\"ALL\">\r\n              </font></i></td>\r\n            <td width=\"32%\" valign=\"bottom\"> \r\n              <div align=\"center\"><img src=\"esubHeader.gif\" width=\"347\" height=\"30\"></div>\r\n            </td>\r\n            <td width=\"34%\" valign=\"top\"> \r\n              <div align=\"right\"><img src=\"YukonLogoWhite.gif\" width=\"132\" height=\"28\"></div>\r\n              <font face=\"Arial, Helvetica, sans-serif\" size=\"2\"><A HREF=\"esub-demo/SVGView.exe\">Click here to install Adobe SVG Plugin 3.0\r\n            </A></FONT><BR>\r\n            <BR>\r\n\r\n            </td>\r\n          </tr>\r\n        </table>\r\n        \r\n    </td>\r\n  </tr>\r\n  <tr> \r\n    <td bgcolor=\"#000000\" height=\"1\"></td>\r\n  </tr>\r\n  <tr> \r\n    <td class=\"BGNoRepeat\" height=\"49\">&nbsp;</td>\r\n  </tr>\r\n  <tr> \r\n    <td bgcolor=\"#000000\" height=\"1\"></td>\r\n  </tr>\r\n  <tr> \r\n    <td bgcolor=\"#FFFFFF\">\r\n      <table width=\"760\" border=\"0\" cellspacing=\"0\" cellpadding=\"20\" align=\"center\">\r\n        <tr>\r\n          <td width=\"407\" valign=\"top\"> \r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Esubstation.com \r\n              is a Web-based application that monitors one or more substations \r\n              using Cannon Substation <i>Advisor</i>&reg; applications, without \r\n              the need for a local substation computer. The Advisor applications \r\n              reside as a suite of algorithms on a Cannon Yukon server located \r\n              either at Cannon Technologies facilities, or on the customer's own \r\n              intranet.</font></p>\r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\"> Substation \r\n              conditions are monitored with Cannon or third-party sensors. When \r\n              configurable limits are exceeded, alarm information is provided \r\n              via pager or e-mail message. Substation information is accessed \r\n              by a Web browser located on any computer with Internet or intranet \r\n              access.</font></p>\r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">The specifications \r\n              of the esubstation.com application are listed <a href=\"esubSpecificationGuide.pdf\" class=\"BlackLink\" target=\"_blank\">here</a>.</font></p>\r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Sign in below \r\n              to view our application.</font><br>\r\n              <br></p>\r\n            <p>\r\n      <center>\r\n        <table width=\"250\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\"\r\n    height=\"89\">\r\n\t\r\n\t\t");

            // end
            // begin [file="/login.jsp";from=(82,4);to=(85,2)]
                
                 		  if( lastAttemptFailed != null )
                		   {
                		
            // end
            // HTML // begin [file="/login.jsp";from=(85,4);to=(92,2)]
                out.write("\r\n \t\t \r\n   \t\t\t <div align=\"center\">\r\n    \t\t\t<font FACE=\"Arial\" color=Red size=2>\r\n       \t\t\t Login Failed\r\n    \t\t\t</font>\r\n\t\t\t</div>\r\n\t\t");

            // end
            // begin [file="/login.jsp";from=(92,4);to=(94,2)]
                
                 		  }
                		
            // end
            // HTML // begin [file="/login.jsp";from=(94,4);to=(186,0)]
                out.write("\r\n          <tr>\r\n          <FORM METHOD=\"POST\" ACTION=\"esub-demo2/sublist.jsp\"> \r\n            <td width=\"40%\" height=\"20\"> \r\n              <p align=RIGHT>&nbsp;<font size=\"-1\" face=\"Arial\">User Name:</font>\r\n            </td>\r\n            <td width=\"60%\" height=\"20\"> <font size=\"-1\" face=\"Arial\">\r\n              <input name=\"USERNAME\" type=\"text\" \r\n        size=\"20\">\r\n              </font> </td>\r\n          </tr>\r\n          <tr> \r\n            <td width=\"40%\" height=\"20\"> \r\n              <p align=RIGHT>&nbsp;<font size=\"-1\" face=\"Arial\">Password:</font>\r\n            </td>\r\n            <td width=\"60%\" height=\"20\"> <font size=\"-1\" face=\"Arial\">\r\n              <input name=\"PASSWORD\" type=\"password\" \r\n        size=\"20\">\r\n              </font> </td>\r\n          </tr>\r\n          <tr> \r\n            <td width=\"40%\" height=\"20\">&nbsp; </td>\r\n            <td width=\"60%\" height=\"20\"> <input type=\"image\" src=\"SubmitButton.gif\" width=\"58\" height=\"20\" border=\"0\"></td>\r\n            <input name=\"LOGIN\" type=\"hidden\" value=\"true\"/>\r\n            </form>\r\n          </tr>\r\n        </table>\r\n\t\t<div align=\"center\"><br>\r\n                          <font face=\"Arial, Helvetica, sans-serif\" size=\"2\">To obtain a password, \r\n              or if you have forgotten your password, contact:<br>\r\n                <a href=\"mailto:info@cannontech.com\"><img src=\"Contact.gif\" width=\"128\" height=\"20\" border=\"0\"></a> \r\n                </font></div>\r\n      </center>\r\n\t\t\t</td>\r\n\t\t\t\r\n        \r\n          <td width=\"273\"><font face=\"Arial, Helvetica, sans-serif\" size=\"2\"><b>Why \r\n            esubstation.com? </b></font> \r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Intelligent \r\n              Monitoring</font></p>\r\n            <ul>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Filters &amp; \r\n                interprets substation data</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Uses Cannon \r\n                Advisor sensors or 3rd party sensors</font></li>\r\n            </ul>\r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Intelligent \r\n              Notification</font></p>\r\n            <ul>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Based on \r\n                user-defined limits/events</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Notification \r\n                via email and/or page</font></li>\r\n            </ul>\r\n            <p><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Web Browser \r\n              Access</font></p>\r\n            <ul>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Operation \r\n                &amp; maintenance information</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Planning \r\n                information</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Engineering \r\n                information</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Real-time \r\n                data</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Historical \r\n                tools</font></li>\r\n              <li><font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Predictive \r\n                maintenance trends</font><font size=\"2\"><br>\r\n                </font></li>\r\n            </ul>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n  <tr> \r\n    <td  bgcolor=\"#000000\" height=\"1\"></td>\r\n  </tr>\r\n</table>\r\n\r\n<BLOCKQUOTE>\r\n      <P><CENTER><FONT COLOR=\"#000000\" SIZE=\"-1\" FACE=\"Times New Roman, Helvetica\"><BR\r\n      CLEAR=\"ALL\">\r\n      </FONT><FONT COLOR=\"#ffffff\" SIZE=\"-1\" FACE=\"Times New Roman, Helvetica\">&copy; \r\n      2002 Cannon Technologies, Inc. All rights reserved.<BR\r\n      CLEAR=\"ALL\">\r\n      </FONT>\r\n</CENTER></BLOCKQUOTE>\r\n</body>\r\n\r\n</html>\r\n");

            // end

        } catch (Throwable t) {
            if (out != null && out.getBufferSize() != 0)
                out.clearBuffer();
            if (pageContext != null) pageContext.handlePageException(t);
        } finally {
            if (_jspxFactory != null) _jspxFactory.releasePageContext(pageContext);
        }
    }
}
