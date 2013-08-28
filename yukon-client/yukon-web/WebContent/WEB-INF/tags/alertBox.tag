<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" description="Values are 'confirm', 'error', 'warning'. Default: 'error'" %>
<%@ attribute name="nameKey" %>
<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="contents" description="Used when 'nameKey' is not provided." %>

<cti:default var="type" value="error"/>

<div class="userMessage ${fn:toUpperCase(type)}">
    <c:choose>
        <c:when test="${not empty pageScope.contents}">${contents}</c:when>
        <c:otherwise>
            <i:inline key="${nameKey}" arguments="${arguments}"/>
        </c:otherwise>
    </c:choose>
</div>
