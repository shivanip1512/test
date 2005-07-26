<%@ page import="com.cannontech.database.data.point.PointBase" %>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x"%>


<%
%>
<f:subview id="alarming" rendered="#{ptEditorForm.visibleTabs['Alarming']}" >

	<f:verbatim><br/><br/><fieldset><legend>Notifcation</legend></f:verbatim>
		<h:outputLabel for="notifGroupList" value="Notifcation Group: "/>
		<h:selectOneMenu id="notifGroupList" value="#{ptEditorForm.pointBase.pointAlarming.notificationGroupID}" >
			<f:selectItems value="#{ptEditorForm.notifcationGrps}"/>
		</h:selectOneMenu>
	
		<f:verbatim><br/></f:verbatim>
		<h:outputLabel for="contactList" value="Contact: "/>
		<h:selectOneMenu id="contactList" value="#{ptEditorForm.pointBase.pointAlarming.recipientID}" >
			<f:selectItems value="#{ptEditorForm.emailNotifcations}"/>
		</h:selectOneMenu>
		
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="alarmAckCheck"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnAck}"/>
		<h:outputLabel for="alarmAckCheck" value="Notify when alarms are Acknowledged"/>
		<f:verbatim><br/></f:verbatim>
		<h:selectBooleanCheckbox id="alarmClearCheck"
			value="#{ptEditorForm.pointBase.pointAlarming.notifyOnClear}"/>
		<h:outputLabel for="alarmClearCheck" value="Notify when alarms Clear"/>
		
	<f:verbatim></fieldset></f:verbatim>


	<f:verbatim><br/><br/><fieldset><legend>Alarming</legend></f:verbatim>
		<h:selectBooleanCheckbox id="disableAlarmsCheckBox" value="#{ptEditorForm.pointBase.point.alarmsDisabled}"/>
		<h:outputLabel for="disableAlarmsCheckBox" value="Disable All Alarms"/>	
	
	    <f:verbatim><br/><br/></f:verbatim>
	
	    <x:dataTable id="alarmTable" value="#{ptEditorForm.alarmTableEntries}"
	                var="alarmTableEntry"
	                preserveDataModel="true"
	                rowIndexVar="rowNumber">
	        <h:column>
	            <f:facet name="header">
	                <h:outputText value="Condition" />
	            </f:facet>
	            <h:outputText value="#{alarmTableEntry.condition}" styleClass="staticLabel"/>
	        </h:column>
	        <h:column>
	            <f:facet name="header">
	                <h:outputText value="Category" />
	            </f:facet>
				<h:selectOneMenu id="alarmCat" value="#{alarmTableEntry.generate}" >
					<f:selectItems value="#{ptEditorForm.alarmCategories}"/>
				</h:selectOneMenu>
	        </h:column>
	        <h:column>
	            <f:facet name="header">
	                <h:outputText value="Notify" />
	            </f:facet>
				<h:selectOneMenu id="alarmNotif" value="#{alarmTableEntry.excludeNotify}" >
					<f:selectItem itemLabel="(none)" itemValue="N"/>
					<f:selectItem itemLabel="Exclude Notify" itemValue="E"/>
					<f:selectItem itemLabel="Auto Ack" itemValue="A"/>
					<f:selectItem itemLabel="Exclude Notify & Auto Ack" itemValue="B"/>
				</h:selectOneMenu>				
	        </h:column>

	    </x:dataTable>
	<f:verbatim></fieldset></f:verbatim>

</f:subview>