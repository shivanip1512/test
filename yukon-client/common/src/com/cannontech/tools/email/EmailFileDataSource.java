/*
 * Created on Dec 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.email;

import java.io.File;
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
public class EmailFileDataSource implements DataSource {
	
	private File file = null;
	private String name = null;
	private String contentType = null;
	
	private FileDataSource fds = null;
	
	public EmailFileDataSource(File file) {
		this.file = file;
	}
	
	public EmailFileDataSource(File file, String name, String contentType) {
		this.file = file;
		this.name = name;
		this.contentType = contentType;
	}
	
	private FileDataSource getFileDataSource() {
		if (fds == null)
			fds = new FileDataSource(file);
		return fds;
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getContentType()
	 */
	public String getContentType() {
		if (contentType != null)
			return contentType;
		return getFileDataSource().getContentType();
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
		if (name != null)
			return name;
		return getFileDataSource().getName();
	}

	/* (non-Javadoc)
	 * @see javax.activation.DataSource#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		throw new IOException(getClass() + ": this method is not supported.");
	}

}
