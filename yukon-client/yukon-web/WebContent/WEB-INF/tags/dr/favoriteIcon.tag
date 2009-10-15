<%@ attribute name="paoId" required="true" type="java.lang.Integer" %>
<%@ attribute name="isFavorite" required="true" type="java.lang.Boolean" %>
<%@ attribute name="includeText" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:if test="${isFavorite}">
    <cti:url var="removeFavoriteUrl" value="/spring/dr/removeFavorite">
        <cti:param name="paoId" value="${paoId}"/>
    </cti:url>
    <a href="${removeFavoriteUrl}" class="simpleLink">
        <cti:logo key="yukon.web.modules.dr.actions.removeFavoriteIcon"/>
        <c:if test="${includeText}">
            <cti:msg key="yukon.web.modules.dr.actions.removeFavorite"/>
        </c:if>
    </a>
</c:if>
<c:if test="${!isFavorite}">
    <cti:url var="addFavoriteUrl" value="/spring/dr/addFavorite">
        <cti:param name="paoId" value="${paoId}"/>
    </cti:url>
    <a href="${addFavoriteUrl}" class="simpleLink">
        <cti:logo key="yukon.web.modules.dr.actions.addFavoriteIcon"/>
        <c:if test="${includeText}">
            <cti:msg key="yukon.web.modules.dr.actions.addFavorite"/>
        </c:if>
    </a>
</c:if>