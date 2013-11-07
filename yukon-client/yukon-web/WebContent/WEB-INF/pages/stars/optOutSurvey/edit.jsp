<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>


<cti:msgScope paths="modules.dr.surveyEdit">

<script type="text/javascript">
specifyStopDateChecked = function () {
    jQuery('#stopDate').prop('disabled', !jQuery('#specifyStopDateCheckbox').is(":checked"));
}

submitForm = function () {
    return submitFormViaAjax('ajaxDialog', 'optOutSurveyForm');
}
jQuery( function () {
    // init dateTime fields dynamically brought onto page after initial page load
    Yukon.ui.initDateTimePickers();
});
</script>

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/stars/optOutSurvey/save"/>
<form:form id="optOutSurveyForm" commandName="optOutSurveyDto" action="${submitUrl}"
    onsubmit="return submitForm()">
    <form:hidden path="optOutSurveyId"/>
    <form:hidden path="energyCompanyId"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".programs">
            <tags:bind path="programIds">
                <tags:pickerDialog type="lmProgramPicker"
                    id="programPicker" selectionProperty="paoName"
                    destinationFieldName="programIds" initialIds="${optOutSurveyDto.programIds}"
                    multiSelectMode="true" linkType="selection" useInitialIdsIfEmpty="true"
                    extraArgs="${optOutSurveyDto.energyCompanyId}"/>
            </tags:bind>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".survey">
            <tags:bind path="surveyId">
                <tags:pickerDialog type="surveyPicker" id="surveyPicker" selectionProperty="surveyName"
                    destinationFieldName="surveyId" initialId="${optOutSurveyDto.surveyId}"
                    immediateSelectMode="true" linkType="selection" useInitialIdsIfEmpty="true"/>
            </tags:bind>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".startDate">
            <dt:dateTime id="startDate" path="startDate" value="${optOutSurveyDto.startDate}"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".stopDate" labelForId="specifyStopDateCheckbox">
            <form:checkbox id="specifyStopDateCheckbox" path="specifyStopDate"
                onclick="specifyStopDateChecked()"/>
            <label for="specifyStopDateCheckbox"><i:inline key=".specifyStopDate"/></label>
            <dt:dateTime id="stopDate" path="stopDate" value="${optOutSurveyDto.stopDate}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="action-area">
        <cti:button nameKey="ok" type="submit" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="jQuery('#ajaxDialog').dialog('close');"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
jQuery( function () {
    specifyStopDateChecked();
});
</script>
