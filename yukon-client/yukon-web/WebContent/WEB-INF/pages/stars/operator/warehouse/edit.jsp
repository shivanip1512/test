<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="warehouse.${mode}">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/energyCompany.css"/>
    
     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="${updateUrl}" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="${createUrl}" var="action"/>
    </cti:displayForPageEditModes>
    
     <form:form commandName="warehouseDto" action="${action}">
        <input type="hidden" name="ecId" value="${ecId}"/>
        
        <input type="hidden" name="warehouse.warehouseID" value="${warehouseDto.warehouse.warehouseID}"/>
        <input type="hidden" name="warehouse.addressID" value="${warehouseDto.warehouse.addressID}"/>
        <input type="hidden" name="warehouse.energyCompanyID" value="${warehouseDto.warehouse.energyCompanyID}"/>
        <input type="hidden" name="address.addressID" value="${warehouseDto.warehouse.addressID}"/>
            
        <tags:nameValueContainer2>
            
            <tags:inputNameValue nameKey=".name" path="warehouse.warehouseName" size="60" maxlength="60"/>
            <tags:inputNameValue nameKey=".locationAddress1" path="address.locationAddress1" size="40" maxlength="40"/>
            <tags:inputNameValue nameKey=".locationAddress2" path="address.locationAddress2" size="40" maxlength="40"/>
            <tags:inputNameValue nameKey=".cityName" path="address.cityName"  size="32" maxlength="32"/>
            <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" size="2" maxlength="2"/>
            <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" size="12" maxlength="12"/>
            <tags:hidden path="address.county"/>
            
            <tags:textareaNameValue nameKey=".notes" rows="3" cols="40" path="warehouse.notes"></tags:textareaNameValue>
        </tags:nameValueContainer2>
        
        <button type="submit" name="save" class="formSubmit">
            <i:inline key=".save"/>
        </button>
        <cti:url var="warehouseIndexUrl" value="${indexUrl}">
            <cti:param name="ecId" value="${ecId}"/>
        </cti:url>
        <cti:button key="cancel" onclick="javascript:window.location ='${warehouseIndexUrl}'"/>
     </form:form>
</cti:standardPage>