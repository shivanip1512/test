<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="jobsscheduler.status">
    <cti:linkTabbedContainer>
        <cti:msg2 key=".jobsscheduler.all.contextualPageName" var="allName"/>
        <cti:msg2 key=".jobsscheduler.status.contextualPageName" var="statusName"/>
        <cti:msg2 key=".jobsscheduler.active.contextualPageName" var="activeName"/>
        <cti:linkTab selectorName="${allName}"><c:url value="all" /></cti:linkTab>
        <cti:linkTab selectorName="${activeName}" ><c:url value="active" /></cti:linkTab>
        <cti:linkTab selectorName="${statusName}"  initiallySelected="true"><c:url value="status" /></cti:linkTab>
    </cti:linkTabbedContainer>
  
  <table class="results-table">
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
        <td>${fn:escapeXml(jobStatus.job.jobDefinition.title)}</td>
        <td><cti:formatDate value="${jobStatus.startTime}" type="BOTH" /></td>
        <td><cti:formatDate value="${jobStatus.stopTime}" type="BOTH" /></td>
        <td>${jobStatus.jobRunStatus}</td>
        <td>${jobStatus.job.disabled}</td>
        <td>${fn:escapeXml(jobStatus.message)}</td>
      </tr>
    </c:forEach>
  </table>
</cti:standardPage>
