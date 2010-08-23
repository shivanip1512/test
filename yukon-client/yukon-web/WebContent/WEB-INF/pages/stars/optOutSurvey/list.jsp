<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="optOutSurvey" page="list">
    <tags:simpleDialog id="ajaxDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

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
						    <c:forEach var="programId" items="${optOutSurvey.programIds}">
						        <spring:escapeBody htmlEscape="true">
						            ${programsById[programId].name.displayName}
						        </spring:escapeBody><br>
						    </c:forEach>
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

        <script type="text/javascript">
        function chooseSurvey(programIds) {
        	surveyPicker.show()
        	return true;
        }

        function addOptOutSurvey(surveyid) {
            openSimpleDialog('ajaxDialog', $('addForm').action,
                    '<spring:escapeBody javaScriptEscape="true"><cti:msg2 key=".addDialogTitle"/></spring:escapeBody>',
                     $('addForm').serialize(true));
            return true;
        }
        </script>

        <cti:url var="addUrl" value="/spring/stars/optOutSurvey/edit"/>
        <form id="addForm" action="${addUrl}">
            <div class="actionArea">
	            <tags:pickerDialog type="assignedProgramPicker" id="programPicker"
	                destinationFieldName="programIds" endAction="chooseSurvey"
	                multiSelectMode="true" linkType="button"
	                extraArgs="${energyCompanyId}"><cti:msg2 key=".add"/></tags:pickerDialog>
                <tags:pickerDialog type="surveyPicker" id="surveyPicker"
                    destinationFieldName="surveyId" endAction="addOptOutSurvey"
                    styleClass="simpleLink" immediateSelectMode="true"
                    linkType="none"/>
            </div>
        </form>
    </tags:pagedBox>
</cti:standardPage>
