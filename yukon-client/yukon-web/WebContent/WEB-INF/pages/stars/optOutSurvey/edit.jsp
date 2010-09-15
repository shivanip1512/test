<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.optOutSurvey.edit">

<script type="text/javascript">
specifyStopDateChecked = function() {
    setDateTimeInputEnabled('stopDate', $('specifyStopDateCheckbox').checked);
}

submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');
    return submitFormViaAjax('ajaxDialog', 'inputForm');
}
</script>

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/spring/stars/optOutSurvey/save"/>
<form:form id="inputForm" commandName="optOutSurvey" action="${submitUrl}"
    onsubmit="return submitForm()">
    <form:hidden path="optOutSurveyId"/>
    <c:forEach var="programId" items="${optOutSurvey.programIds}">
        <input type="hidden" name="programIds" value="${programId}"/>
    </c:forEach>
    <form:hidden path="surveyId"/>
    <form:hidden path="energyCompanyId"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".programs">
            <c:forEach var="programId" items="${optOutSurvey.programIds}" end="2">
                <spring:escapeBody htmlEscape="true">${programNamesById[programId]}</spring:escapeBody><br>
            </c:forEach>
            <c:if test="${fn:length(optOutSurvey.programIds) > 3}">
                <i:inline key=".ellipsis"/>
            </c:if>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".survey">
            <spring:escapeBody htmlEscape="true">${survey.surveyName}</spring:escapeBody>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".startDate">
            <tags:dateTimeInput path="startDate" fieldValue="${optOutSurvey.startDate}"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".stopDate" labelForId="specifyStopDateCheckbox">
            <form:checkbox id="specifyStopDateCheckbox" path="specifyStopDate"
                onclick="specifyStopDateChecked()"/>
            <label for="specifyStopDateCheckbox"><i:inline key=".specifyStopDate"/></label>
            <tags:dateTimeInput path="stopDate" fieldValue="${optOutSurvey.stopDate}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="actionArea">
        <input type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
specifyStopDateChecked();
</script>
