<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage module="operator" page="appliance.edit">
    
    <tags:setFormEditMode mode="${mode}"/>

    <cti:includeScript link="/JavaScript/yukonGeneral.js"/>

    <script type="text/javascript">

        alignTableColumnsByTable('#enrollmentTable', '#hardwareSummaryTable');
    
    </script>
    
    <table>
      <tr>
        <td rowspan="3" valign="top">
            <tags:formElementContainer nameKey="applianceInfo">

                <form:form commandName="starsAppliance" action="/stars/operator/appliances/applianceUpdate">
                    <tags:nameValueContainer2>
                        <input type="hidden" name="applianceId" value="${starsAppliance.applianceID}">
                        <input type="hidden" name="accountId" value="${accountId}">
                        
                        <form:hidden path="applianceID"/>
                        <form:hidden path="applianceCategory.applianceCategoryId"/>
                        <tags:nameValue2 nameKey=".applianceCategory">
                            <spring:escapeBody htmlEscape="true">${starsAppliance.applianceCategory.name}</spring:escapeBody>
                        </tags:nameValue2>
                
                        <tags:inputNameValue nameKey=".modelNumber" path="modelNumber"/>
                        <tags:yukonListEntrySelectNameValue nameKey=".location" path="location.entryID" energyCompanyId="${energyCompanyId}" listName="APP_LOCATION"/>
                        <tags:yukonListEntrySelectNameValue nameKey=".manufacturer" path="manufacturer.entryID" energyCompanyId="${energyCompanyId}" listName="MANUFACTURER"/>
                        <tags:inputNameValue nameKey=".yearManufactured" path="yearManufactured"/>
                        <tags:inputNameValue nameKey=".kwCapacity" path="kwCapacity"/>
                        <tags:inputNameValue nameKey=".efficiencyRating" path="efficiencyRating"/>
                        <tags:textareaNameValue cols="20" rows="4" nameKey=".notes" path="notes"/>
                        
                        <!-- Appliance Specific Fields -->
                        <c:choose>
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'AIR_CONDITIONER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".ac.tonnage" path="airConditioner.tonnage.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".ac.type" path="airConditioner.acType.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'DUAL_STAGE'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.stageOneTonnage" path="dualStageAC.tonnage.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.stageTwoTonnage" path="dualStageAC.stageTwoTonnage.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.type" path="dualStageAC.acType.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'CHILLER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".chiller.tonnage" path="chiller.tonnage.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".chiller.type" path="chiller.acType.entryID" energyCompanyId="${energyCompanyId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'WATER_HEATER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".wh.numOfGallons" path="waterHeater.numberOfGallons.entryID" energyCompanyId="${energyCompanyId}" listName="WH_NUM_OF_GALLONS"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".wh.energySource" path="waterHeater.energySource.entryID" energyCompanyId="${energyCompanyId}" listName="WH_ENERGY_SOURCE"/>
                                <tags:inputNameValue nameKey=".wh.numOfHeatingCoils" path="waterHeater.numberOfElements"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'GENERATOR'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".generator.transferSwitchType" path="generator.transferSwitchType.entryID" energyCompanyId="${energyCompanyId}" listName="GEN_TRANSFER_SWITCH_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".generator.transferSwitchManufacturer" path="generator.transferSwitchManufacturer.entryID" energyCompanyId="${energyCompanyId}" listName="GEN_TRANSFER_SWITCH_MFG"/>
                                <tags:inputNameValue nameKey=".generator.peakKWCapacity" path="generator.peakKWCapacity"/>
                                <tags:inputNameValue nameKey=".generator.fuelCapacity" path="generator.fuelCapGallons"/>
                                <tags:inputNameValue nameKey=".generator.startDelay" path="generator.startDelaySeconds"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'DUAL_FUEL'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".dualFuel.switchOverType" path="dualFuel.switchOverType.entryID" energyCompanyId="${energyCompanyId}" listName="DF_SWITCH_OVER_TYPE"/>
                                <tags:inputNameValue nameKey=".dualFuel.secondaryKWCapacity" path="dualFuel.secondaryKWCapacity"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualFuel.secondaryEnergySource" path="dualFuel.secondaryEnergySource.entryID" energyCompanyId="${energyCompanyId}" listName="DF_SECONDARY_SOURCE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'GRAIN_DRYER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.dryerType" path="grainDryer.dryerType.entryID" energyCompanyId="${energyCompanyId}" listName="GRAIN_DRYER_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.binSize" path="grainDryer.binSize.entryID" energyCompanyId="${energyCompanyId}" listName="GD_BIN_SIZE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerEnergySource" path="grainDryer.blowerEnergySource.entryID" energyCompanyId="${energyCompanyId}" listName="GD_ENERGY_SOURCE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerHorsePower" path="grainDryer.blowerHorsePower.entryID" energyCompanyId="${energyCompanyId}" listName="GD_HORSE_POWER"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerHeatSource" path="grainDryer.blowerHeatSource.entryID" energyCompanyId="${energyCompanyId}" listName="GD_HEAT_SOURCE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'STORAGE_HEAT'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".storageHeat.storageType" path="storageHeat.storageType.entryID" energyCompanyId="${energyCompanyId}" listName="STORAGE_HEAT_TYPE"/>
                                <tags:inputNameValue nameKey=".storageHeat.peakKWCapacity" path="storageHeat.peakKWCapacity"/>
                                <tags:inputNameValue nameKey=".storageHeat.hoursToRecharge" path="storageHeat.hoursToRecharge"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'HEAT_PUMP'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.pumpType" path="heatPump.pumpType.entryID" energyCompanyId="${energyCompanyId}" listName="HEAT_PUMP_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.pumpSize" path="heatPump.pumpSize.entryID" energyCompanyId="${energyCompanyId}" listName="HEAT_PUMP_SIZE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.standbySource" path="heatPump.standbySource.entryID" energyCompanyId="${energyCompanyId}" listName="HP_STANDBY_SOURCE"/>
                                <tags:inputNameValue nameKey=".heatPump.restartDelaySeconds" path="heatPump.restartDelaySeconds"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'IRRIGATION'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.irrigationType" path="irrigation.irrigationType.entryID" energyCompanyId="${energyCompanyId}" listName="IRRIGATION_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.horsePower" path="irrigation.horsePower.entryID" energyCompanyId="${energyCompanyId}" listName="IRR_HORSE_POWER"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.energySource" path="irrigation.energySource.entryID" energyCompanyId="${energyCompanyId}" listName="IRR_ENERGY_SOURCE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.soilType" path="irrigation.soilType.entryID" energyCompanyId="${energyCompanyId}" listName="IRR_SOIL_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.meterLocation" path="irrigation.meterLocation.entryID" energyCompanyId="${energyCompanyId}" listName="IRR_METER_LOCATION"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.meterVoltage" path="irrigation.meterVoltage.entryID" energyCompanyId="${energyCompanyId}" listName="IRR_METER_VOLTAGE"/>
                            </c:when>
                        </c:choose>
                        
                    </tags:nameValueContainer2>
                    
                    <br>
                    
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <cti:displayForPageEditModes modes="CREATE">
                            <cti:button nameKey="save" name="create" type="submit"/>
                            <cti:url value="/stars/operator/appliances/applianceList" var="cancelUrl">
                                <cti:param name="accountId" value="${accountId}"/>
                            </cti:url>
                        </cti:displayForPageEditModes>
                        
                        <cti:displayForPageEditModes modes="EDIT">
                            <cti:button nameKey="save" name="update" type="submit"/>
                            <cti:button nameKey="delete" name="delete" type="submit"/>
                            <cti:url value="/stars/operator/appliances/view" var="cancelUrl">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="applianceId" value="${starsAppliance.applianceID}"/>
                            </cti:url>
                        </cti:displayForPageEditModes>
                        
                        <cti:button nameKey="cancel" href="${cancelUrl}"/>
                    </cti:displayForPageEditModes>
                    
                    <cti:displayForPageEditModes modes="VIEW">
                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                            <cti:url value="/stars/operator/appliances/edit" var="editUrl">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="applianceId" value="${starsAppliance.applianceID}"/>
                            </cti:url>
                            <cti:button nameKey="edit" href="${editUrl}"/>
                        </cti:checkRolesAndProperties>
                    </cti:displayForPageEditModes>
                    
                </form:form>

                <br>
            </tags:formElementContainer>
        </td>
        <td rowspan="3" width="50px"></td>
        <td valign="top">
            <c:if test="${not empty displayableInventoryEnrollment}">
                <tags:formElementContainer nameKey="enrollment">
                    <tags:nameValueContainer2 id="enrollmentTable">
                        <tags:nameValue2 nameKey=".program">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.programName.displayName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".loadGroup">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.loadGroupName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".relay">
                            <spring:escapeBody htmlEscape="true">${displayableInventoryEnrollment.relay}</spring:escapeBody>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
            </c:if>
        </td>
      </tr>
      <tr><td></td></tr>
      <tr>
        <td valign="top">
            <c:if test="${not empty hardware}">
                <tags:formElementContainer nameKey="hardwareSummary">
                    <tags:nameValueContainer2 id="hardwareSummaryTable">
                        <tags:nameValue2 nameKey=".category">
                            <spring:escapeBody htmlEscape="true">${hardware.categoryName}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".hardwareType">
                            <spring:escapeBody htmlEscape="true">${hardware.displayType}</spring:escapeBody>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".serialNumber">
                            <spring:escapeBody htmlEscape="true">${hardware.serialNumber}</spring:escapeBody>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:formElementContainer>
            </c:if>          
        </td>
      </tr>
    </table>
</cti:standardPage>