<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<tags:setFormEditMode mode="${mode}"/>

<c:set var="okAction" value="none"/>
<cti:displayForPageEditModes modes="EDIT">
    <c:set var="okAction" value="yukonDialogSubmit"/>
</cti:displayForPageEditModes>

<dialog:ajaxPage nameKey="editQuestion" module="survey" page="edit" okEvent="${okAction}">

<script type="text/javascript">
jQuery(document).ready(function() {
    questionTypeChanged();
    jQuery('#questionType').change(questionTypeChanged);
    jQuery('#inputForm').ajaxForm({'target' : '#ajaxDialog'});

    <cti:displayForPageEditModes modes="EDIT">
    initWithAnswerKeys(${cti:jsonString(answerKeys)});
    jQuery('#questionKey').focus();
    jQuery('#questionKey').select();
    </cti:displayForPageEditModes>
});
</script>

<cti:url var="submitUrl" value="saveQuestion"/>
<form:form id="inputForm" commandName="question" action="${submitUrl}">
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
		<tags:boxContainer2 id="answersBox" nameKey="answers">
	        <div class="dialogScrollArea">
			    <table id="answerTable" class="compactResultsTable rowHighlighting">
			        <tr>
			            <th><i:inline key=".answerKey"/></th>
                        <cti:displayForPageEditModes modes="EDIT">
                            <th><i:inline key=".actions"/></th>
			            </cti:displayForPageEditModes>
			        </tr>
					<cti:displayForPageEditModes modes="VIEW">
					   <c:forEach var="answer" items="${question.answers}">
                       <tr>
                           <td><spring:escapeBody htmlEscape="true">${answer.answerKey}</spring:escapeBody> </td>
                       </tr>
					   </c:forEach>
					</cti:displayForPageEditModes>
			    </table>
	        </div>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:labeledImg nameKey="addAnswer" href="javascript: addAnswer()"/>
            </cti:displayForPageEditModes>
	    </tags:boxContainer2>
    </div>
</form:form>

</dialog:ajaxPage>
