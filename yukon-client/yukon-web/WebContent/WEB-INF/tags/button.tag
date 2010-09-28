<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag body-content="empty" %>

<%@ attribute name="nameKey" required="true" rtexprvalue="true"%>
<%@ attribute name="type"%>
<%@ attribute name="onclick"%>

<c:if test="${empty pageScope.type}">
    <c:set var="type" value="button"/>
</c:if>

<c:set var="additionalAttributes" value=""/>
<c:if test="${!empty pageScope.onclick}">
    <c:set var="additionalAttributes"
        value="${additionalAttributes} onclick=\"${pageScope.onclick}\""/>
</c:if>

<cti:msgScope paths=".${pageScope.nameKey},components.button.${pageScope.nameKey}">

<button type="${pageScope.type}"${additionalAttributes}><cti:msg2 key=".label"/></button>

</cti:msgScope>
