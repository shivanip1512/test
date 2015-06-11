<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="cbcStrategy">

    <h:selectBooleanCheckbox id="Edit_Strategy" onclick="yukon.da.common.lockButtonsPerSubmit('Strategy_Buttons'); submit();"
        value="#{capControlForm.editingCBCStrategy}" rendered="#{capControlForm.hasEditingRole}"/>
        
    <x:outputLabel for="Edit_Strategy" value="Edit Strategy" title="A toggle to edit the selected strategy" styleClass="padCheckBoxLabel" rendered="#{capControlForm.hasEditingRole}"/>
    
    <x:outputText id="stratNameWarn" styleClass="alert" rendered="#{capControlForm.editingCBCStrategy && capControlForm.hasEditingRole}" 
        value="  (WARNING: Modifying this strategy will affect all feeders or subs that use this strategy)"/>
        
    <x:htmlTag value="br" rendered="#{capControlForm.hasEditingRole}"/>

    <x:panelGrid forceId="true" id="body" columns="2" styleClass="gridLayout clear" columnClasses="gridCell,gridCell" >
        <h:column>
        
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Detail" />
                </x:htmlTag>
            
                <x:panelGrid columns="2">
                    <x:outputLabel for="Control_Method" value="Control Method: " title="How the CapBanks are to be controlled"/>
                    <x:selectOneMenu id="Control_Method" onchange="submit();" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" immediate="true"
                        value="#{capControlForm.strategy.controlMethod}" valueChangeListener="#{capControlForm.controlMethodChanged}" converter="controlMethodConverter">
                        <f:selectItems value="#{capControlForm.controlMethods}"/>
                    </x:selectOneMenu>
    
                    <x:outputLabel for="Control_Interval" value="Analysis Interval: " rendered="#{!capControlForm.timeOfDay}" 
                        title="How often the system should check to determine the need for control"/>
                    <x:selectOneMenu id="Control_Interval" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                        rendered="#{!capControlForm.timeOfDay}"
                        value="#{capControlForm.strategy.controlInterval}" >
                        <f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Max_Confirm_Time" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
                    <x:selectOneMenu id="Max_Confirm_Time" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.minResponseTime}" >
                        <f:selectItem itemLabel="(none)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Pass_Percent" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control" />
                    <x:panelGroup>
                        <x:inputText id="Pass_Percent" styleClass="percentLabel" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" required="true"
                            value="#{capControlForm.strategy.minConfirmPercent}">
                            <f:validateLongRange minimum="0" maximum="100" />
                        </x:inputText>
                        <x:outputText id="PassPercLab" value="%" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Failure_Percent" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
                    <x:panelGroup>
                        <x:inputText id="Failure_Percent" styleClass="percentLabel" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" required="true"
                            value="#{capControlForm.strategy.failurePercent}">
                            <f:validateLongRange minimum="0" maximum="100" />
                        </x:inputText>
                        <x:outputText id="FailPercLab" value="%" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Send_Retries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
                    <x:inputText id="Send_Retries" styleClass="percentLabel" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" required="true"
                        value="#{capControlForm.strategy.controlSendRetries}">
                        <f:validateLongRange minimum="0" maximum="100" />
                    </x:inputText>
            
                    <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Delay_Time" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Delay_Time" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.controlDelayTime}" >
                        <f:selectItem itemValue="0" itemLabel="(none)" />
                        <f:selectItems value="#{capControlForm.timeInterval}" />
                    </x:selectOneMenu>
            
                    <x:panelGroup>
                        <x:selectBooleanCheckbox rendered="#{!capControlForm.timeOfDay}" id="IntegrateFlag" value="#{capControlForm.integrateFlag}" 
                            disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" onclick="submit();"/>
                        <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="IntegrateFlag" value="Integrate Control?" styleClass="padCheckBoxLabel"/>
                    </x:panelGroup>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="IntegratePeriods" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy || !capControlForm.integrateFlag}"
                        value="#{capControlForm.integratePeriod}" >
                        <f:selectItems value="#{selLists.integrationPeriods}" />
                    </x:selectOneMenu>
                    
                </x:panelGrid>            
            
                <h:selectBooleanCheckbox id="Like_Day_Fall_Back_CheckBox" onclick="submit();" rendered="#{!capControlForm.timeOfDay}"
                    value="#{capControlForm.likeDayFallBack}" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" />
                <x:outputLabel for="Like_Day_Fall_Back_CheckBox" value="Fall back to like-day history control." rendered="#{!capControlForm.timeOfDay}"
                    styleClass="padCheckBoxLabel"
                    title="Check to use like-day history control on this strategy."/>
            </x:htmlTag>
    
            <x:htmlTag value="br"/>
    
            <x:htmlTag value="fieldset" styleClass="fieldSet"  rendered="#{!capControlForm.timeOfDay}">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Operations" />
                </x:htmlTag>
                <x:panelGrid columns="2">
                    <x:outputLabel for="Max_Daily_Ops" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
                    <x:panelGroup>
                        <x:inputText id="Max_Daily_Ops" styleClass="char16Label" required="true"
                            disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                            value="#{capControlForm.strategy.maxDailyOperation}" >
                            <f:validateLongRange minimum="0" maximum="9999" />
                        </x:inputText>
                        <x:outputText id="MaxDailyOpsDesc" value="(0 = unlimited)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="endOfDayOperation" value="End of Day Operation: " title="End the day on this operation."/>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="endOfDayOperation" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.strategy.endDaySettings}" >
                        <f:selectItem itemValue="(none)" itemLabel="(none)" />
                        <f:selectItem itemValue="Trip" itemLabel="Trip" />
                        <f:selectItem itemValue="Close" itemLabel="Close" />
                    </x:selectOneMenu>
                </x:panelGrid>
                
                <h:selectBooleanCheckbox id="Disabled_Ops" value="#{capControlForm.strategy.maxOperationDisabled}"
                    disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" />
                <x:outputLabel for="Disabled_Ops" value="Disable upon reaching max operations" styleClass="padCheckBoxLabel"
                    title="Option to disable automatic control on this device upon reaching the max number of operations."/>
            </x:htmlTag>
        </h:column>
        
        <h:column>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Target Settings" />
                </x:htmlTag>
                
                <f:verbatim>
                    <!-- DO NOT DISABLE THE Control_Algorithm DROP DOWN FOR IVVC: If it is disable or not rendered it will update incorrectly when changing control mehtod. -->
                </f:verbatim>
                <x:panelGrid forceId="true" id="strategyPeaksGrid" columns="2">
                
                    <x:outputLabel for="Control_Algorithm" value="Control Algorithm: " title="The units and process we use to make control decisions"/>
                    <x:selectOneMenu id="Control_Algorithm" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" value="#{capControlForm.strategy.algorithm}" 
                        converter="controlAlgorithmConverter" onchange="submit();"
                        valueChangeListener="#{capControlForm.algorithmChanged}">
                        <f:selectItems value="#{capControlForm.controlAlgorithims}"/>
                    </x:selectOneMenu>

                    <x:outputLabel for="Peak_Start_Time" value="Peak Start Time: " title="Starting time for the peak time window"  rendered="#{!capControlForm.timeOfDay}"/>
                    <x:panelGroup rendered="#{!capControlForm.timeOfDay}">
                        <x:inputText id="Peak_Start_Time" styleClass="char16Label" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.strategy.peakStartTime}" >
                        </x:inputText>
                        <x:outputText id="PkStartLab" value="(HH:mm)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Peak_Stop_Time" value="Peak Stop Time: " title="Stop time for the peak time window" rendered="#{!capControlForm.timeOfDay}"/>
                    <x:panelGroup rendered="#{!capControlForm.timeOfDay}">
                        <x:inputText id="Peak_Stop_Time" styleClass="char16Label" disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.strategy.peakStopTime}" >
                        </x:inputText>
                        <x:outputText id="PkStopLab" value="(HH:mm)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>

                </x:panelGrid>
                
                <h:dataTable id="peakSettingsData" var="setting" headerClass="peakSettingsTableHeader" rowClasses="peakSettingsTableCell" columnClasses="peakSettingsTableCell"
                    value="#{capControlForm.strategy.targetSettings}" >

                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Week Day" rendered="#{capControlForm.timeOfDay}"/>
                        </f:facet>
                        <h:outputText value="#{setting.type.displayName}" style="padding-left:50px;"/>
                        <h:outputText value=":" />
                    </h:column>

                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="#{capControlForm.peakHeader}"/>
                        </f:facet>
                        <x:inputText styleClass="char8Label" required="true" value="#{setting.peakValue}" 
                                     disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"/>
                        <x:outputText value="#{setting.type.units}" styleClass="padUnitsLabel"/>
                    </h:column>
                    
                    <h:column rendered="#{capControlForm.timeOfDay}">
                        <f:facet name="header">
                            <x:outputText value="Open" />
                        </f:facet>
                        <x:outputText value="#{100 - setting.peakValue}" styleClass="padUnitsLabel"/>
                        <x:outputText value="%" styleClass="padUnitsLabel"/>
                    </h:column>
                    
                    <h:column rendered="#{capControlForm.timeOfDay}">
                        <f:facet name="header">
                            <x:outputText value="Weekend" />
                        </f:facet>
                        <h:outputText value="#{setting.type.displayName}" style="padding-left:50px;"/>
                    </h:column>
                    
                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="#{capControlForm.offPeakHeader}"/>
                        </f:facet>
                        <x:inputText styleClass="char8Label" required="true" value="#{setting.offPeakValue}" 
                                     disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"/>
                        <x:outputText value="#{setting.type.units}" styleClass="padUnitsLabel"/>
                    </h:column>
                    
                    <h:column rendered="#{capControlForm.timeOfDay}">
                        <f:facet name="header">
                            <x:outputText value="Open" />
                        </f:facet>
                        <x:outputText value="#{100 - setting.offPeakValue}" styleClass="padUnitsLabel"/>
                        <x:outputText value="%" styleClass="padUnitsLabel"/>
                    </h:column>

                </h:dataTable>
                
                <x:htmlTag value="br"/>
                
                <x:outputText value="* ( - ) indicates leading" rendered="#{capControlForm.strategy.voltVar || capControlForm.strategy.KVarAlgorithm}"/>
            </x:htmlTag>
            
            <x:htmlTag value="br"/>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet" rendered="#{capControlForm.strategy.ivvc}">
                <x:htmlTag value="legend">
                    <x:outputText value="Voltage Violation Settings"/>
                </x:htmlTag>

				<h:dataTable id="voltageViolationSettingsData" var="setting" 
					value="#{capControlForm.strategy.voltageViolationSettings}"
					headerClass="peakSettingsTableHeader"
					rowClasses="peakSettingsTableCell"
					columnClasses="peakSettingsTableCell">

                    <h:column>
                        <h:outputText value="#{setting.name.displayName}" style="padding-left:50px;"/>
                        <h:outputText value=":" />
                    </h:column>

                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Bandwidth"/>
                        </f:facet>
                        <x:inputText styleClass="char8Label" required="true" value="#{setting.bandwidth}" 
                                     disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Cost"/>
                        </f:facet>
                        <x:inputText styleClass="char8Label" required="true" value="#{setting.cost}" 
                                     disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"/>
                    </h:column>
                    <h:column>
                        <f:facet name="header">
                            <x:outputText value="Emergency Cost"/>
                        </f:facet>
                        <x:inputText styleClass="char8Label" required="true" value="#{setting.emergencyCost}" 
                                     disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"/>
                    </h:column>

				</h:dataTable>

			</x:htmlTag>
            
            <x:htmlTag value="br" rendered="#{capControlForm.strategy.ivvc && capControlForm.strategy.busOptimized}"/>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet" rendered="#{capControlForm.strategy.ivvc && capControlForm.strategy.busOptimized}">
                <x:htmlTag value="legend">
                    <x:outputText value="Power Factor Correction Settings"/>
                </x:htmlTag>

                <x:panelGrid columns="2">
					<x:outputLabel for="pf-bandwidth" value="Bandwidth: " title="Power Factor Correction Bandwidth" />
					<x:panelGroup>
						<x:inputText id="pf-bandwidth"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.powerFactorCorrectionSetting.bandwidth}">
							<f:validateDoubleRange minimum="0"/>
							<f:validateDoubleRange maximum="2"/>
						</x:inputText>
						<x:outputText value="bandwidth units" styleClass="padUnitsLabel" />
					</x:panelGroup>
	
					<x:outputLabel for="pf-cost" value="Cost: " title="Power Factor Correction Cost" />
					<x:panelGroup>
						<x:inputText id="pf-cost"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.powerFactorCorrectionSetting.cost}">
							<f:validateDoubleRange minimum="0"/>
						</x:inputText>
						<x:outputText value="cost units" styleClass="padUnitsLabel" />
					</x:panelGroup>
	
					<x:outputLabel for="pf-max_cost" value="Max Cost: " title="Power Factor Correction Max Cost" />
					<x:panelGroup>
						<x:inputText id="pf-max_cost"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.powerFactorCorrectionSetting.maxCost}">
							<f:validateDoubleRange minimum="0" />
						</x:inputText>
						<x:outputText value="max cost units" styleClass="padUnitsLabel" />
					</x:panelGroup>

				</x:panelGrid>

			</x:htmlTag>
            
            <x:htmlTag value="br" rendered="#{capControlForm.strategy.ivvc}"/>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet" rendered="#{capControlForm.strategy.ivvc}">
                <x:htmlTag value="legend">
                    <x:outputText value="Minimum Communication Percentages"/>
                </x:htmlTag>

                <x:panelGrid columns="2">
					<x:outputLabel for="mcp_banks" value="Capacitor Bank: " title="Capacitor Bank Min Communication Percentage" />
					<x:panelGroup>
						<x:inputText id="mcp_banks"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.minCommunicationPercentageSetting.banksReportingRatio}">
							<f:validateDoubleRange minimum="0"/>
							<f:validateDoubleRange maximum="100"/>
						</x:inputText>
						<x:outputText value="%" styleClass="padUnitsLabel" />
					</x:panelGroup>

					<x:outputLabel for="mcp_regulator" value="Voltage Regulator: " title="Voltage Regulator Min Communication Percentage" />
					<x:panelGroup>
						<x:inputText id="mcp_regulator"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.minCommunicationPercentageSetting.regulatorReportingRatio}">
							<f:validateDoubleRange minimum="0"/>
							<f:validateDoubleRange maximum="100"/>
						</x:inputText>
						<x:outputText value="%" styleClass="padUnitsLabel" />
					</x:panelGroup>

					<x:outputLabel for="mcp_additional_voltage_point" value="Additional Voltage Points: " title="Additional Voltage Points Min Communication Percentage" />
					<x:panelGroup>
						<x:inputText id="mcp_additional_voltage_point"
							disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}"
							required="true" value="#{capControlForm.strategy.minCommunicationPercentageSetting.voltageMonitorReportingRatio}">
							<f:validateDoubleRange minimum="0"/>
							<f:validateDoubleRange maximum="100"/>
						</x:inputText>
						<x:outputText value="%" styleClass="padUnitsLabel" />
					</x:panelGroup>

				</x:panelGrid>

			</x:htmlTag>
            
            <x:htmlTag value="br" rendered="#{capControlForm.strategy.ivvc}"/>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet" rendered="#{!capControlForm.timeOfDay}">
                <x:htmlTag value="legend">
                    <x:outputText value="Peak Operating Days" />
                </x:htmlTag>
                    
                <x:selectManyCheckbox id="Peak_Operating_Days" value="#{capControlForm.stratDaysOfWeek}"
                    disabled="#{!capControlForm.hasEditingRole || !capControlForm.editingCBCStrategy}" layout="pageDirection">
                    <f:selectItems value="#{selLists.daySelections}"/>
                </x:selectManyCheckbox>
            </x:htmlTag>
            
        </h:column>
        
    </x:panelGrid>
    
</f:subview>