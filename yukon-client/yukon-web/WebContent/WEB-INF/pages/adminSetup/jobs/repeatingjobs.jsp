<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<table class="results-table">
    <tr>
        <c:forEach var="column" items="${columns}">
            <tags:sort column="${column}" />
        </c:forEach>
    </tr>
    <c:forEach items="${allRepeating.resultList}" var="job">
        <c:choose>
            <c:when test="${lastGroupId == job.jobGroupId}">
                 <tr>
                    <td></td>
                    <td>${job.beanName}</td>
                    <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
                    <td><cti:dataUpdaterValue type="JOB"
                            identifier="${job.id}/SCHEDULE_DESCRIPTION" /></td>
                    <td>
                        <form action="toggleState" method="POST">
                            <cti:csrfToken />
                            <input type="hidden" name="jobId" value="${job.id}"> <input
                                type="hidden" name="redirectTab" value="all">
                            <tags:switch checked="${!job.disabled}" name="toggleState"
                                data-job-id="${job.id}" classes="js-scripts-toggle toggle-sm" />
                        </form>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr>
                    <td><cti:url var="editScheduleDetailsUrl" value="/admin/maintenance/edit">
                            <cti:param name="jobId" value="${job.id}" />
                        </cti:url> <a href="${editScheduleDetailsUrl}">${fn:escapeXml(job.jobDefinition.title)} - ${job.jobGroupId}</a>
                    </td>
                    <td>${job.beanName}</td>
                    <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
                    <td><cti:dataUpdaterValue type="JOB"
                            identifier="${job.id}/SCHEDULE_DESCRIPTION" /></td>
                    <td>
                        <form action="toggleState" method="POST">
                            <cti:csrfToken />
                            <input type="hidden" name="jobId" value="${job.id}"> <input
                                type="hidden" name="redirectTab" value="all">
                            <tags:switch checked="${!job.disabled}" name="toggleState"
                                data-job-id="${job.id}" classes="js-scripts-toggle toggle-sm" />
                        </form>
                    </td>
                </tr>
            </c:otherwise>
          </c:choose>
         <c:set var="lastGroupId" value="${job.jobGroupId}" />
    </c:forEach>
</table>
<tags:pagingResultsControls result="${allRepeating}" adjustPageCount="true" />
