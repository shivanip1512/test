<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="cc.user.overview">

    <cti:tabs>
        <cti:msg2 var="eventsTabName" key=".events"/>
        <cti:tab title="${eventsTabName}" selected="true">
            <tags:sectionContainer2 nameKey="currentEvents">
                <c:set var="events" value="${currentEvents}"/>
                <%@ include file="eventTable.jsp" %>
            </tags:sectionContainer2>
            
            <tags:sectionContainer2 nameKey="pendingEvents">
                <c:set var="events" value="${pendingEvents}"/>
                <%@ include file="eventTable.jsp" %>
            </tags:sectionContainer2>
            
            <tags:sectionContainer2 nameKey="recentEvents">
                <c:set var="events" value="${recentEvents}"/>
                <%@ include file="eventTable.jsp" %>
            </tags:sectionContainer2>
        </cti:tab>
        
        <cti:msg2 var="trendsTabName" key=".trends"/>
        <cti:tab title="${trendsTabName}" selected="${showTrends}">
            <div id="label-json" class="dn">${fn:escapeXml(labels)}</div>
            <div class="column-7-17 clearfix">
                <div class="column one">
                    <div class="side-nav trend-list">
                        <ul>
                            <c:forEach var="trend" items="${trends}">
                                <c:if test="${trendId == trend.graphDefinitionID}"><c:set var="selected" value="selected"/></c:if>
                                <c:if test="${trendId != trend.graphDefinitionID}"><c:set var="selected" value=""/></c:if>
                                <li data-graph-id="${trend.graphDefinitionID}" class="${selected}">
                                    <cti:url var="trendUrl" value="/dr/cc/user/overview">
                                        <cti:param name="trendId" value="${trend.graphDefinitionID}"/>
                                    </cti:url>
                                    <a href="${trendUrl}">${fn:escapeXml(trend.name)}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
                
                <div class="column two nogutter trend-container pr" data-trend="${trendId}">
                    <div class="glass"></div>
                </div>
            </div>
        </cti:tab>
    </cti:tabs>
    
    <cti:includeScript link="/resources/js/common/yukon.trends.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.tools.trends.js"/>
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="HIGH_STOCK_NO_DATA"/>
    
</cti:standardPage>