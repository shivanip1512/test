<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>

<f:subview id="cbcController" rendered="#{capControlForm.visibleTabs['CBCController']}" >

	<h:panelGrid id="cbcBody" columns="1" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

		<h:column>
		    <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

			<f:verbatim><br/></f:verbatim>
			<x:panelGroup id="oneWayCBC" rendered="#{!capControlForm.CBControllerEditor.twoWay}">
				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlSerNum" value="Serial Number: " title="Serial number of the controller device" />
				<x:inputText id="cntrlSerNumEd" styleClass="staticLabel"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.serialNumber}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" />
				<x:selectOneMenu id="cntrlRoute" value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.routeID}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.routes}"/>
				</x:selectOneMenu>
			</x:panelGroup>


			<x:panelGroup id="twoWayCBC" rendered="#{capControlForm.CBControllerEditor.twoWay}" >
				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMast" styleClass="staticLabel"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.masterAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMSlav" styleClass="staticLabel"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.slaveAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses" />
				<x:selectOneMenu id="cntrlComChann" value="#{capControlForm.CBControllerEditor.paoCBC.deviceDirectCommSettings.portID}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.commChannels}"/>
				</x:selectOneMenu>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="How long to wait after communications" />
				<x:inputText id="cntrlCommW" styleClass="staticLabel"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.postCommWait}" >
					<f:validateLongRange minimum="0" maximum="99999" />
				</x:inputText>


				<h:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
					<h:column>
					<f:verbatim><br/></f:verbatim>
					<h:selectBooleanCheckbox id="scanIntegrityChk" onclick="submit();"
							valueChangeListener="#{capControlForm.showScanRate}"
							value="#{capControlForm.CBControllerEditor.editingIntegrity}"
							immediate="true" />
					<x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" />
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
					<x:selectOneMenu id="integrityInterval"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].intervalRate}" >
						<f:selectItems value="#{capControlForm.timeInterval}"/>
					</x:selectOneMenu>
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="integrityAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
					<x:selectOneMenu id="integrityAltInterval"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].alternateRate}" >
						<f:selectItems value="#{capControlForm.timeInterval}"/>
					</x:selectOneMenu>
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
					<x:selectOneMenu id="integrityGrp"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].scanGroup}" >
						<f:selectItem itemLabel="Default" itemValue="0" />
						<f:selectItem itemLabel="First" itemValue="1" />
						<f:selectItem itemLabel="Second" itemValue="2" />
					</x:selectOneMenu>
					</h:column>


					<h:column>
					<f:verbatim><br/></f:verbatim>
					<h:selectBooleanCheckbox id="scanExceptionChk" onclick="submit();"
							valueChangeListener="#{capControlForm.showScanRate}"
							value="#{capControlForm.CBControllerEditor.editingException}"
							immediate="true" />
					<x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" />
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
					<x:selectOneMenu id="exceptionInterval"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].intervalRate}" >
						<f:selectItems value="#{capControlForm.timeInterval}"/>
					</x:selectOneMenu>
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
					<x:selectOneMenu id="exceptionAltInterval"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].alternateRate}" >
						<f:selectItems value="#{capControlForm.timeInterval}"/>
					</x:selectOneMenu>
					<f:verbatim><br/></f:verbatim>
					<x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
					<x:selectOneMenu id="exceptionGrp"
							rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].scanGroup}" >
						<f:selectItem itemLabel="Default" itemValue="0" />
						<f:selectItem itemLabel="First" itemValue="1" />
						<f:selectItem itemLabel="Second" itemValue="2" />
					</x:selectOneMenu>
					</h:column>
					
				</h:panelGrid>

			</x:panelGroup>


			<f:verbatim></fieldset></f:verbatim>

		</h:column>

	
	</h:panelGrid>
		
    


</f:subview>