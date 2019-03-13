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
#downloads-table tbody tr, #issues-list tbody tr {
    transition: opacity .7s ease-in-out;
}
</style>

<dt:pickerIncludes/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/resources/js/pages/yukon.dr.ecobee.js"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="dataDownloads" id="data-downloads">
                <table class="js-data-downloads name-value-table with-form-controls ${fn:length(downloads) <= 0 ? 'dn' : ''}" id="downloads-table">
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
                    <cti:button nameKey="initiateDownload" data-popup="#ecobee-download-popup" classes="js-download"/>
                    <div data-dialog 
                        id="ecobee-download-popup"
                        data-url="ecobee/download/settings" 
                        data-load-event="yukon_dr_ecobee_download_settings_load"
                        data-event="yukon_dr_ecobee_download_start" 
                        data-width="600" 
                        data-title="<cti:msg2 key=".download.title"/>" 
                        class="dn"></div>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter" id="sync-issues" data-report-id="${report.reportId}" data-empty-key="<cti:msg2 key=".issues.none"/>">
            <tags:sectionContainer2 nameKey="issues" arguments="${fn:length(report.errors)}">
                <c:choose>
                    <c:when test="${fn:length(report.errors) > 0}">
                        <div class="scroll-lg">
                            <table class="compact-results-table with-form-controls separated no-stripes"
                                id="issues-list">
                                <thead></thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach items="${report.errors}" var="issue">
                                        <cti:msg2 var="explanation" key="${issue.errorType.detailKey}" 
                                            arguments="${issue.arguments}"/>
                                        <tr data-error-id="${issue.errorId}" data-explanation="${fn:escapeXml(explanation)}">
                                            <td><i:inline key="${issue.errorType.formatKey}" arguments="${issue.arguments}"/></td>
                                            <td>
                                                <c:if test="${issue.errorType.fixable}">
                                                    <cti:button renderMode="buttonImage" data-popup="#ecobee-fixable" classes="fr M0 js-fixable-issue" 
                                                        icon="icon-wrench"/>
                                                </c:if>
                                                <c:if test="${not issue.errorType.fixable}">
                                                    <cti:button renderMode="buttonImage" classes="fr M0 js-unfixable-issue" 
                                                        icon="icon-error"/>
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
                        <div data-dialog 
                            id="ecobee-fixable"
                            data-event="yukon_dr_ecobee_fix_start" 
                            data-width="600" 
                            data-title="<cti:msg2 key=".issues.fix.title"/>" 
                            class="dn">
                            <input type="hidden" name="reportId" id="ecobee-report-id">
                            <input type="hidden" name="errorId" id="ecobee-error-id">
                            <p id="ecobee-issue-explanation"></p>
                        </div>
                        <div id="ecobee-unfixable" title="<cti:msg2 key=".issues.unfixable.title"/>" class="dn">
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