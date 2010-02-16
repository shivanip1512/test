<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="rowHighlight" required="false" %>
<%@ attribute name="nameColumnWidth" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="isSection" required="false" type="java.lang.Boolean" %>

<cti:msg2 var="name" key="${pageScope.nameKey}" blankIfMissing="true"/>

<tags:nameValue name="${pageScope.name}" rowHighlight="${pageScope.rowHighlight}" nameColumnWidth="${nameColumnWidth}" id="${pageScope.id}" isSection="${pageScope.isSection}">

	<jsp:doBody/>
	
</tags:nameValue>