package com.cannontech.servlet;

import javax.servlet.http.HttpServlet;

import com.cannontech.util.GlobalProperties;

/**
 * Maintains a set yukon resources in the servlet context.
 * Persistent connections to various servers and any
 * periodic processing that needs to be done.
 *
 * There only reason we really need persistent connections to
 * the yukon servers is performance.  The process of registering
 * and getting desired data is way too slow to be done for every
 * request.
 *
 * Note, this role doesn't suite a servlet all that well but
 * its quick and dirty and there are less moving parts than
 * any obvious (to me) alternatives (JMS, etc)
 *
 * Creation date: (2/12/2002 11:01:25 AM)
 * @author: Aaron Lauinger
 */
public class YukonResources extends HttpServlet implements Runnable {

	// Connections to yukon servers
	public static final String DISPATCH_CONNECTION 	= "DISPATCH";
	public static final String MACS_CONNECTION		= "MACS";
	public static final String LOADMANAGEMENT_CONNECTION = "LOADMANAGEMENT";

	// Other long running services
	public static final String POINT_CHANGE_CACHE = "POINTCACHE";

	private static final String RUNNER_THREAD = "YukonResourcesRunner";
/**
 * YukonResources constructor comment.
 */
public YukonResources() {
	super();
}
public void destroy() {
	super.destroy();
	
	log("YukonResources::destroy()");
	
	com.cannontech.message.dispatch.ClientConnection dispatchConn = 
		(com.cannontech.message.dispatch.ClientConnection) getServletContext().getAttribute(DISPATCH_CONNECTION);

	if( dispatchConn != null ) {
		log("YukonResources::destroy() - Disconnecting from dispatch");
	    try {
			dispatchConn.disconnect();
	    }
 	   catch(java.io.IOException e) {
	 	   log(e.getMessage());
	    }
	}
	else {
		log("YukonResources::destroy - no dispatch connection found in context");
	}

	Thread runner = (Thread) getServletContext().getAttribute(RUNNER_THREAD);

	if( runner != null ) {
		if( runner.isAlive() ) {
			log("YukonResources::destroy() - Shutting down working thread");
		}
		else {
			log("YukonResources::destroy() - working thread found in context but is dead");
		}
	}
	else {
		log("YukonResources::destroy() - no working thread found in context");
	}
	
	
}
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException {

	super.init(config);
	
	log("YukonResources::init()");
	
	GlobalProperties props = GlobalProperties.getInstance();

	com.cannontech.message.dispatch.ClientConnection dispatchConn = 
		(com.cannontech.message.dispatch.ClientConnection) getServletContext().getAttribute(DISPATCH_CONNECTION);
	
	if( dispatchConn == null ) {
		log("YukonResources::init() - Attempting to connection to dispatch");
		
		String host = props.getProperty(GlobalProperties.DISPATCH_MACHINE, "127.0.0.1");
		int port = Integer.parseInt(props.getProperty(GlobalProperties.DISPATCH_PORT, "1510"));
	
		dispatchConn = new com.cannontech.message.dispatch.ClientConnection();
		dispatchConn.setHost(host);
		dispatchConn.setPort(port);
		
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("YukonResources" + (new java.util.Date()).getTime());
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(5000);
		com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
		pReg.setRegFlags(com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_PTS_MASK |
							com.cannontech.message.dispatch.message.PointRegistration.REG_ALARMS );
		com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
		multi.getVector().addElement(reg);
		multi.getVector().addElement(pReg);

		//dispatchConn.setRegistrationMsg(multi);
		dispatchConn.setAutoReconnect(true);		
		dispatchConn.setTimeToReconnect(30);

		try {
			dispatchConn.connectWithoutWait();
		}
		catch( java.io.IOException e ) {
			log(e.getMessage());
		}

		getServletContext().setAttribute(DISPATCH_CONNECTION, dispatchConn);
	}
	else {
		log("YukonResources::init() - Found a pre-existing dispatch connection in context");
	}

	Thread runner = (Thread) getServletContext().getAttribute(RUNNER_THREAD);

	if( runner == null ) {
		log("YukonResources::init() - Starting up working thread");

		runner = new Thread(this);
		runner.setDaemon(true);
		runner.start();
	}
	else {
		if( runner.isAlive() ) 
			log("YukonResources::init() - Found a pre-existing, alive working thread in context");
		else
			log("YukonResources::init() - Found a pre-existing, dead working thread in context");		
	}
	
	
	
}
/**
 * Creation date: (2/12/2002 1:51:17 PM)
 */
public void run() {
	log("YukonResources::run()");

	try {
		while(true) {						
			Thread.currentThread().sleep(60000);
			log("YukonResources::run() - looping dispatch");

			com.cannontech.message.dispatch.ClientConnection conn =
				(com.cannontech.message.dispatch.ClientConnection) getServletContext().getAttribute(DISPATCH_CONNECTION);

			if( conn != null ) {
				log("YukonResources::run() - looping dispatch");
				
				com.cannontech.message.dispatch.message.Command cmd = new com.cannontech.message.dispatch.message.Command();
				cmd.setOperation( com.cannontech.message.dispatch.message.Command.NO_OP );
				conn.write( cmd );
			
				Object response = conn.read();
				
				log("YukonResources::run() - received loopback " + response);			
			}
			else {
				log("YukonResources::run() - no dispatch connection found in context");
			}
		}
	}
	catch(InterruptedException e) {
		log("YukonResources::run() - interrupted");
	}

	log("YukonResources::run() - exiting");
}
/**
 * service method comment.
 */
public void service(javax.servlet.ServletRequest arg1, javax.servlet.ServletResponse arg2) throws javax.servlet.ServletException, java.io.IOException {}
}
