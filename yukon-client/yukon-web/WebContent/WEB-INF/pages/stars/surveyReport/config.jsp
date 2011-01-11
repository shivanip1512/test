<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="survey" page="reportConfig">

<cti:includeCss link="/WebConfig/yukon/styles/operator/survey.css"/>

<c:set var="baseKey" value="yukon.web.surveys.${survey.surveyKey}"/>
<script type="text/javascript">
var answerMessagesById = {};
<c:forEach var="question" items="${questions}">
    <c:forEach var="answer" items="${question.answers}">
        answerMessagesById[${answer.surveyQuestionAnswerId}] = '<cti:msg2 javaScriptEscape="true" key="${baseKey}.${question.questionKey}.${answer.answerKey}"/>';
    </c:forEach>
</c:forEach>



function reportTypeChanged() {
	if ($F('reportTypeSelect') === 'detail') {
		$('accountNumberRow').show();
		$('deviceSerialNumberRow').show();
	} else {
        $('accountNumberRow').hide();
        $('deviceSerialNumberRow').hide();
	}
}

var questionsById = ${cti:jsonString(questionsById)};

function selectAllChanged() {
	var checkboxes = $$('#answerList input');
	var checked = $('selectAll').checked;
	for (var index = 0; index < checkboxes.length; index++) {
		if (checkboxes[index].id != 'selectAll') {
			checkboxes[index].checked = checked;
		}
	}
}

function answerCheckboxChanged() {
	var selectAllChecked = true;
    var checkboxes = $$('#answerList input');
    for (var index = 0; index < checkboxes.length; index++) {
        if (checkboxes[index].id != 'selectAll') {
            if (!checkboxes[index].checked) {
                selectAllChecked = false;
                break;
            }
        }
    }
    $('selectAll').checked = selectAllChecked;
}

function questionChanged() {
	var answerList = $('answerList');
    var listItems = answerList.getElementsBySelector('.generated');
    for (var index = 0; index < listItems.length; index++) {
        answerList.removeChild(listItems[index]);
    }
    var questionId = $F('questionSelect');
    var question = questionsById[questionId];
	if (question.questionType == 'DROP_DOWN') {
        var includeOtherAnswersItem = $('includeOtherAnswersItem');
	    for (var index = 0; index < question.answers.length; index++) {
	        var listItem = document.createElement('li');
	        listItem.className = 'generated';
	        var answer = question.answers[index];
	        var answerId = answer.surveyQuestionAnswerId;
	        listItem.innerHTML = '<input type="checkbox" name="answerId"' +
	            ' onclick="answerCheckboxChanged()" id="answerId_' +
	            answerId + '" value="' + answerId + '"><label for="answerId_' +
	            answerId + '"> ' + answerMessagesById[answerId] + '</label>';
	        answerList.insertBefore(listItem, includeOtherAnswersItem);
	    }
	    if (question.textAnswerAllowed) {
	    	includeOtherAnswersItem.show();
	    } else {
	    	includeOtherAnswersItem.hide();
	    }
	    if (question.answerRequired) {
            $('includeUnansweredItem').hide();
        } else {
            $('includeUnansweredItem').show();
        }
        $('answersRow').show();
	} else {
		$('answersRow').hide();
	}
	answerCheckboxChanged();
    // show all answers by default
    $('selectAll').checked = true;
    selectAllChanged();
}

function programsChosen(programs) {
    if (programs && programs.length) {
    	$('noProgramsSelected').hide();
    } else {
        $('noProgramsSelected').show();
    }
	return true;
}

function updateFieldsFromBackingBean() {
    //deselect everything
    $$("#answerList input:checkbox").each(
            function(el) {
                el.setValue(false);
            });
    
	var initialAnswers = ${cti:jsonString(reportConfig.answerId)};
	for (var index = 0; index < initialAnswers.length; index++) {
	    $('answerId_' + initialAnswers[index]).checked = true;
	}

    if(${cti:jsonString(reportConfig.includeUnanswered)}) {
        $('includeUnanswered').checked = true;
    }
    
    if(${cti:jsonString(reportConfig.includeOtherAnswers)}) {
        $('includeOtherAnswers').checked = true;
    }

    //if no report type exists, select all
    if(!${cti:jsonString(reportConfig.reportType)}) {
        $('selectAll').checked = true;
        selectAllChanged();
    } else {
        answerCheckboxChanged();
    }
}
</script>

<cti:msg2 var="surveyTitle" key="${baseKey}.title" blankIfMissing="true"/>
<c:if test="${empty surveyTitle}">
    <c:set var="surveyTitle" value="${survey.surveyName}"/>
</c:if>
<spring:escapeBody htmlEscape="true">${surveyTitle}</spring:escapeBody>

<c:set var="baseKey" value="yukon.web.surveys.${survey.surveyKey}"/>
<cti:url var="submitUrl" value="/spring/stars/surveyReport/report"/>
<form:form action="${submitUrl}" commandName="reportConfig">
    <form:hidden path="surveyId"/>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".reportType">
		    <form:select id="reportTypeSelect" path="reportType" onchange="reportTypeChanged()">
		        <form:option value="summary"><i:inline key=".summary"/></form:option>
		        <form:option value="detail"><i:inline key=".detail"/></form:option>
		    </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".startDate">
            <tags:dateInputCalendar fieldName="startDate" springInput="true"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".endDate">
            <tags:dateInputCalendar fieldName="stopDate" springInput="true"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".question">
            <form:select id="questionSelect" path="questionId" onchange="questionChanged()">
                <c:forEach var="question" items="${questions}">
                    <form:option value="${question.surveyQuestionId}">
                        <i:inline key="${baseKey}.${question.questionKey}"/>
                    </form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".answers" rowId="answersRow">
            <tags:bind path="answerId">
                <ul id="answerList">
                    <li id="selectAllItem">
                        <input type="checkbox" name="selectAll" id="selectAll"
                            onclick="selectAllChanged()">
                        <label for="selectAll"><i:inline key=".selectAll"/></label>
                    </li>
                    <li id="includeOtherAnswersItem">
                        <tags:checkbox path="includeOtherAnswers" id="includeOtherAnswers"
                            onclick="answerCheckboxChanged()"/>
                        <label for="includeOtherAnswers"><i:inline key=".includeOtherAnswers"/></label>
                    </li>
                    <li id="includeUnansweredItem">
                        <tags:checkbox path="includeUnanswered" id="includeUnanswered"
                            onclick="answerCheckboxChanged()"/>
                        <label for="includeUnanswered"><i:inline key=".includeUnanswered"/></label>
                    </li>
                </ul>
            </tags:bind>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".programs">
            <tags:pickerDialog id="programPicker" type="lmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker"
                destinationFieldName="programId" extraArgs="${energyCompanyId}"
                endAction="programsChosen" multiSelectMode="true" linkType="selection"
                allowEmptySelection="true"
                selectionProperty="paoName" initialIds="${reportConfig.programIds}"/>
            <div id="noProgramsSelected">
                <i:inline key=".noProgramsSelected"/>
            </div>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".accountNumber" rowId="accountNumberRow">
            <tags:input path="accountNumber"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".deviceSerialNumber" rowId="deviceSerialNumberRow">
            <tags:input path="deviceSerialNumber"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="pageActionArea">
        <input type="submit" value="<cti:msg2 key=".showReport"/>">
    </div>
</form:form>

<script type="text/javascript">

Event.observe(window, 'load', function() {
    questionChanged();
    reportTypeChanged();
    updateFieldsFromBackingBean();
  });

</script>

</cti:standardPage>
