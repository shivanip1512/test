<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="cssClass" required="false" %>

<c:set var="filteredByContainer" value="true" scope="request"/>
<div class="filter_container ${pageScope.cssClass}">
	<jsp:doBody/>
</div>
<c:set var="filteredByContainer" value="false" scope="request"/>