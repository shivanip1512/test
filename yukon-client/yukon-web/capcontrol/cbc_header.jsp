<%@ page language="java" %>

<%@ taglib uri="../WEB-INF/cti.tld" prefix="cti" %>

<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.roles.application.TDCRole" %>
<%@ page import="com.cannontech.cbc.gui.SubBusTableModel" %>
<%@ page import="com.cannontech.cbc.gui.FeederTableModel" %>
<%@ page import="com.cannontech.cbc.gui.CapBankTableModel" %>
<%@ page import="com.cannontech.clientutils.CTILogger" %>
<%@ page import="com.cannontech.cbc.web.CapControlWebAnnex" %>
<%@ page import="com.cannontech.yukon.cbc.CBCCommand" %>
<%@ page import="com.cannontech.servlet.CBCConnServlet" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.yukon.cbc.SubBus" %>
<%@ page import="com.cannontech.yukon.cbc.Feeder" %>
<%@ page import="com.cannontech.yukon.cbc.CapBankDevice" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<jsp:useBean id="cbcAnnex" scope="session"
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

	if( cbcAnnex.getYukonUser() == null )
		cbcAnnex.setYukonUser( (LiteYukonUser)session.getAttribute("YUKON_USER") );


   if( !cbcAnnex.hasValidConn() )
		cbcAnnex.setConnection( connServlet.getConnection() );
	

	/**
	* Remember this, every client session has its own set of table
	* models in it. So, each session has its own copy of the blob.
	* This may need to be reworked when many sessions are present
	**/
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
		//if we are just starting, we must have an area
		if( subBusMdl.getAreaNames().size() > 0 && cbcSession.getLastArea() == null)
			cbcSession.setLastArea( subBusMdl.getAreaNames().get(0).toString() );	
	}
		
	
	//set the filter to the one we want
	subBusMdl.setFilter( cbcSession.getLastArea() );
	
%>
