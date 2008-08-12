<%@ attribute name="address1" required="false" type="java.lang.String"%>
<%@ attribute name="address2" required="false" type="java.lang.String"%>
<%@ attribute name="city" required="false" type="java.lang.String"%>
<%@ attribute name="state" required="false" type="java.lang.String"%>
<%@ attribute name="zip" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<tags:notNullDataLine value="${address1}"/>
<tags:notNullDataLine value="${address2}"/>
<c:if test="${not empty city}">
	${city},
</c:if>
${state} ${zip}
