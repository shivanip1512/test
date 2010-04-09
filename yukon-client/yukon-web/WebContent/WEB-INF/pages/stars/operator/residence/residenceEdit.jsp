<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="residence" mode="${mode}">

	<form:form id="residenceUpdateForm" commandName="customerResidence" action="/spring/stars/operator/residence/residenceUpdate">
	
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
	
		<tags:sectionContainer2 key="customerResidenceSection">
	
			<cti:dataGrid cols="2" tableStyle="width:95%;" rowStyle="vertical-align:top;" cellStyle="width:50%;padding-right:20px;">
	
				<%-- COLUMN 1 --%>
				<cti:dataGridCell>
				
					<tags:nameValueContainer2 nameColumnWidth="200px">
					
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
				
					<tags:nameValueContainer2 nameColumnWidth="200px">
					
						<tags:yukonListEntrySelectNameValue nameKey=".mainCoolingSystem" path="mainCoolingSystemId" accountId="${accountId}" listName="COOLING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainHeatingSystem" path="mainHeatingSystemId" accountId="${accountId}" listName="HEATING_SYSTEM"/>
						<tags:yukonListEntrySelectNameValue nameKey=".numberOfOccupants" path="numberOfOccupantsId" accountId="${accountId}" listName="NUM_OF_OCCUPANTS"/>
						<tags:yukonListEntrySelectNameValue nameKey=".ownershipType" path="ownershipTypeId" accountId="${accountId}" listName="OWNERSHIP_TYPE"/>
						<tags:yukonListEntrySelectNameValue nameKey=".mainFuelType" path="mainFuelTypeId" accountId="${accountId}" listName="FUEL_TYPE"/>
						<tags:textareaNameValue nameKey=".notes" path="notes" rows="4" cols="26"/>
						
					</tags:nameValueContainer2>
				
				</cti:dataGridCell>
			
			</cti:dataGrid>
		
		</tags:sectionContainer2>
		
		<%-- BUTTONS --%>
		<cti:displayForPageEditModes modes="EDIT">
			<br>
			<tags:slowInput2 myFormId="residenceUpdateForm" key="save" width="80px"/>
			<tags:reset/>
		</cti:displayForPageEditModes>

	</form:form>
	
</cti:standardPage>