<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="meterConfig">
    <tags:setFormEditMode mode="${mode}"/>
    
    <cti:url var="updateMeterConfigUrl" value="/stars/operator/hardware/config/updateMeterConfig"/>
    <form:form modelAttribute="meter" action="${updateMeterConfigUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}"/>
        <form:hidden path="deviceId"/>
        <form:hidden path="disabled"/>
        <form:hidden path="name"/>
        <form:hidden path="route"/>
        <input type="hidden" name="deviceTypeId" value="${meter.paoType.deviceTypeId}">
        <input type="hidden" name="paoId" value="${meter.paoIdentifier.paoId}">
        
        
        <tags:nameValueContainer2>

            <tags:nameValue2 nameKey=".meterName">
                <spring:escapeBody htmlEscape="true">${meter.name}</spring:escapeBody>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".meterType">${meter.paoType.paoTypeName}</tags:nameValue2>

            <tags:nameValue2 nameKey=".meterNumber">
                <tags:input path="meterNumber"/>
            </tags:nameValue2>
            
            <c:if test="${meter.paoType.isPlc()}">
                <tags:nameValue2 nameKey=".physicalAddress">
                    <tags:input path="address"/>
                </tags:nameValue2>
    
                <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
            </c:if>
   
        </tags:nameValueContainer2>
        

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <cti:button nameKey="save" type="submit" classes="primary action"/>
                    <cti:button nameKey="cancel" name="cancel" type="submit"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>    
        </div>        
    </form:form>
</cti:standardPage>