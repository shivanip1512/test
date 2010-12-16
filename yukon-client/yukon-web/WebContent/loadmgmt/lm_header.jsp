<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ page language="java" %>

<cti:verifyRolesAndProperties value="THREE_TIER_DIRECT"/>

<%@ include file="js/lm_funcs.js" %>
<%@ include file="js/configwin.js" %>


<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="com.cannontech.core.dao.DaoFactory" %>
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
<%@ page import="com.cannontech.loadcontrol.gui.manualentry.ConstraintTableModel" %>
<%@ page import="com.cannontech.loadcontrol.gui.manualentry.ResponseProg" %>
<%@ page import="com.cannontech.message.server.ServerResponseMsg" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.util.ServletUtil" %>

<link href="../editor/css/greybox/greybox.css" rel="stylesheet" type="text/css" media="all" /> 
<script type="text/javascript" src="../JavaScript/GreyBox/AmiJS.js"></script>
<script type="text/javascript" src="../JavaScript/GreyBox/greybox.js"></script>
<script type="text/JavaScript" src="../JavaScript/lib/prototype/1.7.0.0/prototype.js"></script>




<jsp:useBean 
	id="lmSession" scope="session"
	class="com.cannontech.web.loadcontrol.LMSession"
/>

<%  
    LCConnectionServlet connServlet = (LCConnectionServlet)
        application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
   
   
	LoadcontrolCache lcCache = connServlet.getCache(); 
   
	String areaState = request.getParameter("area_state");
	if( areaState != null )
		lmSession.setAreaView( areaState );
	
	if( lmSession.getAreaView() == null )
		lmSession.setAreaView(
			DaoFactory.getAuthDao().hasPAOAccess((LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER))
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
