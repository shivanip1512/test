<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="widgets.rfBroadcastWidget">
<div class= "js-rf-broadcast-widget">
<input type="hidden" id="js-refresh-tooltip" value="${refreshTooltip}"/>
<input type="hidden" id="js-update-tooltip" value="${updateTooltip}"/>
<input type="hidden" id="js-force-update-interval" value="${forceRefreshInterval}"/>
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key="yukon.web.modules.dr.rf.details.eventTime"/></th>
                <th><i:inline key="yukon.web.modules.dr.rf.details.results"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="test" varStatus="loop" items="${results}">
                <tr>
                <c:choose>
                    <c:when test="${loop.index == 0}">
                        <td><i:inline key=".lastRun"/></td>
                    </c:when>
                    <c:otherwise>
                        <td><cti:formatDate type="DATE" value="${test.timeMessageSent}"/></td>
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
        <a href="<cti:url value="/dr/rf/details"/>"><i:inline key=".viewDetails"/></a>
        <span class="fr">
            <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
            <span class="fl" style="font-size:11px" title="${lastUpdatedMsg}"><cti:formatDate value="${lastAttemptedRefresh}" type="DATEHMS_12"/></span>
            <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-update-rf-broadcast"/>
        </span>
    </div>
</div>
<cti:includeScript link="/resources/js/widgets/yukon.widget.rfBroadcast.js"/>
</cti:msgScope>