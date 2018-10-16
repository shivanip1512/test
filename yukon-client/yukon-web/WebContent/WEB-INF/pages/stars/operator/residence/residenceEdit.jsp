<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="residence">

    <style>.short-select select {max-width: 150px;}</style>
    <cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <tags:setFormEditMode mode="${mode}"/>

    <cti:url var="submitUrl" value="/stars/operator/residence/residenceUpdate"/>
	<form:form modelAttribute="customerResidence" action="${submitUrl}" method="POST">
	    <cti:csrfToken/>
		<input type="hidden" name="accountId" value="${accountId}"/>
		<form:hidden path="accountSiteId"/>

		<div class="column-12-12 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                
                    <tags:yukonListEntrySelectNameValue nameKey=".residenceType" path="residenceTypeId" energyCompanyId="${energyCompanyId}" listName="RESIDENCE_TYPE" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".constructionMaterial" path="constructionMaterialId" energyCompanyId="${energyCompanyId}" listName="CONSTRUCTION_MATERIAL" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".decadeBuilt" path="decadeBuiltId" energyCompanyId="${energyCompanyId}" listName="DECADE_BUILT" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".squareFeet" path="squareFeetId" energyCompanyId="${energyCompanyId}" listName="SQUARE_FEET" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".insulationDepth" path="insulationDepthId" energyCompanyId="${energyCompanyId}" listName="INSULATION_DEPTH" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".generalCondition" path="generalConditionId" energyCompanyId="${energyCompanyId}" listName="GENERAL_CONDITION" valueClass="short-select"/>
                
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <tags:nameValueContainer2>
                
                    <tags:yukonListEntrySelectNameValue nameKey=".mainCoolingSystem" path="mainCoolingSystemId" energyCompanyId="${energyCompanyId}" listName="COOLING_SYSTEM" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".mainHeatingSystem" path="mainHeatingSystemId" energyCompanyId="${energyCompanyId}" listName="HEATING_SYSTEM" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".numberOfOccupants" path="numberOfOccupantsId" energyCompanyId="${energyCompanyId}" listName="NUM_OF_OCCUPANTS" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".ownershipType" path="ownershipTypeId" energyCompanyId="${energyCompanyId}" listName="OWNERSHIP_TYPE" valueClass="short-select"/>
                    <tags:yukonListEntrySelectNameValue nameKey=".mainFuelType" path="mainFuelTypeId" energyCompanyId="${energyCompanyId}" listName="FUEL_TYPE" valueClass="short-select"/>
                    <tags:textareaNameValue nameKey=".notes" path="notes" rows="4" cols="25"/>
                    
                </tags:nameValueContainer2>
            </div>
        </div>
			
		<%-- BUTTONS --%>
        <div class="page-action-area">
    		<cti:displayForPageEditModes modes="EDIT">
                <cti:button nameKey="save" type="submit" classes="js-blocker primary action"/>
                <cti:url value="/stars/operator/residence/view" var="viewUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
    			<cti:button nameKey="cancel" href="${viewUrl}"/>
    		</cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <cti:url value="/stars/operator/residence/edit" var="editUrl">
                        <cti:param name="accountId" value="${accountId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
        </div>

	</form:form>
	</cti:checkEnergyCompanyOperator>
</cti:standardPage>