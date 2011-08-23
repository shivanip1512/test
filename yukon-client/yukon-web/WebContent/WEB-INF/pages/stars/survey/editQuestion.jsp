<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<tags:setFormEditMode mode="${mode}"/>

<cti:msgScope paths="modules.survey.edit">

<span id="templateIcons" style="display: none">
<cti:img nameKey="up" href="#"/>
<cti:img nameKey="down" href="#"/>
<cti:img nameKey="deleteAnswer" href="#"/>
<cti:img nameKey="up.disabled"/>
<cti:img nameKey="down.disabled"/>
</span>

<script type="text/javascript">
<cti:displayForPageEditModes modes="EDIT">
var templateIcons = $$('#templateIcons > a')
moveUpIcon = templateIcons[0];
moveDownIcon = templateIcons[1];
deleteAnswerIcon = templateIcons[2];

var templateIcons = $$('#templateIcons > img')
moveUpDisabledIcon = templateIcons[0];
moveDownDisabledIcon = templateIcons[1];

submitForm = function() {
	return submitFormViaAjax('ajaxDialog', 'inputForm')
}

initWithAnswerKeys(${cti:jsonString(answerKeys)});
</cti:displayForPageEditModes>

questionTypeChanged = function() {
	questionType = $('questionType').value;
	var divs = $$('.additionalInfo');
	for (var index = 0; index < divs.length; index++) {
		var div = divs[index];
	    if (div.id.endsWith('_' + questionType)) {
	        div.show();
	    } else {
	        div.hide();
	    }
	}
}
</script>

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/spring/stars/survey/saveQuestion"/>
<form:form id="inputForm" commandName="question" action="${submitUrl}"
    onsubmit="return submitForm()">
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
	            <form:select path="questionType" id="questionType"
	                onchange="questionTypeChanged()">
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

    <div class="actionArea">
        <cti:displayForPageEditModes modes="EDIT">
            <cti:button nameKey="ok" type="submit"/>
        </cti:displayForPageEditModes>
        <cti:button nameKey="cancel" onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
questionTypeChanged();
<cti:displayForPageEditModes modes="EDIT">
$('questionKey').focus();
$('questionKey').select();
</cti:displayForPageEditModes>
</script>
