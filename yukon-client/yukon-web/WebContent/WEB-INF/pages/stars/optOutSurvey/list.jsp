<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="dr" page="surveyList">
    <tags:simpleDialog id="ajaxDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/survey.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <cti:includeScript link="/JavaScript/picker.js"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/tableCreation.js"/>
    <cti:msg2 var="programListTitle" key=".programListTitle" javaScriptEscape="true"/>
    <cti:msg2 var="addSurveyTitle" key=".addSurveyTitle" javaScriptEscape="true"/>
	<script type="text/javascript">
	    var programListTitle = '${programListTitle}';
        var addSurveyTitle = '${addSurveyTitle}';
	</script>

    <c:set var="baseUrl" value="/spring/stars/optOutSurvey/list"/>
    <cti:url var="submitUrl" value="${baseUrl}"/>

    <cti:msg2 var="boxTitle" key=".optOutSurveys"/>
    <tags:pagedBox title="${boxTitle}" searchResult="${optOutSurveys}"
        baseUrl="${baseUrl}">
        <c:if test="${optOutSurveys.hitCount == 0}">
            <i:inline key=".noResults"/>
        </c:if>
        <c:if test="${optOutSurveys.hitCount > 0}">
            <table id="optOutSurveyList" class="compactResultsTable rowHighlighting">
                <tr>
                    <th><i:inline key=".surveyName"/></th>
                    <th><i:inline key=".programs"/></th>
                    <th><i:inline key=".startDate"/></th>
                    <th><i:inline key=".stopDate"/></th>
                    <th><i:inline key=".actions"/></th>
                </tr>
                <cti:msg2 var="noStopDate" key=".noStopDate"/>
                <c:forEach var="optOutSurvey" items="${optOutSurveys.resultList}">
                    <c:set var="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><spring:escapeBody
                            htmlEscape="true">${optOutSurvey.surveyName}</spring:escapeBody></td>
                        <td>
						    <c:forEach var="programId" items="${optOutSurvey.programIds}" end="2">
                                <spring:escapeBody htmlEscape="true">${programNamesById[programId]}</spring:escapeBody><br>
						    </c:forEach>
				            <c:if test="${fn:length(optOutSurvey.programIds) > 3}">
				                <cti:url var="programListUrl" value="/spring/stars/optOutSurvey/programList">
				                    <cti:param name="optOutSurveyId" value="${optOutSurveyId}"/>
				                </cti:url>

				                <a href="javascript:openSimpleDialog('ajaxDialog', '${programListUrl}', programListTitle)"><i:inline key=".morePrograms"/></a>
				            </c:if>
                        </td>
                        <td>
                            <cti:formatDate type="BOTH" value="${optOutSurvey.startDate}"/>
                        </td>
                        <td>
                            <cti:formatDate type="BOTH" value="${optOutSurvey.stopDate}"
                                nullText="${noStopDate}"/>
                        </td>
                        <td>
                            <cti:url var="editUrl" value="/spring/stars/optOutSurvey/edit">
                                <cti:param name="optOutSurveyId"
                                    value="${optOutSurvey.optOutSurveyId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="ajaxDialog"
                                key="edit" skipLabel="true" 
                                actionUrl="${editUrl}"/>
                            <cti:url var="deleteUrl" value="/spring/stars/optOutSurvey/confirmDelete">
                                <cti:param name="optOutSurveyId" value="${optOutSurveyId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="ajaxDialog"
                                key="delete" actionUrl="${deleteUrl}"
                                skipLabel="true"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <cti:url var="addUrl" value="/spring/stars/optOutSurvey/edit"/>
        <form id="addForm" action="${addUrl}">
            <div class="actionArea">
                <cti:url var="addUrl" value="/spring/stars/optOutSurvey/edit"/>
                <cti:button nameKey="add" onclick="openSimpleDialog('ajaxDialog', '${addUrl}', addSurveyTitle)"/>
            </div>
        </form>
    </tags:pagedBox>
</cti:standardPage>
