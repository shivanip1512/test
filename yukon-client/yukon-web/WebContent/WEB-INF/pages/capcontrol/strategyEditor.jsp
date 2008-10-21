<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:verbatim>
<script type="text/JavaScript">
	
	Event.observe(window, 'load', function() {resetVals()});
	
	function controlAlgoChanged () {
		$("controlAlgoToggle").value = "true";
		document.forms[0].submit();
	}

	function resetVals() {
		if ($("controlAlgoToggle").value == "true") {
			if ($("Peak_Leading")){
				$("Peak_Leading").value = "0.0";
			}
			if ($("Offpeak_Leading")) {
				$("Offpeak_Leading").value = "0.0";
			}
			if ($("Peak_Lagging")) {
				$("Peak_Lagging").value = "0.0";
			}
			if($ ("Offpeak_Lagging")) {
				$("Offpeak_Lagging").value = "0.0";
			}
			if($("peak_upper")) {
				$("peak_upper").value = "0.0";
			}
			if($("offpeak_upper")) {
				$("offpeak_upper").value = "0.0";
			}
			if($("peak_lower")){
				$("peak_lower").value = "0.0";
			}
			if($("offpeak_lower")) {
				$("offpeak_lower").value = "0.0";
			}
			if($("Target_Peak_PF_Point")) {
				$("Target_Peak_PF_Point").value = "100.0";
			}
			if($("Target_OffPeak_PF_point")) {			
				$("Target_OffPeak_PF_point").value = "100.0";
			}
			$("controlAlgoToggle").value = "false";
		}
	}

</script>

</f:verbatim>
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
	<h:selectBooleanCheckbox id="Edit_Strategy" 
        onclick="lockButtonsPerSubmit('Strategy_Buttons'); submit();"
		value="#{capControlForm.editingCBCStrategy}"
		disabled="#{capControlForm.currentStrategyID == 0}" />
	<x:outputLabel for="Edit_Strategy" 
	   value="Edit Strategy" 
	   title="A toggle to edit the selected strategy"/>
	<x:outputText id="stratNameWarn" styleClass="alert"
		rendered="#{capControlForm.editingCBCStrategy}" 
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
	
	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell" >
		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
		
		    <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Detail" />
                </x:htmlTag>
		    
                <x:panelGrid columns="2">
                    <x:outputLabel for="Control_Method" value="Control Method: " title="How the CapBanks are to be controlled"/>
                    <x:selectOneMenu id="Control_Method" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlMethod}" 
                        valueChangeListener="#{capControlForm.currentStratModel.resetValues}">
                        <f:selectItems value="#{capControlForm.controlMethods}"/>
                    </x:selectOneMenu>
	
                    <x:outputLabel for="Control_Interval" value="Analysis Interval: " title="How often the system should check to determine the need for control"/>
                    <x:selectOneMenu id="Control_Interval" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlInterval}" >
                        <f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Max_Confirm_Time" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
                    <x:selectOneMenu id="Max_Confirm_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minResponseTime}" >
                        <f:selectItem itemLabel="(none)" itemValue="0"/>
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Pass_Percent" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control"/>
                    <x:panelGroup>
	                    <x:inputText id="Pass_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
	                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minConfirmPercent}">
	                        <f:validateLongRange minimum="0" maximum="100" />
	                    </x:inputText>
	                    <x:outputText id="PassPercLab" value="%"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Failure_Percent" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
                    <x:panelGroup>
	                    <x:inputText id="Failure_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
	                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].failurePercent}">
	                        <f:validateLongRange minimum="0" maximum="100" />
	                    </x:inputText>
	                    <x:outputText id="FailPercLab" value="%"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Send_Retries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
                    <x:inputText id="Send_Retries" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlSendRetries}">
                        <f:validateLongRange minimum="0" maximum="100" />
                    </x:inputText>
            
                    <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Delay_Time" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Delay_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlDelayTime}" >
                        <f:selectItem itemValue="0" itemLabel="(none)" />
                        <f:selectItems value="#{capControlForm.timeInterval}" />
                    </x:selectOneMenu>
            
                    <x:panelGroup>
	                    <x:selectBooleanCheckbox rendered="#{!capControlForm.timeOfDay}" id="IntegrateFlag" value="#{capControlForm.currentStratModel.integrateFlag}" 
	                        disabled="#{!capControlForm.editingCBCStrategy}" onclick="submit();"/>
	                    <x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="IntegrateFlag" value="Integrate Control?"/>
                    </x:panelGroup>
                    <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="IntegratePeriods" disabled="#{!capControlForm.editingCBCStrategy || !capControlForm.currentStratModel.integrateFlag}"
                        value="#{capControlForm.currentStratModel.integratePeriod}" >
                        <f:selectItems value="#{selLists.integrationPeriods}" />
                    </x:selectOneMenu>
                    
                </x:panelGrid>            
            
                <h:selectBooleanCheckbox id="Like_Day_Fall_Back_CheckBox" onclick="submit();" rendered="#{!capControlForm.timeOfDay}"
                    value="#{capControlForm.currentStratModel.likeDayFallBack}" disabled="#{!capControlForm.editingCBCStrategy}" />
                <x:outputLabel for="Like_Day_Fall_Back_CheckBox" value="Fall back to like-day history control." rendered="#{!capControlForm.timeOfDay}"
                    title="Check to use like-day history control on this strategy."/>
            </x:htmlTag>
	
            <h:panelGrid rendered="#{!capControlForm.timeOfDay}" columnClasses="gridCell">
                <h:column>
                    <x:htmlTag value="fieldset" styleClass="fieldSet">
                        <x:htmlTag value="legend">
                            <x:outputText value="Strategy Operations" />
                        </x:htmlTag>
                        <x:outputLabel for="Max_Daily_Ops" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
                        <x:inputText id="Max_Daily_Ops" styleClass="char16Label" required="true"
                            disabled="#{!capControlForm.editingCBCStrategy}"
                            value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxDailyOperation}" >
                            <f:validateLongRange minimum="0" maximum="9999" />
                        </x:inputText>
                        <x:outputText id="MaxDailyOpsDesc" value="(0 = unlimited)"/>
	
                        <x:htmlTag value="br"/>
                        <h:selectBooleanCheckbox id="Disabled_Ops" value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxOperationDisabled}"
                            disabled="#{!capControlForm.editingCBCStrategy}" />
                        <x:outputLabel for="Disabled_Ops" value="Disable upon reaching max operations" 
                            title="Option to disable automatic control on this device upon reaching the max number of operations."/>
                    </x:htmlTag>
                </h:column>
            </h:panelGrid>
        </h:column>

        <h:column rendered="#{capControlForm.currentStrategyID != 0 && (!capControlForm.timeOfDay)}" >
            <x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend">
	                <x:outputText value="Strategy Peaks" />
	            </x:htmlTag>
                
                <x:panelGrid columns="2" columnClasses="gridCell,gridCell">
                
                    <x:outputLabel for="Control_Algorithm" value="Control Algorithm: " title="The units and process we use to make control decisions"/>
                    <x:selectOneMenu id="Control_Algorithm" onchange="controlAlgoChanged();" disabled="#{!capControlForm.editingCBCStrategy}"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlUnits}" 
                        valueChangeListener="#{capControlForm.currentStratModel.resetValues}">
                        <f:selectItems value="#{selLists.cbcControlAlgorithim}"/>
                    </x:selectOneMenu>

                    <x:outputLabel for="Peak_Start_Time" value="Peak Start Time: " title="Starting time for the peak time window" />
                    <x:panelGroup>
                        <x:inputText id="Peak_Start_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStartTime}" >
                        </x:inputText>
                        <x:outputText id="PkStartLab" value="(HH:mm)"/>
                    </x:panelGroup>
            
                    <x:outputLabel for="Peak_Stop_Time" value="Peak Stop Time: " title="Stop time for the peak time window" />
                    <x:panelGroup>
                        <x:inputText id="Peak_Stop_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                            required="true" converter="cti_TimeConverter"
                            value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStopTime}" >
                        </x:inputText>
                        <x:outputText id="PkStopLab" value="(HH:mm)"/>
                    </x:panelGroup>

                </x:panelGrid>
                
                <x:panelGrid columns="3" columnClasses="gridCellMedium,gridCellMedium,gridCellMedium"> 
                    <x:outputText value=" "/>
                    <x:outputText value="Peak" style="font-weight: bold; padding: 3; text-align: center;"/>
                    <x:outputText value="Off Peak" style="font-weight: bold; padding: 3; text-align: center;"/>
                    
                    <x:outputText  value="Target PF*" styleClass="padlabel" rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
                    <x:panelGroup rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}">
                        <x:inputText forceId="true" id="Target_Peak_PF_Point" size="7" 
                            disabled="#{!capControlForm.editingCBCStrategy}"
                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                            required="true"
                            value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                            valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-100" maximum="100" />
                        </x:inputText>
                        <x:outputText value="%" styleClass="padlabel" rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
                    </x:panelGroup>
                    <x:panelGroup rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}">
	                    <x:inputText forceId="true" id="Target_OffPeak_PF_point" size="7" 
	                        disabled="#{!capControlForm.editingCBCStrategy}"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
	                        required="true"
	                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
	                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                        <f:validateDoubleRange minimum="-100" maximum="100" />
	                    </x:inputText>
	                    <x:outputText value="%" styleClass="padlabel" rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
                    </x:panelGroup>
                    
                    <x:panelGroup>
                        <x:outputText value="Min. of Bank Open" styleClass="char16PadLabel"
                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                            && capControlForm.currentStratModel.PFAlgorithm}"/>
	                    <x:outputText value="KVAR Leading" styleClass="char16PadLabel"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
	                        && capControlForm.currentStratModel.KVarAlgorithm}"/>
	                    <x:outputText value="Upper Volt Limit" styleClass="char16PadLabel" 
                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                            && !(capControlForm.currentStratModel.KVarAlgorithm || capControlForm.currentStratModel.PFAlgorithm)}"/>
                    </x:panelGroup>
                    <x:panelGroup>
	                    <x:inputText forceId="true" id="Peak_Leading" size="7"
	                        disabled="#{!capControlForm.editingCBCStrategy}"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
	                        && !capControlForm.currentStratModel.PFAlgorithm}" 
	                        required="true"
	                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
	                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
	                    </x:inputText>
	                    <x:panelGroup>
	                        <x:inputText forceId="true" id="PF_Peak_Leading" size="7"
	                            disabled="#{!capControlForm.editingCBCStrategy}"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
	                            && capControlForm.currentStratModel.PFAlgorithm}" 
	                            required="true"
	                            value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
	                            valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                            <f:validateLongRange minimum="51" maximum="100" />
	                        </x:inputText>
	                        <x:outputText value="%" styleClass="padlabel"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
	                    </x:panelGroup>
                    </x:panelGroup>
	                <x:panelGroup>
	                    <x:inputText forceId="true" id="Offpeak_Leading" size="7" 
	                        disabled="#{!capControlForm.editingCBCStrategy}"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LEAD]
	                        && !capControlForm.currentStratModel.PFAlgorithm}" 
	                        required="true"
	                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
	                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
	                    </x:inputText>
	                    <x:panelGroup>
	                        <x:inputText forceId="true" id="PF_Offpeak_Leading" size="7" 
	                            disabled="#{!capControlForm.editingCBCStrategy}"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LEAD]
	                            && capControlForm.currentStratModel.PFAlgorithm}" 
	                            required="true"
	                            value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
	                            valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                            <f:validateLongRange minimum="51" maximum="100" />
	                        </x:inputText>
	                        <x:outputText value="%" styleClass="padlabel"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
	                    </x:panelGroup>
                    </x:panelGroup>
                    
                    <x:panelGroup>
	                    <x:outputText value="Lower Volt Limit" styleClass="char16PadLabel"
                            rendered="#{ capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                            && !(capControlForm.currentStratModel.KVarAlgorithm || capControlForm.currentStratModel.PFAlgorithm)}"/>
	                    <x:outputText value="KVAR Lagging" styleClass="char16PadLabel"
	                        rendered="#{ capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
	                        && capControlForm.currentStratModel.KVarAlgorithm}"/>
	                    <x:outputText value="Min. of Bank Close" styleClass="char16PadLabel"
                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                            && capControlForm.currentStratModel.PFAlgorithm}"/>
                    </x:panelGroup>
                    <x:panelGroup>
	                    <x:inputText forceId="true" id="Peak_Lagging" size="7"
	                        disabled="#{!capControlForm.editingCBCStrategy}"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
	                        && !capControlForm.currentStratModel.PFAlgorithm}" 
	                        required="true"
	                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LAG]}" 
	                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
	                    </x:inputText>
	                    <x:panelGroup>
	                        <x:inputText forceId="true" id="PF_Peak_Lagging" size="7"
	                            disabled="#{!capControlForm.editingCBCStrategy}"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
	                            && capControlForm.currentStratModel.PFAlgorithm}" 
	                            required="true"
	                            value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LAG]}" 
	                            valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                            <f:validateLongRange minimum="51" maximum="100" />
	                        </x:inputText>
	                        <x:outputText value="%" styleClass="padlabel" 
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
	                    </x:panelGroup>
                    </x:panelGroup>
                    <x:panelGroup>
	                    <x:inputText forceId="true" id="Offpeak_Lagging" size="7" 
	                        disabled="#{!capControlForm.editingCBCStrategy}"
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LAG]
	                        && !capControlForm.currentStratModel.PFAlgorithm}" 
	                        required="true"
	                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LAG]}" 
	                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
	                    </x:inputText>
	                    <x:panelGroup>
	                        <x:inputText forceId="true" id="PF_Offpeak_Lagging" size="7" 
	                            disabled="#{!capControlForm.editingCBCStrategy}"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LAG]
	                            && capControlForm.currentStratModel.PFAlgorithm}" 
	                            required="true"
	                            value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LAG]}" 
	                            valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
	                            <f:validateLongRange minimum="51" maximum="100" />
	                        </x:inputText>
	                        <x:outputText value="%" styleClass="padlabel"
	                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
	                    </x:panelGroup>
                    </x:panelGroup>
                    
                    <x:panelGroup>
	                    <x:outputText value="Upper Volt Limit" styleClass="char16PadLabel" 
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]
	                        && !capControlForm.currentStratModel.voltVar}"/>
	                    <x:outputText value="KVAR Leading" styleClass="char16PadLabel"
                            rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]
                            && capControlForm.currentStratModel.voltVar}"/>
                    </x:panelGroup>
                    <x:inputText forceId="true" id="peak_upper" size="7"
                        disabled="#{!capControlForm.editingCBCStrategy}"
                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                        required="true"
                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <x:inputText forceId="true" id="offpeak_upper" size="7" required="true"
                        disabled="#{!capControlForm.editingCBCStrategy}"
                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    
                    <x:panelGroup>
	                    <x:outputText value="Lower Volt Limit" styleClass="char16PadLabel" 
	                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]
	                        && !capControlForm.currentStratModel.voltVar}"/>
	                    <x:outputText value="KVAR Lagging" styleClass="char16PadLabel" 
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]
                             && capControlForm.currentStratModel.voltVar}"/>
	                </x:panelGroup>
                    <x:inputText forceId="true" id="peak_lower"  size="7" 
                        disabled="#{!capControlForm.editingCBCStrategy}"
                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]}"                                 
                        required="true"
                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LOWER]}" 
                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <x:inputText forceId="true" id="offpeak_lower" size="7" 
                        disabled="#{!capControlForm.editingCBCStrategy}"
                        rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                        required="true"
                        value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                        valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    
                </x:panelGrid>
                    
                <x:outputText value="*(-) indicates leading" rendered="#{capControlForm.currentStratModel.PFAlgorithm}"/>

            </x:htmlTag>
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
         
        <h:column rendered="#{capControlForm.currentStrategyID != 0 && (capControlForm.timeOfDay)}" >
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Strategy Settings" />
                </x:htmlTag>
                
                <x:panelGrid columns="3" cellspacing="3">
                    <x:outputText value=" "/>
                    <x:outputText value="Close" style="font-weight: bold; text-align: center;"/>
                    <x:outputText value="Open" style="font-weight: bold; text-align: center;"/>
                    
                    <x:outputLabel for="hourZeroClose" id="hourZeroLabel" value = "00:00"/>
                    <x:inputText id="hourZeroClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourZero.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourZeroClose" id="hourZeroOpen" value="#{capControlForm.strategyTimeOfDay.hourZero.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="hourOneClose" id="hourOneLabel" value="01:00"/>
                    <x:inputText id="hourOneClose" disabled="#{!capControlForm.editingCBCStrategy}" 
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourOne.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourOneClose" id="hourOneOpen" value="#{capControlForm.strategyTimeOfDay.hourOne.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="hourTwoClose" id="hourTwoLabel" value="02:00"/>
                    <x:inputText id="hourTwoClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwo.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwoClose" id="hourTwoOpen" value="#{capControlForm.strategyTimeOfDay.hourTwo.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>

                    <x:outputLabel for="hourThreeClose" id="hourThreeLabel" value="03:00"/>
                    <x:inputText id="hourThreeClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourThree.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourThreeClose"  id="hourThreeOpen" value="#{capControlForm.strategyTimeOfDay.hourThree.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="hourFourClose" id="hourFourLabel" value="04:00"/>
                    <x:inputText id="hourFourClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourFour.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourFourClose" id="hourFourOpen" value="#{capControlForm.strategyTimeOfDay.hourFour.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="hourFiveClose" id="hourFiveLabel" value="05:00"/>
                    <x:inputText id="hourFiveClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourFive.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourFiveClose" id="hourFiveOpen" value="#{capControlForm.strategyTimeOfDay.hourFive.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                    
                    <x:outputLabel for="hourSixClose" id="hourSixLabel" value="06:00"/>
                    <x:inputText id="hourSixClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourSix.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourSixClose" id="hourSixOpen" value="#{capControlForm.strategyTimeOfDay.hourSix.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>

                    <x:outputLabel for="hourSevenClose" id="hourSevenLabel" value="07:00"/>
                    <x:inputText id="hourSevenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourSeven.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourSevenClose" id="hourSevenOpen" value="#{capControlForm.strategyTimeOfDay.hourSeven.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourEightClose" id="hourEightLabel" value="08:00"/>
                    <x:inputText id="hourEightClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourEight.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourEightClose" id="hourEightOpen" value="#{capControlForm.strategyTimeOfDay.hourEight.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>

                    <x:outputLabel for="hourNineClose" id="hourNineLabel" value="09:00"/>
                    <x:inputText id="hourNineClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourNine.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourNineClose" id="hourNineOpen" value="#{capControlForm.strategyTimeOfDay.hourNine.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourTenClose" id="hourTenLabel" value="10:00"/>
                    <x:inputText id="hourTenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTenClose" id="hourTenOpen" value="#{capControlForm.strategyTimeOfDay.hourTen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourElevenClose" id="hourElevenLabel" value="11:00"/>
                    <x:inputText id="hourElevenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourEleven.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourElevenClose" id="hourElevenOpen" value="#{capControlForm.strategyTimeOfDay.hourEleven.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourTwelveClose" id="hourTwelveLabel" value="12:00"/>
                    <x:inputText id="hourTwelveClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwelve.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwelveClose" id="hourTwelveOpen" value="#{capControlForm.strategyTimeOfDay.hourTwelve.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourThirteenClose" id="hourThirteenLabel" value="13:00"/>
                    <x:inputText id="hourThirteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourThirteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourThirteenClose" id="hourThirteenOpen" value="#{capControlForm.strategyTimeOfDay.hourThirteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourFourteenClose" id="hourFourteenLabel" value = "14:00"/>
                    <x:inputText id="hourFourteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourFourteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourFourteenClose" id="hourFourteenOpen" value="#{capControlForm.strategyTimeOfDay.hourFourteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourFifteenClose" id="hourFifteenLabel" value="15:00"/>
                    <x:inputText id="hourFifteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourFifteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourFifteenClose" id="hourFifteenOpen" value="#{capControlForm.strategyTimeOfDay.hourFifteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourSixteenClose" id="hourSixteenLabel" value="16:00"/>
                    <x:inputText id="hourSixteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourSixteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourSixteenClose" id="hourSixteenOpen" value="#{capControlForm.strategyTimeOfDay.hourSixteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourSeventeenClose" id="hourSeventeenLabel" value="17:00"/>
                    <x:inputText id="hourSeventeenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourSeventeen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourSeventeenClose" id="hourSeventeenOpen" value="#{capControlForm.strategyTimeOfDay.hourSeventeen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>

                    <x:outputLabel for="hourEighteenClose" id="hourEighteenLabel" value="18:00"/>
                    <x:inputText id="hourEighteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourEighteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourEighteenClose" id="hourEighteenOpen" value="#{capControlForm.strategyTimeOfDay.hourEighteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourNineteenClose" id="hourNineteenLabel" value="19:00"/>
                    <x:inputText id="hourNineteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourNineteen.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourNineteenClose" id="hourNineteenOpen" value="#{capControlForm.strategyTimeOfDay.hourNineteen.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>

                    <x:outputLabel for="hourTwentyClose" id="hourTwentyLabel" value="20:00"/>
                    <x:inputText id="hourTwentyClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwenty.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwentyClose" id="hourTwentyOpen" value="#{capControlForm.strategyTimeOfDay.hourTwenty.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourTwentyOneClose" id="hourTwentyOneLabel" value="21:00"/>
                    <x:inputText id="hourTwentyOneClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwentyOne.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwentyOneClose" id="hourTwentyOneOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyOne.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourTwentyTwoClose" id="hourTwentyTwoLabel" value="22:00"/>
                    <x:inputText id="hourTwentyTwoClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwentyTwo.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwentyTwoClose" id="hourTwentyTwoOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyTwo.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
        
                    <x:outputLabel for="hourTwentyThreeClose" id="hourTwentyThreeLabel" value="23:00"/>
                    <x:inputText id="hourTwentyThreeClose" disabled="#{!capControlForm.editingCBCStrategy}"
                        size="3" value="#{capControlForm.strategyTimeOfDay.hourTwentyThree.percentClose}"/>
                    <x:panelGroup>
                        <x:outputLabel for="hourTwentyThreeClose" id="hourTwentyThreeOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyThree.percentOpen}"/>
                        <x:outputText value="%"/>
                    </x:panelGroup>
                </x:panelGrid>
            </x:htmlTag>
        </h:column>
    </h:panelGrid>
	<x:inputHidden forceId="true" id="controlAlgoToggle" value="#{capControlForm.currentStratModel.resetPkOffPkVals}" />
</f:subview>