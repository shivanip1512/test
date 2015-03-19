<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<table class="compact-results-table">
    <thead>
        <tr>
            <tags:sort column="${program}"/>
            <tags:sort column="${category}"/>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
    <c:forEach var="program" items="${assignedPrograms.resultList}">
        <tr>
            <td><dr:assignedProgramName assignedProgram="${program}"/></td>
            <td>
                <cti:url var="viewUrl" value="view">
                    <cti:param name="ecId" value="${param.ecId}"/>
                    <cti:param name="applianceCategoryId" value="${program.applianceCategoryId}"/>
                </cti:url>
                <a href="${viewUrl}">${fn:escapeXml(applianceCategoriesById[program.applianceCategoryId].name)}</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<tags:pagingResultsControls result="${assignedPrograms}" adjustPageCount="true"/>
