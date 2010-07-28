<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.survey.list">

<h1 class="dialogQuestion">
    <cti:msg2 key=".confirmDelete" arguments="${survey.surveyName}"/>
</h1>

<cti:url var="deleteUrl" value="/spring/stars/survey/delete"/>
<form id="confirmForm" action="${deleteUrl}">
    <input type="hidden" name="surveyId" value="${survey.surveyId}"/>

    <div class="actionArea">
        <input type="button" value="<cti:msg2 key=".ok"/>"
            onclick="submitFormViaAjax('ajaxDialog', 'confirmForm')"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="$('ajaxDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
