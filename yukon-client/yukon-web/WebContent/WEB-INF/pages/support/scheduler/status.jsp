<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="scheduler.status">
  <table class="resultsTable">
    <tr>
      <th width="15%"><i:inline key=".column.jobName"/></th>
      <th width="10%"><i:inline key=".column.start"/></th>
      <th width="10%"><i:inline key=".column.stop"/></th>
      <th><i:inline key=".column.state"/></th>
      <th><i:inline key=".column.disabled"/></th>
      <th><i:inline key=".column.errorMessage"/></th>
    </tr>
    <c:forEach items="${jobStatusList}" var="jobStatus">
      <tr>
        <td>${jobStatus.job.jobDefinition.title}</td>
        <td><cti:formatDate value="${jobStatus.startTime}" type="BOTH" /></td>
        <td><cti:formatDate value="${jobStatus.stopTime}" type="BOTH" /></td>
        <td>${jobStatus.jobState}</td>
        <td>${jobStatus.job.disabled}</td>
        <td>${jobStatus.message}</td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
