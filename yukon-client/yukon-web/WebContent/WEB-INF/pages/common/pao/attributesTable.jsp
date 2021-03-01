<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<table class="compact-results-table">
    <thead>
        <tr>
            <th><i:inline key="yukon.common.attribute"/></th>
        </tr>
    </thead>
    <c:forEach var="attribute" items="${point.allAttributes}">
        <tr>
            <td><i:inline key="${attribute}" htmlEscape="true"/></td>
        </tr>
    </c:forEach>
</table>