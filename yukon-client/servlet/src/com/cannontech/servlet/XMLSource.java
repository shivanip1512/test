package com.cannontech.servlet;

import javax.servlet.http.*;
/**
 * Creation date: (1/7/2002 1:01:45 PM)
 * @author:  Aaron Lauinger 
 */
public class XMLSource extends HttpServlet {
/**
 * XMLSource constructor comment.
 */
public XMLSource() {
	super();
}
/**
 * service method comment.
 */
public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
	javax.servlet.http.HttpSession session = ((HttpServletRequest)req).getSession(false);
	
	com.cannontech.database.data.web.User user = 
		(com.cannontech.database.data.web.User) session.getValue("USER");
	
	try {			
		org.exolab.castor.xml.Marshaller.marshal(user, resp.getWriter());
	} 
	catch(org.exolab.castor.xml.ValidationException ve) {
		ve.printStackTrace();
	}
	catch(org.exolab.castor.xml.MarshalException me) {
		me.printStackTrace();
	}
}
}
