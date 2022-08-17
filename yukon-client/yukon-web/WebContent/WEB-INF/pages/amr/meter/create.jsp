<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.widgets.meterInformationWidget">

<cti:url var="action" value="/meter/save"/>
<form:form id="meter-create-form" action="${action}" method="post" modelAttribute="meter">
    <cti:csrfToken/>
    <c:if test="${errorMessage != null}">
        <div class="user-message error">
            ${errorMessage}
        </div>
    </c:if>
    <tags:nameValueContainer2 naturalWidth="false" tableClass="with-form-controls">
        <tags:nameValue2 nameKey=".type">
            <tags:selectWithItems items="${meterTypes}" path="type" id="meter-type" groupItems="true" onchange="yukon.ami.meterDetails.updateMeterTypeFields()"/>
        </tags:nameValue2>
        <tags:inputNameValue nameKey=".deviceName" path="name" valueClass="full-width" maxlength="60" size="40" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE"/>
        <tags:inputNameValue nameKey=".meterNumber" path="meterNumber" maxlength="50" size="40" valueClass="js-meter-info-meter-number" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE"/>
        <tags:inputNameValue nameKey=".physicalAddress" path="address" maxlength="18" size="18" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE" rowClass="js-mct-fields"/>
        
        <tags:selectNameValue nameKey=".ports" items="${ports}" path="portId" itemLabel="paoName" itemValue="liteID" rowClass="js-ied-fields dn"/>
        
        <tags:selectNameValue nameKey=".route" items="${routes}" path="routeId" itemLabel="paoName" itemValue="liteID" rowClass="js-mct-fields"/>
        <tags:inputNameValue nameKey=".serialNumber" path="serialNumber" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE" rowClass="js-rf-fields dn"/>
        <tags:inputNameValue nameKey=".manufacturer" path="manufacturer" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE" rowClass="js-rf-fields dn"/>
        <tags:inputNameValue nameKey=".model" path="model" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE" rowClass="js-rf-fields dn"/>
        <tags:nameValue2 nameKey=".status">
            <tags:switchButton path="disabled" onNameKey=".enabled" offNameKey=".disabled" inverse="true"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form:form>

    <cti:toJson id="rf-meter-types" object="${rfMeterTypes}"/>
    <cti:toJson id="mct-meter-types" object="${mctMeterTypes}"/>
<style>
#contentPopup {
    overflow: visible !important;
}
</style>
<script>
    $("#meter-type").chosen({width: '250px;'});
</script>

</cti:msgScope>