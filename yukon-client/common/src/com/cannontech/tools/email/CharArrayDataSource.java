/*
 * Created on Dec 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.email;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CharArrayDataSource implements DataSource {
	
	private char[] chars = new char[0];
	private String contentType = "";
	private String name = "";
	
	private FileDataSource fds = null;
	
	public CharArrayDataSource(char[] chars) {
		this.chars = chars;
	}
	
	public CharArrayDataSource(char[] chars, String name, String contentType) {
		this.chars = chars;
		this.name = name;
		this.contentType = contentType;
	}
	
	private FileDataSource getFileDataSource() throws IOException {
		if (fds == null) {
			File tempFile = File.createTempFile("ctiMailTemp", "att");					
			FileWriter fWriter = new FileWriter(tempFile);
			fWriter.write( chars );
			fWriter.flush();
			fWriter.close();
			tempFile.deleteOnExit();
			
			fds = new FileDataSource(tempFile);
		}
		
		return fds;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		return contentType;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return getFileDataSource().getInputStream();
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException(getClass() + ": the method is not supported");
	}
	
	public void setName(String name) {
		this.name = name;
	}

}
