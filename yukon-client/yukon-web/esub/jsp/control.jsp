<%@ page import="com.cannontech.common.cache.PointChangeCache" %>
<%@ page import="com.cannontech.database.cache.functions.PAOFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.PointFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.StateFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LitePoint" %>
<%@ page import="com.cannontech.database.data.lite.LiteState" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.message.dispatch.message.PointData" %>

<%
	/* control.jsp
	 * Takes input do do control on a given point.
	 * Required parameters:
	 * pointid - The ID of the point to control
	 */

	int pointid = Integer.parseInt(request.getParameter("pointid"));
	LitePoint lp = PointFuncs.getLitePoint(pointid);
	LiteYukonPAObject lpao = PAOFuncs.getLiteYukonPAO(lp.getPaobjectID());
	
	PointChangeCache pcc = PointChangeCache.getPointChangeCache();
	PointData pdata = pcc.getValue(lp.getPointID());
	
	LiteState[] ls = StateFuncs.getLiteStates(lp.getStateGroupID());
	String deviceName = lpao.getPaoName();
	String pointName = lp.getPointName();
	String currentState = pcc.getState(lp.getPointID(), pdata.getValue());
	String selectedRawState = null;
	
	String confirm = request.getParameter("confirm");
	if(confirm != null) {
		String newState = request.getParameter("state" + Integer.toString(ls[0].getStateRawState()));
		if(newState != null) {
			selectedRawState = Integer.toString(ls[0].getStateRawState());
		}
		else {
			newState = request.getParameter("state" + Integer.toString(ls[1].getStateRawState()));
			if(newState != null) {
				selectedRawState = Integer.toString(ls[1].getStateRawState());
			}
			else { //this should not happen
				return;
			}				
		}
	}
%>
<html>
<head>
<title>Control</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script langauge = "Javascript" src= "control.js"></script>
<script language = "Javascript">
function submitForm() {
  if(form1.send.value == 'true' ) {
  	submitControl();
  }  
} 
</script>
</head>
<body bgcolor="#000000" text="#000000"><form name = "form1"  method="get" onSubmit="submitForm()">    
  <table width="100%" border="0" cellspacing="0" cellpadding="9" >
    <tr>
    <td>
      <table width="100%" border="2" cellspacing="0" cellpadding="3" bgcolor = "#FFFFFF">
        <tr> 
            <td height="2" width="50%" valign = "top" bgcolor = "#CCCCCC"><b><font size="2" face="Arial, Helvetica, sans-serif">Status Control</font></b></td>
        </tr>
        <tr>
        	<td>
        		
              <table>
                <tr> 
                  <td> <font face="Arial, Helvetica, sans-serif" size="2">Device:</font></td>
                  <td><%= deviceName %></td>                  
                </tr>
                <tr> 
                  <td> <font face="Arial, Helvetica, sans-serif" size="2">Point:</font> 
                  </td>
                  <td><%= pointName %></td>
                </tr>
                <tr> 
                  <td> <font face="Arial, Helvetica, sans-serif" size="2">Current 
                    State: </font></td>
                  <td><%= currentState %></td>
                </tr>
               
              </table>
        	</td>
        </tr>
        
          <tr>
    <td align = "center" valign = "bottom">
<% if(confirm == null) { %>    
        <input type="submit" name="state<%= ls[0].getStateRawState() %>" value="<%= ls[0].getStateText() %>" >
        <input type="submit" name="state<%= ls[1].getStateRawState() %>" value="<%= ls[1].getStateText() %>" >
<% } else { %>
		<input type="submit" name="Execute" value="Execute">
		<input type="button" name="cancelbutton" value="Cancel" onclick = "Javascript:window.close()">
<% } %>        
      
    </td>
  </tr>                
      </table>
    </td>
  </tr>
</table>

<% if(confirm == null) 	{ %>
	<input type="hidden" name="send" value="false">
	<input type="hidden" name="confirm" value="true">
<% } else { %>
	<input type="hidden" name="send" value="true">
	<input type="hidden" name="confirm" value="false">
	
	<input type="hidden" name="selectedRawState" value="<%= selectedRawState %>" > 
<% } %>
	<input type="hidden" name="pointid" value="<%=pointid%>">
</form>
</body>
</html>
