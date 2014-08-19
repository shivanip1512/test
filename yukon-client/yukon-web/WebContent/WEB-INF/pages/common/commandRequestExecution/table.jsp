<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.cre.list">
    <table id="cresTable" class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <c:if test="${!singleJob}">
                    <th><i:inline key=".executions.tableHeader.type"/></th>
                </c:if>
                <th><i:inline key=".executions.tableHeader.startTime"/></th>
                <th><i:inline key=".executions.tableHeader.stopTime"/></th>
                <th><i:inline key=".executions.tableHeader.successCount"/></th>
                <th><i:inline key=".executions.tableHeader.failCount"/></th>
                <th><i:inline key=".executions.tableHeader.unsupportedCount"/></th>
                <th><i:inline key=".executions.tableHeader.totalCount"/></th>
                <th><i:inline key=".executions.tableHeader.status"/></th>
                <th><i:inline key=".executions.tableHeader.user"/></th>
        </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:if test="${fn:length(result.resultList) <= 0}">
                <c:set var="colCount" value="7"/>
                <c:if test="${!singleJob}">
                    <c:set var="colCount" value="8"/>
                </c:if>
                <tr>
                    <td colspan="${colCount}" style="text-align:center;" class="subtle"><i:inline key=".noExecutions"/></td>
                </tr>
            </c:if>
        
            <c:forEach var="creWrapper" items="${result.resultList}" varStatus="status">
                <cti:url var="detailsUrl" value="/common/commandRequestExecutionResults/detail">
                    <cti:param name="commandRequestExecutionId" value="${creWrapper.cre.id}"/>
                    <cti:param name="jobId" value="${backingBean.jobId}"/>
                </cti:url>
                <tr class="cp" title="${creWrapper.cre.commandRequestExecutionType.description} ID: ${creWrapper.cre.id}" data-href="${detailsUrl}">
                    
                    <c:if test="${!singleJob}">
                        <td>${creWrapper.cre.commandRequestExecutionType.shortName}</td>
                    </c:if>
                    <td class="wsnw"><cti:formatDate type="DATEHM" value="${creWrapper.cre.startTime}"/></td>
                    <td class="wsnw"><cti:formatDate type="DATEHM" value="${creWrapper.cre.stopTime}" nullText="N/A"/></td>
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
                    <td>${fn:escapeXml(creWrapper.cre.userName)}</td>
                    
                </tr>
            
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls adjustPageCount="true" result="${result}"/>
</cti:msgScope>