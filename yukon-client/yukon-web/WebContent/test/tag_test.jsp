<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ page import="com.cannontech.roles.operator.ConsumerInfoRole" %>
<%@ page import="com.cannontech.roles.loadcontrol.DirectLoadcontrolRole" %>
<%@ page import="com.cannontech.roles.operator.OddsForControlRole" %>
<%@ page import="com.cannontech.roles.application.DBEditorRole" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<html>
!tag_test!<br><br>

<b>checklogin</b><br>
----<br>
User is logged in<br>
----<br>

<br><b>getProperty</b><br>
----<br>
Getting value for WebClientRole.STYLE_SHEET<br>
<cti:getProperty propertyid="<%= WebClientRole.STYLE_SHEET %>" /><br>
You should see a style sheet above this line!<br>
----<br>

