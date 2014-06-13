<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="ecobee.details">

<style>
.progress-ecobee {
    display: inline-block;
    margin-bottom: -4px;
    width: 80px; 
}
#downloads-table tbody tr {
    transition: opacity .7s ease-in-out;
}
</style>

<dt:pickerIncludes/>
<cti:includeScript link="/JavaScript/yukon.picker.js"/>
<cti:includeScript link="/JavaScript/yukon.dialog.js"/>
<cti:includeScript link="/JavaScript/yukon.tables.js"/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/JavaScript/yukon.dr.ecobee.js"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="dataDownloads" id="data-downloads">
                <table class="name-value-table with-form-controls id="downloads-table"
                    ${fn:length(downloads) <= 0 ? 'dn' : ''}">
                    <thead></thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${downloads}" var="entry">
                            <c:set var="key" value="${entry.key}"/>
                            <c:set var="download" value="${entry.value}"/>
                            <%@ include file="download.row.jsp" %>
                    </c:forEach>
                    </tbody>
                </table>
                <span class="empty-list ${fn:length(downloads) > 0 ? 'dn' : ''}"><i:inline key=".download.none"/></span>
                <div class="action-area">
                    <cti:button nameKey="download" popup="#ecobee-download-popup" icon="icon-page-white-excel"/>
                    <div dialog 
                        id="ecobee-download-popup"
                        data-url="ecobee/download/settings" 
                        data-load-event="yukon.dr.ecobee.download.settings.load"
                        data-event="yukon.dr.ecobee.download.start" 
                        data-width="600" 
                        data-title="<cti:msg2 key=".download.title"/>" 
                        class="dn"></div>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter" id="sync-issues" data-report-id="${report.reportId}">
            <tags:sectionContainer2 nameKey="issues" arguments="${fn:length(report.errors)}">
                <c:choose>
                    <c:when test="${fn:length(report.errors) > 0}">
                <div class="scroll-large">
                    <table class="compact-results-table with-form-controls separated no-stripes"
                        data-report-id="${report.reportId}">
                        <thead></thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${report.errors}" var="issue">
                                <tr>
                                    <td>
                                        <i:inline key="${issue.errorType.formatKey}" arguments="${issue.arguments}"/>
                                    </td>
                                    <td>
                                        <c:if test="${issue.errorType.fixable}">
                                            <cti:msg2 var="explanation" key="${issue.errorType.detailKey}"
                                                arguments="${issue.arguments}"/>
                                            <cti:button popup="#ecobee-fix" renderMode="buttonImage" classes="fr M0"
                                                icon="icon-wrench" data-error-id="${issue.errorId}"
                                                data-explanation="${fn:escapeXml(explanation)}" busy="true"/>
                                        </c:if>
                                        <c:if test="${not issue.errorType.fixable}">
                                            <cti:msg2 var="explanation" key="${issue.errorType.detailKey}"
                                                arguments="${issue.arguments}"/>
                                            <cti:button popup="#ecobee-unfixable" renderMode="buttonImage" classes="fr M0"
                                                icon="icon-error" data-error-id="${issue.errorId}"
                                                data-explanation="${fn:escapeXml(explanation)}"/>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="action-area">
                    <cti:button nameKey="issues.fixAll" icon="icon-wrench" id="fix-all-btn"/>
                </div>
                <div dialog
                    id="ecobee-fix"
                    data-form
                    data-width="400"
                    data-event="yukon.dr.ecobee.fix" 
                    data-title="<cti:msg2 key=".issues.fix.title"/>" 
                    data-load-event="yukon.dr.ecobee.fix.init"
                    class="dn">
                    <form action="ecobee/fixIssue" method="POST">
                        <cti:csrfToken/>
                        <p id="ecobee-issue-explanation"></p>
                        <input type="hidden" name="reportId" id="ecobee-report-id">
                        <input type="hidden" name="errorId" id="ecobee-error-id">
                    </form>
                </div>
                <div
                    id="ecobee-unfixable" 
                    data-width="400"
                    data-title="<cti:msg2 key=".issues.unfixable.title"/>" 
                    data-load-event="yukon.dr.ecobee.fix.init"
                    class="dn">
                    <p id="ecobee-unfixable-explanation"></p>
                </div>
                    </c:when>
                    <c:otherwise>
                        <span class="empty-list"><i:inline key=".issues.none"/></span>
                    </c:otherwise>
                </c:choose>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:standardPage>