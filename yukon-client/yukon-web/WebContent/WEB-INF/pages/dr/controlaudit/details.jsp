<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlaudit.details">
<script>
$(function () {
    $('#download-btn').click(function (ev) {
    	document.auditReport.action = yukon.url("/dr/controlaudit/details/export");
         document.auditReport.submit();
    });
    $('#update-btn').click(function (ev) {
    	document.auditReport.action = yukon.url("/dr/controlaudit/details");
         document.auditReport.submit();
    });
});
</script>
<form id="auditReport" name="auditReport" action="<cti:url value="/dr/controlaudit/details"/>">
    <div class="clearfix column-16-8 stacked">
        <div class="column one">
            
                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                    <tags:nameValue2 nameKey=".dateRange">
                        <dt:dateRange startValue="${from}" endValue="${to}" startName="from" endName="to"
                            wrapperClasses="dib fl">
                            <div class="dib fl" style="margin-right: 5px;">
                                <i:inline key="yukon.common.to" />
                            </div>
                        </dt:dateRange>
                        <cti:button id="update-btn" nameKey="update" type="submit" busy="true" classes="action primary" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".numTests">${fn:length(auditEventMessageStats.resultList)}</tags:nameValue2>
                </tags:nameValueContainer2>
            <!-- </form> -->
        </div>
        <div class="column two nogutter">
        <div class="action-area">
        <c:if test="${not empty auditEventMessageStats}">
        </c:if>
                <c:choose>
                    <c:when test="${maxCsvRows > searchResult.hitCount}">
                        <cti:button nameKey="download" href="${csvLink}" icon="icon-page-excel"/>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="download" id="download-btn" icon="icon-page-excel"/> 
                        <d:confirm on="#download-btn" nameKey="confirmExport"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    </form>

    <table class="compact-results-table row-highlighting has-actions dashed">
        <thead>
            <tr>
                <th><i:inline key=".eventID" /></th>
                <th><i:inline key=".program" /></th>
                <th><i:inline key=".group" /></th>
                <th><i:inline key=".startTime" /></th>
                <th><i:inline key=".progress" /></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${auditEventMessageStats.resultList}" var="auditEventMessageStat">
                <c:set var="successWidth" value="${auditEventMessageStat.eventStats.percentConfirmed * 100}" />
                <c:set var="failedWidth" value="${auditEventMessageStat.eventStats.percentUnknown * 100}" />
                <tr>
                    <td><span>${auditEventMessageStat.controlEventId}</span></td>
                    <td><span>${auditEventMessageStat.programName}</span></td>
                    <td><span>${auditEventMessageStat.groupName}</span></td>
                    <td><cti:formatDate type="FULL" value="${auditEventMessageStat.startTime}" /></td>
                    <td>
                        <div class="progress" style="width: 80px; float: left;">
                            <div class="progress-bar progress-bar-success" role="progressbar"
                                aria-valuenow="${auditEventMessageStat.eventStats.percentConfirmed}%" aria-valuemin="0" 
                                title="${auditEventMessageStat.numConfirmed} <i:inline key=".confirmed" />"
                                aria-valuemax="100" style="width: ${successWidth}%"></div>
                            <div class="progress-bar progress-bar-warning" role="progressbar"
                                aria-valuenow="${auditEventMessageStat.eventStats.percentUnknown}%" aria-valuemin="0" 
                                title="${auditEventMessageStat.numUnknowns} <i:inline key=".unreported" />"
                                aria-valuemax="100" style="width: ${failedWidth}%"></div>
                        </div> 
                    </td>
                    <td>
                    <cm:dropdown triggerClasses="fr" menuClasses="no-icons">
                            <cti:url var="mapNetworkUrl" value="/dr/controlaudit/download?eventId=${auditEventMessageStat.controlEventId}"/>
                            <cm:dropdownOption icon="icon-download" key=".download" href="${mapNetworkUrl}"/>
                    </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
<tags:pagingResultsControls result="${auditEventMessageStats}" adjustPageCount="true"/>
</cti:standardPage>