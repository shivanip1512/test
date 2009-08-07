<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="selectorName" required="true" type="java.lang.String"%>
<%@ attribute name="initiallySelected" required="false" type="java.lang.Boolean"%>

<c:if test="${empty initiallySelected}">
	<c:set var="initiallySelected" value="false"/>
</c:if>

<cti:uniqueIdentifier var="thisId" prefix="contentSelectorContent_" />

<div id="${thisId}" 
	 class="x-hide-display"
	 selectorName="${selectorName}" 
	 initiallySelected="${initiallySelected}">

	<jsp:doBody/>

</div>