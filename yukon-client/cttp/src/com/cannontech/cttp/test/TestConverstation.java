/*
 * Created on Oct 29, 2003
 */
package com.cannontech.cttp.test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import junit.framework.Test;
import junit.framework.TestResult;

import com.altova.xml.types.SchemaString;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_UserLoginType;
import com.cannontech.cttp.schema.cttp_dtd_v0r1Doc;

/**
 * @author aaron
 */
public class TestConverstation  implements Test {

	
	public static void main(String[] args) throws Exception {
	/*	cttp_OperationType cttpOp = new cttp_OperationType();
		cttp_UserLoginType cttpUserLogin = new cttp_UserLoginType();
		cttpUserLogin.adduserID("cannon");
		cttpUserLogin.adduserPassword("uDkusT8AGPyrv7lsDVFsy6BIpWzi9gAD");
		cttpUserLogin.addutilityID("SCE");
		
		cttpOp.addcttp_UserLogin(cttpUserLogin);
		cttpOp.addcttp_ServerStatusRequest("");
		
		cttp_dtd_v0r1Doc doc = new cttp_dtd_v0r1Doc();
			
		doc.setRootElementName(null, "cttp-Operation");//"cttp-Operation");
		doc.save("TestStatusRequest.xml", cttpOp);
		
		doc = new cttp_dtd_v0r1Doc();
		
		cttpOp = new cttp_OperationType(doc.load("ServerStatus.xml"));
		cttp_UserLoginType userLogin = cttpOp.getcttp_UserLogin();
		System.out.println(userLogin.getuserPassword().asString());	*/
		
		serverStatusTest();
	//	submitCommandTest();
		
		System.out.println("done");
	}	
	
	static void serverStatusTest() throws Exception {
		FileInputStream fis = new FileInputStream("StatusRequest.xml");
		cttp_dtd_v0r1Doc doc = new cttp_dtd_v0r1Doc();
		cttp_OperationType cttpOp = new cttp_OperationType(doc.load(fis));
		fis.close();
		
		cttp_UserLoginType cttpUserLogin = cttpOp.getcttp_UserLogin();
		dumpUserLogin(cttpUserLogin);
		
		SchemaString statusStr = cttpOp.getcttp_ServerStatusRequest();
		if(statusStr == null) {
			System.out.println("No server status included");
		}		
	}
		
	
	static void dumpUserLogin(cttp_UserLoginType cttpUserLogin) throws Exception {
		System.out.println("UserID: " + cttpUserLogin.getuserID().toString());
		System.out.println("Password: " + cttpUserLogin.getuserPassword().toString());
		System.out.println("UtilityID: " + cttpUserLogin.getutilityID().toString());		
	}

	/* (non-Javadoc)
	 * @see junit.framework.Test#countTestCases()
	 */
	public int countTestCases() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see junit.framework.Test#run(junit.framework.TestResult)
	 */
	public void run(TestResult arg0) {
		// TODO Auto-generated method stub
		
	}
}
