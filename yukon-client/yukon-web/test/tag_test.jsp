<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.operator.CommercialMeteringRole" %>
<%@ page import="com.cannontech.roles.operator.DirectLoadcontrolRole" %>
<%@ page import="com.cannontech.roles.operator.DirectCurtailmentRole" %>
<%@ page import="com.cannontech.roles.operator.EnergyBuybackRole" %>
<%@ page import="com.cannontech.roles.operator.OddsForControlRole" %>
<%@ page import="com.cannontech.roles.application.DBEditorRole" %>
<%@ page import="com.cannontech.roles.yukon.SystemRole" %>

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<html>
!tag_test!<br><br>

<b>checklogin</b><br>
----<br>
<cti:checklogin/>
User is logged in<br>
----<br>

<br><b>checkRole</b><br>
----<br>
Checking for WebClientRole<br>
<cti:checkRole roleid="<%=WebClientRole.ROLEID%>">
Found WebClient role, good<br>
</cti:checkRole>
----<br>
Checking for DBEditorRole<br>
<cti:checkRole roleid="<%=DBEditorRole.ROLEID%>">
Found DBEditorRole, this could be an error<br>
</cti:checkRole>
----<br>

<br><b>checkNoRole</b><br>
----<br>
Checking for no WebClientRole<br>
<cti:checkNoRole roleid="<%=WebClientRole.ROLEID%>">
Did NOT find WebClient role, this could be an error<br>
</cti:checkNoRole>
----<br>
Checking for no DBEditorRole<br>
<cti:checkNoRole roleid="<%=DBEditorRole.ROLEID%>">
Did NOT find DBEditor role, good<br>
</cti:checkNoRole>
----<br>

<br><b>checkProperty</b><br> 
----<br>
Checking for WebClientRole.STYLE_SHEET<br>
<cti:checkProperty propertyid="<%=WebClientRole.STYLE_SHEET%>">
Found style sheet,good<br>
</cti:checkProperty>
----<br>
Checking for DBEditor.POINT_ID_EDIT<br>
<cti:checkProperty propertyid="<%=DBEditorRole.POINT_ID_EDIT%>">
Found DBEditor.POINT_ID_EDIT, this could be an error<br>
</cti:checkProperty>
----<br>

<br><b>checkNoProperty</b><br>
----<br>
Checking for false WebClientRole.STYLE_SHEET<br>
<cti:checkNoProperty propertyid="<%=WebClientRole.STYLE_SHEET%>">
style sheet is false!  check into this!!<br>
</cti:checkNoProperty>
----<br>
Checking for false DBEditor.POINT_ID_EDIT<br>
<cti:checkNoProperty propertyid="<%=DBEditorRole.POINT_ID_EDIT%>">
DBEditorRole.POINT_ID_EDIT is false!, good<br>
</cti:checkNoProperty>
----<br>

<br><b>checkMultiRole</b><br>
----<br>
Checking for WebClientRole AND SystemRole<br>
<cti:checkMultiRole roleid="<%= Integer.toString(WebClientRole.ROLEID) + ',' + Integer.toString(SystemRole.ROLEID) %>">
Found WebClientRole AND SystemRole, good<br>
</cti:checkMultiRole>
----<br>
Checking for WebClientRole AND DBEditorRole<br>
<cti:checkMultiRole roleid="<%= Integer.toString(WebClientRole.ROLEID) + ',' + Integer.toString(DBEditorRole.ROLEID) %>">
Found WebClientRole AND DBEditorRole, this could be an error<br>
</cti:checkMultiRole>
----<br>

<br><b>checkMultiProperty</b><br>
----<br>
Checking for WebClientRole.STYLE_SHEET AND SystemRole.DISPATCH_MACHINE<br>
<cti:checkMultiProperty propertyid="<%= Integer.toString(WebClientRole.STYLE_SHEET) + ',' + Integer.toString(SystemRole.DISPATCH_MACHINE) %>">
Found style sheet AND dispatch machine<br>
</cti:checkMultiProperty>
----<br>
Checking for WebClientRole.STYLE_SHEET AND DBEditorRole.POINT_ID_EDIT<br>
<cti:checkMultiProperty propertyid="<%= Integer.toString(WebClientRole.STYLE_SHEET) + ',' + Integer.toString(DBEditorRole.POINT_ID_EDIT) %>">
Found style sheet AND DBEditor.POINT_ID_EDIT, this could be an error<br>
</cti:checkMultiProperty>
----<br>

<br><b>getProperty</b><br>
----<br>
Getting value for WebClientRole.STYLE_SHEET<br>
<cti:getProperty propertyid="<%= WebClientRole.STYLE_SHEET %>" /><br>
You should see a style sheet above this line!<br>
----<br>




