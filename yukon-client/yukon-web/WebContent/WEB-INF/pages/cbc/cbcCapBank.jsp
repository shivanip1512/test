<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x" %>
<%@ page import="com.cannontech.web.editor.point.PointForm" %>
<%@ page import="com.cannontech.web.editor.CapControlForm" %>
<%@ page import="com.cannontech.web.util.*" %>


<%

PointForm ptEditorForm =
    (PointForm)JSFParamUtil.getJSFVar( "ptEditorForm" );


%>

<f:subview id="cbcCapBank" rendered="#{capControlForm.visibleTabs['CBCCapBank']}" >

<f:verbatim>

<script type="text/javascript">

addSmartScrolling('capbankHiden', 'capbankDiv', null, null);

</script>

</f:verbatim>

    <h:panelGrid id="capbankBody" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >

        <h:column>
            <f:verbatim><br/><fieldset><legend>Configuration</legend></f:verbatim>

            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="bankOperation" value="Operation Method: " title="How this CapBank should operate in the field"/>
            <x:selectOneMenu id="bankOperation" onchange="submit();"
                    value="#{capControlForm.PAOBase.capBank.operationalState}" >
                <f:selectItems value="#{selLists.capBankOpStates}"/>
            </x:selectOneMenu>

            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="bankSize" value="Bank Size: " title="The total size of the CapBank"/>
            <x:inputText id="bankSize" styleClass="char8Label" required="true"
                    value="#{capControlForm.PAOBase.capBank.bankSize}" >
                <f:validateLongRange minimum="0" maximum="99999" />
            </x:inputText>
            <x:outputText id="bankSizeDesc" value="kVar"/>

            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="bankReclose" value="Reclose Delay: " title="The amount of time this CapBank should wait before executing the close command"/>
            <x:selectOneMenu id="bankReclose"
                    value="#{capControlForm.PAOBase.capBank.recloseDelay}" >
                <f:selectItem itemLabel="(none)" itemValue="0"/>
                <f:selectItems value="#{capControlForm.timeInterval}"/>
            </x:selectOneMenu>


            <f:verbatim><br/><br/></f:verbatim>
            <x:div id="capbankDiv" forceId="true" styleClass="scrollSmall" 
                rendered="#{capControlForm.bankControlPtVisible}" >

            <x:outputLabel for="cntrlPoint" value="Control Point: " title="Point used for monitoring the control (Only displays points that are not yet used by CapBanks)" styleClass="medStaticLabel"/>
            <x:outputText id="cntrlPoint" rendered="#{capControlForm.PAOBase.capBank.controlPointID != 0}"
                value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}
                / #{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].pointName}" styleClass="medLabel"/>
            <x:outputText id="cntrlPoint_none" rendered="#{capControlForm.PAOBase.capBank.controlPointID == 0}"
                value="(none)" styleClass="medLabel"/>


            <x:tree2 id="capBankPoints" value="#{capControlForm.capBankPoints}" var="node"
                    showRootNode="false" varNodeToggler="t"
                    preserveToggle="true" clientSideToggle="false" >
                <f:facet name="root">
                    <x:panelGroup>
                        <h:commandLink id="paoRoot" action="#{t.toggleExpanded}" value="#{node.description}"/>
                    </x:panelGroup>
                </f:facet>
    
                <f:facet name="paoTypes">
                    <x:panelGroup>
                        <x:outputText id="paoTypeNodeCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
                    </x:panelGroup>
                </f:facet>

                <f:facet name="paos">
                    <x:panelGroup>
                        <x:outputText id="paoNodeCnt" value="#{node.description} (#{node.childCount})" rendered="#{!empty node.children}"/>
                    </x:panelGroup>
                </f:facet>
    
                <f:facet name="points">
                    <x:panelGroup>
                        <x:graphicImage value="/editor/images/blue_check.gif" height="14" width="14" hspace="2"
                            rendered="#{capControlForm.PAOBase.capBank.controlPointID == node.identifier}" />
                        <x:commandLink id="pointNode" value="#{node.description}"
                            actionListener="#{capControlForm.capBankTeeClick}">
                            <f:param name="ptID" value="#{node.identifier}"/>
                        </x:commandLink>                
    
                    </x:panelGroup>
                </f:facet>
    
            </x:tree2>
            </x:div>
            <x:commandLink id="capBankPoint_setNone" title="Do not use a control point" styleClass="medStaticLabel"
                        rendered="#{capControlForm.bankControlPtVisible}"
                        value="No Control Point" actionListener="#{capControlForm.capBankTeeClick}">
                <f:param name="ptID" value="0"/>
            </x:commandLink>
            <f:verbatim></fieldset></f:verbatim>
        </h:column>
        <h:column>
        
        <f:verbatim><br/><fieldset><legend>CapBank Operations</legend></f:verbatim>
            <f:verbatim><br/></f:verbatim>
            <x:outputLabel for="maxDailyOps" value="Max Daily Operations: " title="The total number of controls allowed per day"/>
            <x:inputText id="maxDailyOps" styleClass="char16Label" required="true"
                    value="#{capControlForm.PAOBase.capBank.maxDailyOps}" >
                    <f:validateLongRange minimum="0" maximum="9999" />
            </x:inputText>
            <x:outputText id="maxDailyOpsDesc" value="(0 = unlimited)"/>
    
            <f:verbatim><br/></f:verbatim>
            <h:selectBooleanCheckbox id="disabledOps"
                    value="#{capControlForm.PAOBase.capBank.maxOperationDisabled}" />
            <x:outputLabel for="disabledOps" value="Disable upon reaching max operations" title="Should we be automatically disabled after reaching our max op counts"/>
            <f:verbatim><br><br></f:verbatim>
            <x:outputText styleClass="legendLikeLabel" value="CapBank Points"/>
            <f:verbatim><br><br></f:verbatim>
            <x:div forceId="true" id = "CapBankPointsDiv" styleClass="scrollVerySmall">
             <x:dataList forceId="true" id="CapBankPointsList" var="item" value="#{capControlForm.capBankPointList}" layout="unorderedList" styleClass="listWithNoBullets">                    
               <x:panelGroup>
                <f:verbatim>&nbsp;&nbsp;&nbsp;</f:verbatim>
                  <x:commandLink id="ptLink" value="#{item.pointName}" actionListener="#{capControlForm.capBankPointClick}">
                     <f:param name="ptID" value="#{item.liteID}" />
                  </x:commandLink>
               </x:panelGroup>
             </x:dataList>
          </x:div>
            <f:verbatim><br><br></f:verbatim>
            <f:verbatim></fieldset></f:verbatim>
                    <f:verbatim><br/><fieldset><legend>Contoller Configuration</legend></f:verbatim>
                        <f:verbatim><br/></f:verbatim> 
                         <x:outputText value="CBC Controller: " title="Click on the link to edit the CBC"/>
                            <x:commandLink id="CBCEditor" value="#{dbCache.allPAOsMap[dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID].paoName}"
                           actionListener="#{ptEditorForm.paoClick}" title="Click on the link to edit the CBC">
                                <f:param name="paoID" value="#{dbCache.allPointsMap[capControlForm.PAOBase.capBank.controlPointID].paobjectID}"/>
                            </x:commandLink> 

            <f:verbatim><br/></f:verbatim>
            <x:panelGroup id="oneWayCBC" rendered="#{!capControlForm.CBControllerEditor.twoWay}">
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlSerNum" value="Serial Number: " title="Serial number of the controller device" />
                <x:inputText id="cntrlSerNumEd" styleClass="staticLabel" disabled="true"
                        value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.serialNumber}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlRoute" value="Control Route: " title="Communication route the conroller uses" />
                <x:selectOneMenu id="cntrlRoute" value="#{capControlForm.CBControllerEditor.paoCBC.deviceCBC.routeID}" disabled="true">
                    <f:selectItem itemLabel="(none)" itemValue="0"/>
                    <f:selectItems value="#{selLists.routes}"/>
                </x:selectOneMenu>
            </x:panelGroup>


            <x:panelGroup id="twoWayCBC" rendered="#{capControlForm.CBControllerEditor.twoWay}" >
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlSerNum2" value="Serial Number: " title="Serial number of the controller device" />
                <x:inputText id="cntrlSerNumEd2" styleClass="staticLabel" disabled="true"
                        value="#{capControlForm.CBControllerEditor.paoCBC.serialNumber}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>
                
                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlMast" value="Master Address: " title="Integer address of the controller device in the field" />
                <x:inputText id="cntrlMast" styleClass="staticLabel" disabled="true"
                        value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.masterAddress}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlMSlav" value="Slave Address: " title="Integer address of the controller device in the field" />
                <x:inputText id="cntrlMSlav" styleClass="staticLabel" disabled="true"
                        value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.slaveAddress}" >
                    <f:validateLongRange minimum="0" maximum="9999999999" />
                </x:inputText>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlComChann" value="Comm. Channel: " title="Communication channel the conroller uses" />
                <x:selectOneMenu id="cntrlComChann" value="#{capControlForm.CBControllerEditor.paoCBC.deviceDirectCommSettings.portID}" disabled="true">
                    <f:selectItem itemLabel="(none)" itemValue="0"/>
                    <f:selectItems value="#{selLists.commChannels}"/>
                </x:selectOneMenu>

                <f:verbatim><br/></f:verbatim>
                <x:outputLabel for="cntrlCommW" value="Post Comm. Wait: " title="How long to wait after communications" />
                <x:inputText id="cntrlCommW" styleClass="staticLabel" disabled="true"
                        value="#{capControlForm.CBControllerEditor.paoCBC.deviceAddress.postCommWait}" >
                    <f:validateLongRange minimum="0" maximum="99999" />
                </x:inputText>


                <h:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn" >
                    <h:column>
                    <f:verbatim><br/></f:verbatim>
                    <h:selectBooleanCheckbox id="scanIntegrityChk" onclick="submit();"
                            valueChangeListener="#{capControlForm.showScanRate}"
                            value="#{capControlForm.CBControllerEditor.editingIntegrity}"
                            immediate="true" disabled="true"/>
                    <x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityInterval"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].intervalRate}" 
                            disabled="true">
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityAltInterval" disabled="true"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity'].alternateRate}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Integrity']}" />
                    <x:selectOneMenu id="integrityGrp" disabled="true"
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
                            immediate="true" disabled="true"/>
                    <x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" />
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionInterval" disabled="true"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].intervalRate}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionAltInterval" disabled="true"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}"
                            value="#{capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception'].alternateRate}" >
                        <f:selectItems value="#{capControlForm.timeInterval}"/>
                    </x:selectOneMenu>
                    <f:verbatim><br/></f:verbatim>
                    <x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
                            rendered="#{not empty capControlForm.CBControllerEditor.paoCBC.deviceScanRateMap['Exception']}" />
                    <x:selectOneMenu id="exceptionGrp" disabled="true"
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
        
    <x:inputHidden id="capbankHiden" forceId="true" value="#{capControlForm.offsetMap['capbankHiden']}"/>


</f:subview>