<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.survey.edit, modules.adminSetup.survey.edit.editQuestion">

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="EDIT">
    <cti:toJson var="dataAnswerKeys" object="${answerKeys}"/>
</cti:displayForPageEditModes>

<cti:url var="submitUrl" value="saveQuestion"/>
<form:form id="question-form" modelAttribute="question" action="${submitUrl}" data-answer-keys="${dataAnswerKeys}" htmlEscape="false">
    <cti:csrfToken/>
    <form:hidden path="surveyId"/>
    <form:hidden path="surveyQuestionId"/>
    <form:hidden path="displayOrder"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".questionKey">
            <tags:input id="questionKey" path="questionKey" size="30" maxlength="64"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".answerRequired" labelForId="answerRequiredCheckbox">
            <tags:checkbox id="answerRequiredCheckbox" path="answerRequired"/>
            <label for="answerRequiredCheckbox">
                <i:inline key=".answerRequiredDescription"/>
            </label>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".questionType">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:msg2 key="${question.questionType}"/>
                <form:hidden path="questionType" id="questionType"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <form:select path="questionType" id="questionType">
                    <c:forEach var="questionType" items="${questionTypes}">
                        <cti:msg var="optionLabel" key="${questionType}"/>
                        <form:option value="${questionType}" label="${optionLabel}"/>
                    </c:forEach>
                </form:select>
            </cti:displayForPageEditModes>
        </tags:nameValue2>

        <tags:nameValue2 rowId="textAnswerAllowed_DROP_DOWN" rowClass="additionalInfo"
            nameKey=".textAnswerAllowed" labelForId="textAnswerAllowedCheckbox">
            <tags:checkbox id="textAnswerAllowedCheckbox" path="textAnswerAllowed"/>
            <label for="textAnswerAllowedCheckbox">
                <i:inline key=".textAnswerAllowedDescription"/>
            </label>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div id="additionalInfo_DROP_DOWN" class="additionalInfo">
        <tags:sectionContainer2 id="answersBox" nameKey="answers">
            <div class="scroll-md">
                <table id="answerTable" class="compact-results-table dashed">
                    <thead>
                        <tr>
                            <th><i:inline key=".answerKey"/></th>
                            <cti:displayForPageEditModes modes="EDIT">
                                <th><i:inline key=".actions"/></th>
                            </cti:displayForPageEditModes>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <cti:displayForPageEditModes modes="VIEW">
                           <c:forEach var="answer" items="${question.answers}">
                           <tr>
                               <td>${fn:escapeXml(answer.answerKey)}</td>
                           </tr>
                           </c:forEach>
                        </cti:displayForPageEditModes>
                    </tbody>
                </table>
            </div>
            <cti:displayForPageEditModes modes="EDIT">
                <div style="margin-top: 10px;" class="clearfix"><cti:button renderMode="labeledImage" nameKey="addAnswer" href="javascript: yukon.surveys.edit.addAnswer()" classes="fl" icon="icon-add"/></div>
            </cti:displayForPageEditModes>
        </tags:sectionContainer2>
    </div>
</form:form>

</cti:msgScope>
