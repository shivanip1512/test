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
	
CTILogger.debug( "		CBC-HDR called" );
	
	//set the filter to the one we want
	subBusMdl.setFilter( cbcSession.getLastArea() );
	
	
//	 ADD THIS TO THE TOP LATER
//<cti:checklogin/>
//<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>
//
  	// YUKON_USER is an ugly name, give it an alias
//	LiteYukonUser user = YUKON_USER;	
//
%>
