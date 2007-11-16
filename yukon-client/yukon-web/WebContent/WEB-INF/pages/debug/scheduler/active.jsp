<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Debug Home Page" module="debug">
  <cti:standardMenu menuSelection="scheduler|active" />

  <table class="resultsTable">
    <tr>
      <th>Job Name</th>
      <th>Start Time</th>
      <th>Running?</th>
    </tr>
    <c:forEach items="${activeJobs}" var="job">
      <tr>
        <td title="${job.id}">${job.jobDefinition.title}</td>
        <td>
            <form action="abortJob" method="post">
            <input type="hidden" name="jobId" value="${job.id}"> 
            <input type="submit" value="Abort">
            </form>
        </td>
      </tr>
    </c:forEach>
  </table>

</cti:standardPage>
