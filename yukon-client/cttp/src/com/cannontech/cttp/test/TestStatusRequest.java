/*
 * Created on Nov 4, 2003
 */
package com.cannontech.cttp.test;

import java.io.ByteArrayInputStream;

import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_UserLoginType;
import com.cannontech.cttp.schema.cttp_dtd_v0r1Doc;

import junit.framework.TestCase;

/**
 * @author aaron
 */
public class TestStatusRequest extends TestCase {
	public TestStatusRequest(String arg) {
		super(arg);
	}
	
	protected void runTest() throws Throwable {
		ByteArrayInputStream bis = new ByteArrayInputStream(ExampleCTTP.CTTP_STATUS_REQUEST.getBytes());
		cttp_dtd_v0r1Doc doc = new cttp_dtd_v0r1Doc();
		cttp_OperationType cttpOp = new cttp_OperationType(doc.load(bis));
		bis.close();
		
		cttp_UserLoginType cttpUserLogin = cttpOp.getcttp_UserLogin();
		
		assertEquals(cttpUserLogin.getuserID().toString(), ExampleCTTP.VALID_USERID);
		assertEquals(cttpUserLogin.getuserPassword().toString(), ExampleCTTP.VALID_PASSWORD);
		assertNotNull(cttpOp.getcttp_ServerStatusRequest());
	}
}
