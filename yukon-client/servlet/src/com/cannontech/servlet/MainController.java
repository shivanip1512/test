package com.cannontech.servlet;

/**
 * Creation date: (4/9/2001 10:41:14 AM)
 * @author: Aaron Lauinger
 */
import com.cannontech.database.data.web.User;

public class MainController extends javax.servlet.http.HttpServlet {
/**
 * MainController constructor comment.
 */
public MainController() {
	super();
}
/**
 * service method comment.
 */
public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{

	javax.servlet.http.HttpSession session = ((javax.servlet.http.HttpServletRequest) req).getSession(false);
	User user = null;
	
	if (session == null || (user = (User) session.getValue("USER") ) == null )
	{
		javax.servlet.RequestDispatcher dispatcher =
		getServletContext().getRequestDispatcher("/login.jsp");
			
		dispatcher.forward(req,resp);
	}

	javax.servlet.RequestDispatcher dispatcher =
		getServletContext().getRequestDispatcher("main.jsp");
			
	dispatcher.include(req,resp);
	
}
}
