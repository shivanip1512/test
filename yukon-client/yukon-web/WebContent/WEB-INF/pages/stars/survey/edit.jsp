<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="survey" page="edit">

<cti:includeCss link="/WebConfig/yukon/styles/operator/survey.css"/>
<cti:includeScript link="/JavaScript/stars/surveyQuestionEdit.js"/>

<tags:simpleDialog id="ajaxDialog"/>

<c:set var="surveyId" value="${survey.surveyId}"/>
<c:set var="baseUrl" value="/spring/stars/survey/edit"/>

<table class="widgetColumns">
    <tr>
        <td class="widgetColumnCell" valign="top">
            <div class="widgetContainer">
                <tags:boxContainer2 nameKey="info">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <spring:escapeBody
                                htmlEscape="true">${survey.surveyName}</spring:escapeBody>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".key">
                            <spring:escapeBody
                                htmlEscape="true">${survey.surveyKey}</spring:escapeBody>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:boxContainer2>
            </div>
        </td>
        <td class="widgetColumnCell" valign="top">
            <div class="widgetContainer">
                <tags:boxContainer2 nameKey="actions">
                    <ul>
                        <c:if test="${hasBeenTaken}"><div id="takenNote">
                            <i:inline key=".hasBeenTaken"/>
                        </div></c:if>
                        <c:if test="${!hasBeenTaken}">
	                        <cti:url var="detailUrl"
	                            value="/spring/stars/survey/editDetails">
	                            <cti:param name="surveyId" value="${surveyId}"/>
	                        </cti:url>
	                        <li>
	                            <tags:simpleDialogLink2 dialogId="ajaxDialog"
	                                key="edit" actionUrl="${detailUrl}"/>
	                        </li>
	                        <cti:url var="addQuestionUrl"
	                            value="/spring/stars/survey/addQuestion">
	                            <cti:param name="surveyId" value="${surveyId}"/>
	                        </cti:url>
	                        <li>
	                            <tags:simpleDialogLink2 dialogId="ajaxDialog"
	                                key="addQuestion" actionUrl="${addQuestionUrl}"/>
	                        </li>
                        </c:if>
                        <cti:url var="sampleXmlUrl"
                            value="/spring/stars/survey/sampleXml">
                            <cti:param name="surveyId" value="${surveyId}"/>
                        </cti:url>
                        <li>
                            <cti:labeledImg nameKey="sampleXml" href="${sampleXmlUrl}"/>
                        </li>
                        <c:if test="${hasBeenTaken}">
                            <cti:url var="reportUrl"
                                value="/spring/stars/surveyReport/config">
                                <cti:param name="surveyId" value="${surveyId}"/>
                            </cti:url>
                            <li>
                                <cti:labeledImg nameKey="report" href="${reportUrl}"/>
                            </li>
                        </c:if>
                    </ul>
                </tags:boxContainer2>
            </div>
        </td>
    </tr>
    <tr>
        <td class="widgetColumnCell" valign="top" colspan="2">
            <div class="widgetContainer">
                <tags:boxContainer2 nameKey="questions">
					<c:if test="${empty questions}">
					    <i:inline key=".noQuestions"/>
					</c:if>
					<c:if test="${!empty questions}">
					    <table class="compactResultsTable rowHighlighting">
					        <tr>
					            <th><i:inline key=".key"/></th>
					            <th><i:inline key=".answerRequired"/></th>
					            <th><i:inline key=".questionType"/></th>
					            <th><i:inline key=".actions"/></th>
					        </tr>
					        <c:forEach var="question" varStatus="status" items="${questions}">
					            <tr class="<tags:alternateRow odd="" even="altRow"/>">
					                <td>
					                    <spring:escapeBody htmlEscape="true">
					                        ${question.questionKey}
					                    </spring:escapeBody>
					                </td>
					                <td>
					                    <c:if test="${question.answerRequired}">
					                        <i:inline key=".answerIsRequired"/>
					                    </c:if>
					                    <c:if test="${!question.answerRequired}">
					                        <i:inline key=".answerNotRequired"/>
					                    </c:if>
					                </td>
					                <td>
					                    <cti:msg2 key="${question.questionType}"/>
					                </td>
					                <td>
					                    <cti:url var="editQuestionUrl"
					                        value="/spring/stars/survey/editQuestion">
					                        <cti:param name="surveyQuestionId"
					                            value="${question.surveyQuestionId}"/>
					                    </cti:url>
					                    <tags:simpleDialogLink2 dialogId="ajaxDialog"
					                        key="editQuestion" skipLabel="true" 
					                        actionUrl="${editQuestionUrl}"/>
                                        <c:if test="${!hasBeenTaken}">
						                    <c:if test="${status.first}">
						                        <cti:img nameKey="up.disabled"/>
						                    </c:if>
						                    <c:if test="${!status.first}">
						                        <cti:url var="moveProgramUpUrl"
						                            value="/spring/stars/survey/moveQuestion">
						                            <cti:param name="direction" value="up"/>
						                            <cti:param name="surveyQuestionId"
						                                value="${question.surveyQuestionId}"/>
						                        </cti:url>
						                        <cti:img nameKey="up" href="javascript:simpleAJAXRequest('${moveProgramUpUrl}')"/>
						                    </c:if>
						                    <c:if test="${status.last}">
						                        <cti:img nameKey="down.disabled"/>
						                    </c:if>
						                    <c:if test="${!status.last}">
						                        <cti:url var="moveProgramDownUrl"
						                            value="/spring/stars/survey/moveQuestion">
						                            <cti:param name="direction" value="down"/>
						                            <cti:param name="surveyQuestionId"
						                                value="${question.surveyQuestionId}"/>
						                        </cti:url>
						                        <cti:img nameKey="down" href="javascript:simpleAJAXRequest('${moveProgramDownUrl}')"/>
						                    </c:if>
						                    <cti:url var="deleteUrl"
						                        value="/spring/stars/survey/confirmDeleteQuestion">
						                        <cti:param name="surveyQuestionId"
						                            value="${question.surveyQuestionId}"/>
						                    </cti:url>
						                    <tags:simpleDialogLink2 dialogId="ajaxDialog"
						                        key="deleteQuestion" actionUrl="${deleteUrl}"
						                        skipLabel="true"/>
					                    </c:if>
					                </td>
					            </tr>
					        </c:forEach>
					    </table>
					</c:if>
                </tags:boxContainer2>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>
