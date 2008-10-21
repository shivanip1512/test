<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<c:url var="orphanURL" value="/spring/capcontrol/tier/cceditorpopup"/>
<%
	CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
	String itemid = JSFParamUtil.getJSFReqParam("itemid");
	String type = JSFParamUtil.getJSFReqParam("type");

	if (itemid != null && type != null){
    	capControlForm.initItem(Integer.parseInt(itemid), Integer.parseInt(type));
	}
%>
<f:subview id="cbcCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}">
	<f:verbatim>
		<div id="pointSelectHelp"><jsp:include flush="true" page="include/pointSelectInc.jsp" /></div>

        <script type="text/javascript">
            function closeOrphanedCBCPopup(){
                $('orphanedCBCContent').hide();
            }
		
            function showOrphanedCBCPopup(){
		    
                new Ajax.Updater('orphanedCBCBody', '${orphanURL}', {
                    method: 'post'
                });
                $('orphanedCBCContent').show();
            }
		
            function setCBC( deviceName, pointId, pointName){
                var device = $('cbcDevice')
                device.innerHTML = deviceName;
                $('ctlPoint').innerHTML = pointName;
                $('cbc_point').value = pointId;
            }
        </script>

	</f:verbatim>

	<x:panelGrid id="capbankBody" columns="2" styleClass="gridLayout"
		columnClasses="gridCell,gridCell">
		<x:column>
			<x:htmlTag value="fieldset" styleClass="fieldSet">
				<x:htmlTag value="legend">
					<x:outputText value="Cap Bank Info" />
				</x:htmlTag>
				
				<x:panelGrid columns="2">
				
					<x:outputLabel for="CapBank_Address" value="Address: "
						title="Geographical location of this CapBank" />
					<x:inputText id="CapBank_Address"
						value="#{capBankEditor.capBank.location}" required="true"
						maxlength="60" size="60" />
	
					<x:outputLabel for="capMapLocID" value="Map Location ID: "
						title="Mapping code/string used for third-party systems" />
					<x:inputText id="capMapLocID"
						value="#{capBankEditor.capBank.capBank.mapLocationID}"
						required="true" maxlength="64" />
	
					<x:outputLabel for="bankMan" value="Switch Manufacture: "
						title="What company manufactured this item" />
					<x:selectOneMenu id="bankMan"
						value="#{capBankEditor.capBank.capBank.switchManufacture}">
						<f:selectItem itemValue="(none)" />
						<f:selectItem itemValue="ABB" />
						<f:selectItem itemValue="Cannon Tech" />
						<f:selectItem itemValue="Cooper" />
						<f:selectItem itemValue="Trinetics" />
						<f:selectItem itemValue="Siemens" />
						<f:selectItem itemValue="Westinghouse" />
					</x:selectOneMenu>
	
					<x:outputLabel for="cntrlerType" value="Controller Type: "
						title="What type of switch this item is" />
					<x:selectOneMenu id="cntrlerType"
						value="#{capBankEditor.capBank.capBank.controllerType}">
						<f:selectItem itemValue="(none)" />
						<f:selectItem itemValue="CTI DLC" />
						<f:selectItem itemValue="CTI Paging" />
						<f:selectItem itemValue="CTI FM" />
						<f:selectItem itemValue="FP Paging" />
						<f:selectItem itemValue="Telemetric" />
					</x:selectOneMenu>
	
					<x:outputLabel for="bankType" value="Type of Switch: "
						title="What type of switch this item is" />
					<x:selectOneMenu id="bankType"
						value="#{capBankEditor.capBank.capBank.typeOfSwitch}">
						<f:selectItem itemValue="(none)" />
						<f:selectItem itemValue="Oil" />
						<f:selectItem itemValue="Vacuum" />
						<f:selectItem itemValue="Mix" />
					</x:selectOneMenu>
				
				</x:panelGrid>

			</x:htmlTag>
			
			<x:htmlTag value="br" />

			<x:htmlTag value="fieldset" styleClass="fieldSet">
				<x:htmlTag value="legend">
					<x:outputText value="Configuration" />
				</x:htmlTag>
                
                <x:panelGrid columns="2">
                
					<x:outputLabel for="bankOperation" value="Operation Method: "
						title="How this CapBank should operate in the field" />
					<x:selectOneMenu id="bankOperation"
						value="#{capBankEditor.capBank.capBank.operationalState}"
						onchange="submit()">
						<f:selectItems value="#{selLists.capBankOpStates}" />
					</x:selectOneMenu>
	
					<x:outputLabel for="bankSize" value="Bank Size: "
						title="The total size of the CapBank" />
                    
                    <x:panelGroup>
                    
						<x:selectOneMenu id="bankSizeSelect"
							value="#{capBankEditor.capBank.capBank.bankSize}"
							onchange="submit()" rendered="#{!capBankEditor.customSize}">
							<f:selectItems value="#{selLists.addCapBankSizes}" />
						</x:selectOneMenu>
		
						<f:verbatim>&nbsp;&nbsp;</f:verbatim>
		
						<x:inputText id="bankSize" styleClass="char8Label" required="true"
							value="#{capBankEditor.capBank.capBank.bankSize}"
							rendered="#{capBankEditor.customSize}">
							<f:validateLongRange minimum="0" maximum="99999" />
						</x:inputText>
						<x:outputText id="bankSizeDesc" value="kVar" />
						<f:verbatim>&nbsp;&nbsp;</f:verbatim>
		
						<x:outputLabel for="isCustomSize" value="Custom Bank Size?"
							title="The total size of the CapBank" />
						<x:selectBooleanCheckbox id="isCustomSize"
							value="#{capBankEditor.customSize}" onclick="submit()" />
					</x:panelGroup>
	
					<x:outputLabel for="bankReclose" value="Reclose Delay: "
						title="The amount of time this CapBank should wait before executing the close command" />
					<x:selectOneMenu id="bankReclose"
						value="#{capBankEditor.capBank.capBank.recloseDelay}">
						<f:selectItem itemLabel="(none)" itemValue="0" />
						<f:selectItems value="#{capControlForm.timeInterval}" />
					</x:selectOneMenu>
				
				</x:panelGrid>

				<x:htmlTag value="br" />
				<x:htmlTag value="br" />

				<x:div id="cbcPointDiv" forceId="true">
					<x:inputHidden id="cbc_point" forceId="true"
						value="#{capBankEditor.capBank.capBank.controlPointID }"
						valueChangeListener="#{capBankEditor.ctlPointChanged}" />
					<x:outputLabel for="cbcDevice" value="Control Device/Point: "
						title="Point used for monitoring the control (Only displays points that are not yet used by CapBanks)"
						styleClass="medStaticLabel" />
					<x:outputText id="cbcDevice" forceId="true"
						value="#{capBankEditor.ctlPaoName}" />
					<x:outputText id="devicePointSeperator" value=" : " />

					<x:outputText id="ctlPoint" forceId="true"
						value="#{capBankEditor.ctlPointName}" />

					<x:htmlTag value="br" />

					<h:outputLink value="javascript:void(0);"
						onclick="showOrphanedCBCPopup();">
						<h:outputText value="Select point..." />
					</h:outputLink>
				</x:div>

				<x:htmlTag value="br" />

				<x:commandLink id="cbcPoint_setNone"
					title="Do not use a point to control cap bank"
					styleClass="medStaticLabel" value="No Control Point"
					actionListener="#{capBankEditor.capBankTeeClick}">
					<f:param name="ptID" value="0" />
				</x:commandLink>
			</x:htmlTag>
			
			<x:htmlTag value="br" />
			
			<x:panelGroup>
				<x:htmlTag value="fieldset" styleClass="fieldSet">
					<x:htmlTag value="legend">
						<x:outputText value="Cap Bank Points" />
					</x:htmlTag>
					<x:div forceId="true" id="SubstationBusEditorScrollDiv"
						styleClass="scrollSmall">
						<x:tree2 id="SubstationBusEditPointTree"
							value="#{capControlForm.pointTreeForm.pointList}" var="node"
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
										actionListener="#{capControlForm.pointTreeForm.pointClick}">
										<f:param name="ptID" value="#{node.identifier}" />
									</x:commandLink>
								</x:panelGroup>
							</f:facet>
						</x:tree2>
					</x:div>
				</x:htmlTag>
			</x:panelGroup>

			<x:htmlTag value="br" />

			<h:outputText styleClass="tableHeader" value="Point Editor: " />

			<x:commandLink id="addPtLnk" value="Add Point" actionListener="#{capControlForm.pointTreeForm.addPointClick}">
				<f:param name="parentId" value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
			</x:commandLink>

			<x:outputText value=" | " />

			<x:commandLink id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.pointTreeForm.deletePointClick}"/>

		</x:column>
		<x:column>

			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Cap Bank Operations" />
                </x:htmlTag>
				<x:outputLabel for="maxDailyOps" value="Max Daily Operations: "
					title="The total number of controls allowed per day" />
				<x:inputText id="maxDailyOps" styleClass="char16Label"
					required="true" value="#{capBankEditor.capBank.capBank.maxDailyOps}">
					<f:validateLongRange minimum="0" maximum="9999" />
				</x:inputText>
				<x:outputText id="maxDailyOpsDesc" value="(0 = unlimited)" />
	
				<x:htmlTag value="br" />
				
				<x:selectBooleanCheckbox id="disabledOps"
					value="#{capBankEditor.capBank.capBank.maxOperationDisabled}" />
				<x:outputLabel for="disabledOps"
					value="Disable upon reaching max operations"
					title="Option to disable automatic control on this device upon reaching the max number of operations." />
	
				<x:htmlTag value="br" />
				<x:htmlTag value="br" />
			</x:htmlTag>
			
			<x:htmlTag value="br" />
			
			<x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend">
                    <x:outputText value="Controller Configuration" />
                </x:htmlTag>
                
                <x:panelGrid columns="2">
                
					<x:outputText value="CBC Controller: " title="Click on the link to edit the CBC" />
					
					<x:panelGroup>
					
						<x:commandLink rendered="#{capBankEditor.ctlPaoID != 0}"
							id="CBCEditor" value="#{capBankEditor.ctlPaoName}"
							actionListener="#{capControlForm.paoClick}"
							title="Click on the link to edit the CBC">
							<f:param name="paoID" value="#{capBankEditor.ctlPaoID}" />
						</x:commandLink>
		
						<x:outputText styleClass="medStaticLabel"
							value="No Controller Selected"
							rendered="#{capBankEditor.ctlPaoID == 0}" />
		
					</x:panelGroup>
						
					<x:outputLabel for="cntrlSerNumEd" value="Serial Number: "
						title="Serial number of the controller device"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}" />
					<x:inputText id="cntrlSerNumEd" styleClass="staticLabel"
						disabled="true"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}"
						value="#{capBankEditor.controller.deviceCBC.serialNumber}"
						maxlength="9" size="9">
						<f:validateLongRange minimum="0" maximum="9999999999" />
					</x:inputText>
	
					<x:outputLabel for="cntrlRoute" value="Control Route: "
						title="Communication route the conroller uses"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}" />
					<x:selectOneMenu id="cntrlRoute"
						value="#{capBankEditor.controller.deviceCBC.routeID}"
						disabled="true"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}">
						<f:selectItem itemLabel="(none)" itemValue="0" />
						<f:selectItems value="#{selLists.routes}" />
					</x:selectOneMenu>
						
					<x:outputLabel for="cntrlSerNumEd2" value="Serial Number: " title="Serial number of the controller device"
                           rendered="#{capBankEditor.twoWayController}"/>
					<x:inputText id="cntrlSerNumEd2" styleClass="staticLabel" disabled="true"
						value="#{capBankEditor.controller.deviceCBC.serialNumber}" rendered="#{capBankEditor.twoWayController}">
						<f:validateLongRange minimum="0" maximum="9999999999" />
					</x:inputText>
					
					<x:outputLabel for="cntrlMast" value="Master Address: " rendered="#{capBankEditor.twoWayController}" 
                           title="Integer address of the controller device in the field" />
					<x:inputText id="cntrlMast" styleClass="staticLabel" disabled="true" rendered="#{capBankEditor.twoWayController}" 
                           value="#{capBankEditor.controller.deviceAddress.masterAddress}">
						<f:validateLongRange minimum="0" maximum="9999999999" />
					</x:inputText>
	
					<x:outputLabel for="cntrlMSlav" value="Slave Address: " rendered="#{capBankEditor.twoWayController}" 
                           title="Integer address of the controller device in the field" />
					<x:inputText id="cntrlMSlav" styleClass="staticLabel" disabled="true" rendered="#{capBankEditor.twoWayController}"
						value="#{capBankEditor.controller.deviceAddress.slaveAddress}">
						<f:validateLongRange minimum="0" maximum="9999999999" />
					</x:inputText>
	
					<x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses"
                           rendered="#{capBankEditor.twoWayController}"/>
					<x:selectOneMenu id="cntrlComChann" rendered="#{capBankEditor.twoWayController}" 
					    value="#{capBankEditor.controller.deviceDirectCommSettings.portID}" disabled="true">
						<f:selectItem itemLabel="(none)" itemValue="0" />
						<f:selectItems value="#{selLists.commChannels}" />
					</x:selectOneMenu>
	
					<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " rendered="#{capBankEditor.twoWayController}" 
                           title="How long to wait after communications" />
					<x:inputText id="cntrlCommW" styleClass="staticLabel" disabled="true" rendered="#{capBankEditor.twoWayController}" 
                           value="#{capBankEditor.controller.deviceAddress.postCommWait}">
						<f:validateLongRange minimum="0" maximum="99999" />
					</x:inputText>
					
				</x:panelGrid>

				<x:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell" rendered="#{capBankEditor.twoWayController}">
					<x:column>

						<x:htmlTag value="br" />
						
						<x:selectBooleanCheckbox id="scanIntegrityChk" value="#{capBankEditor.editingIntegrity}" immediate="true" disabled="true" />
						<x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" />
						
						<x:htmlTag value="br" />
						
						<x:panelGrid columns="2">
						
							<x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityInterval"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].intervalRate}"
								disabled="true">
								<f:selectItems value="#{selLists.timeInterval}" />
							</x:selectOneMenu>
							
							<x:outputLabel for="integrityAltInterval" value="Alt. Interval: "
								title="An alternate scan rate that can be less or more than the primary scan rate"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityAltInterval" disabled="true"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].alternateRate}">
								<f:selectItems value="#{selLists.timeInterval}" />
							</x:selectOneMenu>
							
							<x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:selectOneMenu id="integrityGrp" disabled="true" rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Integrity'].scanGroup}">
								<f:selectItem itemLabel="Default" itemValue="0" />
								<f:selectItem itemLabel="First" itemValue="1" />
								<f:selectItem itemLabel="Second" itemValue="2" />
							</x:selectOneMenu>
						</x:panelGrid>
					</x:column>

					<x:column>
						
						<x:htmlTag value="br" />
						
						<x:selectBooleanCheckbox id="scanExceptionChk" value="#{capBankEditor.editingException}" immediate="true" disabled="true" />
						<x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" />
						
						<x:htmlTag value="br" />
						
						<x:panelGrid columns="2">
						
							<x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionInterval" disabled="true" rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Exception'].intervalRate}">
								<f:selectItems value="#{selLists.timeInterval}" />
							</x:selectOneMenu>
							
							<x:outputLabel for="exceptionAltInterval" value="Alt. Interval: "
								title="An alternate scan rate that can be less or more than the primary scan rate"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionAltInterval" disabled="true" rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Exception'].alternateRate}">
								<f:selectItems value="#{selLists.timeInterval}" />
							</x:selectOneMenu>
							
							<x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:selectOneMenu id="exceptionGrp" disabled="true" rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"
								value="#{capBankEditor.controller.deviceScanRateMap['Exception'].scanGroup}">
								<f:selectItem itemLabel="Default" itemValue="0" />
								<f:selectItem itemLabel="First" itemValue="1" />
								<f:selectItem itemLabel="Second" itemValue="2" />
							</x:selectOneMenu>
						
						</x:panelGrid>
						
					</x:column>

				</x:panelGrid>
			</x:htmlTag>
		</x:column>
	</x:panelGrid>
	<f:verbatim>
        <div id="orphanedCBCContent" class="popUpDiv simplePopup" style="display: none;">
            <!--  fix for IE6 bug (see itemPicker.css for more info) -->
            <!--[if lte IE 6.5]><iframe></iframe><![endif]-->
            <div class="titledContainer boxContainer">

                <div class="titleBar boxContainer_titleBar">
                    <div class="controls" onclick="closeOrphanedCBCPopup()">
                        <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/collapse.gif">
                    </div>
                    <div class="title boxContainer_title">Orphaned CBC's</div>
                </div>

                <div class="content boxContainer_content">
                    <div id="orphanedCBCBody"></div>
                </div>

            </div>
        </div>
	</f:verbatim>
	<x:inputHidden id="capbankHiden" forceId="true" value="#{capControlForm.offsetMap['capbankHiden']}" />
</f:subview>