<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="scheduler.jobs">

<h2><i:inline key=".repeatingJobs"/></h2>
  <table class="resultsTable">
    <tr>
      <th width="15%"><i:inline key=".column.jobName"/></th>
      <th><i:inline key=".column.beanName"/></th>
      <th><i:inline key=".column.username"/></th>
      <th><i:inline key=".column.cronString"/></th>
      <th><i:inline key=".column.enabled"/></th>
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
        <c:choose>
            <c:when test="${job.disabled}"><i:inline key=".disabled"/></c:when>
            <c:otherwise><i:inline key=".enabled"/></c:otherwise>
        </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>


<h2><i:inline key=".oneTimeJobs"/></h2>
  <table class="resultsTable">
    <tr>
      <th width="15%"><i:inline key=".column.jobName"/></th>
      <th><i:inline key=".column.beanName"/></th>
      <th><i:inline key=".column.username"/></th>
      <th><i:inline key=".column.scheduledStart"/></th>
      <th><i:inline key=".column.enabled"/></th>
    </tr>
    <c:forEach items="${allOneTime}" var="job">
      <tr>
        <td>${job.jobDefinition.title}</td>
        <td title="${job.id}">${job.beanName}</td>
        <td>${job.userContext.yukonUser.username}</td>
        <td>${job.startTime}</td>
        <td><c:choose>
            <c:when test="${job.disabled}"><i:inline key=".disabled"/></c:when>
            <c:otherwise><i:inline key=".enabled"/></c:otherwise>
        </c:choose></td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
