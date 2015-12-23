<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

  <table class="results-table">
    <tr>
        <c:forEach var="column" items="${columns}">
              <tags:sort column="${column}"/>
        </c:forEach>
    </tr>
    <c:forEach items="${jobStatusList.resultList}" var="jobStatus">
        <c:if test="${jobStatus.jobRunStatus == 'COMPLETED'}"><c:set var="colorClass" value="success"/></c:if>
        <c:if test="${jobStatus.jobRunStatus == 'FAILED'}"><c:set var="colorClass" value="error"/></c:if>
        <c:set var="enabledColor" value="success"/>
        <c:set var="enabledStatus" value="Enabled"/>
        <c:if test="${jobStatus.job.disabled}"><c:set var="enabledColor" value="error"/><c:set var="enabledStatus" value="Disabled"/></c:if>
        <c:choose>
            <c:when test="${lastGroupId == jobStatus.job.jobGroupId}">
                 <tr>
                    <td></td>
                    <td><cti:formatDate value="${jobStatus.startTime}" type="BOTH" /></td>
                    <td><cti:formatDate value="${jobStatus.stopTime}" type="BOTH" /></td>
                    <td class="${colorClass}"><i:inline key="${jobStatus.jobRunStatus.formatKey}"/></td>
                    <td class="${enabledColor}">${enabledStatus}</td>
                    <td>${fn:escapeXml(jobStatus.message)}</td>
                  </tr>
            </c:when>
            <c:otherwise>
                  <tr>
                    <td>
                       <cti:url var="editScheduleDetailsUrl" value="/admin/maintenance/edit" >
                            <cti:param name="jobId" value="${jobStatus.job.id}"/>
                       </cti:url>            
                       <a href="${editScheduleDetailsUrl}">${fn:escapeXml(jobStatus.job.jobDefinition.title)} - ${jobStatus.job.jobGroupId }</a>
                    </td>
                    <td><cti:formatDate value="${jobStatus.startTime}" type="BOTH" /></td>
                    <td><cti:formatDate value="${jobStatus.stopTime}" type="BOTH" /></td>
                    <td class="${colorClass}"><i:inline key="${jobStatus.jobRunStatus.formatKey}"/></td>
                    <td class="${enabledColor}">${enabledStatus}</td>
                    <td>${fn:escapeXml(jobStatus.message)}</td>
                  </tr>
            </c:otherwise>
        </c:choose>

      <c:set var="lastGroupId" value="${jobStatus.job.jobGroupId}"/>
    </c:forEach>
  </table>
  <tags:pagingResultsControls result="${jobStatusList}" adjustPageCount="true"/>
 