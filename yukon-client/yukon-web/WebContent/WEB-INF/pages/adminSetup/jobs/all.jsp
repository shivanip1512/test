<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="jobsscheduler.all">
    <cti:linkTabbedContainer>
        <cti:msg2 key=".jobsscheduler.all.contextualPageName" var="allName"/>
        <cti:msg2 key=".jobsscheduler.status.contextualPageName" var="statusName"/>
        <cti:msg2 key=".jobsscheduler.active.contextualPageName" var="activeName"/>
        <cti:linkTab selectorName="${allName}" initiallySelected="true"><c:url value="all" /></cti:linkTab>
        <cti:linkTab selectorName="${activeName}" ><c:url value="active" /></cti:linkTab>
        <cti:linkTab selectorName="${statusName}" ><c:url value="status" /></cti:linkTab>
    </cti:linkTabbedContainer>
<h4><i:inline key=".repeatingJobs"/></h2>
  <table class="results-table">
    <tr>
      <th width="15%"><i:inline key=".column.jobName"/></th>
      <th><i:inline key=".column.beanName"/></th>
      <th><i:inline key=".column.username"/></th>
      <th><i:inline key=".column.cronString"/></th>
      <th><i:inline key=".column.enabled"/></th>
    </tr>
    <c:forEach items="${allRepeating}" var="job">
      <tr>
        <td title="${job.id}">${fn:escapeXml(job.jobDefinition.title)}</td>
        <td>${job.beanName}</td>
        <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
        <td>${job.cronString}</td>
        <td>
        <c:if test="${job.disabled}">
            <form action="enableJob" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="jobId" value="${job.id}">
                <cti:button type="submit" nameKey="enable"/>
            </form>
        </c:if>
        <c:if test="${!job.disabled}">
            <form action="disableJob" method="post">
                <cti:csrfToken/>
                <input type="hidden" name="jobId" value="${job.id}">
                <cti:button type="submit" nameKey="disable"/>
            </form>
        </c:if>
        <c:choose>
            <c:when test="${job.disabled}"><i:inline key=".disabled"/></c:when>
            <c:otherwise><i:inline key=".enabled"/></c:otherwise>
        </c:choose>
        </td>
      </tr>
    </c:forEach>
  </table>


<h4><i:inline key=".oneTimeJobs"/></h2>
  <table class="results-table">
    <tr>
      <th width="15%"><i:inline key=".column.jobName"/></th>
      <th><i:inline key=".column.beanName"/></th>
      <th><i:inline key=".column.username"/></th>
      <th><i:inline key=".column.scheduledStart"/></th>
      <th><i:inline key=".column.enabled"/></th>
    </tr>
    <c:forEach items="${allOneTime}" var="job">
      <tr>
        <td>${fn:escapeXml(job.jobDefinition.title)}</td>
        <td title="${job.id}">${job.beanName}</td>
        <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
        <td>${job.startTime}</td>
        <td><c:choose>
            <c:when test="${job.disabled}"><i:inline key=".disabled"/></c:when>
            <c:otherwise><i:inline key=".enabled"/></c:otherwise>
        </c:choose></td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
