<%@ page language="java" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="com.cannontech.cbc.gui.SubBusTableModel" %>
<%@ page import="com.cannontech.cbc.gui.FeederTableModel" %>
<%@ page import="com.cannontech.cbc.gui.CapBankTableModel" %>
<%@ page import="com.cannontech.clientutils.CTILogger" %>
<%@ page import="com.cannontech.cbc.web.CapControlWebAnnex" %>
<%@ page import="com.cannontech.cbc.messages.CBCCommand" %>


<jsp:useBean 
	id="cbcServlet" scope="session"
	class="com.cannontech.cbc.web.CapControlWebAnnex"
/>

<jsp:useBean 
	id="cbcSession" scope="session" 
	class="com.cannontech.cbc.web.CBCSessionInfo"
/>

<%  
	SubBusTableModel subBusMdl = cbcServlet.getSubTableModel(); 
	FeederTableModel feederMdl = cbcServlet.getFeederTableModel(); 
	CapBankTableModel capBankMdl = cbcServlet.getCapBankTableModel(); 
	
	
	String lastArea = request.getParameter("area");
	if( lastArea == null )
		lastArea = cbcSession.getLastArea();
		
	if( lastArea == null)
		lastArea = SubBusTableModel.ALL_FILTER;
	
	
	cbcSession.setLastArea( lastArea );
	cbcSession.setRefreshRate( CapControlWebAnnex.REF_SECONDS_DEF );	
	
	
	//set the filter to the one we want
	subBusMdl.setFilter( cbcSession.getLastArea() );
	
	
	//handle any commands that we may need to send to the server from any page here
	String cmdType = request.getParameter("cmdExecute");
	
	if( "Submit".equals(cmdType) )
	{
		try
		{
			Integer cmdID = new Integer( request.getParameter("cmdID") );
			String controlType = request.getParameter("controlType");
			Integer cmdRowID = new Integer( request.getParameter("cmdRowID") );
			String manChange = request.getParameter("manualChange");
			
			
			CTILogger.debug(request.getServletPath() +
				"	  cmdID = " + cmdID +
				", controlType = " + controlType +
				", cmdRowID = " + cmdRowID +
				", manualChng = " + manChange  );
			
			//send the command with the id, type, rowid
			cbcServlet.executeCommand( 
							cmdID.intValue(), 
							controlType, 
							cmdRowID.intValue(),
							(manChange == null ? null : new Integer(manChange)) );
			
			cbcSession.setRefreshRate( CapControlWebAnnex.REF_SECONDS_PEND );
		}
		catch( Exception e )
		{
			CTILogger.warn( "Command was attempted but failed for the following reason:", e );
		}
	}


	
//	 ADD THIS TO THE TOP LATER
//<cti:checklogin/>
//<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>
//
  	// YUKON_USER is an ugly name, give it an alias
//	LiteYukonUser user = YUKON_USER;	
//
%>
