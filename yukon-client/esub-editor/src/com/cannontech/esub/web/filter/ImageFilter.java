package com.cannontech.esub.web.filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonImage;

/**
 * Forwards all request for any file that matches this filter to
 * the /images directory
 * 
 * @author alauinger
 */
public class ImageFilter implements Filter {

	private FilterConfig config;
	
	/**
	 * @see javax.servlet.Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fc) throws ServletException {
		config = fc;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(
		ServletRequest req,
		ServletResponse resp,
		FilterChain chain)
		throws IOException, ServletException {
			
		ServletContext sc = config.getServletContext();
		
		HttpServletRequest hreq = (HttpServletRequest)req;
		HttpServletResponse hres = (HttpServletResponse)resp;
		 
		String uri = hreq.getRequestURI();
		String conPath = hreq.getContextPath();

		String imgPath= uri.replaceFirst(conPath, "");
		
		if( imgPath.startsWith("/images/") ) {		
			ensureImageExists(config.getServletContext().getRealPath(imgPath));
			chain.doFilter(req,resp);		
		}
		else {			
			imgPath = "/images" + imgPath.substring(imgPath.lastIndexOf("/"));
			ensureImageExists(config.getServletContext().getRealPath(imgPath));
			config.getServletContext().getRequestDispatcher(imgPath).forward(req, resp);
		}
			
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		config = null;
	}

	private void ensureImageExists(String imagePath) {
		File iFile = new File(imagePath);
		if(iFile.exists())
			return;
		
		int indx = imagePath.lastIndexOf("\\");
		if(indx == -1) {
			indx = imagePath.lastIndexOf("/");
		}
		
		if(indx == -1) {
			CTILogger.error("Unable to determine image name from path: " + imagePath);
			return; 
		}
					
		String imageName = imagePath.substring(indx+1);
		
		CTILogger.info("Attempting to locate image: " + imageName + " in the database");

		for(Iterator i = DefaultDatabaseCache.getInstance().getAllYukonImages().iterator(); i.hasNext();) {
			LiteYukonImage img = (LiteYukonImage) i.next();
			if(img.getImageName().equals(imageName)) {
				CTILogger.info("Image found, copying to: " + imagePath);
				byte[] imgBuf = img.getImageValue();
				OutputStream out = null;
				try {
					out = new FileOutputStream(imagePath);
					out.write(imgBuf);
					return;
				} 
				catch(IOException ioe) {
					CTILogger.error("Error copying " + imageName + " to " + imagePath, ioe);
				}
				finally {
					if(out != null)	try {out.close(); } catch(IOException ioe2) { }					
				}
			}
		}
		CTILogger.info("Unable to locate " + imageName + " in the database, sorry");
	}
}
