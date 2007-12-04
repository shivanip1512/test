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
                                                        ||capControlForm.visibleTabs['CBCSubstationBus'] 
                                                        || capControlForm.visibleTabs['CBCFeeder'] 
                                                        || capControlForm.visibleTabs['CBCStrategy']}" >

    <f:subview id="paoArea" rendered="#{capControlForm.visibleTabs['CBCArea']}" >
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Area_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
		<x:selectOneMenu id="Area_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlArea.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}" >
			<f:selectItems value="#{capControlForm.cbcStrategies}"/>
		</x:selectOneMenu>
    </f:subview>

    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstationBus']}" >
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Sub_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
		<x:selectOneMenu id="Sub_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}">
			<f:selectItems value="#{capControlForm.cbcStrategies}"/>
		</x:selectOneMenu>
    </f:subview>

    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Feeder_Strategy_Selection" value="Selected Strategy: " title="The current control strategy we are using"/>
		<x:selectOneMenu id="Feeder_Strategy_Selection" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlFeeder.strategyID}" 
                valueChangeListener="#{capControlForm.newStrategySelected}">
			<f:selectItems value="#{capControlForm.cbcStrategies}"/>
		</x:selectOneMenu>
    </f:subview>

	<f:verbatim><br/></f:verbatim>
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
	<f:verbatim><br/></f:verbatim>
	<x:panelGroup id="Strategy_Buttons" forceId="true">
		<x:commandButton id="Create_Strategy" value="New Strategy" title="Create a new strategy"
				action="#{capControlForm.createStrategy}" styleClass="stdButton" />
		<x:commandButton id="Delete_Strategy" value="Delete Strategy" title="Delete the selected strategy" styleClass="stdButton"
				action="#{capControlForm.deleteStrategy}" onclick="return window.confirm('Are you sure you want to delete this strategy?\r\nNote: Deleting this strategy will force all data to be saved and the current strategy will be set to (none).');"
				disabled="#{capControlForm.currentStrategyID == 0}">
				<f:actionListener type="com.cannontech.web.editor.CtiNavActionListener" />
		</x:commandButton>
	</x:panelGroup>
	
	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
		
		    <f:verbatim><br/>
                <fieldset>
                    <legend>Strategy Detail</legend>
		    </f:verbatim>
		    <f:verbatim>
		    <table>
		    <tr>
		    <td>
		    </f:verbatim>
		    
		    <x:outputLabel for="Strategy_Name" value="Strategy Name: " title="A label for the strategy in the system"/>
		    
		    <f:verbatim>
            </td>
            <td>
		    </f:verbatim>
		    
            <x:inputText id="Strategy_Name" disabled="#{!capControlForm.editingCBCStrategy}" required="true" maxlength="32"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].strategyName}" />
                    
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
            
			<x:outputLabel for="Control_Method" value="Control Method: " title="How the CapBanks are to be controlled"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:selectOneMenu id="Control_Method" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlMethod}" 
                    valueChangeListener="#{capControlForm.currentStratModel.resetValues}">
                    
                <f:selectItems value="#{capControlForm.controlMethods}"/>
            </x:selectOneMenu>
	
			<f:verbatim>
			</td>
			</tr>
			<tr>
			<td>
            </f:verbatim>
            
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Control_Interval" value="Analysis Interval: " title="How often the system should check to determine the need for control"/>
	
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Control_Interval" disabled="#{!capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlInterval}" >
                <f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
                <f:selectItems value="#{capControlForm.timeInterval}"/>
            </x:selectOneMenu>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Max_Confirm_Time" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Max_Confirm_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minResponseTime}" >
                <f:selectItem itemLabel="(none)" itemValue="0"/>
                <f:selectItems value="#{capControlForm.timeInterval}"/>
            </x:selectOneMenu>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Pass_Percent" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:inputText rendered="#{!capControlForm.timeOfDay}" id="Pass_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minConfirmPercent}">
                    <f:validateLongRange minimum="0" maximum="100" />
            </x:inputText>
            <x:outputText rendered="#{!capControlForm.timeOfDay}" id="PassPercLab" value="%"/>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Failure_Percent" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:inputText rendered="#{!capControlForm.timeOfDay}" id="Failure_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].failurePercent}">
                    <f:validateLongRange minimum="0" maximum="100" />
            </x:inputText>
            <x:outputText rendered="#{!capControlForm.timeOfDay}" id="FailPercLab" value="%"/>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Send_Retries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:inputText rendered="#{!capControlForm.timeOfDay}" id="Send_Retries" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlSendRetries}">
                    <f:validateLongRange minimum="0" maximum="100" />
            </x:inputText>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="Delay_Time" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			
			<x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="Delay_Time" disabled="#{!capControlForm.editingCBCStrategy}"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlDelayTime}" >
                <f:selectItem itemValue="0" itemLabel="(none)" />
                <f:selectItems value="#{capControlForm.timeInterval}" />
            </x:selectOneMenu>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
			
			<x:selectBooleanCheckbox rendered="#{!capControlForm.timeOfDay}" id="IntegrateFlag" value="#{capControlForm.currentStratModel.integrateFlag}" 
                    disabled="#{!capControlForm.editingCBCStrategy}" onclick="submit();"/>
			<x:outputLabel rendered="#{!capControlForm.timeOfDay}" for="IntegrateFlag" value="Integrate Control?"/>
			
            <f:verbatim>
            </td>
            <td>
            </f:verbatim>
            
            <x:selectOneMenu rendered="#{!capControlForm.timeOfDay}" id="IntegratePeriods" disabled="#{!capControlForm.editingCBCStrategy || !capControlForm.currentStratModel.integrateFlag}"
                    value="#{capControlForm.currentStratModel.integratePeriod}" >
                <f:selectItems value="#{selLists.integrationPeriods}" />
            </x:selectOneMenu>
            
            <f:verbatim>
            </td>
            </tr>
            </table>
            <table>
            <tr>
            <td>
            </f:verbatim>
            
            <h:selectBooleanCheckbox id="Like_Day_Fall_Back_CheckBox" onclick="submit();" rendered="#{!capControlForm.timeOfDay}"
                value="#{capControlForm.currentStratModel.likeDayFallBack}" disabled="#{!capControlForm.editingCBCStrategy}" />
            <x:outputLabel for="Like_Day_Fall_Back_CheckBox" value="Fall back to like-day history control." rendered="#{!capControlForm.timeOfDay}"
                title="Check to use like-day history control on this strategy."/>
	
			<f:verbatim>
			</td>
			</tr>
			</table>
            </fieldset>
			</f:verbatim>
	
            <h:panelGrid rendered="#{!capControlForm.timeOfDay}">
            <h:column>
		    <f:verbatim><br/><fieldset><legend>Strategy Operations</legend></f:verbatim>
			<x:outputLabel for="Max_Daily_Ops" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
			<x:inputText id="Max_Daily_Ops" styleClass="char16Label" required="true"
					disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxDailyOperation}" >
					<f:validateLongRange minimum="0" maximum="9999" />
			</x:inputText>
			<x:outputText id="MaxDailyOpsDesc" value="(0 = unlimited)"/>
	
			<f:verbatim><br/></f:verbatim>
			<h:selectBooleanCheckbox id="Disabled_Ops" value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxOperationDisabled}"
					disabled="#{!capControlForm.editingCBCStrategy}" />
			<x:outputLabel for="Disabled_Ops" value="Disable upon reaching max operations" 
                title="Should we be automatically disabled after reaching our max op counts"/>
			<f:verbatim></fieldset></f:verbatim>
			</h:column>
			</h:panelGrid>
		</h:column>

		<h:column rendered="#{capControlForm.currentStrategyID != 0 && (!capControlForm.timeOfDay)}" >
            <f:verbatim><br/><fieldset><legend>Strategy Peaks</legend>
            <table>
            <tr>
            <td>
            </f:verbatim>
            
			<x:outputLabel for="Control_Algorithm" value="Control Algorithm: " title="The units and process we use to make control decisions"/>
			
			<f:verbatim>
			</td>
			<td>
			</f:verbatim>
			<x:selectOneMenu id="Control_Algorithm" onchange="controlAlgoChanged();" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlUnits}" 
                    valueChangeListener="#{capControlForm.currentStratModel.resetValues}"   
                    >
				<f:selectItems value="#{selLists.cbcControlAlgorithim}"/>
			</x:selectOneMenu>

            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
            
            <x:outputLabel for="Peak_Start_Time" value="Peak Start Time: " title="Starting time for the peak time window" />
            
            <f:verbatim>
            </td>
            <td>
            </f:verbatim>
            
            <x:inputText id="Peak_Start_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                required="true" converter="cti_TimeConverter"
                value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStartTime}" >
            </x:inputText>
            <x:outputText id="PkStartLab" value="(HH:mm)"/>
            
            <f:verbatim>
            </td>
            </tr>
            <tr>
            <td>
            </f:verbatim>
            
            <x:outputLabel for="Peak_Stop_Time" value="Peak Stop Time: " title="Stop time for the peak time window" />
            
            <f:verbatim>
            </td>
            <td>
            </f:verbatim>
            
            <x:inputText id="Peak_Stop_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                    required="true" converter="cti_TimeConverter"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStopTime}" >
            </x:inputText>
            <x:outputText id="PkStopLab" value="(HH:mm)"/>

            <f:verbatim>
            </td>
            </tr>
            </table>
            <br>
            <br>
            </f:verbatim>

            <f:verbatim>
               <table id="algorithmTableData" >
                <tr id="headerRow"> 
                    <td> </td>
                    <td style="font-weight: bold; 
                           background-color: #E7EFF7;padding: 3;text-align: center;">
                           Peak Setting </td>
                    <td width="20px"/>
                    <td width="20px"/>
                    <td style="font-weight: bold; 
                           background-color: #E7EFF7;padding: 3;text-align: center;">
                           OffPeak Settings</td>
                </tr>
                 <tr id="pfrow">
                    <td id="lbl_TargetPF">
            </f:verbatim>  
                    <x:outputText  value="Target PF*" styleClass="padlabel"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
             <f:verbatim>
                    </td> <!--lbl_TargetPF-->
 
                <td id="peakPFPoint"> 
            </f:verbatim>
              <x:inputText forceId="true" id="Target_Peak_PF_Point" size="7" 
                                             disabled="#{!capControlForm.editingCBCStrategy}"
                                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                                             required="true"
                                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                                        <f:validateDoubleRange minimum="-100" maximum="100" />
                            </x:inputText>
                            <x:outputText value="%" styleClass="padlabel"
                                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
            
            <f:verbatim>
                </td> <!--peakPFPoint-->
                    <td width="20px"/>
                    <td width="20px"/>
                <td id="offPkPFPoint">
            </f:verbatim> 
                 <x:inputText forceId="true" id="Target_OffPeak_PF_point" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-100" maximum="100" />
                </x:inputText>
                <x:outputText value="%" styleClass="padlabel" rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
            <f:verbatim>
                </td> <!--offPkPFPoint-->
              </tr>    <!--pfrow-->
                <tr id="upperLimit1">
                    <td id="lbl_upperLimit1">
            </f:verbatim>
                <x:outputText value="KVAR Leading" styleClass="char16PadLabel"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                             && capControlForm.currentStratModel.KVarAlgorithm}"/> 
                <x:outputText value="Upper Volt Limit" styleClass="char16PadLabel"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                             && !(capControlForm.currentStratModel.KVarAlgorithm || capControlForm.currentStratModel.PFAlgorithm)}"/> 
                <x:outputText value="Min. of BankOpen" styleClass="char16PadLabel"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                             && capControlForm.currentStratModel.PFAlgorithm}"/> 
            <f:verbatim>
                    </td>
                    <td id="peakLead1"> 
            </f:verbatim>
                <x:inputText
                forceId="true" id="Peak_Leading" size="7"
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                             && !capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                                value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
                                valueChangeListener="#{capControlForm.currentStratModel.dataChanged}" 
                                
                                >
                                <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
                <x:inputText forceId="true" id="PF_Peak_Leading" size="7"
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]
                             && capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                                value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
                                valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                                <f:validateLongRange minimum="0" maximum="100" />
                </x:inputText>

                <x:outputText value="%" styleClass="padlabel"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>

            <f:verbatim>
                    </td>
                   <td width="20px"/>
                    <td width="20px"/>
                    <td id="offPkLead1"> 
            </f:verbatim>
                <x:inputText forceId="true" id="Offpeak_Leading" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LEAD]
                             && !capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                             <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
                <x:inputText forceId="true" id="PF_Offpeak_Leading" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LEAD]
                             && capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateLongRange minimum="0" maximum="100" />
                </x:inputText>
                <x:outputText value="%" styleClass="padlabel"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
            <f:verbatim>
                    </td>
                </tr>
            <!--end UpperLimit1-->
<!--start lowLimit1 -->
              <tr id="lowLimit1">
                <td id="lbl_lowLimit1">
            </f:verbatim>
               <x:outputText value="KVAR Lagging" styleClass="char16PadLabel"
                             rendered="#{ capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                             && capControlForm.currentStratModel.KVarAlgorithm}"/>
               <x:outputText value="Lower Volt Limit" styleClass="char16PadLabel"
                             rendered="#{ capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                             && !(capControlForm.currentStratModel.KVarAlgorithm || capControlForm.currentStratModel.PFAlgorithm)}"/>
               <x:outputText value="Min. of BankClose" styleClass="char16PadLabel"
                             rendered="#{ capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                             && capControlForm.currentStratModel.PFAlgorithm}"/>
            <f:verbatim>    
                </td> <!--lbl_lowLimit-->
                <td id="peakLagging1">

            </f:verbatim>
                <x:inputText forceId="true" id="Peak_Lagging" size="7"
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                             && !capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LAG]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                             <f:validateDoubleRange minimum="-999999" maximum="999999" />
                
                </x:inputText>
                <x:inputText forceId="true" id="PF_Peak_Lagging" size="7"
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]
                             && capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LAG]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateLongRange minimum="0" maximum="100" />
                </x:inputText>

                <x:outputText value="%" styleClass="padlabel"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
            <f:verbatim>    
                </td> <!--peakLagging-->
                <td width="20px"/>
                <td width="20px"/>
                <td id="offPkLagging1">
            </f:verbatim>
                <x:inputText forceId="true" id="Offpeak_Lagging" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LAG]
                             && !capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LAG]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                             <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
                <x:inputText forceId="true" id="PF_Offpeak_Lagging" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LAG]
                             && capControlForm.currentStratModel.PFAlgorithm}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LAG]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                <f:validateLongRange minimum="0" maximum="100" />
                </x:inputText>
                <x:outputText value="%" styleClass="padlabel"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}"/>
            <f:verbatim>
                </td> <!--offPkLagging-->
               </tr>
<!-- stop lowLimit2 -->
                <tr id="upperLimit2">
                    <td id="lbl_upperLimit2">
            </f:verbatim>
               <x:outputText value="Upper Volt Limit" styleClass="char16PadLabel"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]
                             && !capControlForm.currentStratModel.voltVar}"/>
                <x:outputText value="KVAR Leading" styleClass="char16PadLabel"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]
                             && capControlForm.currentStratModel.voltVar}"/>
            <f:verbatim>
                    </td>
                    <td id="peakLead2"> 
            </f:verbatim>
                   <x:inputText forceId="true" id="peak_upper" size="7"
                                 disabled="#{!capControlForm.editingCBCStrategy}"
                                 rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                                 required="true"
                                    value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                                    valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                                    <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
            <f:verbatim>
                    </td>
                    <td width="20px"/>
                    <td width="20px"/>
                    <td id="offPkLead2"> 
            </f:verbatim>
                <x:inputText forceId="true" id="offpeak_upper" size="7" required="true"
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>

                
            <f:verbatim>
                    </td>
                </tr>
            <!--end upperLimit2-->            
            <!-- start lowLimit2 -->
                <tr id="lowerLimit2">
                    <td id="lbl_lowerLimit2">
            </f:verbatim>
            <x:outputText value="Lower Volt Limit" styleClass="char16PadLabel" 
                         rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]
                         && !capControlForm.currentStratModel.voltVar
                         }"/>
            <x:outputText value="KVAR Lagging" styleClass="char16PadLabel" 
                         rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]
                         && capControlForm.currentStratModel.voltVar
                         }"/>              
            <f:verbatim>
                    </td>
                    <td id="peakLag2"> 
            </f:verbatim>
               <x:inputText forceId="true" id="peak_lower"  size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]}"                                 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LOWER]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                        <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
            <f:verbatim>
                    </td>
                    <td width="20px"/>
                    <td width="20px"/>
                    <td id="offPkLag2"> 
            </f:verbatim>
                <x:inputText forceId="true" id="offpeak_lower" size="7" 
                             disabled="#{!capControlForm.editingCBCStrategy}"
                             rendered="#{capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                             required="true"
                             value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                             valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
            <f:verbatim>
                    </td>
                </tr>            

      <tr>
        <td>
     </f:verbatim>
     <x:outputText value="*(-) indicates leading" 
                    rendered="#{capControlForm.currentStratModel.PFAlgorithm}"/>
    <f:verbatim>
        </td>
        <td/>
        <td width="20px"/>
        <td width="20px"/>
        <td/>    
    </tr>
    </table>
    </f:verbatim>
    <f:verbatim></fieldset></f:verbatim>
    <f:verbatim><br/><fieldset><legend>Peak Operating Days</legend></f:verbatim>
    
        <x:selectManyCheckbox id="Peak_Operating_Days" value="#{capControlForm.stratDaysOfWeek}"
        disabled="#{!capControlForm.editingCBCStrategy}" layout="pageDirection" 
        rendered="#{capControlForm.currentStrategyID != 0}">
            <f:selectItems value="#{selLists.daySelections}"/>
        </x:selectManyCheckbox>
        <f:verbatim></fieldset></f:verbatim>
    </h:column>
         
    <h:column rendered="#{capControlForm.currentStrategyID != 0 && (capControlForm.timeOfDay)}" >
         
        <f:verbatim><br/><fieldset><legend>Strategy Settings</legend>
        <table>
        <tr>
        <td/>
        <td>
        </f:verbatim>
            <x:outputLabel for="hourZeroClose" value="Close:"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
            <x:outputLabel for="hourZeroClose" value="Open:"/>        
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourZeroClose" id="hourZeroLabel" value = "00:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourZeroClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourZero.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourZeroClose" id="hourZeroOpen" value="#{capControlForm.strategyTimeOfDay.hourZero.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourOneClose" id="hourOneLabel" value="01:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourOneClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourOne.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourOneClose" id="hourOneOpen" value="#{capControlForm.strategyTimeOfDay.hourOne.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwoClose" id="hourTwoLabel" value="02:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwoClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwo.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwoClose" id="hourTwoOpen" value="#{capControlForm.strategyTimeOfDay.hourTwo.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourThreeClose" id="hourThreeLabel" value="03:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourThreeClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourThree.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourThreeClose"  id="hourThreeOpen" value="#{capControlForm.strategyTimeOfDay.hourThree.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFourClose" id="hourFourLabel" value="04:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourFourClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourFour.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFourClose" id="hourFourOpen" value="#{capControlForm.strategyTimeOfDay.hourFour.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFiveClose" id="hourFiveLabel" value="05:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourFiveClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourFive.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFiveClose" id="hourFiveOpen" value="#{capControlForm.strategyTimeOfDay.hourFive.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSixClose" id="hourSixLabel" value="06:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourSixClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourSix.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSixClose" id="hourSixOpen" value="#{capControlForm.strategyTimeOfDay.hourSix.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSevenClose" id="hourSevenLabel" value="07:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourSevenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourSeven.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSevenClose" id="hourSevenOpen" value="#{capControlForm.strategyTimeOfDay.hourSeven.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourEightClose" id="hourEightLabel" value="08:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourEightClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourEight.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourEightClose" id="hourEightOpen" value="#{capControlForm.strategyTimeOfDay.hourEight.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourNineClose" id="hourNineLabel" value="09:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourNineClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourNine.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourNineClose" id="hourNineOpen" value="#{capControlForm.strategyTimeOfDay.hourNine.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTenClose" id="hourTenLabel" value="10:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTenClose" id="hourTenOpen" value="#{capControlForm.strategyTimeOfDay.hourTen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourElevenClose" id="hourElevenLabel" value="11:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourElevenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourEleven.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourElevenClose" id="hourElevenOpen" value="#{capControlForm.strategyTimeOfDay.hourEleven.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwelveClose" id="hourTwelveLabel" value="12:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwelveClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwelve.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwelveClose" id="hourTwelveOpen" value="#{capControlForm.strategyTimeOfDay.hourTwelve.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourThirteenClose" id="hourThirteenLabel" value="13:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourThirteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourThirteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourThirteenClose" id="hourThirteenOpen" value="#{capControlForm.strategyTimeOfDay.hourThirteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFourteenClose" id="hourFourteenLabel" value = "14:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourFourteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourFourteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFourteenClose" id="hourFourteenOpen" value="#{capControlForm.strategyTimeOfDay.hourFourteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFifteenClose" id="hourFifteenLabel" value="15:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourFifteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourFifteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourFifteenClose" id="hourFifteenOpen" value="#{capControlForm.strategyTimeOfDay.hourFifteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSixteenClose" id="hourSixteenLabel" value="16:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourSixteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourSixteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSixteenClose" id="hourSixteenOpen" value="#{capControlForm.strategyTimeOfDay.hourSixteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSeventeenClose" id="hourSeventeenLabel" value="17:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourSeventeenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourSeventeen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourSeventeenClose" id="hourSeventeenOpen" value="#{capControlForm.strategyTimeOfDay.hourSeventeen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourEighteenClose" id="hourEighteenLabel" value="18:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourEighteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourEighteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourEighteenClose" id="hourEighteenOpen" value="#{capControlForm.strategyTimeOfDay.hourEighteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourNineteenClose" id="hourNineteenLabel" value="19:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourNineteenClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourNineteen.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourNineteenClose" id="hourNineteenOpen" value="#{capControlForm.strategyTimeOfDay.hourNineteen.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyClose" id="hourTwentyLabel" value="20:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwentyClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwenty.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyClose" id="hourTwentyOpen" value="#{capControlForm.strategyTimeOfDay.hourTwenty.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyOneClose" id="hourTwentyOneLabel" value="21:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwentyOneClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwentyOne.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyOneClose" id="hourTwentyOneOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyOne.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyTwoClose" id="hourTwentyTwoLabel" value="22:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwentyTwoClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwentyTwo.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyTwoClose" id="hourTwentyTwoOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyTwo.percentOpen}"/>
        <f:verbatim>
        </td>
        </tr>
        <tr>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyThreeClose" id="hourTwentyThreeLabel" value="23:00"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:inputText id="hourTwentyThreeClose" disabled="#{!capControlForm.editingCBCStrategy}"
        value="#{capControlForm.strategyTimeOfDay.hourTwentyThree.percentClose}"/>
        <f:verbatim>
        </td>
        <td>
        </f:verbatim>
        <x:outputLabel for="hourTwentyThreeClose" id="hourTwentyThreeOpen" value="#{capControlForm.strategyTimeOfDay.hourTwentyThree.percentOpen}"/>
    </h:column>
    </h:panelGrid>
	<x:inputHidden forceId="true" id="controlAlgoToggle" value="#{capControlForm.currentStratModel.resetPkOffPkVals}" />
</f:subview>