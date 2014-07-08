<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.survey.list">
<c:if test="${surveys.hitCount == 0}">
    <span class="empty-list"><i:inline key=".noResults"/></span>
</c:if>
<c:if test="${surveys.hitCount > 0}">
    <table id="surveyList" class="compact-results-table row-highlighting">
        <thead>
            <tr>
                <th><cti:msg2 key=".name"/></th>
                <th><cti:msg2 key=".key"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="survey" items="${surveys.resultList}">
                <c:set var="surveyId" value="${survey.surveyId}"/>
                <c:url var="surveyUrl" value="edit">
                    <c:param name="surveyId" value="${surveyId}"/>
                </c:url>
                <tr>
                    <td><a href="${surveyUrl}">${fn:escapeXml(survey.surveyName)}</a></td>
                    <td>${survey.surveyKey}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${surveys}" adjustPageCount="true"/>
</c:if>
</cti:msgScope>