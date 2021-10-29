<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:msgScope paths="modules.dr.home, modules.dr, modules.dr.recentEventParticipation.details">
    <tags:sectionContainer2 nameKey="recentEventParticipation">
    <c:if test="${empty recentEventParticipationSummary}">
        <span class="empty-list"><i:inline key=".noRecentEvents"/></span>
    </c:if>
    <c:if test="${!empty recentEventParticipationSummary}">
        <div class="scroll-lg">
            <table class="compact-results-table row-highlighting dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".name" /></th>
                        <th><i:inline key=".startTime" /></th>
                        <th><i:inline key=".progress" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${recentEventParticipationSummary}" var="recentEventParticipationSummary">
                        <c:set var="successWidth" value="${recentEventParticipationSummary.eventStats.percentConfirmed * 100}" />
                        <c:set var="successAfterRetryWidth" value="${recentEventParticipationSummary.eventStats.percentSuccessAfterRetry * 100}" />
                        <c:set var="failedWidth" value="${recentEventParticipationSummary.eventStats.percentFailed * 100}" />
                        <c:set var="unknownWidth" value="${recentEventParticipationSummary.eventStats.percentUnknown * 100}" />
                        <tr>
                            <td><span>${recentEventParticipationSummary.programName}</span></td>
                            <td><cti:formatDate type="FULL" value="${recentEventParticipationSummary.startTime}" /></td>
                            <td>
                                <div class="progress" style="width: 80px; float: left;">
                                    <div class="progress-bar progress-bar-success" role="progressbar"
                                        aria-valuenow="${recentEventParticipationSummary.eventStats.percentConfirmed}%"
                                        title="<i:inline key=".confirmed" arguments="${recentEventParticipationSummary.numConfirmed}"/>"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%"></div>
                                    <div class="progress-bar progress-bar-info" role="progressbar"
                                        aria-valuenow="${recentEventParticipationSummary.eventStats.percentSuccessAfterRetry}%"
                                        title="<i:inline key=".confirmedAfterRetry" arguments="${recentEventParticipationSummary.numConfirmedAfterRetry}"/>"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${successAfterRetryWidth}%"></div>
                                    <div class="progress-bar progress-bar-warning" role="progressbar"
                                        aria-valuenow="${recentEventParticipationSummary.eventStats.percentFailed}%"
                                        title="<i:inline key=".failed" arguments="${recentEventParticipationSummary.numFailed}" />"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${failedWidth}%"></div>
                                    <div class="progress-bar progress-bar-unknown" role="progressbar"
                                        aria-valuenow="${recentEventParticipationSummary.eventStats.percentUnknown}%"
                                        title="<i:inline key=".unreported" arguments="${recentEventParticipationSummary.numUnknowns}" />"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${unknownWidth}%">
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="action-area">
            <a href="<cti:url value="/dr/recenteventparticipation/details"/>"><i:inline key=".details" /></a>
        </div>
    </c:if>
    </tags:sectionContainer2>
</cti:msgScope>