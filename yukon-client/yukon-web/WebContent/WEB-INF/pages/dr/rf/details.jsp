<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="rf.details">
    
    <cti:includeScript link="JQUERY_FLOTCHARTS_PIE"/>

    <div class="clearfix column-16-8 stacked">
        <div class="column one">
            <form action="<cti:url value="/dr/rf/details"/>">
                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                    <tags:nameValue2 nameKey=".dateRange">
                        <dt:dateRange startValue="${from}" endValue="${to}" startName="from" endName="to" wrapperClasses="dib fl">
                            <div class="dib fl" style="margin-right: 5px;"><i:inline key="yukon.common.to"/></div>
                        </dt:dateRange>
                        <cti:button nameKey="update" type="submit" busy="true" classes="action primary"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".numTests">${fn:length(tests)}</tags:nameValue2>
                </tags:nameValueContainer2>
            </form>
        </div>
        <div class="column two nogutter">
        </div>
    </div>
    
    <table class="compact-results-table has-actions">
        <thead>
            <tr>
                <th><i:inline key=".eventTime"/></th>
                <th>
                    <i:inline key=".results"/>
                    <div id="rf-event-info" class="dn" data-title="<cti:msg2 key=".rf.details.rfPerformanceResult"/>" data-width="500" data-height="220" style="overflow: hidden;">
                        <i:inline key="yukon.web.modules.dr.home.rfPerformance.infoText"/>
                    </div>
                    <cti:icon icon="icon-information" classes="fn cp" data-popup="#rf-event-info" data-popup-toggle=""/>
                </th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${tests}" var="test">
                <tr>
                    <td><cti:formatDate type="FULL" value="${test.timeMessageSent}"/></td>
                    <td colspan="2">
                        <dr:rfPerformanceStats test="${test.eventStats}"/>
                        <c:if test="${hasStats}">
                            <cm:dropdown triggerClasses="fr" menuClasses="no-icons">
                                <c:choose>
                                    <c:when test = "${test.eventMessage.archived}">
                                        <cm:dropdownOption key=".detailsNotAvailable"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:url var="eventDetailsUrl" value="/dr/rf/broadcast/eventDetail/${test.messageId}"/>
                                        <cm:dropdownOption key=".eventDetails" href="${eventDetailsUrl}"/>
                                    </c:otherwise>
                                </c:choose>
                            </cm:dropdown>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <div id="devices-popup"></div>
</cti:standardPage>