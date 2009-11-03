<%@ attribute name="paoId" required="true" type="java.lang.Integer" %>
<%@ attribute name="isFavorite" required="true" type="java.lang.Boolean" %>
<%@ attribute name="fromHomePage" required="false" type="java.lang.Boolean" %>
<%@ attribute name="isRecentlyViewedItem" required="false" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<c:if test="${isFavorite}">
    <c:set var="logoKey" value="remove"/>
    <c:set var="onclick" value="removeFavorite(${paoId})"/>
</c:if>

<c:if test="${!isFavorite}">
    <c:set var="logoKey" value="add"/>
    <c:set var="onclick" value="addFavorite(${paoId})"/>
</c:if>

<c:set var="refId" value=' id="favoritesIcon${paoId}"'/>
<c:if test="${!empty fromHomePage && fromHomePage && !empty isRecentlyViewedItem && isRecentlyViewedItem}">
    <c:set var="refId" value=' id="rvfavoritesIcon${paoId}"'/>
</c:if>

<a${refId} href="javascript:void(0)" class="simpleLink"
    onclick="${onclick}">
    <cti:logo key="yukon.web.modules.dr.actions.${logoKey}FavoriteIcon"/>
</a>
