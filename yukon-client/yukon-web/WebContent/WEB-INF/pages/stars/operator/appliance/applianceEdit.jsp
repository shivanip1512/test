<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>
<%@ taglib tagdir="/WEB-INF/tags/dr/forms" prefix="drForms" %>

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

                <form:form commandName="starsAppliance" action="/spring/stars/operator/appliances/applianceUpdate">
                    <tags:nameValueContainer2>
                        <input type="hidden" name="applianceId" value="${starsAppliance.applianceID}">
                        <input type="hidden" name="accountId" value="${accountId}">
                        
                        <form:hidden path="applianceID"/>
                        <form:hidden path="applianceCategory.applianceCategoryId"/>
                        <tags:nameValue2 nameKey=".applianceCategory">
                            <spring:escapeBody htmlEscape="true">${starsAppliance.applianceCategory.name}</spring:escapeBody>
                        </tags:nameValue2>
                
                        <tags:inputNameValue nameKey=".modelNumber" path="modelNumber"/>
                        <tags:yukonListEntrySelectNameValue nameKey=".location" path="location.entryID" accountId="${accountId}" listName="APP_LOCATION"/>
                        <tags:yukonListEntrySelectNameValue nameKey=".manufacturer" path="manufacturer.entryID" accountId="${accountId}" listName="MANUFACTURER"/>
                        <tags:inputNameValue nameKey=".yearManufactured" path="yearManufactured"/>
                        <tags:inputNameValue nameKey=".kwCapacity" path="kwCapacity"/>
                        <tags:inputNameValue nameKey=".efficiencyRating" path="efficiencyRating"/>
                        <tags:textareaNameValue cols="20" rows="4" nameKey=".notes" path="notes"/>
                        
                        <!-- Appliance Specific Fields -->
                        <c:choose>
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'AIR_CONDITIONER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".ac.tonnage" path="airConditioner.tonnage.entryID" accountId="${accountId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".ac.type" path="airConditioner.acType.entryID" accountId="${accountId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'DUAL_STAGE'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.stageOneTonnage" path="dualStageAC.tonnage.entryID" accountId="${accountId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.stageTwoTonnage" path="dualStageAC.stageTwoTonnage.entryID" accountId="${accountId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualAC.type" path="dualStageAC.acType.entryID" accountId="${accountId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'CHILLER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".chiller.tonnage" path="chiller.tonnage.entryID" accountId="${accountId}" listName="AC_TONNAGE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".chiller.type" path="chiller.acType.entryID" accountId="${accountId}" listName="AC_TYPE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'WATER_HEATER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".wh.numOfGallons" path="waterHeater.numberOfGallons.entryID" accountId="${accountId}" listName="WH_NUM_OF_GALLONS"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".wh.energySource" path="waterHeater.energySource.entryID" accountId="${accountId}" listName="WH_ENERGY_SOURCE"/>
                                <tags:inputNameValue nameKey=".wh.numOfHeatingCoils" path="waterHeater.numberOfElements"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'GENERATOR'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".generator.transferSwitchType" path="generator.transferSwitchType.entryID" accountId="${accountId}" listName="GEN_TRANSFER_SWITCH_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".generator.transferSwitchManufacturer" path="generator.transferSwitchManufacturer.entryID" accountId="${accountId}" listName="GEN_TRANSFER_SWITCH_MFG"/>
                                <tags:inputNameValue nameKey=".generator.peakKWCapacity" path="generator.peakKWCapacity"/>
                                <tags:inputNameValue nameKey=".generator.fuelCapacity" path="generator.fuelCapGallons"/>
                                <tags:inputNameValue nameKey=".generator.startDelay" path="generator.startDelaySeconds"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'DUAL_FUEL'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".dualFuel.switchOverType" path="dualFuel.switchOverType.entryID" accountId="${accountId}" listName="DF_SWITCH_OVER_TYPE"/>
                                <tags:inputNameValue nameKey=".dualFuel.secondaryKWCapacity" path="dualFuel.secondaryKWCapacity"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".dualFuel.secondaryEnergySource" path="dualFuel.secondaryEnergySource.entryID" accountId="${accountId}" listName="DF_SECONDARY_SOURCE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'GRAIN_DRYER'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.dryerType" path="grainDryer.dryerType.entryID" accountId="${accountId}" listName="GRAIN_DRYER_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.binSize" path="grainDryer.binSize.entryID" accountId="${accountId}" listName="GD_BIN_SIZE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerEnergySource" path="grainDryer.blowerEnergySource.entryID" accountId="${accountId}" listName="GD_ENERGY_SOURCE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerHorsePower" path="grainDryer.blowerHorsePower.entryID" accountId="${accountId}" listName="GD_HORSE_POWER"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".grainDryer.blowerHeatSource" path="grainDryer.blowerHeatSource.entryID" accountId="${accountId}" listName="GD_HEAT_SOURCE"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'STORAGE_HEAT'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".storageHeat.storageType" path="storageHeat.storageType.entryID" accountId="${accountId}" listName="STORAGE_HEAT_TYPE"/>
                                <tags:inputNameValue nameKey=".storageHeat.peakKWCapacity" path="storageHeat.peakKWCapacity"/>
                                <tags:inputNameValue nameKey=".storageHeat.hoursToRecharge" path="storageHeat.hoursToRecharge"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'HEAT_PUMP'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.pumpType" path="heatPump.pumpType.entryID" accountId="${accountId}" listName="HEAT_PUMP_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.pumpSize" path="heatPump.pumpSize.entryID" accountId="${accountId}" listName="HEAT_PUMP_SIZE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".heatPump.standbySource" path="heatPump.standbySource.entryID" accountId="${accountId}" listName="HP_STANDBY_SOURCE"/>
                                <tags:inputNameValue nameKey=".heatPump.restartDelaySeconds" path="heatPump.restartDelaySeconds"/>
                            </c:when>
                
                            <c:when test="${starsAppliance.applianceCategory.applianceType eq 'IRRIGATION'}">
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.irrigationType" path="irrigation.irrigationType.entryID" accountId="${accountId}" listName="IRRIGATION_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.horsePower" path="irrigation.horsePower.entryID" accountId="${accountId}" listName="IRR_HORSE_POWER"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.energySource" path="irrigation.energySource.entryID" accountId="${accountId}" listName="IRR_ENERGY_SOURCE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.soilType" path="irrigation.soilType.entryID" accountId="${accountId}" listName="IRR_SOIL_TYPE"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.meterLocation" path="irrigation.meterLocation.entryID" accountId="${accountId}" listName="IRR_METER_LOCATION"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".irrigation.meterVoltage" path="irrigation.meterVoltage.entryID" accountId="${accountId}" listName="IRR_METER_VOLTAGE"/>
                            </c:when>
                        </c:choose>
                        
                    </tags:nameValueContainer2>
                    
                    <br>
                    
                    <cti:displayForPageEditModes modes="CREATE">
                        <button type="submit" name="create" class="formSubmit">
                            <i:inline key=".create"/>
                        </button>
                    </cti:displayForPageEditModes>
                    
                    <cti:displayForPageEditModes modes="EDIT">
                        <button type="submit" name="update" class="formSubmit">
                            <i:inline key=".save"/>
                        </button>
                        <button type="submit" name="delete" class="formSubmit">
                            <i:inline key=".delete"/>
                        </button>
                    </cti:displayForPageEditModes>
                    
                    <button type="submit" name="cancel" class="formSubmit">
                        <i:inline key=".cancel"/>
                    </button>
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