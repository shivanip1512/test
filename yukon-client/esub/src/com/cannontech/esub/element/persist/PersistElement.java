package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.esub.element.DrawingElement;

/**
 * @author aaron
 */
public interface PersistElement {
	 public void readFromJLX(DrawingElement drawingElem, InputStream in) throws IOException;
	 public void saveAsJLX(DrawingElement drawingElem, OutputStream out) throws IOException;
}
