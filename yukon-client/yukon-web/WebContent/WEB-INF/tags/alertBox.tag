<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" description="Values are 'success', 'info', 'warning', 'error'. Default: 'error'" %>
<%@ attribute name="nameKey" %>
<%@ attribute name="arguments" type="java.lang.Object" %>

<cti:default var="type" value="error"/>

<div class="user-message ${type}">
    <c:choose>
        <c:when test="${not empty pageScope.nameKey}">
            <i:inline key="${nameKey}" arguments="${arguments}"/>
        </c:when>
        <c:otherwise><jsp:doBody/></c:otherwise>
    </c:choose>
</div>