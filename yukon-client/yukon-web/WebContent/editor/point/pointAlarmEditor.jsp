<%@ page import="com.cannontech.database.data.point.PointBase" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>


<f:subview id="alarming" rendered="#{ptEditorForm.visibleTabs['Alarming']}" >

	<f:verbatim><fieldset><legend>Notifcation</legend></f:verbatim>
		<x:outputLabel for="notifGroupList" value="Notifcation Group: "/>
		<x:selectOneMenu id="notifGroupList" value="#{ptEditorForm.pointBase.pointAlarming.notificationGroupID}" >
			<f:selectItems value="#{ptEditorForm.notifcationGrps}"/>
		</x:selectOneMenu>
	
		<f:verbatim><br/></f:verbatim>
		<x:outputLabel for="contactList" value="Contact: "/>
		<x:selectOneMenu id="contactList" value="#{ptEditorForm.pointBase.pointAlarming.recipientID}" >
			<f:selectItems value="#{ptEditorForm.emailNotifcations}"/>
		</x:selectOneMenu>
		
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="alarmAckCheck"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnAck}"/>
		<x:outputLabel for="alarmAckCheck" value="Notify when alarms are Acknowledged"/>
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="alarmClearCheck"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnClear}"/>
		<x:outputLabel for="alarmClearCheck" value="Notify when alarms Clear"/>
		
	<f:verbatim></fieldset></f:verbatim>


	<f:verbatim><br/><br/><fieldset><legend>Alarming</legend></f:verbatim>
		<h:selectBooleanCheckbox id="disableAlarmsCheckBox" value="#{ptEditorForm.pointBase.point.alarmsDisabled}"/>
		<x:outputLabel for="disableAlarmsCheckBox" value="Disable All Alarms"/>	
	
	    <f:verbatim><br/><br/></f:verbatim>
	
	    <x:dataTable id="alarmTable" value="#{ptEditorForm.alarmTableEntries}"
	                var="alarmTableEntry"
	                preserveDataModel="true"
	                rowIndexVar="rowNumber">
	        <h:column>
	            <f:facet name="header">
	                <x:outputText value="Condition" />
	            </f:facet>
	            <x:outputText value="#{alarmTableEntry.condition}" styleClass="staticLabel"/>
	        </h:column>
	        <h:column>
	            <f:facet name="header">
	                <x:outputText value="Category" />
	            </f:facet>
				<x:selectOneMenu id="alarmCat" value="#{alarmTableEntry.generate}" >
					<f:selectItems value="#{ptEditorForm.alarmCategories}"/>
				</x:selectOneMenu>
	        </h:column>
	        <h:column>
	            <f:facet name="header">
	                <x:outputText value="Notify" />
	            </f:facet>
				<x:selectOneMenu id="alarmNotif" value="#{alarmTableEntry.excludeNotify}" >
					<f:selectItem itemLabel="(none)" itemValue="N"/>
					<f:selectItem itemLabel="Exclude Notify" itemValue="E"/>
					<f:selectItem itemLabel="Auto Ack" itemValue="A"/>
					<f:selectItem itemLabel="Exclude Notify & Auto Ack" itemValue="B"/>
				</x:selectOneMenu>				
	        </h:column>

	    </x:dataTable>
	<f:verbatim></fieldset></f:verbatim>

</f:subview>