<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="widgets.rfBroadcastWidget">
    <c:choose>
        <c:when test="${showRfBroadcastWidget}">
            <div class= "js-rf-broadcast-widget">
                <cti:toJson object="${widgetUpdateDate}" id="js-widget-json-data"/>
                <table class="compact-results-table">
                    <thead>
                        <th><i:inline key="yukon.web.modules.dr.rf.details.eventTime"/></th>
                        <th><i:inline key="yukon.web.modules.dr.rf.details.results"/></th>
                    </thead>
                    <tbody>
                        <c:forEach var="test" varStatus="loop" items="${results}">
                            <cti:formatDate value="${test.timeMessageSent}" type="FULL" var="eventTime"/>
                            <tr>
                            <c:choose>
                                <c:when test="${loop.index == 0}">
                                    <td title="${eventTime}"><i:inline key=".lastRun"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td title="${eventTime}"><cti:formatDate type="DATE" value="${test.timeMessageSent}"/></td>
                                </c:otherwise>
                            </c:choose>
                                <td colspan="2">
                                    <dr:rfPerformanceStats test="${test.eventStats}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="PT20">
                    <cti:url value="/dr/rf/details" var="detailsUrl"/>
                    <a href="${detailsUrl}" target="_blank"><i:inline key="yukon.common.viewDetails"/></a>
                    <span class="fr">
                        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
                        <span class="fl js-last-updated" style="font-size:11px" title="${lastUpdatedMsg}"></span>
                        <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-update-rf-broadcast"
                                    disabled="true"></cti:button>
                    </span>
                </div>
            </div>
        </c:when>
       <c:otherwise>
           <span class="error"><i:inline key="yukon.web.widgets.notAuthorized"/></span>
       </c:otherwise>
    </c:choose>
    <cti:includeScript link="/resources/js/widgets/yukon.widget.rfBroadcast.js"/>
</cti:msgScope>