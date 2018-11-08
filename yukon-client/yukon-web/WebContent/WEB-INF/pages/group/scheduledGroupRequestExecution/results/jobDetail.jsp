<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="schedules.VIEW" module="tools">

<div class="dn js-running-warning warning">
    <i:inline key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.runningWarning"/>
</div>

<cti:msg var="pageTitle" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.pageTitle" />
<cti:msg var="infoSectionText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.section" />
<cti:msg var="scheduleNameText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.scheduleName" />
<cti:msg var="jobTypeText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.jobType" />
<cti:msg var="creTypeText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.creType" />
<cti:msg var="scheduleStatusText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.status" />
<cti:msg var="attributeText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.attribute" />
<cti:msg var="commandText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.command" />
<cti:msg var="scheduleDescriptionText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.scheduleDescription" />
<cti:msg var="lastRunText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.lastRun" />
<cti:msg var="lastResultsText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.lastResults" />
<cti:msg var="lastResultsSuccessText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.lastResults.success" />
<cti:msg var="lastResultsMissedText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.lastResults.missed" />
<cti:msg var="lastResultsTotalText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.lastResults.total" />
<cti:msg var="deviceGroupText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.deviceGroup" />
<cti:msg var="nextRunText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.nextRun" />
<cti:msg var="nextRunDisabledText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.nextRunDisabled" />
<cti:msg var="userText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.user" />
<cti:msg var="executionsText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.executions" />
<cti:msg var="executionsButtonText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.executionsButton" />
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.popInfo.deviceGroup" />
<cti:msg var="editScheduleButtonText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.editScheduleButton" />
<cti:msg var="queuedRetryCountText" key="yukon.common.device.schedules.home.retry.queuedRetryCount"/>
<cti:msg var="nonQueuedRetryCountText" key="yukon.common.device.schedules.home.retry.nonQueuedRetryCount"/>
<cti:msg var="maxTotalRunTimeHoursText" key="yukon.common.device.schedules.home.retry.maxTotalRunTimeHours"/>
<cti:msg var="notApplicable" key="yukon.common.na"/>

<c:set var="take20" value="20px" />

<script type="text/javascript">
    function toggleLastRunLink() {
      //assumes data is of type Hash
        return function(data) {
            var lastRunDate = data.value;
            if (lastRunDate !== "${notApplicable}") {
                $('#noLastRunSpan').hide();
                $('#hasLastRunSpan').show();
            } else {
                $('#noLastRunSpan').show();
                $('#hasLastRunSpan').hide();
            }
            enableHyperlinkforLastRun();
        };
    }
    
    function enableDisableEdit() {
        return function (data) {
            var state = data.state;
            
            if (state === 'Running') {
                $('.js-edit-btn').attr('disabled','disabled');
                $('.js-running-warning').removeClass('dn');
            } else {
                $('.js-edit-btn').removeAttr('disabled');
                $('.js-running-warning').addClass('dn');
            }
        };
      }
    
    function enableHyperlinkforLastRun() {
        if($("span.creCountForJob > span").text() > 0) {
            $("a.jobLastRun").removeClass("disabled");
        } else {
            $("a.jobLastRun").addClass("disabled");
        }
    }
    
    $( document ).ready(function() {
        enableHyperlinkforLastRun();
    });
    
</script>
    
<%-- JOB INFO --%>
<tags:sectionContainer title="${infoSectionText}" id="jobInfoSection">
                
    <tags:nameValueContainer>

        <%-- name --%>
        <tags:nameValue name="${scheduleNameText}" nameColumnWidth="160px">
            ${fn:escapeXml(jobWrapper.shortName)}
        </tags:nameValue>
        
        <%-- cre type --%>
        <tags:nameValue name="${creTypeText}">
            ${jobWrapper.commandRequestTypeShortName}
        </tags:nameValue>
        
        <%-- attribute/command --%>
        <c:if test="${not empty jobWrapper.attributes}">
            <tags:nameValue name="${attributeText}">
                <c:forEach items="${jobWrapper.attributes}" var="attribute" >
                    <cti:msg2 key="${attribute.message}" />
                </c:forEach>
            </tags:nameValue>
        </c:if>
        <c:if test="${not empty jobWrapper.command}">
            <tags:nameValue name="${commandText}">${fn:escapeXml(jobWrapper.command)}</tags:nameValue>
        </c:if>

        <%-- device group --%>
        <tags:nameValue name="${deviceGroupText}">
            <c:choose>
                <c:when test="${not empty jobWrapper.deviceGroupName}">
                    <cti:url var="deviceGroupUrl" value="/group/editor/home">
                        <cti:param name="groupName" value="${jobWrapper.deviceGroupName}"/>
                    </cti:url>
                    <a href="${deviceGroupUrl}">
                        ${fn:escapeXml(jobWrapper.deviceGroupName)}
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="error"><i:inline key=".groupDoesNotExist"/></span>
                </c:otherwise>
            </c:choose>
        </tags:nameValue>
        
        <%-- user --%>
        <tags:nameValue name="${userText}">
            ${fn:escapeXml(jobWrapper.job.userContext.yukonUser.username)}
        </tags:nameValue>
        
        <%-- retry setup --%>
        <c:if test="${jobWrapper.retrySetup}">
            <tags:nameValue name="Retry Options">
                
                <table class="compact-results-table">
                    <c:if test="${not empty jobWrapper.queuedRetryCount}">
                    <tr>
                        <td style="width:15px;">${jobWrapper.queuedRetryCount}</td>
                        <td>${queuedRetryCountText}</td>
                    </tr>
                    </c:if>
                    <c:if test="${not empty jobWrapper.nonQueuedRetryCount}">
                    <tr>
                        <td>${jobWrapper.nonQueuedRetryCount}</td>
                        <td>${nonQueuedRetryCountText}</td>
                    </tr>
                    <c:if test="${not empty jobWrapper.stopRetryAfterHoursCount}">
                    <tr>
                        <td>${jobWrapper.stopRetryAfterHoursCount}</td>
                        <td>${maxTotalRunTimeHoursText}</td>
                    </tr>
                    </c:if>
                    </c:if>
                </table>
                
            </tags:nameValue>
        </c:if>
        
        <tags:nameValueGap gapHeight="${take20}"/>
        
        <%-- enabled --%>
        <tags:nameValue name="${scheduleStatusText}">
            <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/STATE_TEXT"/>
            <cti:dataUpdaterCallback function="enableDisableEdit()" initialize="true" state="JOB/${jobWrapper.job.id}/STATE"/>
        </tags:nameValue>
        
        <%-- schedule description --%>
        <tags:nameValue name="${scheduleDescriptionText}">
            <cti:dataUpdaterValue type="JOB" identifier="${jobWrapper.job.id}/SCHEDULE_DESCRIPTION"/>
        </tags:nameValue>
        
        <%-- last run --%>
        <tags:nameValue name="${lastRunText}">
        
            <cti:dataUpdaterCallback function="toggleLastRunLink()"
                value="SCHEDULED_GROUP_REQUEST_EXECUTION/${jobWrapper.job.id}/LAST_RUN_DATE"/>
            
            <c:choose>
                <c:when test="${empty jobWrapper.lastRun}">
                    <c:set var="hasLastRunSpanInitialStyle" value="display:none;"/>
                </c:when>
                <c:otherwise>
                    <c:set var="noLastRunSpanInitialStyle" value="display:none;"/>
                </c:otherwise>
            </c:choose>
        
            <span id="noLastRunSpan" style="${noLastRunSpanInitialStyle}" class="fl">${notApplicable}</span>
            
            <span id="hasLastRunSpan" style="${hasLastRunSpanInitialStyle}" class="fl">            
                 <cti:url var="lastRunUrl" value="/group/scheduledGroupRequestExecutionResults/viewLastRun">
                     <cti:param name="jobId" value="${jobWrapper.job.id}"/>
                 </cti:url>
                 <a href="${lastRunUrl}" class="jobLastRun disabled"><cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION"
                    identifier="${jobWrapper.job.id}/LAST_RUN_DATE"/></a>
            </span>
            
            <tags:helpInfoPopup title="Last Run">
                <cti:msg key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.popInfo.lastRun" />
            </tags:helpInfoPopup>
        
        </tags:nameValue>
        
        <%-- last results --%>
        <tags:nameValue name="${lastResultsText}">
            <amr:scheduledGroupRequestExecutionJobLastRunStats jobId="${jobWrapper.job.id}"
                hasStatsInitially="${not empty jobWrapper.lastRun}"/>
        </tags:nameValue>
        
        <%-- next run --%>
        <tags:nameValue name="${nextRunText}">
        <c:choose>
            <c:when test="${jobWrapper.job.disabled}">
                ${nextRunDisabledText}
            </c:when>
            <c:otherwise>
                <amr:scheduledGroupRequestExecutionJobNextRunDate jobId="${jobWrapper.job.id}"/>
            </c:otherwise>
        </c:choose>
        </tags:nameValue>
        
        <%-- all executions button --%>
        <tags:nameValue name="${executionsText}">
            <form name="viewAllExecutionsForm" action="/common/commandRequestExecutionResults/list" method="get">
                <cti:url var="creListUrl" value="/common/commandRequestExecutionResults/list">
                    <cti:param name="jobId" value="${jobWrapper.job.id}"/>
                </cti:url>
                <a href="${creListUrl}" class="fl">${executionsButtonText}</a> 
                <span class="fl creCountForJob">&nbsp;(<cti:dataUpdaterValue type="SCHEDULED_GROUP_REQUEST_EXECUTION"
                    identifier="${jobWrapper.job.id}/CRE_COUNT_FOR_JOB" />)</span>
                <tags:helpInfoPopup title="${executionsButtonText}">
                	<cti:msg2 key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.info.popInfo.executions" />
            	</tags:helpInfoPopup>
            </form>
        </tags:nameValue> 
    </tags:nameValueContainer>
</tags:sectionContainer>
                
<%-- EDIT --%>
<c:if test="${canManage}">
    <div class="page-action-area">
        <cti:url value="/group/scheduledGroupRequestExecution/home" var="editUrl">
            <cti:param name="editJobId" value="${jobWrapper.job.id}"/>
        </cti:url>
        <cti:button disabled="${jobWrapper.jobStatus eq 'RUNNING'}" classes="js-edit-btn" nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
    </div>
</c:if>

</cti:standardPage>
