<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<%@ page import="com.cannontech.web.util.JSFParamUtil" %>
<jsp:directive.page import="com.cannontech.database.data.pao.DBEditorTypes"/>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%
    CapControlForm capControlForm =
    (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
  String itemid = JSFParamUtil.getJSFReqParam("itemid");
  String type = JSFParamUtil.getJSFReqParam("type");
  
  
  if (itemid != null && type!= null) {
    capControlForm.initItem(Integer.parseInt(itemid), Integer.parseInt(type));
    capControlForm.setCBControllerEditor(null);
    capControlForm.getCBControllerEditor(); 
    }
%>

<f:subview id="cbcController" rendered="#{capControlForm.visibleTabs['CBCController']}" >


    <h:panelGrid id="cbcBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
    

        <h:column rendered="#{capControlForm.CBControllerEditor.oneWay}">
           <f:verbatim><br/><fieldset class="fieldSet"><legend>Configuration</legend></f:verbatim>
            <f:verbatim><br/></f:verbatim>
			<x:panelGroup id="oneWayCBC" >
				<f:verbatim><br/></f:verbatim>
                                
				<x:outputLabel for="cntrlSerNumEd" value="Serial Number: " title="Serial number of the controller device" />
				<x:inputText id="cntrlSerNumEd" styleClass="staticLabel"
						value="#{capControlForm.CBControllerEditor.serialNumber}" maxlength="9" size="9">
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" />
				<x:selectOneMenu id="cntrlRoute" value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.routeID}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.routes}"/>
				</x:selectOneMenu>
			</x:panelGroup>
            <f:verbatim>
            </fieldset>
            </f:verbatim>
            </h:column>

            <h:column rendered="#{capControlForm.CBControllerEditor.twoWay}">
            <f:verbatim><br/><fieldset class="fieldSet"><legend>Configuration</legend></f:verbatim>
            <f:verbatim><br/></f:verbatim>
			
            <x:panelGroup id="editCBCCheck" >
                
                <h:selectBooleanCheckbox id="editCntrl" value="#{capControlForm.CBControllerEditor.editingController}" 
                         onclick="submit();"/>
                <x:outputLabel for="editCntrl" value="Edit Controller" title="Allows editing of the controller this CapBank currently uses" />
            </x:panelGroup>
            <f:verbatim><br/><br/></f:verbatim>
            <x:panelGroup id="twoWayCBC" >
				
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlSerNumEd2" value="Serial Number: " title="Serial number of the controller device" />
                <x:inputText id="cntrlSerNumEd2" styleClass="staticLabel" disabled="#{!capControlForm.CBControllerEditor.editingController}"
                        value="#{capControlForm.CBControllerEditor.serialNumber}" maxlength="9" size="9" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>
                
                <f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMast" styleClass="staticLabel" disabled="#{!capControlForm.CBControllerEditor.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.masterAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field" />
				<x:inputText id="cntrlMSlav" styleClass="staticLabel" disabled="#{!capControlForm.CBControllerEditor.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.slaveAddress}" >
					<f:validateLongRange minimum="0" maximum="9999999999" />
				</x:inputText>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses" />
				<x:selectOneMenu disabled="#{!capControlForm.CBControllerEditor.editingController}" id="cntrlComChann" value="#{capControlForm.CBControllerEditor.paoCBC.deviceDirectCommSettings.portID}" >
					<f:selectItem itemLabel="(none)" itemValue="0"/>
					<f:selectItems value="#{selLists.commChannels}"/>
				</x:selectOneMenu>

				<f:verbatim><br/></f:verbatim>
				<x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="How long to wait after communications" />
				<x:inputText id="cntrlCommW" styleClass="staticLabel" disabled="#{!capControlForm.CBControllerEditor.editingController}"
						value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.postCommWait}" >
					<f:validateLongRange minimum="0" maximum="99999" />
				</x:inputText>
            </x:panelGroup>
               <h:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
                
                    <h:column>
                    <f:verbatim><br/></f:verbatim>
                    <h:selectBooleanCheckbox id="scanIntegrityChk" onclick="submit();"
                            valueChangeListener="#{capControlForm.showScanRate}"
                            value="#{capControlForm.CBControllerEditor.editingIntegrity}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityInterval"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].intervalRate}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityAltInterval"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].alternateRate}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityGrp"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].scanGroup}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
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
                            immediate="true" disabled="#{!capControlForm.CBControllerEditor.editingController}"/>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionInterval"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].intervalRate}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionAltInterval"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].alternateRate}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionGrp"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].scanGroup}"
                            disabled="#{!capControlForm.CBControllerEditor.editingController}" >
                        <f:selectItem itemLabel="Default" itemValue="0" />
                        <f:selectItem itemLabel="First" itemValue="1" />
                        <f:selectItem itemLabel="Second" itemValue="2" />
                    </x:selectOneMenu>
                    </h:column>
                </h:panelGrid>                            
               <f:verbatim>
            </fieldset>
            </f:verbatim>
             </h:column>
            
            <h:column>
            <x:panelGroup>                      
                <f:verbatim>
                    <br />
                    <fieldset class="fieldSet">
                        <legend>
                            Analog and Status Points
                        </legend>
                </f:verbatim>
                <x:div forceId="true" id="CBCCtlEditorScrollDiv" styleClass="scrollSmall">

                    <x:tree2 binding="#{capControlForm.CBControllerEditor.pointTree}" id="CtlEditPointTree" value="#{capControlForm.CBControllerEditor.pointList}" var="node" showRootNode="false" varNodeToggler="t" preserveToggle="true" clientSideToggle="false" showLines="false">

                        <f:facet name="root" >
                            <x:panelGroup>
                                <x:outputText id="rootLink" value="#{node.description}" />
                            </x:panelGroup>
                        </f:facet>
                        <f:facet name="pointtype">
                            <x:panelGroup>
                                <x:outputText id="paChCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
                            </x:panelGroup>
                        </f:facet>
                                          								
						<f:facet name="sublevels">
							<x:panelGroup>
								<x:outputText id="subLvlCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}" />
							</x:panelGroup>
						</f:facet>
                  
                        <f:facet name="points">
                            <x:panelGroup>
                                <x:commandLink  id="ptLink" value="#{node.description}" actionListener="#{capControlForm.CBControllerEditor.pointClick}">
                                    <f:param name="ptID" value="#{node.identifier}" />
                                </x:commandLink>
                            </x:panelGroup>
                        </f:facet>
                    </x:tree2>
                </x:div>
                <f:verbatim>
                    </fieldset>
                </f:verbatim>
            </x:panelGroup>
               <f:verbatim>
                    <br />
              </f:verbatim>     
            <h:outputText styleClass="tableHeader" value="Point Editor" />
                      <x:commandLink  id="addPtLnk" value="Add Point" actionListener="#{capControlForm.CBControllerEditor.addPointClick}">
                           <f:param name="parentId" value="#{capControlForm.CBControllerEditor.paoCBC.PAObjectID}" />
                      </x:commandLink>
 						<f:verbatim>
 							&nbsp; <bold>|</bold>&nbsp;
 						</f:verbatim>                     
                      <x:commandLink  id="deletePtLnk" value="Delete Point" actionListener="#{capControlForm.CBControllerEditor.deletePointClick}">
                      </x:commandLink>
 
                      </h:column>
                


				

	




	
	</h:panelGrid>
		
    


</f:subview>