package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * EmbedSVGJSServlet exists to overcome an Internet Explorer limitation introduced by a windows update patch sometime in q1 of 2006.
 * After the patch, Active X controls directly embedded in a page require a user action to become active.  The result is that, for example,
 * it now takes two clicks to follow a link in an SVG or flash page.  One to make the control active and another to actually follow the link.
 * 
 * This msdn link has some information about the problem:
 * http://msdn.microsoft.com/library/default.asp?url=/workshop/author/dhtml/overview/activating_activex.asp
 * 
 * This servlet generates javascript that writes the embed control tag into an html document and has the effect of activating
 * the control.
 * 
 * Required parameters -
 * svgfile - the name of the svg file that the control should display
 * width - the width of the control on the page
 * height - the height of the control on the page
 * 
 * 
 * @author alauinger
 * @see com.cannontech.esub.util.HtmlGenerator
 * @see web.xml 
 */
public class EmbedSVGJSServlet extends HttpServlet {

	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String svgFile = req.getParameter("svgfile");
		int width = Integer.parseInt(req.getParameter("width"));
		int height = Integer.parseInt(req.getParameter("height"));
		
		Writer w = resp.getWriter();
		String html = "document.write('<embed src=\"" + svgFile + "\" name=\"SVGEmbed\" width=\"" + width + "\" height=\"" + height + "\" type=\"image/svg-xml\" pluginspage=\"http://www.adobe.com/svg/viewer/install\" wmode=\"transparent\" />')";
        w.write(html);
		
	}

}
