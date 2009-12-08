<%@ page import="com.cannontech.web.editor.CapControlForm"%>
<%@ page import="com.cannontech.web.util.JSFParamUtil"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="x"%>
<f:subview id="ltcView" rendered="#{capControlForm.visibleTabs['LTC']}">
    <h:panelGrid id="cbcBody" columns="2" styleClass="gridLayout" columnClasses="gridCell,gridCell">

        <h:column>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Communication"/></x:htmlTag>
                
                <x:panelGrid columns="2">
    
                    <x:outputLabel for="masterAddressInput" value="Master Address:" styleClass="nameValueLabel"/>
                    <x:inputText id="masterAddressInput" value="#{capControlForm.ltcBase.deviceAddress.masterAddress}" maxlength="18">
                        <f:validateLongRange minimum="0" maximum="4294967296" />
                    </x:inputText>
                    
                    <x:outputLabel for="slaveAddressInput" value="Slave Address:" styleClass="nameValueLabel"/>
                    <x:inputText id="slaveAddressInput" value="#{capControlForm.ltcBase.deviceAddress.slaveAddress}" maxlength="18">
                        <f:validateLongRange minimum="0" maximum="4294967296" />
                    </x:inputText>
                    
                    <x:outputLabel for="commChannelInput" value="Comm. Channel:" title="The communication channel this LTC will use." styleClass="nameValueLabel"/>
                    <x:selectOneMenu id="commChannelInput" value="#{capControlForm.ltcBase.deviceDirectCommSettings.portID}">
                        <f:selectItems value="#{selLists.commChannels}" />
                    </x:selectOneMenu>
                    
                    <x:outputLabel for="postCommWaitInput" value="Post Comm. Wait:" styleClass="nameValueLabel"/>
                    <x:panelGroup>
                        <x:inputText id="postCommWaitInput" value="#{capControlForm.ltcBase.deviceAddress.postCommWait}" >
                            <f:validateLongRange minimum="0" maximum="4294967296" />
                        </x:inputText>
                        <x:outputText value="(msec.)"/>
                    </x:panelGroup>
                    
                </x:panelGrid>
            </x:htmlTag>
            
            <x:htmlTag value="br"/>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="LTC Points"/></x:htmlTag>
            
                <x:div forceId="true" id="ltcPointsDiv" styleClass="scrollSmall">
                    <x:tree2 id="LtcPointTree" value="#{capControlForm.pointTreeForm.pointList}" var="node"
                        showRootNode="false" 
                        varNodeToggler="t" 
                        preserveToggle="true"
                        clientSideToggle="true" 
                        showLines="false">

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
                                    <f:param name="tabId" value="1"/>
                                </x:commandLink>
                            </x:panelGroup>
                        </f:facet>
                    </x:tree2>
                </x:div>
            </x:htmlTag>
        </h:column>
        
        <h:column>
            <x:htmlTag value="fieldset" styleClass="fieldSet">
                <x:htmlTag value="legend"><x:outputText value="Scan Rate"/></x:htmlTag>
                
                <h:panelGrid id="scanGrid" columns="2" styleClass="gridLayout" columnClasses="gridColumn,gridColumn">
    
                    <h:column>
                        <h:selectBooleanCheckbox id="scanIntegrityChk" onclick="submit();"
                            valueChangeListener="#{capControlForm.showScanRate}"
                            value="#{capControlForm.ltcBase.scanOne}"/>
                        
                        <x:outputLabel for="scanIntegrityChk" value="Class 0,1,2,3 Scan" title="Integrity scan type" styleClass="padCheckBoxLabel"/>
                        
                        <x:panelGrid columns="2">
                            
                            <x:outputLabel for="integrityInterval" value="Interval: " title="How often this scan should occur"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="integrityInterval" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Integrity'].intervalRate}">
                                <f:selectItems value="#{capControlForm.timeInterval}"/>
                            </x:selectOneMenu>
                            
                            <x:outputLabel for="integrityAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="integrityAltInterval" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Integrity'].alternateRate}">
                                <f:selectItems value="#{capControlForm.timeInterval}"/>
                            </x:selectOneMenu>
                            
                            <x:outputLabel for="integrityGrp" value="Scan Group: " title="A way to group scans into a common collection"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="integrityGrp" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Integrity']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Integrity'].scanGroup}">
                                <f:selectItem itemLabel="Default" itemValue="0" />
                                <f:selectItem itemLabel="First" itemValue="1" />
                                <f:selectItem itemLabel="Second" itemValue="2" />
                            </x:selectOneMenu>
                        
                        </x:panelGrid>
                    </h:column>
    
                    <h:column>
                        
                        <h:selectBooleanCheckbox id="scanExceptionChk" onclick="submit();" valueChangeListener="#{capControlForm.showScanRate}"
                            value="#{capControlForm.ltcBase.scanTwo}" immediate="true"/>
                        
                        <x:outputLabel for="scanExceptionChk" value="Class 1,2,3 Scan" title="Exception scan type" styleClass="padCheckBoxLabel"/>
                            
                        <x:panelGrid columns="2">
                        
                            <x:outputLabel for="exceptionInterval" value="Interval: " title="How often this scan should occur"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="exceptionInterval" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Exception'].intervalRate}">
                                <f:selectItems value="#{capControlForm.timeInterval}" />
                            </x:selectOneMenu>
    
                            <x:outputLabel for="exceptionAltInterval" value="Alt. Interval: " title="An alternate scan rate that can be less or more than the primary scan rate"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="exceptionAltInterval" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Exception'].alternateRate}">
                                <f:selectItems value="#{capControlForm.timeInterval}" />
                            </x:selectOneMenu>
    
                            <x:outputLabel for="exceptionGrp" value="Scan Group: " title="A way to group scans into a common collection"
                                rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}" styleClass="nameValueLabel"/>
                                
                            <x:selectOneMenu id="exceptionGrp" rendered="#{not empty capControlForm.ltcBase.deviceScanRateMap['Exception']}"
                                value="#{capControlForm.ltcBase.deviceScanRateMap['Exception'].scanGroup}">
                                <f:selectItem itemLabel="Default" itemValue="0" />
                                <f:selectItem itemLabel="First" itemValue="1" />
                                <f:selectItem itemLabel="Second" itemValue="2" />
                            </x:selectOneMenu>
                            
                        </x:panelGrid>
                    </h:column>
                </h:panelGrid>
                    
            </x:htmlTag>
        </h:column>
    </h:panelGrid>
</f:subview>