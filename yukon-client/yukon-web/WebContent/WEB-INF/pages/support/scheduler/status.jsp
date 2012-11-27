<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="scheduler|status" />
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	<cti:crumbLink url="/support/" title="Support" />
	<cti:crumbLink url="/support/scheduler/active" title="Jobs"/>
    <cti:crumbLink>Status</cti:crumbLink>
</cti:breadCrumbs>


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
