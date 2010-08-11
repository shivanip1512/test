<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<f:subview id="outerView">
	<x:panelGrid id="subStrategyBody" columns="2" styleClass="gridLayout" columnClasses="gridCell, gridCell">
		<h:column>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Season Strategy"/></x:htmlTag> 
            
                <h:outputLabel for="Season_Schedule_Selection" value="Season Schedule:"/>
                <x:selectOneMenu id="Season_Schedule_Selection" onchange="submit();"
				    disabled="#{capControlForm.editingCBCStrategy}"
				    value="#{capControlForm.scheduleId}"
				    valueChangeListener="#{capControlForm.newScheduleSelected}">
                    <f:selectItems value="#{capControlForm.cbcSchedules}" />
                </x:selectOneMenu>
			
                <x:htmlTag value="br"/>
                <x:htmlTag value="br"/>
			
                <h:dataTable id="seasons" value="#{capControlForm.seasonsForSchedule}" var="season"
                    columnClasses="scroller"
                    headerClass="tableHeader scroller">
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Season Name" />
                        </f:facet>
                        <h:outputText value="#{season.seasonName}" />
                    </h:column>
                    
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="Season Strategy" />
                        </f:facet>
                        <x:selectOneMenu id="Strategy_Selection" onchange="submit();"
                            disabled="#{capControlForm.editingCBCStrategy}"
                            value="#{capControlForm.assignedStratMap[season]}">

                            <f:selectItems value="#{capControlForm.cbcStrategies}" />
                        </x:selectOneMenu>
                    </h:column>
                    
                    <h:column>
                        <f:facet name="header">
                            <h:outputText value="" />
                        </f:facet>
                        <x:commandLink action="#{capControlForm.dataModel.createEditorLink}" value="Edit" 
                            rendered="#{capControlForm.editingAuthorized && capControlForm.assignedStratMap[season] > -1}"
                            actionListener="#{capControlForm.setTab}"
                            title="Click here to edit this strategy.">
                            <f:param name="type" value="5"/>
                            <f:param name="itemid" value="#{capControlForm.assignedStratMap[season]}" />
                            <f:param name="tabId" value="7"/>
                        </x:commandLink>
                    </h:column>
                </h:dataTable>
			</x:htmlTag>
			
        </h:column>
        
		<h:column>
		
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Holiday Strategy"/></x:htmlTag>
                
                <h:outputLabel for="Holiday_Schedule_Selection" value="Holiday Schedule:"/>
                <x:selectOneMenu id="Holiday_Schedule_Selection" onchange="submit();"
				    disabled="#{capControlForm.editingCBCStrategy}"
				    value="#{capControlForm.holidayScheduleId}"
                    valueChangeListener="#{capControlForm.newHolidayScheduleSelected}">
                    <f:selectItems value="#{capControlForm.cbcHolidaySchedules}" />
                </x:selectOneMenu> 
                
                <x:htmlTag value="br"/>
                <x:htmlTag value="br"/>
                
                <x:panelGrid columns="3">
                
	                <h:outputLabel for="Holiday_Strategy_Selection" value="Strategy:" rendered="#{capControlForm.holidayScheduleId > 0}"/>
	                <x:selectOneMenu id="Holiday_Strategy_Selection"
					    onchange="submit();"
					    disabled="#{capControlForm.editingCBCStrategy}"
					    value="#{capControlForm.holidayStrategyId}"
					    rendered="#{capControlForm.holidayScheduleId > 0}">
					    <f:selectItems value="#{capControlForm.cbcHolidayStrategies}" />
	                </x:selectOneMenu> 
	                
	                <x:commandLink action="#{capControlForm.dataModel.createEditorLink}" value="Edit"
					    	rendered="#{capControlForm.holidayScheduleId > 0 && capControlForm.editingAuthorized}"
	                		actionListener="#{capControlForm.setTab}"
	                	    title="Click here to edit this strategy.">
					    <f:param name="type" value="5" />
					    <f:param name="itemid" value="#{capControlForm.holidayStrategyId}" />
					    <f:param name="tabId" value="7"/>
	                </x:commandLink>
                
                </x:panelGrid>
                
            </x:htmlTag>
		</h:column>
	</x:panelGrid>
</f:subview>