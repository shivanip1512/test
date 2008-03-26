<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>


<f:subview id="alarming" rendered="#{ptEditorForm.visibleTabs['Alarming']}" >

	<h:panelGrid id="alarming_body" columns="2" styleClass="gridLayout" columnClasses="gridColumn, gridColumn" >

	<h:column>
		<f:verbatim><fieldset class="fieldSet"><legend>Notifcation</legend></f:verbatim>
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Notifcation_Group_List" value="Notifcation Group: "/>
		<x:selectOneMenu id="Notifcation_Group_List" value="#{ptEditorForm.pointBase.pointAlarming.notificationGroupID}" >
			<f:selectItems value="#{ptEditorForm.notifcationGrps}"/>
		</x:selectOneMenu>
	
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="Contact_List" value="Contact: "/>
		<x:selectOneMenu id="Contact_List" value="#{ptEditorForm.pointBase.pointAlarming.recipientID}" >
			<f:selectItems value="#{ptEditorForm.emailNotifcations}"/>
		</x:selectOneMenu>
		
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="Alarm_Acknowledge"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnAck}"/>
		<x:outputLabel for="Alarm_Acknowledge" value="Notify when alarms are Acknowledged"/>
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="alarmClearCheck"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnClear}"/>
		<x:outputLabel for="alarmClearCheck" value="Notify when alarms Clear"/>		
		<f:verbatim></fieldset></f:verbatim>
	</h:column>


	<h:column>
		<f:verbatim><fieldset class="fieldSet"><legend>Alarming</legend></f:verbatim>
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="disableAlarmsCheckBox" onclick="submit();"
				immediate="true"
				value="#{ptEditorForm.pointBase.point.alarmsDisabled}"/>
		<x:outputLabel for="disableAlarmsCheckBox" value="Disable All Alarms"/>	
	
	    <f:verbatim><br/></f:verbatim>
	    <x:dataTable id="alarmTable" value="#{ptEditorForm.alarmTableEntries}"
	                var="alarmTableEntry"
		            styleClass="fullTable" headerClass="scrollerTableHeader"
		            footerClass="scrollerTableHeader"
		            rowClasses="tableRow,altTableRow" 
	                >
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
		<f:verbatim></fieldset></f:verbatim>
	</h:column>
	</h:panelGrid>
</f:subview>