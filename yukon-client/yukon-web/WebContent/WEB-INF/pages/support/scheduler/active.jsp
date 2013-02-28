<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="scheduler.active">
  <table class="resultsTable">
    <tr>
      <th><i:inline key=".column.jobName"/></th>
      <th><i:inline key=".column.startTime"/></th>
      <th><i:inline key=".column.running"/></th>
    </tr>
    <c:forEach items="${activeJobs}" var="job">
      <tr>
        <td title="${job.id}">${job.jobDefinition.title}</td>
        <td>
            <form action="abortJob" method="post">
            <input type="hidden" name="jobId" value="${job.id}"> 
            <cti:msg2 var="abortButtonLabel" key=".abortButton"/>
            <input type="submit" value="${abortButtonLabel}">
            </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
