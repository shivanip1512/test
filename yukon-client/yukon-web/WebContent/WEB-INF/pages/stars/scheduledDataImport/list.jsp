<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="scheduledDataImportList">
    <div id="errorMessages" class="error"></div>
    <tags:sectionContainer2 nameKey="tableTitle">
        <div id="page-actions" class="dn">
            <cti:url var="createUrl" value="/stars/scheduledDataImport/create"/>
            <cm:dropdownOption icon="icon-plus-green" key=".create" id="create-option" href= "${createUrl}"/>
            <cm:dropdownOption icon="icon-email" key=".notification"/>
        </div>
        <cti:url var="listUrl" value="/stars/scheduledDataImport/list"/>
        <c:choose>
            <c:when test="${empty filterResult.resultList}">
                <span class="empty-list">
                    <i:inline key="yukon.common.search.noResultsFound"/>
                </span>
            </c:when>
            <c:otherwise>
                <div data-url="${listUrl}" data-static>
                    <table id="jobs-table" class="compact-results-table has-actions">
                        <thead>
                            <tags:sort column="${NAME}"/>
                            <tags:sort column="${TYPE}"/>
                            <tags:sort column="${SCHEDULE}"/>
                            <tags:sort column="${NEXT_RUN}"/>
                            <tags:sort column="${STATUS}"/>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </thead>
                        <body>
                            <c:forEach var="jobWrapper" items="${filterResult.resultList}">
                                <c:set var="jobId" value="${jobWrapper.job.id}"/>
                                <cti:msg2 var="rowTitle" key=".jobID" argument="${jobId}"/>
                                <tr id="tr_${jobId}" title="${rowTitle}" data-job-id="${jobId}">
                                <%-- Schedule Name --%>
                                    <td>
                                        <cti:url var="jobDetailUrl" value="/stars/scheduledDataImport/${jobId}/view"/>
                                        <a href="${jobDetailUrl}">${fn:escapeXml(jobWrapper.shortName)}</a>
                                    </td>
                                    <%-- Import Type --%>
                                    <cti:msg2 var="importType" key="${jobWrapper.importType}"/>
                                    <td>${importType}</td>
                                    <%-- Schedule --%>
                                    <td>${jobWrapper.scheduleDescription}</td>
                                    <td class="nextRunDate">
                                        <cti:dataUpdaterValue type="JOB" identifier="${jobId}/NEXT_RUN_DATE"/>
                                    </td>
                                    <%-- Status --%>
                                    <td id="status_${jobId}">
                                        <span id="jobNotRunningSpan_${jobId}"
                                            <c:if test="${jobWrapper.jobState eq 'RUNNING'}">style="display:none;"</c:if>>
                                            <cti:dataUpdaterValue type="JOB" identifier="${jobId}/STATE_TEXT"/>
                                        </span>
                                        <div id="jobRunningSpan_${jobId}"
                                            <c:if test="${not (jobWrapper.jobState eq 'RUNNING')}">style="display:none;"</c:if> class="wsnw">
                                        </div>
                                    </td>
                                    <td class="tar">
                                        <cm:dropdown icon="icon-cog">
                                            <cm:dropdownOption id="start-schedule-${jobId}" classes="js-schedule-start-now" key=".start" data-job-id="${jobId}" icon="icon-bullet-go"/>
                                            <cm:dropdownOption id="view-history-${jobId}" key="yukon.web.components.button.history.label" icon="icon-script" />
                                            <cm:dropdownOption id="delete-schedule-btn-${jobId}" data-ok-event="yukon:schedule:delete" data-job-id="${jobId}" key=".delete" icon="icon-cross" classes="js-hide-dropdown"/>
                                            <d:confirm on="#delete-schedule-btn-${jobId}" nameKey="deleteConfirm" argument="${jobWrapper.name}"/>
                                            <cm:dropdownOption id="disable-schedule-${jobId}" key="yukon.common.disable" classes="js-schedule-toggle" data-job-id="${jobId}" icon="icon-delete"/>
                                            <cm:dropdownOption id="enable-schedule-${jobId}" key="yukon.common.enable" classes="js-schedule-toggle" data-job-id="${jobId}" icon="icon-accept"/>
                                        </cm:dropdown>
                                    </td>
                                </tr>
                                <span>
                                    <cti:dataUpdaterCallback function="yukon.assets.scheduleddataimport.setTrClassByJobState(${jobId})" initialize="true" state="JOB/${jobId}/STATE"/>
                                </span>
                            </c:forEach>
                        </body>
                    </table>
                    <tags:pagingResultsControls result="${filterResult}" hundreds="true" adjustPageCount="true"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer2>
    <cti:includeScript link="/resources/js/pages/yukon.assets.scheduleddataimport.js" />
</cti:standardPage>