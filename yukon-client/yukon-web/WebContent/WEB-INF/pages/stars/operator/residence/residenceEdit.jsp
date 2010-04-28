<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="residence">
<tags:setFormEditMode mode="${mode}"/>

	<form:form id="residenceUpdateForm" commandName="customerResidence" action="/spring/stars/operator/residence/residenceUpdate">
	
		<input type="hidden" name="accountId" value="${accountId}"/>
		<form:hidden path="accountSiteId"/>
	
		<tags:formElementContainer nameKey="customerResidenceSection">
	
			<cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:40px;">
	
				<%-- COLUMN 1 --%>
				<cti:dataGridCell>
				
					<tags:nameValueContainer2>
					
						<tags:yukonListEntrySelectNameValue nameKey=".residenceType" path="residenceTypeId" accountId="${accountId}" listName="RESIDENCE_TYPE"/>
						<tags:yukonListEntrySelectNameValue nameKey=".constructionMaterial" path="constructionMaterialId" accountId="${accountId}" listName="CONSTRUCTION_MATERIAL"/>
						<tags:yukonListEntrySelectNameValue nameKey=".decadeBuilt" path="decadeBuiltId" accountId="${accountId}" listName="DECADE_BUILT"/>
						<tags:yukonListEntrySelectNameValue nameKey=".squareFeet" path="squareFeetId" accountId="${accountId}" listName="SQUARE_FEET"/>
						<tags:yukonListEntrySelectNameValue nameKey=".insulationDepth" path="insulationDepthId" accountId="${accountId}" listName="INSULATION_DEPTH"/>
						<tags:yukonListEntrySelectNameValue nameKey=".generalCondition" path="generalConditionId" accountId="${accountId}" listName="GENERAL_CONDITION"/>
					
					</tags:nameValueContainer2>
	
				</cti:dataGridCell>
				
				<%-- COLUMN 2 --%>
				<cti:dataGridCell>
				
					<tags:nameValueContainer2>
					
						<tags:yukonListEntrySelectNameValue nameKey=".mainCoolingSystem" path="mainCoolingSystemId" accountId="${accountId}" listName="COOLING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainHeatingSystem" path="mainHeatingSystemId" accountId="${accountId}" listName="HEATING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".numberOfOccupants" path="numberOfOccupantsId" accountId="${accountId}" listName="NUM_OF_OCCUPANTS"/>
						<tags:yukonListEntrySelectNameValue nameKey=".ownershipType" path="ownershipTypeId" accountId="${accountId}" listName="OWNERSHIP_TYPE"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainFuelType" path="mainFuelTypeId" accountId="${accountId}" listName="FUEL_TYPE"/>
						<tags:textareaNameValue nameKey=".notes" path="notes" rows="4" cols="20"/>
						
					</tags:nameValueContainer2>
				
				</cti:dataGridCell>
			
			</cti:dataGrid>
		
		</tags:formElementContainer>
		
		<%-- BUTTONS --%>
		<cti:displayForPageEditModes modes="EDIT">
			<br>
			<tags:slowInput2 formId="residenceUpdateForm" key="save"/>
		</cti:displayForPageEditModes>

	</form:form>
	
</cti:standardPage>