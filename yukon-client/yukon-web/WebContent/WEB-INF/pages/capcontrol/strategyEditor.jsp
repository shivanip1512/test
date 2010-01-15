<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="cbcStrategy" rendered="#{capControlForm.visibleTabs['CBCArea'] 
                                     || capControlForm.visibleTabs['CBCSubstationBus'] 
                                     || capControlForm.visibleTabs['CBCFeeder'] 
                                     || capControlForm.visibleTabs['CBCStrategy']}" >

    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
        <x:htmlTag value="br"/>
        <x:outputLabel for="Area_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
        <x:selectOneMenu id="Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
                value="#{capControlForm.PAOBase.capControlArea.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}" >
            <f:selectItems value="#{capControlForm.cbcStrategies}"/>
        </x:selectOneMenu>
    </f:subview>

    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}" >
        <x:htmlTag value="br"/>
        <x:outputLabel for="Sub_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
        <x:selectOneMenu id="Sub_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
                value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}">
            <f:selectItems value="#{capControlForm.cbcStrategies}"/>
        </x:selectOneMenu>
    </f:subview>

    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
        <x:htmlTag value="br"/>
        <x:outputLabel for="Feeder_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
        <x:selectOneMenu id="Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
                value="#{capControlForm.PAOBase.capControlFeeder.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}">
            <f:selectItems value="#{capControlForm.cbcStrategies}"/>
        </x:selectOneMenu>
    </f:subview>

    <x:htmlTag value="br"/>
    <h:selectBooleanCheckbox id="Edit_Strategy" onclick="lockButtonsPerSubmit('Strategy_Buttons'); submit();"
        value="#{capControlForm.editingCBCStrategy}" disabled="#{capControlForm.currentStrategyID == 0}" />
        
    <x:outputLabel for="Edit_Strategy" value="Edit Strategy" title="A toggle to edit the selected strategy" styleClass="padCheckBoxLabel"/>
    
    <x:outputText id="stratNameWarn" styleClass="alert" rendered="#{capControlForm.editingCBCStrategy}" 
        value="  (WARNING: Modifying this strategy will affect all feeders or subs that use this strategy)"/>
        
    <x:htmlTag value="br"/>
    
    <x:panelGroup id="Strategy_Buttons" forceId="true">
        <x:commandButton id="Create_Strategy" value="New Strategy" title="Create a new strategy"
                action="#{capControlForm.createStrategy}" styleClass="stdButton" />
        <x:commandButton id="Delete_Strategy" value="Delete Strategy" title="Delete the selected strategy" styleClass="stdButton"
                action="#{capControlForm.deleteStrategy}" onclick="return window.confirm('Are you sure you want to delete this strategy?\r\nNote: Deleting this strategy will force all data to be saved and the current strategy will be set to (none).');"
                disabled="#{capControlForm.currentStrategyID == 0}">
                <f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
        </x:commandButton>
    </x:panelGroup>
    
    <x:panelGrid forceId="true" id="body" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell" >
        <h:column rendered="#{capControlForm.currentStrategyID != 0}" >
        
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Detail" />
                </x:htmlTag>
            
                <x:panelGrid columns="2">
                    <x:outputLabel for="Control_Method" value="Control Method: " title="How the CapBanks are to be controlled"/>
                    <x:selectOneMenu id="Control_Method" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.controlMethod}">
                        <f:selectItems value="#{capControlForm.controlMethods}"/>
                    </x:selectOneMenu>
    
                    <x:outputLabel for="Control_Interval" value="Analysis Interval: " title="How often the system should check to determine the need for control"/>
                    <x:selectOneMenu id="Control_Interval" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.controlInterval}" >
                        <f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Max_Confirm_Time" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
                    <x:selectOneMenu id="Max_Confirm_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.minResponseTime}" >
                        <f:selectItem itemLabel="(none)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Pass_Percent" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control" />
                    <x:panelGroup>
                        <x:inputText id="Pass_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                            value="#{capControlForm.strategy.minConfirmPercent}">
                            <f:validateLongRange minimum="0" maximum="100" />
                        </x:inputText>
                        <x:outputText id="PassPercLab" value="%" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Failure_Percent" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
                    <x:panelGroup>
                        <x:inputText id="Failure_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                            value="#{capControlForm.strategy.failurePercent}">
                            <f:validateLongRange minimum="0" maximum="100" />
                        </x:inputText>
                        <x:outputText id="FailPercLab" value="%" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Send_Retries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
                    <x:inputText id="Send_Retries" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                        value="#{capControlForm.strategy.controlSendRetries}">
                        <f:validateLongRange minimum="0" maximum="100" />
                    </x:inputText>
            
                    <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Delay_Time" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Delay_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.strategy.controlDelayTime}" >
                        <f:selectItem itemValue="0" itemLabel="(none)" />
                        <f:selectItems value="#{capControlForm.timeInterval}" />
                    </x:selectOneMenu>
            
                    <x:panelGroup>
                        <x:selectBooleanCheckbox rendered="#{!capControlForm.timeOfDay}" id="IntegrateFlag" value="#{capControlForm.integrateFlag}" 
                            disabled="#{!capControlForm.editingCBCStrategy}" onclick="submit();"/>
                        <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="IntegrateFlag" value="Integrate Control?" styleClass="padCheckBoxLabel"/>
                    </x:panelGroup>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="IntegratePeriods" disabled="#{!capControlForm.editingCBCStrategy || !capControlForm.integrateFlag}"
                        value="#{capControlForm.integratePeriod}" >
                        <f:selectItems value="#{selLists.integrationPeriods}" />
                    </x:selectOneMenu>
                    
                </x:panelGrid>            
            
                <h:selectBooleanCheckbox id="Like_Day_Fall_Back_CheckBox" onclick="submit();" rendered="#{!capControlForm.timeOfDay}"
                    value="#{capControlForm.likeDayFallBack}" disabled="#{!capControlForm.editingCBCStrategy}" />
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
                            disabled="#{!capControlForm.editingCBCStrategy}"
                            value="#{capControlForm.strategy.maxDailyOperation}" >
                            <f:validateLongRange minimum="0" maximum="9999" />
                        </x:inputText>
                        <x:outputText id="MaxDailyOpsDesc" value="(0 = unlimited)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="endOfDayOperation" value="End of Day Operation: " title="End the day on this operation."/>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="endOfDayOperation" disabled="#{!capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.strategy.endDaySettings}" >
                        <f:selectItem itemValue="(none)" itemLabel="(none)" />
                        <f:selectItem itemValue="Trip" itemLabel="Trip" />
                        <f:selectItem itemValue="Close" itemLabel="Close" />
                    </x:selectOneMenu>
                </x:panelGrid>
                
                <h:selectBooleanCheckbox id="Disabled_Ops" value="#{capControlForm.strategy.maxOperationDisabled}"
                    disabled="#{!capControlForm.editingCBCStrategy}" />
                <x:outputLabel for="Disabled_Ops" value="Disable upon reaching max operations" styleClass="padCheckBoxLabel"
                    title="Option to disable automatic control on this device upon reaching the max number of operations."/>
            </x:htmlTag>
        </h:column>
        
        <h:column rendered="#{capControlForm.currentStrategyID != 0 && (!capControlForm.timeOfDay)}" >
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Peaks" />
                </x:htmlTag>
                
                <x:panelGrid forceId="true" id="strategyPeaksGrid" columns="2">
                
                    <x:outputLabel for="Control_Algorithm" value="Control Algorithm: " title="The units and process we use to make control decisions"/>
                    <x:selectOneMenu id="Control_Algorithm" onchange="controlAlgoChanged();" disabled="#{!capControlForm.editingCBCStrategy}" 
                        value="#{capControlForm.strategy.controlUnits}" valueChangeListener="#{capControlForm.controlUnitsChanged}">
                        <f:selectItems value="#{capControlForm.controlAlgorithims}"/>
                    </x:selectOneMenu>

                    <x:outputLabel for="Peak_Start_Time" value="Peak Start Time: " title="Starting time for the peak time window" />
                    <x:panelGroup>
                        <x:inputText id="Peak_Start_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.strategy.peakStartTime}" >
                        </x:inputText>
                        <x:outputText id="PkStartLab" value="(HH:mm)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Peak_Stop_Time" value="Peak Stop Time: " title="Stop time for the peak time window" />
                    <x:panelGroup>
                        <x:inputText id="Peak_Stop_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.strategy.peakStopTime}" >
                        </x:inputText>
                        <x:outputText id="PkStopLab" value="(HH:mm)" styleClass="padUnitsLabel"/>
                    </x:panelGroup>

                </x:panelGrid>
                
                <x:panelGrid forceId="true" id="peakSettingsGrid" columns="3"> 
                    
                    <h:dataTable id="peakSettingsData" var="setting" headerClass="peakSettingsTableHeader" rowClasses="peakSettingsTableCell"
                        value="#{capControlForm.strategy.targetSettings}"
                        columnClasses="peakSettingsTableCell" rows="10">

                        <h:column>
                            <f:facet name="header">
                            </f:facet>
                            <h:outputText value="#{setting.name}" />
                            <h:outputText value=":" />
                        </h:column>

                        <h:column>
                            <f:facet name="header">
                                <x:outputText value="Peak" />
                            </f:facet>
                            <x:inputText styleClass="char8Label" required="true" value="#{setting.peakValue}" />
                            <x:outputText value="#{setting.units}" styleClass="padUnitsLabel"/>
                        </h:column>
                        
                        <h:column>
                            <f:facet name="header">
                                <x:outputText value="Off Peak" />
                            </f:facet>
                            <x:inputText styleClass="char8Label" required="true" value="#{setting.offPeakValue}" />
                            <x:outputText value="#{setting.units}" styleClass="padUnitsLabel"/>
                        </h:column>

                    </h:dataTable>
                    
                </x:panelGrid>
                
                <x:outputText value="* ( - ) indicates leading" rendered="#{capControlForm.strategy.PFAlgorithm}"/>
            </x:htmlTag>
            
            <x:htmlTag value="br"/>
            
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Peak Operating Days" />
                </x:htmlTag>
                    
                <x:selectManyCheckbox id="Peak_Operating_Days" value="#{capControlForm.stratDaysOfWeek}"
                    disabled="#{!capControlForm.editingCBCStrategy}" layout="pageDirection" 
                    rendered="#{capControlForm.currentStrategyID != 0}">
                    <f:selectItems value="#{selLists.daySelections}"/>
                </x:selectManyCheckbox>
            </x:htmlTag>
        </h:column>
        
    </x:panelGrid>
    
    <x:inputHidden forceId="true" id="controlAlgoToggle" value="false" />
    
</f:subview>

<f:verbatim>
    <script type="text/JavaScript">
        
        function controlAlgoChanged () {
            $("controlAlgoToggle").value = "true";
            document.forms[0].submit();
        }
    
    </script>
</f:verbatim>