<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="meterConfig">
    <tags:setFormEditMode mode="${mode}"/>
    
    <cti:includeCss link="/WebConfig/yukon/styles/operator/hardwareConfig.css"/>

    <form:form id="updateForm" commandName="meter" action="/spring/stars/operator/hardware/config/updateMeterConfig">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <form:hidden path="deviceId"/>
        <form:hidden path="disabled"/>
        <form:hidden path="name"/>
        <form:hidden path="route"/>
        <form:hidden path="paoType"/>
        
        <tags:formElementContainer nameKey="meterConfigurationSection">
        
            <tags:nameValueContainer2>
    
                <tags:nameValue2 nameKey=".meterName">
                    <spring:escapeBody htmlEscape="true">${meter.name}</spring:escapeBody>
                </tags:nameValue2>
    
                <tags:nameValue2 nameKey=".meterType">
                    ${meter.paoType.paoTypeName}
                </tags:nameValue2>
    
                <tags:nameValue2 nameKey=".meterNumber">
                    <tags:input path="meterNumber"/>
                </tags:nameValue2>
    
                <tags:nameValue2 nameKey=".physicalAddress">
                    <tags:input path="address"/>
                </tags:nameValue2>
    
                <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
    
            </tags:nameValueContainer2>
        
        </tags:formElementContainer>

        <br>
        <br>
        
        <cti:displayForPageEditModes modes="EDIT">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 formId="updateForm" key="save" />
                <button type="submit" name="cancel" class="formSubmit">
                        <i:inline key=".cancel"/>
                    </button>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>    
    </form:form>
</cti:standardPage>