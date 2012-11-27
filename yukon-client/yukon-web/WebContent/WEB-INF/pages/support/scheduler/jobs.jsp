<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="scheduler|jobs" />
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	<cti:crumbLink url="/support/" title="Support" />
    <cti:crumbLink url="/support/scheduler/active" title="Jobs"/>
    <cti:crumbLink>All</cti:crumbLink>
</cti:breadCrumbs>

<h2>Repeating Jobs</h2>
  <table class="resultsTable">
    <tr>
      <th width="15%">Job Name</th>
      <th>Bean Name</th>
      <th>Username</th>
      <th>Cron String</th>
      <th>Enable/Disable</th>
    </tr>
    <c:forEach items="${allRepeating}" var="job">
      <tr>
        <td title="${job.id}">${job.jobDefinition.title}</td>
        <td>${job.beanName}</td>
        <td>${job.userContext.yukonUser.username}</td>
        <td>${job.cronString}</td>
        <td>
        <%--
        <c:if test="${job.disabled}">
            <form action="enableJob" method="post">
            <input type="hidden" name="jobId" value="${job.id}">
            <input type="submit" value="Enable">
            </form>
        </c:if>
        <c:if test="${!job.disabled}">
            <form action="disableJob" method="post">
            <input type="hidden" name="jobId" value="${job.id}">
            <input type="submit" value="Disable">
            </form>
        </c:if>
        --%>
        ${job.disabled ? "Disabled" : "Enabled"}
        </td>
      </tr>
    </c:forEach>
  </table>


<h2>One Time Jobs</h2>
  <table class="resultsTable">
    <tr>
      <th width="15%">Job Name</th>
      <th>Bean Name</th>
      <th>Username</th>
      <th>Scheduled Start</th>
      <th>Enable/Disable</th>
    </tr>
    <c:forEach items="${allOneTime}" var="job">
      <tr>
        <td>${job.jobDefinition.title}</td>
        <td title="${job.id}">${job.beanName}</td>
        <td>${job.userContext.yukonUser.username}</td>
        <td>${job.startTime}</td>
        <td>${job.disabled ? "Disabled" : "Enabled"}</td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
