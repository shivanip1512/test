/*
 * Created on Oct 29, 2003
 */
package com.cannontech.cttp.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.exolab.castor.xml.Unmarshaller;


import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_dtd_v0r1Doc;

/**
 * @author aaron
 */
public class TestUnmarshall {

	public static void main(String[] args) throws Exception {
		cttp_dtd_v0r1Doc doc = new cttp_dtd_v0r1Doc();
		cttp_OperationType cttpOp = new cttp_OperationType(doc.load("ServerStatus.xml"));
		System.out.println("done");
	}
}
