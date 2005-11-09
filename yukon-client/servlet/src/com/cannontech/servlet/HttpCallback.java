package com.cannontech.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Takes an arbitrary url and issues at GET request.
 * Initially this is used to control a web cam where the browser refuses to send a request
 * to a server other than where the html served from.
 * @author aaron
 *
 */
public class HttpCallback extends HttpServlet {

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String urlStr = req.getParameter("url");
		URL url = new URL(URLDecoder.decode(urlStr));	
		URLConnection conn = url.openConnection();
		conn.connect();
		conn.getContent();
		
		resp.setDateHeader ("Expires", 0); 			//prevents caching at the proxy server		
		resp.getOutputStream().write("OK".getBytes());
	}
}
