<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.dr.surveyList">

<h1 class="dialogQuestion">
    <cti:msg2 key=".confirmDelete" arguments="${optOutSurvey.surveyName}"/>
</h1>

<cti:url var="deleteUrl" value="/spring/stars/optOutSurvey/delete"/>
<form id="confirmForm" action="${deleteUrl}">
    <input type="hidden" name="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>

    <div class="actionArea">
        <input type="button" value="<cti:msg2 key=".ok"/>"
            onclick="submitFormViaAjax('ajaxDialog', 'confirmForm')"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="$('ajaxDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
