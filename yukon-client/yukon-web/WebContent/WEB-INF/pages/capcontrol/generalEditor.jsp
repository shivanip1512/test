<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.database.data.device.DeviceTypesFuncs" %>

<f:subview id="general" rendered="#{capControlForm.visibleTabs['General']}" >
    <x:htmlTag value="fieldset" styleClass="fieldSet">
        <x:htmlTag value="legend"><x:outputText value="General"/></x:htmlTag>
		<x:div id="topDiv">
			<x:htmlTag value="br" />

			<h:panelGroup id="generalPao"
				rendered="#{capControlForm.visibleTabs['GeneralPAO']}">
				<x:outputText value="Type: "
					title="System specific type of CapControl object" />
				<x:outputText
					value="#{capControlForm.PAOBase.PAOType} (id: #{capControlForm.PAOBase.PAObjectID})"
					styleClass="staticLabel"
					rendered="#{!capControlForm.editingController}" />
				<x:selectOneMenu id="Select_702XCBC_PAOType"
					value="#{capControlForm.CBControllerEditor.deviceType}"
					rendered="#{capControlForm.CBControllerEditor.device702X && capControlForm.PAOBase.PAOType != 'CAP BANK'}">
					<f:selectItems value="#{selLists.typeList702X}" />
				</x:selectOneMenu>
				<x:selectOneMenu id="Select_701XCBC_PAOType"
					value="#{capControlForm.CBControllerEditor.deviceType}"
					rendered="#{capControlForm.CBControllerEditor.device701X && capControlForm.PAOBase.PAOType != 'CAP BANK'}">
					<f:selectItems value="#{selLists.typeList701X}" />
				</x:selectOneMenu>
				<x:selectOneMenu id="Select_DNPCBC_PAOType"
					value="#{capControlForm.CBControllerEditor.deviceType}"
					rendered="#{capControlForm.CBControllerEditor.deviceDNP && capControlForm.PAOBase.PAOType != 'CAP BANK'}">
					<f:selectItems value="#{selLists.typeListDNP}" />
				</x:selectOneMenu>
				
				<x:htmlTag value="br"/>
				
				<x:outputText value="Class: "
					title="System specific class of CapControl object" />
				<x:outputText value="#{capControlForm.PAOBase.PAOClass}"
					styleClass="staticLabel" />

                <x:htmlTag value="br"/>				
				
				<x:outputText value="Parent: "
					title="The parent object this item belongs to" />
				<x:outputText value="#{capControlForm.parent}"
					styleClass="staticLabel" />
				
				<x:htmlTag value="br"/>

				<x:outputLabel for="paoName" value="Name: "
					title="System wide label for this object" />
				<x:inputText id="paoName" value="#{capControlForm.PAOBase.PAOName}"
					styleClass="char32Label" required="true" maxlength="60"
					rendered="#{capControlForm.PAOBase.PAOType != 'CAP BANK'}" />
				<x:inputText id="paoNameForCaps"
					value="#{capBankEditor.capBank.PAOName}" styleClass="char32Label"
					required="true" maxlength="60"
					rendered="#{capControlForm.PAOBase.PAOType == 'CAP BANK'}" />

				<x:htmlTag value="br"/>
				
				<h:selectBooleanCheckbox id="disablePao"
					value="#{capControlForm.PAOBase.disabled}"
					rendered="#{capControlForm.PAOBase.PAOType != 'CAP BANK'}" />
				<h:selectBooleanCheckbox id="disablePaoForCaps"
					value="#{capBankEditor.capBank.disabled}"
					rendered="#{capControlForm.PAOBase.PAOType == 'CAP BANK'}" />

				<x:outputLabel for="disablePao" value="Disable"
					title="Disables/Enables the object." />
			</h:panelGroup>

			<h:panelGroup id="generalSched"
				rendered="#{capControlForm.visibleTabs['GeneralSchedule']}">
				<x:outputLabel for="name" value="Name: "
					title="System wide label for this object" />
				<x:inputText id="name"
					value="#{capControlForm.PAOBase.scheduleName}"
					styleClass="char32Label" required="true" maxlength="64" />
				
				<x:htmlTag value="br"/>
				
				<h:selectBooleanCheckbox id="disableSched"
					value="#{capControlForm.PAOBase.disabled}" />
				<x:outputLabel for="disableSched" value="Disable"
					title="Do not allow this schedule to run in the future" />
			</h:panelGroup>

			<h:panelGroup id="cbcStrat"
				rendered="#{capControlForm.visibleTabs['CBCStrategy']}">
				<x:outputLabel for="stratName" value="Name: "
					title="System wide label for this object" />
				<x:inputText id="stratName"
					value="#{capControlForm.cbcStrategiesMap[capControlForm.currentStrategyID].strategyName}"
					styleClass="char32Label" required="true" maxlength="32" />
				
			</h:panelGroup>

		</x:div>
	</x:htmlTag>
</f:subview>