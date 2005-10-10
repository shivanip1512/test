<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="x" %>

<f:subview id="cbcController" rendered="#{capControlForm.visibleTabs['CBCController']}" >

	<h:panelGrid id="cbcBody" columns="1" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

		<h:column>
		    <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
			<x:panelGroup id="oneWayCBC" rendered="#{!capControlForm.CBControllerEditor.twoWay}">
				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlSerNum" value="Serial Number: " title="Serial number of the controller device" />
				<x:inputText id="cntrlSerNumEd" styleClass="staticLabel" disabled="#{!capControlForm.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.serialNumber}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" />
				<x:selectOneMenu id="cntrlRoute" value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.routeID}"
						disabled="#{!capControlForm.editingController}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.routes}"/>
				</x:selectOneMenu>
			</x:panelGroup>


			<x:panelGroup id="twoWayCBC" rendered="#{capControlForm.CBControllerEditor.twoWay}" >
				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMast" styleClass="staticLabel" disabled="#{!capControlForm.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.masterAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMSlav" styleClass="staticLabel" disabled="#{!capControlForm.editingController}"						
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.slaveAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses" />
				<x:selectOneMenu id="cntrlComChann" value="#{capControlForm.CBControllerEditor.paoCBC.deviceDirectCommSettings.portID}"
						disabled="#{!capControlForm.editingController}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.commChannels}"/>
				</x:selectOneMenu>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="??" />
				<x:inputText id="cntrlCommW" styleClass="staticLabel" disabled="#{!capControlForm.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.postCommWait}" >
					<f:validateLongRange minimum="0" maximum="99999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrl0Scan" value="Class 0,1,2,3 Scan: " title="??" />
				<x:selectOneMenu id="cntrl0Scan" value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].intervalRate}"
						disabled="#{!capControlForm.editingController}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{capControlForm.timeInterval}"/>
				</x:selectOneMenu>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrl1Scan" value="Class 1,2,3 Scan: " title="??" />
				<x:selectOneMenu id="cntrl1Scan" value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].intervalRate}"
						disabled="#{!capControlForm.editingController}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{capControlForm.timeInterval}"/>
				</x:selectOneMenu>

			</x:panelGroup>


			<f:verbatim></fieldset></f:verbatim>

		</h:column>

	
	</h:panelGrid>
		
    


</f:subview>