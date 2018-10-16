<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.surveyEdit">

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/stars/optOutSurvey/save"/>
<form:form modelAttribute="optOutSurveyDto" action="${submitUrl}">
    <cti:csrfToken/>
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
            <dt:dateTime path="startDate" value="${optOutSurveyDto.startDate}"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".stopDate" labelForId="specify-stopdate-cb">
            <form:checkbox id="specify-stopdate-cb" path="specifyStopDate"/>
            <label for="specify-stopdate-cb"><i:inline key=".specifyStopDate"/></label>
            <c:set var="stopDateDisabled" value="${optOutSurveyDto.specifyStopDate}"/>
            <dt:dateTime id="survey-stopdate" path="stopDate" value="${optOutSurveyDto.stopDate}" 
                disabled="${!stopDateDisabled}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

</form:form>

</cti:msgScope>