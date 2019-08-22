<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operations.dashboard">
    <cti:tabs>
        <cti:msg2 var="favoritesTab" key=".favorites" />
        <cti:tab title="${favoritesTab}">
            <c:if test="${empty favorites}">
                <span class="empty-list"><i:inline key=".favorites.emptyList" /></span>
            </c:if>
            <c:if test="${not empty favorites}">
                <cti:msg2 var="undoText" key="yukon.common.undo"/>
    
                <c:forEach var="module" items="${favorites}">
                    <c:if test="${!empty favorites}">
                        <h5><cti:msg2 key="${module.key}"/></h5>
                    </c:if>
                    <ul class="simple-list stacked favorites" data-undo-text="${undoText}">
                        <c:forEach items="${module.value}" var="favorite">
                            <c:set var="favoritePageName"><cti:pageName userPage="${favorite}"/></c:set>
                            <cti:msg2 var="removedText" key=".favorites.removed" htmlEscapeArguments="false"
                                arguments="${favoritePageName}"/>
                            <li class="favorite" data-removed-text="${removedText}">
                                <cti:listAsSafeString var="labelArgs" list="${favorite.arguments}"/>
                                <cti:button classes="b-favorite remove" nameKey="favorite" renderMode="image" icon="icon-star" 
                                    data-name="${favorite.name}"
                                    data-module="${favorite.moduleName}"
                                    data-path="${favorite.path}"
                                    data-label-args="${labelArgs}"/>
                                <a href="<cti:url value="${favorite.path}"/>">${favoritePageName}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:forEach>
            </c:if>
        </cti:tab>
        <cti:msg2 var="historyTab" key=".history" />
        <cti:tab title="${historyTab}">
            <c:if test="${empty history}">
                <span class="empty-list"><i:inline key=".history.emptyList" /></span>
            </c:if>
            <c:if test="${not empty history}">
                <ol class="simple-list">
                    <c:forEach var="historyItem" items="${history}">
                        <li><a href="<cti:url value="${fn:escapeXml(historyItem.path)}"/>"><cti:pageName userPage="${historyItem}"/></a></li>
                    </c:forEach>
                </ol>
            </c:if>
        </cti:tab>
    </cti:tabs>
</cti:msgScope>