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
        <c:forEach items="${allRepeating.resultList}" var="job">
            <tr>
                <td>
                    <c:if test="${lastGroupId != job.jobGroupId}">
                        ${fn:escapeXml(job.jobDefinition.title)}
                    </c:if>
                </td>
                <td>${job.beanName}</td>
                <td>${fn:escapeXml(job.userContext.yukonUser.username)}</td>
                <td><cti:dataUpdaterValue type="JOB" identifier="${job.id}/SCHEDULE_DESCRIPTION" /></td>
                <td>
                    <c:choose>
                        <c:when test="${job.deleted}">
                            <i:inline key="yukon.common.jobState.DELETED"/>
                        </c:when>
                        <c:otherwise>
                            <form action="toggleState?jobId=${job.id}" method="POST">
                                <cti:csrfToken />
                                <tags:switch checked="${!job.disabled}" name="toggleState"
                                    data-job-id="${job.id}" classes="js-scripts-toggle toggle-sm" />
                            </form>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <c:set var="lastGroupId" value="${job.jobGroupId}" />
        </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${allRepeating}" adjustPageCount="true" />
