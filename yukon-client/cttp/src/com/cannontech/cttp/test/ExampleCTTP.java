/*
 * Created on Nov 4, 2003
 */
package com.cannontech.cttp.test;

import com.cannontech.cttp.Cttp;

/**
 * @author aaron
 */
public class ExampleCTTP {
	
	public static final String VALID_USERID = "joe";
	public static final String VALID_PASSWORD = "54354543";

	public static final String CTTP_STATUS_REQUEST = 
	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +                                                   
	"<cttp-Operation cttpVersion=\"CTTP-V0R1\">\n" +
	"<cttp-UserLogin userID=\"" + VALID_USERID + "\" userPassword=\"" + VALID_PASSWORD + "\" utilityID=\"SCE\"/>\n" +
	"<cttp-ServerStatusRequest/>\n" +
	"</cttp-Operation>\n";                           
	
	public static final String CTTP_COMMAND_REQUEST = 
	"<?xml version=\"1.0\"?>\n" +
	"<cttp-Operation cttpVersion=\"CTTP-V0R1\">\n" +
	"<cttp-UserLogin userID=\"AuthorizedUser\" userPassword=\"ValidPassword\"/>\n" +
	"<cttp-SubmitCommandRequest>\n" +
	"<cttp-TargetGroup groupID=\"Group1\"/>\n" +
	"<cttp-TargetGroup groupID=\"Group3\"/>\n" +
	"<cttp-TargetGroup groupID=\"Group5\"/>\n" +
	"<cttp-Command>\n" +
	"<cttp-OffsetCommand offsetDegreesF=\"10\" offsetDuration=\"6\"/>\n" +
	"</cttp-Command>\n" +
	"</cttp-SubmitCommandRequest>\n" +
	"</cttp-Operation>\n";
	
	public static final String CTTP_COMMAND_STATUS_REQUEST =
	"<?xml version=\"1.0\"?>\n" +
	"<cttp-Operation cttpVersion=\"CTTP-V0R1\">\n" +
	"<cttp-UserLogin userID=\"AuthorizedUser\" userPassword=\"ValidPassword\"/>\n" +
	"<cttp-CommandStatusRequest commandTrackingCode=\"6F9619FF-8B86-D011-B42D-00C04FC964FF\"/>\n" +
	"</cttp-Operation>\n";
	
	public static final String CTTP_GROUP_STATUS_REQUEST = 
	"<?xml version=\"1.0\"?>\n" +
	"<cttp-Operation cttpVersion=\"CTTP-V0R1\">\n" +
	"<cttp-UserLogin userID=\"AuthorizedUser\" userPassword=\"ValidPassword\"/>\n" +
	"<cttp-GroupStatusRequest>\n" +
	"<cttp-AllGroups/>\n" +
	"</cttp-GroupStatusRequest>\n" +
	"</cttp-Operation>\n";
	
	public static final String CTTP_VALID_STATUS_RESPONSE =
	"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + 
	"<!DOCTYPE cttp-Operation SYSTEM \"http://65.165.200.74/cttp-dtd-v0r1.dtd\">\n" + 
	"<cttp-Operation cttpVersion=\"" + Cttp.CTTP_VERSION + "\">\n" +
	"<cttp-ServerStatusResponse>\n" +
	"<cttp-ServerStatus timestamp=\"2003-11-06T16:19:35\" version=\"0.001\" owner=\"Cannon Technologies\" supportContact=\"Technical Support\" supportPhone=\"763-595-7775\" supportEmail=\"support@cannontech.com\">\n" +
	"Cannon Technologies CTTP Gateway Server Version 0.001 OK\n</cttp-ServerStatus>\n" +  
	"</cttp-ServerStatusResponse>\n" +
	"</cttp-Operation>\n";
	
	public static final String CTTP_INVALID_STATUS_RESPONSE = // OK!!!!!
		"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + 
		"<!DOCTYPE cttp-Operation SYSTEM \"http://65.165.200.74/cttp-dtd-v0r1.dtd\">\n" + 
		"<cttp-Operation cttpVersion=\"" + Cttp.CTTP_VERSION + "\">\n" +		
		"<cttp-ServerStatusResponse>\n" +
		"<cttp-Failure errorCode=\"401\" errorText=\"Incorrect password\">\n" +
		"</cttp-Failure>\n" +   
		"</cttp-ServerStatusResponse>\n" +
		"</cttp-Operation>\n";
	
}
