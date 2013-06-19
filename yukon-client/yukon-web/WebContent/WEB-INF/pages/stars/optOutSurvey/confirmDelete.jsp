<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.surveyList">

<h4 class="dialogQuestion stacked">
    <cti:msg2 key=".confirmDelete" arguments="${optOutSurvey.surveyName}"/>
</h4>

<cti:url var="deleteUrl" value="/stars/optOutSurvey/delete"/>
<form id="confirmForm" action="${deleteUrl}">
    <input type="hidden" name="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>

    <div class="actionArea">
        <cti:button nameKey="ok" onclick="submitFormViaAjax('ajaxDialog', 'confirmForm')" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="jQuery('#ajaxDialog').dialog('close');"/>
    </div>
</form>

</cti:msgScope>