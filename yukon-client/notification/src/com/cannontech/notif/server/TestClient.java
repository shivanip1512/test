package com.cannontech.notif.server;


import java.io.File;
import java.io.FileReader;

import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.notif.message.DefColl_NotifEmailAttchMsg;
import com.cannontech.notif.message.DefColl_NotifEmailMsg;
import com.cannontech.notif.message.NotifEmailAttchMsg;
import com.cannontech.notif.message.NotifEmailMsg;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.streamer.CollectableMappings;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TestClient
{

	/**
	 * 
	 */
	public TestClient()
	{
		super();
	}

	public static void main(String[] args)
	{
		TestClient tc = new TestClient();
		
		try
		{
			//Socket sock = new Socket( "127.0.0.1", 1515 );
			
			ClientConnection conn = new ClientConnection()
			{
				protected void registerMappings(CollectableStreamer polystreamer) 
				{
					super.registerMappings( polystreamer );

					
					polystreamer.register( new DefColl_NotifEmailAttchMsg() );
					polystreamer.register( new DefColl_NotifEmailMsg() );
					polystreamer.register( CollectableMappings.OrderedVector );
					polystreamer.register( new DefineCollectableMulti() );
					
				}
			};


			
			conn.setHost("127.0.0.1");
			conn.setPort(1515);

			NotifEmailMsg msg = new NotifEmailMsg();
			msg.setBody( "This is body" );
			msg.setSubject( "This is subject" );
			msg.setTo( "ryan@cannontech.com" );
			msg.setNotifGroupID( 3 );
			
			File f = new File("d:/park.txt");
			FileReader fr = new FileReader(f);
			char[] allBytes = new char[ (int)f.length() ];
			fr.read( allBytes );
			msg.getAttachments().add( new NotifEmailAttchMsg(
					"Something.xpx", allBytes) );


			File f1 = new File("d:/t.sql");
			FileReader fr1 = new FileReader(f1);
			allBytes = new char[ (int)f1.length() ];
			fr1.read( allBytes );
			msg.getAttachments().add( new NotifEmailAttchMsg(
					"Something2.xpx", allBytes) );



			NotifEmailMsg msg1 = new NotifEmailMsg();
			msg1.setBody( "This is body (NO GROUP)" );
			msg1.setSubject( "This is subject (NO GROUP)" );
			msg1.setTo( "ryan@cannontech.com" );
			
			File f2 = new File("d:/park.txt");
			FileReader fr2 = new FileReader(f2);
			char[] allBytes2 = new char[ (int)f2.length() ];
			fr2.read( allBytes2 );
			msg1.getAttachments().add( new NotifEmailAttchMsg(
					"New.xpx", allBytes2) );



			Multi m = new Multi();
			m.getVector().add( msg );
			m.getVector().add( msg1 );
			
			conn.connect();
			conn.write( m );


			//Thread.currentThread().sleep(10000);
			conn.disconnect();
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
		
	}

}
