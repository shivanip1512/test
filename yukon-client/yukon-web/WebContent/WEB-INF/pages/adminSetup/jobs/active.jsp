<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="jobsscheduler.active">
    <cti:linkTabbedContainer>
        <cti:msg2 key=".jobsscheduler.all.contextualPageName" var="allName"/>
        <cti:msg2 key=".jobsscheduler.status.contextualPageName" var="statusName"/>
        <cti:msg2 key=".jobsscheduler.active.contextualPageName" var="activeName"/>
        <cti:linkTab selectorName="${allName}"><c:url value="all" /></cti:linkTab>
        <cti:linkTab selectorName="${activeName}" initiallySelected="true" ><c:url value="active" /></cti:linkTab>
        <cti:linkTab selectorName="${statusName}" ><c:url value="status" /></cti:linkTab>
    </cti:linkTabbedContainer>
  
  <table class="compact-results-table">
    <thead>
        <tr>
          <th><i:inline key=".column.jobName"/></th>
          <th><i:inline key=".column.startTime"/></th>
          <th><i:inline key=".column.running"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach items="${activeJobs}" var="jobStatus">
          <tr>
            <td title="${jobStatus.job.id}">
               <cti:url var="editScheduleDetailsUrl" value="/admin/maintenance/edit" >
                    <cti:param name="jobId" value="${jobStatus.job.id}"/>
                </cti:url>
                <a href="${editScheduleDetailsUrl}">${fn:escapeXml(jobStatus.job.jobDefinition.title)}</a>
            </td>
            <td><cti:formatDate value="${jobStatus.startTime}" type="TIME" /></td>
            <td>
                <form action="abortJob" method="post">
                    <cti:csrfToken/>
                    <input type="hidden" name="jobId" value="${jobStatus.job.id}"> 
                    <cti:msg2 var="cancelButtonLabel" key=".cancelButton"/>
                    <cti:button type="submit" label="${cancelButtonLabel}"/>
                </form>
            </td>
          </tr>
        </c:forEach>
    </tbody>
  </table>
</cti:standardPage>
