package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.jasper.runtime.*;


public class sublist$jsp extends HttpJspBase {


    static {
    }
    public sublist$jsp( ) {
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

            // HTML // begin [file="/esub-demo/sublist.jsp";from=(0,0);to=(41,0)]
                out.write("<HTML>\r\n\r\n\r\n<HEAD>\r\n  <META NAME=\"GENERATOR\" CONTENT=\"Adobe PageMill 3.0 Win\">\r\n  <META NAME=\"robots\" CONTENT=\"noindex, nofollow\">\r\n  <TITLE>ESubstation.com</TITLE>\r\n</HEAD>\r\n<BODY BGCOLOR=\"#000000\" LINK=\"#000000\" ALINK=\"#000000\" VLINK=\"#000000\">\r\n\r\n<P><CENTER><!--SELECTION--><!--/SELECTION--><IMG SRC=\"images/esublogo.gif\"\r\nWIDTH=\"281\" HEIGHT=\"88\" ALIGN=\"BOTTOM\" BORDER=\"0\" NATURALSIZEFLAG=\"0\"\r\nALT=\"ESubstation Logo\"></CENTER></P>\r\n\r\n<P><CENTER><FONT COLOR=\"#cccc99\" SIZE=\"-1\" FACE=\"Arial\">Click\r\non one of the following substations to display the OneLine.</FONT></CENTER></P>\r\n\r\n<P><CENTER><TABLE WIDTH=\"600\" BORDER=\"4\" CELLSPACING=\"0\" CELLPADDING=\"0\">\r\n  <TR>\r\n    <TD WIDTH=\"100%\" BGCOLOR=\"#ffffff\">\r\n    <P><CENTER><TABLE WIDTH=\"600\" BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"8\">\r\n      <TR>                     \r\n\r\n        <TD WIDTH=\"33%\" VALIGN=\"TOP\" BGCOLOR=\"#cccc99\">\r\n        <FONT SIZE=\"+2\" FACE=\"Arial\">West District</FONT>\r\n        <A HREF=\"morningside.svg\" ><FONT SIZE=\"-1\" FACE=\"Arial, Helvetica\">Morningside</FONT></A>\r\n        </TD> \r\n\r\n        <TD WIDTH=\"33%\" VALIGN=\"TOP\" BGCOLOR=\"#cccc99\">\r\n        <FONT SIZE=\"+2\" FACE=\"Arial\">Central District</FONT></TD>\r\n \r\n        <TD WIDTH=\"34%\" VALIGN=\"TOP\" BGCOLOR=\"#cccc99\">\r\n        <P><FONT SIZE=\"+2\" FACE=\"Arial\">East District</FONT></TD>\r\n\r\n      </TR>\r\n    </TABLE></CENTER></TD>\r\n  </TR>\r\n</TABLE></CENTER>\r\n\r\n</BODY>\r\n</HTML>\r\n");

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
