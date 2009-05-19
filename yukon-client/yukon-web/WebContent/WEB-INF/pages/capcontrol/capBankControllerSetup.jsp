<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<%
    CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
    String itemid = JSFParamUtil.getJSFReqParam("itemid");
    String type = JSFParamUtil.getJSFReqParam("type");
  
    if (itemid != null && type!= null) {
        capControlForm.initItem(Integer.parseInt(itemid), Integer.parseInt(type));
        capControlForm.setCBControllerEditor(null);
        capControlForm.getCBControllerEditor(); 
    }
%>
<f:subview id="cbcController" rendered="#{capControlForm.visibleTabs['CBCController']}">
	<h:panelGrid id="cbcBody" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell">

		<h:column rendered="#{capControlForm.CBControllerEditor.oneWay}">
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Configuration"/></x:htmlTag>
				
				<x:panelGrid columns="2">
	
					<x:outputLabel for="cntrlSerNumEd" value="Serial Number: " title="Serial number of the controller device" />
					<x:inputText id="cntrlSerNumEd" styleClass="staticLabel" value="#{capControlForm.CBControllerEditor.serialNumber}"
						maxlength="9" size="9">
						<f:validateLongRange minimum="0" maximum="9999999999" />
					</x:inputText>
	
					<x:outputLabel for="cntrlRoute" value="Control Route: "
						title="Communication route the conroller uses" />
					<x:selectOneMenu id="cntrlRoute" value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.routeID}">
						<f:selectItem itemLabel="(none)" itemValue="0" />
						<f:selectItems value="#{selLists.routes}" />
					</x:selectOneMenu>
				</x:panelGrid>
			</x:htmlTag>
		</h:column>

		<h:column rendered="#{capControlForm.CBControllerEditor.twoWay}">
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Configuration"/></x:htmlTag>

				<x:panelGroup id="editCBCCheck">
					<h:selectBooleanCheckbox id="editCntrl"
                        disabled="#{!capControlForm.editingAuthorized}"
						value="#{capControlForm.CBControllerEditor.editingController}"
						onclick="submit();" />
					<x:outputLabel for="editCntrl" value="Edit Controller" title="Allows editing of the controller this CapBank currently uses" />
				</x:panelGroup>
				
				<x:htmlTag value="br"/>
				
				<x:panelGroup id="twoWayCBC">
                    <x:panelGrid columns="2">
					
						<x:outputLabel for="cntrlSerNumEd2" value="Serial Number: "
							title="Serial number of the controller device" />
						<x:inputText id="cntrlSerNumEd2" styleClass="staticLabel"
							disabled="#{!capControlForm.CBControllerEditor.editingController}"
							value="#{capControlForm.CBControllerEditor.serialNumber}"
							maxlength="9" size="9">
							<f:validateLongRange minimum="0" maximum="9999999999" />
						</x:inputText>
		
						<x:outputLabel for="cntrlMast" value="Master Address: "
							title="Integer address of the controller device in the field" />
						<x:inputText id="cntrlMast" styleClass="staticLabel"
							disabled="#{!capControlForm.CBControllerEditor.editingController}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.masterAddress}">
							<f:validateLongRange minimum="0" maximum="9999999999" />
						</x:inputText>
		
						<x:outputLabel for="cntrlMSlav" value="Slave Address: "
							title="Integer address of the controller device in the field" />
						<x:inputText id="cntrlMSlav" styleClass="staticLabel"
							disabled="#{!capControlForm.CBControllerEditor.editingController}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.slaveAddress}">
							<f:validateLongRange minimum="0" maximum="9999999999" />
						</x:inputText>
		
						<x:outputLabel for="cntrlComChann" value="Comm. Channel: "
							title="Communication channel the conroller uses" />
						<x:selectOneMenu
							disabled="#{!capControlForm.CBControllerEditor.editingController}"
							id="cntrlComChann"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceDirectCommSettings.portID}">
							<f:selectItem itemLabel="(none)" itemValue="0" />
							<f:selectItems value="#{selLists.commChannels}" />
						</x:selectOneMenu>
		
						<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: "
							title="How long to wait after communications" />
						<x:inputText id="cntrlCommW" styleClass="staticLabel"
							disabled="#{!capControlForm.CBControllerEditor.editingController}"
							value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.postCommWait}">
							<f:validateLongRange minimum="0" maximum="99999" />
						</x:inputText>
					</x:panelGrid>
				</x:panelGroup>
				
				<h:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn">
	
					<h:column>
						<h:selectBooleanCheckbox id="scanIntegrityChk" onclick="submit();"
							valueChangeListener="#{capControlForm.showScanRate}"
							value="#{capControlForm.CBControllerEditor.editingIntegrity}"
							disabled="#{!capControlForm.CBControllerEditor.editingController || capControlForm.editingAuthorized}" />
						
						<x:htmlTag value="br"/>
						
						<x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan"
								title="Integrity scan type" />
						
						<x:panelGrid columns="2">
							
							<x:outputLabel for="integrityInterval" value="Interval: "
								title="How often this scan should occur"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityInterval"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].intervalRate}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItems value="#{capControlForm.timeInterval}" />
							</x:selectOneMenu>
							<x:outputLabel for="integrityAltInterval" value="Alt. Interval: "
								title="An alternate scan rate that can be less or more than the primary scan rate"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityAltInterval"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].alternateRate}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItems value="#{capControlForm.timeInterval}" />
							</x:selectOneMenu>
							<x:outputLabel for="integrityGrp" value="Scan Group: "
								title="A way to group scans into a common collection"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityGrp"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].scanGroup}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItem itemLabel="Default" itemValue="0" />
								<f:selectItem itemLabel="First" itemValue="1" />
								<f:selectItem itemLabel="Second" itemValue="2" />
							</x:selectOneMenu>
						
						</x:panelGrid>
					</h:column>
	
					<h:column>
						<h:selectBooleanCheckbox id="scanExceptionChk" onclick="submit();"
							valueChangeListener="#{capControlForm.showScanRate}"
							value="#{capControlForm.CBControllerEditor.editingException}"
							immediate="true"
							disabled="#{!capControlForm.CBControllerEditor.editingController || capControlForm.editingAuthorized}" />
						
						<x:htmlTag value="br"/>
						
						<x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan"
							title="Exception scan type" />
							
						<x:panelGrid columns="2">
                            <x:outputLabel for="exceptionInterval" value="Interval: "
                                title="How often this scan should occur"
                                rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionInterval"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].intervalRate}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItems value="#{capControlForm.timeInterval}" />
							</x:selectOneMenu>
	
							<x:outputLabel for="exceptionAltInterval" value="Alt. Interval: "
								title="An alternate scan rate that can be less or more than the primary scan rate"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionAltInterval"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].alternateRate}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItems value="#{capControlForm.timeInterval}" />
							</x:selectOneMenu>
	
							<x:outputLabel for="exceptionGrp" value="Scan Group: "
								title="A way to group scans into a common collection"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionGrp"
								rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
								value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].scanGroup}"
								disabled="#{!capControlForm.CBControllerEditor.editingController}">
								<f:selectItem itemLabel="Default" itemValue="0" />
								<f:selectItem itemLabel="First" itemValue="1" />
								<f:selectItem itemLabel="Second" itemValue="2" />
							</x:selectOneMenu>
						</x:panelGrid>
					</h:column>
				</h:panelGrid>
			</x:htmlTag>
		</h:column>

		<h:column>
			<x:panelGroup>
				<x:htmlTag value="fieldset" styleClass="fieldSet">
                    <x:htmlTag value="legend"><x:outputText value="Analog and Status Points"/></x:htmlTag>
					<x:div forceId="true" id="CBCCtlEditorScrollDiv"
						styleClass="scrollSmall">
	
						<x:tree2 binding="#{capControlForm.CBControllerEditor.pointTree}"
							id="CtlEditPointTree"
							value="#{capControlForm.CBControllerEditor.pointList}" var="node"
							showRootNode="false" varNodeToggler="t" preserveToggle="true"
							clientSideToggle="true" showLines="false">
	
							<f:facet name="root">
								<x:panelGroup>
									<x:outputText id="rootLink" value="#{node.description}" />
								</x:panelGroup>
							</f:facet>
							<f:facet name="pointtype">
								<x:panelGroup>
									<x:outputText id="paChCnt"
										value="#{node.description} (#{node.childCount})"
										rendered="#{!empty node.children}" />
								</x:panelGroup>
							</f:facet>
	
							<f:facet name="sublevels">
								<x:panelGroup>
									<x:outputText id="subLvlCnt"
										value="#{node.description} (#{node.childCount})"
										rendered="#{!empty node.children}" />
								</x:panelGroup>
							</f:facet>
	
							<f:facet name="points">
								<x:panelGroup>
									<x:commandLink id="ptLink" value="#{node.description}"
										actionListener="#{capControlForm.CBControllerEditor.pointClick}">
										<f:param name="ptID" value="#{node.identifier}" />
									</x:commandLink>
								</x:panelGroup>
							</f:facet>
						</x:tree2>
					</x:div>
				</x:htmlTag>
			</x:panelGroup>
			<x:htmlTag value="br"/>
			<h:outputText styleClass="tableHeader" value="Point Editor" rendered="#{capControlForm.editingAuthorized}"/>
			<x:commandLink id="addPtLnk" value="Add Point"
                rendered="#{capControlForm.editingAuthorized}"
				actionListener="#{capControlForm.CBControllerEditor.addPointClick}">
				<f:param name="parentId"
					value="#{capControlForm.CBControllerEditor.paoCBC.PAObjectID}" />
			</x:commandLink>
			<x:outputText value=" | " rendered="#{capControlForm.editingAuthorized}"/>
			<x:commandLink id="deletePtLnk" value="Delete Point"
                rendered="#{capControlForm.editingAuthorized}"
				actionListener="#{capControlForm.CBControllerEditor.deletePointClick}">
			</x:commandLink>

		</h:column>

	</h:panelGrid>

</f:subview>