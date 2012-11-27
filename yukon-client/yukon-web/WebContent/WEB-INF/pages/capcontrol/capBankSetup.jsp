<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.util.*" %>
<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<%@ page import="com.cannontech.web.editor.CapBankEditorForm" %>
<cti:url var="orphanURL" value="/capcontrol/tier/cceditorpopup/orphans"/>

<% /*TODO This probably isn't necessary!  */
	CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
    CapBankEditorForm capBankEditorForm = (CapBankEditorForm)JSFParamUtil.getJSFVar("capBankEditor");
	String itemid = JSFParamUtil.getJSFReqParam("itemid");
	String type = JSFParamUtil.getJSFReqParam("type");

	if (itemid != null && type != null){
    	capBankEditorForm.init(capControlForm.getDbPersistent());
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
                    
					<x:outputLabel for="bankMan" value="Switch Manufacturer: "
						title="What company manufactured this item" />
					<x:selectOneMenu id="bankMan"
						value="#{capBankEditor.capBank.capBank.switchManufacture}">
						<f:selectItems value="#{selLists.switchManufacturers}" />
					</x:selectOneMenu>
	
					<x:outputLabel for="cntrlerType" value="Controller Type: "
						title="What type of switch this item is" />
					<x:selectOneMenu id="cntrlerType"
						value="#{capBankEditor.capBank.capBank.controllerType}">
						<f:selectItems value="#{selLists.controllerTypes}" />
					</x:selectOneMenu>
	
					<x:outputLabel for="bankType" value="Type of Switch: "
						title="What type of switch this item is" />
					<x:selectOneMenu id="bankType"
						value="#{capBankEditor.capBank.capBank.typeOfSwitch}">
						<f:selectItems value="#{selLists.switchTypes}" />
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
                        disabled="#{!capControlForm.editingAuthorized}"
						value="#{capBankEditor.capBank.capBank.operationalState}"
						onchange="submit()">
						<f:selectItems value="#{selLists.capBankOpStates}" />
					</x:selectOneMenu>
	
					<x:outputLabel for="bankSize" value="Bank Size: "
						title="The total size of the CapBank" />
                    
                    <x:panelGroup>
                    
						<x:selectOneMenu id="bankSizeSelect"
                            disabled="#{!capControlForm.editingAuthorized}"
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
                            disabled="#{!capControlForm.editingAuthorized}"
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
                        rendered="#{capControlForm.editingAuthorized}"
						onclick="showOrphanedCBCPopup();">
						<h:outputText value="Select point..." />
					</h:outputLink>
				</x:div>

				<x:htmlTag value="br" />

				<x:commandLink id="cbcPoint_setNone"
                    rendered="#{capControlForm.editingAuthorized}"
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
										<f:param name="tabId" value="9"/>
									</x:commandLink>
								</x:panelGroup>
							</f:facet>
						</x:tree2>
					</x:div>
				</x:htmlTag>
			</x:panelGroup>

			<x:htmlTag value="br" />

			<h:outputText styleClass="tableHeader" value="Point Editor: " rendered="#{capControlForm.editingAuthorized}"/>

			<x:commandLink id="addPtLnk" value="Add Point" actionListener="#{capControlForm.pointTreeForm.addPointClick}" rendered="#{capControlForm.editingAuthorized}">
				<f:param name="parentId" value="#{capControlForm.pointTreeForm.pao.PAObjectID}" />
				<f:param name="tabId" value="9"/>
			</x:commandLink>

			<x:outputText value=" | " rendered="#{capControlForm.editingAuthorized}"/>

			<x:commandLink id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.pointTreeForm.deletePointClick}" rendered="#{capControlForm.editingAuthorized}">
                <f:param name="tabId" value="9"/>
            </x:commandLink>

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
				    disabled="#{!capControlForm.editingAuthorized}"
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
						
					<x:outputLabel for="cntrlSerNumEd" value="Serial Number: " title="Serial number of the controller device" styleClass="padlabel"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}" />
                        
					<x:outputText id="cntrlSerNumEd" value="#{capBankEditor.controller.deviceCBC.serialNumber}" 
                        rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}"/>
						
					<x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses"  styleClass="padlabel"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}" />
					
                    <x:outputText id="cntrlRoute" value="#{capBankEditor.controllerRouteName}"
						rendered="#{capBankEditor.capBank.capBank.controlPointID != 0 && capBankEditor.oneWayController}"/>
						
					<x:outputLabel for="cntrlSerNumEd2" value="Serial Number: " title="Serial number of the controller device"  styleClass="padlabel"
                        rendered="#{capBankEditor.twoWayController}"/>
                           
					<x:outputText id="cntrlSerNumEd2" value="#{capBankEditor.controller.deviceCBC.serialNumber}" 
                        rendered="#{capBankEditor.twoWayController}"/>
					
					<x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field"  styleClass="padlabel" 
                        rendered="#{capBankEditor.twoWayController}"/>
                        
					<x:outputText id="cntrlMast" value="#{capBankEditor.controller.deviceAddress.masterAddress}"
                        rendered="#{capBankEditor.twoWayController}"/> 
                           
					<x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field"  styleClass="padlabel" 
                        rendered="#{capBankEditor.twoWayController}"/>
                        
					<x:outputText id="cntrlMSlav" value="#{capBankEditor.controller.deviceAddress.slaveAddress}"
                        rendered="#{capBankEditor.twoWayController}"/>
	
					<x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses"  styleClass="padlabel"
                        rendered="#{capBankEditor.twoWayController}"/>
                           
					<x:outputText id="cntrlComChann" value="#{capBankEditor.commChannelName}"
                        rendered="#{capBankEditor.twoWayController}"/>
	
					<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="How long to wait after communications"  styleClass="padlabel"
                        rendered="#{capBankEditor.twoWayController}"/>
                           
					<x:outputText id="cntrlCommW" value="#{capBankEditor.controller.deviceAddress.postCommWait}" 
                        rendered="#{capBankEditor.twoWayController}"/>
					
				</x:panelGrid>
                
                <x:htmlTag value="br" />

				<x:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell" rendered="#{capBankEditor.twoWayController}">
					
                    <x:column>
						
						<x:selectBooleanCheckbox id="scanIntegrityChk" value="#{capBankEditor.editingIntegrity}" immediate="true" disabled="true" />
						<x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" styleClass="padCheckBoxLabel nameValueLabel"/>
						
						<x:panelGrid columns="2">
						
							<x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"  styleClass="padlabel"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:outputText id="integrityInterval" value="#{capBankEditor.integrityInterval}"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"/>
								
							<x:outputLabel for="integrityAltInterval" value="Alt. Interval: "  styleClass="padlabel"
								title="An alternate scan rate that can be less or more than the primary scan rate"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:outputText id="integrityAltInterval" value="#{capBankEditor.alternateIntegrityInterval}"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"/>
							
							<x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"  styleClass="padlabel"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}" />
							<x:outputText id="integrityGrp" value="#{capBankEditor.integrityScanGroup}"
                                 rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Integrity']}"/>
								
						</x:panelGrid>
					</x:column>

					<x:column>
						
						<x:selectBooleanCheckbox id="scanExceptionChk" value="#{capBankEditor.editingException}" immediate="true" disabled="true"/>
						<x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" styleClass="padCheckBoxLabel nameValueLabel"/>
						
						<x:panelGrid columns="2">
						
							<x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"  styleClass="padlabel"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:outputText id="exceptionInterval" value="#{capBankEditor.exceptionInterval}"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"/>
							
							<x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                                 styleClass="padlabel"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:outputText id="exceptionAltInterval" value="#{capBankEditor.alternateExceptionInterval}"
                                rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"/>
							
							<x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"  styleClass="padlabel"
								rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}" />
							<x:outputText id="exceptionGrp" value="#{capBankEditor.exceptionScanGroup}"
                                rendered="#{not empty capBankEditor.controller.deviceScanRateMap['Exception']}"/>
								
						</x:panelGrid>
						
					</x:column>

				</x:panelGrid>
			</x:htmlTag>
		</x:column>
	</x:panelGrid>
	<f:verbatim>
        <div id="orphanedCBCContent" class="popUpDiv simplePopup" style="display: none;">
            <!--  fix for IE6 bug (see YukonGeneralStyles.css ".simplePopup iframe" for more info) -->
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
</f:subview>