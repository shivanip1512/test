<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.survey.edit">

<span id="templateIcons" style="display: none">
<cti:img key="up" href="#"/>
<cti:img key="down" href="#"/>
<cti:img key="deleteAnswer" href="#"/>
<cti:img key="up.disabled"/>
<cti:img key="down.disabled"/>
</span>

<script type="text/javascript">
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
            <form:checkbox id="answerRequiredCheckbox" path="answerRequired"/>
            <label for="answerRequiredCheckbox">
                <i:inline key=".answerRequiredDescription"/>
            </label>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".questionType">
            <form:select path="questionType" id="questionType"
                onchange="questionTypeChanged()">
                <c:forEach var="questionType" items="${questionTypes}">
                    <cti:msg var="optionLabel" key="${questionType}"/>
                    <form:option value="${questionType}" label="${optionLabel}"/>
                </c:forEach>
            </form:select>
        </tags:nameValue2>

        <tags:nameValue2 rowId="textAnswerAllowed_DROP_DOWN" rowClass="additionalInfo"
            nameKey=".textAnswerAllowed" labelForId="textAnswerAllowedCheckbox">
            <form:checkbox id="textAnswerAllowedCheckbox" path="textAnswerAllowed"/>
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
			            <th><i:inline key=".actions"/></th>
			        </tr>
			    </table>
	        </div>
	        <cti:labeledImg key="addAnswer" href="javascript: addAnswer()"/>
	    </tags:boxContainer2>
    </div>

    <div class="actionArea">
        <input type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
questionTypeChanged();
$('questionKey').focus();
$('questionKey').select();
</script>
