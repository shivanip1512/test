<%@ tag body-content="empty"%>

<%@ attribute name="input" required="true" type="com.cannontech.web.input.Input"%>
<%@ attribute name="error" required="true" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:if test="${error}">
    <c:set var="styles" value="style='color: red'"/>
</c:if>
<span ${styles} title="${input.description}">${input.displayName} 
    <c:set var="count" value="${fn:length(input.validatorList)}" />
    <c:forEach var="validator" items="${input.validatorList}">
        <c:set var="count" value="${count - 1}" />
        <i>${validator.description}</i><c:if test="${count > 0}">,&nbsp;</c:if>
    </c:forEach>
</span>