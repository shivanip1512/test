<%
	//since things like XmlHTTP do not allow URL calls to pages
	// or servlets outside of its current location (with the exception
	// of an absolute URL), we use this proxy to redirect us to where
	// we need to go.

	String q = "";
	if( request.getQueryString() != null )
		q = "?" + request.getQueryString();

	response.sendRedirect(
		response.encodeRedirectURL(
		"/servlet/CBCServlet" + q) );

%>
