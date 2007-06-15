<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${searchResults != null}">
	<a href="${searchResults}">&lt;- Back to search results</a>
</c:if>
