<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.dr.recentEventParticipation.details">


    <table class="compact-results-table row-highlighting stacked has-alerts">
        <thead>
            <tr>
                <th nowrap="nowrap"><i:inline key=".eventID" /></th>
                <th><i:inline key=".program" /></th>
                <th><i:inline key=".group" /></th>
                <th><i:inline key=".startTime" /></th>
                <th><i:inline key=".progress" /></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
            </tr>
        </thead>
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
                            <cti:url var="mapNetworkUrl" value="/dr/recenteventparticipation/download?eventId=${auditEventMessageStat.controlEventId}"/>
                            <cm:dropdownOption icon="icon-download" key=".download" href="${mapNetworkUrl}"/>
                    </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot></tfoot>
    </table>
    <tags:pagingResultsControls result="${auditEventMessageStats}" adjustPageCount="true" />

</cti:msgScope>