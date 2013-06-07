package com.cannontech.web.loadcontrol;

/**
 * Bean to be used by a jsp to send start a mandatory curtailment.
 * Creation date: (6/11/2001 12:43:16 PM)
 * @author: Aaron Lauinger
 */
import java.util.Date;

import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;

public class NewMandatoryCurtailment
{

/**
 * NewMandatoryCurtailment constructor comment.
 */
public NewMandatoryCurtailment() {
	super();
}
/**
 */
public void submitMandatoryCurtailment(LoadControlClientConnection conn, int programID, Date notificationDate, Date curtailmentDate, int duration, String comments )
{
	java.util.GregorianCalendar notifyCal = new java.util.GregorianCalendar();
	notifyCal.setTime(notificationDate);

	java.util.GregorianCalendar startCal = new java.util.GregorianCalendar();
	startCal.setTime(curtailmentDate);

	Date stopDate = new Date(curtailmentDate.getTime() + (60*duration));
	java.util.GregorianCalendar stopCal = new java.util.GregorianCalendar();
	stopCal.setTime(stopDate);
	
	ManualControlRequestMessage msg = new ManualControlRequestMessage();
	
	msg.setCommand( ManualControlRequestMessage.SCHEDULED_START );
	msg.setYukonId(programID);
	msg.setNotifyTime(notifyCal);
	msg.setStartTime(startCal);
	msg.setStopTime(stopCal);
	msg.setAddditionalInfo(comments);

	conn.write(msg);
}
}
