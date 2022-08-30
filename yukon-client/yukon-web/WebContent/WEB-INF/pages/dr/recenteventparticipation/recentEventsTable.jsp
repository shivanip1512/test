<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.dr.recentEventParticipation.details">

    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${totalEvents}</span>&nbsp;<i:inline key=".numEvents"/>

    <table class="compact-results-table has-alerts has-actions row-highlighting">
        <thead>
            <tr>
                <th nowrap="nowrap"><i:inline key=".externalEventID" /></th>
                <th><i:inline key=".program" /></th>
                <th><i:inline key=".group" /></th>
                <th><i:inline key=".startTime" /></th>
                <th><i:inline key=".progress" /></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${recentEventParticipationStatsResults.resultList}" var="recentEventParticipationStats">
                <c:set var="successWidth" value="${recentEventParticipationStats.eventStats.percentConfirmed * 100}" />
                <c:set var="successAfterRetryWidth" value="${recentEventParticipationStats.eventStats.percentSuccessAfterRetry * 100}" />
                <c:set var="failedWidth" value="${recentEventParticipationStats.eventStats.percentFailed * 100}" />
                <c:set var="unknownWidth" value="${recentEventParticipationStats.eventStats.percentUnknown * 100}" />
                <tr>
                    <td><span>${recentEventParticipationStats.externalEventId}</span></td>
                    <td><span>${recentEventParticipationStats.programName}</span></td>
                    <td><span>${recentEventParticipationStats.groupName}</span></td>
                    <td><cti:formatDate type="FULL" value="${recentEventParticipationStats.startTime}" /></td>
                    <td>
                        <div class="progress" style="width: 80px; float: left;">
                            <div class="progress-bar progress-bar-success" role="progressbar"
                                aria-valuenow="${recentEventParticipationStats.eventStats.percentConfirmed}%" aria-valuemin="0"
                                title="<i:inline key=".confirmed" arguments="${recentEventParticipationStats.numConfirmed}"/>"
                                aria-valuemax="100" style="width: ${successWidth}%"></div>
                            <div class="progress-bar progress-bar-info" role="progressbar"
                                aria-valuenow="${recentEventParticipationStats.eventStats.percentSuccessAfterRetry}%" aria-valuemin="0"
                                title="<i:inline key=".confirmedAfterRetry" arguments="${recentEventParticipationStats.numConfirmedAfterRetry}"/>"
                                aria-valuemax="100" style="width: ${successAfterRetryWidth}%"></div>
                            <div class="progress-bar progress-bar-warning" role="progressbar"
                                aria-valuenow="${recentEventParticipationStats.eventStats.percentFailed}%" aria-valuemin="0"
                                title="<i:inline key=".failed" arguments="${recentEventParticipationStats.numFailed}"/>" aria-valuemax="100"
                                style="width: ${failedWidth}%"></div>
                            <div class="progress-bar progress-bar-unknown" role="progressbar"
                                aria-valuenow="${recentEventParticipationStats.eventStats.percentUnknown}%" aria-valuemin="0"
                                title="<i:inline key=".unreported" arguments="${recentEventParticipationStats.numUnknowns}" />"
                                aria-valuemax="100" style="width: ${unknownWidth}%"></div>
                        </div>
                    </td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cti:url var="downloadUrl"
                                value="/dr/recenteventparticipation/download?eventId=${recentEventParticipationStats.controlEventId}" />
                            <cm:dropdownOption icon="icon-download" key=".download" href="${downloadUrl}" />
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot></tfoot>
    </table>
    <tags:pagingResultsControls result="${recentEventParticipationStatsResults}" adjustPageCount="true" />

</cti:msgScope>