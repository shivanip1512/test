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

<cti:includeScript link="/JavaScript/yukon.picker.js"/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/JavaScript/yukon.dr.ecobee.js"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="queryStats" styleClass="stacked">
                <tags:nameValueContainer2 naturalWidth="false">
                    <c:forEach items="${statsList}" var="stats">
                        <tr>
                            <td class="name"><cti:formatDate type="MONTH_YEAR" value="${stats.month}"/>:</td>
                            <td class="value full-width">
                                <dr:ecobeeStats value="${stats}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="dataDownloads">
                <table class="compact-results-table dashed">
                    <thead>
                        <tr><th>Started</th><th colspan="2"></th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                    <c:forEach items="${downloadsList}" var="download">
                        <tr>
                            <td class="name"><cti:formatDate type="MONTH_DAY_HM" value="${download.startDate}"/>:</td>
                            <c:if test="${download.complete}">
                            <td colspan="2">
                                <span class="success"><cti:msg2 key=".download.finished"/></span>
                                <span>&nbsp;(<cti:formatDate type="MONTH_DAY_HM" value="${download.endDate}"/>)</span>
                            </td>
                            </c:if>
                            <c:if test="${not download.complete}">
                                <td class="half-width">
                                    <div class="progress progress-striped active">
                                        <div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 60.0%">
                                        </div>
                                    </div>
                                </td>
                                <td style="width:54px;">60.0%</td>
                            </c:if>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button nameKey="download" popup="#ecobee-download" icon="icon-page-white-excel"/>
                    <div dialog data-event="ecobeeDownload" id="ecobee-download" data-width="800" data-title="<cti:msg2 key=".dataDownloads.title"/>" class="dn">
                        <form>
                            <cti:csrfToken/>
                            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                                <tags:nameValue2 nameKey=".download.startDate" valueClass="full-width">
                                    <input type="hidden" id="ecobee-start-report-date">
                                    <input type="hidden" id="ecobee-end-report-date">
                                    <dt:dateTime name="ecobeeStartReportDate" value="${oneDayAgo}" maxDate="${now}"/>
                                    <span class="fl" style="margin-right: 5px;"><i:inline key="yukon.common.to"/></span>
                                    <dt:dateTime name="ecobeeEndReportDate" value="${now}" maxDate="${now}"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                            <h3><i:inline key=".loadGroups"/></h3>
                            <div id="loadGroup">
                                <input type="hidden" id="loadGroupId" name="loadGroup">
                                <tags:pickerDialog
                                    type="ecobeeGroupPicker"
                                    id="loadGroupPicker"
                                    linkType="none"
                                    container="loadGroup" 
                                    multiSelectMode="true"
                                    destinationFieldName="loadGroupIds"
                                    destinationFieldId="loadGroupId"
                                    endAction="yukon.dr.ecobee.assignInputs"/>
                            </div>
                        </form>
                    </div>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="issues" arguments="${fn:length(issues)}">
            <div class="scroll-large">
                <table class="compact-results-table dashed with-form-controls">
                    <thead>
                        <tr><th><cti:msg2 key=".issues.type"/></th><th></th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${issues}" var="issue">
                            <tr>
                                <td><i:inline key="${issue.type}"/></td>
                                <td>
                                    <c:if test="${issue.type.deviceIssue}"><cti:msg2 key=".issues.serialNumber"/>&nbsp;${issue.serialNumber}</c:if>
                                    <c:if test="${!issue.type.deviceIssue}"><cti:msg2 key=".issues.group"/>&nbsp;${issue.loadGroupName}</c:if>
                                    <c:if test="${issue.type.fixable}"><cti:button popup="#ecobee-fix" renderMode="buttonImage" classes="fr" icon="icon-wrench"/></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                </div>
                <div class="action-area">
                    <cti:button nameKey="issues.fixAll" icon="icon-wrench"/>
                </div>
                <div dialog 
                    id="ecobee-fix" 
                    data-width="400"
                    data-event="yukon.dr.ecobee.fix" 
                    data-title=<cti:msg2 key=".issues.fix.title"/> 
                    class="dn">
                    
                </div>
            </tags:sectionContainer2>
        </div>
        <cti:dataUpdaterCallback function="yukon.dr.ecobee.updater" initialize="true" value="ECOBEE_READ/0/RECENT_DOWNLOADS"/>
    </div>
</cti:standardPage>