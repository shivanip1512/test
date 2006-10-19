
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="cbcStrategy" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}" >

    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}" >
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
	
	<h:selectBooleanCheckbox id="Edit_Strategy" onclick="lockButtonsPerSubmit('Strategy_Buttons'); submit();"
		value="#{capControlForm.editingCBCStrategy}"
		disabled="#{capControlForm.currentStrategyID == 0}" />
		
	<x:outputLabel for="Edit_Strategy" value="Edit Strategy" title="A toggle to edit the selected strategy"/>
	<x:outputText id="stratNameWarn" styleClass="alert"
		rendered="#{capControlForm.editingCBCStrategy}" value="  (WARNING: Modifying this strategy will affect all feeders or subs that use this strategy)"/>
	<f:verbatim><br/></f:verbatim>
	<x:panelGroup id="Strategy_Buttons" forceId="true">
	<x:commandButton id="Create_Strategy" value="New Strategy" title="Create a new strategy"
			action="#{capControlForm.createStrategy}" styleClass="stdButton" />
	<x:commandButton id="Delete_Strategy" value="Delete Strategy" title="Delete the selected strategy" styleClass="stdButton"
			action="#{capControlForm.deleteStrategy}" onclick="return window.confirm('Are you sure you want to delete this strategy?\r\nNote: Deleting this strategy will force all data to be saved and the current strategy will be set to (none).');"
			disabled="#{capControlForm.currentStrategyID == 0}" />
	</x:panelGroup>
	
	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
		    <f:verbatim><br/><fieldset><legend>Strategy Detail</legend></f:verbatim>
			<x:outputLabel for="Strategy_Name" value="Strategy Name: " title="A label for the strategy in the system"/>
			<x:inputText id="Strategy_Name" disabled="#{!capControlForm.editingCBCStrategy}" required="true" maxlength="32"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].strategyName}" />
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Control_Method" value="Control Method: " title="How the CapBanks are to be controlled"/>
			<x:selectOneMenu id="Control_Method" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlMethod}" >
				<f:selectItems value="#{capControlForm.controlMethods}"/>
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Control_Interval" value="Analysis Interval: " title="How often the system should check to determine the need for control"/>
			<x:selectOneMenu id="Control_Interval" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlInterval}" >
				<f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
				<f:selectItems value="#{capControlForm.timeInterval}"/>
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Max_Confirm_Time" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
			<x:selectOneMenu id="Max_Confirm_Time" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minResponseTime}" >
				<f:selectItem itemLabel="(none)" itemValue="0"/>
				<f:selectItems value="#{capControlForm.timeInterval}"/>				
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Pass_Percent" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control"/>
			<x:inputText id="Pass_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minConfirmPercent}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
			<x:outputText id="PassPercLab" value="%"/>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Failure_Percent" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
			<x:inputText id="Failure_Percent" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].failurePercent}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
			<x:outputText id="FailPercLab" value="%"/>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Send_Retries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
			<x:inputText id="Send_Retries" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlSendRetries}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="Delay_Time" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
			<x:selectOneMenu id="Delay_Time" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlDelayTime}" >
				<f:selectItem itemValue="0" itemLabel="(none)" />
				<f:selectItems value="#{capControlForm.timeInterval}" />
			</x:selectOneMenu>
	
	
			<f:verbatim></fieldset></f:verbatim>
	
	
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
			<x:outputLabel for="DisabledOps" value="Disable upon reaching max operations" title="Should we be automatically disabled after reaching our max op counts"/>
			<f:verbatim></fieldset></f:verbatim>
		</h:column>

		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
        <f:verbatim><fieldset><legend>Strategy Peaks</legend></f:verbatim>

			<x:outputLabel for="Control_Algorithim" value="Control Algorithim: " title="The units and process we use to make control decisions"/>
			<x:selectOneMenu id="Control_Algorithim" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlUnits}" 
                    >
				<f:selectItems value="#{selLists.cbcControlAlgorithim}"/>
			</x:selectOneMenu>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="Peak_Start_Time" value="Peak Start Time: " title="Starting time for the peak time window" />
                <x:inputText id="Peak_Start_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                        required="true" converter="cti_TimeConverter"
                        value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStartTime}" >
                </x:inputText>
                <x:outputText id="PkStartLab" value="(HH:mm)"/>

            <x:outputLabel for="Peak_Stop_Time" value="Peak Stop Time: " title="Stop time for the peak time window" />
            <x:inputText id="Peak_Stop_Time" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
                    required="true" converter="cti_TimeConverter"
                    value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStopTime}" >
            </x:inputText>
            <x:outputText id="PkStopLab" value="(HH:mm)"/>


			<f:verbatim><br/></f:verbatim>
             <h:panelGrid id="peaks" columns="5" >
                <h:column>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputText value="Peak" styleClass="char16Label"
                            />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputText value="OffPeak" styleClass="char16Label"
                            />

                </h:column>
                <h:column id="lower">
                    <x:outputText value="Lower"  
                                  title="The lower limit value for peak control (upper value for the peak control window)"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="peak_lower"  size="7"
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 ||
                                 capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LOWER]}"                                 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LOWER]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="offpeak_lower" size="7" 
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LOWER]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                    <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                </h:column>
                <h:column id="upper">
                  <x:outputText value="Upper" 
                                  title="The leading value for peak control (upper value for the peak control window)"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="peak_upper" size="7"
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                                 required="true"
                                    value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_UPPER]}" 
                                    valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                                    <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText id="offpeak_upper" size="7" required="true"
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_UPPER]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>

                </h:column>

                <h:column id="lag">
                    <x:outputText value="Lagging"  
                                  title="The lower limit value for peak control (upper value for the peak control window)"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="peak_lagging" size="7"
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LAG]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LAG]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="offpeak_lagging" size="7" 
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LAG]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LAG]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                    <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                </h:column>
                <h:column id="lead">
                  <x:outputText value="Leading"  
                                  title="The leading value for peak control (upper value for the peak control window)"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="peak_leading" size="7"
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
                                 required="true"
                                    value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_LEAD]}" 
                                    valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                                    <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>
                    <f:verbatim><br/></f:verbatim>
                    <x:inputText forceId="true" id="offpeak_leading" size="7" 
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_LEAD]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                    </x:inputText>


                </h:column>

                          
         </h:panelGrid>
         <h:panelGrid id="pf" columns="2" >
            <h:column>
                <f:verbatim><br/></f:verbatim>
                <x:outputText value="Peak PF Set Pt" styleClass="padlabel"/>
                <x:inputText forceId="true" id="peak_pf_point" size="7" 
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.PEAK_PF_POINT]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
            
            </h:column>
            <h:column>
                <f:verbatim><br/></f:verbatim>
                <x:outputText value="OffPeak PF Set Pt" styleClass="padlabel"/>
                <x:inputText forceId="true" id="offpeak_pf_point" size="7" 
                                 disabled="#{!capControlForm.editingCBCStrategy
                                 || capControlForm.currentStratModel.enableTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
                                 required="true"
                                 value="#{capControlForm.currentStratModel.valueTable[capControlForm.currentStratModel.OFFP_PF_POINT]}" 
                                 valueChangeListener="#{capControlForm.currentStratModel.dataChanged}">
                            <f:validateDoubleRange minimum="-999999" maximum="999999" />
                </x:inputText>
            </h:column>
         </h:panelGrid>
            <f:verbatim></fieldset></f:verbatim> 
			<f:verbatim><br/></f:verbatim>
		    <f:verbatim><fieldset><legend>Peak Operating Days</legend></f:verbatim>
			<x:selectManyCheckbox id="Peak_Operating_Days" value="#{capControlForm.stratDaysOfWeek}"
					disabled="#{!capControlForm.editingCBCStrategy}" layout="pageDirection" >
				<f:selectItems value="#{selLists.daySelections}"/>
			</x:selectManyCheckbox>
			<f:verbatim></fieldset></f:verbatim>

		</h:column>
	
	</h:panelGrid>
		
</f:subview>