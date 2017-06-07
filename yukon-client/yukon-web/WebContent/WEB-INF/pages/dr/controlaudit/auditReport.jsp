<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:msgScope paths="modules.dr.home, modules.dr, modules.dr.controlaudit.details">
    <tags:sectionContainer2 nameKey="controlAuditRecentEvents">
        <div class="scroll-lg">
            <table class="compact-results-table row-highlighting has-actions dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".name" /></th>
                        <th><i:inline key=".startTime" /></th>
                        <th><i:inline key=".progress" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${controlAuditSummary}" var="controlAuditSummary">
                        <c:set var="successWidth" value="${controlAuditSummary.eventStats.percentConfirmed * 100}" />
                        <c:set var="failedWidth" value="${controlAuditSummary.eventStats.percentUnknown * 100}" />
                        <tr>
                            <td><span>${controlAuditSummary.programName}</span></td>
                            <td><cti:formatDate type="FULL" value="${controlAuditSummary.startTime}" /></td>
                            <td>
                                <div class="progress" style="width: 80px; float: left;">
                                    <div class="progress-bar progress-bar-success" role="progressbar"
                                        aria-valuenow="${controlAuditSummary.eventStats.percentConfirmed}%"
                                        title="${controlAuditSummary.numConfirmed} <i:inline key=".confirmed" />"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%"></div>
                                    <div class="progress-bar progress-bar-warning" role="progressbar"
                                        aria-valuenow="${controlAuditSummary.eventStats.percentUnknown}%"
                                        title="${controlAuditSummary.numUnknowns} <i:inline key=".unreported" />"
                                        aria-valuemin="0" aria-valuemax="100" style="width: ${failedWidth}%">
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="action-area">
            <a href="<cti:url value="/dr/controlaudit/details"/>"><i:inline key=".details" /></a>
        </div>
    </tags:sectionContainer2>
</cti:msgScope>