<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<cti:url var="action" value="/widget/relayInformationWidget/edit"/>

<form:form id="relay-info-form" action="${action}" method="put" modelAttribute="relay">

    <cti:csrfToken/>     
    
    <form:hidden cssClass="js-relay-info-id" path="deviceId"/>

    <input type="hidden" name="shortName" value="relayInformationWidget">
    <input type="hidden" name="identity" value="false">
    
    <tags:nameValueContainer2 naturalWidth="false" tableClass="with-form-controls">
        <tags:inputNameValue nameKey=".deviceName" path="name" valueClass="full-width" maxlength="60" size="40"/>
        <tags:inputNameValue nameKey=".serialNumber" path="serialNumber"/>
        <tags:inputNameValue nameKey=".manufacturer" path="manufacturer"/>
        <tags:inputNameValue nameKey=".model" path="model"/>
    </tags:nameValueContainer2>
</form:form>