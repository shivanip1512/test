<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="survey" page="edit">

<cti:includeScript link="/JavaScript/ajaxDialog.js"/>
<cti:includeScript link="/JavaScript/stars/surveyQuestionEdit.js"/>

<div id="ajaxDialog"></div>

<span id="templateIcons" style="display: none">
<cti:button nameKey="up" styleClass="moveAnswerUp" renderMode="image"/>
<cti:button nameKey="down" styleClass="moveAnswerDown" renderMode="image"/>
<cti:button nameKey="deleteAnswer" styleClass="deleteAnswer" renderMode="image"/>
<cti:button nameKey="up.disabled" renderMode="image" disabled="true"/>
<cti:button nameKey="down.disabled" renderMode="image" disabled="true"/>
</span>

<c:set var="surveyId" value="${survey.surveyId}"/>
<c:set var="baseUrl" value="edit"/>

<cti:url var="detailUrl" value="editDetails">
    <cti:param name="surveyId" value="${surveyId}"/>
</cti:url>
<cti:url var="addQuestionUrl" value="addQuestion">
    <cti:param name="surveyId" value="${surveyId}"/>
</cti:url>
<cti:url var="editQuestionUrl" value="editQuestion"/>
<cti:url var="moveQuestionUrl" value="moveQuestion"/>
<script type="text/javascript">
jQuery(document).ready(function() {
    jQuery('#editDetailsBtn').click(function() {
        jQuery('#ajaxDialog').load('${detailUrl}');
    });

    jQuery('#addQuestionBtn').click(function() {
        jQuery('#ajaxDialog').load('${addQuestionUrl}');
    });

    jQuery('.editQuestionBtn').click(function(event) {
        var surveyQuestionId = jQuery(event.target).closest('tr').attr('questionId');
        var editQuestionUrl = '${editQuestionUrl}?surveyQuestionId=' + surveyQuestionId;
        jQuery('#ajaxDialog').load(editQuestionUrl);
    });

    function moveQuestionFunc(direction) {
        return function(event) {
            var surveyQuestionId = jQuery(event.target).closest('tr').attr('questionId');
            location.href = '${moveQuestionUrl}?direction=' + direction +
                    '&surveyQuestionId=' + surveyQuestionId;
        };
    }

    jQuery('.moveDownBtn').click(moveQuestionFunc('down'));
    jQuery('.moveUpBtn').click(moveQuestionFunc('up'));
    jQuery(document).on('click', '.moveAnswerUp', moveAnswerUp);
    jQuery(document).on('click', '.moveAnswerDown', moveAnswerDown);
    jQuery(document).on('click', '.deleteAnswer', deleteAnswer);

    jQuery(document).bind('yukonDetailsUpdated', closeAjaxDialogAndRefresh);
    jQuery(document).bind('yukonQuestionSaved', closeAjaxDialogAndRefresh);
});

<c:if test="${!hasBeenTaken}">
var templateIcons = jQuery('#templateIcons > button')
moveUpIcon = templateIcons[0];
moveDownIcon = templateIcons[1];
deleteAnswerIcon = templateIcons[2];
moveUpDisabledIcon = templateIcons[3];
moveDownDisabledIcon = templateIcons[4];
</c:if>
</script>

<table class="widgetColumns">
    <tr>
        <td class="widgetColumnCell first" valign="top">
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
        <td class="widgetColumnCell last" valign="top">
            <div class="widgetContainer">
                <tags:boxContainer2 nameKey="actions">
                    <ul>
                        <c:if test="${hasBeenTaken}"><div id="takenNote">
                            <i:inline key=".hasBeenTaken"/>
                        </div></c:if>
                        <c:if test="${!hasBeenTaken}">
	                        <li>
                                <cti:button id="editDetailsBtn" nameKey="edit"
                                    renderMode="labeledImage" styleClass="f_blocker"/>
                            </li>
	                        <li>
                                <cti:button id="addQuestionBtn" nameKey="addQuestion"
                                    renderMode="labeledImage" styleClass="f_blocker"/>
	                        </li>
                        </c:if>
                        <cti:url var="sampleXmlUrl" value="sampleXml">
                            <cti:param name="surveyId" value="${surveyId}"/>
                        </cti:url>
                        <li>
                            <cti:button nameKey="sampleXml" href="${sampleXmlUrl}" renderMode="labeledImage"/>
                        </li>
                        <c:if test="${hasBeenTaken}">
                            <cti:url var="reportUrl"
                                value="/stars/surveyReport/config">
                                <cti:param name="surveyId" value="${surveyId}"/>
                            </cti:url>
                            <li>
                                <cti:button nameKey="report" href="${reportUrl}" renderMode="labeledImage"/>
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
					            <th><i:inline key=".questionKey"/></th>
					            <th><i:inline key=".answerRequired"/></th>
					            <th><i:inline key=".questionType"/></th>
					            <th><i:inline key=".actions"/></th>
					        </tr>
					        <c:forEach var="question" varStatus="status" items="${questions}">
					            <tr class="<tags:alternateRow odd="" even="altRow"/>"
                                    questionId="${question.surveyQuestionId}">
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
                                        <cti:button nameKey="editQuestion" renderMode="image"
                                            styleClass="editQuestionBtn f_blocker"/>
                                        <c:if test="${!hasBeenTaken}">
						                    <c:if test="${status.first}">
						                        <cti:button renderMode="image"
                                                    nameKey="up.disabled" disabled="true"/>
						                    </c:if>
						                    <c:if test="${!status.first}">
						                        <cti:button renderMode="image" nameKey="up"
                                                    styleClass="moveUpBtn"/>
						                    </c:if>
						                    <c:if test="${status.last}">
						                        <cti:button renderMode="image"
                                                    nameKey="down.disabled" disabled="true"/>
						                    </c:if>
						                    <c:if test="${!status.last}">
						                        <cti:button renderMode="image" nameKey="down"
                                                    styleClass="moveDownBtn"/>
						                    </c:if>
						                    <cti:url var="deleteUrl" value="deleteQuestion">
						                        <cti:param name="surveyQuestionId"
						                            value="${question.surveyQuestionId}"/>
						                    </cti:url>
                                            <dialog:confirm on="#deleteBtn${question.surveyQuestionId}"
                                                nameKey="confirmDelete"
                                                argument="${question.questionKey}"/>
                                            <cti:button id="deleteBtn${question.surveyQuestionId}"
                                                nameKey="deleteQuestion" renderMode="image" href="${deleteUrl}"/>
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
