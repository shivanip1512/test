<%@ attribute name="value" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test='${not empty value}'>
	${value}<BR>
</c:if>