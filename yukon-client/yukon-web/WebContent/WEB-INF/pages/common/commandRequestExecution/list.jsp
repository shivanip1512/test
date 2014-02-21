<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.commandRequestExecution.results.list.pageTitle" />
<cti:msg var="filterSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.section" />
<cti:msg var="filerDateFromText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.dateFrom" />
<cti:msg var="filerDateToText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.dateTo" />
<cti:msg var="filterTypeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.type" />
<cti:msg var="filterTypeAnyText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.typeAny" />
<cti:msg var="filterButtonText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.button" />
<cti:msg var="filterClearText" key="yukon.web.modules.amr.commandRequestExecution.results.list.filter.clear" />
<cti:msg var="executionsSectionText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.section" />
<cti:msg var="executionsTypeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.type" />
<cti:msg var="executionsStatusText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.status" />
<cti:msg var="executionsStartTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.startTime" />
<cti:msg var="executionsStopTimeText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.stopTime" />
<cti:msg var="successCountText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.successCount" />
<cti:msg var="unsupportedCountText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.unsupportedCount" />
<cti:msg var="failCountText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.failCount" />
<cti:msg var="totalCountText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.totalCount" />
<cti:msg var="executionsUserText" key="yukon.web.modules.amr.commandRequestExecution.results.list.executions.tableHeader.user" />
<cti:msg var="noExecutionsText" key="yukon.web.modules.amr.commandRequestExecution.results.list.noExecutions" />
    
<cti:standardPage title="${pageTitle}" module="amr">
<cti:msgScope paths="yukon.web.modules.amr.commandRequestExecution.results.list">

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Home" />
        <%-- cres --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
    </cti:breadCrumbs>
    
    <script type="text/javascript">
        function forwardToCreDetail(row, id) {
            window.location = "/common/commandRequestExecutionResults/detail?commandRequestExecutionId=" + id + "&jobId=" + ${jobId};
        }
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <c:set var="singleJob" value="false"/>
    <c:if test="${jobId > 0}">
        <c:set var="singleJob" value="true"/>
    </c:if>
    
    <c:if test="${singleJob}">
        <tags:nameValueContainer tableClass="stacked">
            <tags:nameValue name="${executionsTypeText}" nameColumnWidth="60px">${singleJobType}</tags:nameValue>
        </tags:nameValueContainer>
    </c:if>
    
    <%-- FILTER POPUP --%>
    <tags:simplePopup id="filterPopup" title="${filterSectionText}" on="#filterButton">
    
        <form name="filterForm" id="filterForm" action="/common/commandRequestExecutionResults/list" method="get">
        
            <c:if test="${not empty error}">
                <div class="error">${error}</div><br>
            </c:if>
            
            <input type="hidden" name="commandRequestExecutionId" value="${commandRequestExecutionId}">
            <input type="hidden" name="jobId" value="${jobId}">
            
            <tags:nameValueContainer>
            
                <tags:nameValue name="${filerDateFromText}" nameColumnWidth="100px">
                    <cti:formatDate var="fromDateStr" type="DATE" value="${fromDate}" nullText=""/>
                    <dt:date name="fromDate" value="${fromDate}" />
                </tags:nameValue>
                
                <tags:nameValue name="${filerDateToText}">
                    <cti:formatDate var="toDateStr" type="DATE" value="${toDate}" nullText=""/>
                    <dt:date name="toDate" value="${toDate}" />
                </tags:nameValue>
                
                <c:if test="${!singleJob}">
                
                    <tags:nameValue name="${filterTypeText}">
                        <select name="typeFilter">
                            <option value="ANY">${filterTypeAnyText}</option>
                            <c:forEach var="commandRequestExecutionType" items="${commandRequestExecutionTypes}">
                                <option value="${commandRequestExecutionType}" title="${commandRequestExecutionType.description}" <c:if test="${typeFilter eq commandRequestExecutionType}">selected</c:if>>${commandRequestExecutionType.shortName}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                
                </c:if>
            
            </tags:nameValueContainer>
            
            <div class="action-area">
                <cti:button label="${filterButtonText}" busy="true" type="submit" classes="primary action"/>
                <cti:url value="/common/commandRequestExecutionResults/list" var="clearUrl"/>
                <cti:button label="${filterClearText}" href="${clearUrl}"/>
            </div>
        </form>
        
    </tags:simplePopup>
    
    
    <%-- RESULTS TABLE --%>
    <div id="f-page-buttons">
        <cti:button nameKey="filter" id="filterButton" icon="icon-filter"/>
    </div>

    <table id="cresTable" class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <c:if test="${!singleJob}">
                    <th>${executionsTypeText}</th>
                </c:if>
                <th>${executionsStartTimeText}</th>
                <th>${executionsStopTimeText}</th>
                <th>${successCountText}</th>
                <th>${failCountText}</th>
                <th>${unsupportedCountText}</th>
                <th>${totalCountText}</th>
                <th>${executionsStatusText}</th>
                <th>${executionsUserText}</th>
        </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:if test="${fn:length(creWrappers) <= 0}">
                <c:set var="colCount" value="7"/>
                <c:if test="${!singleJob}">
                    <c:set var="colCount" value="8"/>
                </c:if>
                <tr>
                    <td colspan="${colCount}" style="text-align:center;" class="subtle">${noExecutionsText}</td>
                </tr>
            </c:if>
        
            <c:forEach var="creWrapper" items="${creWrappers}" varStatus="status">
            
                <tr class="cp" 
                    onclick="forwardToCreDetail(this, ${creWrapper.cre.id})" 
                    title="${creWrapper.cre.commandRequestExecutionType.description} ID: ${creWrapper.cre.id}">
                    
                    <c:if test="${!singleJob}">
                        <td>${creWrapper.cre.commandRequestExecutionType.shortName}</td>
                    </c:if>
                    <td style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${creWrapper.cre.startTime}"/></td>
                    <td style="white-space:nowrap;"><cti:formatDate type="DATEHM" value="${creWrapper.cre.stopTime}" nullText="N/A"/></td>
                    <td>${creWrapper.successCount}</td>
                    <td>${creWrapper.failCount}</td>
                    <td>${creWrapper.unsupportedCount}</td>
                    <td>${creWrapper.totalCount}</td>
                    <td style="white-space:nowrap;">
                        <c:choose>
                            <c:when test="${creWrapper.cre.commandRequestExecutionStatus == 'FAILED'}">
                                <c:set var="statusSpanClass" value="error"/>
                            </c:when>
                            <c:when test="${creWrapper.cre.commandRequestExecutionStatus == 'IN_PROGRESS'}">
                                <c:set var="statusSpanClass" value="success"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="statusSpanClass" value=""/>
                            </c:otherwise>
                        </c:choose>
                        <span class="${statusSpanClass}">
                            <cti:msg key="${creWrapper.cre.commandRequestExecutionStatus.formatKey}" />
                        </span>
                    </td>
                    <td>${creWrapper.cre.userName}</td>
                    
                </tr>
            
            </c:forEach>
        </tbody>
    </table>
</cti:msgScope>
</cti:standardPage>