<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:msgScope paths="modules.adminSetup.survey.list">

<cti:url var="submitUrl" value="saveDetails"/>
<form:form id="survey-details-form" modelAttribute="survey" action="${submitUrl}">
    <cti:csrfToken/>
    <form:hidden path="surveyId"/>
    <form:hidden path="energyCompanyId"/>
    <tags:nameValueContainer>
        <cti:msg2 var="fieldName" key=".name"/>
        <tags:nameValue name="${fieldName}">
            <tags:input id="surveyName" path="surveyName" size="30" maxlength="64"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".key"/>
        <tags:nameValue name="${fieldName}">
            <tags:input path="surveyKey" size="30" maxlength="64"/>
        </tags:nameValue>
    </tags:nameValueContainer>
</form:form>

</cti:msgScope>