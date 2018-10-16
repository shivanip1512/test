<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="warehouse.${mode}">
    
     <tags:setFormEditMode mode="${mode}"/>
     
    <cti:url value="${baseUrl}/update" var="action">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    
     <form:form modelAttribute="warehouseDto" action="${action}" name="warehouseForm">
        <cti:csrfToken/>
        <tags:hidden path="warehouse.warehouseID"/>
        <tags:hidden path="warehouse.addressID"/>
        <tags:hidden path="warehouse.energyCompanyID"/>
        <tags:hidden path="address.addressID"/>
            
        <tags:nameValueContainer2>
            
            <tags:inputNameValue nameKey=".name" path="warehouse.warehouseName" size="50" maxlength="60"/>
            <tags:inputNameValue nameKey=".locationAddress1" path="address.locationAddress1" size="30" maxlength="100"/>
            <tags:inputNameValue nameKey=".locationAddress2" path="address.locationAddress2" size="30" maxlength="100"/>
            <tags:inputNameValue nameKey=".cityName" path="address.cityName"  size="25" maxlength="32"/>
            <tags:inputNameValue nameKey=".stateCode" path="address.stateCode" size="2" maxlength="2"/>
            <tags:inputNameValue nameKey=".zipCode" path="address.zipCode" size="12" maxlength="12"/>
            <tags:hidden path="address.county"/>
            
            <tags:textareaNameValue nameKey=".notes" rows="3" cols="40" path="warehouse.notes"></tags:textareaNameValue>
        </tags:nameValueContainer2>
        
        <div class="page-action-area">
            <!-- Save/Update -->
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="save" name="create" type="submit" classes="action primary"/>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" name="update" type="submit" classes="action primary"/>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="delete" name="delete" type="submit" classes="delete"/>
                <d:confirm on="button.delete" nameKey="confirmDelete"/>
            </cti:displayForPageEditModes>

            <!-- Cancel -->
            <cti:url var="warehouseIndexUrl" value="${baseUrl}/home">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <cti:button nameKey="cancel" href="${warehouseIndexUrl}"/>
        </div>
     </form:form>
  
</cti:standardPage>