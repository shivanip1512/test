<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="survey" page="reportConfig">

<c:set var="baseKey" value="yukon.web.surveys.${survey.surveyKey}"/>
<script type="text/javascript">
var answerMessagesById = {};
<c:forEach var="question" items="${questions}">
    <c:forEach var="answer" items="${question.answers}">
        answerMessagesById[${answer.surveyQuestionAnswerId}] = '<cti:msg2 javaScriptEscape="true" key="${baseKey}.${question.questionKey}.${answer.answerKey}"/>';
    </c:forEach>
</c:forEach>



function reportTypeChanged() {
    if (jQuery('#reportTypeSelect').val() === 'detail') {
        jQuery('#accountNumberRow').show();
        jQuery('#deviceSerialNumberRow').show();
    } else {
        jQuery('#accountNumberRow').hide();
        jQuery('#deviceSerialNumberRow').hide();
    }
}

var questionsById = ${cti:jsonString(questionsById)};

function selectAllChanged() {
    var checkboxes = jQuery('#answerList input'),
        checked = jQuery('#selectAll').prop("checked"),
        index;
    for (index = 0; index < checkboxes.length; index++) {
        if (jQuery(checkboxes[index]).attr('id') !== 'selectAll') {
            jQuery(checkboxes[index]).prop('checked', checked);
        }
    }
}

function answerCheckboxChanged() {
    var selectAllChecked = true,
        checkboxes = jQuery('#answerList input'),
        index;
    for (index = 0; index < checkboxes.length; index++) {
        if (jQuery(checkboxes[index]).attr('id') !== 'selectAll') {
            if (!jQuery(checkboxes[index]).prop("checked")) {
                selectAllChecked = false;
                break;
            }
        }
    }
    jQuery('#selectAll').prop("checked", selectAllChecked);
}

function questionChanged() {
    var answerList = jQuery('#answerList');
    answerList.remove('.generated');
    var questionId = jQuery('#questionSelect').val();
    var question = questionsById[questionId];
    if (question.questionType === 'DROP_DOWN') {
        var includeOtherAnswersItem = jQuery('#includeOtherAnswersItem');
        for (var index = 0; index < question.answers.length; index++) {
            var listItem = document.createElement('li');
            listItem.className = 'generated';
            var answer = question.answers[index];
            var answerId = answer.surveyQuestionAnswerId;
            listItem.innerHTML = '<input type="checkbox" name="answerId"' +
                ' onclick="answerCheckboxChanged()" id="answerId_' +
                answerId + '" value="' + answerId + '"><label for="answerId_' +
                answerId + '"> ' + answerMessagesById[answerId] + '</label>';
            answerList.insertBefore(listItem, includeOtherAnswersItem[0]);
        }
        if (question.textAnswerAllowed) {
            includeOtherAnswersItem.show();
        } else {
            includeOtherAnswersItem.hide();
        }
        if (question.answerRequired) {
            jQuery('#includeUnansweredItem').hide();
        } else {
            jQuery('#includeUnansweredItem').show();
        }
        jQuery('#answersRow').show();
    } else {
        jQuery('#answersRow').hide();
    }
    answerCheckboxChanged();
    // show all answers by default
    jQuery('#selectAll').prop("checked", true);
    selectAllChanged();
}

function programsChosen(programs) {
    if (programs && programs.length) {
        jQuery('#noProgramsSelected').hide();
    } else {
        jQuery('#noProgramsSelected').show();
    }
    return true;
}

function updateFieldsFromBackingBean() {
    var initialAnswers,
        index;

    //deselect everything
    jQuery("#answerList input:checkbox").each(
        function(idx, el) {
            jQuery(el).prop("checked", false);
    });
    
    initialAnswers = ${cti:jsonString(reportConfig.answerId)};
    for (index = 0; index < initialAnswers.length; index++) {
        jQuery('#answerId_' + initialAnswers[index]).prop("checked", true);
    }

    if(${cti:jsonString(reportConfig.includeUnanswered)}) {
        jQuery('#includeUnanswered').prop("checked", true);
    }
    
    if(${cti:jsonString(reportConfig.includeOtherAnswers)}) {
        jQuery('#includeOtherAnswers').prop("checked", true);
    }

    //if no report type exists, select all
    if(!${cti:jsonString(reportConfig.reportType)}) {
        jQuery('#selectAll').prop("checked", true);
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
<cti:url var="submitUrl" value="/stars/surveyReport/report"/>
<form:form action="${submitUrl}" method="GET" commandName="reportConfig">
    <form:hidden path="surveyId"/>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".reportType">
            <form:select id="reportTypeSelect" path="reportType" onchange="reportTypeChanged()">
                <form:option value="summary"><i:inline key=".summary"/></form:option>
                <form:option value="detail"><i:inline key=".detail"/></form:option>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".startDate">
            <dt:date path="startDate" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".endDate">
            <dt:date path="stopDate" />
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
            <tags:pickerDialog id="programPicker" type="lmProgramPicker"
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

  jQuery(function() {
    questionChanged();
    reportTypeChanged();
    updateFieldsFromBackingBean();
  });

</script>

</cti:standardPage>
