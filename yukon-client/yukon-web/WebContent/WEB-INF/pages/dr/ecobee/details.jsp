<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                        <tr>
                            <td><cti:formatDate type="MONTH_DAY_HM" value="${startDownLoad}"/></td>
                            <td class="half-width">
                                <div class="progress">
                                    <div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 60.0%">
                                    </div>
                                </div>
                            </td>
                            <td style="width:54px;">60.0%</td>
                        </tr>
                        <tr>
                            <td><cti:formatDate type="MONTH_DAY_HM" value="${startDate}"/></td>
                            <td colspan="2">
                            <c:if test="${downLoadFinished}">
                                <span class="success"><cti:msg2 key=".download.finished"/></span>
                                <span>&nbsp;(<cti:formatDate type="MONTH_DAY_HM" value="${endDate}"/>)</span>
                            </c:if>
                            <c:if test="${not downLoadFinished}">
                                <span class="success"><cti:msg2 key=".download.inProgress"/></span>
                                <span>&nbsp;()</span>
                            </c:if>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button nameKey="download" popup="#ecobee-download" icon="icon-page-white-excel"/>
                    <div dialog data-form id="ecobee-download" data-width="800" data-title=<cti:msg2 key=".dataDownloads.title"/> class="dn">
                        <form:form action="ecobee/download" method="POST" commandName="ecobeeDownload">
                            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                                <tags:nameValue2 nameKey=".download.time" rowId="ecobee-download-schedule" valueClass="full-width">
                                    <div class="column-6-18 clearfix stacked">
                                        <div class="column one">
                                            <span class="f-time-label fwb">&nbsp;</span>
                                                <input type="hidden" id="ecobee-download-time" name="ecobeeDownloadTime">
<%--                                            <!-- <tags:hidden path="downloadTime" id="ecobee-download-time"/> -->
 --%>
                                        </div>
                                        <div class="column two nogutter">
                                            <div class="f-time-slider" style="margin-top: 7px;"></div>
                                        </div>
                                    </div>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".loadGroupPicker" rowId="loadGroupPickerContainer">
                                    <div id="loadGroup">
                                        <input type="hidden" id="loadGroupId" name="loadGroupId">
                                        <tags:pickerDialog
                                            type="ecobeeGroupPicker"
                                            id="loadGroupPicker"
                                            linkType="none"
                                            containerDiv="loadGroup" 
                                            multiSelectMode="true"
                                            destinationFieldId="loadGroupId"/>
                                    </div>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </form:form>
                    </div>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter dn">
            <tags:sectionContainer2 nameKey="issues" arguments="4">
                <table class="compact-results-table dashed with-form-controls">
                    <thead>
                        <tr><th>Type</th><th></th></tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach items="${issues}" var="issue">
                            <tr>
                                <td><i:inline key="${issue.type}"/></td>
                                <td>
                                    <c:if test="${issue.type.deviceIssue}"><cti:msg2 key=".issues.serialNumber"/>&nbsp;${issue.serialNumber}</c:if>
                                    <c:if test="${!issue.type.deviceIssue}"><cti:msg2 key=".issues.group"/>&nbsp;${issue.loadGroupName}</c:if>
                                    <c:if test="${issue.type.fixable}"><cti:button renderMode="buttonImage" classes="fr" icon="icon-wrench"/></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:msg2 key=".issues.fixAllButton" var="fixAllButton"/>
                    <cti:button label="${fixAllButton}" icon="icon-wrench"/>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:standardPage>