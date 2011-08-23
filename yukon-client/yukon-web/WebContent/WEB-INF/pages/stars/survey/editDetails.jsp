<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.survey.list">

<script type="text/javascript">
submitForm = function() {
	return submitFormViaAjax('ajaxDialog', 'inputForm')
}
</script>

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/spring/stars/survey/saveDetails"/>
<form:form id="inputForm" commandName="survey" action="${submitUrl}"
    onsubmit="return submitForm()">
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

    <div class="actionArea">
        <cti:button nameKey="ok" type="submit"/>
        <cti:button nameKey="cancel" onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>

<script type="text/javascript">
$('surveyName').focus();
</script>
