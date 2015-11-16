<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="ptAnalogEditor" rendered="#{ptEditorForm.visibleTabs['PointAnalog']}" >

	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell" >
	
		<h:column>
		
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Physical Setup"/></x:htmlTag>
			
                <x:panelGrid columns="2">
					<x:outputLabel for="Point_Offset" value="Point Offset: " title="The physical offset value within the current device or parent this point belongs to" />
					
					<x:panelGroup>
						<x:inputText id="Point_Offset" value="#{ptEditorForm.pointBase.point.pointOffset}"
							required="true" maxlength="8" styleClass="char8Label" >
								<f:validateLongRange minimum="0" maximum="99999999" />
						</x:inputText>
						
				        <x:outputText id="Point_Offset_Zero" value="(0 = No offset set)" />
			        
			        </x:panelGroup>
			
					<x:outputLabel for="Deadband" value="Deadband: " title="The amount the value of this point must deviate before the point is read and updated" />
					
					<x:panelGroup>
						<x:inputText id="Deadband" value="#{ptEditorForm.pointBase.pointAnalog.deadband}"
							required="true" maxlength="8" styleClass="char8Label" >
								<f:validateDoubleRange minimum="-1" maximum="99999999" />
						</x:inputText>
				        
				        <x:outputText id="Deadband_Zero" value="(0 = No deadband set)" />
	                </x:panelGroup>
	                
					<x:outputLabel for="Multiplier" value="Multiplier: " title="A value that is always applied to the raw reading of this point" />
					
					<x:inputText id="Multiplier" value="#{ptEditorForm.pointBase.pointAnalog.multiplier}"
						required="true" maxlength="16" styleClass="char16Label" >
							<f:validateDoubleRange minimum="-99999999" maximum="99999999" />
					</x:inputText>
			
					<x:outputLabel for="Data_Offset" value="Data Offset: " title="A value that is added to the raw reading when making calculations" />
					
					<x:inputText id="Data_Offset" value="#{ptEditorForm.pointBase.pointAnalog.dataOffset}"
						required="true" maxlength="16" styleClass="char16Label" >
							<f:validateDoubleRange minimum="-99999999" maximum="99999999" />
					</x:inputText>
				</x:panelGrid>
		
			</x:htmlTag>
			
            <x:htmlTag value="br"/>

            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Control Settings"/></x:htmlTag>

                <h:selectBooleanCheckbox id="Control_Inhibit" 
                        disabled="#{!ptEditorForm.pointAnalogControlEntry.controlAvailable}"
                        value="#{ptEditorForm.pointBase.pointAnalogControl.controlInhibited}" />
                <x:outputLabel for="Control_Inhibit" value="Control Inhibit" 
                        title="Check this box to disble control for this point" />
                
                <x:panelGrid columns="2">
            
                    <x:outputLabel for="Control_Type" value="Control Type: " 
                            title="Specifies the type of control this point will do" />
                    <x:selectOneMenu id="Control_Type" onchange="submit();"
                            disabled="#{!capControlForm.editingAuthorized}"
                            value="#{ptEditorForm.pointBase.pointAnalogControl.controlType}" >
                        <f:selectItems value="#{selLists.ptAnalogControlTypes}" />
                    </x:selectOneMenu>
            
                    <x:outputLabel for="Control_Pt_Offset" value="Control Pt. Offset: "
                            title="Specifies the physical location used for wiring the relay point" />
                    <x:inputText id="Control_Pt_Offset" required="#{ptEditorForm.pointAnalogControlEntry.controlAvailable}" 
                            disabled="#{!ptEditorForm.pointAnalogControlEntry.controlAvailable}"
                            maxlength="8" styleClass="char8Label"
                            value="#{ptEditorForm.pointBase.pointAnalogControl.controlOffset}" >
                        <f:validateLongRange minimum="-99999999" maximum="99999999" />
                    </x:inputText>
                </x:panelGrid>
            </x:htmlTag>

			<x:htmlTag value="br"/>
			
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Stale Data"/></x:htmlTag>
                
                <h:selectBooleanCheckbox id="enableStaleData" onclick="submit();"
                            disabled="#{!capControlForm.editingAuthorized}"
                            valueChangeListener="#{ptEditorForm.staleData.enableClick}"
                            value="#{ptEditorForm.staleData.enabled}"
                            immediate="true" />
                            
                <x:outputLabel for="enableStaleData" value="Enable" title="Turn on/off stale data checking." />
                
                <x:htmlTag value="br"/>
                <x:panelGrid columns="2">
                    <x:outputLabel for="staleDataTime" value="Time in Minutes:"/>
                    <x:inputText id="staleDataTime" value="#{ptEditorForm.staleData.time}" disabled="#{!ptEditorForm.staleData.enabled}">
                        <f:validateLongRange minimum="0" maximum="99999999" />
                    </x:inputText>
                    
                    <x:outputLabel for="staleDataUpdateStyle" value="Update Style:"/>
                    <x:selectOneMenu id="staleDataUpdateStyle" value="#{ptEditorForm.staleData.updateStyle}" disabled="#{!ptEditorForm.staleData.enabled || !capControlForm.editingAuthorized}">
			            <f:selectItems value="#{ptEditorForm.staleData.updateStyles}"/>
			        </x:selectOneMenu>
                </x:panelGrid>
            </x:htmlTag>
			
		</h:column>
	
	    <h:column>
	    
	        <x:htmlTag value="fieldset" styleClass="fieldSet">
	        
		        <x:htmlTag value="legend"><x:outputText value="Limits"/></x:htmlTag>
	            
		        <h:panelGrid id="limitGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn, gridColumn" >
		            
		            <h:column>
		            
				        <h:selectBooleanCheckbox id="Limit_One" onclick="submit();"
                            disabled="#{!capControlForm.editingAuthorized}"
							valueChangeListener="#{ptEditorForm.pointLimitEntry.showLimit}"
							value="#{ptEditorForm.pointLimitEntry.editingLimitOne}"
							immediate="true" />
						
						<x:outputLabel for="Limit_One" value="Limit 1" title="The first limit that can be set for this point, used to determine if an alarm condition is active" />
				
                        <x:panelGrid columns="2">
							<x:outputLabel for="Limit_One_High" value="High: " title="The upper value for this limit (used for an alarming condition)"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}" />
								
							<x:inputText id="Limit_One_High"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}"
								value="#{ptEditorForm.pointBase.limitOne.highLimit}" 
								required="true" maxlength="16" styleClass="char16Label" >
                                <f:validateDoubleRange minimum="-99999999" maximum="99999999" />
							</x:inputText>
					
							<x:outputLabel for="Limit_One_Low" value="Low: " title="The lower value for this limit (used for an alarming condition)"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}" />
								
							<x:inputText id="Limit_One_Low"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}"
								value="#{ptEditorForm.pointBase.limitOne.lowLimit}" 
								required="true" maxlength="16" styleClass="char16Label" >
                                <f:validateDoubleRange minimum="-99999999" maximum="99999999" />
							</x:inputText>
					
							<x:outputLabel for="Limit_One_Duration" value="Duration: " title="The number of seconds the limit must be violated before an alarm is generated"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}" />
								
	                        <x:panelGroup>
								<x:inputText id="Limit_One_Duration"
									rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}"
									value="#{ptEditorForm.pointBase.limitOne.limitDuration}" 
									required="true" maxlength="8" styleClass="char8Label" >
                                    <f:validateLongRange minimum="0" maximum="99999999" />
								</x:inputText>
								
								<x:outputText id="Limit_One_Secs" value="secs." rendered="#{ptEditorForm.pointLimitEntry.editingLimitOne}" />
							</x:panelGroup>
						
						</x:panelGrid>
		        
		            </h:column>
		
					<h:column>
					
						<h:selectBooleanCheckbox id="Limit_Two" onclick="submit();"
                            disabled="#{!capControlForm.editingAuthorized}"
							valueChangeListener="#{ptEditorForm.pointLimitEntry.showLimit}"
							value="#{ptEditorForm.pointLimitEntry.editingLimitTwo}"
							immediate="true" />
							
						<x:outputLabel for="Limit_Two" value="Limit 2" title="The second limit that can be set for this point, used to determine if an alarm condition is active" />
				
						<x:panelGrid columns="2">
						
							<x:outputLabel for="Limit_Two_High" value="High2: " title="The upper value for this limit (used for an alarming condition)"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}" />
								
							<x:inputText id="Limit_Two_High"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}"
								value="#{ptEditorForm.pointBase.limitTwo.highLimit}" 
								required="true" maxlength="16" styleClass="char16Label" >
									<f:validateDoubleRange minimum="-99999999" maximum="99999999" />
							</x:inputText>
					
							<x:outputLabel for="Limit_Two_Low" value="Low2: " title="The lower value for this limit (used for an alarming condition)"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}" />
								
							<x:inputText id="Limit_Two_Low"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}"
								value="#{ptEditorForm.pointBase.limitTwo.lowLimit}" 
								required="true" maxlength="16" styleClass="char16Label" >
									<f:validateDoubleRange minimum="-99999999" maximum="99999999" />
							</x:inputText>
					
							<x:outputLabel for="Limit_Two_Duration" value="Duration2: " title="The number of seconds the limit must be violated before an alarm is generated"
								rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}" />
	                        
	                        <x:panelGroup>
								<x:inputText id="Limit_Two_Duration"
									rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}"
									value="#{ptEditorForm.pointBase.limitTwo.limitDuration}" 
									required="true" maxlength="8" styleClass="char8Label" >
										<f:validateLongRange minimum="0" maximum="99999999" />
								</x:inputText>
								
								<x:outputText id="Limit_Two_Secs" value="secs." rendered="#{ptEditorForm.pointLimitEntry.editingLimitTwo}" />
							</x:panelGroup>
						</x:panelGrid>
						
					</h:column>
		
		        </h:panelGrid>
			
		        <x:panelGrid columns="2">
			        <x:panelGroup>
				        <h:selectBooleanCheckbox id="Reasonability_High" onclick="submit();"
                          disabled="#{!capControlForm.editingAuthorized}"
						  valueChangeListener="#{ptEditorForm.pointLimitEntry.showReasonabilityLimit}"
						  value="#{ptEditorForm.pointLimitEntry.highReasonabilityValid}" />
						  
				        <x:outputLabel for="Reasonability_High" value="High Reasonability: " title="All readings exceeding this value are ignored." />
			        
			        </x:panelGroup>
			        
			        <x:inputText id="Reasonability_High_Limit"
					  value="#{ptEditorForm.pointBase.pointUnit.highReasonabilityLimit}"
					  rendered="#{ptEditorForm.pointLimitEntry.highReasonabilityValid}"
					  maxlength="16" styleClass="char16Label" />
					  
			        <x:inputText id="Reasonability_High_None" readonly="true" disabled="true"
					  value="(no limit set)" maxlength="16" styleClass="char16Label"
					  rendered="#{!ptEditorForm.pointLimitEntry.highReasonabilityValid}" />
	                
	                <x:panelGroup>
				        <h:selectBooleanCheckbox id="Reasonability_Low" onclick="submit();"
                          disabled="#{!capControlForm.editingAuthorized}"
						  valueChangeListener="#{ptEditorForm.pointLimitEntry.showReasonabilityLimit}"
						  value="#{ptEditorForm.pointLimitEntry.lowReasonabilityValid}" />
						  
				        <x:outputLabel for="Reasonability_Low" value="Low Reasonability: " title="All readings less than this value are ignored." />
			        </x:panelGroup>
			        <x:inputText id="Reasonability_Low_Limit"
					  value="#{ptEditorForm.pointBase.pointUnit.lowReasonabilityLimit}"
					  rendered="#{ptEditorForm.pointLimitEntry.lowReasonabilityValid}"
					  maxlength="16" styleClass="char16Label" />
					  
			        <x:inputText id="Reasonability_Low_None" readonly="true" disabled="true"
					  value="(no limit set)" maxlength="16" styleClass="char16Label"
					  rendered="#{!ptEditorForm.pointLimitEntry.lowReasonabilityValid}" />
				  
                </x:panelGrid>
	
	        </x:htmlTag>
	        
	    </h:column>
	
	</h:panelGrid>

</f:subview>