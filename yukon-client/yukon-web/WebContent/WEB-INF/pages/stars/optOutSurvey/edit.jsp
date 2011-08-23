<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.dr.surveyEdit">

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
<form:form id="inputForm" commandName="optOutSurveyDto" action="${submitUrl}"
    onsubmit="return submitForm()">
    <form:hidden path="optOutSurveyId"/>
    <form:hidden path="energyCompanyId"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".programs">
            <tags:bind path="programIds">
                <tags:pickerDialog type="lmDirectProgramPaoPermissionCheckingByEnergyCompanyIdPicker"
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
            <tags:dateTimeInput path="startDate" fieldValue="${optOutSurveyDto.startDate}"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".stopDate" labelForId="specifyStopDateCheckbox">
            <form:checkbox id="specifyStopDateCheckbox" path="specifyStopDate"
                onclick="specifyStopDateChecked()"/>
            <label for="specifyStopDateCheckbox"><i:inline key=".specifyStopDate"/></label>
            <tags:dateTimeInput path="stopDate" fieldValue="${optOutSurveyDto.stopDate}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="actionArea">
        <cti:button nameKey="ok" type="submit"/>
        <cti:button nameKey="cancel" onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
specifyStopDateChecked();
</script>
