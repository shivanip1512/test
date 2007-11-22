<%@ attribute name="altRowOn" required="false" %>
<%@ attribute name="style" required="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="nameValueContainter" value="true" scope="request"/>
<c:set var="altRow" value="false" scope="request"/>
<c:set var="altRowOn" value="${altRowOn}" scope="request"/>

<table class="nameValueTable" style="${style}">
	<jsp:doBody/>
</table>
	
<c:set var="nameValueContainter" value="false" scope="request"/>