<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="residence">
<tags:setFormEditMode mode="${mode}"/>

    <cti:url var="submitUrl" value="/spring/stars/operator/residence/residenceUpdate"/>
	<form:form commandName="customerResidence" action="${submitUrl}" method="POST">
	
		<input type="hidden" name="accountId" value="${accountId}"/>
		<form:hidden path="accountSiteId"/>
	
		<tags:formElementContainer nameKey="customerResidenceSection">
	
			<cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:40px;">
	
				<%-- COLUMN 1 --%>
				<cti:dataGridCell>
				
					<tags:nameValueContainer2>
					
						<tags:yukonListEntrySelectNameValue nameKey=".residenceType" path="residenceTypeId" energyCompanyId="${energyCompanyId}" listName="RESIDENCE_TYPE"/>
						<tags:yukonListEntrySelectNameValue nameKey=".constructionMaterial" path="constructionMaterialId" energyCompanyId="${energyCompanyId}" listName="CONSTRUCTION_MATERIAL"/>
						<tags:yukonListEntrySelectNameValue nameKey=".decadeBuilt" path="decadeBuiltId" energyCompanyId="${energyCompanyId}" listName="DECADE_BUILT"/>
						<tags:yukonListEntrySelectNameValue nameKey=".squareFeet" path="squareFeetId" energyCompanyId="${energyCompanyId}" listName="SQUARE_FEET"/>
						<tags:yukonListEntrySelectNameValue nameKey=".insulationDepth" path="insulationDepthId" energyCompanyId="${energyCompanyId}" listName="INSULATION_DEPTH"/>
						<tags:yukonListEntrySelectNameValue nameKey=".generalCondition" path="generalConditionId" energyCompanyId="${energyCompanyId}" listName="GENERAL_CONDITION"/>
					
					</tags:nameValueContainer2>
	
				</cti:dataGridCell>
				
				<%-- COLUMN 2 --%>
				<cti:dataGridCell>
				
					<tags:nameValueContainer2>
					
						<tags:yukonListEntrySelectNameValue nameKey=".mainCoolingSystem" path="mainCoolingSystemId" energyCompanyId="${energyCompanyId}" listName="COOLING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainHeatingSystem" path="mainHeatingSystemId" energyCompanyId="${energyCompanyId}" listName="HEATING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".numberOfOccupants" path="numberOfOccupantsId" energyCompanyId="${energyCompanyId}" listName="NUM_OF_OCCUPANTS"/>
						<tags:yukonListEntrySelectNameValue nameKey=".ownershipType" path="ownershipTypeId" energyCompanyId="${energyCompanyId}" listName="OWNERSHIP_TYPE"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainFuelType" path="mainFuelTypeId" energyCompanyId="${energyCompanyId}" listName="FUEL_TYPE"/>
						<tags:textareaNameValue nameKey=".notes" path="notes" rows="4" cols="20"/>
						
					</tags:nameValueContainer2>
				
				</cti:dataGridCell>
			
			</cti:dataGrid>
		
		</tags:formElementContainer>
		
		<%-- BUTTONS --%>
        <div class="pageActionArea">
		<cti:displayForPageEditModes modes="EDIT">
            <cti:button nameKey="save" type="submit" styleClass="f_blocker"/>
            <cti:url value="/spring/stars/operator/residence/view" var="viewUrl">
                <cti:param name="accountId" value="${accountId}"/>
            </cti:url>
			<cti:button nameKey="cancel" href="${viewUrl}"/>
		</cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:url value="/spring/stars/operator/residence/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
                <cti:button nameKey="edit" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        </div>

	</form:form>
	
</cti:standardPage>