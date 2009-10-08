<%@ attribute name="areaCode" required="false" type="java.lang.String"%>
<%@ attribute name="phone" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty pageScope.areaCode}">
    (${pageScope.areaCode}) 
</c:if>
${phone}