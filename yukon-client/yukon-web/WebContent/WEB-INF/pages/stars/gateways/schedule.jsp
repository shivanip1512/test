<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">


<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<cti:url var="url" value="/stars/gateways/${id}/schedule"/>
<form:form id="gateway-schedule-form" action="${url}" method="post" commandName="schedule">
    <cti:csrfToken/>
    
    <cti:uniqueIdentifier var="uid" prefix="schedule"/>
    <input type="hidden" name="uid" value="${uid}">
    <tags:cronExpressionData id="${uid}" state="${state}" allowTypeChange="false"/>
    
</form:form>

</cti:msgScope>