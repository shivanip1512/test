<%@ page language="java" %>

<%@ taglib uri="../WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.cbc.gui.SubBusTableModel" %>
<%@ page import="com.cannontech.cbc.gui.FeederTableModel" %>
<%@ page import="com.cannontech.cbc.gui.CapBankTableModel" %>
<%@ page import="com.cannontech.clientutils.CTILogger" %>
<%@ page import="com.cannontech.cbc.web.CapControlWebAnnex" %>
<%@ page import="com.cannontech.cbc.messages.CBCCommand" %>
<%@ page import="com.cannontech.servlet.CBCConnServlet" %>

<jsp:useBean 
	id="cbcAnnex" scope="session"
	class="com.cannontech.cbc.web.CapControlWebAnnex"
/>

<jsp:useBean 
	id="cbcSession" scope="session" 
	class="com.cannontech.cbc.web.CBCSessionInfo"
/>


<cti:checklogin/>

<%  
    CBCConnServlet connServlet = (CBCConnServlet)
        application.getAttribute(CBCConnServlet.SERVLET_CONTEXT_ID);
   
   
   if( !cbcAnnex.hasValidConn() )
		cbcAnnex.setConnection( connServlet.getConnection() );
	
	SubBusTableModel subBusMdl = cbcAnnex.getSubTableModel(); 
	FeederTableModel feederMdl = cbcAnnex.getFeederTableModel(); 
	CapBankTableModel capBankMdl = cbcAnnex.getCapBankTableModel(); 
	
	String subArea = request.getParameter("area");
	if( subArea != null )
	{
		cbcSession.setLastArea( subArea );
	}
	else
	{
		if( connServlet.getAreaNames().size() > 0 )
			cbcSession.setLastArea( connServlet.getAreaNames().get(0).toString() );	
	}
	
	
	cbcAnnex.setUserName( 
		(session.getAttribute("YUKON_USER") == null 
		 ? "(null)"
		 : session.getAttribute("YUKON_USER").toString()) );
	
	
	//set the filter to the one we want
	subBusMdl.setFilter( cbcSession.getLastArea() );
	
%>
