<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="adminSetup" page="survey.edit">

<cti:includeScript link="/resources/js/pages/yukon.surveys.edit.js"/>

<div id="question-popup" title="<cti:msg2 key=".surveyQuestion"/>"></div>

<span id="templateIcons" style="display: none">
<cti:button nameKey="up" classes="moveAnswerUp" renderMode="image" icon="icon-bullet-go-up"/>
<cti:button nameKey="down" classes="moveAnswerDown" renderMode="image" icon="icon-bullet-go-down"/>
<cti:button nameKey="deleteAnswer" classes="deleteAnswer" renderMode="image" icon="icon-cross"/>
<cti:button nameKey="up.disabled" renderMode="image" disabled="true" icon="icon-bullet-go-up"/>
<cti:button nameKey="down.disabled" renderMode="image" disabled="true" icon="icon-bullet-go-down"/>
</span>

<c:set var="surveyId" value="${survey.surveyId}"/>
<c:set var="baseUrl" value="edit"/>

<div class="column-12-12">
    <div class="column one">
        <tags:sectionContainer2 nameKey="info">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    ${fn:escapeXml(survey.surveyName)}
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".key">
                    ${fn:escapeXml(survey.surveyKey)}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="actions">
            <c:if test="${hasBeenTaken}">
                <div id="takenNote"><i:inline key=".hasBeenTaken"/></div>
            </c:if>
            <ul class="button-stack">
                <c:if test="${!hasBeenTaken}">
                    <li>
                        <cti:url var="detailUrl" value="editDetails">
                            <cti:param name="surveyId" value="${surveyId}"/>
                        </cti:url>
                        <cti:msg2 var="detailsTitle" key="modules.adminSetup.survey.list.details.title"/>
                        <cti:button nameKey="edit" icon="icon-pencil" renderMode="labeledImage" data-popup="#details-popup"/>
                        <div id="details-popup" data-dialog data-event="yukon.survey.details.edit" data-url="${detailUrl}" data-title="${detailsTitle}"></div>
                    </li>
                    <li>
                        <cti:url var="addQuestionUrl" value="addQuestion">
                            <cti:param name="surveyId" value="${surveyId}"/>
                        </cti:url>
                        <cti:button id="addQuestionBtn" data-add-question-url="${addQuestionUrl}" nameKey="addQuestion"
                            renderMode="labeledImage" icon="icon-add"/>
                    </li>
                </c:if>
                <li>
                    <cti:url var="sampleXmlUrl" value="sampleXml">
                        <cti:param name="surveyId" value="${surveyId}"/>
                    </cti:url>
                    <cti:button nameKey="sampleXml" href="${sampleXmlUrl}" renderMode="labeledImage" icon="icon-page-code"/>
                </li>
                <li>
                    <cti:url var="deleteUrl" value="delete">
                        <cti:param name="surveyId" value="${surveyId}"/>
                    </cti:url>
                    <cti:button id="delete-survey" nameKey="sampleXml"  label="Delete Survey" href="${deleteUrl}" renderMode="labeledImage" icon="icon-cross"/>
                    <d:confirm on="#delete-survey" nameKey="confirmDelete"
                            argument="${survey.surveyName}"/>
                </li>
                <c:if test="${hasBeenTaken}">
                    <cti:url var="reportUrl"
                        value="/stars/surveyReport/config">
                        <cti:param name="surveyId" value="${surveyId}"/>
                    </cti:url>
                    <li>
                        <cti:button nameKey="report" href="${reportUrl}" renderMode="labeledImage" icon="icon-application-view-columns"/>
                    </li>
                </c:if>
            </ul>
        </tags:sectionContainer2>
    </div>
</div>
<div class="column-24">
    <div class="column one nogutter">
        <c:if test="${empty questions}">
            <span class="empty-list"><i:inline key=".noQuestions"/></span>
        </c:if>
        <c:if test="${!empty questions}">
            <table class="compact-results-table">
                <thead>
                <tr>
                    <th><i:inline key=".questionKey"/></th>
                    <th><i:inline key=".answerRequired"/></th>
                    <th><i:inline key=".questionType"/></th>
                    <th><i:inline key=".actions"/></th>
                </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="question" varStatus="status" items="${questions}">
                    <tr questionId="${question.surveyQuestionId}">
                        <td>
                            ${fn:escapeXml(question.questionKey)}
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
                            <i:inline key="${question.questionType}"/>
                        </td>
                        <td>
                            <cti:url var="editQuestionUrl" value="editQuestion">
                                <cti:param name="surveyQuestionId" value="${question.surveyQuestionId}" />
                            </cti:url>
                            <cti:button nameKey="editQuestion" data-edit-question-url="${editQuestionUrl}" renderMode="image"
                                classes="editQuestionBtn" icon="icon-pencil"/>
                            <c:if test="${!hasBeenTaken}">
                                <c:if test="${status.first}">
                                    <cti:button renderMode="image"
                                        nameKey="up.disabled" disabled="true" icon="icon-bullet-go-up"/>
                                </c:if>
                                <c:if test="${!status.first}">
                                    <cti:url var="moveUpUrl" value="moveQuestion">
                                        <cti:param name="direction" value="up" />
                                        <cti:param name="surveyQuestionId" value="${question.surveyQuestionId}" />
                                    </cti:url>
                                    <cti:button renderMode="image" nameKey="up" icon="icon-bullet-go-up"
                                        href="${moveUpUrl}"/>
                                </c:if>
                                <c:if test="${status.last}">
                                    <cti:button renderMode="image" icon="icon-bullet-go-down"
                                        nameKey="down.disabled" disabled="true"/>
                                </c:if>
                                <c:if test="${!status.last}">
                                    <cti:url var="moveDownUrl" value="moveQuestion">
                                        <cti:param name="direction" value="down" />
                                        <cti:param name="surveyQuestionId" value="${question.surveyQuestionId}" />
                                    </cti:url>
                                    <cti:button renderMode="image" nameKey="down" icon="icon-bullet-go-down"
                                        href="${moveDownUrl}"/>
                                </c:if>
                                <cti:url var="deleteUrl" value="deleteQuestion">
                                    <cti:param name="surveyQuestionId"
                                        value="${question.surveyQuestionId}"/>
                                </cti:url>
                                <d:confirm on="#deleteBtn${question.surveyQuestionId}"
                                    nameKey="confirmDelete"
                                    argument="${question.questionKey}"/>
                                <cti:button id="deleteBtn${question.surveyQuestionId}" icon="icon-cross"
                                    nameKey="deleteQuestion" renderMode="image" href="${deleteUrl}"/>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</div>

<script type="text/javascript">
$(function () { yukon.surveys.edit.init({hasBeenTaken : '${hasBeenTaken}'}); });
</script>
</cti:standardPage>