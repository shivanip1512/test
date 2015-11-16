<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.surveyList">

<p><i:inline key=".confirmDelete" arguments="${optOutSurvey.surveyName}"/></p>

<cti:url var="deleteUrl" value="/stars/optOutSurvey/delete"/>
<form action="${deleteUrl}">
    <cti:csrfToken/>
    <input type="hidden" name="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>
</form>

</cti:msgScope>