<%@ attribute name="value" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test='${value != null}'>
        ${value}<br>
</c:if>