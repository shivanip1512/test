<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="ecobee.details">
<cti:includeScript link="/JavaScript/yukon.picker.js"/>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/JavaScript/yukon.dr.ecobee.js"/>

    <script type="text/javascript">
        $(function () {
            try {
                loadGroupPicker.show.call(loadGroupPicker, true);
            } catch (pickerException) {
                debug.log('pickerException: ' + pickerException);
            };
        });
    </script>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="queryStats" styleClass="stacked">
                <tags:nameValueContainer2 naturalWidth="false">
                        <c:forEach items="${ecobeeStatsList}" var="ecobeeStat">
                        <tr>
                            <td class="name">${ecobeeStat.monthYearStr}:</td>
                            <td class="value full-width">
                                <div class="progress query-statistics" style="width: 80px;float:left;"
                                    data-query-counts='{ "currentMonthDataCollectionCount": "${ecobeeStat.dataCollectionCount}",
                                        "currentMonthDemandResponseCount": "${ecobeeStat.demandResponseCount}",
                                        "currentMonthSystemCount": "${ecobeeStat.systemCount}" }'>
                                    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 27.0%"></div>
                                    <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 50.0%"></div>
                                    <div class="progress-bar progress-bar-default" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 23.0%"></div>
                                </div>
                                <div class="fl query-counts" style="margin-left: 10px;" title="<cti:msg2 key=".ecobee.details.statistics.title"/>">
                                    <span class="query-total" style="margin-right: 10px;width:48px;display: inline-block;">42</span>
                                    <span class="label label-success">${ecobeeStat.demandResponseCount}</span>
                                    <span class="label label-info">${ecobeeStat.dataCollectionCount}</span>
                                    <span class="label label-default">${ecobeeStat.systemCount}</span>
                                </div>
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
                            <td>2014-04-09 11:32 AM</td>
                            <td class="half-width">
                                <div class="progress"><div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 60.0%"></div></div>
                            </td>
                            <td style="width:54px;">60.0%</td>
                        </tr>
                        <tr>
                            <td>2014-04-06 10:02 AM</td>
                            <td colspan="2">
                                <span class="success">Finished</span><span>&nbsp;(2014-04-06 10:02 AM)</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button nameKey="download" popup="#ecobee-download" icon="icon-page-white-excel"/>
                    <div dialog data-form id="ecobee-download" data-width="800" data-title="Download Stuff" class="dn">
                        <form:form action="ecobee/download" method="POST" commandName="ecobeeDownload">
                            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                                <tags:nameValue2 nameKey=".ecobee.details.download.time" rowId="ecobee-download-schedule" valueClass="full-width">
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
                                <tags:nameValue2 nameKey="yukon.web.modules.dr.ecobee.details.loadGroupPicker" rowId="loadGroupPickerContainer"/>
                                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
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
                                </tags:nameValueContainer2>
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
                                <td><cti:msg2 key="${issue.type}"/></td>
                                <td>
                                    <c:if test="${issue.type.deviceIssue}">SN: ${issue.serialNumber}</c:if>
                                    <c:if test="${!issue.type.deviceIssue}">Group: ${issue.loadGroupName}</c:if>
                                    <c:if test="${issue.type.fixable}"><cti:button renderMode="buttonImage" classes="fr" icon="icon-wrench"/></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button label="Fix All" icon="icon-wrench"/>
                </div>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:standardPage>