<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="id" %>
<%@ attribute name="input" required="true" type="com.cannontech.web.input.type.InputType" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="pageEditMode" type="com.cannontech.web.PageEditMode"%>

<c:set var="inputType" value="${input}" scope="request"/>
<spring:bind path="${path}" htmlEscape="true">
    <jsp:include flush="false" page="/WEB-INF/pages/simpleInput/${input.renderer}">
        <jsp:param name="id" value="${pageScope.id}"/>
        <jsp:param name="inputType" value="${input}"/>
        <jsp:param name="mode" value="${pageScope.pageEditMode}"/>
    </jsp:include>
</spring:bind>