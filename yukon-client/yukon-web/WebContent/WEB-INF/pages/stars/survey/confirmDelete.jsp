<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.survey.list">

<h1 class="dialogQuestion">
    <cti:msg2 key=".confirmDelete" arguments="${survey.surveyName}"/>
</h1>

<cti:url var="deleteUrl" value="/spring/stars/survey/delete"/>
<form id="confirmForm" action="${deleteUrl}">
    <input type="hidden" name="surveyId" value="${survey.surveyId}"/>

    <div class="actionArea">
        <cti:button nameKey="ok" onclick="submitFormViaAjax('ajaxDialog', 'confirmForm')"/>
        <cti:button nameKey="cancel" onclick="parent.$('ajaxDialog').hide()"/>
    </div>
</form>

</cti:msgScope>
