<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="alarming" rendered="#{ptEditorForm.visibleTabs['Alarming']}" >
	<h:panelGrid id="alarming_body" columns="2" styleClass="gridLayout" columnClasses="gridColumn, gridColumn" >
		<h:column>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Notification"/></x:htmlTag>
                <x:panelGrid columns="2">
					<x:outputLabel for="Notifcation_Group_List" value="Notifcation Group: "/>
					<x:selectOneMenu id="Notifcation_Group_List" value="#{ptEditorForm.pointBase.pointAlarming.notificationGroupID}" >
						<f:selectItems value="#{ptEditorForm.notifcationGrps}"/>
					</x:selectOneMenu>
				</x:panelGrid>
				
				<h:selectBooleanCheckbox id="Alarm_Acknowledge" value="#{ptEditorForm.pointBase.pointAlarming.notifyOnAck}"/>
				<x:outputLabel for="Alarm_Acknowledge" value="Notify when alarms are Acknowledged"/>
				<x:htmlTag value="br"/>
				<h:selectBooleanCheckbox id="alarmClearCheck"
					value="#{ptEditorForm.pointBase.pointAlarming.notifyOnClear}"/>
				<x:outputLabel for="alarmClearCheck" value="Notify when alarms Clear"/>		
			</x:htmlTag>
		</h:column>
	
		<h:column>
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Alarming"/></x:htmlTag>
				<h:selectBooleanCheckbox id="disableAlarmsCheckBox" onclick="submit();" immediate="true"
                        disabled="#{!capControlForm.editingAuthorized}"
						value="#{ptEditorForm.pointBase.point.alarmsDisabled}"/>
				<x:outputLabel for="disableAlarmsCheckBox" value="Disable All Alarms"/>
				
				<x:htmlTag value="br"/>
			
			    <x:dataTable id="alarmTable" value="#{ptEditorForm.alarmTableEntries}"
			                var="alarmTableEntry"
				            styleClass="fullTable" headerClass="scrollerTableHeader"
				            footerClass="scrollerTableHeader"
				            rowClasses="tableRow,altTableRow">
			        <h:column>
			            <f:facet name="header">
			                <x:outputText value="Condition" />
			            </f:facet>
			            <x:outputText value="#{alarmTableEntry.condition}" />
			        </h:column>
			        <h:column>
			            <f:facet name="header">
			                <x:outputText value="Category" />
			            </f:facet>
						<x:selectOneMenu id="alarmCat" value="#{alarmTableEntry.generate}"
								disabled="#{ptEditorForm.pointBase.point.alarmsDisabled}" >
							<f:selectItems value="#{ptEditorForm.alarmCategories}"/>
						</x:selectOneMenu>
			        </h:column>
			        <h:column>
			            <f:facet name="header">
			                <x:outputText value="Notify" />
			            </f:facet>
						<x:selectOneMenu id="alarmNotif" value="#{alarmTableEntry.excludeNotify}"
								disabled="#{ptEditorForm.pointBase.point.alarmsDisabled}" >
							<f:selectItems value="#{selLists.ptAlarmNotification}"/>
						</x:selectOneMenu>
			        </h:column>
			    </x:dataTable>
			    
			</x:htmlTag>
		</h:column>
	</h:panelGrid>
</f:subview>