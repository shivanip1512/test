<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:if test="${searchResults != null}">
	<a href="${searchResults}"><i:inline key="yukon.web.backToSearchResults"/></a>
</c:if>
