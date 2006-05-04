package com.cannontech.notif.test;


import java.text.DateFormat;
import java.util.Date;

import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.yukon.INotifConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestClient
{

	public TestClient()
	{
		super();
	}

	public static void main(String[] args)
	{
		TestClient tc = new TestClient();
		
		try
		{
            INotifConnection notificationConn = ConnPool.getInstance().getDefNotificationConn();

            Thread.sleep(5000);
            
            NotifLMControlMsg msg = new NotifLMControlMsg();
            int[] notifArray = {2};
            msg.notifGroupIds = notifArray;
            msg.notifType = NotifLMControlMsg.STARTING_CONTROL_NOTIFICATION;
            msg.programId = 6;
            msg.startTime = new Date();
            msg.stopTime = DateFormat.getInstance().parse("11/1/2005 3:45 PM");
            //notificationConn.write(msg);
            Thread.sleep(5000);
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
		
	}

}
