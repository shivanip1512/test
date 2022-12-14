<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<table class="compact-results-table">
    <thead>
        <tr>
            <c:forEach var="column" items="${columns}">
                <tags:sort column="${column}" />
            </c:forEach>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach items="${allOneTime.resultList}" var="job">
            <tr>
                <td>${fn:escapeXml(job.jobDefinition.title)}</td>
                <td title="${job.id}">${job.beanName}</td>
                <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
                <td>${job.startTime}</td>
                <td>
                    <c:choose>
                        <c:when test="${job.deleted}">
                            <i:inline key="yukon.common.jobState.DELETED" />
                        </c:when>
                        <c:otherwise>
                            <form id="toggleJobForm_${job.id}" action="toggleState" method="POST">
                                <cti:csrfToken />
                                <input type="hidden" name="jobId" value="${job.id}">
                                <tags:switch checked="${!job.disabled}" name="toggleState"
                                    data-job-id="${job.id}" classes="js-scripts-toggle toggle-sm" />
                            </form>
                        </c:otherwise>
                    </c:choose>
               </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${allOneTime}" adjustPageCount="true" />


