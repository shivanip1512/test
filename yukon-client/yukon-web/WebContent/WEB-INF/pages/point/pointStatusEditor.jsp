<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="ptStatusEditor" rendered="#{ptEditorForm.visibleTabs['PointStatus']}" >

	<x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Physical Setup"/></x:htmlTag>

		<x:htmlTag value="br"/>
		<x:outputLabel for="Point_Offset" value="Point Offset: " title="The physical offset value within the current device or parent this point belongs to" />
		<x:inputText id="Point_Offset" value="#{ptEditorForm.pointBase.point.pointOffset}"
			required="true" maxlength="8" styleClass="char8Label" >
				<f:validateLongRange minimum="0" maximum="99999999" />
		</x:inputText>
        <x:outputText id="Point_Offset_Zero" value="(0 = No offset set)" />
		<x:htmlTag value="br"/>

	</x:htmlTag>

	<x:htmlTag value="br"/>
	<x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="Control Settings"/></x:htmlTag>
		<h:panelGrid id="cntrlSettings" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell" >
	
			<h:column>
				<h:selectBooleanCheckbox id="Control_Inhibit" value="#{ptEditorForm.pointBase.pointStatus.controlDisabled}" />
				<x:outputLabel for="Control_Inhibit" value="Control Inhibit" title="Check this box to disble control for this point" />
				
				<x:panelGrid columns="2">
			
					<x:outputLabel for="Control_Type" value="Control Type: " title="Specifies the type of control this point will do" />
					<x:selectOneMenu id="Control_Type" onchange="submit();"
							value="#{ptEditorForm.pointBase.pointStatus.controlType}" >
						<f:selectItems value="#{selLists.ptControlTypes}" />
					</x:selectOneMenu>
			
					<x:outputLabel for="Control_Pt_Offset" value="Control Pt. Offset: "
							title="Specifies the physical location used for wiring the relay point" />
					<x:inputText id="Control_Pt_Offset" required="true" 
							disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
							maxlength="8" styleClass="char8Label"
							value="#{ptEditorForm.pointBase.pointStatus.controlOffset}" >
						<f:validateDoubleRange minimum="-99999999" maximum="99999999" />
					</x:inputText>
			
					<x:outputLabel for="Close_Time_1" value="Close Time 1: " title="Specify how long each relay stays energized" />
					
					<x:panelGroup>
						<x:inputText id="Close_Time_1" required="true" 
								maxlength="8" styleClass="char8Label"
								disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
								value="#{ptEditorForm.pointBase.pointStatus.closeTime1}" >
							<f:validateDoubleRange minimum="0" maximum="99999" />
						</x:inputText>
						<x:outputText id="time1Millis" value="(millis.)" />
                    </x:panelGroup>
                    
					<x:outputLabel for="Close_Time_2" value="Close Time 2: " title="Specify how long each relay stays energized" />
					
					<x:panelGroup>
						<x:inputText id="Close_Time_2" required="true" 
								maxlength="8" styleClass="char8Label"
								disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
								value="#{ptEditorForm.pointBase.pointStatus.closeTime2}" >
							<f:validateDoubleRange minimum="0" maximum="99999" />
						</x:inputText>
						<x:outputText id="time2Millis" value="(millis.)" />
					</x:panelGroup>
					
					<x:outputLabel for="Command_Timeout" value="Command Timeout: "
							title="The length of time to use to scan for a state change following control. An alarm is raised if a state change is not detected." />
					
					<x:panelGroup>
						<x:inputText id="Command_Timeout" required="true" 
								maxlength="8" styleClass="char8Label"
								disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
								value="#{ptEditorForm.pointBase.pointStatus.commandTimeOut}" >
							<f:validateDoubleRange minimum="0" maximum="9999999" />
						</x:inputText>
						<x:outputText id="cmdTimeSecs" value="(secs.)" />
                    </x:panelGroup>
				</x:panelGrid>
	
			</h:column>
	
			<h:column>
                <x:panelGrid columns="2">
					<x:outputLabel for="Open_Command" value="Open Command String: " title="The OPEN command string sent out when Yukon controls this point" />
					<x:inputText id="Open_Command"
						disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
						value="#{ptEditorForm.pointBase.pointStatus.stateZeroControl}" 
						required="true" maxlength="100" styleClass="char64Label" />
			
					<x:outputLabel for="Close_Command" value="Close Command String: " title="The CLOSE command string sent out when Yukon controls this point" />
					<x:inputText id="Close_Command"
						disabled="#{!ptEditorForm.pointStatusEntry.controlAvailable}"
						value="#{ptEditorForm.pointBase.pointStatus.stateOneControl}" 
						required="true" maxlength="100" styleClass="char64Label" />
                </x:panelGrid>
			</h:column>
		</h:panelGrid>
	</x:htmlTag>
</f:subview>