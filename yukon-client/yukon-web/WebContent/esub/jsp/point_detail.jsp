<%@ page import="java.text.DecimalFormat" %>

<%@ page import="com.cannontech.database.cache.functions.PointFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.StateFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.UnitMeasureFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.database.data.lite.LitePoint" %>
<%@ page import="com.cannontech.database.data.lite.LitePointUnit" %>
<%@ page import="com.cannontech.database.data.lite.LiteState" %>
<%@ page import="com.cannontech.database.data.lite.LiteTag" %>
<%@ page import="com.cannontech.database.data.lite.LiteUnitMeasure" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%@ page import="com.cannontech.common.cache.PointChangeCache" %>
<%@ page import="com.cannontech.database.data.point.PointTypes" %>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.esub.util.UpdateUtil" %>

<%
/* point_detail.jsp
 
   Displays some current information about a given point, and gives
   a link to control it if that is applicable.  Intended as a little
   piece of html that can be popped up.
   
   Required Parameters:
   pointid - the point in question
*/
	   
	int pointID = Integer.parseInt(request.getParameter("pointid"));
	LitePoint lPoint = PointFuncs.getLitePoint(pointID);	
	String pointName = lPoint.getPointName();
	int pointOffset = lPoint.getPointOffset();
	double currentValue = PointChangeCache.getPointChangeCache().getValue(pointID).getValue();	
	
	// Build a formatter so we can present the value with the correct decimal places    
    DecimalFormat valueFormatter = new DecimalFormat();
	
	String uOfM = null; 
	String currentState = null;
	if(PointTypes.STATUS_POINT == lPoint.getPointType()) {
		currentState = PointChangeCache.getPointChangeCache().getCurrentState(pointID).getStateText();
	} 
	else { // analog
		LiteUnitMeasure lUOfM = UnitMeasureFuncs.getLiteUnitMeasureByPointID(pointID);
		if(lUOfM != null) {
			uOfM = lUOfM.getUnitMeasureName();		
		}
	    LitePointUnit lpu = PointFuncs.getPointUnit(pointID);	
		valueFormatter.setMaximumFractionDigits(lpu.getDecimalPlaces());
		valueFormatter.setMinimumFractionDigits(lpu.getDecimalPlaces());	    		
	}
	
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
    boolean offerControl = (PointTypes.STATUS_POINT == lPoint.getPointType() &&
    						AuthFuncs.checkRoleProperty(user, com.cannontech.roles.operator.EsubDrawingsRole.CONTROL));
%>
<html>
<table border="1" bgcolor = "#CCCCCC">
<tr><td>
<table >
<tr>
<td>Point ID:</td><td><%= pointID %></td>
</tr>
<tr>
<td>Point Offset:</td><td><%= pointOffset %></td>
</tr>
<tr>
<td>Point Name:</td><td><%= pointName %></td>
</tr>
<% if(currentState != null) { %>
<tr>
<td>Current State:</td><td><%= currentState %></td>
</tr>
<% } %>
<tr>
<td>Current Value:</td><td><%= valueFormatter.format(currentValue) %> <% if(uOfM != null) { %><%= uOfM %><% } %></td>
</tr>
</table>
</td></tr>
<tr><td align="center">
<% if(offerControl) { %>
<a href="" onclick="hidePointDetails(); showControlWindow(<%= pointID %>); return false;">Control/Tags</a>
<% } %>
</td></tr>
</table>

</html>