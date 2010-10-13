<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.surveyList">

<h1 class="dialogQuestion">
    <cti:msg2 key=".confirmDelete" arguments="${optOutSurvey.surveyName}"/>
</h1>

<cti:url var="deleteUrl" value="/spring/stars/optOutSurvey/delete"/>
<form id="confirmForm" action="${deleteUrl}">
    <input type="hidden" name="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>

    <div class="actionArea">
        <cti:button key="ok" onclick="submitFormViaAjax('ajaxDialog', 'confirmForm')"/>
        <cti:button key="cancel" onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
