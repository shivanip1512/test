<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:choose>
    <c:when test="${showCarrierSettings}">
        <cti:url var="action" value="/widget/meterInformationWidget/edit-plc"/>
    </c:when>
    <c:otherwise>
        <cti:url var="action" value="/widget/meterInformationWidget/edit-rf"/>
    </c:otherwise>
</c:choose>

<form:form id="meter-info-edit" action="${action}" method="post" commandName="meter">
    <cti:csrfToken/>
    <form:hidden path="deviceId"/>
    <input type="hidden" name="shortName" value="meterInformationWidget">
    <tags:nameValueContainer2 naturalWidth="false" tableClass="with-form-controls">
        <tags:inputNameValue nameKey=".deviceName" path="name" valueClass="full-width" maxlength="60" size="40"/>
        <tags:inputNameValue nameKey=".meterNumber" path="meterNumber" maxlength="50" size="40"/>
        <c:if test="${showCarrierSettings}">
            <tags:inputNameValue nameKey=".physicalAddress" path="address" maxlength="18" size="18"/>
            <c:if test="${routable}">
                <tags:selectNameValue nameKey=".route" items="${routes}" path="routeId" itemLabel="paoName" itemValue="liteID"/>
            </c:if>
        </c:if>
        <c:if test="${showRFMeshSettings}">
            <tags:inputNameValue nameKey=".serialNumber" path="serialNumber"/>
            <tags:inputNameValue nameKey=".manufacturer" path="manufacturer"/>
            <tags:inputNameValue nameKey=".model" path="model"/>
        </c:if>
        <tags:nameValue2 nameKey=".status">
            <tags:switchButton path="disabled" greenNameKey="enabled" redNameKey="disabled" inverse="true"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form:form>