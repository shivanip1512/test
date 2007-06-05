<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

results:

<c:if test="${searchResults != null}">
	<a href="${searchResults}">Back to search results</a>
</c:if>
