package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.jasper.runtime.*;


public class GraphSettings$jsp extends HttpJspBase {


    static {
    }
    public GraphSettings$jsp( ) {
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

            // begin [file="/jsp/GraphSettings.jsp";from=(0,2);to=(36,0)]
                
                    int view = Integer.parseInt(request.getParameter("view"));
                    String period = request.getParameter("period");
                    String start = request.getParameter("start");
                   
                    if(period.equalsIgnoreCase("Today")) {
                        period = "1 Day";
                    }
                   // else 
                   // if(period.equalsIgnoreCase("Yesterday")) {
                   //     period = "Prev 1 Day";
                   // }
                
                    String[] periodOption = { 
                        "1 Day",
                        "3 Days",
                        "5 Days",
                        "1 Week",
                        "1 Month",        
                        "Yesterday",
                        "Prev 2 Days",
                        "Prev 3 Days",
                        "Prev 5 Days",
                        "Prev 7 Days"
                    };
                
                    // make the current period  the first one
                    for(int i = 0; i < periodOption.length; i++) {
                        if(periodOption[i].equalsIgnoreCase(period)) {
                            String temp = periodOption[0];
                            periodOption[0] = period;
                            periodOption[i] = temp;
                            break;
                        }       
                    }
                    
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(36,2);to=(63,61)]
                out.write("\r\n\r\n<html>\r\n<head>\r\n<title>Graph Settings</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">\r\n<script langauge = \"Javascript\" src = \"updateGraph.js\"></script>\r\n<script langauge = \"Javascript\" src = \"refresh.js\"></script>\r\n<script  LANGUAGE=\"JavaScript1.2\" SRC=\"Calendar1-82.js\"></script>\r\n</head>\r\n<body bgcolor=\"#000000\" text=\"#000000\"><form name = \"MForm\">    \r\n  <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"9\" >\r\n    <tr>\r\n    <td>\r\n      <table width=\"100%\" border=\"2\" cellspacing=\"0\" cellpadding=\"3\" bgcolor = \"#FFFFFF\">\r\n        <tr> \r\n          <td height=\"2\" width=\"50%\" valign = \"top\" bgcolor = \"#CCCCCC\"><b><font size=\"2\" face=\"Arial, Helvetica, sans-serif\">Graph \r\n            Settings</font></b></td>\r\n        </tr>\r\n        <tr>\r\n        \t<td>\r\n        \t\t<table>\r\n                    <tr>\r\n        \t\t\t\t<td>\r\n        \t\t\t\t\t<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Start Date:</font>\r\n        \t\t\t\t</td>\r\n        \t\t\t\t<td>\r\n        \t\t\t\t\t<input type=\"text\" name=\"start\" size=\"9\" value=\"");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(63,64);to=(63,71)]
                out.print( start );
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(63,73);to=(80,32)]
                out.write("\" >\r\n        \t\t\t\t\t<!--<a HREF=\"javascript:show_calendar('MForm.startDate')\"\r\n                            \tonMouseOver=\"window.status='Pop Calendar';return true;\"\r\n                                onMouseOut=\"window.status='';return true;\"><img SRC=\"StartCalendar.gif\" WIDTH=\"20\" HEIGHT=\"15\" ALIGN=\"ABSMIDDLE\" BORDER=\"0\">\r\n                            </a>-->\r\n        \t\t\t\t</td>\r\n        \t\t\t\t<td>\r\n        \t\t\t\t\t<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">(mm/dd/yy)</font>\r\n        \t\t\t\t</td>\r\n        \t\t\t</tr>\r\n\r\n        \t\t    <tr>\r\n        \t\t    \t<td>\r\n        \t\t    \t\t<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Graph Period:</font>\r\n        \t\t    \t</td>\r\n        \t\t    \t<td>\r\n        \t\t    \t\t<select name=\"period\">\r\n                                ");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(80,34);to=(84,32)]
                
                                                    for(int i = 0; i < periodOption.length; i++) {
                                                        out.println("<option value=\"" + periodOption[i] + "\"" + ">" + periodOption[i]);
                                                    }
                                                
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(84,34);to=(94,50)]
                out.write("\r\n        \t\t    \t    </select>\r\n        \t\t    \t</td>\r\n        \t\t    </tr>\r\n                    <tr>\r\n                        <td>\r\n                            <font face=\"Arial, Helvetica, sans-serif\" size=\"2\">Graph Type:</font>\r\n                        </td>\r\n                        <td>\r\n                            <select name=\"view\">\r\n                                <option value=\"0\" ");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(94,52);to=(94,93)]
                 if(view==0) { out.print(" selected"); } 
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(94,95);to=(95,50)]
                out.write(">Line\r\n                                <option value=\"3\" ");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(95,52);to=(95,93)]
                 if(view==3) { out.print(" selected"); } 
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(95,95);to=(96,50)]
                out.write(">Step Line\r\n                                <option value=\"1\" ");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(96,52);to=(96,93)]
                 if(view==1) { out.print(" selected"); } 
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(96,95);to=(97,50)]
                out.write(">Bar\r\n                                <option value=\"2\" ");

            // end
            // begin [file="/jsp/GraphSettings.jsp";from=(97,52);to=(97,93)]
                 if(view==2) { out.print(" selected"); } 
            // end
            // HTML // begin [file="/jsp/GraphSettings.jsp";from=(97,95);to=(116,0)]
                out.write(">3D Bar\r\n                            </select>\r\n                        </td>\r\n                    </tr>\r\n        \t    </table>\r\n        \t</td>\r\n        </tr>        \r\n      </table>\r\n    </td>\r\n  </tr>\r\n  <tr>\r\n    <td align = \"center\" valign = \"bottom\">\r\n      <input type=\"submit\" name=\"Submit2\" value=\"Update Graph\" onclick = \"update()\">\r\n      <input type=\"submit\" name=\"Submit\" value=\"Cancel\" onclick = \"Javascript:window.close()\">\r\n    </td>\r\n  </tr>\r\n</table></form>\r\n</body>\r\n</html>\r\n");

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
