<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- line number is key plus 2. (adjust for zero-indexed map key and header row) --%>
<ul class="simple-list">
    <c:forEach var="e" items="${exceptionRowNumberMap}">
        <li>Line ${e.key + 2} - ${e.value.message}</li>
    </c:forEach>
</ul>
