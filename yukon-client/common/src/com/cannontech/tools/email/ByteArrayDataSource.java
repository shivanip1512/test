/*
 * Created on Dec 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.email;

import java.io.ByteArrayInputStream;
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
public class ByteArrayDataSource implements DataSource {
	
	private byte[] bytes = new byte[0];
	private String contentType = "";
	private String name = "";
	
	private FileDataSource fds = null;
	
	public ByteArrayDataSource(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public ByteArrayDataSource(byte[] bytes, String name, String contentType) {
		this.bytes = bytes;
		this.name = name;
		this.contentType = contentType;
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
		return new ByteArrayInputStream(bytes);
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
		throw new IOException(getClass() + ": this method is not supported");
	}

}
