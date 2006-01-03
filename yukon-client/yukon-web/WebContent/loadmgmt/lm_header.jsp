<%@ page language="java" %>

<%@ include file="js/lm_funcs.js" %>

<%@ taglib uri="../WEB-INF/cti.tld" prefix="cti" %>


<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.clientutils.CTILogger" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.cannontech.common.util.NativeIntVector" %>

<%@ page import="com.cannontech.loadcontrol.datamodels.ControlAreaTableModel" %>
<%@ page import="com.cannontech.loadcontrol.datamodels.ProgramTableModel" %>
<%@ page import="com.cannontech.loadcontrol.datamodels.GroupTableModel" %>

<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.loadcontrol.LCUtils" %>
<%@ page import="com.cannontech.loadcontrol.displays.ControlAreaActionListener" %>

<%@ page import="com.cannontech.loadcontrol.data.LMControlArea" %>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramBase" %>
<%@ page import="com.cannontech.loadcontrol.data.ILMGroup" %>
<%@ page import="com.cannontech.web.loadcontrol.WebCmdMsg" %>
<%@ page import="com.cannontech.web.loadcontrol.LMSession" %>
<%@ page import="com.cannontech.web.loadcontrol.LMCmdMsgFactory" %>
<%@ page import="com.cannontech.web.loadcontrol.ILCCmds" %>
<%@ page import="com.cannontech.loadcontrol.data.IGearProgram" %>
<%@ page import="com.cannontech.loadcontrol.data.LMControlAreaTrigger" %>
<%@ page import="com.cannontech.common.util.CtiUtilities" %>
<%@ page import="com.cannontech.database.data.lite.LiteLMProgScenario" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.cache.functions.LMFuncs" %>
<%@ page import="com.cannontech.loadcontrol.gui.manualentry.ConstraintTableModel" %>
<%@ page import="com.cannontech.loadcontrol.gui.manualentry.ResponseProg" %>
<%@ page import="com.cannontech.message.server.ServerResponseMsg" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>

<jsp:useBean 
	id="lmSession" scope="session"
	class="com.cannontech.web.loadcontrol.LMSession"
/>

<cti:checklogin/>

<%  
    LCConnectionServlet connServlet = (LCConnectionServlet)
        application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
   
   
	LoadcontrolCache lcCache = connServlet.getCache(); 
   
	String areaState = request.getParameter("area_state");
	if( areaState != null )
		lmSession.setAreaView( areaState );
	
	if( lmSession.getAreaView() == null )
		lmSession.setAreaView(
			AuthFuncs.hasPAOAccess((LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER))
				? ControlAreaActionListener.SEL_ALL_CONTROL_AREAS
				: ControlAreaActionListener.SEL_ACTIVE_AREAS );
	
	int refreshIn = 60*1000;
	if( request.getParameter("quickrefresh") != null)
	{
		refreshIn = 4*1000;
	}
	// Remove the quickrefresh parameter
	String reloadURI = ServletUtil.tweakRequestURI(request, "quickrefresh", null);
%>

<script language="JavaScript">
function reload()
{
	setTimeout( "window.location = '<%= reloadURI %>';", <%= refreshIn %> );
}
</script>
