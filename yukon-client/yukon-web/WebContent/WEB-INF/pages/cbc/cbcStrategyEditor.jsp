<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>

<f:subview id="cbcStrategy" rendered="#{capControlForm.visibleTabs['CBCSubstation'] || capControlForm.visibleTabs['CBCFeeder']}" >

    <f:subview id="paoSubBus" rendered="#{capControlForm.visibleTabs['CBCSubstation']}" >
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="subStrat" value="Selected Strategy: " title="The current control strategy we are using"/>
		<x:selectOneMenu id="subStrat" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlSubstationBus.strategyID}" >
			<f:selectItems value="#{capControlForm.cbcStrategies}"/>
		</x:selectOneMenu>
    </f:subview>


    <f:subview id="paoFeeder" rendered="#{capControlForm.visibleTabs['CBCFeeder']}" >
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="fdrStrat" value="Selected Strategy: " title="The current control strategy we are using"/>
		<x:selectOneMenu id="fdrStrat" onchange="submit();" disabled="#{capControlForm.editingCBCStrategy}"
				value="#{capControlForm.PAOBase.capControlFeeder.strategyID}" >
			<f:selectItems value="#{capControlForm.cbcStrategies}"/>
		</x:selectOneMenu>
    </f:subview>


	<f:verbatim><br/></f:verbatim>
	<h:selectBooleanCheckbox id="editStrat" onclick="submit();"
		value="#{capControlForm.editingCBCStrategy}"
		disabled="#{capControlForm.currentStrategyID == 0}" />
	<x:outputLabel for="editStrat" value="Edit Strategy" title="A toggle to edit the selected strategy"/>
	<x:outputText id="stratNameWarn" styleClass="alert"
		rendered="#{capControlForm.editingCBCStrategy}" value="  (WARNING: Modifying this strategy will affect all feeders or subs that use this strategy)"/>
	<f:verbatim><br/></f:verbatim>
	<x:commandButton id="createStrat" value="New Strategy" title="Create a new strategy"
			action="#{capControlForm.createStrategy}" styleClass="stdButton" />
	<x:commandButton id="deleteStrat" value="Delete Strategy" title="Delete the selected strategy" styleClass="stdButton"
			action="#{capControlForm.deleteStrategy}" onclick="return window.confirm('Are you sure you want to delete this strategy?\r\nNote: Deleting this strategy will force all data to be saved and the current strategy will be set to (none).');"
			disabled="#{capControlForm.currentStrategyID == 0}" />


	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn" >
		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
		    <f:verbatim><br/><fieldset><legend>Strategy Detail</legend></f:verbatim>
			<x:outputLabel for="StrategyName" value="Strategy Name: " title="A label for the strategy in the system"/>
			<x:inputText id="StrategyName" disabled="#{!capControlForm.editingCBCStrategy}" required="true" maxlength="32"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].strategyName}" />
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="CntrlMeth" value="Control Method: " title="How the CapBanks are to be controlled"/>
			<x:selectOneMenu id="CntrlMeth" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlMethod}" >
				<f:selectItems value="#{selLists.cbcControlMethods}"/>
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="CntrlInterv" value="Analysis Interval: " title="How often the system should check to determine the need for control"/>
			<x:selectOneMenu id="CntrlInterv" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlInterval}" >
				<f:selectItem itemLabel="(On New Data Only)" itemValue="0"/>
				<f:selectItems value="#{capControlForm.timeInterval}"/>
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="MaxConfTime" value="Max Confirm Time: " title="How much time the system waits until the control is considered successful"/>
			<x:selectOneMenu id="MaxConfTime" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minResponseTime}" >
				<f:selectItem itemLabel="(none)" itemValue="0"/>
				<f:selectItems value="#{capControlForm.timeInterval}"/>				
			</x:selectOneMenu>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="PassPerc" value="Pass Percent: " title="This amount of change or higher is considered to be a successful control"/>
			<x:inputText id="PassPerc" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].minConfirmPercent}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
			<x:outputText id="PassPercLab" value="%"/>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="FailPerc" value="Failure Percent: " title="This amount of change or lower is considered to be a failed control"/>
			<x:inputText id="FailPerc" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].failurePercent}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
			<x:outputText id="FailPercLab" value="%"/>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="SendRetries" value="Send Retries: " title="How many times the control should be repeatedly sent out to the field"/>
			<x:inputText id="SendRetries" styleClass="percentLabel" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlSendRetries}">
					<f:validateLongRange minimum="0" maximum="100" />
			</x:inputText>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="DelayTime" value="Delay Time: " title="How much time we should wait before sending the control command into the field"/>
			<x:selectOneMenu id="DelayTime" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlDelayTime}" >
				<f:selectItem itemValue="0" itemLabel="(none)" />
				<f:selectItems value="#{capControlForm.timeInterval}" />
			</x:selectOneMenu>
	
	
			<f:verbatim></fieldset></f:verbatim>
	
	
		    <f:verbatim><br/><fieldset><legend>Strategy Operations</legend></f:verbatim>
			<x:outputLabel for="MaxDailyOps" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
			<x:inputText id="MaxDailyOps" styleClass="char16Label" required="true"
					disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxDailyOperation}" >
					<f:validateLongRange minimum="0" maximum="9999" />
			</x:inputText>
			<x:outputText id="MaxDailyOpsDesc" value="(0 = unlimited)"/>
	
			<f:verbatim><br/></f:verbatim>
			<h:selectBooleanCheckbox id="DisabledOps" value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].maxOperationDisabled}"
					disabled="#{!capControlForm.editingCBCStrategy}" />
			<x:outputLabel for="DisabledOps" value="Disable upon reaching max operations" title="Should we be automatically disabled after reaching our max op counts"/>
			<f:verbatim></fieldset></f:verbatim>
		</h:column>

		<h:column rendered="#{capControlForm.currentStrategyID != 0}" >
		    <f:verbatim><fieldset><legend>Strategy Peaks</legend></f:verbatim>
			<x:outputLabel for="CntrlAlg" value="Control Algorithim: " title="The units and process we use to make control decisions"/>
			<x:selectOneMenu id="CntrlAlg" onchange="submit();" disabled="#{!capControlForm.editingCBCStrategy}"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].controlUnits}" >
				<f:selectItems value="#{selLists.cbcControlAlgorithim}"/>
			</x:selectOneMenu>


			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="PkLag" value="Peak Lagging: " title="The lagging value for peak control (upper value for the peak control window)" />
			<x:inputText id="PkLag" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakLag}" >
					<f:validateDoubleRange minimum="-999999" maximum="999999" />
			</x:inputText>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="PkLead" value="Peak Leading: " title="The leading value for peak control (lower value for the peak control window)" />
			<x:inputText id="PkLead" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakLead}" >
					<f:validateDoubleRange minimum="-999999" maximum="999999" />
			</x:inputText>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="PkStart" value="Peak Start Time: " title="Starting time for the peak time window" />
			<x:inputText id="PkStart" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
					required="true" converter="cti_TimeConverter"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStartTime}" >
			</x:inputText>
			<x:outputText id="PkStartLab" value="(HH:mm)"/>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="PkStop" value="Peak Stop Time: " title="Stop time for the peak time window" />
			<x:inputText id="PkStop" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}"
					required="true" converter="cti_TimeConverter"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].peakStopTime}" >
			</x:inputText>
			<x:outputText id="PkStopLab" value="(HH:mm)"/>
			
	
			<f:verbatim><br/><br/></f:verbatim>
			<x:outputLabel for="OffPkLag" value="Off Peak Lagging: " title="The lagging value for off peak control (upper value for the off peak control window)" />
			<x:inputText id="OffPkLag" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].offPkLag}" >
					<f:validateDoubleRange minimum="-999999" maximum="999999" />
			</x:inputText>
	
			<f:verbatim><br/></f:verbatim>
			<x:outputLabel for="OffPkLead" value="Off Peak Leading: " title="The leading value for off peak control (lower value for the off peak control window)" />
			<x:inputText id="OffPkLead" styleClass="char16Label" disabled="#{!capControlForm.editingCBCStrategy}" required="true"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].offPkLead}" >
					<f:validateDoubleRange minimum="-999999" maximum="999999" />
			</x:inputText>
			<f:verbatim></fieldset></f:verbatim>


			<f:verbatim><br/></f:verbatim>
		    <f:verbatim><fieldset><legend>Peak Operating Days</legend></f:verbatim>
			<h:selectManyCheckbox id="pkOpDays" value="#{capControlForm.stratDaysOfWeek}"
					disabled="#{!capControlForm.editingCBCStrategy}" layout="pageDirection" >
				<f:selectItem itemLabel="Sunday" itemValue="0" />
				<f:selectItem itemLabel="Monday" itemValue="1" />
				<f:selectItem itemLabel="Tuesday" itemValue="2" />
				<f:selectItem itemLabel="Wednesday" itemValue="3" />
				<f:selectItem itemLabel="Thursday" itemValue="4" />
				<f:selectItem itemLabel="Friday" itemValue="5" />
				<f:selectItem itemLabel="Saturday" itemValue="6" />
			</h:selectManyCheckbox>
			<f:verbatim></fieldset></f:verbatim>

		</h:column>
	
	</h:panelGrid>
		
    


</f:subview>