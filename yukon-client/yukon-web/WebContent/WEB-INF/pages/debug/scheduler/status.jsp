<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Debug Home Page" module="debug">
  <cti:standardMenu menuSelection="scheduler|status" />


  <table class="resultsTable">
    <tr>
      <th width="15%">Job Name</th>
      <th width="10%">Start</th>
      <th width="10%">Stop</th>
      <th>State</th>
      <th>Disabled</th>
      <th>Error Message</th>
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
