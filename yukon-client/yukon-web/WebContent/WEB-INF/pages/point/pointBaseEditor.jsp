<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="general" rendered="#{ptEditorForm.visibleTabs['General']}" >
	<h:panelGrid id="body" columns="2" styleClass="gridLayout" columnClasses="gridColumn, gridColumn" >
		<h:column>
	        <x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="General"/></x:htmlTag>
		        <x:panelGrid columns="2">
	
					<x:outputLabel for="Point_Type" value="Point Type: "/>
			        <x:outputText id="Point_Type" value="#{ptEditorForm.pointBase.point.pointType} (id: #{ptEditorForm.pointBase.point.pointID})" styleClass="staticLabel"/>
			
					<x:outputLabel for="Parent" value="Parent: "/>
			        <x:outputText  id = "Parent" value="#{databaseCache.allPaosMap[ptEditorForm.pointBase.point.paoID]} (id: #{ptEditorForm.pointBase.point.paoID})" styleClass="staticLabel"/>
					
					<x:outputLabel for="Point_Name" value="Point Name: "/>
					<x:inputText id="Point_Name" value="#{ptEditorForm.pointBase.point.pointName}" required="true" maxlength="60" styleClass="char32Label" />
				
					<x:outputLabel for="Logical_Group" value="Timing Group: "/>
					<x:selectOneMenu id="Logical_Group" value="#{ptEditorForm.pointBase.point.logicalGroup}" >
						<f:selectItems value="#{ptEditorForm.logicalGroups}"/>
					</x:selectOneMenu>
			
				</x:panelGrid>
				
				<h:selectBooleanCheckbox id="Out_Of_Service" value="#{ptEditorForm.pointBase.point.outOfService}"/>
				<x:outputLabel for="Out_Of_Service" value="Disable Point"/>
				
	        </x:htmlTag>
		</h:column>
		
		<h:column >
		<f:subview id="archive_point_analog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
			<x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="Archive"/></x:htmlTag>
	            <x:panelGrid columns="2">
					<x:outputLabel for="Archive_Type" value="Archive Data: "/>
					<x:selectOneMenu id="Archive_Type" value="#{ptEditorForm.pointBase.point.archiveType}"
                            converter="archiveTypeConverter"
                            disabled="#{!capControlForm.editingAuthorized}"
							onchange="submit();" valueChangeListener="#{ptEditorForm.archiveTypeChanged}">
						<f:selectItems value="#{selLists.ptArchiveType}"/>
					</x:selectOneMenu>
				
					<x:outputLabel for="Archive_Interval" value="Interval: "/>
					<x:selectOneMenu id="Archive_Interval" value="#{ptEditorForm.pointBase.point.archiveInterval}" disabled="#{!ptEditorForm.archiveInterEnabled}">
						<f:selectItems value="#{ptEditorForm.archiveInterval}"/>
					</x:selectOneMenu>
				</x:panelGrid>
			</x:htmlTag>
		</f:subview>
		
		<f:subview id="archive_point_status" rendered="#{ptEditorForm.visibleTabs['PointStatus']}" >
	        <x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="Archive"/></x:htmlTag>
	            <x:outputLabel for="archive_checkbox" value="Archive: "/>
	            <x:selectBooleanCheckbox id="archive_checkbox" value="#{ptEditorForm.pointBase.point.archiveStatusData}"/>
			</x:htmlTag>
		</f:subview>	
		
	
	    <f:subview id="pointAnalog" rendered="#{ptEditorForm.visibleTabs['PointAnalog'] || ptEditorForm.visibleTabs['PointAccum'] || ptEditorForm.visibleTabs['PointCalc']}" >
		    <x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="Analog Summary"/></x:htmlTag>
	            <x:panelGrid columns="2">
					<x:outputLabel for="Unit_Of_Measure" value="Unit of Measure: "/>
					<x:selectOneMenu id="Unit_Of_Measure" value="#{ptEditorForm.pointBase.pointUnit.uomID}" >
						<f:selectItems value="#{ptEditorForm.uofMs}"/>
					</x:selectOneMenu>        
			
					<x:outputLabel for="Decimal_Digits" value="Decimal Digits: "/>
					<x:selectOneMenu id="Decimal_Digits" value="#{ptEditorForm.pointBase.pointUnit.decimalPlaces}" >
						<f:selectItems value="#{ptEditorForm.decimalDigits}"/>
					</x:selectOneMenu>
					
					<x:outputLabel for="stateGroups" value="State Group: "/>
                    <x:selectOneMenu id="stateGroups" value="#{ptEditorForm.pointBase.point.stateGroupID}"
                            disabled="#{!capControlForm.editingAuthorized}"
                            onchange="submit();" valueChangeListener="#{ptEditorForm.stateGroupChanged}">
                        <f:selectItems value="#{ptEditorForm.stateGroups}"/>
                    </x:selectOneMenu>
				</x:panelGrid>
			</x:htmlTag>
	    </f:subview>
	    
	
	    <f:subview id="pointStatus" rendered="#{ptEditorForm.visibleTabs['PointStatus'] || ptEditorForm.visibleTabs['PointCalcStatus']}" >
			<x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="Status Summary"/></x:htmlTag>
	            <x:panelGrid columns="2">
					<x:outputLabel for="stateGroups" value="State Group: "/>
					<x:selectOneMenu id="stateGroups" value="#{ptEditorForm.pointBase.point.stateGroupID}"
                            disabled="#{!capControlForm.editingAuthorized}"
							onchange="submit();" valueChangeListener="#{ptEditorForm.stateGroupChanged}">
						<f:selectItems value="#{ptEditorForm.stateGroups}"/>
					</x:selectOneMenu>
			
					<x:outputLabel for="initialState" value="Initial State: "/>
					<x:selectOneMenu id="initialState" value="#{ptEditorForm.pointBase.pointStatus.initialState}" >
						<f:selectItems value="#{ptEditorForm.initialStates}"/>
					</x:selectOneMenu>
				</x:panelGrid>
			</x:htmlTag>
	    </f:subview>
	
	    
	    <f:subview id="pointCalc" rendered="#{ptEditorForm.visibleTabs['PointCalcStatus'] || ptEditorForm.visibleTabs['PointCalc']}" >
			<x:htmlTag value="fieldset" styleClass="fieldSet">
	            <x:htmlTag value="legend"><x:outputText value="Calc Point Summary"/></x:htmlTag>
	            <x:panelGrid columns="2">
					<x:outputLabel for="updateType" value="Update Type: "/>
					<x:selectOneMenu id="updateType" value="#{ptEditorForm.pointBase.calcBase.updateType}"
                            disabled="#{!capControlForm.editingAuthorized}"
							onchange="submit();" valueChangeListener="#{ptEditorForm.updateTypeChanged}" >
						<f:selectItems value="#{selLists.ptUpdateType}"/>
					</x:selectOneMenu>
			
					<x:outputLabel for="calcStatRate" value="Update Rate: "/>
					<x:selectOneMenu id="calcStatRate" value="#{ptEditorForm.pointBase.calcBase.periodicRate}" disabled="#{!ptEditorForm.calcRateEnabled}" >
						<f:selectItems value="#{ptEditorForm.archiveInterval}"/>
					</x:selectOneMenu>
				</x:panelGrid>
			</x:htmlTag>
	    </f:subview>
	</h:column>
</h:panelGrid>
</f:subview>