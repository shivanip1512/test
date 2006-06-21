/*
 * Created on Oct 29, 2003
 */
package com.cannontech.cttp.test;

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
