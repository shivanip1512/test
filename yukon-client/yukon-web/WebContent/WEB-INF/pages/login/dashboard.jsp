<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operations" page="dashboard">

    <div class="column_12_12">
        <div class="column one section">
            <cti:tabbedContentSelector mode="section">
                <cti:msg2 var="favoritesTab" key=".favorites" />
                <cti:tabbedContentSelectorContent selectorName="${favoritesTab}">
                    <c:if test="${empty favorites}">
                        <span class="empty-list"><i:inline key=".favorites.emptyList" /></span>
                    </c:if>
                    <c:if test="${not empty favorites}">
                            <cti:msg2 var="removedText" key=".favorites.removed"/>
                            <cti:msg2 var="undoText" key="yukon.common.undo"/>

                            <c:forEach var="module" items="${favorites}">
                                        <c:if test="${favorites.size() > 1}">
                                            <h5><cti:msg2 key="${module.key}"/></h5>
                                        </c:if>
                                        <ul class="simple-list stacked favorites" data-removed-text="${removedText}" data-undo-text="${undoText}">
                                            <c:forEach items="${module.value}" var="favorite">
                                                <li class="favorite">

                                                    <cti:button classes="b-favorite remove" nameKey="favorite" renderMode="image" icon="icon-star" 
                                                        data-name="${favorite.name}" 
                                                        data-module="${favorite.module}" 
                                                        data-path="${favorite.path}" 
                                                        data-label-args="${favorite.labelArgs}" 
                                                        data-header="${fn:escapeXml(favorite.header)}"/>

                                                    <a href="${favorite.page.path}">${fn:escapeXml(favorite.header)}</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                            </c:forEach>
                    </c:if>
                </cti:tabbedContentSelectorContent>
                <cti:msg2 var="historyTab" key=".history" />
                <cti:tabbedContentSelectorContent selectorName="${historyTab}">
                    <c:if test="${empty history}">
                        <span class="empty-list"><i:inline key=".history.emptyList" /></span>
                    </c:if>
                    <c:if test="${not empty history}">
                        <ol class="simple-list">
                            <c:forEach items="${history}" var="historyElem">
                                <li><a href="${historyElem.path}">${fn:escapeXml(historyElem.header)}</a></li>
                            </c:forEach>
                        </ol>
                    </c:if>
                </cti:tabbedContentSelectorContent>
            </cti:tabbedContentSelector>
        </div>
        <div class="column two nogutter">
            <cti:checkRolesAndProperties value="JAVA_WEB_START_LAUNCHER_ENABLED">
                <cti:msgScope paths="yukon.web.layout.standard">
                    <tags:sectionContainer2 nameKey="applications">
                            <%@ include file="../jws/applications.jsp" %>
                    </tags:sectionContainer2>
                </cti:msgScope>
            </cti:checkRolesAndProperties>
        </div>
    </div>

    <div class="column_12_12 clear">
        <div class="column one">
            <tags:widget bean="subscribedMonitorsWidget" container="section"/>
        </div>
    </div>
</cti:standardPage>