<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="com.cannontech.roles.application.WebClientRole"%>
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


<cti:checklogin/>

<%  
	SubBusTableModel subBusMdl = cbcServlet.getSubTableModel(); 
	FeederTableModel feederMdl = cbcServlet.getFeederTableModel(); 
	CapBankTableModel capBankMdl = cbcServlet.getCapBankTableModel(); 
	
	String subArea = request.getParameter("area");
	if( subArea != null )
		cbcSession.setLastArea( subArea );
	
	
	cbcSession.setRefreshRate( CapControlWebAnnex.REF_SECONDS_DEF );
	
	cbcServlet.setUserName( 
		(session.getAttribute("YUKON_USER") == null 
		 ? "(null)"
		 : session.getAttribute("YUKON_USER").toString()) );
	
	
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
	
	
%>
